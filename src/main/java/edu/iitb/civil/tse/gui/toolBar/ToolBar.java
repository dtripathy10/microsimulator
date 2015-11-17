package edu.iitb.civil.tse.gui.toolBar;

import edu.iitb.civil.tse.apps.Global;
import edu.iitb.civil.tse.controller.UserAction;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.metal.MetalSliderUI;

public class ToolBar extends JPanel implements ActionListener {

    JToolBar leftToolBar, middleToolBar, rightToolBar;
    Border raisedbevel, blackline;
    Insets margins;
    ToolBarButton newFile, openFile, saveFile, zoom, refresh, help, node, link, nodeModification, edgeModification, run;
    private final ToolBarButton recording;
    JTextField leftTextField;

    public ToolBar() {

        blackline = BorderFactory.createLineBorder(Color.GRAY);
        this.setBorder(blackline);
        BorderLayout bl = new BorderLayout(2, 0);
        this.setLayout(bl);
        leftToolBar = new JToolBar();
        leftToolBar.setFloatable(false);
        leftToolBar.setRollover(true);
        raisedbevel = BorderFactory.createRaisedBevelBorder();
        //leftToolBar.setBorder(raisedbevel);
        margins = new Insets(0, 0, 0, 0);
        newFile = new ToolBarButton("ico/main/" + "newfile.png");
        newFile.setToolTipText("New");
        newFile.setMargin(margins);
        newFile.setActionCommand("new");
        newFile.addActionListener(this);
        leftToolBar.add(newFile);
        leftToolBar.addSeparator();

        openFile = new ToolBarButton("ico/main/" + "open.png");
        openFile.setToolTipText("Open");
        openFile.setMargin(margins);
        openFile.setActionCommand("open");
        openFile.addActionListener(this);
        leftToolBar.add(openFile);
        leftToolBar.addSeparator();



        saveFile = new ToolBarButton("ico/main/" + "save.png");
        saveFile.setToolTipText("Save");
        saveFile.setMargin(margins);
        saveFile.setActionCommand("save");
        saveFile.addActionListener(this);
        leftToolBar.add(saveFile);
        leftToolBar.addSeparator();

        zoom = new ToolBarButton("ico/main/" + "zoomOut.png");
        zoom.setToolTipText("Zoom");
        zoom.addActionListener(this);
        zoom.setActionCommand("zoom");
        zoom.setMargin(margins);
        leftToolBar.add(zoom);
        leftToolBar.addSeparator();

        help = new ToolBarButton("ico/main/" + "help.png");
        help.setToolTipText("Help");
        help.addActionListener(this);
        help.setActionCommand("help");
        leftToolBar.add(help);
        help.setMargin(margins);

        recording = new ToolBarButton("ico/main/" + "recording.png");
        recording.setToolTipText("Recording");
        recording.addActionListener(this);
        recording.setActionCommand("recording");
        recording.setMargin(margins);
        leftToolBar.add(recording);
        leftToolBar.addSeparator();


        this.add(leftToolBar, BorderLayout.WEST);


        rightToolBar = new JToolBar();
        rightToolBar.setFloatable(false);
        rightToolBar.setRollover(true);

        rightToolBar.addSeparator();
//        slider = new JSlider(0, 20, 10);
//        slider.setMajorTickSpacing(10);
//        slider.setMinorTickSpacing(5);
//        slider.setExtent(5);
//        slider.setPaintTicks(true);
//        slider.setPreferredSize(new Dimension(200, 5));
//        slider.setUI(new MyNewMetalSliderUI());
//        rightToolBar.add(slider);
//
//        rightToolBar.addSeparator();

        JLabel label = new JLabel("CURRENT SIMULATION TIME:");
        label.setFont(new Font("Serif", Font.BOLD, 12));
        //label.setOpaque(true);
        label.setForeground(Color.BLUE);
        //label.setBackground(new Color(0, 1, 0));
        rightToolBar.add(label);



        leftTextField = new JTextField();
        leftTextField.setPreferredSize(new Dimension(100, 10));
        //label.setOpaque(true);
        leftTextField.setForeground(Color.magenta);
        leftTextField.setFont(new Font("Serif", Font.BOLD, 12));
        rightToolBar.add(leftTextField);
        rightToolBar.addSeparator();

        // rightToolBar.setBorder(raisedbevel);
        run = new ToolBarButton("ico/main/" + "run.png");
        run.setToolTipText("Run");
        run.setActionCommand("run");
        run.addActionListener(this);
        run.setMargin(margins);
        rightToolBar.add(run);
        rightToolBar.addSeparator();
        run = new ToolBarButton("ico/main/" + "pause.png");
        run.setToolTipText("Pause");
        run.setActionCommand("pause");
        run.addActionListener(this);
        run.setMargin(margins);
        rightToolBar.add(run);
        rightToolBar.addSeparator();
        run = new ToolBarButton("ico/main/" + "stop.png");
        run.setToolTipText("Stop");
        run.setActionCommand("stop");
        run.addActionListener(this);
        run.setMargin(margins);
        rightToolBar.add(run);
        rightToolBar.addSeparator();
        run = new ToolBarButton("ico/main/" + "reset.png");
        run.setToolTipText("Reset");
        run.setActionCommand("reset");
        run.addActionListener(this);
        run.setMargin(margins);
        rightToolBar.add(run);
        rightToolBar.addSeparator();
        this.add(rightToolBar, BorderLayout.EAST);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub
        String x = e.getActionCommand();
        switch (x) {
            case "new":
                Global.getViewerController().setCurrentAction(UserAction.NEW);
                setActionMessage("" + Global.getViewerController().getCurrentAction() + "\t");
                break;
            case "open":
                Global.getViewerController().setCurrentAction(UserAction.OPEN);
                setActionMessage("" + Global.getViewerController().getCurrentAction() + "\t");
                break;
            case "save":
                Global.getViewerController().setCurrentAction(UserAction.SAVE);
                setActionMessage("" + Global.getViewerController().getCurrentAction() + "\t");
                break;
            case "zoom":
                Global.getViewerController().setCurrentAction(UserAction.ZOOM);
                setActionMessage("" + Global.getViewerController().getCurrentAction() + "\t");
                break;
            case "help":
                Global.getViewerController().setCurrentAction(UserAction.HELP);
                setActionMessage("" + Global.getViewerController().getCurrentAction() + "\t");
                break;
            case "recording":
                Global.getViewerController().setCurrentAction(UserAction.RECORDING);
                setActionMessage("" + Global.getViewerController().getCurrentAction() + "\t");
                break;
        }

        switch (x) {
            case "run":
                Global.getViewerController().setCurrentAction(UserAction.RUN);
                setActionMessage("" + Global.getViewerController().getCurrentAction() + "\t");
                break;
            case "pause":
                Global.getViewerController().setCurrentAction(UserAction.PAUSE);
                setActionMessage("" + Global.getViewerController().getCurrentAction() + "\t");
                break;
            case "stop":
                Global.getViewerController().setCurrentAction(UserAction.STOP);
                setActionMessage("" + Global.getViewerController().getCurrentAction() + "\t");
                break;
            case "reset":
                Global.getViewerController().setCurrentAction(UserAction.RESET);
                setActionMessage("" + Global.getViewerController().getCurrentAction() + "\t");
                break;
        }

    }

    private void setActionMessage(final String message) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            private String Message;

            @Override
            public void run() {
                Global.getStatusBar().setMessage(message);
            }
        });
    }

    public void setSimulationTime(int timestemp) {
        leftTextField.setText(formatIntoHHMMSS(timestemp));
    }

    static String formatIntoHHMMSS(int secsIn) {
        int hours = secsIn / 3600,
                remainder = secsIn % 3600,
                minutes = remainder / 60,
                seconds = remainder % 60;
        return ((hours < 10 ? "0" : "") + hours
                + "  :  " + (minutes < 10 ? "0" : "") + minutes
                + "  :  " + (seconds < 10 ? "0" : "") + seconds);

    }
}