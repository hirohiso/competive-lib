package jp.hirohiso.competive.util.sequence;

import java.io.PrintWriter;

public class SimpleListMain {
    public static void main(String[] args) {

    }

    private class NodeList{
        SimpleNode head;
        SimpleNode tail;

        NodeList(SimpleNode head) {
            this.head = head;
            this.tail = head;
        }

        void add(NodeList other) {
            if (other.head == null) {
                return;
            }
            if (this.head == null) {
                this.head = other.head;
                this.tail = other.tail;
                return;
            }
            this.tail.next = other.head;
            this.tail = other.tail;
        }

        void print(PrintWriter pw) {
            var current = head;
            while (current != null) {
                pw.print(current.value + " ");
                current = current.next;
            }
            pw.println();
        }
    }
    private class SimpleNode {
        long value;
        SimpleNode next;

        SimpleNode(long value) {
            this.value = value;
        }
    }
}
