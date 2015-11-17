package edu.iitb.civil.tse.vehicle;

import java.util.Random;

/**
 *
 * @author Debabrata Tripathy
 */
public class Bus extends Vehicle {

    public static final int LENGTH = 8;
    public static final int WIDTH = 3;

    public final void initialse() {
        this.length = LENGTH;
        this.width = WIDTH;
        this.vehicleType = VehicleType.BUS;
    }

    public Bus(int vehicleId, double random_vehicleHeadway, VehicleType vehicleType, TurningMovement turningMovement, double arrivalTimedouble, double noiseProbability, int velocity) {
        initialse();
        this.vehicleId = vehicleId;
        this.random_vehicleHeadway = random_vehicleHeadway;
        this.vehicleType = vehicleType;
        this.turningMovement = turningMovement;
        this.arrivalTimeDouble = arrivalTimedouble;
        this.arrivalTime = (int) Math.round(arrivalTimedouble);
        this.noiseProbability = noiseProbability;
        this.velocity = velocity;
        Random numran = new Random();
        cRed = 0.0;
        cGreen = 1.0;
        cBlue = 0.5;
    }

    @Override
    public double getDistance() {
        return offset;
    }

    @Override
    public int getStripNo() {
        return stripNo;
    }
}
