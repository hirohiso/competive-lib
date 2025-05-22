package jp.hirohiso.competive.util.math;

public class EulerPhiMain {
    public static void main(String[] args) {

    }

    private long euler_phi(long n){
        var ret = n;
        for (long i = 2; i * i <= n; i++) {
            if (n % i == 0) {
                ret -= ret / i;
                while (n % i == 0) {
                    n /= i;
                }
            }
        }
        if(n > 1){
            ret -= ret / n;
        }
        return ret;
    }
}
