package org.example.tasks.trees;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(System.out));

        String[] parts;

        parts = (reader.readLine()).trim().split("\\s+");
        int treesNumber = Integer.parseInt(parts[0]);
        int lengthSquare = Integer.parseInt(parts[1]);

        HashSet<Point> points = new HashSet<>(treesNumber);
        for (int i = 0; i < treesNumber; i++) {
            parts = (reader.readLine()).trim().split("\\s+");
            points.add(new Point(
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1])
            ));
        }
        reader.close();

        long pairsCount = 0;
        List<Point> availableDxDy = getAvailableDxDy(lengthSquare);
        for (Point point : points) {
            for (Point shift : availableDxDy) {
                int newX = point.getX() + shift.getX();
                int newY = point.getY() + shift.getY();

                Point pairCandidate = new Point(newX, newY);

                if (points.contains(pairCandidate)) {
                    // подсчёт точек только "с одной стороны", чтобы избежать дубликатов
                    if (newX > point.getX() || (newX == point.getX() && newY > point.getY())) {
                        pairsCount++;
                    }
                }
            }
        }

        writer.write(String.valueOf(pairsCount));
        writer.flush();
        writer.close();
    }

    static class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x && y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    static List<Point> getAvailableDxDy(int lengthSquare) {
        List<Point> result = new ArrayList<>();
        int limit = (int) Math.sqrt(lengthSquare);

        for (int dx = 0; dx <= limit; dx++) {
            int remainder = lengthSquare - dx * dx;
            if (remainder < 0) {
                continue;
            }

            int dy = (int) Math.sqrt(remainder);
            if (remainder == dy * dy) {
                result.add(new Point(dx, dy));
                if (dy != 0) {
                    result.add(new Point(dx, -dy));
                }
                if (dx != 0) {
                    result.add(new Point(-dx, dy));
                }
                if (dx != 0 && dy != 0) {
                    result.add(new Point(-dx, -dy));
                }
            }
        }
        return result;
    }
}