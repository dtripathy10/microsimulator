package edu.iitb.civil.tse.network;

import edu.iitb.civil.tse.geometry.Geometry;
import edu.iitb.civil.tse.geometry.Polygon;
import edu.iitb.civil.tse.gui.panel.opengl.Point3D;
import edu.iitb.civil.tse.vehicle.TurningMovement;
import edu.iitb.civil.tse.vehicle.Vehicle;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VirtualLink {

    private int MAXSPEED = 5;
    private double PNOISE;
    public static double CELLSIZE = 1.2;
    public String id;
    public double length;
    public List<Vehicle> vehicles;
    public MappingCell[][] mappingCells;
    public Vehicle[][] cells;
    public int noOfStrip;
    public int CELLS;
    public int noOfLane = 2;
    public double speed;
    public Link inLink;
    public Link outLink;
    public Unsignalised unsignalisedNode;
    public TurningMovement turningMovement;
    Point3D sourcePoint;
    Point3D destinationPoint;
    Point3D centerPoint;
    public static int maxspeed_turning = 3;
    private FileWriter fileWritter;
    private PrintWriter out;
    private int maxspeed_straight = 5;
    private int lane;
    private Random random = new Random();

    public VirtualLink(Link inLink, Link outLink, Unsignalised unsignalisedNode, TurningMovement turningMovement) {
        this.inLink = inLink;
        this.outLink = outLink;
        this.unsignalisedNode = unsignalisedNode;
        this.turningMovement = turningMovement;
        vehicles = new ArrayList<>();
        //this.length = Geometry.distance(inLink.getDestination().getPoint(), outLink.getSource().getPoint());
        this.vehicles = new ArrayList<>();
    }

    public void setCoordinate(Point3D sourcePoint, Point3D destinationPoint) {
        this.sourcePoint = sourcePoint;
        this.destinationPoint = destinationPoint;
        this.length = Geometry.distance(this.sourcePoint, this.destinationPoint);
        this.CELLS = (int) Math.round(length / 1.2);
        this.mappingCells = new MappingCell[3 * noOfLane][CELLS];
        this.cells = new Vehicle[3 * noOfLane][CELLS];
        //mapping code
        List<List<Point3D>> result = new ArrayList<>();
        for (int i = 3 * noOfLane; i >= 0; i--) {
            List<Point3D> list;
            List<Point3D> tempList;
            if (i == 0) {
                list = Geometry.getLinearMapping(sourcePoint, destinationPoint, CELLS);
            } else {
                tempList = Geometry.getLeftPoints(sourcePoint, destinationPoint, i * 1.2);
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
                mappingCells[i][j] = mc;
            }
        }

    }

    public void setSourcePoint(Point3D sourcePoint) {
        this.sourcePoint = sourcePoint;
    }

    public void setDestinationPoint(Point3D destinationPoint) {
        this.destinationPoint = destinationPoint;
    }

    public void setCenterPoint(Point3D centerPoint) {
        this.centerPoint = centerPoint;
    }

    public Point3D getSourcePoint() {
        return sourcePoint;
    }

    public Point3D getDestinationPoint() {
        return destinationPoint;
    }

    public Point3D getCenterPoint() {
        return centerPoint;
    }

    @Override
    public String toString() {
        return "VirtualLink{" + "id=\t" + id + ", inLink id=\t" + inLink.id + ", outLink id=\t" + outLink.id + ", unsignalisedNode=\t" + unsignalisedNode.id + '}';
    }

    void setCoordinate1(Point3D startPoint, Point3D centerPoint) {
        this.sourcePoint = startPoint;
        this.centerPoint = centerPoint;
        double radius = Geometry.distance(sourcePoint, centerPoint);
        double arcLength = Math.toRadians(90) * radius;
        double noOfAngel = Math.floor(arcLength / 1.2);
        this.CELLS = (int) Math.round(noOfAngel);
        this.mappingCells = new MappingCell[3][CELLS];
        this.cells = new Vehicle[3][CELLS];
        //mapping code
        List<List<Point3D>> result = new ArrayList<>();
        for (int i = 0; i <= 3; i++) {
            List<Point3D> list;
            Point3D temp;
            switch (turningMovement) {
                case LEFT:
                    if (i == 0) {
                        list = Geometry.getCircularMapping(sourcePoint, centerPoint, turningMovement, CELLS);
                    } else {
                        temp = Geometry.getLeftPointBOTTOM(inLink.getSourcePoint(), sourcePoint, i * 1.2);
                        list = Geometry.getCircularMapping(temp, centerPoint, turningMovement, CELLS);
                    }
                    result.add(list);
                    break;
                case RIGHT:
                    if (i == 0) {
                        list = Geometry.getCircularMapping(sourcePoint, centerPoint, turningMovement, CELLS);
                    } else {
                        temp = Geometry.getRightPointBOTTOM(inLink.getSourcePoint(), sourcePoint, i * 1.2);
                        list = Geometry.getCircularMapping(temp, centerPoint, turningMovement, CELLS);
                    }
                    result.add(list);
                    break;
            }

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
                mappingCells[i][j] = mc;
            }
        }
    }

    void doMapping() {
        switch (turningMovement) {
            case LEFT:
                for (int i = 0; i < mappingCells.length; i++) {
                    for (int j = 0; j < mappingCells[i].length; j++) {
                        checkIngrid1(mappingCells[i][j]);
                    }
                }
                break;
            case RIGHT:
                for (int i = 0; i < mappingCells.length; i++) {
                    for (int j = 0; j < mappingCells[i].length; j++) {
                        checkIngrid1(mappingCells[i][j]);
                    }
                }
                break;
            case STRAIGHT:
                for (int i = 0; i < mappingCells.length; i++) {
                    for (int j = 0; j < mappingCells[i].length; j++) {
                        Point3D[] points = {mappingCells[i][j].topLeft, mappingCells[i][j].topRight, mappingCells[i][j].bottomRight, mappingCells[i][j].bottomLeft};
                        checkIngrid(points, mappingCells[i][j]);
                    }
                }
                break;
        }
    }

    private void checkIngrid(Point3D[] points, MappingCell mc) {
        for (int i = 0; i < unsignalisedNode.cells.length; i++) {
            for (int j = 0; j < unsignalisedNode.cells[i].length; j++) {
                Point3D[] basePoint = {unsignalisedNode.cells[i][j].topLeft, unsignalisedNode.cells[i][j].topRight, unsignalisedNode.cells[i][j].bottomRight, unsignalisedNode.cells[i][j].bottomLeft};
                if (equalMM(points, basePoint)) {
                    mc.parentCell.add(unsignalisedNode.cells[i][j]);
                }
            }
        }
    }

    private boolean equalMM(Point3D[] points, Point3D[] basePoint) {
        List list = Arrays.asList(basePoint);
        for (Point3D point3D : points) {
            if (!list.contains(point3D)) {
                return false;

            }
        }
        return true;
    }

    private void checkIngrid1(MappingCell mappingCell) {
        for (int i = 0; i < unsignalisedNode.cells.length; i++) {
            for (int j = 0; j < unsignalisedNode.cells[i].length; j++) {
                Point3D[] basePoint = {unsignalisedNode.cells[i][j].topLeft, unsignalisedNode.cells[i][j].topRight, unsignalisedNode.cells[i][j].bottomRight, unsignalisedNode.cells[i][j].bottomLeft};
                Polygon p = new Polygon(basePoint);
                if (p.contains(mappingCell.topLeft)) {
                    mappingCell.parentCell.add(unsignalisedNode.cells[i][j]);
                } else if (p.contains(mappingCell.topRight)) {
                    mappingCell.parentCell.add(unsignalisedNode.cells[i][j]);
                } else if (p.contains(mappingCell.bottomLeft)) {
                    mappingCell.parentCell.add(unsignalisedNode.cells[i][j]);
                } else if (p.contains(mappingCell.bottomRight)) {
                    mappingCell.parentCell.add(unsignalisedNode.cells[i][j]);
                } else if (Geometry.isPointPresentOnLineSegment(unsignalisedNode.cells[i][j].topLeft, unsignalisedNode.cells[i][j].topRight, mappingCell.topLeft)) {
                    mappingCell.parentCell.add(unsignalisedNode.cells[i][j]);
                } else if (Geometry.isPointPresentOnLineSegment(unsignalisedNode.cells[i][j].topLeft, unsignalisedNode.cells[i][j].topRight, mappingCell.topRight)) {
                    mappingCell.parentCell.add(unsignalisedNode.cells[i][j]);
                } else if (Geometry.isPointPresentOnLineSegment(unsignalisedNode.cells[i][j].topLeft, unsignalisedNode.cells[i][j].topRight, mappingCell.bottomLeft)) {
                    mappingCell.parentCell.add(unsignalisedNode.cells[i][j]);
                } else if (Geometry.isPointPresentOnLineSegment(unsignalisedNode.cells[i][j].topLeft, unsignalisedNode.cells[i][j].topRight, mappingCell.bottomRight)) {
                    mappingCell.parentCell.add(unsignalisedNode.cells[i][j]);
                } else if (Geometry.isPointPresentOnLineSegment(unsignalisedNode.cells[i][j].topLeft, unsignalisedNode.cells[i][j].bottomLeft, mappingCell.topLeft)) {
                    mappingCell.parentCell.add(unsignalisedNode.cells[i][j]);
                } else if (Geometry.isPointPresentOnLineSegment(unsignalisedNode.cells[i][j].topLeft, unsignalisedNode.cells[i][j].bottomLeft, mappingCell.topRight)) {
                    mappingCell.parentCell.add(unsignalisedNode.cells[i][j]);
                } else if (Geometry.isPointPresentOnLineSegment(unsignalisedNode.cells[i][j].topLeft, unsignalisedNode.cells[i][j].bottomLeft, mappingCell.bottomLeft)) {
                    mappingCell.parentCell.add(unsignalisedNode.cells[i][j]);
                } else if (Geometry.isPointPresentOnLineSegment(unsignalisedNode.cells[i][j].topLeft, unsignalisedNode.cells[i][j].bottomLeft, mappingCell.bottomRight)) {
                    mappingCell.parentCell.add(unsignalisedNode.cells[i][j]);
                } else if (Geometry.isPointPresentOnLineSegment(unsignalisedNode.cells[i][j].topRight, unsignalisedNode.cells[i][j].bottomRight, mappingCell.topLeft)) {
                    mappingCell.parentCell.add(unsignalisedNode.cells[i][j]);
                } else if (Geometry.isPointPresentOnLineSegment(unsignalisedNode.cells[i][j].topRight, unsignalisedNode.cells[i][j].bottomRight, mappingCell.topRight)) {
                    mappingCell.parentCell.add(unsignalisedNode.cells[i][j]);
                } else if (Geometry.isPointPresentOnLineSegment(unsignalisedNode.cells[i][j].topRight, unsignalisedNode.cells[i][j].bottomRight, mappingCell.bottomLeft)) {
                    mappingCell.parentCell.add(unsignalisedNode.cells[i][j]);
                } else if (Geometry.isPointPresentOnLineSegment(unsignalisedNode.cells[i][j].topRight, unsignalisedNode.cells[i][j].bottomRight, mappingCell.bottomRight)) {
                    mappingCell.parentCell.add(unsignalisedNode.cells[i][j]);
                } else if (Geometry.isPointPresentOnLineSegment(unsignalisedNode.cells[i][j].bottomLeft, unsignalisedNode.cells[i][j].bottomRight, mappingCell.topLeft)) {
                    mappingCell.parentCell.add(unsignalisedNode.cells[i][j]);
                } else if (Geometry.isPointPresentOnLineSegment(unsignalisedNode.cells[i][j].bottomLeft, unsignalisedNode.cells[i][j].bottomRight, mappingCell.topRight)) {
                    mappingCell.parentCell.add(unsignalisedNode.cells[i][j]);
                } else if (Geometry.isPointPresentOnLineSegment(unsignalisedNode.cells[i][j].bottomLeft, unsignalisedNode.cells[i][j].bottomRight, mappingCell.bottomLeft)) {
                    mappingCell.parentCell.add(unsignalisedNode.cells[i][j]);
                } else if (Geometry.isPointPresentOnLineSegment(unsignalisedNode.cells[i][j].bottomLeft, unsignalisedNode.cells[i][j].bottomRight, mappingCell.bottomRight)) {
                    mappingCell.parentCell.add(unsignalisedNode.cells[i][j]);
                }
            }
        }
    }

    public void update(int timestemp) {
        for (int i = 0; i <= vehicles.size() - 1; i++) {
            Vehicle vehicle = vehicles.get(i);
            if (vehicle.updated == timestemp) {
                vehicle.updated = 0;
                continue;
            }
            int gap;
            if ((this.CELLS - 5 < vehicle.offset)) {
                switch (vehicle.turningMovement) {
                    case LEFT:
                        gap = calculateGapAtIntersection(vehicle);
                        vehicle.velocity = calucLateVelocity(vehicle, maxspeed_turning, gap);
                        freeCell(vehicle);
                        if (vehicle.offset > ((vehicle.offset + vehicle.velocity) % (CELLS - 1))) {
                            //it passes the vlink and add into  link
                            vehicle.offset = (vehicle.offset + vehicle.velocity) % (CELLS - 1);
                            vehicles.remove(vehicle);
                            i--;
                            addLink(vehicle);
                            vehicle.previousVisitedLink = this;
                            vehicle.updated = timestemp;
                            vehicle.previousStrip = vehicle.stripNo;
                            vehicle.stripNo = getChangingStripNumber(vehicle);
                            outLink.updateCell(vehicle);
                        } else {
                            //do the noral procesure
                            vehicle.offset = vehicle.offset + vehicle.velocity;
                            updateCell(vehicle);
                        }
                        break;
                    case RIGHT:
                        gap = calculateGapAtIntersection(vehicle);
                        vehicle.velocity = calucLateVelocity(vehicle, maxspeed_turning, gap);
                        freeCell(vehicle);
                        if (vehicle.offset > ((vehicle.offset + vehicle.velocity) % (CELLS - 1))) {
                            //it passes the vlink and add into  link
                            vehicle.offset = (vehicle.offset + vehicle.velocity) % (CELLS - 1);
                            vehicles.remove(vehicle);
                            i--;
                            addLink(vehicle);
                            vehicle.previousVisitedLink = this;
                            vehicle.updated = timestemp;
                            vehicle.previousStrip = vehicle.stripNo;
                            vehicle.stripNo = getChangingStripNumber(vehicle);
                            outLink.updateCell(vehicle);

                        } else {
                            //do the noral procesure
                            vehicle.offset = vehicle.offset + vehicle.velocity;
                            updateCell(vehicle);
                        }
                        break;
                    case STRAIGHT:
                        gap = calculateGapAtIntersection(vehicle);
                        vehicle.velocity = calucLateVelocity(vehicle, maxspeed_straight, gap);
                        freeCell(vehicle);
                        if (vehicle.offset > ((vehicle.offset + vehicle.velocity) % (CELLS - 1))) {
                            //it passes the vlink and add into  link
                            vehicle.offset = (vehicle.offset + vehicle.velocity) % (CELLS - 1);
                            vehicles.remove(vehicle);
                            i--;
                            addLink(vehicle);
                            vehicle.previousVisitedLink = this;
                            vehicle.updated = timestemp;
                            vehicle.previousStrip = vehicle.stripNo;
                            vehicle.stripNo = getChangingStripNumber(vehicle);
                            outLink.updateCell(vehicle);
                        } else {
                            //do the noral procesure
                            vehicle.offset = vehicle.offset + vehicle.velocity;
                            updateCell(vehicle);
                        }
                        break;
                }
            } else {
                gap = calculateGap(vehicle);
                vehicle.velocity = calucLateVelocity(vehicle, maxspeed_straight, gap);
                freeCell(vehicle);
                vehicle.offset = vehicle.offset + vehicle.velocity;
                updateCell(vehicle);
            }
        }
    }

    private int calculateGap(Vehicle vehicle) {
        int gap = 0;
        switch (vehicle.vehicleType) {
            case BIKE:
                for (int j = vehicle.offset + 1, k = vehicle.stripNo; j <= vehicle.offset + MAXSPEED && (j <= CELLS - 1); j++) {
                    if ((cells[k][j] == null)) {
                        gap++;
                    } else {
                        break;
                    }
                }
                return gap;
            case AUTO:
                for (int j = vehicle.offset + 1, k = vehicle.stripNo; j <= vehicle.offset + MAXSPEED && (j <= CELLS - 1); j++) {
                    if ((cells[k][j] == null) && (cells[k + 1][j] == null)) {
                        gap++;
                    } else {
                        break;
                    }
                }
                return gap;
            case CAR:
                for (int j = vehicle.offset + 1, k = vehicle.stripNo; j <= vehicle.offset + MAXSPEED && (j <= CELLS - 1); j++) {
                    if ((cells[k][j] == null) && (cells[k + 1][j] == null)) {
                        gap++;
                    } else {
                        break;
                    }
                }
                return gap;
            case BUS:
                for (int j = vehicle.offset + 1, k = vehicle.stripNo; j <= vehicle.offset + MAXSPEED && (j <= CELLS - 1); j++) {
                    if ((cells[k][j] == null) && (cells[k + 1][j] == null) && (cells[k + 2][j] == null)) {
                        gap++;
                    } else {
                        break;
                    }
                }
                return gap;
        }
        return 0;

    }

    private void freeCell(Vehicle vehicle) {
        int count = 0;
        switch (vehicle.vehicleType) {
            case BIKE:
                for (int j = vehicle.offset; (j > vehicle.offset - vehicle.length); j--) {
                    if (j < 0) {
                        //do free in virtual link
                        Link vlink = inLink;
                        int previousStrip = vehicle.previousStrip;
                        int remaingUpdate = vehicle.length - count;
                        for (int k = vlink.CELLS - 1; (k > (vlink.CELLS - 1) - remaingUpdate); k--) {
                            vlink.cells[previousStrip][k] = null;
                        }
                        return;
                    } else {
                        count++;
                        cells[vehicle.stripNo][j] = null;
                    }
                }
                break;
            case AUTO:
                for (int j = vehicle.offset; (j > vehicle.offset - vehicle.length); j--) {
                    if (j < 0) {
                        //do free in virtual link
                        Link vlink = inLink;
                        int previousStrip = vehicle.previousStrip;
                        int remaingUpdate = vehicle.length - count;
                        for (int k = vlink.CELLS - 1; (k > (vlink.CELLS - 1) - remaingUpdate); k--) {
                            vlink.cells[previousStrip][k] = null;
                            vlink.cells[previousStrip + 1][k] = null;
                        }
                        return;
                    } else {
                        count++;
                        cells[vehicle.stripNo][j] = null;
                        cells[vehicle.stripNo + 1][j] = null;
                    }
                }
                break;
            case CAR:
                for (int j = vehicle.offset; (j > vehicle.offset - vehicle.length); j--) {
                    if (j < 0) {
                        //do free in virtual link
                        Link vlink = inLink;
                        int previousStrip = vehicle.previousStrip;
                        int remaingUpdate = vehicle.length - count;
                        for (int k = vlink.CELLS - 1; (k > (vlink.CELLS - 1) - remaingUpdate); k--) {
                            vlink.cells[previousStrip][k] = null;
                            vlink.cells[previousStrip + 1][k] = null;
                        }
                        return;
                    } else {
                        count++;
                        cells[vehicle.stripNo][j] = null;
                        cells[vehicle.stripNo + 1][j] = null;
                    }
                }
                break;
            case BUS:
                for (int j = vehicle.offset; (j > vehicle.offset - vehicle.length); j--) {
                    if (j < 0) {
                        //do free in virtual link
                        Link vlink = inLink;
                        int previousStrip = vehicle.previousStrip;
                        int remaingUpdate = vehicle.length - count;
                        for (int k = vlink.CELLS - 1; (k > (vlink.CELLS - 1) - remaingUpdate); k--) {
                            vlink.cells[previousStrip][k] = null;
                            vlink.cells[previousStrip + 1][k] = null;
                            vlink.cells[previousStrip + 2][k] = null;
                        }
                        return;
                    } else {
                        count++;
                        cells[vehicle.stripNo][j] = null;
                        cells[vehicle.stripNo + 1][j] = null;
                        cells[vehicle.stripNo + 2][j] = null;
                    }
                }
                break;
        }
    }

    public void updateCell(Vehicle vehicle) {
        int count = 0;
        switch (vehicle.vehicleType) {
            case BIKE:
                for (int j = vehicle.offset; (j > vehicle.offset - vehicle.length); j--) {
                    if (j < 0) {
                        //do free in virtual link
                        Link vlink = inLink;
                        int previousStrip = vehicle.previousStrip;
                        int remaingUpdate = vehicle.length - count;
                        for (int k = vlink.CELLS - 1; (k > (vlink.CELLS - 1) - remaingUpdate); k--) {
                            vlink.cells[previousStrip][k] = vehicle;
                        }
                        return;
                    } else {
                        count++;
                        cells[vehicle.stripNo][j] = vehicle;
                    }
                }
                break;
            case AUTO:
                for (int j = vehicle.offset; (j > vehicle.offset - vehicle.length); j--) {
                    if (j < 0) {
                        //do free in virtual link
                        Link vlink = inLink;
                        int previousStrip = vehicle.previousStrip;
                        int remaingUpdate = vehicle.length - count;
                        for (int k = vlink.CELLS - 1; (k > (vlink.CELLS - 1) - remaingUpdate); k--) {
                            vlink.cells[previousStrip][k] = vehicle;
                            vlink.cells[previousStrip + 1][k] = vehicle;
                        }
                        return;
                    } else {
                        count++;
                        cells[vehicle.stripNo][j] = vehicle;
                        cells[vehicle.stripNo + 1][j] = vehicle;
                    }
                }
                break;
            case CAR:
                for (int j = vehicle.offset; (j > vehicle.offset - vehicle.length); j--) {
                    if (j < 0) {
                        //do free in virtual link
                        Link vlink = inLink;
                        int previousStrip = vehicle.previousStrip;
                        int remaingUpdate = vehicle.length - count;
                        for (int k = vlink.CELLS - 1; (k > (vlink.CELLS - 1) - remaingUpdate); k--) {
                            vlink.cells[previousStrip][k] = vehicle;
                            vlink.cells[previousStrip + 1][k] = vehicle;
                        }
                        return;
                    } else {
                        count++;
                        cells[vehicle.stripNo][j] = vehicle;
                        cells[vehicle.stripNo + 1][j] = vehicle;
                    }
                }
                break;
            case BUS:
                for (int j = vehicle.offset; (j > vehicle.offset - vehicle.length); j--) {
                    if (j < 0) {
                        //do free in virtual link
                        Link vlink = inLink;
                        int previousStrip = vehicle.previousStrip;
                        int remaingUpdate = vehicle.length - count;
                        for (int k = vlink.CELLS - 1; (k > (vlink.CELLS - 1) - remaingUpdate); k--) {
                            vlink.cells[previousStrip][k] = vehicle;
                            vlink.cells[previousStrip + 1][k] = vehicle;
                            vlink.cells[previousStrip + 2][k] = vehicle;
                        }
                        return;
                    } else {
                        count++;
                        cells[vehicle.stripNo][j] = vehicle;
                        cells[vehicle.stripNo + 1][j] = vehicle;
                        cells[vehicle.stripNo + 2][j] = vehicle;
                    }
                }
                break;
        }
    }

    private int calucLateVelocity(Vehicle vehicle, int maximumVelocity, int gap) {
        int velocity = 0;
        double randomNumber = random.nextGaussian();
        if (vehicle.velocity >= gap) {
            if (randomNumber <= PNOISE) {
                velocity = gap - 1;
                if (velocity < 0) {
                    velocity = 0;
                }
            } else {
                velocity = gap;
            }
        } else if (vehicle.velocity < maximumVelocity) {
            if (randomNumber <= PNOISE) {
                velocity = vehicle.velocity;
            } else {
                velocity = vehicle.velocity + 1;
            }
        } else if ((vehicle.velocity == maximumVelocity) && (vehicle.velocity < gap)) {
            if (randomNumber <= PNOISE) {
                velocity = maximumVelocity - 1;
            } else {
                velocity = maximumVelocity;
            }
        }
        return velocity;
    }

    private int calculateGapAtIntersection(Vehicle vehicle) {
        int gap = 0;
        int calculateGapAtConnector = 0;
        switch (vehicle.vehicleType) {
            case BIKE:
                for (int j = vehicle.offset + 1, k = vehicle.stripNo; j <= vehicle.offset + 5 && j <= CELLS - 1; j++) {
                    if ((cells[k][j] == null) && (j != CELLS)) {
                        gap++;
                    } else {
                        //Access the virtual link and calculate the gap
                        calculateGapAtConnector = outLink.calculateTransferGap(vehicle, getChangingStripNumber(vehicle));
                        if (gap + calculateGapAtConnector >= MAXSPEED) {
                            return MAXSPEED;
                        }
                        break;
                    }
                }
                return gap + calculateGapAtConnector;
            case AUTO:
                for (int j = vehicle.offset + 1, k = vehicle.stripNo; j <= vehicle.offset + 5 && j <= CELLS - 1; j++) {
                    if ((cells[k][j] == null) && (cells[k + 1][j] == null) && (j != CELLS)) {
                        gap++;
                    } else {
                        //Access the virtual link and calculate the gap
                        calculateGapAtConnector = outLink.calculateTransferGap(vehicle, getChangingStripNumber(vehicle));
                        if (gap + calculateGapAtConnector >= MAXSPEED) {
                            return MAXSPEED;
                        }
                        break;
                    }
                }
                return gap + calculateGapAtConnector;
            case CAR:
                for (int j = vehicle.offset + 1, k = vehicle.stripNo; j <= vehicle.offset + 5 && j <= CELLS - 1; j++) {
                    if ((cells[k][j] == null) && (cells[k + 1][j] == null) && (j != CELLS)) {
                        gap++;
                    } else {
                        //Access the virtual link and calculate the gap
                        calculateGapAtConnector = outLink.calculateTransferGap(vehicle, getChangingStripNumber(vehicle));
                        if (gap + calculateGapAtConnector >= MAXSPEED) {
                            return MAXSPEED;
                        }
                        break;
                    }
                }
                return gap + calculateGapAtConnector;
            case BUS:
                for (int j = vehicle.offset + 1, k = vehicle.stripNo; j <= vehicle.offset + 5 && j <= CELLS - 1; j++) {
                    if ((cells[k][j] == null) && (cells[k + 1][j] == null) && (cells[k + 2][j] == null) && (j != CELLS)) {
                        gap++;
                    } else {
                        //Access the virtual link and calculate the gap
                        calculateGapAtConnector = outLink.calculateTransferGap(vehicle, getChangingStripNumber(vehicle));
                        if (gap + calculateGapAtConnector >= MAXSPEED) {
                            return MAXSPEED;
                        }
                        break;
                    }
                }
                return gap + calculateGapAtConnector;
        }
        return 0;
    }

    private void addLink(Vehicle vehicle) {
        outLink.vehicles.add(vehicle);
    }

    public void print(int timestep) {

    	if(true) {
    		return;
    	}
        if (timestep % 100 == 0 || fileWritter == null) {
            if (out != null && fileWritter != null) {
                out.close();
                try {
                    fileWritter.close();
                } catch (IOException ex) {
                    Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            try {
                String dir = "D:/data/UNSIGNALISED NODE";
                File theDir = new File(dir);
                // if the directory does not exist, create it
                if (!theDir.exists()) {
                    boolean result = theDir.mkdir();
                    if (result) {
                        System.out.println("DIR created");
                    }
                }
                dir = "D:/data/UNSIGNALISED NODE/NODE-" + unsignalisedNode.id;
                theDir = new File(dir);
                // if the directory does not exist, create it
                if (!theDir.exists()) {
                    boolean result = theDir.mkdir();
                    if (result) {
                        System.out.println("DIR created");
                    }
                }
                dir = dir + "/CONNECTOR-" + id;
                theDir = new File(dir);
                // if the directory does not exist, create it
                if (!theDir.exists()) {
                    boolean result = theDir.mkdir();
                    if (result) {
                        System.out.println("DIR created");
                    }
                }
                fileWritter = new FileWriter(theDir + "/" + timestep + ".csv");
            } catch (IOException ex) {
                Logger.getLogger(Link.class.getName()).log(Level.SEVERE, null, ex);
            }
            out = new PrintWriter(fileWritter);
            writeFileHeader();
        }
        for (int i = 0; i < vehicles.size(); i++) {
            Vehicle vehicle = vehicles.get(i);
            out.println(timestep + "," + vehicle.vehicleId + "," + vehicle.vehicleType + ","
                    + vehicle.turningMovement + "," + vehicle.offset + ","
                    + vehicle.stripNo + "," + vehicle.velocity);
        }
        if (vehicles.isEmpty()) {
            String a = "--";
            out.println(timestep + "," + a + "," + a + ","
                    + a + "," + a + ","
                    + a + "," + a);
        }
    }

    private void writeFileHeader() {
        out.println("---------------------HEADER INFORMATION---------------------");
        out.println("CELL SIZE - \t" + CELLS);
        out.println("In LINK ID - \t" + inLink.id);
        out.println("OUT LINK ID - \t" + outLink.id);
        out.println("MAXIMUM SPEED - \t" + maxspeed_straight);
        out.println("Number of Lane - \t" + lane);
        out.println("Turning Movement - \t" + turningMovement);
        out.println("Unsignalised Intersection ID - \t" + unsignalisedNode.id);
        out.println("-----------------------------------------------------------");
        out.println("TIMESTEP,ID,TYPE,TURNING_MOVEMENT,OFFSET,STRIP,VELOCITY");

    }

    public void cleanResources() {
        if (fileWritter != null || out != null) {
            try {
                fileWritter.close();
            } catch (IOException ex) {
                Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
            }
            out.close();
        }
    }

    public int calculateTransferGap(Vehicle vehicle, int stripNumber) {
        int gap = 0;
        switch (vehicle.vehicleType) {
            case BIKE:
                for (int j = 0, k = stripNumber; j <= MAXSPEED && (j <= CELLS - 1); j++) {
                    if ((cells[k][j] == null)) {
                        gap++;
                    } else {
                        break;
                    }
                }
                return gap;
            case AUTO:
                for (int j = 0, k = stripNumber; j <= MAXSPEED && (j <= CELLS - 1); j++) {
                    if ((cells[k][j] == null) && (cells[k + 1][j] == null)) {
                        gap++;
                    } else {
                        break;
                    }
                }
                return gap;
            case CAR:
                for (int j = 0, k = stripNumber; j <= MAXSPEED && (j <= CELLS - 1); j++) {
                    if ((cells[k][j] == null) && (cells[k + 1][j] == null)) {
                        gap++;
                    } else {
                        break;
                    }
                }
                return gap;
            case BUS:
                for (int j = 0, k = stripNumber; j <= MAXSPEED && (j <= CELLS - 1); j++) {
                    if ((cells[k][j] == null) && (cells[k + 1][j] == null) && (cells[k + 2][j] == null)) {
                        gap++;
                    } else {
                        break;
                    }
                }
                return gap;
        }
        return 0;
    }

    private int getChangingStripNumber(Vehicle vehicle) {
        int stripNumber = 0;
        switch (vehicle.turningMovement) {
            case STRAIGHT:
                stripNumber = vehicle.stripNo;
                break;
            case LEFT:
                switch (vehicle.stripNo) {
                    case 0:
                        stripNumber = vehicle.stripNo;
                        break;
                    case 1:
                        stripNumber = vehicle.stripNo;
                        break;
                    case 2:
                        stripNumber = vehicle.stripNo;
                        break;
                    case 3:
                        System.out.println("ERROR");
                        System.exit(0);
                        break;
                    case 4:
                        System.out.println("ERROR");
                        System.exit(0);
                        break;
                    case 5:
                        System.out.println("ERROR");
                        System.exit(0);
                        break;
                }
                break;
            case RIGHT:
                switch (vehicle.stripNo) {
                    case 0:
                        stripNumber = 3;
                        break;
                    case 1:
                        stripNumber = 4;
                        break;
                    case 2:
                        stripNumber = 5;
                        break;
                    case 3:
                        System.out.println("ERROR");
                        System.exit(0);
                        break;
                    case 4:
                        System.out.println("ERROR");
                        System.exit(0);
                        break;
                    case 5:
                        System.out.println("ERROR");
                        System.exit(0);
                        break;
                }
                break;
        }
        return stripNumber;
    }

    public double[] getCoord() {
        double[] d = new double[4];
        double x2,y2;
        double x1 = sourcePoint.getX();
        double y1 = sourcePoint.getY();
        if (turningMovement == TurningMovement.STRAIGHT) {
            x2 = destinationPoint.getX();
            y2 = destinationPoint.getY();
        } else {
            x2 = centerPoint.getX();
            y2 = centerPoint.getY();
        }

        d[0] = x1;
        d[1] = y1;
        d[2] = x2;
        d[3] = y2;
        return d;
    }

    public double getAngle() {
         if (turningMovement == TurningMovement.STRAIGHT) {
            return Geometry.getAbsoluteAngel(sourcePoint, destinationPoint);
        } else {
            return Geometry.getAbsoluteAngel(sourcePoint, centerPoint);
        }
        
    }
}
