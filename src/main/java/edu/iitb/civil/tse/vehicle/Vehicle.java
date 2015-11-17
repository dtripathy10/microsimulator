package edu.iitb.civil.tse.vehicle;

import edu.iitb.civil.tse.network.Link;
import edu.iitb.civil.tse.network.VirtualLink;

/**
 *
 * @author Debabrata Tripathy
 */
public abstract class Vehicle {

    public int vehicleId;
    public Link link;
    public int offset;
    public int stripNo;
    public int velocity;
    public VehicleType vehicleType;
    public TurningMovement turningMovement;
    public int length;
    public int width;
    public double random_vehicleHeadway;
    public double arrivalTimeDouble;
    public int arrivalTime;
    public double noiseProbability;
    public double cRed;
    public double cGreen;
    public double cBlue;
    public double isPresentOn2Link;
    public int tempOffset;
    public int tempGap;
    public VirtualLink previousVisitedLink;
    public int updated = 0;
    public int previousStrip;
    public int tempStripNumber;

    public double[] getColour() {
        double[] d = new double[3];
        d[0] = cRed;
        d[1] = cGreen;
        d[2] = cBlue;
        return d;
    }

    /**
     *
     * @return
     */

    public abstract double getDistance();

    public abstract int getStripNo();
}
