/*
 * Copyright (c) 2010 by Guido Steinacker
 */

package de.steinacker.jcg.model;

import javax.validation.constraints.Min;

/**
 * Stores location information of main elements of java class
 *
 * @author Guido Steinacker
 */
public final class Location {
    @Min(0)
    private int startOffset;
    @Min(0)
    private int endOffset;
    @Min(0)
    private long lineNumber;

    public Location(int startOffset, int endOffset, long lineNumber) {
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        this.lineNumber = lineNumber;
    }

    public int getStartOffset() {
        return startOffset;
    }

    public int getEndOffset() {
        return endOffset;
    }

    public long getLineNumber() {
        return lineNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Location location = (Location) o;

        if (endOffset != location.endOffset) return false;
        if (lineNumber != location.lineNumber) return false;
        if (startOffset != location.startOffset) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = startOffset;
        result = 31 * result + endOffset;
        result = 31 * result + (int) (lineNumber ^ (lineNumber >>> 32));
        return result;
    }
}