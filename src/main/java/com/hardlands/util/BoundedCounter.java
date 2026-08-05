package com.hardlands.util;

/**
 * Maintains a mutable integer count that cannot advance beyond a fixed limit.
 *
 * <p>The counter starts at {@code 0}. Calls to {@link #tryAdvance()} increment the count by one
 * until the configured limit is reached. Further advancement attempts return {@code false} without
 * modifying the count.</p>
 *
 * <p>A limit of {@code 0} is valid and creates a counter that is immediately at its limit. This
 * class is mutable and does not provide thread-safety guarantees.</p>
 */
public final class BoundedCounter {
    /**
     * Maximum value that the count may reach.
     */
    private final int limit;

    /**
     * Current counter value.
     */
    private int count;

    /**
     * Creates a counter with the specified maximum value.
     *
     * @param limit the maximum value the counter may reach
     * @throws IllegalArgumentException if {@code limit} is negative
     */
    public BoundedCounter(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Limit cannot be negative.");
        }

        this.limit = limit;
    }

    /**
     * Attempts to increase the current count by one.
     *
     * <p>The count is not modified when it has already reached the configured limit.</p>
     *
     * @return {@code true} if the count was increased; {@code false} if the limit had already
     *         been reached
     */
    public boolean tryAdvance() {
        if (this.count >= this.limit) {
            return false;
        }

        this.count++;
        return true;
    }

    /**
     * Resets the current count to {@code 0}.
     */
    public void reset() {
        this.count = 0;
    }

    /**
     * Indicates whether the current count has reached the configured limit.
     *
     * @return {@code true} if the count is at its limit; otherwise {@code false}
     */
    public boolean isAtLimit() {
        return this.count >= this.limit;
    }

    /**
     * Returns the current counter value.
     *
     * @return the current count
     */
    public int getCount() {
        return this.count;
    }

    /**
     * Returns the maximum value this counter may reach.
     *
     * @return the configured limit
     */
    public int getLimit() {
        return this.limit;
    }
}