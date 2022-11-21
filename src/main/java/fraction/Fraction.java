package fraction;

import java.util.Objects;

public class Fraction {
    private long numerator;
    private long denominator;

    public Fraction(long numerator, long denominator) {
        this.numerator = numerator;
        this.denominator = denominator;

        // Euclidean algorithm
        while (numerator > 0 && denominator > 0) {
            if (numerator > denominator) {
                numerator %= denominator;
            } else {
                denominator %= numerator;
            }
        }

        this.numerator /= (numerator + denominator);
        this.denominator /= (numerator + denominator);
    }


    public long getNumerator() {
        return numerator;
    }

    public long getDenominator() {
        return denominator;
    }

    public double asDouble() {
        return (double)numerator / (double)denominator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Fraction fraction = (Fraction) o;

        return numerator == fraction.numerator && denominator == fraction.denominator;
    }

    @Override
    public int hashCode() {
        return Objects.hash(numerator, denominator);
    }

    @Override
    public String toString() {
        return "Fraction{" + numerator + ", " + denominator + "}";
    }
}
