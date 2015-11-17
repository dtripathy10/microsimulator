package edu.iitb.civil.tse.controller;

import edu.iitb.civil.tse.apps.Global;
import edu.iitb.civil.tse.gui.toolBar.StatusBar;
import edu.iitb.civil.tse.microsimulator.Microsimulator;

/**
 *
 * @author Debabrata Tripathy
 */
public class MicrosimulationController {

    private boolean running = false;
    private boolean isPaused = false;
    private int timeStepCount = 1;
    private boolean pause = false;
    private Microsimulator microsimulator;

    public MicrosimulationController() {
    }

    public int getTime() {
        return timeStepCount;
    }

    public void startRunning() {
        running = true;
    }

    public void stopRunning() {
        running = false;
        isPaused = true;
    }

    public void run(StatusBar statusBar) {
        // set up a timing loop
        long startTime;
        long elapsedTime;
        long waitTime;

        if (!isPaused) {
            timeStepCount = 1;
        }

        if (isPaused) {
            isPaused = false;
        }

        while (running) {
            startTime = System.currentTimeMillis();
            // update status bar with status message
            //statusBar.setMessage("Simulator Running. Current Timestep: " + timeStepCount);
            System.out.println("TIMESTAMP  " + timeStepCount);
            Global.toolbar.setSimulationTime(timeStepCount);
            // if time step == max time then stop running
            if (timeStepCount == 1) {
                Global.getMicroSimulator().initialise();

            }
            if (timeStepCount == Global.getRunTime()) {
                running = false;
                isPaused = true;
                // display box saying sim stopped?
                break;
            }

            Global.getMicroSimulator().start(timeStepCount);


            timeStepCount++;
            // sleep until right time to continue
            elapsedTime = System.currentTimeMillis() - startTime;
            long ddd = (long) (1000/Global.getSimRate()-elapsedTime);
            if(ddd<0) {
                ddd = 0;
            }
            try {
                Thread.sleep(ddd);
            } catch (InterruptedException ie) {
            }
        }
        //statusBar.setMessage("Simulator Paused. Current Timestep: " + timeStepCount);
        System.out.println("Simulator Stopped");
    }

    void reset() {
        isPaused = false;
        System.out.println("Simulator Reset");
    }

    void pause() {
        running = false;
        isPaused = true;
    }

    public void test() {
        Microsimulator mi = new Microsimulator();
        mi.initialise();
        while (timeStepCount <= 3600) {
            System.out.println("TIMESTAMP  " + timeStepCount);
            mi.start(timeStepCount);
            timeStepCount++;
        }
        mi.cleanResource();
    }
}
