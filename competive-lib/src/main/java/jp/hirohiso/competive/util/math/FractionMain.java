package jp.hirohiso.competive.util.math;

public class FractionMain {
    public static void main(String[] args) {

    }
    //有理数表現 p /q
    record Fraction(long p, long q) implements Comparable<Fraction> {
        public boolean isUndefined() {
            return p == 0 && q == 0;
        }

        public boolean isPositive() {
            return p > 0;
        }

        public boolean isNegative() {
            return p < 0;
        }

        public boolean isPositiveInfinity() {
            return isPositive() && q == 0;
        }

        public boolean isNegativeInfinity() {
            return isNegative() && q == 0;
        }

        public static Fraction fromWithSimplify(long p, long q) {
            if (p == 0 && q == 0) {
                return new Fraction(0, 0);
            }
            var g = gcd(p, q);
            p /= g;
            q /= g;
            if (q < 0) {//分母を正に揃える
                p = -p;
                q = -q;
            }
            return new Fraction(p, q);
        }

        public static Fraction from(long p, long q) {
            if (p == 0 && q == 0) {
                return new Fraction(0, 0);
            }
            if (q < 0) {//分母を正に揃える
                p = -p;
                q = -q;
            }
            return new Fraction(p, q);
        }
        //有理数公倍数
        public Fraction fracLcm(Fraction f2) {
            var gcd1 = gcd(this.p, f2.p);
            var gcd2 = gcd(this.q, f2.q);
            var lcm = (this.p / gcd1) * f2.p;
            return Fraction.fromWithSimplify(lcm, gcd2);
        }

        //最大公約数
        static long gcd(long a, long b) {
            if (a == 0 || b == 0) {
                if (a == b) {
                    throw new ArithmeticException();
                }
                return Math.abs(a + b);
            }
            long temp;
            while ((temp = a % b) != 0) {
                a = b;
                b = temp;
            }
            return Math.abs(b);
        }

        @Override
        public int compareTo(Fraction o) {
            return Long.compare(this.p * o.q, this.q * o.p);
        }
    }
}
