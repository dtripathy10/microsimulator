package edu.iitb.civil.tse.gui.panel.opengl;

//	Simple class to capture OpenGL frames and save to
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GL;

//	files while program is running, so you can make
//	a movie from it.

//	Usage:
//	1. movie = new GLMovie()
//	2. Change any of the file pattern, frame rate, rectangle
//	   captured parameters if you want/need.
//	3. In your display code, call
//		movie.frame(gl);
//	   after rendering the scene. If the movie is 'live' and
//	   enough time has elapsed, it will capture the current frame
//	   to a new file. Internally frame() does a glFinish(), so
//	   you don't need to.
//	4. Use movie.live() to turn capture on or off. (It starts off)
//
//	The output is stored as numbered uncompressed PPM files. Make
//	a movie with something like
//		convert -flip /tmp/u9011925*.ppm capture.mpg
//
//	The output files are upside down because the native OpenGL
//	image order is bottom to top. Correcting this would take
//	extra time, and it's easily fixed by ImageMagick anyway.
//
//	Internally the file IO is done by a background thread, so on
//	a fast machine you capture at a reasonable rate without the
//	main program slowing down. But if frames are being dropped,
//	you will have to reduce the window size.
//
//	Written by Hugh Fisher.
//	Released under BSD/MIT license
public class GLMovie {

	public boolean enabled;
	public String filePath;
	public int fps;
	public int frameNumber;
	public int buffer;
	public int x, y, w, h;
	// Standard RGB format expected by PPM. If you change
	// this, you have to change a lot of other code too. */
	static final int PIXEL_BYTES = 3;

	// Internal timing for deciding when to capture
	private boolean firstFrame;
	private long timeBase, nextFrameTime;
	// The IO thread
	ByteBuffer pixels;
	Thread tid;

	GLMovie() {
		String uname;

		enabled = true;
		fps = 24;
		buffer = GL.GL_BACK;

		// Save to /tmp under our own name
		// uname = (new UnixSystem()).getUsername();
		// need a base dir to dump this stuff into
		filePath = "D:/tmp/username" + "%04d.ppm";

		// Get bounds later
		x = 0;
		y = 0;
		w = 0;
		h = 0;

		// Internal counters
		firstFrame = true;
		timeBase = 0;
		nextFrameTime = 0;
		frameNumber = 0;

		// Pixels array allocated on demand

		// Background IO
		tid = new WriterThread(this);
	}

	@Override
	public void finalize() {
	}

	// **** Movie attributes ****
	// Path needs %d format somewhere, or you will just overwrite the
	// same file each frame! Default is /tmp/your-username%04d.ppm
	public void path(String newPath) {
		filePath = newPath;
	}

	// Default is 24. 0 means capture every frame
	public void frameRate(int newRate) {
		fps = newRate;
	}

	// Default is back buffer, change for stereo or single buffered
	public void readBuffer(int newBuffer) {
		buffer = newBuffer;
	}

	// Default is entire viewport at time of first frame. Probably
	// not a good idea to resize the window once started.
	public void rect(int newX, int newY, int newW, int newH) {
		x = newX;
		y = newY;
		w = newW;
		h = newH;
	}

	// TRUE means capture frames
	public void live(boolean state) {
		enabled = state;
	}

	// **** Generating images ****

	// There is no buffering between the OpenGL program and the IO
	// thread in this implementation, so the writer thread has to
	// finish each frame file before the next capture. It might be
	// better to have a small 2-4 entry queue which could smooth out
	// an occasional glitch. But if the IO does get slowed down
	// then the amount of queued frames will build up at a huge
	// rate and it will never catch up, so I decided to keep it
	// simple.

	// readBufferContents and writeBufferToFile are synchronized
	// to avoid fights over the pixels buffer. I'm not very good
	// at Java concurrency, so if you know a better way to implement
	// this, go for it.
	private class WriterThread extends Thread {

		private GLMovie owner;

		public WriterThread(GLMovie newOwner) {
			setDaemon(true);
			owner = newOwner;
			this.start();
		}

		@Override
		public void run() {
			while (true) {
				// Wait for a frame
				try {
					synchronized (this) {
						this.wait();
					}
				} catch (InterruptedException ie) {
					return;
				}
				// and write it out
				owner.writeBufferToFile();
			}
		}
	}

	private void start(GL gl) {
		int[] viewPort = { 0, 0, 0, 0 };

		// No rect given at first use?
		if (w == 0 && h == 0) {
			gl.glGetIntegerv(GL.GL_VIEWPORT, viewPort, 0);
			this.rect(viewPort[0], viewPort[1], viewPort[2], viewPort[3]);
			pixels = ByteBuffer.allocate(w * h * PIXEL_BYTES);
		}

		// Time of first frame
		timeBase = System.currentTimeMillis();
	}

	synchronized private void readBufferContents(GL2 gl) {
		// Can't assume aligned on 32 bit boundary
		gl.glPushAttrib(GL2.GL_PIXEL_MODE_BIT);
		gl.glReadBuffer(buffer);
		gl.glPixelStorei(GL.GL_PACK_ALIGNMENT, 1);
		pixels.clear();
		gl.glReadPixels(x, y, w, h, GL.GL_RGB, GL.GL_UNSIGNED_BYTE, pixels);
		gl.glPopAttrib();
	}

	synchronized private void writeBufferToFile() {
		String fname;
		FileChannel f;

		fname = String.format(filePath, frameNumber);
		System.out.println(fname);

		try {
			f = new FileOutputStream(fname).getChannel();
			// PPM uncompress format is easy
			System.out.println("HHHHHHHHHH");
			f.write(ByteBuffer.wrap(String.format("P6 %d %d 255\n", w, h).getBytes()));
			pixels.position(0);
			f.write(pixels);
			f.close();
		} catch (IOException e) {
			System.out.println("Error writing frame " + fname);
			this.live(false);
		}
	}

	public void frame(GL2 context) {
		// Nothing to do?
		// if (!enabled) {
		// return;
		// }

		if (fps > 0 && System.currentTimeMillis() < nextFrameTime) {
			return;
		}

		// Setup?
		if (firstFrame) {
			this.start(context);
			firstFrame = false;
		}

		// Ensure all GL operations have completed
		context.glFinish();

		// Grab the current buffer
		this.readBufferContents(context);

		// And notify IO thread
		synchronized (tid) {
			tid.notify();
		}

		// We're done, get ready for next
		frameNumber += 1;
		if (fps > 0) {
			nextFrameTime = timeBase + (frameNumber * 1000) / fps;
		}
	}
}
