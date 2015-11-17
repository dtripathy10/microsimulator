package edu.iitb.civil.tse.gui;

import com.jogamp.opengl.util.FPSAnimator;
import edu.iitb.civil.tse.apps.Global;
import edu.iitb.civil.tse.controller.UserAction;
import edu.iitb.civil.tse.controller.ViewerController;
import edu.iitb.civil.tse.gui.panel.opengl.OpenGLPanel;
import edu.iitb.civil.tse.gui.panel.opengl.Point3D;
import edu.iitb.civil.tse.gui.toolBar.EastToolBar;
import edu.iitb.civil.tse.gui.toolBar.StatusBar;
import edu.iitb.civil.tse.gui.toolBar.ToolBar;
import edu.iitb.civil.tse.network.Generator;
import edu.iitb.civil.tse.network.Link;
import edu.iitb.civil.tse.network.Movement;
import edu.iitb.civil.tse.network.Network;
import edu.iitb.civil.tse.network.Unsignalised;
import edu.iitb.civil.tse.vehicle.TurningMovement;
import edu.iitb.civil.tse.vehicle.VehicleType;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.HashMap;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.awt.GLJPanel;
import javax.swing.*;

/**
 * Testing the full-screen mode
 */
public final class Editor extends JFrame {
	// Windowed mode settings

	Editor frame;
	ViewerController vc;
	private static String winModeTitle = "JAYANT Traffic Simulator";
	// ////////////////////////Full
	// screen//////////////////////////////////////////////////
	private static int winModeX, winModeY; // top-left corner (x, y)
	private static int winModeWidth, winModeHeight; // width and height
	private boolean inFullScreenMode; // in fullscreen or windowed mode?
	private boolean fullScreenSupported; // is fullscreen supported?
	private GraphicsDevice defaultScreen;
	// /////////////////////////////////////////////////////////////////////////////////////
	private OpenGLPanel jrender;
	private GLJPanel canvas;
	private ToolBar toolBar;
	public FPSAnimator animator;
	// /////////////////////////////////////////////////////////////////////////////////////
	private Network network = new Network();
	public HashMap<VehicleType, Integer> propertionOfVehicle = new HashMap();
	public HashMap<TurningMovement, Integer> turningMovementOfVehicle = new HashMap();
	public double arrivalRate = 0.9;
	public double averageHeadway = 5;
	public IOUtility ioUtility = new IOUtility();
	StatusBar statusBar;

	/**
	 * Constructor to setTopComponent up the GUI components
	 */
	public Editor() {
		setExtendedState(Frame.MAXIMIZED_BOTH);
		getContentPane().setLayout(new BorderLayout());
		vc = Global.getViewerController();
		toolBar = new ToolBar();

		EastToolBar jt = new EastToolBar();
		getContentPane().add(toolBar, BorderLayout.NORTH);
		getContentPane().add(jt, BorderLayout.WEST);
		Global.toolbar = toolBar;
		statusBar = new StatusBar();
		getContentPane().add(statusBar, BorderLayout.SOUTH);
		Global.setStatusBar(statusBar);
		// Set icon
		ImageIcon icon = createImageIcon("/ico/donate.png", "a pretty but meaningless splat");
		this.setIconImage(icon.getImage());
		//
		frame = this;
		Global.setTopComponent(frame);
		jrender = new OpenGLPanel(vc);
		canvas = new GLJPanel();
		canvas.addGLEventListener(jrender);
		canvas.addMouseListener(jrender);
		canvas.addMouseMotionListener(jrender);
		canvas.addMouseWheelListener(jrender);
		getContentPane().add(canvas, BorderLayout.CENTER);
		animator = new FPSAnimator(canvas, 20, true);

		canvas.requestFocus();
		canvas.display();

		// Check if full screen mode supported?
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		defaultScreen = env.getDefaultScreenDevice();
		fullScreenSupported = defaultScreen.isFullScreenSupported();
		// menu item

		// Set the windowed mode initial width and height to about fullscreen
		// Get the screen width and height
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		winModeWidth = (int) dim.getWidth();
		winModeHeight = (int) dim.getHeight() - 35; // minus task bar
		winModeX = 0;
		winModeY = 0;
		init();
		if (!fullScreenSupported) {
			setUndecorated(false);
			setResizable(true);
			defaultScreen.setFullScreenWindow(this); // full-screen mode
			inFullScreenMode = true;
		} else {
			setUndecorated(false);
			setResizable(true);
			defaultScreen.setFullScreenWindow(null); // windowed mode
			setBounds(winModeX, winModeY, winModeWidth, winModeHeight);
			inFullScreenMode = false;
		}
		Global.setNetwork(network);
		frame.setTitle(winModeTitle);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	public final void init() {
		JMenuBar menuBar = new JMenuBar();

		JMenu menu = new JMenu("File");
		menu.setMnemonic('F');
		JMenuItem item;
		// -------------------------------------------New----------------------------------------------------//
		// -------------------------------------------START----------------------------------------------------//
		item = new JMenuItem("New");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (network.isNetworkEmpty()) {
					createNetwork();
				} else {
					int result = JOptionPane.showConfirmDialog(null, "Do we Want to delete?", "New",
							JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
					if (result == JOptionPane.YES_OPTION) {
						createNetwork();
					}
				}

			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		menu.add(item);
		// -------------------------------------------END----------------------------------------------------//
		menu.addSeparator();
		// -------------------------------------------Open----------------------------------------------------//
		// -------------------------------------------START----------------------------------------------------//
		item = new JMenuItem("Open");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new MyFilter());
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				int returnVal = chooser.showOpenDialog(Global.getEditor());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					ioUtility.openNewNetworkData(chooser.getSelectedFile().getParent());
					System.out.println("You chose to open this file: " + chooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		menu.add(item);
		// -------------------------------------------END----------------------------------------------------//
		menu.addSeparator();
		// -------------------------------------------Save----------------------------------------------------//
		// -------------------------------------------START----------------------------------------------------//
		item = new JMenuItem("Save");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (ioUtility.getCurrentDirectory() == null) {
					JFileChooser chooser = new JFileChooser();
					chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int returnVal = chooser.showSaveDialog(Global.getEditor());
					if (returnVal == JFileChooser.APPROVE_OPTION) {
						ioUtility.saveNewNetworkData(chooser.getSelectedFile().getAbsolutePath());
					}
				} else {
					ioUtility.saveNetworkData();
				}
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		menu.add(item);
		// -------------------------------------------END----------------------------------------------------//
		// -------------------------------------------Save
		// As----------------------------------------------------//
		// -------------------------------------------START----------------------------------------------------//
		item = new JMenuItem("Save As");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = chooser.showSaveDialog(Global.getEditor());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					ioUtility.saveNewNetworkData(chooser.getSelectedFile().getAbsolutePath());
				}
			}
		});
		menu.add(item);
		// -------------------------------------------END----------------------------------------------------//
		menu.addSeparator();
		// -------------------------------------------QUIT----------------------------------------------------//
		// -------------------------------------------START----------------------------------------------------//
		item = new JMenuItem("Quit");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread() {
					@Override
					public void run() {
						animator.stop(); // stop the animator loop
						System.exit(0);
					}
				}.start();
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
		menu.add(item);
		// -------------------------------------------END----------------------------------------------------//
		menuBar.add(menu);
		// -------------------------------------------END----------------------------------------------------//
		menu = new JMenu("View");
		// -------------------------------------------QUIT----------------------------------------------------//
		// -------------------------------------------START----------------------------------------------------//
		item = new JMenuItem("Roate View Point Down");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				vc.setViewPointVerticalAngle(vc.getViewPointVerticalAngle() + 5);
				if (vc.getViewPointVerticalAngle() > 85) {
					vc.setViewPointVerticalAngle(85);
				}
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke("DOWN"));
		menu.add(item);
		// -------------------------------------------END----------------------------------------------------//
		// -------------------------------------------QUIT----------------------------------------------------//
		// -------------------------------------------Roate View Point
		// Up----------------------------------------------------//
		item = new JMenuItem("Roate View Point Up");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				vc.setViewPointVerticalAngle(vc.getViewPointVerticalAngle() - 5);
				if (vc.getViewPointVerticalAngle() < 0) {
					vc.setViewPointVerticalAngle(0);
				}
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke("UP"));
		menu.add(item);
		// -------------------------------------------END----------------------------------------------------//
		// -------------------------------------------Roate View Point
		// Left----------------------------------------------------//
		// -----------------------------------------------START------------------------------------------------//
		item = new JMenuItem("Roate View Point Left");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				vc.setViewPointHorizontalAngle(vc.getViewPointHorizontalAngle() - 5);
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke("LEFT"));
		menu.add(item);
		// -------------------------------------------END----------------------------------------------------//
		// -------------------------------------------Roate View Point
		// Right----------------------------------------------------//
		// -------------------------------------------START----------------------------------------------------//
		item = new JMenuItem("Roate View Point Right");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				vc.setViewPointHorizontalAngle(vc.getViewPointHorizontalAngle() + 5);
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke("RIGHT"));
		menu.add(item);
		// -------------------------------------------END----------------------------------------------------//
		menu.addSeparator();
		// -------------------------------------------Zoom
		// In----------------------------------------------------//
		// -------------------------------------------START----------------------------------------------------//
		item = new JMenuItem("Zoom In");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				vc.setViewerZoomLocation(vc.getViewerZoomLocation() + 10);
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke("Z"));
		menu.add(item);
		// -------------------------------------------END----------------------------------------------------//
		// -------------------------------------------Zoom
		// Out----------------------------------------------------//
		// -------------------------------------------START----------------------------------------------------//
		item = new JMenuItem("Zoom Out");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				vc.setViewerZoomLocation(vc.getViewerZoomLocation() - 10);
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke("X"));
		menu.add(item);

		menu.addSeparator();
		// -------------------------------------------END----------------------------------------------------//
		// -------------------------------------------Move Left In
		// X----------------------------------------------------//
		// -------------------------------------------START----------------------------------------------------//
		item = new JMenuItem("Move Left In X");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				vc.setViewerXLocation(vc.getViewerXLocation() - 10);
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke("A"));
		menu.add(item);
		// -------------------------------------------END----------------------------------------------------//
		// -------------------------------------------Move Right In
		// X----------------------------------------------------//
		// -------------------------------------------START----------------------------------------------------//
		item = new JMenuItem("Move Right In X");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				vc.setViewerXLocation(vc.getViewerXLocation() + 10);
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke("S"));
		menu.add(item);
		// -------------------------------------------END----------------------------------------------------//
		// -------------------------------------------Move Up In
		// Y----------------------------------------------------//
		// -------------------------------------------START----------------------------------------------------//
		item = new JMenuItem("Move Up In Y");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				vc.setViewerYLocation(vc.getViewerYLocation() - 10);
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke("Q"));
		menu.add(item);
		// -------------------------------------------END----------------------------------------------------//
		// -------------------------------------------Move Down In
		// Y----------------------------------------------------//
		// -------------------------------------------START----------------------------------------------------//
		item = new JMenuItem("Move Down In Y");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				vc.setViewerYLocation(vc.getViewerYLocation() + 10);
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke("W"));
		menu.add(item);
		// -------------------------------------------END----------------------------------------------------//
		// -------------------------------------------Full Screen
		// F----------------------------------------------------//
		// -------------------------------------------START----------------------------------------------------//
		item = new JMenuItem("Full Screen  F");

		item.setAccelerator(KeyStroke.getKeyStroke("F"));
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!inFullScreenMode) {
					setVisible(false);
					setResizable(false);
					dispose();
					setUndecorated(true);
					defaultScreen.setFullScreenWindow(Editor.this);
					setVisible(true);
					inFullScreenMode = true;
				} else {
					setVisible(false);
					dispose();
					setUndecorated(false);
					setResizable(true);
					defaultScreen.setFullScreenWindow(null);
					setBounds(winModeX, winModeY, winModeWidth, winModeHeight);
					setVisible(true);
					inFullScreenMode = false;
				}
			}
		});
		menu.add(item);
		// -------------------------------------------END----------------------------------------------------//
		// -------------------------------------------Modify
		// Link----------------------------------------------------//
		// -------------------------------------------START----------------------------------------------------//

		item = new JMenuItem("Decrease Ground Size");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				vc.setGroundWidth(vc.getGroundWidth() - 50);
				vc.setGroundLength(vc.getGroundLength() - 50);
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke("MINUS"));
		menu.add(item);
		// -------------------------------------------END----------------------------------------------------//
		// -------------------------------------------Modify
		// Link----------------------------------------------------//
		// -------------------------------------------START----------------------------------------------------//
		item = new JMenuItem("Increase Ground Size");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				vc.setGroundWidth(vc.getGroundWidth() + 50);
				vc.setGroundLength(vc.getGroundLength() + 50);
			}
		});
		item.setAccelerator(KeyStroke.getKeyStroke("EQUALS"));
		menu.add(item);
		// -------------------------------------------END----------------------------------------------------//
		menuBar.add(menu);
		// -------------------------------------------END----------------------------------------------------//
		menu = new JMenu("Network Editor");
		menu.setMnemonic('M');
		// -------------------------------------------Create
		// Generator----------------------------------------------------//
		// -------------------------------------------START----------------------------------------------------//
		item = new JMenuItem("Create Generator");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Global.getViewerController().setCurrentAction(UserAction.CREATEGENERATOR);
			}
		});
		// shortcut key here.
		menu.add(item);
		// -------------------------------------------END----------------------------------------------------//
		// -------------------------------------------Create Unsignalised
		// Node----------------------------------------------------//
		// -------------------------------------------START----------------------------------------------------//
		item = new JMenuItem("Create Unsignalised Node");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Global.getViewerController().setCurrentAction(UserAction.CREATEUNSIGNALISED);
			}
		});
		// shortcut key here.
		menu.add(item);
		// -------------------------------------------END----------------------------------------------------//
		// -------------------------------------------Create
		// Link----------------------------------------------------//
		// -------------------------------------------START----------------------------------------------------//
		item = new JMenuItem("Create Link");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Global.getViewerController().setCurrentAction(UserAction.CREATELINK);
			}
		});
		// shortcut key here.
		menu.add(item);
		// -------------------------------------------END----------------------------------------------------//
		// -------------------------------------------Modify
		// Generator----------------------------------------------------//
		// -------------------------------------------START----------------------------------------------------//
		item = new JMenuItem("Modify Generator");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Global.getViewerController().setCurrentAction(UserAction.VIEWGENERATOR);
			}
		});
		// shortcut key here.
		menu.add(item);
		// -------------------------------------------END----------------------------------------------------//
		menu.addSeparator();
		// -------------------------------------------Modify Unsignalised
		// Node----------------------------------------------------//
		// -------------------------------------------START----------------------------------------------------//
		item = new JMenuItem("Modify Unsignalised Node");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Global.getViewerController().setCurrentAction(UserAction.VIEWUNSIGNALISED);
			}
		});
		// shortcut key here.
		menu.add(item);
		// -------------------------------------------END----------------------------------------------------//
		menu.addSeparator();
		// -------------------------------------------Modify
		// Link----------------------------------------------------//
		// -------------------------------------------START----------------------------------------------------//
		item = new JMenuItem("Modify Link");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Global.getViewerController().setCurrentAction(UserAction.VIEWLINK);
				// prompt for road properties
			}
		});
		// shortcut key here.
		menu.add(item);

		menuBar.add(menu);
		// -------------------------------------------END----------------------------------------------------//

		menu = new JMenu("Simulator");

		menu.setMnemonic('S');

		item = new JMenuItem("Run");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Global.getViewerController().setCurrentAction(UserAction.RUN);
			}
		});
		menu.add(item);
		item = new JMenuItem("Pause");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Global.getViewerController().setCurrentAction(UserAction.PAUSE);
			}
		});
		menu.add(item);
		item = new JMenuItem("Stop");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Global.getViewerController().setCurrentAction(UserAction.STOP);
			}
		});
		menu.add(item);
		item = new JMenuItem("Reset");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Global.getViewerController().setCurrentAction(UserAction.RESET);
			}
		});
		menu.add(item);
		item = new JMenuItem("Recording");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Global.getViewerController().setCurrentAction(UserAction.RECORDING);
			}
		});
		menu.add(item);

		menuBar.add(menu);
		menu = new JMenu("Evaluation");

		menu.setMnemonic('S');

		item = new JMenuItem("X");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		menu.add(item);
		item = new JMenuItem("Y");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		menu.add(item);
		item = new JMenuItem("Z");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		menu.add(item);
		item = new JMenuItem("A");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			}
		});
		menu.add(item);

		menuBar.add(menu);
		menu = new JMenu("Help");

		menu.setMnemonic('H');

		item = new JMenuItem("About");

		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(frame, "Created by:\n Jayant Sangole, Debabrata Tripathy \n IIT Bombay",
						" JAYANT Traffic Simulator", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		menu.add(item);
		menuBar.add(menu);
		frame.setJMenuBar(menuBar);
	}

	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	private void createNetwork() {
		network = new Network();
		// //Generator -1
		// HashMap<VehicleType, Integer> propertionOfVehicle = new HashMap();
		// HashMap<TurningMovement, Integer>
		// propertionOfTurningMovementOfVehicle = new HashMap();
		// propertionOfVehicle.put(VehicleType.CAR, 25);
		// propertionOfVehicle.put(VehicleType.BIKE, 25);
		// propertionOfVehicle.put(VehicleType.BUS, 25);
		// propertionOfVehicle.put(VehicleType.AUTO, 25);
		// propertionOfTurningMovementOfVehicle.put(TurningMovement.LEFT, 0);
		// propertionOfTurningMovementOfVehicle.put(TurningMovement.RIGHT, 0);
		// propertionOfTurningMovementOfVehicle.put(TurningMovement.STRAIGHT,
		// 100);
		// Generator generator1 = new Generator("1", 0, 0, 100,
		// propertionOfVehicle, propertionOfTurningMovementOfVehicle);
		// network.addGeneratorNode(generator1);
		// //Generator -2
		// propertionOfVehicle = new HashMap();
		// propertionOfTurningMovementOfVehicle = new HashMap();
		// propertionOfVehicle.put(VehicleType.CAR, 25);
		// propertionOfVehicle.put(VehicleType.BIKE, 40);
		// propertionOfVehicle.put(VehicleType.BUS, 10);
		// propertionOfVehicle.put(VehicleType.AUTO, 25);
		// propertionOfTurningMovementOfVehicle.put(TurningMovement.LEFT, 0);
		// propertionOfTurningMovementOfVehicle.put(TurningMovement.RIGHT, 0);
		// propertionOfTurningMovementOfVehicle.put(TurningMovement.STRAIGHT,
		// 100);
		// Generator generator2 = new Generator("2", 600, 0, 0.9,
		// propertionOfVehicle, propertionOfTurningMovementOfVehicle);
		// network.addGeneratorNode(generator2);
		// //Generator - 3
		// propertionOfVehicle = new HashMap();
		// propertionOfTurningMovementOfVehicle = new HashMap();
		// propertionOfVehicle.put(VehicleType.CAR, 25);
		// propertionOfVehicle.put(VehicleType.BIKE, 25);
		// propertionOfVehicle.put(VehicleType.BUS, 25);
		// propertionOfVehicle.put(VehicleType.AUTO, 25);
		// propertionOfTurningMovementOfVehicle.put(TurningMovement.LEFT, 0);
		// propertionOfTurningMovementOfVehicle.put(TurningMovement.RIGHT, 0);
		// propertionOfTurningMovementOfVehicle.put(TurningMovement.STRAIGHT,
		// 100);
		// Generator generator3 = new Generator("3", 200, -200, 0.5,
		// propertionOfVehicle, propertionOfTurningMovementOfVehicle);
		// network.addGeneratorNode(generator3);
		// //Generator - 4
		// propertionOfVehicle = new HashMap();
		// propertionOfTurningMovementOfVehicle = new HashMap();
		// propertionOfVehicle.put(VehicleType.CAR, 25);
		// propertionOfVehicle.put(VehicleType.BIKE, 25);
		// propertionOfVehicle.put(VehicleType.BUS, 25);
		// propertionOfVehicle.put(VehicleType.AUTO, 25);
		// propertionOfTurningMovementOfVehicle.put(TurningMovement.LEFT, 20);
		// propertionOfTurningMovementOfVehicle.put(TurningMovement.RIGHT, 40);
		// propertionOfTurningMovementOfVehicle.put(TurningMovement.STRAIGHT,
		// 40);
		// Generator generator4 = new Generator("4", 200, 200, 0.95,
		// propertionOfVehicle, propertionOfTurningMovementOfVehicle);
		// network.addGeneratorNode(generator4);
		// //Generator -5
		// propertionOfVehicle = new HashMap();
		// propertionOfTurningMovementOfVehicle = new HashMap();
		// propertionOfVehicle.put(VehicleType.CAR, 25);
		// propertionOfVehicle.put(VehicleType.BIKE, 25);
		// propertionOfVehicle.put(VehicleType.BUS, 25);
		// propertionOfVehicle.put(VehicleType.AUTO, 25);
		// propertionOfTurningMovementOfVehicle.put(TurningMovement.LEFT, 0);
		// propertionOfTurningMovementOfVehicle.put(TurningMovement.RIGHT, 30);
		// propertionOfTurningMovementOfVehicle.put(TurningMovement.STRAIGHT,
		// 70);
		// Generator generator5 = new Generator("5", 400, 200, 0.5,
		// propertionOfVehicle, propertionOfTurningMovementOfVehicle);
		// network.addGeneratorNode(generator5);
		// //Generator -6
		// propertionOfVehicle = new HashMap();
		// propertionOfTurningMovementOfVehicle = new HashMap();
		// propertionOfVehicle.put(VehicleType.CAR, 25);
		// propertionOfVehicle.put(VehicleType.BIKE, 25);
		// propertionOfVehicle.put(VehicleType.BUS, 25);
		// propertionOfVehicle.put(VehicleType.AUTO, 25);
		// propertionOfTurningMovementOfVehicle.put(TurningMovement.LEFT, 30);
		// propertionOfTurningMovementOfVehicle.put(TurningMovement.RIGHT, 0);
		// propertionOfTurningMovementOfVehicle.put(TurningMovement.STRAIGHT,
		// 70);
		// Generator generator6 = new Generator("2", 400, -200, 0.5,
		// propertionOfVehicle, propertionOfTurningMovementOfVehicle);
		// network.addGeneratorNode(generator6);
		// //unsignalised Node
		// Unsignalised node4 = new Unsignalised("4", new Point3D(200, 0, 0));
		// network.addUnsignalisedNode(node4);
		//
		// //unsignalised Node
		// Unsignalised node8 = new Unsignalised("8", new Point3D(400, 0, 0));
		// network.addUnsignalisedNode(node8);
		//
		// //Creation of Link
		// Link linkAB = new Link(1 + "AB", generator1, node4, 2, 2.0,
		// Movement.LEFT, 0);
		// Link linkBA = new Link(1 + "BA", node4, generator1, 2, 1.0,
		// Movement.RIGHT, 0);
		// network.addLink(linkAB);
		// network.addLink(linkBA);
		//
		// linkAB = new Link(2 + "AB", generator2, node8, 2, 2.0, Movement.LEFT,
		// 0);
		// linkBA = new Link(2 + "BA", node8, generator2, 2, 1.0,
		// Movement.RIGHT, 0);
		// network.addLink(linkAB);
		// network.addLink(linkBA);
		//
		//
		// linkAB = new Link(3 + "AB", generator3, node4, 2, 2.0, Movement.LEFT,
		// 0);
		// linkBA = new Link(3 + "BA", node4, generator3, 2, 1.0,
		// Movement.RIGHT, 0);
		// network.addLink(linkAB);
		// network.addLink(linkBA);
		//
		// linkAB = new Link(4 + "AB", generator4, node4, 2, 2.0, Movement.LEFT,
		// 0);
		// linkBA = new Link(4 + "BA", node4, generator4, 2, 1.0,
		// Movement.RIGHT, 0);
		// network.addLink(linkAB);
		// network.addLink(linkBA);
		//
		// linkAB = new Link(5 + "AB", generator6, node8, 2, 2.0, Movement.LEFT,
		// 0);
		// linkBA = new Link(5 + "BA", node8, generator6, 2, 1.0,
		// Movement.RIGHT, 0);
		// network.addLink(linkAB);
		// network.addLink(linkBA);
		// linkAB = new Link(6 + "AB", generator5, node8, 2, 2.0, Movement.LEFT,
		// 0);
		// linkBA = new Link(6 + "BA", node8, generator5, 2, 1.0,
		// Movement.RIGHT, 0);
		// network.addLink(linkAB);
		// network.addLink(linkBA);
		// linkAB = new Link(7 + "AB", node4, node8, 2, 2.0, Movement.LEFT, 0);
		// linkBA = new Link(7 + "BA", node8, node4, 2, 1.0, Movement.RIGHT, 0);
		// network.addLink(linkAB);
		// network.addLink(linkBA);
		// Generator -1
		HashMap<VehicleType, Integer> propertionOfVehicle = new HashMap();
		HashMap<TurningMovement, Integer> propertionOfTurningMovementOfVehicle = new HashMap();
		propertionOfVehicle.put(VehicleType.CAR, 20);
		propertionOfVehicle.put(VehicleType.BIKE, 45);
		propertionOfVehicle.put(VehicleType.BUS, 10);
		propertionOfVehicle.put(VehicleType.AUTO, 25);
		propertionOfTurningMovementOfVehicle.put(TurningMovement.LEFT, 0);
		propertionOfTurningMovementOfVehicle.put(TurningMovement.RIGHT, 30);
		propertionOfTurningMovementOfVehicle.put(TurningMovement.STRAIGHT, 70);
		Generator generator1 = new Generator("1", 0, 0, 0.5, propertionOfVehicle, propertionOfTurningMovementOfVehicle);
		network.addGeneratorNode(generator1);
		// Generator -2
		propertionOfVehicle = new HashMap();
		propertionOfTurningMovementOfVehicle = new HashMap();
		propertionOfVehicle.put(VehicleType.CAR, 25);
		propertionOfVehicle.put(VehicleType.BIKE, 40);
		propertionOfVehicle.put(VehicleType.BUS, 10);
		propertionOfVehicle.put(VehicleType.AUTO, 25);
		propertionOfTurningMovementOfVehicle.put(TurningMovement.LEFT, 30);
		propertionOfTurningMovementOfVehicle.put(TurningMovement.RIGHT, 0);
		propertionOfTurningMovementOfVehicle.put(TurningMovement.STRAIGHT, 70);
		Generator generator2 = new Generator("2", 400, 0, 0.5, propertionOfVehicle,
				propertionOfTurningMovementOfVehicle);
		network.addGeneratorNode(generator2);
		// Generator - 3
		propertionOfVehicle = new HashMap();
		propertionOfTurningMovementOfVehicle = new HashMap();
		propertionOfVehicle.put(VehicleType.CAR, 25);
		propertionOfVehicle.put(VehicleType.BIKE, 40);
		propertionOfVehicle.put(VehicleType.BUS, 5);
		propertionOfVehicle.put(VehicleType.AUTO, 30);
		propertionOfTurningMovementOfVehicle.put(TurningMovement.LEFT, 20);
		propertionOfTurningMovementOfVehicle.put(TurningMovement.RIGHT, 80);
		propertionOfTurningMovementOfVehicle.put(TurningMovement.STRAIGHT, 0);
		Generator generator3 = new Generator("3", 200, -200, 0.5, propertionOfVehicle,
				propertionOfTurningMovementOfVehicle);
		network.addGeneratorNode(generator3);
		// unsignalised Node
		Unsignalised node4 = new Unsignalised("4", new Point3D(200, 0, 0));
		network.addUnsignalisedNode(node4);

		// Creation of Link
		Link linkAB = new Link(1 + "AB", generator1, node4, 2, 2.0, Movement.LEFT, 0);
		Link linkBA = new Link(1 + "BA", node4, generator1, 2, 1.0, Movement.RIGHT, 0);
		network.addLink(linkAB);
		network.addLink(linkBA);

		linkAB = new Link(2 + "AB", generator2, node4, 2, 2.0, Movement.LEFT, 0);
		linkBA = new Link(2 + "BA", node4, generator2, 2, 1.0, Movement.RIGHT, 0);
		network.addLink(linkAB);
		network.addLink(linkBA);

		linkAB = new Link(3 + "AB", generator3, node4, 2, 2.0, Movement.LEFT, 0);
		linkBA = new Link(3 + "BA", node4, generator3, 2, 1.0, Movement.RIGHT, 0);
		network.addLink(linkAB);
		network.addLink(linkBA);

		network.processLinkConnectivity();
		Global.setNetwork(network);
	}

	public void run() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				animator.start();
			}
		});
		System.out.println("Animator Running");
	}
}

class MyFilter extends javax.swing.filechooser.FileFilter {

	@Override
	public String getDescription() {
		return "*.jayant";
	}

	@Override
	public boolean accept(File file) {
		return (file.isFile() && file.getName().toLowerCase().endsWith(".jayant")) || file.isDirectory();
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
}
