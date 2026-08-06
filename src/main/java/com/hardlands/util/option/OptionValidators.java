package com.hardlands.util.option;

import org.jspecify.annotations.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * Provides reusable predicates for validating option values.
 *
 * <p>The validators returned by this class can be supplied to option-related APIs that accept
 * {@link Predicate} or {@link IntPredicate} instances. General-purpose validators are declared
 * directly on this class, while type-specific validators are grouped into nested utility classes.</p>
 *
 * <p>Unless otherwise documented, validators expect non-null input values. Passing {@code null}
 * to validators that invoke methods on the tested value may result in a
 * {@link NullPointerException}.</p>
 */
public final class OptionValidators {
    /**
     * Prevents utility-class instantiation.
     */
    private OptionValidators() {}

    /**
     * Creates a predicate that accepts values equal to the expected value.
     *
     * <p>Equality is evaluated through {@link Objects#equals(Object, Object)}, so both the expected
     * value and the tested value may be {@code null}.</p>
     *
     * @param expectedValue the value that tested values must equal
     * @param <T> the validated value type
     * @return a predicate that accepts values equal to {@code expectedValue}
     */
    public static <T> @NonNull Predicate<T> equalTo(T expectedValue) {
        return value -> Objects.equals(value, expectedValue);
    }

    /**
     * Creates a predicate that accepts values contained in the supplied collection.
     *
     * <p>The supplied values are copied when this method is called. Later modifications to the
     * original collection therefore do not affect the returned predicate.</p>
     *
     * @param values the accepted values
     * @param <T> the validated value type
     * @return a predicate that accepts values contained in {@code values}
     * @throws NullPointerException if {@code values} is {@code null} or contains {@code null}
     */
    public static <T> @NonNull Predicate<T> oneOf(Collection<? extends T> values) {
        Set<? extends T> acceptedValues = Set.copyOf(values);
        return acceptedValues::contains;
    }

    /**
     * Creates a predicate that negates the result of another predicate.
     *
     * @param validator the predicate to negate
     * @param <T> the validated value type
     * @return a predicate that accepts values rejected by {@code validator}
     * @throws NullPointerException if {@code validator} is {@code null}
     */
    public static <T> @NonNull Predicate<T> not(Predicate<? super T> validator) {
        Objects.requireNonNull(validator, "validator");
        return value -> !validator.test(value);
    }

    /**
     * Provides reusable validators for integer values.
     */
    public static final class Integers {
        /**
         * Accepts integers greater than {@code 0}.
         */
        public static final IntPredicate POSITIVE = value -> value > 0;

        /**
         * Accepts integers greater than or equal to {@code 0}.
         */
        public static final IntPredicate NON_NEGATIVE = value -> value >= 0;

        /**
         * Accepts integers less than {@code 0}.
         */
        public static final IntPredicate NEGATIVE = value -> value < 0;

        /**
         * Accepts integers less than or equal to {@code 0}.
         */
        public static final IntPredicate NON_POSITIVE = value -> value <= 0;

        /**
         * Accepts integers divisible by {@code 2}.
         */
        public static final IntPredicate EVEN = value -> value % 2 == 0;

        /**
         * Accepts integers that are not divisible by {@code 2}.
         */
        public static final IntPredicate ODD = value -> value % 2 != 0;

        /**
         * Prevents utility-class instantiation.
         */
        private Integers() {}

        /**
         * Creates a predicate that accepts integers greater than or equal to a minimum value.
         *
         * @param minimum the inclusive minimum value
         * @return a predicate that accepts values at least equal to {@code minimum}
         */
        public static @NonNull IntPredicate atLeast(int minimum) {
            return value -> value >= minimum;
        }

        /**
         * Creates a predicate that accepts integers less than or equal to a maximum value.
         *
         * @param maximum the inclusive maximum value
         * @return a predicate that accepts values at most equal to {@code maximum}
         */
        public static @NonNull IntPredicate atMost(int maximum) {
            return value -> value <= maximum;
        }

        /**
         * Creates a predicate that accepts integers within an inclusive range.
         *
         * @param minimum the inclusive minimum value
         * @param maximum the inclusive maximum value
         * @return a predicate that accepts values between {@code minimum} and {@code maximum}
         * @throws IllegalArgumentException if {@code minimum} is greater than {@code maximum}
         */
        public static @NonNull IntPredicate betweenInclusive(int minimum, int maximum) {
            if (minimum > maximum) {
                throw new IllegalArgumentException("Minimum cannot be greater than maximum");
            }

            return value -> value >= minimum && value <= maximum;
        }
    }

    /**
     * Provides reusable validators for string values.
     *
     * <p>Validators in this class expect non-null strings.</p>
     */
    public static final class Strings {
        /**
         * Accepts strings containing at least one character.
         */
        public static final Predicate<String> NON_EMPTY = value -> !value.isEmpty();

        /**
         * Accepts strings containing at least one non-whitespace character.
         */
        public static final Predicate<String> NON_BLANK = value -> !value.isBlank();

        /**
         * Prevents utility-class instantiation.
         */
        private Strings() {}

        /**
         * Creates a predicate that accepts strings with at least the specified number of characters.
         *
         * @param minimumLength the inclusive minimum string length
         * @return a predicate that accepts strings whose length is at least {@code minimumLength}
         * @throws IllegalArgumentException if {@code minimumLength} is negative
         */
        public static @NonNull Predicate<String> minimumLength(int minimumLength) {
            if (minimumLength < 0) {
                throw new IllegalArgumentException("Minimum length cannot be negative");
            }

            return value -> value.length() >= minimumLength;
        }

        /**
         * Creates a predicate that accepts strings with no more than the specified number of
         * characters.
         *
         * @param maximumLength the inclusive maximum string length
         * @return a predicate that accepts strings whose length is at most {@code maximumLength}
         * @throws IllegalArgumentException if {@code maximumLength} is negative
         */
        public static @NonNull Predicate<String> maximumLength(int maximumLength) {
            if (maximumLength < 0) {
                throw new IllegalArgumentException("Maximum length cannot be negative");
            }

            return value -> value.length() <= maximumLength;
        }

        /**
         * Creates a predicate that accepts strings whose lengths are within an inclusive range.
         *
         * @param minimumLength the inclusive minimum string length
         * @param maximumLength the inclusive maximum string length
         * @return a predicate that accepts strings whose lengths are within the specified range
         * @throws IllegalArgumentException if {@code minimumLength} is negative
         * @throws IllegalArgumentException if {@code minimumLength} is greater than
         *         {@code maximumLength}
         */
        public static @NonNull Predicate<String> lengthBetweenInclusive(
                int minimumLength,
                int maximumLength
        ) {
            if (minimumLength < 0) {
                throw new IllegalArgumentException("Minimum length cannot be negative");
            }

            if (minimumLength > maximumLength) {
                throw new IllegalArgumentException(
                        "Minimum length cannot be greater than maximum length"
                );
            }

            return value -> value.length() >= minimumLength && value.length() <= maximumLength;
        }

        /**
         * Creates a predicate that accepts strings matching the supplied regular expression pattern.
         *
         * <p>The entire tested string must match the pattern, as defined by
         * {@link java.util.regex.Matcher#matches()}.</p>
         *
         * @param pattern the pattern used to validate strings
         * @return a predicate that accepts strings matching {@code pattern}
         * @throws NullPointerException if {@code pattern} is {@code null}
         */
        public static @NonNull Predicate<String> matches(Pattern pattern) {
            return value -> pattern.matcher(value).matches();
        }
    }

    /**
     * Provides reusable validators for collection values.
     *
     * <p>Validators in this class inspect collection size and expect non-null collections.</p>
     */
    public static final class Collections {
        /**
         * Accepts collections containing at least one element.
         */
        public static final Predicate<Collection<?>> NON_EMPTY = value -> !value.isEmpty();

        /**
         * Prevents utility-class instantiation.
         */
        private Collections() {}

        /**
         * Creates a predicate that accepts collections containing at least the specified number of
         * elements.
         *
         * @param minimumSize the inclusive minimum collection size
         * @return a predicate that accepts collections whose size is at least {@code minimumSize}
         * @throws IllegalArgumentException if {@code minimumSize} is negative
         */
        public static @NonNull Predicate<Collection<?>> minimumSize(int minimumSize) {
            if (minimumSize < 0) {
                throw new IllegalArgumentException("Minimum size cannot be negative");
            }

            return value -> value.size() >= minimumSize;
        }

        /**
         * Creates a predicate that accepts collections containing no more than the specified number
         * of elements.
         *
         * @param maximumSize the inclusive maximum collection size
         * @return a predicate that accepts collections whose size is at most {@code maximumSize}
         * @throws IllegalArgumentException if {@code maximumSize} is negative
         */
        public static @NonNull Predicate<Collection<?>> maximumSize(int maximumSize) {
            if (maximumSize < 0) {
                throw new IllegalArgumentException("Maximum size cannot be negative");
            }

            return value -> value.size() <= maximumSize;
        }
    }

    /**
     * Provides reusable validators for map values.
     *
     * <p>Validators in this class inspect map size and expect non-null maps.</p>
     */
    public static final class Maps {
        /**
         * Accepts maps containing at least one entry.
         */
        public static final Predicate<Map<?, ?>> NON_EMPTY = value -> !value.isEmpty();

        /**
         * Prevents utility-class instantiation.
         */
        private Maps() {}

        /**
         * Creates a predicate that accepts maps containing at least the specified number of entries.
         *
         * @param minimumSize the inclusive minimum map size
         * @return a predicate that accepts maps whose size is at least {@code minimumSize}
         * @throws IllegalArgumentException if {@code minimumSize} is negative
         */
        public static @NonNull Predicate<Map<?, ?>> minimumSize(int minimumSize) {
            if (minimumSize < 0) {
                throw new IllegalArgumentException("Minimum size cannot be negative");
            }

            return value -> value.size() >= minimumSize;
        }

        /**
         * Creates a predicate that accepts maps containing no more than the specified number of
         * entries.
         *
         * @param maximumSize the inclusive maximum map size
         * @return a predicate that accepts maps whose size is at most {@code maximumSize}
         * @throws IllegalArgumentException if {@code maximumSize} is negative
         */
        public static @NonNull Predicate<Map<?, ?>> maximumSize(int maximumSize) {
            if (maximumSize < 0) {
                throw new IllegalArgumentException("Maximum size cannot be negative");
            }

            return value -> value.size() <= maximumSize;
        }
    }
}