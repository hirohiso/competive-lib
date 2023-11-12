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

        if (visitedRecursive.contains(root)) {
            return;
        }
        //ルートを探索済みにする
        visitedRecursive.add(root);
        root.getLinkedNodes()
                .stream()
                .filter(n -> !visitedRecursive.contains(n))
                .forEach(n -> dfsRecursive(n));
    }

    //深さ優先探索
    public static void dfs(Node root) {
        //探索済み
        Set<Node> visited = new HashSet<>();
        //スタック
        Deque<Node> stack = new LinkedList<>();
        //帰りがけ処理済みノード
        //Set<Node> postNodes = new HashSet<>();

        //ルートをスタックに積む
        stack.addLast(root);

        Node target;
        while ((target = stack.peekLast()) != null) {
            if(visited.contains(target)){
                stack.pollLast();
                //帰りがけ処理
                //!postNodes.contains(target)
                //postNodes.add(target);
                continue;
            }
            visited.add(target);
            //隣接ノードのうち未訪問以外をスタックにつめる
            target.getLinkedNodes().stream()
                    .filter(n ->!visited.contains(n))
                    .forEach(
                            n->{
                                stack.addLast(n);
                            }
                    );
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
