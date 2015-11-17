package edu.iitb.civil.tse.network;

import edu.iitb.civil.tse.geometry.Geometry;
import edu.iitb.civil.tse.gui.panel.opengl.Point3D;
import edu.iitb.civil.tse.network.algo.DijkstraAlgorithm;
import edu.iitb.civil.tse.vehicle.TurningMovement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Debabrata Tripathy
 */
public class Network {

    public Map<Unsignalised, EdgeList> unsignalised;
    public Map<Generator, EdgeList> generators;
    public Map<Node, EdgeList> nodes;
    public List<Link> links;
    public List<VirtualLink> virtualLink;

    public Network() {
        unsignalised = new HashMap();
        generators = new HashMap();
        nodes = new HashMap();
        links = new ArrayList<>();
        virtualLink = new ArrayList<>();
    }

    public void addUnsignalisedNode(Unsignalised unsignalise) {
        EdgeList e = new EdgeList();
        unsignalised.put(unsignalise, e);
        nodes.put(unsignalise, e);
    }

    public void addGeneratorNode(Generator generator) {
        EdgeList e = new EdgeList();
        generators.put(generator, e);
        nodes.put(generator, e);
    }

    public void addLink(Link link) {
        links.add(link);
        EdgeList e1 = nodes.get(link.getSource());
        e1.addOutEdgeList(link.getDestination(), link);
        EdgeList e2 = nodes.get(link.getDestination());
        e2.addInEdgeList(link.getSource(), link);

    }

    public Link getLink(String id) {
        for (Link link : links) {
            if (link.getId() == null ? id == null : link.getId().equals(id)) {
                return link;
            }
        }
        return null;
    }

    public void addUndirectedLink(Link link) {
    }

    public List<Link> getLinks() {
        return links;
    }

    public List<Generator> getGenerators() {
        return new ArrayList<>(generators.keySet());
    }

    public List<VirtualLink> getVirtualLink() {
        return virtualLink;
    }

    public List<Unsignalised> getUnsignalised() {
        return new ArrayList<>(unsignalised.keySet());
    }

    public List<Link> getOutLink(Generator generator) {
        EdgeList e = generators.get(generator);
        List<Link> outLink = new ArrayList();
        for (Link link : e.getOutEdgeList().values()) {
            outLink.add(link);
        }
        return outLink;
    }

    public List<Link> getOutLink(Unsignalised unsignalisedNode) {
        EdgeList e = unsignalised.get(unsignalisedNode);
        List<Link> outLink = new ArrayList();
        for (Link link : e.getOutEdgeList().values()) {
            outLink.add(link);
        }
        return outLink;
    }

    public List<Link> getInLink(Generator generator) {
        EdgeList e = generators.get(generator);
        List<Link> outLink = new ArrayList();
        for (Link link : e.getInEdgeList().values()) {
            outLink.add(link);
        }
        return outLink;
    }

    public List<Link> getInLink(Unsignalised unsignalisedNode) {
        EdgeList e = unsignalised.get(unsignalisedNode);
        List<Link> outLink = new ArrayList();
        for (Link link : e.getInEdgeList().values()) {
            outLink.add(link);
        }
        return outLink;
    }

    public void processLinkConnectivity() {
        //procedure for create the co_ordinate for link
        for (Link link : links) {
            switch (link.movement) {
                case LEFT:
                    Point3D sourcePoint = link.getSource().getPoint();
                    Point3D destinationPoint = link.getDestination().getPoint();
                    double length = Geometry.distance(sourcePoint, destinationPoint);
                    double angel = Geometry.getAbsoluteAngel(sourcePoint, destinationPoint);
                    Point3D result = Geometry.nextPointOnline(sourcePoint, angel, length - (3.6 * 2)-.5);
                    link.setCoordinate(sourcePoint, result);
                    break;
                case RIGHT:
                    sourcePoint = link.getSource().getPoint();
                    destinationPoint = link.getDestination().getPoint();
                    angel = Geometry.getAbsoluteAngel(sourcePoint, destinationPoint);
                    result = Geometry.nextPointOnline(sourcePoint, angel, (3.6 * 2)+.5);
                    link.setCoordinate(result, destinationPoint);
                    break;
            }
        }
        //procedure for create the virtual link
        for (Unsignalised unsignalisedNode : unsignalised.keySet()) {
            List<Link> outLinks = getOutLink(unsignalisedNode);
            List<Link> inLinks = getInLink(unsignalisedNode);
            for (Link in : inLinks) {
                for (Link out : outLinks) {
                    double sourceAngel = Geometry.getAbsoluteAngel(in.getSourcePoint(), in.getDestinaPoint());
                    double destinationAngel = Geometry.getAbsoluteAngel(out.getSource().getPoint(), out.getDestination().getPoint());
                    double angel = destinationAngel - sourceAngel;
                    if (angel < 0) {
                        angel = 360 + angel;
                    }
                    if (angel <= 20 && angel >= 0) {
                        LinkConnectivity linkConnectivity = new LinkConnectivity(in, out, TurningMovement.STRAIGHT);
                        unsignalisedNode.linkConnecivty.add(linkConnectivity);
                    }
                    if (angel > 20 && angel < 160) {
                        LinkConnectivity linkConnectivity = new LinkConnectivity(in, out, TurningMovement.LEFT);
                        unsignalisedNode.linkConnecivty.add(linkConnectivity);
                    }
                    if (angel >= 160 && angel <= 200) {
                        LinkConnectivity linkConnectivity = new LinkConnectivity(in, out, TurningMovement.UTURN);
                        unsignalisedNode.linkConnecivty.add(linkConnectivity);
                    }
                    if (angel > 200 && angel < 340) {
                        LinkConnectivity linkConnectivity = new LinkConnectivity(in, out, TurningMovement.RIGHT);
                        unsignalisedNode.linkConnecivty.add(linkConnectivity);
                    }
                    if (angel >= 340) {
                        LinkConnectivity linkConnectivity = new LinkConnectivity(in, out, TurningMovement.STRAIGHT);
                        unsignalisedNode.linkConnecivty.add(linkConnectivity);
                    }
                }
            }//for
            int counter = 0;
            for (LinkConnectivity linkConnectivity : unsignalisedNode.linkConnecivty) {
                counter++;
                VirtualLink tempvirtualLink = new VirtualLink(linkConnectivity.inLink, linkConnectivity.outLink, unsignalisedNode, linkConnectivity.turningMovement);
                tempvirtualLink.id = "" + counter + "";
                virtualLink.add(tempvirtualLink);
            }
        }
//        //print all link
//        System.out.println("\n\n\n");
//        System.out.println("PRINT ALL THE INFORMATION OF LINK");
//        System.out.println("\n\n\n");
//        for (Link link : links) {
//            System.out.print("LINK ID = " + link.id + "\t");
//            System.out.print("source Point = " + link.getSourcePoint() + "\t");
//            System.out.print("destination Point = " + link.getDestinaPoint() + "\t");
//            System.out.print("CELL SIZE" + link.CELLS + "\t");
//            System.out.println("\n");
//        }
        //procedure to coordiant the virtual Link
        for (VirtualLink vlink : virtualLink) {
            switch (vlink.turningMovement) {
                case LEFT:
                    double theta = Geometry.getAbsoluteAngel(vlink.inLink.getSourcePoint(), vlink.inLink.getDestinaPoint());
                    double length = Geometry.distance(vlink.inLink.getSourcePoint(), vlink.inLink.getDestinaPoint());
                    Point3D startPoint = Geometry.nextPointOnline(vlink.inLink.getSourcePoint(), theta, length - (1.2 * 3));
                    startPoint = Geometry.getLeftPointBOTTOM(vlink.inLink.getSourcePoint(), startPoint, 3 * 1.2);
                    Point3D centerPoint = Geometry.getLeftPointBOTTOM(vlink.inLink.getSourcePoint(), startPoint, 6 * 1.2);
                    vlink.setCoordinate1(startPoint, centerPoint);
                    break;
                case RIGHT:
                    //get 3*1.2 from left
                    startPoint = Geometry.getLeftPointBOTTOM(vlink.inLink.getSourcePoint(), vlink.inLink.getDestinaPoint(), 3 * 1.2);
                    centerPoint = Geometry.getRightPointBOTTOM(vlink.inLink.getSourcePoint(), vlink.inLink.getDestinaPoint(), 6 * 1.2);
                    vlink.setCoordinate1(startPoint, centerPoint);
                    break;
                case STRAIGHT:
                    vlink.noOfLane = vlink.inLink.lane;
                    vlink.setCoordinate(vlink.inLink.getDestinaPoint(), vlink.outLink.getSourcePoint());
                    break;

            }
        }
//        //print all virtual link
//        System.out.println("\n\n\n");
//        System.out.println("PRINT ALL THE INFORMATION OF VIRTUAL LINK");
//        System.out.println("\n\n\n");
//        for (VirtualLink link : virtualLink) {
//            if(link.turningMovement == TurningMovement.UTURN) {
//                continue;
//            }
//            System.out.print("VIRTUAL LINK ID = " + link.id + "\t");
//            System.out.print("SOURCE POINT = " + link.sourcePoint + "\t");
//            System.out.print("DESTINATION POINT = " + link.destinationPoint + "\t");
//            System.out.print("CENTER POINT = " + link.centerPoint + "\t");
//            System.out.print("CELL SIZE = " + link.CELLS + "\t");
//            System.out.print("MOVEMENT = " + link.turningMovement + "\t");
//            System.out.println("\n\nVIRTUAL LINK CELL ");
//            for (int i = 0; i < link.mappingCells.length; i++) {
//                for (int j = 0; j < link.mappingCells[i].length; j++) {
//                    System.out.print(link.mappingCells[i][j] + "\n");
//                }
//                System.out.println("\n\n\n");
//            }
//            System.out.println("\n");
//        }
        //do processing in UnsignalisedNode
        for (Unsignalised node : unsignalised.keySet()) {
            node.createBaseMatrix();
        }
//        for (Unsignalised node : unsignalised.keySet()) {
//            System.out.println("\n\nVIRTUAL LINK CELL ");
//            for (int i = 0; i < node.mappingCells.length; i++) {
//                for (int j = 0; j < node.mappingCells[i].length; j++) {
//                    System.out.print("( " + node.mappingCells[i][j].rowIndex + ",");
//                    System.out.print(+node.mappingCells[i][j].columnIndex + ")");
//                    System.out.print("\t\t");
//                }
//                System.out.println("\n\n\n");
//            }
//            System.out.println("\n");
//        }
        //create mapping to base matrix for all matrix
        //procedure to coordiant the virtual Link
        for (VirtualLink vlink : virtualLink) {
            vlink.doMapping();
        }
        //        //print all virtual link
        System.out.println("\n\n\n");
        System.out.println("PRINT ALL THE INFORMATION OF VIRTUAL LINK");
        System.out.println("\n\n\n");
        for (VirtualLink link : virtualLink) {
            if (link.turningMovement == TurningMovement.UTURN) {
                continue;
            }
            System.out.print("VIRTUAL LINK ID = " + link.id + "\t");
            System.out.print("SOURCE POINT = " + link.sourcePoint + "\t");
            System.out.print("DESTINATION POINT = " + link.destinationPoint + "\t");
            System.out.print("CENTER POINT = " + link.centerPoint + "\t");
            System.out.print("CELL SIZE = " + link.CELLS + "\t");
            System.out.print("MOVEMENT = " + link.turningMovement + "\t");
            System.out.println("\n\nVIRTUAL LINK CELL ");
            for (int i = 0; i < link.mappingCells.length; i++) {
                for (int j = 0; j < link.mappingCells[i].length; j++) {
                    System.out.print("{  ");
                    for (int k = 0; k < link.mappingCells[i][j].parentCell.size(); k++) {
                        System.out.print("(" + link.mappingCells[i][j].parentCell.get(k).rowIndex + ",");
                        System.out.print(+link.mappingCells[i][j].parentCell.get(k).columnIndex + ")");
                    }
                    System.out.print("  }");
                    System.out.print("\t");
                }
                System.out.println("\n\n\n");
            }
            System.out.println("\n");
        }
    }

    private boolean isLinkPresent(Node source, Node destination) {
        EdgeList e1 = nodes.get(source);
        System.out.println("e1" + e1);
        for (Node node : e1.getOutEdgeList().keySet()) {
            if (node.equals(destination)) {
                return true;
            }
        }
        return false;
    }

    public boolean isNetworkEmpty() {
        if (nodes.keySet().size() + links.size() == 0) {
            System.out.println(nodes.keySet().size() + links.size() == 0);
            return true;
        }
        return false;
    }

    public Path getShortestPath(Node source, Node destination) {
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(this);
        dijkstra.execute(source);
        Path path = new Path();
        List<Node> tempNodes = dijkstra.getPath(destination);
        List<Link> tempLinks = new ArrayList<>();
        if (tempNodes == null) {
            tempNodes = new ArrayList<>();
            path.setNodes(tempNodes);
            path.setLinks(tempLinks);
            return path;
        }
        tempLinks = getAllLinkFromRoute(tempNodes);
        path.setNodes(tempNodes);
        path.setLinks(tempLinks);
        return path;
    }

    private List<Link> getAllLinkFromRoute(List<Node> tempNodes) {
        return null;

    }

    public boolean isGeneratorPresent(Point3D point) {
        for (Generator generator : getGenerators()) {
            //if node is present, return true, otherwise break and return false
        }
        return false;
    }

    public boolean isUnsignalisedNodePresent(Point3D point) {
        for (Unsignalised unsignalisedNode : getUnsignalised()) {
            //if node is present, return true, otherwise break and return false
        }
        return false;
    }

    public boolean isLinkPresent(Point3D point) {
        for (Link link : getLinks()) {
            //if node is present, return true, otherwise break and return false
        }
        return false;
    }

    public Node getNode(String temp) {
        for (Node node : nodes.keySet()) {
            if (node.id.equals(temp)) {
                return node;
            }
        }
        return null;
    }

    public VirtualLink getVirtualLink(Unsignalised unNode, Link in, TurningMovement turningMovement) {
        for (VirtualLink vlink : virtualLink) {
            if ((vlink.inLink.id == null ? in.id == null : vlink.inLink.id.equals(in.id)) && (vlink.unsignalisedNode.id == null ? unNode.id == null : vlink.unsignalisedNode.id.equals(unNode.id)) && (vlink.turningMovement == turningMovement)) {
                return vlink;
            }
        }
        return null;
    }
}
