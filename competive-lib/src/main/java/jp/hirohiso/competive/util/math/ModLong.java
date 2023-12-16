package jp.hirohiso.competive.util.math;

public class ModLong {

    public static void main(String[] args) {
        LongMod longMod = new LongMod(10444, (long)1e9 + 7);
        System.out.println(longMod.plus(1234).multi(1234).plus(-12344).multi(123434567).v());
    }

    public static class LongMod{
        private long result;
        private final long  M;

        public LongMod(long init, long M){
            this.result = init;
            this.M = M;
        }

        public LongMod plus(long t){
            result += t;
            result %= M;
            return this;
        }

        public LongMod multi(long t){
            result *= t;
            result %= M;
            return this;
        }
        public long v(){
            return this.result;
        }
    }
}
