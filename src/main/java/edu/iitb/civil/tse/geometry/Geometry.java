package edu.iitb.civil.tse.geometry;

import edu.iitb.civil.tse.gui.panel.opengl.Line;
import edu.iitb.civil.tse.gui.panel.opengl.Point3D;
import edu.iitb.civil.tse.vehicle.TurningMovement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Debabrata Tripathy
 */
public class Geometry {

    public static Point3D nextPointOnline(Point3D sourcePoint, double theta, double length) {
        theta = Math.toRadians(theta);
        double sintheta = Math.sin(theta);
        double costheta = Math.cos(theta);
        double xCoordinate = format1(sourcePoint.getX() + (length * costheta));
        double yCoordinate = format1(sourcePoint.getY() + (length * sintheta));
        return new Point3D(xCoordinate, yCoordinate, 0);
    }

    private static double format1(double value) {
        return (double) Math.round(value * 100000000) / 100000000;  //you can change this to round up the value(for two position use 100...)
    }

    private static double format(double value) {
        return (double) Math.round(value * 100000000) / 100000000; //you can change this to round up the value(for two position use 100...)
    }

    public static double distance(Point3D point1, Point3D point2) {
        double x1 = point1.getX();
        double y1 = point1.getY();
        double x2 = point2.getX();
        double y2 = point2.getY();
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public static void getMappingMatrix() {
        double r = 10.8, theta = 90;
        double xtemp, ytemp;
        double x, y;
        for (int i = 0; i < 14; i++) {
            theta = (90.0 / 14) * (i + 1);
            xtemp = r * Math.sin(Math.toRadians(theta));
            ytemp = r * Math.cos(Math.toRadians(theta));
            x = xtemp + 0;
            y = 10.8 - (r - ytemp);
            System.out.println("X = \t" + x + "\tY = \t" + y);
        }
    }

    public static double getAbsoluteAngel(Point3D source, Point3D destination) {
        double x1 = source.getX();
        double x2 = destination.getX();
        double y1 = source.getY();
        double y2 = destination.getY();
        double angel = Math.toDegrees(Math.atan((y2 - y1) / (x2 - x1)));
        if (angel == 0) {
            if ((x2 - x1) < 0) {
                angel = 180;
                return angel;
            } else {
                return angel;
            }
        }
        if (angel > 0) {
            if ((y2 - y1) < 0) {
                //belong to quad-II1
                angel = 180 + angel;
                return angel;
            } else {
                return angel;
            }
        }
        if (angel < 0) {
            if ((x2 - x1) < 0) {
                //belong to quad-II
                angel = 180 + angel;
                return angel;
            } else {
                //belong to quad-IV
                angel = 360 + angel;
                return angel;
            }
        }
        return 360;
    }

    public static List<Point3D> getLeftPoints(Point3D sourcePoint, Point3D destinationPoint, double length) {
        List<Point3D> list = new ArrayList<>();
        double angel = Geometry.getAbsoluteAngel(sourcePoint, destinationPoint);
        angel = Math.toRadians(angel);
        double xSOurce = sourcePoint.getX() - length * Math.sin(angel);
        double ySOurce = sourcePoint.getY() + length * Math.cos(angel);
        list.add(new Point3D(xSOurce, ySOurce, 0));
        double xDestination = destinationPoint.getX() - length * Math.sin(angel);
        double yDestination = destinationPoint.getY() + length * Math.cos(angel);
        list.add(new Point3D(xDestination, yDestination, 0));
        return list;
    }

    public static List<Point3D> getRightPoints(Point3D sourcePoint, Point3D destinationPoint, double length) {
        List<Point3D> list = new ArrayList<>();
        double angel = Geometry.getAbsoluteAngel(sourcePoint, destinationPoint);
        angel = Math.toRadians(angel);
        double xSOurce = sourcePoint.getX() + length * Math.sin(angel);
        double ySOurce = sourcePoint.getY() - length * Math.cos(angel);
        list.add(new Point3D(xSOurce, ySOurce, 0));
        double xDestination = destinationPoint.getX() + length * Math.sin(angel);
        double yDestination = destinationPoint.getY() - length * Math.cos(angel);
        list.add(new Point3D(xDestination, yDestination, 0));
        return list;
    }

    public static Point3D getLeftPointSTART(Point3D sourcePoint, Point3D destinationPoint, double length) {
        double angel = Geometry.getAbsoluteAngel(sourcePoint, destinationPoint);
        angel = Math.toRadians(angel);
        double xSOurce = sourcePoint.getX() - length * Math.sin(angel);
        double ySOurce = sourcePoint.getY() + length * Math.cos(angel);
        return new Point3D(xSOurce, ySOurce, 0);

    }

    public static Point3D getRightPointSTART(Point3D sourcePoint, Point3D destinationPoint, double length) {
        double angel = Geometry.getAbsoluteAngel(sourcePoint, destinationPoint);
        angel = Math.toRadians(angel);
        double xSOurce = sourcePoint.getX() + length * Math.sin(angel);
        double ySOurce = sourcePoint.getY() - length * Math.cos(angel);
        return new Point3D(xSOurce, ySOurce, 0);

    }

    public static Point3D getLeftPointBOTTOM(Point3D sourcePoint, Point3D destinationPoint, double length) {
        double angel = Geometry.getAbsoluteAngel(sourcePoint, destinationPoint);
        angel = Math.toRadians(angel);
        double xSOurce = destinationPoint.getX() - length * Math.sin(angel);
        double ySOurce = destinationPoint.getY() + length * Math.cos(angel);
        return new Point3D(xSOurce, ySOurce, 0);

    }

    public static Point3D getRightPointBOTTOM(Point3D sourcePoint, Point3D destinationPoint, double length) {
        double angel = Geometry.getAbsoluteAngel(sourcePoint, destinationPoint);
        angel = Math.toRadians(angel);
        double xSOurce = destinationPoint.getX() + length * Math.sin(angel);
        double ySOurce = destinationPoint.getY() - length * Math.cos(angel);
        return new Point3D(xSOurce, ySOurce, 0);

    }

    public static List<Point3D> getCircularMapping(Point3D pointOnCircle, Point3D center, TurningMovement movement, int noOfAngel) {
        List<Point3D> list = new ArrayList<>();
        list.add(pointOnCircle);
        double radius = Geometry.distance(pointOnCircle, center);
        double theta = 90 / noOfAngel;
        double alpha = Geometry.getAbsoluteAngel(center, pointOnCircle);
        double xtemp, ytemp, tempAlpha;
        double x = center.getX(), y = center.getY();
        switch (movement) {
            case RIGHT://right
                for (int i = 1; i <= noOfAngel; i++) {
                    tempAlpha = alpha - theta * i;
                    xtemp = radius * Math.cos(Math.toRadians(tempAlpha));
                    ytemp = radius * Math.sin(Math.toRadians(tempAlpha));
                    x = center.getX() + xtemp;
                    y = center.getY() + ytemp;
                    list.add(new Point3D(x, y, 0));
                }
                break;
            case LEFT://left
                for (int i = 1; i <= noOfAngel; i++) {
                    tempAlpha = alpha + theta * i;
                    xtemp = radius * Math.cos(Math.toRadians(tempAlpha));
                    ytemp = radius * Math.sin(Math.toRadians(tempAlpha));
                    x = center.getX() + xtemp;
                    y = center.getY() + ytemp;
                    list.add(new Point3D(x, y, 0));
                }
                break;
        }//case
        return list;
    }

    public static List<Point3D> getLinearMapping(Point3D sourcePoint, Point3D destination, int cell) {
        List<Point3D> list = new ArrayList<>();
        list.add(sourcePoint);
        double theta = Geometry.getAbsoluteAngel(sourcePoint, destination);
        for (int i = 1; i <= cell; i++) {
            Point3D temp = Geometry.nextPointOnline(sourcePoint, theta, 1.2 * i);
            list.add(temp);
        }
        return list;
    }

    public static int ccw(Point3D a, Point3D b, Point3D c) {
        double area2 = (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
        if (area2 == 0) { //a,b,c are collinear
            return 0;
        } else if (area2 > 0) { //a,b,c are counter clock wise
            return +1;
        } else if (area2 < 0) { //a,b,c are clock wise
            return -1;
        }
        return Integer.MAX_VALUE;
    }

    public static boolean collinear(Point3D a, Point3D b, Point3D c) {
        return ccw(a, b, c) == 0;
    }

    public static boolean intersect(Line l1, Line l2) {
        int test1, test2;
        test1 = Geometry.ccw(l1.startPoint, l1.endPoint, l2.startPoint)
                * Geometry.ccw(l1.startPoint, l1.endPoint, l2.endPoint);
        test2 = Geometry.ccw(l2.startPoint, l2.endPoint, l1.startPoint)
                * Geometry.ccw(l2.startPoint, l2.endPoint, l1.endPoint);
        return (test1 <= 0) && (test2 <= 0);
    }

    public static boolean isPointPresentOnLineSegment(Point3D a, Point3D b, Point3D c) {
        double distance1 = format(Geometry.distance(a, b));
        double distance2 = format(Geometry.distance(c, b));
        double disance3 = format(Geometry.distance(a, c));
        return disance3 + distance2 == distance1;
    }
}
