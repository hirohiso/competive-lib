package jp.hirohiso.competive.util.math;

import java.util.Arrays;

public class FftLib {

    public static void main(String[] args) {

        //todo:ライブラリを呼び出す前に畳み込み後の配列の長さに合わせて0埋めが必要。
        double[] input = new double[]{1, 2, 3, 4, 0, 0, 0, 0};
        double[] input2 = new double[]{1, 2, 3, 4, 0, 0, 0, 0};
        double[] sample = new double[]{1, 4, 10, 20, 25, 24, 16, 0};
        ComplexArray ca = new ComplexArray(input);
        ca.print();
        ca.fft().print();

        ComplexArray ca2 = new ComplexArray(input2);
        ca2.print();
        ca2.fft().print();
        ca2.fft().ifft().print();

        var ca3 = new ComplexArray(sample);
        ca3.fft().print();
        ca.fft().compose(ca2.fft()).ifft().print();
    }

    public static class ComplexArray {

        Complex[] complexes;

        public ComplexArray(int size) {
            complexes = new Complex[size];
        }

        public ComplexArray(double[] r) {
            complexes = new Complex[r.length];
            for (int i = 0; i < r.length; i++) {
                complexes[i] = new Complex(r[i], 0);
            }
        }

        public ComplexArray(ComplexArray orig) {
            complexes = new Complex[orig.complexes.length];
            for (int j = 0; j < complexes.length; j++) {
                complexes[j] = orig.complexes[j];
            }
        }

        public int size() {
            return this.complexes.length;
        }

        public ComplexArray compose(ComplexArray target) {
            if (this.complexes.length != target.complexes.length) {
                throw new IllegalArgumentException();
            }
            var newSize = target.size();
            /*
             while (this.complexes.length + target.complexes.length + 1 > newSize) {
             newSize <<= 1;
             }
             */
            var resize1 = new ComplexArray(newSize);
            var resize2 = new ComplexArray(newSize);
            for (int i = 0; i < newSize; i++) {
                if (i < this.complexes.length) {
                    resize1.complexes[i] = this.complexes[i];
                    resize2.complexes[i] = target.complexes[i];
                } else {
                    resize1.complexes[i] = new Complex(0, 0);
                    resize2.complexes[i] = new Complex(0, 0);
                }
            }
            ComplexArray result = new ComplexArray(resize1.size());
            for (int i = 0; i < newSize; i++) {
                result.complexes[i] = resize1.complexes[i].multiply(resize2.complexes[i]);
            }
            return result;
        }

        public ComplexArray fft() {
            return fftInner(false);
        }

        public ComplexArray ifft() {
            return fftInner(true);
        }

        public ComplexArray fftInner(boolean invertFlg) {
            int n = this.size();

            ComplexArray result = new ComplexArray(this);
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
                    var temp = result.complexes[i];
                    result.complexes[i] = result.complexes[j];
                    result.complexes[j] = temp;
                }
            }

            for (int len = 2; len <= n; len <<= 1) {
                //要素数N = len *2　のfft計算
                double theta = 2.0 * Math.PI / len * (invertFlg ? -1 : 1);
                var wlen = Complex.fromRadian(theta);

                for (int i = 0; i < n; i += len) {
                    Complex wn = new Complex(1, 0);

                    for (int j = 0; j < len / 2; j++) {
                        Complex u = result.complexes[i + j];
                        Complex v = result.complexes[i + j + len / 2].multiply(wn);

                        result.complexes[i + j] = u.add(v);
                        result.complexes[i + j + len / 2] = u.sub(v);

                        wn = wn.multiply(wlen);
                    }
                }
            }
            if (invertFlg) {
                for (int i = 0; i < result.complexes.length; i++) {
                    result.complexes[i] = result.complexes[i].divide(n);
                }
            }
            return result;
        }

        public void print() {
            for (Complex c : this.complexes) {
                System.out.print(c);
            }
            System.out.println(System.lineSeparator());
        }
    }


    public static class Complex {
        private final double re;
        private final double im;

        public Complex(double re, double im) {
            this.re = re;
            this.im = im;
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

        public double abs() {
            return Math.sqrt(re * re + im * im);
        }

        public static Complex fromRadian(double radian) {
            return new Complex(Math.cos(radian), Math.sin(radian));
        }

        public Complex divide(double d) {
            return new Complex(re / d, im / d);
        }

        @Override
        public String toString() {
            return "{" + re +
                    ',' + im + "} ";
        }
    }
}
