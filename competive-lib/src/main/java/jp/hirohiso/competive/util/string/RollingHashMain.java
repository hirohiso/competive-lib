package jp.hirohiso.competive.util.string;

import java.util.Iterator;

public class RollingHashMain {

    public static void main(String[] args) {
        var rh = new RollingHash();
        var hash = rh.get("abc");
        for (long l : rh.iterator("abcdefabcgdegrwetjrsabc", 3)) {
            System.out.println(l);
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
