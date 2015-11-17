package edu.iitb.civil.tse.vehicle;

import java.util.Random;

/**
 *
 * @author Debabrata Tripathy
 */
public class Car extends Vehicle {

    public static final int LENGTH = 4;
    public static final int WIDTH = 2;

    public final void initialse() {
        this.length = LENGTH;
        this.width = WIDTH;
        this.vehicleType = VehicleType.BUS;
    }

    public Car(int vehicleId, double random_vehicleHeadway, VehicleType vehicleType,
            TurningMovement turningMovement, double arrivalTimedouble,
            double noiseProbability, int velocity) {
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
        cRed = .248;
        cGreen = 0.52;
        cBlue = .126;
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
