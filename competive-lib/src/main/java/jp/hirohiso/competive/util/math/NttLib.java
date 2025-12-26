package jp.hirohiso.competive.util.math;

import java.util.Arrays;

public class NttLib {

    public static void main(String[] args) {
        var fft = new NumberTheoreticTransform();
        var str = Arrays.toString(fft.convolution(new long[]{1, 2, 3}, new long[]{2, 3, 4}));
        System.out.println(str);
    }

    static class NumberTheoreticTransform {
        private NttRing ie1;
        private NttRing ie2;

        public NumberTheoreticTransform() {
            this.ie1 = NttRing.ie1();
            this.ie2 = NttRing.ie2();
        }

        public NttRing[] compose(NttRing[] com1, NttRing[] com2) {

            NttRing[] result = new NttRing[com1.length];
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
            return Arrays.copyOf(toLongArray(cc), a.length + b.length - 1);
        }

        private long[] toLongArray(NttRing[] target) {
            var size = target.length;
            var result = new long[size];
            Arrays.setAll(result, i -> target[i].toLong());
            return result;
        }

        private NttRing[] toFourierArray(long[] target, int targetSize) {
            var result = new NttRing[targetSize];
            for (int i = 0; i < result.length; i++) {
                if (i < target.length) {
                    result[i] = NttRing.from(target[i]);
                } else {
                    result[i] = ie1;
                }
            }
            return result;
        }

        public NttRing[] fft(NttRing[] target) {
            return fftInner(target, false);
        }

        public NttRing[] ifft(NttRing[] target) {
            return fftInner(target, true);
        }

        public NttRing[] fftInner(NttRing[] target, boolean invertFlg) {
            int n = target.length;

            var result = new NttRing[target.length];
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
                var wlen = invertFlg ? NttRing.invRoot(len) : NttRing.root(len);

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
                    result[i] = result[i].divide(NttRing.from(n));
                }
            }
            return result;
        }
    }

    static final class NttRing {
        private final long val;
        private static final long mod = 998244353;
        private static final long PRIMITIVE_ROOT = 3;

        private static long[] roots;
        private static long[] invRoots;
        private static int maxLog;

        static {
            maxLog = Integer.numberOfTrailingZeros(Integer.highestOneBit(1 << 23));
            roots = new long[maxLog + 1];
            invRoots = new long[maxLog + 1];
            // 2^maxLog 次の原始根
            long root = modPow(PRIMITIVE_ROOT, (mod - 1) >> maxLog, mod);
            long invRoot = modPow(root, mod - 2, mod);

            roots[maxLog] = root;
            invRoots[maxLog] = invRoot;

            for (int i = maxLog; i > 0; i--) {
                roots[i - 1] = roots[i] * roots[i] % mod;
                invRoots[i - 1] = invRoots[i] * invRoots[i] % mod;
            }
        }

        private static long modPow(long a, long e, long mod) {
            long r = 1;
            while (e > 0) {
                if ((e & 1) == 1) r = r * a % mod;
                a = a * a % mod;
                e >>= 1;
            }
            return r;
        }

        public NttRing(long v) {
            this.val = v;
        }

        public static NttRing from(long l) {
            return new NttRing(l);
        }

        public long toLong() {
            return this.val;
        }

        private void sameMod(NttRing o) {
            if (this.mod != o.mod) {
                throw new IllegalArgumentException("Not Equal mod :" + o);
            }
        }

        public NttRing multiply(NttRing o) {
            sameMod(o);
            return new NttRing(val * o.val % mod);
        }

        public NttRing add(NttRing o) {
            sameMod(o);
            return new NttRing((val + o.val) % mod);
        }

        public NttRing sub(NttRing o) {
            sameMod(o);
            return new NttRing((val + mod - o.val) % mod);
        }

        //加法の単位元の取得
        private static NttRing ie1() {
            return new NttRing(0);
        }

        //乗法の単位元の取得
        private static NttRing ie2() {
            return new NttRing(1);
        }


        //1 の length根を取得
        public static NttRing root(int length) {
            int k = Integer.numberOfTrailingZeros(length);
            return new NttRing(roots[k]);
        }

        //1 の length根の逆元を取得
        public static NttRing invRoot(int length) {
            //要素数N = len *2　のfft計算
            //1 / len 乗根の取得
            int k = Integer.numberOfTrailingZeros(length);
            return new NttRing(invRoots[k]);
        }

        public NttRing divide(NttRing d) {
            sameMod(d);
            var inv = modPow(d.val, mod - 2, mod);
            return new NttRing(val * inv % mod);
        }

        @Override
        public String toString() {
            return "{" + val + ',' + mod + "} ";
        }
    }
}
