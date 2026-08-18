package org.heather.hardlands.config;

import com.google.auto.service.AutoService;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.Set;

@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_25)
@SupportedAnnotationTypes("org.heather.hardlands.config.ConfigBuilder")
public final class ConfigurationProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        ConfigurationGenerator generator = new ConfigurationGenerator(this.processingEnv);

        for (Element element : roundEnvironment.getElementsAnnotatedWith(ConfigBuilder.class)) {
            if (element.getKind() != ElementKind.CLASS) {
                this.reportError("@ConfigBuilder can only be applied to classes.", element);
                continue;
            }

            try {
                generator.generate((TypeElement) element);
            } catch (IOException | IllegalArgumentException | IllegalStateException exception) {
                this.reportError("Failed to generate configuration: " + exception.getMessage(), element);
            }
        }

        return true;
    }

    private void reportError(String message, Element element) {
        this.processingEnv.getMessager().printMessage(Diagnostic.Kind.ERROR, message, element);
    }
}