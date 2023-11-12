package jp.hirohiso.competive.util.examples;

import java.util.Comparator;
import java.util.PriorityQueue;

public class Pq {
    public static void main(String[] args) {
        PriorityQueue<Long> queue1 = new PriorityQueue<>();
        Comparator<Pair> comparator = Comparator.<Pair>comparingInt( p -> p.t1).thenComparingInt(p->p.t2).reversed();
        PriorityQueue<Pair> queue2 = new PriorityQueue<>(comparator);

        queue1.add(5L);
        queue1.add(2L);
        queue1.add(7L);

        queue2.add( new Pair(1,1));
        queue2.add( new Pair(0,1));
        queue2.add( new Pair(2,1));
        queue2.add( new Pair(1,3));
        queue2.add( new Pair(1,0));

        System.out.println(queue1);
        Pair p = null;
        while ((p = queue2.poll()) != null){
            System.out.println(p);
        }
    }

    public record Pair(int t1,int t2){
    }
}
