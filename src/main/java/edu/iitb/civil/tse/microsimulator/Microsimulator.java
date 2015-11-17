package edu.iitb.civil.tse.microsimulator;

import edu.iitb.civil.tse.apps.Global;
import edu.iitb.civil.tse.network.Generator;
import edu.iitb.civil.tse.network.Link;
import edu.iitb.civil.tse.network.Network;
import edu.iitb.civil.tse.network.VirtualLink;
import edu.iitb.civil.tse.vehicle.Vehicle;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Debabrata Tripathy
 */
public class Microsimulator {

    public Network network;

    public void initialise() {
        //initialise all generaor
        for (Generator generator : Global.getNetwork().getGenerators()) {
            generator.initialise();
        }
    }
    private int timestamp;

    public void start(int timeStepCount) {
        timestamp = timeStepCount;
        network = Global.getNetwork();
        List<Generator> generators = network.getGenerators();
        List<Link> links = network.getLinks();
        List<VirtualLink> virtualLinks = network.getVirtualLink();

        //generate vehicle
        for (Generator generator : generators) {
            generateVehicle(generator);
        }
        //update all vehicle Virtual Link
        for (VirtualLink vlink : virtualLinks) {
            vlink.update(timeStepCount);
        }
        //update all vehicle in link
        for (Link link : links) {
            link.update(network, timeStepCount);
        }
        //print all vehicle Virtual Link
        for (VirtualLink vlink : virtualLinks) {
            vlink.print(timeStepCount);
        }
        //print all vehicle in link
        for (Link link : links) {
            link.print(timeStepCount);
        }
        //delete the vehicle out of the system
        for (Link link : links) {
            link.delete(timeStepCount);
        }
    }

    private void generateVehicle(Generator generator) {
        Link link = network.getOutLink(generator).get(0);
        List<Vehicle> vehicleArray = new ArrayList<>();
        for (Vehicle v : generator.virtualVehiclesQueue) {
            if (v.arrivalTime <= timestamp) {
                vehicleArray.add(v);
            }
        }
        for (Vehicle vehicle : vehicleArray) {
            if (canWePutVehicleInLink(vehicle, link)) {
                generator.virtualVehiclesQueue.remove(vehicle);
                link.vehicles.add(vehicle);
                vehicle.link = link;
                vehicle.updated = timestamp;
            }
        }
    }

    private boolean canWePutVehicleInLink(Vehicle vehicle, Link link) {
        switch (vehicle.turningMovement) {
            case STRAIGHT:
                switch (vehicle.vehicleType) {
                    case BIKE:
                        for (int i = 0; i <= 5; i++) {
                            if (link.cells[i][0] == null && link.cells[i][1] == null) {
                                vehicle.offset = 1;
                                vehicle.stripNo = i;
                                link.cells[i][0] = vehicle;
                                link.cells[i][1] = vehicle;
                                return true;
                            }
                        }
                        break;
                    case AUTO:
                        for (int i = 0; i <= 4; i++) {

                            if (link.cells[i][0] == null && link.cells[i][1] == null
                                    && link.cells[i][2] == null
                                    && link.cells[i + 1][0] == null && link.cells[i + 1][1] == null
                                    && link.cells[i + 1][2] == null) {
                                vehicle.offset = 2;
                                vehicle.stripNo = i;
                                for (int j = 0; j <= 2; j++) {
                                    link.cells[i][j] = vehicle;
                                    link.cells[i + 1][j] = vehicle;
                                }
                                return true;
                            }
                        }
                        break;
                    case CAR:
                        for (int i = 0; i <= 4; i++) {
                            if (link.cells[i][0] == null && link.cells[i][1] == null
                                    && link.cells[i][2] == null && link.cells[i][3] == null
                                    && link.cells[i + 1][0] == null && link.cells[i + 1][1] == null
                                    && link.cells[i + 1][2] == null && link.cells[i + 1][3] == null) {
                                vehicle.offset = 3;
                                vehicle.stripNo = i;
                                for (int j = 0; j <= 3; j++) {
                                    link.cells[i][j] = vehicle;
                                    link.cells[i + 1][j] = vehicle;
                                }
                                return true;
                            }
                        }
                        break;
                    case BUS:
                        for (int i = 0; i <= 3; i++) {
                            if (link.cells[i][0] == null && link.cells[i][1] == null
                                    && link.cells[i][2] == null && link.cells[i][3] == null
                                    && link.cells[i][4] == null && link.cells[i][5] == null
                                    && link.cells[i][6] == null && link.cells[i][7] == null
                                    && link.cells[i + 1][0] == null && link.cells[i + 1][1] == null
                                    && link.cells[i + 1][2] == null && link.cells[i + 1][3] == null
                                    && link.cells[i + 1][4] == null && link.cells[i + 1][5] == null
                                    && link.cells[i + 1][6] == null && link.cells[i + 1][7] == null
                                    && link.cells[i + 2][0] == null && link.cells[i + 2][1] == null
                                    && link.cells[i + 2][2] == null && link.cells[i + 2][3] == null
                                    && link.cells[i + 2][4] == null && link.cells[i + 2][5] == null
                                    && link.cells[i + 2][6] == null && link.cells[i + 2][7] == null) {
                                vehicle.offset = 7;
                                vehicle.stripNo = i;

                                for (int j = 0; j <= 7; j++) {
                                    link.cells[i][j] = vehicle;
                                    link.cells[i + 1][j] = vehicle;
                                    link.cells[i + 2][j] = vehicle;
                                }
                                return true;

                            }
                        }
                        break;
                }
                break;


            case LEFT:
                switch (vehicle.vehicleType) {
                    case BIKE:
                        for (int i = 0; i <= 0; i++) {
                            if (link.cells[i][0] == null && link.cells[i][1] == null) {
                                vehicle.offset = 1;
                                vehicle.stripNo = i;
                                link.cells[i][0] = vehicle;
                                link.cells[i][1] = vehicle;
                                return true;
                            }
                        }
                        break;
                    case AUTO:
                        for (int i = 0; i <= 0; i++) {
                            if (link.cells[i][0] == null && link.cells[i][1] == null
                                    && link.cells[i][2] == null
                                    && link.cells[i + 1][0] == null && link.cells[i + 1][1] == null
                                    && link.cells[i + 1][2] == null) {
                                vehicle.offset = 2;
                                vehicle.stripNo = i;
                                for (int j = 0; j <= 2; j++) {
                                    link.cells[i][j] = vehicle;
                                    link.cells[i + 1][j] = vehicle;
                                }
                                return true;
                            }
                        }
                        break;
                    case CAR:
                        for (int i = 0; i <= 0; i++) {
                            if (link.cells[i][0] == null && link.cells[i][1] == null
                                    && link.cells[i][2] == null && link.cells[i][3] == null
                                    && link.cells[i + 1][0] == null && link.cells[i + 1][1] == null
                                    && link.cells[i + 1][2] == null && link.cells[i + 1][3] == null) {
                                vehicle.offset = 3;
                                vehicle.stripNo = i;
                                for (int j = 0; j <= 3; j++) {
                                    link.cells[i][j] = vehicle;
                                    link.cells[i + 1][j] = vehicle;
                                }
                                return true;
                            }
                        }
                        break;
                    case BUS:
                        for (int i = 0; i <= 0; i++) {
                            if (link.cells[i][0] == null && link.cells[i][1] == null
                                    && link.cells[i][2] == null && link.cells[i][3] == null
                                    && link.cells[i][4] == null && link.cells[i][5] == null
                                    && link.cells[i][6] == null && link.cells[i][7] == null
                                    && link.cells[i + 1][0] == null && link.cells[i + 1][1] == null
                                    && link.cells[i + 1][2] == null && link.cells[i + 1][3] == null
                                    && link.cells[i + 1][4] == null && link.cells[i + 1][5] == null
                                    && link.cells[i + 1][6] == null && link.cells[i + 1][7] == null
                                    && link.cells[i + 2][0] == null && link.cells[i + 2][1] == null
                                    && link.cells[i + 2][2] == null && link.cells[i + 2][3] == null
                                    && link.cells[i + 2][4] == null && link.cells[i + 2][5] == null
                                    && link.cells[i + 2][6] == null && link.cells[i + 2][7] == null) {
                                vehicle.offset = 7;
                                vehicle.stripNo = i;
                                for (int j = 0; j <= 7; j++) {
                                    link.cells[i][j] = vehicle;
                                    link.cells[i + 1][j] = vehicle;
                                    link.cells[i + 2][j] = vehicle;
                                }
                                return true;

                            }
                        }
                        break;
                }
                break;
            case RIGHT:
                switch (vehicle.vehicleType) {
                    case BIKE:
                        for (int i = 5; i >= 5; i--) {
                            if (link.cells[i][0] == null && link.cells[i][1] == null) {
                                vehicle.offset = 1;
                                vehicle.stripNo = i;
                                link.cells[i][0] = vehicle;
                                link.cells[i][1] = vehicle;
                                return true;
                            }
                        }
                        break;
                    case AUTO:
                        for (int i = 5; i >= 5; i--) {
                            if (link.cells[i][0] == null && link.cells[i][1] == null
                                    && link.cells[i][2] == null
                                    && link.cells[i - 1][0] == null && link.cells[i - 1][1] == null
                                    && link.cells[i - 1][2] == null) {
                                vehicle.offset = 2;
                                vehicle.stripNo = i - 1;
                                for (int j = 0; j <= 2; j++) {
                                    link.cells[i][j] = vehicle;
                                    link.cells[i - 1][j] = vehicle;
                                }
                                return true;
                            }
                        }
                        break;
                    case CAR:
                        for (int i = 5; i >= 5; i--) {
                            if (link.cells[i][0] == null && link.cells[i][1] == null
                                    && link.cells[i][2] == null && link.cells[i][3] == null
                                    && link.cells[i - 1][0] == null && link.cells[i - 1][1] == null
                                    && link.cells[i - 1][2] == null && link.cells[i - 1][3] == null) {
                                vehicle.offset = 3;
                                vehicle.stripNo = i - 1;
                                for (int j = 0; j <= 3; j++) {
                                    link.cells[i][j] = vehicle;
                                    link.cells[i - 1][j] = vehicle;
                                }
                                return true;
                            }
                        }
                        break;
                    case BUS:
                        for (int i = 5; i >= 5; i--) {
                            if (link.cells[i][0] == null && link.cells[i][1] == null
                                    && link.cells[i][2] == null && link.cells[i][3] == null
                                    && link.cells[i][4] == null && link.cells[i][5] == null
                                    && link.cells[i][6] == null && link.cells[i][7] == null
                                    && link.cells[i - 1][0] == null && link.cells[i - 1][1] == null
                                    && link.cells[i - 1][2] == null && link.cells[i - 1][3] == null
                                    && link.cells[i - 1][4] == null && link.cells[i - 1][5] == null
                                    && link.cells[i - 1][6] == null && link.cells[i - 1][7] == null
                                    && link.cells[i - 2][0] == null && link.cells[i - 2][1] == null
                                    && link.cells[i - 2][2] == null && link.cells[i - 2][3] == null
                                    && link.cells[i - 2][4] == null && link.cells[i - 2][5] == null
                                    && link.cells[i - 2][6] == null && link.cells[i - 2][7] == null) {
                                vehicle.offset = 7;
                                vehicle.stripNo = i - 2;
                                for (int j = 0; j <= 7; j++) {
                                    link.cells[i][j] = vehicle;
                                    link.cells[i - 1][j] = vehicle;
                                    link.cells[i - 2][j] = vehicle;
                                }
                                return true;
                            }
                        }
                        break;
                }
                break;
        }
        return false;
    }

    public void cleanResource() {
        network = Global.getNetwork();
        List<Link> links = network.getLinks();
        //clean all resources on link
        for (Link link : links) {
            link.cleanResources();
        }
        List<Generator> generators = network.getGenerators();
        //clean all resources on generator
        for (Generator generator : generators) {
            generator.cleanResources();
        }
        List<VirtualLink> virtualLinks = network.getVirtualLink();
        //clean all resources on connector
        for (VirtualLink virtual : virtualLinks) {
            virtual.cleanResources();
        }
    }
}
