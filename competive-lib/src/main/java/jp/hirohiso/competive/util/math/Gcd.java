package jp.hirohiso.competive.util.math;

public class Gcd {

    public static void main(String[] args) {
        // TODO 自動生成されたメソッド・スタブ

    }

    public static class MathUtil {

        public static long chineseRemainderTheorem(long[] x, long[] m) {
            if (x.length != m.length) {
                throw new IllegalArgumentException();
            }
            int n = x.length;
            long M = 1;
            long X = 0;
            for (int i = 0; i < n; i++) {
                ExtGcdPara para = getExtGcdPara();
                extgcd(M, m[i], para);
                var x2 = para.x * (x[i] - X);
                X += (M * x2);
                M *= m[i];
                X = (X % M + M) % M;
            }
            return X;
        }

        public static class ExtGcdPara {
            int x;
            int y;

            public ExtGcdPara swap() {
                int temp = this.x;
                this.x = this.y;
                this.y = temp;

                return this;
            }
        }

        public static ExtGcdPara getExtGcdPara() {
            return new ExtGcdPara();

        }

        //最小公倍数
        static long lcm(long a, long b) {
            long temp;
            long c = a;
            c *= b;
            while ((temp = a % b) != 0) {
                a = b;
                b = temp;
            }
            return (long) (c / b);
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

        //拡張ユークリッド互除法
        static long extgcd(long a, long b, ExtGcdPara para) {
            long temp = 0;
            long x0 = 1, x1 = 0;
            long y0 = 0, y1 = 1;

            while ((b) != 0) {
                temp = a % b;
                long x2 = x0 - (a / b) * x1;
                long y2 = y0 - (a / b) * y1;
                x0 = x1;
                x1 = x2;
                y0 = y1;
                y1 = y2;

                a = b;
                b = temp;
            }
            para.x = (int) (x0);
            para.y = (int) (y0);

            return (long) a;
        }

        /*
         * 繰り返し二乗法
         */
        static long powmod(int a, int n, int m) {
            long result = 1;
            long x = a;
            while (n > 0) {
                if ((n & 0b1) == 0b1) {
                    result = (result * x) % m;
                }
                x = (x * x) % m;
                n >>= 1;
            }
            return result;
        }
    }
}
