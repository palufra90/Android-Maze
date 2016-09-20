package com.example.francesca.labyrinth.util;

/**
 * Created by francesca on 19/01/2016.
 */
public class Geometry {
    public static class Point {
        public float x, y, z;
        public Point(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Point translateY(float distance) {
            return new Point(x, y + distance, z);
        }
    }

    public static class Circle {
        public final Point center;
        public final float radius;

        public Circle(Point center, float radius) {
            this.center = center;
            this.radius = radius;
        }
    }

    public static boolean intersects(Circle circle, Point ray) {
        return distanceBetween(circle.center, ray) < circle.radius;
    }

    public static float distanceBetween(Point point, Point ray) {
        Vector p1ToPoint = vectorBetween(ray, point);

        float distanceFromPointToRay = p1ToPoint.length();
        return distanceFromPointToRay;
    }

    public static Vector vectorBetween(Point from, Point to) {
        return new Vector(
                to.x - from.x,
                to.y - from.y,
                to.z - from.z);
    }

    public static class Vector  {
        public final float x, y, z;

        public Vector(float x, float y, float z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public float length() {
            return (float)Math.sqrt(
                    x * x + y * y + z * z);
        }
    }
}