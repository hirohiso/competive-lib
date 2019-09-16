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

    //深さ優先探索
    public static void dfs(Node root){
        //探索済み
        Set<Node> visited = new HashSet<>();
        //スタック
        Deque<Node> stack = new LinkedList<>();

        //ルートを探索済みにして、スタックに積む
        visited.add(root);
        stack.addLast(root);

        Node target;
        while((target = stack.pollLast()) != null){
            List<Node> nextNodes = target.getLinkedNodes();
            for(Node n :nextNodes){
                if(!visited.contains(n)){
                    //訪問済みでない
                    visited.add(n);
                    stack.addLast(n);
                }
            }
        }

    }
    public static class Node{
        private List<Node> linkedNode = new LinkedList<>();

        public List<Node> getLinkedNodes(){
            return linkedNode;
        }

    }

}
