package edu.iitb.civil.tse.network;

import edu.iitb.civil.tse.apps.Global;
import edu.iitb.civil.tse.gui.panel.opengl.Point3D;
import edu.iitb.civil.tse.vehicle.Auto;
import edu.iitb.civil.tse.vehicle.Bike;
import edu.iitb.civil.tse.vehicle.Bus;
import edu.iitb.civil.tse.vehicle.Car;
import edu.iitb.civil.tse.vehicle.TurningMovement;
import edu.iitb.civil.tse.vehicle.Vehicle;
import edu.iitb.civil.tse.vehicle.VehicleType;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Debabrata Tripathy
 */
public class Generator extends Node {

    public HashMap<VehicleType, Integer> propertionOfVehicle = new HashMap();
    public HashMap<TurningMovement, Integer> propertionOfTurningMovementOfVehicle = new HashMap();
    public double arrivalRate;
    public List<Vehicle> virtualVehiclesQueue = Collections.synchronizedList(new ArrayList<Vehicle>());
    //
    public long totalNumberOfVehicle;
    public HashMap<VehicleType, Long> numberOfVehicleByType = new HashMap();
    public HashMap<TurningMovement, Long> numberOfturningMOvementofVehicle = new HashMap();
    //
    private ArrayList<Double> randomvehicleheadway = new ArrayList<>();
    private ArrayList<Double> randomizedProbabilityPerVehicle = new ArrayList<>();
    private ArrayList<VehicleType> randomvehicleType = new ArrayList<>();
    private ArrayList<TurningMovement> randomvehicleTurning = new ArrayList<>();
    private ArrayList<Integer> randomvehiclevelocity = new ArrayList<>();
    //
    private FileWriter fileWritter;
    private PrintWriter out;
    private int maxspeed_straight =5;

    public Generator(String id, double xCoordiante, double yCoordiante, double arrivalRate, HashMap<VehicleType, Integer> propertionOfVehicle, HashMap<TurningMovement, Integer> propertionOfTurningMovementOfVehicle) {
        this.id = id;
        this.xCordinate = xCoordiante;
        this.yCoordianate = yCoordiante;
        this.zCoordianate = 0;
        this.nodeType = NodeType.GENERATOR;
        this.arrivalRate = arrivalRate;
        this.propertionOfVehicle = propertionOfVehicle;
        this.propertionOfTurningMovementOfVehicle = propertionOfTurningMovementOfVehicle;
        System.out.println("GENERATOR = \t" + getId());
    }

    public Generator(String id, Point3D p) {
        this.id = id;
        arrivalRate = .4;
        this.xCordinate = p.getX();
        this.yCoordianate = p.getY();
        this.zCoordianate = p.getZ();
        this.nodeType = NodeType.GENERATOR;
        propertionOfVehicle.put(VehicleType.CAR, 30);
        propertionOfVehicle.put(VehicleType.BIKE, 50);
        propertionOfVehicle.put(VehicleType.BUS, 5);
        propertionOfVehicle.put(VehicleType.AUTO, 15);
        propertionOfTurningMovementOfVehicle.put(TurningMovement.LEFT, 0);
        propertionOfTurningMovementOfVehicle.put(TurningMovement.RIGHT, 0);
        propertionOfTurningMovementOfVehicle.put(TurningMovement.STRAIGHT, 100);
    }

    public void initialise() {
        totalNumberOfVehicle = calculateNoOfVehicle();
        numberOfVehicleByType = calculateNoOfVehicleBYType();
        numberOfturningMOvementofVehicle = calculateturningMovementOfVehicle();
        generateRandomForVehicleHeadway();
        generateRandomForVehicleType();
        generateRandomForVehicleTurning();
        generateRandomForVehicleInitialspeed();
        calculateRandomizedProbabilityPerVehicle();
        addIntoVirtualQueue();

    }

    private long calculateNoOfVehicle() {
        return Math.round(arrivalRate * Global.getRunTime());
        //return 5;
    }

    private HashMap<VehicleType, Long> calculateNoOfVehicleBYType() {
        for (Map.Entry<VehicleType, Integer> entry : propertionOfVehicle.entrySet()) {
            double calculate;
            calculate = (entry.getValue() / 100.0) * totalNumberOfVehicle;
            numberOfVehicleByType.put(entry.getKey(), (long) Math.round(calculate));
        }
        return numberOfVehicleByType;
    }

    private HashMap<TurningMovement, Long> calculateturningMovementOfVehicle() {
        for (Map.Entry<TurningMovement, Integer> entry : propertionOfTurningMovementOfVehicle.entrySet()) {
            double calculate = (entry.getValue() / 100.0) * totalNumberOfVehicle;
            numberOfturningMOvementofVehicle.put(entry.getKey(), (long) Math.round(calculate));
        }
        return numberOfturningMOvementofVehicle;
    }

    private void generateRandomForVehicleHeadway() {
        Random ra = new Random();
        for (int i = 0; i < totalNumberOfVehicle; i++) {
            randomvehicleheadway.add(ra.nextDouble());
        }
    }

    private void generateRandomForVehicleInitialspeed() {
        Random ra = new Random();
        for (int i = 0; i < totalNumberOfVehicle; i++) {
            randomvehiclevelocity.add(1 + ra.nextInt(maxspeed_straight));
        }
    }

    private void generateRandomForVehicleType() {
        ArrayList<Integer> nums = new ArrayList<>();
        for (int i = 1; i <= totalNumberOfVehicle; i++) {
            nums.add(new Integer(i));
        }
        Collections.shuffle(nums);
        long t1 = numberOfVehicleByType.get(VehicleType.BIKE);
        long t2 = numberOfVehicleByType.get(VehicleType.AUTO);
        long t3 = numberOfVehicleByType.get(VehicleType.CAR);
        long t4 = numberOfVehicleByType.get(VehicleType.BUS);
        for (int i = 0; i < totalNumberOfVehicle; i++) {
            long temp = nums.get(i);
            if (temp <= t1) {
                randomvehicleType.add(VehicleType.BIKE);
            } else if ((temp > t1) && (temp <= t1 + t2)) {
                randomvehicleType.add(VehicleType.AUTO);
            } else if ((temp > t1 + t2) && (temp <= t1 + t2 + t3)) {
                randomvehicleType.add(VehicleType.CAR);
            } else if ((temp > t1 + t2 + t3)) {
                randomvehicleType.add(VehicleType.BUS);
            }
        }
    }

    private void generateRandomForVehicleTurning() {
        ArrayList<Integer> nums = new ArrayList<>();
        for (int i = 1; i <= totalNumberOfVehicle; i++) {
            nums.add(new Integer(i));
        }
        Collections.shuffle(nums);
        long t1 = numberOfturningMOvementofVehicle.get(TurningMovement.LEFT);
        long t2 = numberOfturningMOvementofVehicle.get(TurningMovement.RIGHT);
        long t3 = numberOfturningMOvementofVehicle.get(TurningMovement.STRAIGHT);
        for (int i = 0; i < totalNumberOfVehicle; i++) {
            int temp = nums.get(i);
            if (temp <= t1) {
                randomvehicleTurning.add(TurningMovement.LEFT);
            } else if ((temp > t1) && (temp <= t1 + t2)) {
                randomvehicleTurning.add(TurningMovement.RIGHT);
            } else if ((temp > t1 + t2)) {
                randomvehicleTurning.add(TurningMovement.STRAIGHT);
            }
        }
    }

    private double calculateHeadway(double random_vehicleHeadway) {
        return (((-1) / arrivalRate) * Math.log(random_vehicleHeadway));
    }

    private void calculateRandomizedProbabilityPerVehicle() {
        Random ra = new Random();
        for (int i = 0; i < totalNumberOfVehicle; i++) {
            randomizedProbabilityPerVehicle.add(ra.nextDouble());
        }
    }

    private void addIntoVirtualQueue() {
        Vehicle previousVehicle = null;
        int vehicleId;
        double random_vehicleHeadway;
        double noiseProbability;
        double headway;
        int velocity;
        double arrivalTimedouble;
        VehicleType vehicleType;
        TurningMovement turningMovement;
        Vehicle vehicle = null;
        for (int i = 0; i < totalNumberOfVehicle; i++) {
            vehicleId = i;
            vehicleType = randomvehicleType.get(i);
            turningMovement = randomvehicleTurning.get(i);
            random_vehicleHeadway = randomvehicleheadway.get(i);
            noiseProbability = randomizedProbabilityPerVehicle.get(i);
            velocity = randomvehiclevelocity.get(i);
            headway = calculateHeadway(random_vehicleHeadway);
            if (previousVehicle == null) {
                arrivalTimedouble = 1.0;
            } else {
                arrivalTimedouble = headway + previousVehicle.arrivalTimeDouble;
            }

            switch (vehicleType) {
                case BIKE:
                    vehicle = new Bike(vehicleId, random_vehicleHeadway, vehicleType, turningMovement, arrivalTimedouble, noiseProbability, velocity);
                    virtualVehiclesQueue.add(vehicle);
                    break;
                case AUTO:
                    vehicle = new Auto(vehicleId, random_vehicleHeadway, vehicleType, turningMovement, arrivalTimedouble, noiseProbability, velocity);
                    virtualVehiclesQueue.add(vehicle);
                    break;
                case CAR:
                    vehicle = new Car(vehicleId, random_vehicleHeadway, vehicleType, turningMovement, arrivalTimedouble, noiseProbability, velocity);
                    virtualVehiclesQueue.add(vehicle);
                    break;
                case BUS:
                    vehicle = new Bus(vehicleId, random_vehicleHeadway, vehicleType, turningMovement, arrivalTimedouble, noiseProbability, velocity);
                    virtualVehiclesQueue.add(vehicle);
                    break;

            }
            previousVehicle = vehicle;
            print(vehicleId, vehicleType, turningMovement, random_vehicleHeadway, noiseProbability, velocity, headway, arrivalTimedouble);
        }
    }
    long previousArrivalTime = -2;

    public void print(int vehicleId, VehicleType vehicleType, TurningMovement turningMovement, double random_vehicleHeadway, double noiseProbability, double velocity, double headway, double arrivalTimeDouble) {
        
    	if(true) {
    		return;
    	}
    	if (((Math.round(arrivalTimeDouble)) % 500 == 0 && previousArrivalTime != Math.round(arrivalTimeDouble)) || fileWritter == null) {
            if (out != null && fileWritter != null) {
                out.close();
                try {
                    fileWritter.close();
                } catch (IOException ex) {
                    Logger.getLogger(Generator.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            try {
                String dir = "D:/data/GENERATOR";
                File theDir = new File(dir);
                // if the directory does not exist, create it
                if (!theDir.exists()) {
                    boolean result = theDir.mkdir();
                    if (result) {
                        System.out.println("DIR created");
                    }
                }
                dir = "D:/data/GENERATOR/GENERATOR-" + id;
                theDir = new File(dir);
                // if the directory does not exist, create it
                if (!theDir.exists()) {
                    boolean result = theDir.mkdir();
                    if (result) {
                        System.out.println("DIR created");
                    }
                }
                fileWritter = new FileWriter(theDir + "/" + (int) arrivalTimeDouble + ".csv");
            } catch (IOException ex) {
                Logger.getLogger(Link.class.getName()).log(Level.SEVERE, null, ex);
            }
            out = new PrintWriter(fileWritter);
            writeFileHeader();
            previousArrivalTime = Math.round(arrivalTimeDouble);
        }
        out.println(vehicleId + "," + vehicleType + "," + turningMovement + "," + random_vehicleHeadway + "," + velocity + "," + headway + "," + arrivalTimeDouble + "," + Math.round(arrivalTimeDouble));
    }

    private void writeFileHeader() {
        out.println("---------------------HEADER INFORMATION---------------------");
        out.println("GENERATOR ID \t" + id);
        out.println("ARRIVAL RATE\t" + arrivalRate);
        out.println("POSITION\t[ " + xCordinate + ", " + yCoordianate + ", " + zCoordianate + " ]");
        out.println("PERCENTAGE OF LEFT TURNING MOVEMENT\t" + propertionOfTurningMovementOfVehicle.get(TurningMovement.LEFT));
        out.println("PERCENTAGE OF RIGHT TURNING MOVEMENT\t" + propertionOfTurningMovementOfVehicle.get(TurningMovement.RIGHT));
        out.println("PERCENTAGE OF STRAIGHT MOVEMENT\t" + propertionOfTurningMovementOfVehicle.get(TurningMovement.STRAIGHT));
        out.println("PERCENTAGE OF CAR\t" + propertionOfVehicle.get(VehicleType.CAR));
        out.println("PERCENTAGE OF BIKE\t" + propertionOfVehicle.get(VehicleType.BIKE));
        out.println("PERCENTAGE OF BUS\t" + propertionOfVehicle.get(VehicleType.BUS));
        out.println("PERCENTAGE OF AUTO\t" + propertionOfVehicle.get(VehicleType.AUTO));
        //print some generator information
        out.println("-----------------------------------------------------------");
        out.println("VEHICLE ID" + "," + "VEHICLE TYPE" + "," + "TURNING MOVEMENT" + "," + "RND VEHICLE HEADWAY" + "," + "VELOCITY" + "," + "HEADWAY" + "," + "ARRIVAL TIME DOUBLE" + "," + "ARRIVAL TIME");

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
}
