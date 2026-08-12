package org.heather.hardlands.core.option;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
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
        Set<? extends T> accepted = Set.copyOf(Objects.requireNonNull(values, "Values cannot be null"));
        return accepted::contains;
    }

    public static <T> Predicate<T> not(Predicate<? super T> validator) {
        Objects.requireNonNull(validator, "Validator cannot be null");
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

        public static IntPredicate between(int minimum, int maximum) {
            validateRange(minimum, maximum);
            return value -> value >= minimum && value <= maximum;
        }
    }

    public static final class Floats {

        public static final Predicate<Float> POSITIVE = value -> Float.isFinite(value) && value > 0.0F;
        public static final Predicate<Float> NON_NEGATIVE = value -> Float.isFinite(value) && value >= 0.0F;
        public static final Predicate<Float> PERCENTAGE = between(0.0F, 1.0F);

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
            validateRange(minimum, maximum);

            return value -> Float.isFinite(value) && value >= minimum && value <= maximum;
        }
    }

    public static final class Doubles {

        public static final Predicate<Double> POSITIVE = value -> Double.isFinite(value) && value > 0.0D;
        public static final Predicate<Double> NON_NEGATIVE = value -> Double.isFinite(value) && value >= 0.0D;
        public static final Predicate<Double> PERCENTAGE = between(0.0D, 1.0D);

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
            validateRange(minimum, maximum);

            return value -> Double.isFinite(value) && value >= minimum && value <= maximum;
        }
    }

    public static final class Longs {

        public static final Predicate<Long> POSITIVE = value -> value > 0L;
        public static final Predicate<Long> NON_NEGATIVE = value -> value >= 0L;
        public static final Predicate<Long> NEGATIVE = value -> value < 0L;
        public static final Predicate<Long> NON_POSITIVE = value -> value <= 0L;

        private Longs() {}

        public static Predicate<Long> atLeast(long minimum) {
            return value -> value >= minimum;
        }

        public static Predicate<Long> atMost(long maximum) {
            return value -> value <= maximum;
        }

        public static Predicate<Long> between(long minimum, long maximum) {
            validateRange(minimum, maximum);
            return value -> value >= minimum && value <= maximum;
        }
    }

    public static final class Strings {

        public static final Predicate<String> NON_EMPTY = value -> !value.isEmpty();
        public static final Predicate<String> NON_BLANK = value -> !value.isBlank();

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
            requireNonNegative(minimum, "Minimum length");
            validateRange(minimum, maximum);

            return value -> value.length() >= minimum && value.length() <= maximum;
        }

        public static Predicate<String> matches(Pattern pattern) {
            return Objects.requireNonNull(pattern, "Pattern cannot be null").asMatchPredicate();
        }
    }

    public static final class Collections {

        public static final Predicate<Collection<?>> NON_EMPTY = value -> !value.isEmpty();

        private Collections() {}

        public static Predicate<Collection<?>> minSize(int minimum) {
            requireNonNegative(minimum, "Minimum inventorySize");
            return value -> value.size() >= minimum;
        }

        public static Predicate<Collection<?>> maxSize(int maximum) {
            requireNonNegative(maximum, "Maximum inventorySize");
            return value -> value.size() <= maximum;
        }

        public static Predicate<Collection<?>> sizeBetween(int minimum, int maximum) {
            requireNonNegative(minimum, "Minimum inventorySize");
            validateRange(minimum, maximum);

            return value -> value.size() >= minimum && value.size() <= maximum;
        }
    }

    public static final class Maps {

        public static final Predicate<Map<?, ?>> NON_EMPTY = value -> !value.isEmpty();

        private Maps() {}

        public static Predicate<Map<?, ?>> minSize(int minimum) {
            requireNonNegative(minimum, "Minimum inventorySize");
            return value -> value.size() >= minimum;
        }

        public static Predicate<Map<?, ?>> maxSize(int maximum) {
            requireNonNegative(maximum, "Maximum inventorySize");
            return value -> value.size() <= maximum;
        }

        public static Predicate<Map<?, ?>> sizeBetween(int minimum, int maximum) {
            requireNonNegative(minimum, "Minimum inventorySize");
            validateRange(minimum, maximum);

            return value -> value.size() >= minimum && value.size() <= maximum;
        }
    }

    private static void requireFinite(double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalArgumentException("Value must be finite");
        }
    }

    private static void requireNonNegative(int value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " cannot be negative");
        }
    }

    private static <T extends Comparable<T>> void validateRange(T minimum, T maximum) {
        if (minimum.compareTo(maximum) > 0) {
            throw new IllegalArgumentException("Minimum cannot be greater than maximum");
        }
    }
}