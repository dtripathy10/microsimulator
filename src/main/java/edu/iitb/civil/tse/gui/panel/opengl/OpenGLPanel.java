package edu.iitb.civil.tse.gui.panel.opengl;

import edu.iitb.civil.tse.apps.Global;
import edu.iitb.civil.tse.controller.ViewerController;
import edu.iitb.civil.tse.network.Generator;
import edu.iitb.civil.tse.network.Link;
import edu.iitb.civil.tse.network.Network;
import edu.iitb.civil.tse.network.Node;
import edu.iitb.civil.tse.network.NodeType;
import edu.iitb.civil.tse.network.Unsignalised;
import edu.iitb.civil.tse.network.VirtualLink;
import edu.iitb.civil.tse.vehicle.TurningMovement;
import edu.iitb.civil.tse.vehicle.Vehicle;
import edu.iitb.civil.tse.vehicle.VehicleType;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.List;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;

import javax.swing.JOptionPane;

public class OpenGLPanel implements GLEventListener, MouseListener, MouseMotionListener, MouseWheelListener {

	private double roadheight = 0.2;
	private double intersectionheight = 0.3;
	private double lanGAPwidth = .5;
	private double lanewidth = 1.2 * 3;
	private static double height = 0.2;// 1.5;
	private static final GLU glu = new GLU(); // Get a new OpenGL Utilties
												// Library
	private MouseEvent mouseClick = null;
	private MouseEvent mouseDrag = null;
	private MouseEvent mouseDown = null;
	private ViewerController VC;
	private Network network;
	// link contineous
	private boolean linkContineous;
	private Point3D sourcePoint, destinationPoint;
	private MouseEvent mousemoved;
	static GLMovie movie = new GLMovie();

	public OpenGLPanel(ViewerController vc) {
		VC = vc;
		System.out.println("OpenGLPanel Created");
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		gl.glClearDepth(1.0);
		gl.glEnable(GL.GL_LINE_SMOOTH);
		// gl.glEnable(GL.GL_BLEND);
		// gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		gl.glShadeModel(GL2.GL_FLAT);
		gl.glClearColor(0.25f, 0.25f, 0.25f, 0);
		// gl.glHint(GL.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
		// gl.glDisable(GL.GL_POLYGON_SMOOTH);
		// gl.glEnable(GL.GL_DEPTH_TEST);

	}

	@Override
	public void display(GLAutoDrawable drawable) {
		final GL2 gl = drawable.getGL().getGL2();
		// //////////////

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

		gl.glLoadIdentity();
		gl.glPushMatrix();
		gl.glTranslated(VC.getViewerXLocation(), VC.getViewerYLocation(), -VC.getViewerZoomLocation()); // set
																										// the
																										// viewer
																										// above
																										// the
																										// surface
		gl.glRotated(VC.getViewPointVerticalAngle(), -1, 0, 0);
		gl.glRotated(VC.getViewPointHorizontalAngle(), 0, 0, -1);
		// draw the ground

		// ///////
		gl.glColor3d(0, 1, 1);
		gl.glBegin(GL2.GL_QUADS);
		gl.glColor3d(1, 1, 1);
		gl.glVertex3d(-VC.getGroundLength() / 2, VC.getGroundWidth() / 2, 0);
		gl.glVertex3d(VC.getGroundLength() / 2, VC.getGroundWidth() / 2, 0);
		gl.glVertex3d(VC.getGroundLength() / 2, -VC.getGroundWidth() / 2, 0);
		gl.glVertex3d(-VC.getGroundLength() / 2, -VC.getGroundWidth() / 2, 0);
		gl.glEnd();
		if (mousemoved != null) {
			final Point3D point = calcGroundPoint(gl, mousemoved);

			java.awt.EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					Global.getStatusBar().setCordinate(point);
				}
			});
		}
		if (mouseClick != null) {

			final Point3D point = calcGroundPoint(gl, mouseClick);
			switch (VC.getCurrentAction()) {
			case CREATEGENERATOR:
				createGenerator(point);
				break;
			case CREATELINK:
				if (linkContineous == false) {
					linkContineous = true;
				}
				// createLink(point);
				break;
			case CREATEUNSIGNALISED:
				createUnsignalisedNode(point);
				break;
			case VIEWGENERATOR:
				viewGenerator(point);
				break;
			case VIEWLINK:
				viewLink(point);
				break;
			case VIEWUNSIGNALISED:
				viewUnsignalisedNode(point);
				break;
			case NOTHING:
				break;
			case RECORDING:
				movie.enabled = true;
			}

		}
		mouseClick = null;
		network = Global.getNetwork();

		// draw Links

		drawLinks(gl, network.getLinks());
		// drawVLinks(gl, network.getVirtualLink());
		// draw Node
		for (Node node : network.nodes.keySet()) {
			Point3D p = node.getPoint();
			// drawIntersection(gl, node);
		}
		gl.glPopMatrix();
		// movie.frame(gl);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
		if (height <= 0) {
			height = 1;
		}
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		// calculate the aspect ratio of the window
		glu.gluPerspective(45.0, (double) width / (double) height, 5, 2500);

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

	}

	// code based on:
	// http://www.java-tips.org/other-api-tips/jogl/how-to-use-gluunproject-in-jogl.html
	public Point3D calcGroundPoint(GL2 gl, MouseEvent e) {
		int viewport[] = new int[4];
		double mvmatrix[] = new double[16];
		double projmatrix[] = new double[16];
		int realy = 0; // GL y coord pos
		double wcoord0[] = new double[4]; // wx, wy, wz; // returned x,y,z
											// coords
		double wcoord1[] = new double[4];

		int x = e.getX();
		int y = e.getY();

		gl.glGetIntegerv(GL.GL_VIEWPORT, viewport, 0);
		gl.glGetDoublev(GL2.GL_MODELVIEW_MATRIX, mvmatrix, 0);
		gl.glGetDoublev(GL2.GL_PROJECTION_MATRIX, projmatrix, 0);
		/* note viewport[3] is height of window in pixels */
		realy = viewport[3] - y; // Inver the Y coordinate

		// Get the coordinates at Z-depth = 0
		glu.gluUnProject((double) x, (double) realy, 0, mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord0, 0);
		// Get the coordinates at Z-depth = 1
		glu.gluUnProject((double) x, (double) realy, 1, mvmatrix, 0, projmatrix, 0, viewport, 0, wcoord1, 0);

		double z = 0;

		double a = wcoord1[0] - wcoord0[0];
		double b = wcoord1[1] - wcoord0[1];
		double c = wcoord1[2] - wcoord0[2];
		double t = (z - wcoord0[2]) / c;

		double projx = (t * a) + wcoord0[0];
		double projy = (t * b) + wcoord0[1];

		// if projx or porjy outside of ground bounds return null.
		if (Math.abs(projx) > VC.getGroundLength() / 2 || Math.abs(projy) > VC.getGroundWidth() / 2) {
			return null;
		}

		return new Point3D(projx, projy, 0);
	}

	int currx = 0;
	int curry = 0;
	int initx = 0;
	int inity = 0;

	@Override
	public void mouseClicked(MouseEvent e) {
		mouseClick = e;

	}

	@Override
	public void mousePressed(MouseEvent e) {
		// record the initial point of movement
		initx = e.getX();
		inity = e.getY();
		// if currentaction is move intersections

		mouseDown = e;

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mouseClick = e;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {

		// if button is new intersection then place down the new intersection.

		mouseDrag = e;

		// rotate the viewer
		currx = e.getX();
		curry = e.getY();

		int movex = initx - currx;
		int movey = inity - curry;

		if (Math.abs(movex) > 4) {
			if (movex > 4) {
				VC.setViewPointHorizontalAngle(VC.getViewPointHorizontalAngle() + 2);
			} else {
				VC.setViewPointHorizontalAngle(VC.getViewPointHorizontalAngle() - 2);
			}
			initx = currx;
		}

		if (Math.abs(movey) > 4) {
			if (movey > 4) {
				VC.setViewPointVerticalAngle(VC.getViewPointVerticalAngle() + 2);
			} else {
				VC.setViewPointVerticalAngle(VC.getViewPointVerticalAngle() - 2);
			}
			if (VC.getViewPointVerticalAngle() > 85) {
				VC.setViewPointVerticalAngle(85);
			}
			if (VC.getViewPointVerticalAngle() < 0) {
				VC.setViewPointVerticalAngle(0);
			}
			inity = curry;
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mousemoved = e;
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int notches = e.getWheelRotation();
		if (notches < 0) {
			// "Mouse wheel moved UP "
			VC.setViewerZoomLocation(VC.getViewerZoomLocation() - 10);
		} else {
			// "Mouse wheel moved DOWN "
			VC.setViewerZoomLocation(VC.getViewerZoomLocation() + 10);
		}
	}

	public void drawLinks(GL2 gl, List<Link> links) {
		for (Link link : links) {
			double[] d = link.getCoord();
			int lane = link.lane;
			double angle = link.getAngle();
			double length = link.length;
			gl.glTranslated(d[0], d[1], 0);
			gl.glRotated(angle, 0, 0, 1);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glColor3d(0.3, 0.3, 0.3);
			gl.glVertex3d(0, 0, roadheight);
			gl.glVertex3d(0, lanewidth * lane + lanGAPwidth * lane, roadheight);
			gl.glVertex3d(length, lanewidth * lane + lanGAPwidth * lane, roadheight);
			gl.glVertex3d(length, 0, roadheight);
			gl.glEnd();
			for (int i = 0; i < lane; i++) {
				if (i == 0) {
					gl.glBegin(GL2.GL_QUADS);
					gl.glColor3d(1, 1, 0);
					gl.glVertex3d(0, 0, roadheight);
					gl.glVertex3d(0, lanGAPwidth, roadheight);
					gl.glVertex3d(length, lanGAPwidth, roadheight);
					gl.glVertex3d(length, 0, roadheight);
					gl.glEnd();
				} else {
					gl.glColor3d(0, 0, 0);
					gl.glBegin(GL.GL_LINES);
					gl.glVertex3d(0, lanewidth * i + lanGAPwidth, roadheight);
					gl.glVertex3d(length, lanewidth * i + lanGAPwidth, roadheight);
					gl.glEnd();
				}
			}
			// draw left cars
			for (Vehicle vehicle : link.vehicles) {
				drawVehicle(gl, vehicle);
			}
			gl.glRotated(-angle, 0, 0, 1);
			gl.glTranslated(-d[0], -d[1], 0);
		}
	}

	public void drawVLinks(GL2 gl, List<VirtualLink> links) {
		for (VirtualLink link : links) {
			if (link.turningMovement == TurningMovement.UTURN) {
				continue;
			}
			double[] d = link.getCoord();
			int lane = 1;
			double angle = link.getAngle();
			double length = link.length;
			gl.glTranslated(d[0], d[1], 0);
			gl.glRotated(angle, 0, 0, 1);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glColor3d(0.3, 0.3, 0.3);
			gl.glVertex3d(0, 0, roadheight);
			gl.glVertex3d(0, lanewidth * lane + lanGAPwidth * lane, roadheight);
			gl.glVertex3d(length, lanewidth * lane + lanGAPwidth * lane, roadheight);
			gl.glVertex3d(length, 0, roadheight);
			gl.glEnd();
			for (int i = 0; i < lane; i++) {
				if (i == 0) {
					gl.glBegin(GL2.GL_QUADS);
					gl.glColor3d(1, 1, 0);
					gl.glVertex3d(0, 0, roadheight);
					gl.glVertex3d(0, lanGAPwidth, roadheight);
					gl.glVertex3d(length, lanGAPwidth, roadheight);
					gl.glVertex3d(length, 0, roadheight);
					gl.glEnd();
				} else {
					gl.glColor3d(0, 0, 0);
					gl.glBegin(GL.GL_LINES);
					gl.glVertex3d(0, lanewidth * i + lanGAPwidth, roadheight);
					gl.glVertex3d(length, lanewidth * i + lanGAPwidth, roadheight);
					gl.glEnd();
				}
			}
			// draw left cars
			for (Vehicle vehicle : link.vehicles) {
				drawVehicle(gl, vehicle);
			}
			gl.glRotated(-angle, 0, 0, 1);
			gl.glTranslated(-d[0], -d[1], 0);
		}
	}

	public void drawIntersection(GL2 gl, Node p) {
		if (p.nodeType == NodeType.UNSIGNALISED) {
			Unsignalised po = (Unsignalised) p;
			gl.glColor3d(1, 0, 0);
			gl.glBegin(GL2.GL_POLYGON);
			gl.glVertex3d(po.l1.x, po.l1.y, intersectionheight);
			gl.glVertex3d(po.l2.x, po.l2.y, intersectionheight);
			gl.glVertex3d(po.l3.x, po.l3.y, intersectionheight);
			gl.glVertex3d(po.l4.x, po.l4.y, intersectionheight);
			gl.glEnd();
		}

	}

	public void test(final String point) {
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (point != null) {
					JOptionPane.showMessageDialog(Global.getEditor(), point);
				}
			}
		});
	}

	private void createGenerator(final Point3D point) {

		if (!network.isGeneratorPresent(point)) {
			Generator generator;
			int size = network.getUnsignalised().size() + network.getGenerators().size() + 1;
			generator = new Generator("" + size + "", point);
			network.addGeneratorNode(generator);
		}
	}

	private void createUnsignalisedNode(final Point3D point) {

		if (!network.isUnsignalisedNodePresent(point)) {
			Unsignalised unsignalised;
			int size = network.getUnsignalised().size() + network.getGenerators().size() + 1;
			unsignalised = new Unsignalised("" + size + "", point);
			network.addUnsignalisedNode(unsignalised);
		}

	}

	private void viewGenerator(Point3D point) {
	}

	private void viewLink(Point3D point) {
	}

	private void viewUnsignalisedNode(Point3D point) {
	}

	private void drawVehicle(GL2 gl, Vehicle vehicle) {
		double distance = (vehicle.getDistance() + 1) * 1.2;
		int strip = vehicle.getStripNo();
		double abc = 0;
		switch (strip) {
		case 0:
			abc = 6 * 1.2 + lanGAPwidth;
			break;
		case 1:
			abc = 5 * 1.2 + lanGAPwidth;
			;
			break;
		case 2:
			abc = 4 * 1.2 + lanGAPwidth;
			;
			break;
		case 3:
			abc = 3 * 1.2 + lanGAPwidth;
			;
			break;
		case 4:
			abc = 2 * 1.2 + lanGAPwidth;
			;
			break;
		case 5:
			abc = 1 * 1.2 + lanGAPwidth;
			;
			break;
		}
		double[] color = vehicle.getColour();
		if (vehicle.vehicleType == VehicleType.BIKE) {
			// gl.glTranslated(distance, strip * 1.2, 0);
			gl.glBegin(GL2.GL_QUADS);
			// front
			gl.glColor3f(1, 1, 0);
			gl.glVertex3d(distance - 0.3, abc - 0.3, 0);
			gl.glVertex3d(distance - 0.3, abc - 0.9, 0);
			gl.glVertex3d(distance - 0.3, abc - 0.9, height);
			gl.glVertex3d(distance - 0.3, abc - 0.3, height);

			// roof
			gl.glColor3d(color[0], color[1], color[2]);
			gl.glVertex3d(distance - 0.3, abc - 0.3, height);
			gl.glVertex3d(distance - 0.3, abc - 0.9, height);
			gl.glVertex3d(distance - 2.1, abc - 0.9, height);
			gl.glVertex3d(distance - 2.1, abc - 0.3, height);

			// rear
			gl.glVertex3d(distance - 2.1, abc - 0.3, 0);
			gl.glVertex3d(distance - 2.1, abc - 0.9, 0);
			gl.glVertex3d(distance - 2.1, abc - 0.9, height);
			gl.glVertex3d(distance - 2.1, abc - 0.3, height);

			// left side
			gl.glVertex3d(distance - 0.3, abc - 0.3, 0);
			gl.glVertex3d(distance - 2.1, abc - 0.3, 0);
			gl.glVertex3d(distance - 2.1, abc - 0.3, height);
			gl.glVertex3d(distance - 0.3, abc - 0.3, height);

			// right side
			gl.glVertex3d(distance - 0.3, abc - 0.9, 0);
			gl.glVertex3d(distance - 2.1, abc - 0.9, 0);
			gl.glVertex3d(distance - 2.1, abc - 0.9, height);
			gl.glVertex3d(distance - 0.3, abc - 0.9, height);
			gl.glEnd();
			// gl.glTranslated(-distance, -abc, 0);
		}
		if (vehicle.vehicleType == VehicleType.CAR) {
			// gl.glTranslated(distance, abc, 0);
			gl.glBegin(GL2.GL_QUADS);
			// front
			gl.glColor3f(1, 1, 0);
			gl.glVertex3d(distance - 0.1, abc - 0.35, 0);
			gl.glVertex3d(distance - 0.1, abc - 2.05, 0);
			gl.glVertex3d(distance - 0.1, abc - 2.05, height);
			gl.glVertex3d(distance - 0.1, abc - 0.35, height);

			// roof
			gl.glColor3d(color[0], color[1], color[2]);
			gl.glVertex3d(distance - 0.1, abc - 0.35, height);
			gl.glVertex3d(distance - 0.1, abc - 2.05, height);
			gl.glVertex3d(distance - 4.7, abc - 2.05, height);
			gl.glVertex3d(distance - 4.7, abc - 0.35, height);

			// rear
			gl.glVertex3d(distance - 4.7, abc - 0.35, 0);
			gl.glVertex3d(distance - 4.7, abc - 2.05, 0);
			gl.glVertex3d(distance - 4.7, abc - 2.05, height);
			gl.glVertex3d(distance - 4.7, abc - 0.35, height);

			// left side
			gl.glVertex3d(distance - 0.1, abc - 0.35, 0);
			gl.glVertex3d(distance - 4.7, abc - 0.35, 0);
			gl.glVertex3d(distance - 4.7, abc - 0.35, height);
			gl.glVertex3d(distance - 0.1, abc - 0.35, height);

			// right side
			gl.glVertex3d(distance - 0.1, abc - 2.05, 0);
			gl.glVertex3d(distance - 4.7, abc - 2.05, 0);
			gl.glVertex3d(distance - 4.7, abc - 2.05, height);
			gl.glVertex3d(distance - 0.1, abc - 2.05, height);
			gl.glEnd();
			// gl.glTranslated(-distance, -abc, 0);
		}
		if (vehicle.vehicleType == VehicleType.AUTO) {
			// gl.glTranslated(distance, abc, 0);
			gl.glBegin(GL2.GL_QUADS);
			// front
			gl.glVertex3d(distance - 0.5, abc - 0.5, 0);
			gl.glVertex3d(distance - 0.5, abc - 1.9, 0);
			gl.glVertex3d(distance - 0.5, abc - 1.9, height);
			gl.glVertex3d(distance - 0.5, abc - 0.5, height);

			// roof
			gl.glColor3d(color[0], color[1], color[2]);
			gl.glVertex3d(distance - 0.5, abc - 0.5, height);
			gl.glVertex3d(distance - 0.5, abc - 1.9, height);
			gl.glVertex3d(distance - 3.1, abc - 1.9, height);
			gl.glVertex3d(distance - 3.1, abc - 0.5, height);

			// rear
			gl.glVertex3d(distance - 3.1, abc - 0.5, 0);
			gl.glVertex3d(distance - 3.1, abc - 1.9, 0);
			gl.glVertex3d(distance - 3.1, abc - 1.9, height);
			gl.glVertex3d(distance - 3.1, abc - 0.5, height);

			// left side
			gl.glVertex3d(distance - 0.5, abc - 0.5, 0);
			gl.glVertex3d(distance - 3.1, abc - 0.5, 0);
			gl.glVertex3d(distance - 3.1, abc - 0.5, height);
			gl.glVertex3d(distance - 0.5, abc - 0.5, height);

			// right side
			gl.glVertex3d(distance - 0.5, abc - 1.9, 0);
			gl.glVertex3d(distance - 3.1, abc - 1.9, 0);
			gl.glVertex3d(distance - 3.1, abc - 1.9, height);
			gl.glVertex3d(distance - 0.5, abc - 1.9, height);
			gl.glEnd();
			// gl.glTranslated(-distance, -abc, 0);
		}
		if (vehicle.vehicleType == VehicleType.BUS) {
			// gl.glTranslated(distance, abc, 0);
			gl.glBegin(GL2.GL_QUADS);
			// front
			gl.glVertex3d(distance - 0.6, abc - 0.55, 0);
			gl.glVertex3d(distance - 0.6, abc - 3.05, 0);
			gl.glVertex3d(distance - 0.6, abc - 3.05, height);
			gl.glVertex3d(distance - 0.6, abc - 0.55, height);

			// roof
			gl.glColor3d(color[0], color[1], color[2]);
			gl.glVertex3d(distance - 0.6, abc - 0.55, height);
			gl.glVertex3d(distance - 0.6, abc - 3.05, height);
			gl.glVertex3d(distance - 9.0, abc - 3.05, height);
			gl.glVertex3d(distance - 9.0, abc - 0.55, height);

			// rear
			gl.glVertex3d(distance - 9.0, abc - 0.55, 0);
			gl.glVertex3d(distance - 9.0, abc - 3.05, 0);
			gl.glVertex3d(distance - 9.0, abc - 3.05, height);
			gl.glVertex3d(distance - 9.0, abc - 0.55, height);

			// left side
			gl.glVertex3d(distance - 0.6, abc - 0.55, 0);
			gl.glVertex3d(distance - 9.0, abc - 0.55, 0);
			gl.glVertex3d(distance - 9.0, abc - 0.55, height);
			gl.glVertex3d(distance - 0.6, abc - 0.55, height);

			// right side
			gl.glVertex3d(distance - 0.6, abc - 3.05, 0);
			gl.glVertex3d(distance - 9.0, abc - 3.05, 0);
			gl.glVertex3d(distance - 9.0, abc - 3.05, height);
			gl.glVertex3d(distance - 0.6, abc - 3.05, height);
			gl.glEnd();
			// gl.glTranslated(-distance, -abc, 0);
		}

	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}
}
