package jp.hirohiso.competive.util.string;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.function.LongBinaryOperator;

public class RollingHashMain {

    public static void main(String[] args) {
        var a = ShiftHash.of('a');
        var b = ShiftHash.of('b');
        var c = ShiftHash.of('c');

        var ab = a.applyR(b);
        var bc = b.applyR(c);

        var invab = b.applyL(a);
        var abc = a.applyR(b).applyR(c);

        System.out.println("a:" + a);
        System.out.println("b:" + b);
        System.out.println("c:" + c);
        System.out.println("ab:" + ab);
        System.out.println("bc" + bc);
        System.out.println("ab:" + invab);
        System.out.println("abc:" + abc);
        System.out.println("ab:" + abc.rightInv(c));//abcの右側からcを引いたもの
        System.out.println("bc:" + abc.leftInv(a));//abcの左側からaを引いたもの

        var test = ShiftHash.E();
        System.out.println("test:" + test);
        System.out.println("test:" + test.applyR(a));
    }

    /**
     * 並び順のハッシュを扱うモノイド
     * <p>
     * 並びそのものは内部で保持しないため、逆元の作用は並びが一致している場合のみ有効
     *
     * @param v
     */
    record ShiftHash(long v, int size) {
        static long b = 3491;//基数
        static long m = 999_999_937;
        //64bit mod
        //static long m = (1L << 61) - 1;

        static LongBinaryOperator mul = (a, b) -> (a * b) % m;

        //オーバフロー対策付き
        //static LongBinaryOperator mul = (a, b) -> mul(a, b, m);

        static ShiftHash of(char c) {
            return new ShiftHash((c) % m, 1);
        }

        //単位元を返却する
        static ShiftHash E() {
            return new ShiftHash(0, 0);
        }

        //右側に追加
        ShiftHash applyR(ShiftHash sh) {
            return apply(this, sh);
        }

        //左側に追加
        ShiftHash applyL(ShiftHash sh) {
            return apply(sh, this);
        }

        //
        ShiftHash rightInv(ShiftHash sh) {
            var div = modinv(powmod(b, sh.size, m), m);
            //shを引き算して切り詰める
            var h = this.v;
            h -= sh.v;
            h = mod(h, m);
            //h *= div;
            //h %= m;
            h = mul.applyAsLong(h, div);
            return new ShiftHash(h, this.size - sh.size);
        }

        ShiftHash leftInv(ShiftHash sh) {
            var h1 = sh.v;
            //h1 *= powmod(b, this.size - sh.size, m);
            //h1 %= m;
            h1 = mul.applyAsLong(h1, powmod(b, this.size - sh.size, m));
            var h2 = this.v;
            h2 -= h1;
            h2 = mod(h2, m);
            return new ShiftHash(h2, this.size - sh.size);
        }

        private static ShiftHash apply(ShiftHash l, ShiftHash r) {
            var h = l.v;
            //h *= powmod(b, r.size, m);
            //h %= m;
            h = mul.applyAsLong(h, powmod(b, r.size, m));
            h += r.v;
            h %= m;
            return new ShiftHash(h, l.size + r.size);
        }


        /*
         * 繰り返し二乗法
         */
        static long powmod(long a, int n, long m) {
            var result = 1L;
            var x = a;
            while (n > 0) {
                if ((n & 0b1) == 0b1) {
                    //result = result * x) % m;
                    result = mul.applyAsLong(result, x);
                }
                //x = (x * x) % m;
                x = mul.applyAsLong(x, x);
                n >>= 1;
            }
            return result;
        }

        //オーバーフローしない乗算 ( m == 2^61 - 1) only
        static long mul(long a, long b, long m) {
            var mask30 = (1L << 30) - 1;
            var mask31 = (1L << 31) - 1;
            var a1 = a >> 31;
            var a0 = a & mask31;
            var b1 = b >> 31;
            var b0 = b & mask31;
            var mid = a1 * b0 + a0 * b1;
            var midu = mid >> 30;
            var midd = mid & mask30;
            var result = a1 * b1 * 2 + midu + (midd << 31) + a0 * b0;
            return mod(result, m);
        }

        static long mod(long a, long m) {
            return (a % m + m) % m;
        }

        /*
         * 逆元を求める
         */
        static long modinv(long a, long m) {
            var result = 1L;
            var n = m - 2;
            var x = mod(a, m);
            while (n > 0) {
                if ((n & 0b1) == 0b1) {
                    result = mul.applyAsLong(result, x);
                }
                x = mul.applyAsLong(x, x);
                n >>= 1;
            }
            return mod(result, m);
        }
    }


    public static class RollingHash {
        private long b = 3491;
        private long m = 999999937;
        private char zero = 'a';


        //strのハッシュ値を返す
        public long get(String str) {
            var size = str.length();
            var hash = 0l;
            for (int i = 0; i < size; i++) {
                hash = shift(hash, (str.charAt(i) - zero));
            }
            return hash;
        }

        //
        public Iterable<Long> iterator(String str, int size) {
            var result = new Iterable<Long>() {
                private String str;
                private int size;
                private long b;
                private long m;
                private long bsize = 1;
                private int now = -1;
                private long hash = 0;

                public Iterable<Long> init(String str, int size, long b, long m) {
                    this.str = str;
                    this.size = size;
                    this.b = b;
                    this.m = m;
                    for (int i = 0; i < size - 1; i++) {
                        this.bsize *= b;
                        this.bsize %= m;
                    }
                    return this;
                }

                @Override
                public Iterator<Long> iterator() {
                    return new Iterator<>() {
                        @Override
                        public boolean hasNext() {
                            return now + size < str.length();
                        }

                        @Override
                        public Long next() {
                            now++;
                            if (now == 0) {
                                for (int i = 0; i < size; i++) {
                                    hash = shift(hash, (str.charAt(i) - zero));
                                }
                                return hash;
                            }
                            var h = (long) (str.charAt(now - 1) - zero);
                            h *= bsize;
                            h %= m;
                            hash -= h;
                            hash = shift(hash, (str.charAt(now + (size - 1)) - zero));
                            return hash;
                        }
                    };
                }

            }.init(str, size, b, m);
            return result;
        }

        private long shift(long h, long c) {
            h *= b;
            h %= m;
            h += c;
            h %= m;
            return h;
        }

    }
}
