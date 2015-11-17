package edu.iitb.civil.tse.apps;

import edu.iitb.civil.tse.controller.ViewerController;
import edu.iitb.civil.tse.gui.Editor;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author Debabrata Tripathy
 */
public class Main {
    public static void main(String[] args) {
        //Create the editor
        // Run the GUI codes on the Event-Dispatching thread for thread safety
        try {
            // Set System L&F
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            // handle exception
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ViewerController viewerController = new ViewerController();
                Global.setViewerController(viewerController);
                Editor v = new Editor();
                v.run();
            }
        });
    }
}
