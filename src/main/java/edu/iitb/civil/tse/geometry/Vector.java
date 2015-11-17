/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.iitb.civil.tse.geometry;

import edu.iitb.civil.tse.gui.panel.opengl.Point3D;

/**
 *
 * @author Debabrata Tripathy
 */
public class Vector {

    public double x;
    public double y;

    public Vector(Point3D sourcePoint, Point3D destinationPoint) {
        y = destinationPoint.getY() - sourcePoint.getY();
        x = destinationPoint.getX() - sourcePoint.getX();
    }

    public static double angleBetweenTwoVector(Vector a, Vector b) {
        double xComponent = a.x + b.x;
        double yComponent = a.y + b.y;
        double adotb = xComponent + yComponent;

        double modea = Math.sqrt(a.x*a.x + a.y*a.y);
        double modeb = Math.sqrt(b.x*b.x + b.y*b.y);

        double costheta = adotb / (modea * modeb);
        double theta = Math.toDegrees(Math.acos(costheta));
        if(adotb < 0) {
            theta = 180 + theta;
        }
        if(adotb>0) {
            theta = 180 - theta;
        }
        return theta;
    }
}
