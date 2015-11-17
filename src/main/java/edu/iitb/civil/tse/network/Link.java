package edu.iitb.civil.tse.network;

import edu.iitb.civil.tse.geometry.Geometry;
import edu.iitb.civil.tse.gui.panel.opengl.Point3D;
import edu.iitb.civil.tse.vehicle.Vehicle;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Debabrata Tripathy
 */
public class Link {

    public static double CELLSIZE = 1.2;
    public String id;
    public Node source;
    public Node destination;
    public double length;
    public List<Vehicle> vehicles;
    public Vehicle[][] cells;
    public int noOfStrip;
    public int CELLS;
    public Movement movement;
    public int lane;
    public double speed;
    private double PNOISE = .4;
    private Point3D sourcePoint;
    private Point3D destinationPoint;
    private FileWriter fileWritter, fileWritter1;
    private PrintWriter out, out1;
    private int maxspeed_straight = 5;
    private int maxspeed_turning = 3;
    private int MAXSPEED = 5;
    private Network network;
    private Random random = new Random();

    public void setCoordinate(Point3D sourcePoint, Point3D destinationPoint) {
        this.sourcePoint = sourcePoint;
        this.destinationPoint = destinationPoint;
        this.length = Geometry.distance(this.sourcePoint, this.destinationPoint);
        this.CELLS = (int) (length / 1.2);
        this.cells = new Vehicle[3 * lane][CELLS];
    }

    public void getCoordinate() {
        List<Point3D> points = new ArrayList();
        points.add(sourcePoint);
        points.add(destinationPoint);
    }

    public Point3D getSourcePoint() {
        return sourcePoint;
    }

    public Point3D getDestinaPoint() {
        return destinationPoint;
    }

    public Link(String string, Node sourceNode, Node destinationNode, int lanesAB, double speedAB, Movement move, double PNOISE) {
        this.id = string;
        this.lane = lanesAB;
        this.speed = speedAB;
        this.source = sourceNode;
        this.destination = destinationNode;
        this.length = Geometry.distance(sourceNode.getPoint(), destinationNode.getPoint());
        this.movement = move;
        vehicles = new ArrayList<>();
        this.PNOISE = PNOISE;
    }

    public String getId() {
        return id;
    }

    public Node getSource() {
        return source;
    }

    public Node getDestination() {
        return destination;
    }

    public void add(Vehicle vehicle) {
        vehicles.add(vehicle);
    }

    public void delete(Vehicle vehicle) {
        vehicles.remove(vehicle);
    }

    public double[] getCoord() {
        double[] d = new double[4];
        double x1 = sourcePoint.getX();
        double y1 = sourcePoint.getY();
        double x2 = destinationPoint.getX();
        double y2 = destinationPoint.getY();
        d[0] = x1;
        d[1] = y1;
        d[2] = x2;
        d[3] = y2;
        return d;
    }

    public double getAngle() {
        return Geometry.getAbsoluteAngel(sourcePoint, destinationPoint);
    }

    public void update(Network network, int timestamp) {
        this.network = network;
        for (int i = 0; i <= vehicles.size() - 1; i++) {
            Vehicle vehicle = vehicles.get(i);
            if (vehicle.updated == timestamp) {
                vehicle.updated = 0;
                continue;
            }
            int gap;
            if ((this.CELLS - 5 < vehicle.offset) && (destination.nodeType == NodeType.UNSIGNALISED)) {
                switch (vehicle.turningMovement) {
                    case LEFT:
                        gap = calculateGapAtIntersection(vehicle); //mapping layer should check
                        vehicle.velocity = calucLateVelocity(vehicle, maxspeed_turning, gap);
                        freeCell(vehicle); //mapping layer should check
                        if (vehicle.offset > ((vehicle.offset + vehicle.velocity) % (CELLS - 1))) {
                            //it passes the link and add into virtual link
                            vehicle.offset = (vehicle.offset + vehicle.velocity) % (CELLS - 1);
                            VirtualLink vLink = network.getVirtualLink((Unsignalised) destination, this, vehicle.turningMovement);
                            vehicles.remove(vehicle);
                            i--;
                            addInVirtualLink(vehicle);
                            vehicle.updated = timestamp;
                            vehicle.previousStrip = vehicle.stripNo;
                            vehicle.stripNo = getChangingStripNumber(vehicle);
                            vLink.updateCell(vehicle);
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
                            //it passes the link and add into virtual link
                            vehicle.offset = (vehicle.offset + vehicle.velocity) % (CELLS - 1);
                            VirtualLink vLink = network.getVirtualLink((Unsignalised) destination, this, vehicle.turningMovement);
                            vehicles.remove(vehicle);
                            i--;
                            addInVirtualLink(vehicle);
                            vehicle.updated = timestamp;
                            vehicle.previousStrip = vehicle.stripNo;
                            vehicle.stripNo = getChangingStripNumber(vehicle);
                            vLink.updateCell(vehicle);

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
                            //it passes the link and add into virtual link
                            vehicle.offset = (vehicle.offset + vehicle.velocity) % (CELLS - 1);
                            VirtualLink vLink = network.getVirtualLink((Unsignalised) destination, this, vehicle.turningMovement);
                            vehicles.remove(vehicle);
                            i--;
                            addInVirtualLink(vehicle);
                            vehicle.previousStrip = vehicle.stripNo;
                            vehicle.updated = timestamp;
                            vehicle.stripNo = getChangingStripNumber(vehicle);
                            vLink.updateCell(vehicle);
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
                        VirtualLink vlink = vehicle.previousVisitedLink;
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
                        VirtualLink vlink = vehicle.previousVisitedLink;
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
                        VirtualLink vlink = vehicle.previousVisitedLink;
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
                        VirtualLink vlink = vehicle.previousVisitedLink;
                        int previousStrip = vehicle.previousStrip;
                        int remaingUpdate = vehicle.length - count;
                        for (int k = vlink.CELLS - 1; (k > (vlink.CELLS - 1) - remaingUpdate)&&(k>=0); k--) {
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
                        VirtualLink vlink = vehicle.previousVisitedLink;
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
                        VirtualLink vlink = vehicle.previousVisitedLink;
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
                        VirtualLink vlink = vehicle.previousVisitedLink;
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
                        VirtualLink vlink = vehicle.previousVisitedLink;
                        int previousStrip = vehicle.previousStrip;
                        int remaingUpdate = vehicle.length - count;
                        for (int k = vlink.CELLS - 1; (k > (vlink.CELLS - 1) - remaingUpdate)&& (k>=0); k--) {
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
        double randomNumber = random.nextDouble();
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
                        VirtualLink vLink = network.getVirtualLink((Unsignalised) destination, this, vehicle.turningMovement);
                        calculateGapAtConnector = vLink.calculateTransferGap(vehicle, getChangingStripNumber(vehicle));
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
                        VirtualLink vLink = network.getVirtualLink((Unsignalised) destination, this, vehicle.turningMovement);
                        calculateGapAtConnector = vLink.calculateTransferGap(vehicle, getChangingStripNumber(vehicle));
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
                        VirtualLink vLink = network.getVirtualLink((Unsignalised) destination, this, vehicle.turningMovement);
                        calculateGapAtConnector = vLink.calculateTransferGap(vehicle, getChangingStripNumber(vehicle));
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
                        VirtualLink vLink = network.getVirtualLink((Unsignalised) destination, this, vehicle.turningMovement);
                        calculateGapAtConnector = vLink.calculateTransferGap(vehicle, getChangingStripNumber(vehicle));
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
                        System.out.println("ERROR");
                        System.exit(0);
                        break;
                    case 1:
                        System.out.println("ERROR");
                        System.exit(0);
                        break;
                    case 2:
                        System.out.println("ERROR");
                        System.exit(0);
                        break;
                    case 3:
                        stripNumber = 0;
                        break;
                    case 4:
                        stripNumber = 1;
                        break;
                    case 5:
                        stripNumber = 2;
                        break;
                }
                break;
        }
        return stripNumber;
    }

    private void addInVirtualLink(Vehicle vehicle) {
        VirtualLink vLink = network.getVirtualLink((Unsignalised) destination, this, vehicle.turningMovement);
        vLink.vehicles.add(vehicle);
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
                String dir = "D:/data/LINK";
                File theDir = new File(dir);
                // if the directory does not exist, create it
                if (!theDir.exists()) {
                    boolean result = theDir.mkdir();
                    if (result) {
                        System.out.println("DIR created");
                    }
                }
                dir = "D:/data/LINK/LINK-" + id;
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
        out.println("\nNumber OF CELLS - \t" + CELLS);
        out.println("Source Node ID - \t" + source.id);
        out.println("Destination Node ID - \t" + destination.id);
        out.println("Source POINT - \t" + sourcePoint);
        out.println("Destination POINT - \t" + destinationPoint);
        out.println("MAXIMUM SPEED - \t" + maxspeed_straight);
        out.println("MAXIMUM SPEED FOR TURNING VEHICLE AT INTERSECTION - \t" + maxspeed_turning);
        out.println("PNOISE - \t" + PNOISE);
        out.println("CELL SIZE - \t" + CELLSIZE);
        out.println("Number of Lane - \t" + lane);
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

    public void delete(int timestemp) {
        int counter = 0;
        if (destination.nodeType == NodeType.GENERATOR) {
            for (int i = 0; i < vehicles.size(); i++) {
                Vehicle vehicle = vehicles.get(i);
                if (vehicle.offset == CELLS - 1) {
                    vehicles.remove(vehicle);
                    freeCell(vehicle);
                    i--;
                    counter++;
                }
            }
            print1(timestemp, counter);
        }
    }

    public int calculateTransferGap(Vehicle vehicle, int stripNo) {
        int gap = 0;
        switch (vehicle.vehicleType) {
            case BIKE:
                for (int j = 0, k = stripNo; j <= MAXSPEED && (j <= CELLS - 1); j++) {
                    if ((cells[k][j] == null)) {
                        gap++;
                    } else {
                        break;
                    }
                }
                return gap;
            case AUTO:
                for (int j = 0, k = stripNo; j <= MAXSPEED && (j <= CELLS - 1); j++) {
                    if ((cells[k][j] == null) && (cells[k + 1][j] == null)) {
                        gap++;
                    } else {
                        break;
                    }
                }
                return gap;
            case CAR:
                for (int j = 0, k = stripNo; j <= MAXSPEED && (j <= CELLS - 1); j++) {
                    if ((cells[k][j] == null) && (cells[k + 1][j] == null)) {
                        gap++;
                    } else {
                        break;
                    }
                }
                return gap;
            case BUS:
                for (int j = 0, k = stripNo; j <= MAXSPEED && (j <= CELLS - 1); j++) {
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

    private void writeHeader() {
        out.println("---------------------HEADER INFORMATION---------------------");
        out.println("generator ID - \t" + destination.id);
        out.println("-----------------------------------------------------------");
        out.println("TIMESTEP,No. OF DELETED VEHICLE");

    }

    public void print1(int timestep, int size) {
        if (timestep % 1000 == 0 || fileWritter1 == null) {
            if (out1 != null && fileWritter1 != null) {
                out1.close();
                try {
                    fileWritter1.close();
                } catch (IOException ex) {
                    Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            try {
                String dir = "D:/data/DELETED";
                File theDir = new File(dir);
                // if the directory does not exist, create it
                if (!theDir.exists()) {
                    boolean result = theDir.mkdir();
                    if (result) {
                        System.out.println("DIR created");
                    }
                }
                dir = "D:/data/DELETED/GENERATOR-" + destination.id;
                theDir = new File(dir);
                // if the directory does not exist, create it
                if (!theDir.exists()) {
                    boolean result = theDir.mkdir();
                    if (result) {
                        System.out.println("DIR created");
                    }
                }
                fileWritter1 = new FileWriter(theDir + "/" + timestep + ".csv");
            } catch (IOException ex) {
                Logger.getLogger(Link.class.getName()).log(Level.SEVERE, null, ex);
            }
            out1 = new PrintWriter(fileWritter1);
            writeHeader();
        }

        out1.println(timestep + "," + size);
        if (size == 0) {
            String a = "--";
            out1.println(timestep + "," + 0);
        }
    }
}
