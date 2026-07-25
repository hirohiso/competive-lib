package jp.hirohiso.competive.util.math;

public class VectorMain {
    static void main() {

    }


    record Vector(long x, long y) {
        public Vector add(Vector other) {
            return new Vector(this.x + other.x, this.y + other.y);
        }

        public Vector inv() {
            return new Vector(-x, -y);
        }

        public long product(Vector other) {
            return this.x * other.x + this.y * other.y;
        }

        public double mag() {
            return Math.abs(x * x + y * y);
        }
    }
}
