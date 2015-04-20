package walnoot.ld32;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.utils.ScreenUtils;

public class Timelapse {
	private static final long TIME_BETWEEN_CAPTURES = 1000;//ms
	private static final String PATH = "C:\\Users\\Michiel\\Desktop\\capture\\";
	private static long lastCaptureTime = 0;
	
	private static final ArrayBlockingQueue<Pixmap> captureQueue = new ArrayBlockingQueue<Pixmap>(5);
	private static CaptureIOThread thread = new CaptureIOThread();
	
	public static void update() {
		if (!thread.isAlive()) {
			thread.start();
		}
		
		long now = System.currentTimeMillis();
		if (now - lastCaptureTime > TIME_BETWEEN_CAPTURES) {
			lastCaptureTime = now;
			
			Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
			
			try {
				captureQueue.put(pixmap);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static class CaptureIOThread extends Thread {
		private int index = -1;
		
		public CaptureIOThread() {
			setDaemon(true);
		}
		
		@Override
		public void run() {
			while (true) {
				try {
					Pixmap pixmap = captureQueue.take();
					
					int w = pixmap.getWidth();
					int h = pixmap.getHeight();
					
					// Flip the pixmap upside down
					ByteBuffer pixels = pixmap.getPixels();
					int numBytes = w * h * 4;
					byte[] lines = new byte[numBytes];
					int numBytesPerLine = w * 4;
					for (int i = 0; i < h; i++) {
						pixels.position((h - i - 1) * numBytesPerLine);
						pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
					}
					pixels.clear();
					pixels.put(lines);
					
					index = getIndex();
					String p = String.format(PATH + "test_%d.png", index);
					PixmapIO.writePNG(Gdx.files.absolute(p), pixmap);
					pixmap.dispose();
					
					index++;
					writeIndex();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		private int getIndex() {
			if (index == -1) {
				try {
					FileInputStream stream = new FileInputStream(PATH + "index.txt");
					
					int i = new DataInputStream(stream).readInt();
					
					System.out.println("read " + i);
					
					stream.close();
					
					return i;
				} catch (FileNotFoundException e) {
					return 0;
				} catch (IOException e) {
					e.printStackTrace();
					
					return 0;
				}
			} else {
				return index;
			}
		}
		
		private void writeIndex() {
			try {
				FileOutputStream stream = new FileOutputStream(PATH + "index.txt");
				
				new DataOutputStream(stream).writeInt(index);
				
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
