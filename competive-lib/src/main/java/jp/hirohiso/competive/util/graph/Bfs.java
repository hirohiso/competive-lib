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

        //ルートを探索済みにして、キューに積む
        visited.add(root);
        queue.addLast(root);

        Node target;
        while ((target = queue.pollFirst()) != null) {
            //隣接ノードを取得
            List<Node> temp = target.getLinkedNodes();

            //未訪問の隣接ノードを取得
            List<Node> nextNodes = new LinkedList<>();
            for (Node n : temp) {
                if (!visited.contains(n)) {
                    //未訪問だけ積める
                    nextNodes.add(n);
                }
            }
            //nextNodesが空の場合は、葉


            for (Node n : nextNodes) {
                //未訪問ノードを全てキューに積む
                visited.add(n);
                queue.addLast(n);
                //のちのち構築した木に親子の関係をしたい場合
                //ここで子に対して、親の参照を貼っとく
            }
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
