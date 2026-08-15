package io.github.pepe3012.hardlands.data.option;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum OptionDataType {

    BOOLEAN(Boolean.class),
    CUSTOM(Object.class),
    DOUBLE(Double.class),
    FLOAT(Float.class),
    INTEGER(Integer.class),
    LIST(List.class),
    LONG(Long.class),
    MAP(Map.class),
    SET(Set.class),
    STRING(String.class);

    private final Class<?> javaType;

    OptionDataType(Class<?> javaType) {
        this.javaType = javaType;
    }

    private static final Map<Class<?>, OptionDataType> BY_JAVA_TYPE = Stream.of(values()).filter(dataType -> dataType != CUSTOM).collect(Collectors.toUnmodifiableMap(OptionDataType::getJavaType, dataType -> dataType));

    public Class<?> getJavaType() {
        return this.javaType;
    }

    public boolean acceptsValue(Object value) {
        return this.javaType.isInstance(value);
    }

    public static OptionDataType fromJavaType(Class<?> javaType) {
        if (javaType == null) {
            throw new IllegalArgumentException("Java type cannot be null");
        }

        OptionDataType dataType = BY_JAVA_TYPE.get(javaType);

        if (dataType != null) {
            return dataType;
        }

        if (List.class.isAssignableFrom(javaType)) {
            return LIST;
        }

        if (Map.class.isAssignableFrom(javaType)) {
            return MAP;
        }

        if (Set.class.isAssignableFrom(javaType)) {
            return SET;
        }

        return CUSTOM;
    }
}