package edu.iitb.civil.tse.gui;

import edu.iitb.civil.tse.apps.Global;
import edu.iitb.civil.tse.gui.panel.opengl.Point3D;
import edu.iitb.civil.tse.network.Generator;
import edu.iitb.civil.tse.network.Link;
import edu.iitb.civil.tse.network.Movement;
import edu.iitb.civil.tse.network.Network;
import edu.iitb.civil.tse.network.Node;
import edu.iitb.civil.tse.network.Unsignalised;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dtripathy10
 */
public class IOUtility {

    private Network network;
    private String currentDirectory;

    public String getCurrentDirectory() {
        return currentDirectory;
    }

    void saveNewNetworkData(String currentDirectory) {
        //set current directory
        this.currentDirectory = currentDirectory;
        //get the network
        this.network = Global.getNetwork();
        //create init file
        File initFile;
        /////////////////////////////////////////////////////////////////////////////////
        initFile = new File(currentDirectory + "\\" + "init.jayant");
        if (!initFile.exists()) {
            try {
                initFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(IOUtility.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream(initFile));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IOUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
        out.println("-----------------------------------------------");
        out.println("some control information will be written......");
        out.println("-----------------------------------------------");
        out.println("NODE -\t" + currentDirectory + "\\" + "node.dat");
        out.println("LINK -\t" + currentDirectory + "\\" + "link.dat");
        out.close();
        //////////////////////////////////init file write complete///////////////////////

        /////////////////////Node////////////////////////////////////////////////////
        initFile = new File(currentDirectory + "\\" + "node.dat");
        if (!initFile.exists()) {
            try {
                initFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(IOUtility.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            out = new PrintStream(new FileOutputStream(initFile));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IOUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
        out.print("NODE ID");
        out.print(",");
        out.print("X COORDINATE");
        out.print(",");
        out.print("Y COORDINATE");
        out.print(",");
        out.print("Z COORDINATE");
        out.print(",");
        out.print("NODE TYPE");
        out.println();
        for (Node node : network.nodes.keySet()) {
            out.print(node.id);
            out.print(",");
            out.print(node.xCordinate);
            out.print(",");
            out.print(node.yCoordianate);
            out.print(",");
            out.print(node.zCoordianate);
            out.print(",");
            out.print(node.nodeType);
            out.println();
        }
        out.close();
        //////////////////////////////////////////////////////////////////////////////

        /////////////////////Link////////////////////////////////////////////////////
        initFile = new File(currentDirectory + "\\" + "link.dat");
        if (!initFile.exists()) {
            try {
                initFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(IOUtility.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            out = new PrintStream(new FileOutputStream(initFile));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IOUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
        //LINKID	ANODE	BNODE	LANES_AB	SPEED_AB	LANES_BA	SPEED_BA
        out.print("LINKID");
        out.print(",");
        out.print("ANODE");
        out.print(",");
        out.print("BNODE");
        out.print(",");
        out.print("LANES_AB");
        out.print(",");
        out.print("SPEED_AB");
        out.print(",");
        out.print("LANES_BA");
        out.print(",");
        out.print("SPEED_BA");
        out.println();
        for (Link link : network.links) {
            out.print(link.id);
            out.print(",");
            out.print(link.source.id);
            out.print(",");
            out.print(link.destination.id);
            out.print(",");
            out.print(3);
            out.print(",");
            out.print(32);
            out.print(",");
            out.print(4);
            out.print(",");
            out.print(43);
            out.println();
        }
        out.close();
        //////////////////////////////////////////////////////////////////////////////

    }

    void saveNetworkData() {
        //get the network
        this.network = Global.getNetwork();
        //create init file
        File initFile;
        /////////////////////////////////////////////////////////////////////////////////
        initFile = new File(currentDirectory + "\\" + "init.jayant");
        if (!initFile.exists()) {
            try {
                initFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(IOUtility.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        PrintStream out = null;
        try {
            out = new PrintStream(new FileOutputStream(initFile));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IOUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
        out.println("-----------------------------------------------");
        out.println("some control information will be written......");
        out.println("-----------------------------------------------");
        out.println("Node -\t" + currentDirectory + "\\" + "node.dat");
        out.println("Link -\t" + currentDirectory + "\\" + "link.dat");
        out.close();
        //////////////////////////////////init file write complete///////////////////////

        /////////////////////Node////////////////////////////////////////////////////
        initFile = new File(currentDirectory + "\\" + "node.dat");
        if (!initFile.exists()) {
            try {
                initFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(IOUtility.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            out = new PrintStream(new FileOutputStream(initFile));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IOUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
        out.print("NODE ID");
        out.print(",");
        out.print("X COORDINATE");
        out.print(",");
        out.print("Y COORDINATE");
        out.print(",");
        out.print("Z COORDINATE");
        out.print(",");
        out.print("NODE TYPE");
        out.println();
        for (Node node : network.nodes.keySet()) {
            out.print(node.id);
            out.print(",");
            out.print(node.xCordinate);
            out.print(",");
            out.print(node.yCoordianate);
            out.print(",");
            out.print(node.zCoordianate);
            out.print(",");
            out.print(node.nodeType);
            out.println();
        }
        out.close();
        //////////////////////////////////////////////////////////////////////////////

        /////////////////////Link////////////////////////////////////////////////////
        initFile = new File(currentDirectory + "\\" + "link.dat");
        if (!initFile.exists()) {
            try {
                initFile.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(IOUtility.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            out = new PrintStream(new FileOutputStream(initFile));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IOUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
        //LINKID	ANODE	BNODE	LANES_AB	SPEED_AB	LANES_BA	SPEED_BA
        out.print("LINKID");
        out.print(",");
        out.print("ANODE");
        out.print(",");
        out.print("BNODE");
        out.print(",");
        out.print("LANES_AB");
        out.print(",");
        out.print("SPEED_AB");
        out.print(",");
        out.print("LANES_BA");
        out.print(",");
        out.print("SPEED_BA");
        out.println();
        for (Link link : network.links) {
            out.print(link.id);
            out.print(",");
            out.print(link.source.id);
            out.print(",");
            out.print(link.destination.id);
            out.print(",");
            out.print(3);
            out.print(",");
            out.print(32);
            out.print(",");
            out.print(4);
            out.print(",");
            out.print(43);
            out.println();
        }
        out.close();
        //////////////////////////////////////////////////////////////////////////////
    }

    void openNewNetworkData(String absolutePath) {
        //set current directory
        this.currentDirectory = absolutePath;
        //get the network
        this.network = Global.getNetwork();

        ////////////////////////////////////////////////////////////////////////////////
        Map<UserFileType, String> userfileLocation = new HashMap<>();
        Scanner scan = null;
        try {
            scan = new Scanner(new File(currentDirectory + "\\" + "init.jayant"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(IOUtility.class.getName()).log(Level.SEVERE, null, ex);
        }
        int counter = 0;
        while (scan.hasNextLine()) {
            if (counter > 2) {
                String value = scan.nextLine();
                String[] str_array = value.split("-");
                String type = str_array[0].trim();
                String path = str_array[1].trim();
                switch (type) {
                    case "NODE":
                        userfileLocation.put(UserFileType.NODE, path);
                        break;
                    case "LINK":
                        userfileLocation.put(UserFileType.LINK, path);
                        break;
                    case "GENERATOR":
                        break;
                    case "UNSGNALISED":
                        break;

                }//switch
            }//if
            else {
                scan.nextLine();
                counter++;

            }
        }//while
        scan.close();
        for (Map.Entry<UserFileType, String> entry : userfileLocation.entrySet()) {
            UserFileType userFileType = entry.getKey();
            String string = entry.getValue();
            switch (userFileType) {
                case NODE:
                    readNodeFile(string);
                    break;
                case LINK:
                    readLinkFile(string);
                    break;
            }

        }

    }

    private void readNodeFile(String fileName) {
        List<String> stringlist = new ArrayList();
        List<List> list = new ArrayList();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String strLine = null;
            StringTokenizer st = null;
            int lineNumber = 0, tokenNumber = 0;
            while ((fileName = br.readLine()) != null) {
                lineNumber++;
                //break comma separated line using ","
                st = new StringTokenizer(fileName, ",");
                while (st.hasMoreTokens()) {
                    //display csv values
                    tokenNumber++;
                    if (lineNumber == 1) {
                        //do nothing
                        st.nextToken();
                    } else {
                        stringlist.add(st.nextToken());
                    }
                }
                //reset token number
                list.add(stringlist);
                stringlist = new ArrayList<>();
                tokenNumber = 0;

            }
            br.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        String id = null, nodeType;
        double xCoordinate = 0, yCoordinate = 0, zCoordinate = 0;
        for (List list1 : list) {
            for (int i = 0; i < list1.size(); i++) {
                String temp = (String) list1.get(i);

                switch (i) {
                    case 0:
                        id = temp;
                        break;
                    case 1:
                        xCoordinate = new Double(temp);
                        System.out.println(xCoordinate);
                        break;
                    case 2:
                        yCoordinate = new Double(temp);
                        break;
                    case 3:
                        zCoordinate = new Double(temp);
                        break;
                    case 4:
                        nodeType = temp;
                        switch (nodeType) {
                            case "GENERATOR":
                                Generator generator;
                                Point3D point = new Point3D(xCoordinate, yCoordinate, zCoordinate);
                                System.out.println(point);
                                generator = new Generator(id, point);
                                network.addGeneratorNode(generator);
                                System.out.println("Generator created");
                                break;
                            case "UNSIGNALISED":
                                Unsignalised unsignalised;
                                point = new Point3D(xCoordinate, yCoordinate, zCoordinate);
                                unsignalised = new Unsignalised(id, point);
                                network.addUnsignalisedNode(unsignalised);
                                System.out.println("Unsignal created");
                                break;

                        }
                        break;
                }//switch
            }//for
            System.out.println(list1.size());
        }
    }

    private void readLinkFile(String fileName) {
        List<String> stringlist = new ArrayList();
        List<List> list = new ArrayList();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String strLine = null;
            StringTokenizer st = null;
            int lineNumber = 0, tokenNumber = 0;
            while ((fileName = br.readLine()) != null) {
                lineNumber++;
                //break comma separated line using ","
                st = new StringTokenizer(fileName, ",");
                while (st.hasMoreTokens()) {
                    //display csv values
                    tokenNumber++;
                    if (lineNumber == 1) {
                        //do nothing
                        st.nextToken();
                    } else {
                        stringlist.add(st.nextToken());
                    }
                }
                //reset token number
                list.add(stringlist);
                stringlist = new ArrayList<>();
                tokenNumber = 0;

            }
            br.close();
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }
        String linkId = null;
        //LINKID,ANODE,BNODE,LANES_AB,SPEED_AB,LANES_BA,SPEED_BA

        for (List list1 : list) {
            Node sourceNode = null, destinationNode = null;
            int lanesAB = 0, lanesBA = 0;
            double speedAB = 0, speedBA = 0;
            for (int i = 0; i < list1.size(); i++) {
                String temp = (String) list1.get(i);
                switch (i) {
                    case 0:
                        linkId = temp;
                        break;
                    case 1:
                        sourceNode = network.getNode(temp);
                        break;
                    case 2:
                        destinationNode = network.getNode(temp);
                        break;
                    case 3:
                        lanesAB = new Integer(temp);
                        break;
                    case 4:
                        speedAB = new Double(temp);
                        break;
                    case 5:
                        lanesBA = new Integer(temp);
                        break;
                    case 6:
                        speedBA = new Double(temp);
                        break;
                }//switch

            }//for
            if (sourceNode == null || destinationNode == null) {
                System.out.println("ERROR");
            } else {
                //create link A-B
                Link linkAB = new Link(linkId + "AB", sourceNode, destinationNode, lanesAB, speedAB, Movement.LEFT,0);
                Link linkBA = new Link(linkId + "BA", destinationNode, sourceNode, lanesBA, speedBA, Movement.RIGHT,0);
                network.addLink(linkAB);
                network.addLink(linkBA);
                sourceNode = null;
                destinationNode = null;
            }
        }
    }
}
