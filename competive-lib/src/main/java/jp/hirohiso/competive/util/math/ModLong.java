package jp.hirohiso.competive.util.math;

public class ModLong {

    public static void main(String[] args) {
        LongMod longMod = new LongMod(10444, (long)1e9 + 7);
        System.out.println(longMod.t(1234).x(1234).t(-12344).x(123434567).v());
    }

    public static class LongMod{
        private long result;
        private final long  M;

        public LongMod(long init, long M){
            this.result = init;
            this.M = M;
        }

        public LongMod t(long t){
            result += t;
            result %= M;
            return this;
        }

        public LongMod x(long t){
            result *= t;
            result %= M;
            return this;
        }
        public long v(){
            return this.result;
        }
    }
}
