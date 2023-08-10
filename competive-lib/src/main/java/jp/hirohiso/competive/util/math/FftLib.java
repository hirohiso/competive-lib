package jp.hirohiso.competive.util.math;

import java.util.Arrays;

public class FftLib {

    public static void main(String[] args) {

        double[] input = new double[]{0, 1, 2, 3, 4, 5, 6, 7};
        double[] input2 = new double[]{-3, 2, 1, 0};
        ComplexArray ca = new ComplexArray(input);

        ca.print();
        ComplexArray after = ca.fft();
        after.print();
        after.ifft().print();

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

        public ComplexArray compose(ComplexArray target){
            if (this.complexes.length != target.complexes.length){
                throw new IllegalArgumentException();
            }
            ComplexArray result = new ComplexArray(this.size());
            for (int i = 0; i < this.complexes.length; i++) {
                result.complexes[i] = this.complexes[i].multiply(target.complexes[i]);
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
            int c = 0;
            for (int i = 1; i < n; ++i) {
                for (int j = n >> 1; j > (c ^= j); j >>= 1) {
                    ;
                }
                if (i > c) {
                    Complex temp = result.complexes[i];
                    result.complexes[i] = result.complexes[c];
                    result.complexes[c] = temp;
                }
            }

            for (int i = 1; i < n; i *= 2) {
                //要素数N = i *2　のfft計算
                double theta = 2.0 * Math.PI / (i * 2) * (invertFlg ? -1 : 1);

                for (int k = 0; k < i; ++k) {
                    Complex wn = Complex.fromRadian(theta * k);

                    for (int j = 0; j < n; j += 2 * i) {
                        Complex tempComp = result.complexes[k + j + 0];
                        Complex tmpComp = result.complexes[k + j + i].multiply(wn);

                        result.complexes[k + j + 0] = tempComp.add(tmpComp);
                        result.complexes[k + j + i] = tempComp.sub(tmpComp);
                    }
                }
            }
            if (invertFlg){
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
