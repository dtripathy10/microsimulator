
package edu.iitb.civil.tse.network;

import edu.iitb.civil.tse.gui.panel.opengl.Point3D;
import edu.iitb.civil.tse.vehicle.Vehicle;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dtripathy10
 */
public class MappingCell {
    Point3D topLeft;
    Point3D topRight;
    Point3D bottomLeft;
    Point3D bottomRight;
    int rowIndex;
    int columnIndex;
    List<MappingCell> parentCell = new ArrayList<>();
    @Override
    public String toString() {
        return "{" + "topLeft=" + topLeft + ", topRight=" + topRight + ", bottomLeft=" + bottomLeft + ", bottomRight=" + bottomRight + '}';
    }

}
