package jp.hirohiso.competive.util.set;

public class ExtendedBitSetMain {
    public static void main(String[] args) {
        ExtendedBitSet a = new ExtendedBitSet(128);
        a.set(0);
        a.set(63);
        a.set(64);
        System.out.println(a.get(0));   // true
        System.out.println(a.get(1));   // false
        System.out.println(a.get(63));  // true
        System.out.println(a.get(64));  // true

        ExtendedBitSet b = new ExtendedBitSet(128);
        b.set(63);
        b.set(127);

        System.out.println(a.and(b));   // {63}
        System.out.println(a.or(b));    // {0,63,64,127}
        System.out.println(a.xor(b));   // {0,64,127}

        ExtendedBitSet c = new ExtendedBitSet(128);
        c.set(0);
        c.set(63);
        System.out.println(c.shiftl(1)); // {1,64}
        System.out.println(c.shiftr(1)); // {62}  (bit 0 が消える)
    }

    static class ExtendedBitSet {
        private final long[] words;
        private final int numBits;

        ExtendedBitSet(int numBits) {
            this.numBits = numBits;
            this.words = new long[(numBits + 63) >>> 6];
        }

        private ExtendedBitSet(long[] words, int numBits) {
            this.numBits = numBits;
            this.words = words.clone();
        }

        void set(int index) {
            words[index >>> 6] |= 1L << (index & 63);
        }

        void clear(int index) {
            words[index >>> 6] &= ~(1L << (index & 63));
        }

        boolean get(int index) {
            return (words[index >>> 6] & (1L << (index & 63))) != 0;
        }

        ExtendedBitSet and(ExtendedBitSet o) {
            long[] result = new long[words.length];
            for (int i = 0; i < words.length; i++) result[i] = words[i] & o.words[i];
            return new ExtendedBitSet(result, numBits);
        }

        ExtendedBitSet or(ExtendedBitSet o) {
            long[] result = new long[words.length];
            for (int i = 0; i < words.length; i++) result[i] = words[i] | o.words[i];
            return new ExtendedBitSet(result, numBits);
        }

        ExtendedBitSet xor(ExtendedBitSet o) {
            long[] result = new long[words.length];
            for (int i = 0; i < words.length; i++) result[i] = words[i] ^ o.words[i];
            return new ExtendedBitSet(result, numBits);
        }

        ExtendedBitSet not() {
            long[] result = new long[words.length];
            for (int i = 0; i < words.length; i++) result[i] = ~words[i];
            // 末尾ワードの余剰ビットをゼロクリア
            int rem = numBits & 63;
            if (rem != 0) result[result.length - 1] &= (1L << rem) - 1;
            return new ExtendedBitSet(result, numBits);
        }

        // nビット左シフト (上位ビット方向)
        ExtendedBitSet shiftl(int n) {
            long[] result = new long[words.length];
            int wordShift = n >>> 6;
            int bitShift  = n & 63;
            for (int i = words.length - 1; i >= wordShift; i--) {
                result[i] = words[i - wordShift] << bitShift;
                if (bitShift != 0 && i - wordShift - 1 >= 0) {
                    result[i] |= words[i - wordShift - 1] >>> (64 - bitShift);
                }
            }
            // 末尾ワードの余剰ビットをゼロクリア
            int rem = numBits & 63;
            if (rem != 0) result[result.length - 1] &= (1L << rem) - 1;
            return new ExtendedBitSet(result, numBits);
        }

        // nビット右シフト (下位ビット方向)
        ExtendedBitSet shiftr(int n) {
            long[] result = new long[words.length];
            int wordShift = n >>> 6;
            int bitShift  = n & 63;
            for (int i = 0; i + wordShift < words.length; i++) {
                result[i] = words[i + wordShift] >>> bitShift;
                if (bitShift != 0 && i + wordShift + 1 < words.length) {
                    result[i] |= words[i + wordShift + 1] << (64 - bitShift);
                }
            }
            return new ExtendedBitSet(result, numBits);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("{");
            boolean first = true;
            for (int i = 0; i < numBits; i++) {
                if (get(i)) {
                    if (!first) sb.append(',');
                    sb.append(i);
                    first = false;
                }
            }
            return sb.append('}').toString();
        }
    }
}