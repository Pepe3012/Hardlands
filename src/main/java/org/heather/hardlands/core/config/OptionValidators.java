package org.heather.hardlands.core.config;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class OptionValidators {

    private OptionValidators() {}

    public static <T> Predicate<T> oneOf(Collection<? extends T> values) {
        if (values == null) {
            throw new IllegalArgumentException("Values cannot be null");
        }

        var accepted = Set.copyOf(values);
        return accepted::contains;
    }

    public static final class Collections {

        public static final Predicate<Collection<?>> NON_EMPTY = value -> !value.isEmpty();

        private Collections() {}

        public static Predicate<Collection<?>> minSize(int minimum) {
            requireNonNegative(minimum, "Minimum size");
            return value -> value.size() >= minimum;
        }

        public static Predicate<Collection<?>> maxSize(int maximum) {
            requireNonNegative(maximum, "Maximum size");
            return value -> value.size() <= maximum;
        }

        public static Predicate<Collection<?>> sizeBetween(int minimum, int maximum) {
            requireRange(minimum, maximum);
            return value -> value.size() >= minimum && value.size() <= maximum;
        }
    }

    public static final class Doubles {

        public static final Predicate<Double> NEGATIVE =
                value -> Double.isFinite(value) && value < 0.0;

        public static final Predicate<Double> NON_NEGATIVE =
                value -> Double.isFinite(value) && value >= 0.0;

        public static final Predicate<Double> NON_POSITIVE =
                value -> Double.isFinite(value) && value <= 0.0;

        public static final Predicate<Double> POSITIVE =
                value -> Double.isFinite(value) && value > 0.0;

        public static final Predicate<Double> UNIT_INTERVAL = between(0.0, 1.0);

        private Doubles() {}

        public static Predicate<Double> atLeast(double minimum) {
            requireFinite(minimum);
            return value -> Double.isFinite(value) && value >= minimum;
        }

        public static Predicate<Double> atMost(double maximum) {
            requireFinite(maximum);
            return value -> Double.isFinite(value) && value <= maximum;
        }

        public static Predicate<Double> between(double minimum, double maximum) {
            requireFinite(minimum);
            requireFinite(maximum);
            requireRange(minimum, maximum);

            return value -> Double.isFinite(value) && value >= minimum && value <= maximum;
        }
    }

    public static final class Floats {

        public static final Predicate<Float> NEGATIVE =
                value -> Float.isFinite(value) && value < 0.0F;

        public static final Predicate<Float> NON_NEGATIVE =
                value -> Float.isFinite(value) && value >= 0.0F;

        public static final Predicate<Float> NON_POSITIVE =
                value -> Float.isFinite(value) && value <= 0.0F;

        public static final Predicate<Float> POSITIVE =
                value -> Float.isFinite(value) && value > 0.0F;

        public static final Predicate<Float> UNIT_INTERVAL = between(0.0F, 1.0F);

        private Floats() {}

        public static Predicate<Float> atLeast(float minimum) {
            requireFinite(minimum);
            return value -> Float.isFinite(value) && value >= minimum;
        }

        public static Predicate<Float> atMost(float maximum) {
            requireFinite(maximum);
            return value -> Float.isFinite(value) && value <= maximum;
        }

        public static Predicate<Float> between(float minimum, float maximum) {
            requireFinite(minimum);
            requireFinite(maximum);
            requireRange(minimum, maximum);

            return value -> Float.isFinite(value) && value >= minimum && value <= maximum;
        }
    }

    public static final class Integers {

        public static final Predicate<Integer> EVEN = value -> value % 2 == 0;
        public static final Predicate<Integer> NEGATIVE = value -> value < 0;
        public static final Predicate<Integer> NON_NEGATIVE = value -> value >= 0;
        public static final Predicate<Integer> NON_POSITIVE = value -> value <= 0;
        public static final Predicate<Integer> ODD = value -> value % 2 != 0;
        public static final Predicate<Integer> POSITIVE = value -> value > 0;

        private Integers() {}

        public static Predicate<Integer> atLeast(int minimum) {
            return value -> value >= minimum;
        }

        public static Predicate<Integer> atMost(int maximum) {
            return value -> value <= maximum;
        }

        public static Predicate<Integer> between(int minimum, int maximum) {
            requireRange(minimum, maximum);
            return value -> value >= minimum && value <= maximum;
        }
    }

    public static final class Longs {

        public static final Predicate<Long> NEGATIVE = value -> value < 0L;
        public static final Predicate<Long> NON_NEGATIVE = value -> value >= 0L;
        public static final Predicate<Long> NON_POSITIVE = value -> value <= 0L;
        public static final Predicate<Long> POSITIVE = value -> value > 0L;

        private Longs() {}

        public static Predicate<Long> atLeast(long minimum) {
            return value -> value >= minimum;
        }

        public static Predicate<Long> atMost(long maximum) {
            return value -> value <= maximum;
        }

        public static Predicate<Long> between(long minimum, long maximum) {
            requireRange(minimum, maximum);
            return value -> value >= minimum && value <= maximum;
        }
    }

    public static final class Maps {

        public static final Predicate<Map<?, ?>> NON_EMPTY = value -> !value.isEmpty();

        private Maps() {}

        public static Predicate<Map<?, ?>> minSize(int minimum) {
            requireNonNegative(minimum, "Minimum size");
            return value -> value.size() >= minimum;
        }

        public static Predicate<Map<?, ?>> maxSize(int maximum) {
            requireNonNegative(maximum, "Maximum size");
            return value -> value.size() <= maximum;
        }

        public static Predicate<Map<?, ?>> sizeBetween(int minimum, int maximum) {
            requireRange(minimum, maximum);
            return value -> value.size() >= minimum && value.size() <= maximum;
        }
    }

    public static final class Strings {

        public static final Predicate<String> NON_BLANK = value -> !value.isBlank();
        public static final Predicate<String> NON_EMPTY = value -> !value.isEmpty();

        private Strings() {}

        public static Predicate<String> minLength(int minimum) {
            requireNonNegative(minimum, "Minimum length");
            return value -> value.length() >= minimum;
        }

        public static Predicate<String> maxLength(int maximum) {
            requireNonNegative(maximum, "Maximum length");
            return value -> value.length() <= maximum;
        }

        public static Predicate<String> lengthBetween(int minimum, int maximum) {
            requireRange(minimum, maximum);
            return value -> value.length() >= minimum && value.length() <= maximum;
        }

        public static Predicate<String> matches(Pattern pattern) {
            if (pattern == null) {
                throw new IllegalArgumentException("Pattern cannot be null");
            }

            return pattern.asMatchPredicate();
        }
    }

    private static void requireFinite(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Value must be finite");
        }
    }

    private static void requireRange(double minimum, double maximum) {
        if (minimum > maximum) {
            throw new IllegalArgumentException("Minimum cannot be greater than maximum");
        }
    }

    private static void requireRange(long minimum, long maximum) {
        if (minimum > maximum) {
            throw new IllegalArgumentException("Minimum cannot be greater than maximum");
        }
    }

    private static void requireNonNegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " cannot be negative");
        }
    }
}