package jp.hirohiso.competive.util.graph;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

public class Dijkstra {

    public static void main(String[] args) {
        Node node1 = new Node();
        Node node2 = new Node();
        Node node3 = new Node();
        Node node4 = new Node();
        node1.addLinkedNode(node2, 5);
        node1.addLinkedNode(node3, 2);
        node2.addLinkedNode(node4, 1);
        node3.addLinkedNode(node4, 8);
        dijkstra(node1);
        System.out.println(_distans.toString());

    }
    //ダイクストラ法
    public static Map<Node,Integer> _distans = new HashMap<>();
    public static void dijkstra(Node root) {
        _distans.put(root, 0);
        Comparator<DistansNodeSet> comp = (DistansNodeSet e1,DistansNodeSet e2)->e1.distans - e2.distans;
        PriorityQueue<DistansNodeSet> pq = new PriorityQueue<>(comp);

        pq.add(new DistansNodeSet(0, root));
        while(!pq.isEmpty()){
            DistansNodeSet pair = pq.poll();
            Node n = pair.getNode();

            if(_distans.getOrDefault(n, Integer.MAX_VALUE) < pair.getDistans()){
                continue;
            }
            for(Entry<Node, Integer> entry:n.getLinkedNodes().entrySet()){
                Node node = entry.getKey();
                int cost = entry.getValue();
                int newCost = _distans.get(n)+cost;
                if(_distans.getOrDefault(node, Integer.MAX_VALUE) > newCost){
                    _distans.put(node, newCost);
                    pq.add(new DistansNodeSet(newCost, node));
                }
            }
        }


    }
    public static class DistansNodeSet{
        private int distans;
        private Node node;

        public DistansNodeSet(int d,Node n){
            distans = d;
            node = n;
        }
        public int getDistans() {
            return distans;
        }
        public Node getNode() {
            return node;
        }
    }
    public static class Node {
        //隣接ノード
        private Map<Node,Integer> linkedEdge = new HashMap<>();


        public void addLinkedNode(Node e,int cost) {
            linkedEdge.put(e, cost);
        }

        public Map<Node,Integer> getLinkedNodes() {
            return linkedEdge;
        }



    }
}
