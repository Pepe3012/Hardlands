package org.heather.hardlands.processor;

import org.heather.hardlands.annotation.ConfigOption;
import org.heather.hardlands.annotation.ConfigurationSpec;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@SupportedSourceVersion(SourceVersion.RELEASE_25)
@SupportedAnnotationTypes("org.heather.hardlands.annotation.ConfigurationSpec")
public final class ConfigurationProcessor extends AbstractProcessor {

    private static final String GENERATED_CLASS_SUFFIX = "Configuration";

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        Set<? extends Element> specifications =
                roundEnvironment.getElementsAnnotatedWith(ConfigurationSpec.class);

        for (Element element : specifications) {
            if (element.getKind() != ElementKind.CLASS) {
                this.reportError("@ConfigurationSpec can only be applied to classes", element);
                continue;
            }

            try {
                this.generateConfiguration((TypeElement) element);
            } catch (IOException exception) {
                this.reportError(exception.getMessage(), element);
            }
        }

        return true;
    }

    private void generateConfiguration(TypeElement specification) throws IOException {
        ConfigurationSpec annotation = specification.getAnnotation(ConfigurationSpec.class);

        String packageName = this.processingEnv
                .getElementUtils()
                .getPackageOf(specification)
                .getQualifiedName()
                .toString();

        String className = this.generatedClassName(specification);
        String qualifiedName = packageName + "." + className;

        JavaFileObject sourceFile = this.processingEnv
                .getFiler()
                .createSourceFile(qualifiedName, specification);

        try (Writer writer = sourceFile.openWriter()) {
            this.writeConfiguration(
                    writer,
                    specification,
                    annotation,
                    packageName,
                    className
            );
        }
    }

    private void writeConfiguration(
            Writer writer,
            TypeElement specification,
            ConfigurationSpec annotation,
            String packageName,
            String className
    ) throws IOException {
        this.writeHeader(writer, packageName, className);
        this.writeOptions(writer, specification, annotation.options());
        this.writeConstructor(writer, className, annotation.identifier());
        this.writeFooter(writer);
    }

    private void writeHeader(
            Writer writer,
            String packageName,
            String className
    ) throws IOException {
        writer.write("""
                package %s;

                import org.heather.hardlands.core.config.Configuration;
                import org.heather.hardlands.core.config.Option;

                public abstract class %s extends Configuration {

                """.formatted(packageName, className));
    }

    private void writeOptions(
            Writer writer,
            TypeElement specification,
            ConfigOption[] options
    ) throws IOException {
        Set<String> fieldNames = new HashSet<>();
        Set<String> keys = new HashSet<>();

        for (ConfigOption option : options) {
            String fieldName = option.name();
            String key = option.key().isBlank()
                    ? toKebabCase(fieldName)
                    : option.key();

            if (!this.isValidFieldName(fieldName)) {
                this.reportError("Invalid option field name: " + fieldName, specification);
                continue;
            }

            if (!fieldNames.add(fieldName)) {
                this.reportError("Duplicate option field name: " + fieldName, specification);
                continue;
            }

            if (!keys.add(key)) {
                this.reportError("Duplicate option key: " + key, specification);
                continue;
            }

            this.writeOption(writer, option, fieldName, key);
        }
    }

    private void writeOption(
            Writer writer,
            ConfigOption option,
            String fieldName,
            String key
    ) throws IOException {
        TypeMirror type = this.resolveType(option);
        String typeName = this.referenceTypeName(type);

        writer.write("""
                    public final Option<%s> %s =
                            super.registerOption("%s", %s.class);

                """.formatted(typeName, fieldName, key, typeName));
    }

    private void writeConstructor(
            Writer writer,
            String className,
            String identifier
    ) throws IOException {
        writer.write("""
                    protected %s() {
                        super("%s");
                    }

                """.formatted(className, identifier));
    }

    private void writeFooter(Writer writer) throws IOException {
        writer.write("}\n");
    }

    private TypeMirror resolveType(ConfigOption option) {
        try {
            Class<?> type = option.type();
            TypeElement element = this.processingEnv
                    .getElementUtils()
                    .getTypeElement(type.getCanonicalName());

            if (element == null) {
                throw new IllegalStateException("Unable to resolve option type: " + type.getName());
            }

            return element.asType();
        } catch (MirroredTypeException exception) {
            return exception.getTypeMirror();
        }
    }

    private String referenceTypeName(TypeMirror type) {
        String typeName;

        if (type.getKind().isPrimitive()) {
            TypeElement boxedType = this.processingEnv
                    .getTypeUtils()
                    .boxedClass((PrimitiveType) type);

            typeName = boxedType.getQualifiedName().toString();
        } else {
            typeName = type.toString();
        }

        return typeName.startsWith("java.lang.")
                ? typeName.substring("java.lang.".length())
                : typeName;
    }

    private boolean isValidFieldName(String name) {
        return SourceVersion.isIdentifier(name)
                && !SourceVersion.isKeyword(name);
    }

    private String generatedClassName(TypeElement specification) {
        return specification.getSimpleName() + GENERATED_CLASS_SUFFIX;
    }

    private void reportError(String message, Element element) {
        this.processingEnv
                .getMessager()
                .printMessage(Diagnostic.Kind.ERROR, message, element);
    }

    private static String toKebabCase(String value) {
        return value
                .replaceAll("([a-z0-9])([A-Z])", "$1-$2")
                .toLowerCase(Locale.ROOT);
    }
}