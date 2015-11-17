package edu.iitb.civil.tse.network;

import edu.iitb.civil.tse.gui.panel.opengl.Point3D;

/**
 *
 * @author Debabrata Tripathy
 */
public abstract class Node {

    public String id;
    public double xCordinate;
    public double yCoordianate;
    public double zCoordianate;
    public NodeType nodeType;
    public String getId() {
        return id;
    }
    public Point3D getPoint() {
        Point3D point3D = new Point3D(xCordinate, yCoordianate, zCoordianate);
        return point3D;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }

}
