package jp.hirohiso.competive.util.math;

public class ArgumentSort {
    public static void main(String[] args) {
        Point[] points = {
            new Point(1, 2),
            new Point(2, 1),
            new Point(3, 3),
            new Point(0, 4),
            new Point(4, 0)
        };

        // 偏角順にソート
        java.util.Arrays.sort(points);

        // 結果を表示
        for (Point p : points) {
            System.out.println(p);
        }
    }
}

record Point(int x, int y) implements Comparable<Point> {
    @Override
    public int compareTo(Point o) {
        //偏角順で比較
        return Integer.compare(y * o.x, x * o.y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
