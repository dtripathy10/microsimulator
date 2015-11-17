package edu.iitb.civil.tse.geometry;

import edu.iitb.civil.tse.gui.panel.opengl.Point3D;

public class Polygon {

    private final int N;            // number of points in the polygon
    private final Point3D[] points;   // the points, setting p[0] = p[N]

    // defensive copy
    public Polygon(Point3D[] points) {
        N = points.length;
        this.points = new Point3D[N + 1];
        System.arraycopy(points, 0, this.points, 0, N);
        this.points[N] = points[0];
    }

    // return size
    public int size() {
        return N;
    }

    // return area of polygon
    public double area() {
        return Math.abs(signedArea());
    }

    // return signed area of polygon
    public double signedArea() {
        double sum = 0.0;
        for (int i = 0; i < N; i++) {
            sum = sum + (points[i].x * points[i + 1].y - (points[i].y * points[i + 1].x));
        }
        return 0.5 * sum;
    }

    // are vertices in counterclockwise order?
    // assumes polygon does not intersect itself
    public boolean isCCW() {
        return signedArea() > 0;
    }

    // return the centroid of the polygon
    public Point3D centroid() {
        double cx = 0.0, cy = 0.0;
        for (int i = 0; i < N; i++) {
            cx = cx + (points[i].x + points[i + 1].x) * (points[i].y * points[i + 1].x - points[i].x * points[i + 1].y);
            cy = cy + (points[i].y + points[i + 1].y) * (points[i].y * points[i + 1].x - points[i].x * points[i + 1].y);
        }
        cx /= (6 * area());
        cy /= (6 * area());
        return new Point3D(cx, cy, 0);
    }

    // does this Polygon contain the point p?
    // if p is on boundary then 0 or 1 is returned, and p is in exactly one point of every partition of plane
    // Reference: http://exaflop.org/docs/cgafaq/cga2.html
    public boolean contains(Point3D p0) {
        int crossings = 0;
        for (int i = 0; i < N; i++) {
            double slope = (points[i + 1].x - points[i].x) / (points[i + 1].y - points[i].y);
            boolean cond1 = (points[i].y <= p0.y) && (p0.y < points[i + 1].y);
            boolean cond2 = (points[i + 1].y <= p0.y) && (p0.y < points[i].y);
            boolean cond3 = p0.x < slope * (p0.y - points[i].y) + points[i].x;
            if ((cond1 || cond2) && cond3) {
                crossings++;
            }
        }
        return (crossings % 2 != 0);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
    }
    

}
