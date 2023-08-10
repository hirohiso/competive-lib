package jp.hirohiso.competive.util.graph;

import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Dfs {

    public static void main(String[] args) {
        // TODO 自動生成されたメソッド・スタブ

    }

    //深さ優先探索（再帰版）
    private static Set<Node> visitedRecursive = new HashSet<>();
    public static void dfsRecursive(Node root){

        //ルートを探索済みにする
        visitedRecursive.add(root);
        //未訪問の隣接ノードを取得する
        //隣接ノードを取得
        List<Node> temp = root.getLinkedNodes();

        //未訪問の隣接ノードを取得
        List<Node> nextNodes = new LinkedList<>();
        for (Node n : temp) {
            if (!visitedRecursive.contains(n)) {
                //未訪問だけ積める
                nextNodes.add(n);
            }
        }

        //未訪問のノードを再帰探索する
        for(Node n : nextNodes){
            dfsRecursive(n);
        }
    }

    //深さ優先探索
    public static void dfs(Node root) {
        //探索済み
        Set<Node> visited = new HashSet<>();
        //スタック
        Deque<Node> stack = new LinkedList<>();

        //ルートを探索済みにして、スタックに積む
        visited.add(root);
        stack.addLast(root);

        Node target;
        while ((target = stack.pollLast()) != null) {
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
                //未訪問ノードを全てスタックに積む
                visited.add(n);
                stack.addLast(n);
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
