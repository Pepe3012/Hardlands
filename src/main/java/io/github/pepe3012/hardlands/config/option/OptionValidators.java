package io.github.pepe3012.hardlands.config.option;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.DoublePredicate;
import java.util.function.IntPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public final class OptionValidators {

    private OptionValidators() {}

    public static <T> Predicate<T> equalTo(T expected) {
        return Predicate.isEqual(expected);
    }

    public static <T> Predicate<T> oneOf(Collection<? extends T> values) {
        if (values == null) {
            throw new IllegalArgumentException("Values cannot be null");
        }

        Set<? extends T> accepted = Set.copyOf(values);
        return accepted::contains;
    }

    public static <T> Predicate<T> not(Predicate<? super T> validator) {
        if (validator == null) {
            throw new IllegalArgumentException("Validator cannot be null");
        }

        return value -> !validator.test(value);
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

        public static final DoublePredicate NON_NEGATIVE = value -> Double.isFinite(value) && value >= 0.0D;
        public static final DoublePredicate PERCENTAGE = between(0.0D, 1.0D);
        public static final DoublePredicate POSITIVE = value -> Double.isFinite(value) && value > 0.0D;

        private Doubles() {}

        public static DoublePredicate atLeast(double minimum) {
            requireFinite(minimum);
            return value -> Double.isFinite(value) && value >= minimum;
        }

        public static DoublePredicate atMost(double maximum) {
            requireFinite(maximum);
            return value -> Double.isFinite(value) && value <= maximum;
        }

        public static DoublePredicate between(double minimum, double maximum) {
            requireFinite(minimum);
            requireFinite(maximum);
            requireRange(minimum, maximum);

            return value -> Double.isFinite(value) && value >= minimum && value <= maximum;
        }
    }

    public static final class Floats {

        public static final Predicate<Float> NON_NEGATIVE = value -> Float.isFinite(value) && value >= 0.0F;
        public static final Predicate<Float> PERCENTAGE = between(0.0F, 1.0F);
        public static final Predicate<Float> POSITIVE = value -> Float.isFinite(value) && value > 0.0F;

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

        public static final IntPredicate EVEN = value -> value % 2 == 0;
        public static final IntPredicate NEGATIVE = value -> value < 0;
        public static final IntPredicate NON_NEGATIVE = value -> value >= 0;
        public static final IntPredicate NON_POSITIVE = value -> value <= 0;
        public static final IntPredicate ODD = value -> value % 2 != 0;
        public static final IntPredicate POSITIVE = value -> value > 0;

        private Integers() {}

        public static IntPredicate atLeast(int minimum) {
            return value -> value >= minimum;
        }

        public static IntPredicate atMost(int maximum) {
            return value -> value <= maximum;
        }

        public static IntPredicate between(int minimum, int maximum) {
            requireRange(minimum, maximum);
            return value -> value >= minimum && value <= maximum;
        }
    }

    public static final class Longs {

        public static final LongPredicate NEGATIVE = value -> value < 0L;
        public static final LongPredicate NON_NEGATIVE = value -> value >= 0L;
        public static final LongPredicate NON_POSITIVE = value -> value <= 0L;
        public static final LongPredicate POSITIVE = value -> value > 0L;

        private Longs() {}

        public static LongPredicate atLeast(long minimum) {
            return value -> value >= minimum;
        }

        public static LongPredicate atMost(long maximum) {
            return value -> value <= maximum;
        }

        public static LongPredicate between(long minimum, long maximum) {
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

        public static Predicate<String> maxLength(int maximum) {
            requireNonNegative(maximum, "Maximum length");
            return value -> value.length() <= maximum;
        }

        public static Predicate<String> minLength(int minimum) {
            requireNonNegative(minimum, "Minimum length");
            return value -> value.length() >= minimum;
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