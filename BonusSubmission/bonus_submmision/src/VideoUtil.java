package RenderScene;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import RenderScene.movie_package.randelshofer.media.quicktime.QuickTimeWriter;

public class VideoUtil {
	public final static String TEMP_FILE = "temp.tmp";

	public static void writeVideoOnlyVFR(String movieFilePath, BufferedImage[] images, double fps,
			QuickTimeWriter.VideoFormat videoFormat) throws IOException {
		File movieFile = new File(movieFilePath);
		int width = images[0].getWidth();
		int height = images[0].getHeight();
		File tmpFile = new File(TEMP_FILE);
		Graphics2D g = null;
		BufferedImage img = null;
		BufferedImage prevImg = null;
		int[] data = null;
		int[] prevData = null;
		QuickTimeWriter qtOut = null;
		try {
			int timeScale = (int) (fps * 100.0);
			int duration = 100;

			qtOut = new QuickTimeWriter(videoFormat == QuickTimeWriter.VideoFormat.RAW ? movieFile : tmpFile);
			qtOut.addVideoTrack(videoFormat, timeScale, width, height);
			qtOut.setSyncInterval(0, 30);

			img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			data = ((DataBufferInt) img.getRaster().getDataBuffer()).getData();
			prevImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			prevData = ((DataBufferInt) prevImg.getRaster().getDataBuffer()).getData();
			g = img.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

			int prevImgDuration = 0;
			for (int i = 0; i < images.length; i++) {

				BufferedImage fImg = images[i];
				g.drawImage(fImg, 0, 0, width, height, null);
				if (i != 0 && Arrays.equals(data, prevData)) {
					prevImgDuration += duration;
				} else {
					if (prevImgDuration != 0) {
						qtOut.writeFrame(0, prevImg, prevImgDuration);
					}
					prevImgDuration = duration;
					System.arraycopy(data, 0, prevData, 0, data.length);
				}

			}
			if (prevImgDuration != 0) {
				qtOut.writeFrame(0, prevImg, prevImgDuration);
			}
			qtOut.toWebOptimizedMovie(movieFile, false);
			tmpFile.delete();
			qtOut.close();
			qtOut = null;
		} finally {
			if (g != null) {
				g.dispose();
			}
			if (img != null) {
				img.flush();
			}
			if (qtOut != null) {
				qtOut.close();
			}
		}
	}
}
