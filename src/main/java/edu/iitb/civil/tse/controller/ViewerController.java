package edu.iitb.civil.tse.controller;

import edu.iitb.civil.tse.apps.Global;
import edu.iitb.civil.tse.microsimulator.Microsimulator;
import edu.iitb.civil.tse.network.Network;
import javax.swing.JOptionPane;

public class ViewerController {

    private float groundWidth = 1800;
    private float groundLength = 1800;
    private float viewerZoomLocation = 800;
    private float viewPointVerticalAngle = 0;
    private float viewPointHorizontalAngle = 0;
    private float viewerXLocation = 0;
    private float viewerYLocation = 0;
    private Network network;
    private UserAction currentAction = UserAction.NOTHING;
    private MicrosimulationController microSimulationController;

    public ViewerController() {
        network = Global.getNetwork();
        microSimulationController = new MicrosimulationController();
        System.out.println("Viewer Controller Created");
    }

    public void setCurrentAction(UserAction currentAction) {
        this.currentAction = currentAction;
        switch (currentAction) {
            case NEW:
                test("NEW");
                break;
            case OPEN:
                test("OPEN");
                break;
            case SAVE:
                test("SAVE");
                break;
            case ZOOM:
                test("ZOOM");
                break;
            case HELP:
                test("HELP");
                break;
            case RECORDING:
                test("RECORDING");
                break;
            case RUN:
                Global.setMicroSimulator(new Microsimulator());
                microSimulationController.startRunning();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        microSimulationController.run(null);
                    }
                }).start();
                break;
            case PAUSE:
                microSimulationController.pause();
                break;
            case STOP:
                test("STOP");
                break;
            case RESET:
                test("RESET");
                break;
        }
    }

    public UserAction getCurrentAction() {
        return currentAction;
    }

    public void setGroundWidth(float value) {
        groundWidth = value;
    }

    public float getGroundWidth() {
        return groundWidth;
    }

    public void setGroundLength(float value) {
        groundLength = value;
    }

    public float getGroundLength() {
        return groundLength;
    }

    public void setViewerZoomLocation(float value) {
        viewerZoomLocation = value;
    }

    public float getViewerZoomLocation() {
        return viewerZoomLocation;
    }

    public void setViewPointVerticalAngle(float value) {
        viewPointVerticalAngle = value;
    }

    public float getViewPointVerticalAngle() {
        return viewPointVerticalAngle;
    }

    public void setViewPointHorizontalAngle(float value) {
        viewPointHorizontalAngle = value;
    }

    public float getViewPointHorizontalAngle() {
        return viewPointHorizontalAngle;
    }

    public void setViewerXLocation(float value) {
        viewerXLocation = value;
    }

    public float getViewerXLocation() {
        return viewerXLocation;
    }

    public void setViewerYLocation(float value) {
        viewerYLocation = value;
    }

    public float getViewerYLocation() {
        return viewerYLocation;
    }

    public void startSimulator() {
        microSimulationController.startRunning();
    }

    public void stopSimulator() {
        microSimulationController.stopRunning();
    }

    public void resetSimulator() {
        microSimulationController.reset();
    }

    public void test(final String point) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                if (point != null) {
                    JOptionPane.showMessageDialog(Global.getEditor(),
                            point);
                }
            }
        });
    }
}
