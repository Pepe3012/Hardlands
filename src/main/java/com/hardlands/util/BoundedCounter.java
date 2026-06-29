package com.hardlands.util;

public final class BoundedCounter {
    private final int limit;
    private int count;

    public BoundedCounter(int limit) {
        if (limit < 0) throw new IllegalArgumentException("Limit cannot be negative.");
        this.limit = limit;
    }

    public boolean tryAdvance() {
        if (this.count >= this.limit) return false;
        this.count++;
        return true;
    }

    public void reset() {
        this.count = 0;
    }

    public boolean isAtLimit() {
        return this.count >= this.limit;
    }

    public int getCount() {
        return this.count;
    }

    public int getLimit() {
        return this.limit;
    }
}