package edu.iitb.civil.tse.network;

import edu.iitb.civil.tse.geometry.Geometry;
import edu.iitb.civil.tse.gui.panel.opengl.Point3D;
import edu.iitb.civil.tse.vehicle.Vehicle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Debabrata Tripathy
 */
public class Unsignalised extends Node {

    public static double CELLSIZE = 1.2;
    public static int MAXSPEED_TURNING;
    public static int MAXSPEED_STRAIGHT;
    public MappingCell[][] cells;
    public Point3D source;
    public Point3D destination;
    public double length;
    public List<Vehicle> vehicles = this.vehicles = Collections.synchronizedList(new ArrayList<Vehicle>());
    public List<LinkConnectivity> linkConnecivty = new ArrayList<>();
    public int CELLS;
    public Point3D l1, l2, l3, l4;

    public Unsignalised(String id, double xCoordiante, double yCoordiante, double length) {
        this.id = id;
        this.xCordinate = xCoordiante;
        this.yCoordianate = yCoordiante;
        this.nodeType = NodeType.UNSIGNALISED;
        CELLS = (int) ((int) (length) / 1.2);
        cells = new MappingCell[12][12];
    }

    public Unsignalised(String id, Point3D p) {
        this.id = id;
        this.xCordinate = p.getX();
        this.yCoordianate = p.getY();
        this.zCoordianate = p.getZ();
        this.nodeType = NodeType.GENERATOR;
        this.nodeType = NodeType.UNSIGNALISED;
        CELLS = (int) ((int) (length / 2) / 1.2);
        cells = new MappingCell[12][12];
    }

    void createBaseMatrix() {
        for (LinkConnectivity lc : linkConnecivty) {
            switch (lc.turningMovement) {
                case STRAIGHT:
                    double theta = Geometry.getAbsoluteAngel(lc.inLink.getSourcePoint(), lc.inLink.getDestinaPoint());
                    if ((theta <= 120) || (theta >= 240)) {
                        source = lc.inLink.getDestinaPoint();
                    }
                    break;
            }
        }
        double theta = Geometry.getAbsoluteAngel(source, getPoint());
        destination = Geometry.nextPointOnline(source, theta, 1.2 * 12);
        List<Point3D> f = Geometry.getLeftPoints(source, destination, 6 * 1.2);
        source = f.get(0);
        destination = f.get(1);
        setLEFTCoordinate();
    }

    public void setLEFTCoordinate() {
        this.length = Geometry.distance(source, destination);
        this.CELLS = (int) Math.round(length / 1.2);
        //mapping code

        List<List<Point3D>> result = new ArrayList<>();
        for (int i = 0; i <= 12; i++) {
            List<Point3D> list;
            List<Point3D> tempList;
            if (i == 0) {
                list = Geometry.getLinearMapping(source, destination, CELLS);
            } else {
                tempList = Geometry.getRightPoints(source, destination, i * 1.2);
                list = Geometry.getLinearMapping(tempList.get(0), tempList.get(1), CELLS);
            }
            result.add(list);
        }
        for (int i = 0; i < result.size() - 1; i++) {
            List<Point3D> temp = result.get(i);
            List<Point3D> temp1 = result.get(i + 1);
            for (int j = 0; j < temp.size() - 1; j++) {
                MappingCell mc = new MappingCell();
                mc.topLeft = temp.get(j);
                mc.topRight = temp.get(j + 1);
                mc.bottomLeft = temp1.get(j);
                mc.bottomRight = temp1.get(j + 1);
                mc.rowIndex = i;
                mc.columnIndex = j;
                cells[i][j] = mc;
            }
        }
        l1 = cells[0][0].topLeft;
        l2 = cells[0][11].topRight;
        l3 = cells[11][11].topLeft;
        l4 = cells[11][0].topRight;

    }
}
