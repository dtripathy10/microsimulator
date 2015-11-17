package edu.iitb.civil.tse.gui.panel.opengl;

public class Point3D implements Comparable<Point3D> {

    public double x;
    public double y;
    public double z;

    public Point3D(double valueX, double valueY, double valueZ) {
        x = valueX;
        y = valueY;
        z = valueZ;
    }

    public Point3D() {
        x = 0;
        y = 0;
        z = 0;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "Point3D{" + "x=" + x + ", y=" + y + ", z=" + z + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Point3D other = (Point3D) obj;
        if (Math.round(this.x) != Math.round(other.x)) {
            return false;
        }
        if (Math.round(this.y) !=Math.round(other.y)) {
            return false;
        }
        if (Math.round(this.z) != Math.round(other.z)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Point3D o) {;
        double x = this.x;
        double xx = o.x;
        double y = this.y;
        double yy = o.y;

        if (x > xx) {
            return 1;
        }else if(x < xx) {
            return -1;
        }else {
            return 0;
        }
    }
}
