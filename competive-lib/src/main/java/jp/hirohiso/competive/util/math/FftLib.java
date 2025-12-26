package jp.hirohiso.competive.util.math;

import java.util.Arrays;
import java.util.function.BiFunction;

public class FftLib {

    public static void main(String[] args) {
        var fft = new FastFourierTransform(Complex.ie1(), Complex.ie2());
        var str = Arrays.toString(fft.convolution(new long[]{1, 2, 3, 4}, new long[]{1, 2, 4, 8}));
        System.out.println(str);
    }

    static class FastFourierTransform {
        private Complex ie1;
        private Complex ie2;

        public FastFourierTransform(Complex ie1, Complex ie2) {
            this.ie1 = ie1;
            this.ie2 = ie2;
        }

        public Complex[] compose(Complex[] com1, Complex[] com2) {

            Complex[] result = new Complex[com1.length];
            for (int i = 0; i < result.length; i++) {
                result[i] = com1[i].multiply(com2[i]);
            }
            return result;
        }

        public long[] convolution(long[] a, long[] b) {
            var newSize = 1;
            while (a.length + b.length - 1 > newSize) {
                newSize <<= 1;
            }
            //aをlongから2冪の配列の環へ
            var ca = toFourierArray(a, newSize);
            //aをに変換
            var fa = fft(ca);
            //bをlongから環へ
            var cb = toFourierArray(b, newSize);
            //bを２冪の配列に変換
            var fb = fft(cb);
            //aとbを畳み込み
            var c = compose(fa, fb);

            //cを逆フーリエ変換して返却
            var cc = ifft(c);
            return toLongArray(cc);
        }

        private long[] toLongArray(Complex[] target) {
            var size = target.length;
            var result = new long[size];
            Arrays.setAll(result, i -> target[i].toLong());
            return result;
        }

        private Complex[] toFourierArray(long[] target, int targetSize) {
            var result = new Complex[targetSize];
            for (int i = 0; i < result.length; i++) {
                if (i < target.length) {
                    result[i] = Complex.from(target[i]);
                } else {
                    result[i] = ie1;
                }
            }
            return result;
        }

        public Complex[] fft(Complex[] target) {
            return fftInner(target, false);
        }

        public Complex[] ifft(Complex[] target) {
            return fftInner(target, true);
        }

        public Complex[] fftInner(Complex[] target, boolean invertFlg) {
            int n = target.length;

            var result = new Complex[target.length];
            for (int i = 0; i < result.length; i++) {
                result[i] = target[i];
            }
            if (n == 1) {
                return result;
            }

            //バタフライ演算
            //bit-reverse
            for (int i = 1, j = 0; i < n; ++i) {
                var bit = n >> 1;
                while ((j & bit) != 0) {
                    j ^= bit;
                    bit >>= 1;
                }
                j ^= bit;
                if (i < j) {
                    var temp = result[i];
                    result[i] = result[j];
                    result[j] = temp;
                }
            }

            for (int len = 2; len <= n; len <<= 1) {
                //1 の len 乗根の取得
                var wlen = invertFlg ? Complex.invRoot(len) : Complex.root(len);

                for (int i = 0; i < n; i += len) {
                    //単位元
                    var wn = ie2;

                    for (int j = 0; j < len / 2; j++) {
                        var u = result[i + j];
                        var v = result[i + j + len / 2].multiply(wn);

                        result[i + j] = u.add(v);
                        result[i + j + len / 2] = u.sub(v);

                        wn = wn.multiply(wlen);
                    }
                }
            }
            if (invertFlg) {
                for (int i = 0; i < result.length; i++) {
                    result[i] = result[i].divide(Complex.from(n));
                }
            }
            return result;
        }
    }

    static final class Complex {
        private final double re;
        private final double im;

        public Complex(double re, double im) {
            this.re = re;
            this.im = im;
        }

        public static Complex from(long l) {
            return new Complex(l, 0);
        }

        public long toLong() {
            return Math.round(this.re);
        }

        public Complex multiply(Complex o) {
            return new Complex((re * o.re) - (im * o.im), (re * o.im) + (im * o.re));
        }

        public Complex add(Complex o) {
            return new Complex(re + o.re, im + o.im);
        }

        public Complex sub(Complex o) {
            return new Complex(re - o.re, im - o.im);
        }

        //加法の単位元の取得
        private static Complex ie1() {
            return new Complex(0, 0);
        }

        //乗法の単位元の取得
        private static Complex ie2() {
            return new Complex(1, 0);
        }

        private static Complex fromRadian(double radian) {
            return new Complex(Math.cos(radian), Math.sin(radian));
        }

        //1 の length根を取得
        public static Complex root(int length) {
            //要素数N = len *2　のfft計算
            double theta = 2.0 * Math.PI / length;
            //1 / len 乗根の取得
            return Complex.fromRadian(theta);
        }

        //1 の length根の逆元を取得
        public static Complex invRoot(int length) {
            //要素数N = len *2　のfft計算
            double theta = 2.0 * Math.PI / length * -1;
            //1 / len 乗根の取得
            return Complex.fromRadian(theta);
        }

        public Complex divide(Complex d) {
            var m = (d.re * d.re) - (d.im * d.im);
            var c = this.multiply(new Complex(d.re, -d.im));
            return new Complex(c.re / m, c.im / m);
        }

        @Override
        public String toString() {
            return "{" + re + ',' + im + "} ";
        }
    }
}
