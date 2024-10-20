package jp.hirohiso.competive.util.graph;

import java.util.*;

public class Bfs {

    public static void main(String[] args) {

    }
    record BfsSolver(boolean[] visited) {
        public int[] bfs(int now, ArrayList<LinkedList<Integer>> list) {
            var ret = new int[list.size()];
            Arrays.fill(ret,-1);

            var q = new LinkedList<Integer>();
            q.addLast(now);
            ret[now] = 0;
            while (!q.isEmpty()){
                var n = q.pollFirst();
                for(var next : list.get(n)){
                    if(visited[next]){
                        continue;
                    }
                    visited[next] = true;
                    ret[next] += ret[n] + 1;//levelを保持
                    q.addLast(next);
                }
            }
            return ret;
        }
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
