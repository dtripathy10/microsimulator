/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.iitb.civil.tse.gui.toolBar;

import java.awt.*;
import javax.swing.*;

import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.metal.MetalSliderUI;

public class MyNewMetalSliderUI extends MetalSliderUI {
// Create our own slider UI
    public static ComponentUI createUI(JComponent c) {
        return new MyNewMetalSliderUI();
    }

//*******************HORIZONTAL MAJOR TICK*******************
    @Override
    public void paintMajorTickForHorizSlider(Graphics g, Rectangle tickBounds, int x) {
        int coordinateX = x;

        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            //Create color using RGB value(RED=255,GREEN=0,BLUE=0)
            //You can use Color picker above to get RGB value for color that you want
            Color majorTickColor = new Color(255, 0, 0);

            //Set color that will use to draw MAJOR TICK using created color
            g.setColor(majorTickColor);

            //Draw MAJOR TICK
            g.drawLine(coordinateX, 0, coordinateX, tickBounds.height);
        }
    }
//*******************HORIZONTAL MAJOR TICK*******************

//*******************HORIZONTAL MINOR TICK*******************
    @Override
    public void paintMinorTickForHorizSlider(Graphics g, Rectangle tickBounds, int x) {
        int coordinateX = x;

        if (slider.getOrientation() == JSlider.HORIZONTAL) {
            //Create color using RGB value(RED=12,GREEN=255,BLUE=0)
            //You can use Color picker above to get RGB value for color that you want
            Color majorTickColor = new Color(12, 255, 0);

            //Set color that will use to draw MINOR TICK using created color
            g.setColor(majorTickColor);

            //Draw MINOR TICK
            g.drawLine(coordinateX, 0, coordinateX, tickBounds.height / 2);
        }
    }
//*******************HORIZONTAL MINOR TICK*******************
}
