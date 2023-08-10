package jp.hirohiso.competive.util.math;

import java.util.Arrays;
import java.util.function.LongFunction;
import java.util.function.LongUnaryOperator;

public class MatrixLib {
    public static void main(String[] args) {
        Matrix a = new Matrix.Builder(2, 2)
                .setValue(0, 0, 1).setValue(0, 1, 0)
                .setValue(1, 0, 0).setValue(1, 1, 1)
                .build();

        Matrix b = a.multiply(a);
        System.out.println(b);

        Matrix c = new Matrix.Builder(2, 3)
                .setRowValue(0, new long[]{1, 2, 3})
                .setRowValue(1, new long[]{0, 1, 2})
                .build();
        System.out.println(c);
        System.out.println(a.multiply(c));
    }


    public static class Matrix {
        private final long[][] values;

        public Matrix(long[][] values) {
            this.values = values;
        }

        public Matrix apply(LongUnaryOperator func){
            Builder builder = new Builder(this.values.length, this.values[0].length);
            for (int i = 0; i < this.values.length; i++) {
                for (int j = 0; j < this.values[i].length; j++) {
                    builder.setValue(i, j, func.applyAsLong(this.values[i][j]));
                }
            }
            return builder.build();
        }

        public Matrix add(Matrix target) {
            Builder builder = new Builder(this.values.length, this.values[0].length);
            for (int i = 0; i < this.values.length; i++) {
                for (int j = 0; j < this.values[i].length; j++) {
                    builder.setValue(i, j, this.values[i][j] + target.values[i][j]);
                }
            }
            return builder.build();
        }

        public Matrix multiply(Matrix target) {
            if (this.values[0].length != target.values.length) {
                throw new IllegalArgumentException("行列の要素数が一致しない");
            }
            int cols = target.values[0].length;
            int rows = this.values.length;
            Builder builder = new Builder(rows, cols);
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    builder.setValue(i, j, multiplyRowCol(i, j, target));
                }
            }
            return builder.build();
        }

        //i行,j列の直積をとる
        private long multiplyRowCol(int i, int j, Matrix target) {
            long result = 0;
            for (int k = 0; k < this.values[i].length; k++) {
                result += this.values[i][k] * target.values[k][j];
            }
            return result;
        }

        @Override
        public String toString() {
            String values = "";
            for (long[] a : this.values) {
                values += Arrays.toString(a);
            }
            return "Matrix{" +
                    "values=" + values +
                    '}';
        }

        public static class Builder {
            long[][] value;

            public Builder(int n, int m) {
                this.value = new long[n][m];
            }

            public Builder setValue(int i, int j, long value) {
                this.value[i][j] = value;
                return this;
            }

            public Builder setRowValue(int i, long[] values) {
                if (this.value[0].length != values.length) {
                    throw new IllegalArgumentException();
                }
                for (int j = 0; j < values.length; j++) {
                    this.value[i][j] = values[j];
                }
                return this;
            }

            public Matrix build() {
                return new Matrix(this.value);
            }
        }
    }
}
