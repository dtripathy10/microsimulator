package edu.iitb.civil.tse.apps;

import edu.iitb.civil.tse.controller.ViewerController;
import edu.iitb.civil.tse.gui.Editor;
import edu.iitb.civil.tse.gui.toolBar.StatusBar;
import edu.iitb.civil.tse.gui.toolBar.ToolBar;
import edu.iitb.civil.tse.microsimulator.Microsimulator;
import edu.iitb.civil.tse.network.Network;

/**
 *
 * @author Debabrata Tripathy
 */
public class Global {

    private static double SIMULATIONRATE = 1;
    private static int STARTDELAY = 12000;
    private static int SIMULATIONTIME = 3600;
    private static Editor editor;
    private static Network network;
    private static ViewerController viewerController;
    private static StatusBar statusBar;
    private static Microsimulator microSimulator;
    public static ToolBar toolbar;

    public static void setTopComponent(Editor frame) {
        editor = frame;
    }

    public static void setNetwork(Network network) {
        Global.network = network;
    }

    public static Editor getEditor() {
        return editor;
    }

    public static Network getNetwork() {
        return network;
    }

    public static void setSimRate(int value) {
        SIMULATIONRATE = value;
    }

    public static void setStartDelay(int intValue) {
        STARTDELAY = intValue;
    }

    public static void setRunTime(int intValue) {
        SIMULATIONTIME = intValue;
    }

    public static int getRunTime() {
        return SIMULATIONTIME;
    }

    public static double getSimRate() {
        return SIMULATIONRATE;
    }

    public static int getStartDelay() {
        return STARTDELAY;
    }
    public static ViewerController getViewerController() {
        return viewerController;
    }

    public static void setViewerController(ViewerController viewerController) {
        Global.viewerController = viewerController;
    }

    public static void setStatusBar(StatusBar statusBar) {
        Global.statusBar = statusBar;
    }

    public static StatusBar getStatusBar() {
        return statusBar;
    }

    public static Microsimulator getMicroSimulator() {
        return microSimulator;
    }
     public static void setMicroSimulator(Microsimulator microsimulator) {
        Global.microSimulator = microsimulator;
    }

}
