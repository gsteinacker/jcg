package test.types;

/**
 * This is a comment.
 */
public enum EnumType02 {
    ONE(1),
    TWO(2),
    THREE(3);

    private int n;

    private EnumType02(final int n) {
        this.n = n;
    }

    public static EnumType02 valueOf(int n) {
        for (final EnumType02 enumType02 : EnumType02.values()) {
            if (enumType02.n == n)
                return enumType02;
        }
        throw new IllegalStateException("Bumm!");
    }
}