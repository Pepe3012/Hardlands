package org.heather.hardlands.core.option;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@RequiredArgsConstructor
public enum OptionDataType {

    BOOLEAN(Boolean.class),
    INTEGER(Integer.class),
    FLOAT(Float.class),
    DOUBLE(Double.class),
    LONG(Long.class),
    STRING(String.class),

    LIST(List.class),
    SET(Set.class),
    MAP(Map.class),

    CUSTOM(Object.class);

    private static final Map<Class<?>, OptionDataType> BY_JAVA_TYPE = Arrays.stream(values()).filter(dataType -> dataType != CUSTOM).collect(Collectors.toUnmodifiableMap(OptionDataType::getJavaType, Function.identity()));

    private final Class<?> javaType;

    public boolean acceptsValue(Object value) {
        return this.javaType.isInstance(value);
    }

    public static OptionDataType fromJavaType(Class<?> javaType) {
        Objects.requireNonNull(javaType, "Java type cannot be null");

        OptionDataType dataType = BY_JAVA_TYPE.get(javaType);
        if (dataType != null) return dataType;

        if (List.class.isAssignableFrom(javaType)) return LIST;
        if (Set.class.isAssignableFrom(javaType)) return SET;
        if (Map.class.isAssignableFrom(javaType)) return MAP;

        return CUSTOM;
    }
}