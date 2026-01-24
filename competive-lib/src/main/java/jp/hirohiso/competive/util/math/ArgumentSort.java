package jp.hirohiso.competive.util.math;

public class ArgumentSort {
    public static void main(String[] args) {
        Point[] points = {
                new Point(-1, 0),
                new Point(-1, -1),
                new Point(0, -1),
                new Point(1, 1),
                new Point(-1, 1),
                new Point(1, -1),
                new Point(1, 0),
                new Point(0, 1),
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
        boolean h0 = (this.y < 0) || (this.y == 0 && this.x < 0); // (y0, x0) < (0, 0)
        boolean h1 = (o.y < 0) || (o.y == 0 && o.x < 0); // (y1, x1) < (0, 0)

        int c = Boolean.compare(h0, h1); // false < true
        if (c != 0) return c;
        //偏角順で比較
        return Integer.compare(y * o.x, x * o.y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
