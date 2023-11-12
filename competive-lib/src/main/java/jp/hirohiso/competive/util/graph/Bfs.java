package jp.hirohiso.competive.util.graph;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Bfs {

    public static void main(String[] args) {

    }
    //幅優先探索
    public static void dfs(Node root) {
        //探索済み
        Set<Node> visited = new HashSet<>();
        //キュー
        Deque<Node> queue = new LinkedList<>();

        //Deque<Node> tsort = new LinkedList<>();
        //ルートを探索済みにして、キューに積む
        visited.add(root);
        queue.addLast(root);

        Node target;
        while ((target = queue.pollFirst()) != null) {
            //tsort.addLast(target);
            target.getLinkedNodes().stream()
                    .filter(n->!visited.contains(n))
                    .forEach(n -> {
                        visited.add(n);
                        queue.addLast(n);
                    });
        }
    }

    public static class Node {
        //隣接ノード
        private List<Node> linkedNode = new LinkedList<>();

        public void addLinkedNode(Node e) {
            linkedNode.add(e);
        }

        public List<Node> getLinkedNodes() {
            return linkedNode;
        }

    }
}
