package com.hardlands.common.option;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class OptionValidators {

    private OptionValidators() {}

    public static <T> Predicate<T> equalTo(T expected) {
        return Predicate.isEqual(expected);
    }

    public static <T> Predicate<T> oneOf(Collection<? extends T> values) {
        Set<? extends T> acceptedValues = Set.copyOf(values);
        return acceptedValues::contains;
    }

    public static <T> Predicate<T> not(Predicate<? super T> validator) {
        return value -> !validator.test(value);
    }

    public static final class Integers {

        public static final IntPredicate POSITIVE = value -> value > 0;
        public static final IntPredicate NON_NEGATIVE = value -> value >= 0;
        public static final IntPredicate NEGATIVE = value -> value < 0;
        public static final IntPredicate NON_POSITIVE = value -> value <= 0;
        public static final IntPredicate EVEN = value -> value % 2 == 0;
        public static final IntPredicate ODD = value -> value % 2 != 0;

        private Integers() {}

        public static IntPredicate atLeast(int minimum) {
            return value -> value >= minimum;
        }

        public static IntPredicate atMost(int maximum) {
            return value -> value <= maximum;
        }

        public static IntPredicate betweenInclusive(int minimum, int maximum) {
            if (minimum > maximum) throw new IllegalArgumentException("Minimum cannot be greater than maximum");

            return value -> value >= minimum && value <= maximum;
        }
    }

    public static final class Floats {

        public static final Predicate<Float> POSITIVE = value -> Float.isFinite(value) && value > 0.0F;
        public static final Predicate<Float> NON_NEGATIVE = value -> Float.isFinite(value) && value >= 0.0F;
        public static final Predicate<Float> PERCENTAGE = value -> Float.isFinite(value) && value >= 0.0F && value <= 1.0F;

        private Floats() {}

        public static Predicate<Float> atLeast(float minimum) {
            return value -> Float.isFinite(value) && value >= minimum;
        }

        public static Predicate<Float> atMost(float maximum) {
            return value -> Float.isFinite(value) && value <= maximum;
        }

        public static Predicate<Float> betweenInclusive(float minimum, float maximum) {
            if (!Float.isFinite(minimum) || !Float.isFinite(maximum)) throw new IllegalArgumentException("Bounds must be finite");
            if (minimum > maximum) throw new IllegalArgumentException("Minimum cannot be greater than maximum");

            return value -> Float.isFinite(value) && value >= minimum && value <= maximum;
        }
    }

    public static final class Strings {

        public static final Predicate<String> NON_EMPTY = value -> !value.isEmpty();
        public static final Predicate<String> NON_BLANK = value -> !value.isBlank();

        private Strings() {}

        public static Predicate<String> minimumLength(int minimum) {
            if (minimum < 0) throw new IllegalArgumentException("Minimum length cannot be negative");

            return value -> value.length() >= minimum;
        }

        public static Predicate<String> maximumLength(int maximum) {
            if (maximum < 0) throw new IllegalArgumentException("Maximum length cannot be negative");

            return value -> value.length() <= maximum;
        }

        public static Predicate<String> lengthBetweenInclusive(int minimum, int maximum) {
            if (minimum < 0) throw new IllegalArgumentException("Minimum length cannot be negative");
            if (minimum > maximum) throw new IllegalArgumentException("Minimum length cannot be greater than maximum");

            return value -> value.length() >= minimum && value.length() <= maximum;
        }

        public static Predicate<String> matches(Pattern pattern) {
            return pattern.asMatchPredicate();
        }
    }

    public static final class Collections {

        public static final Predicate<Collection<?>> NON_EMPTY = value -> !value.isEmpty();

        private Collections() {}

        public static Predicate<Collection<?>> minimumSize(int minimum) {
            if (minimum < 0) throw new IllegalArgumentException("Minimum size cannot be negative");

            return value -> value.size() >= minimum;
        }

        public static Predicate<Collection<?>> maximumSize(int maximum) {
            if (maximum < 0) throw new IllegalArgumentException("Maximum size cannot be negative");

            return value -> value.size() <= maximum;
        }

        public static Predicate<Collection<?>> sizeBetweenInclusive(int minimum, int maximum) {
            if (minimum < 0) throw new IllegalArgumentException("Minimum size cannot be negative");
            if (minimum > maximum) throw new IllegalArgumentException("Minimum size cannot be greater than maximum");

            return value -> value.size() >= minimum && value.size() <= maximum;
        }
    }

    public static final class Maps {

        public static final Predicate<Map<?, ?>> NON_EMPTY = value -> !value.isEmpty();

        private Maps() {}

        public static Predicate<Map<?, ?>> minimumSize(int minimum) {
            if (minimum < 0) throw new IllegalArgumentException("Minimum size cannot be negative");

            return value -> value.size() >= minimum;
        }

        public static Predicate<Map<?, ?>> maximumSize(int maximum) {
            if (maximum < 0) throw new IllegalArgumentException("Maximum size cannot be negative");

            return value -> value.size() <= maximum;
        }

        public static Predicate<Map<?, ?>> sizeBetweenInclusive(int minimum, int maximum) {
            if (minimum < 0) throw new IllegalArgumentException("Minimum size cannot be negative");
            if (minimum > maximum) throw new IllegalArgumentException("Minimum size cannot be greater than maximum");

            return value -> value.size() >= minimum && value.size() <= maximum;
        }
    }
}