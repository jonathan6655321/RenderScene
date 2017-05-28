package RenderScene;

import java.awt.image.BufferedImage;
import java.io.IOException;

import ch.randelshofer.media.quicktime.QuickTimeWriter;

// jonathans args: 
// C:\Development\Graphics\temp\Transparency.txt C:\Development\Graphics\temp\trans.png 500 500
// C:\Development\Graphics\temp\yoda.txt C:\Development\Graphics\temp\yoda.png 500 500

/*
ido args:
"F:\Tau\Courses\Computer Science\2016-2017\Semester 2\Graphic\RenderScene\git\RenderScene\Scenes\Triangle.txt" "F:\Tau\Courses\Computer Science\2016-2017\Semester 2\Graphic\RenderScene\git\RenderScene\testImage.png" 500 500
*/
public class Main {
	private final static int GIF_FRAME_RATE = 300;
	private final static double FRAME_RATE = 2.5;
	private final static boolean GIF = false;

	public static void main(String[] args) {
		try {
			Request request = new Request(args);
			Scene[] scenes = SceneParser.getScenesFromTextFile(request);
			IRenderer renderer = new RayTracingRenderer();
			if (scenes.length == 1) {
				BufferedImage resultImage = renderer.renderScene(scenes[0], request.resultImageWidth,
						request.resultImageHeight);
				ImageUtil.saveImage(resultImage, request.pathToResultImage);
			} else {
				BufferedImage[] frames = new BufferedImage[scenes.length];
				for (int frameNumber = 0; frameNumber < frames.length; frameNumber++) {
					System.out.println("Rendering frame number " + frameNumber + ":");
					frames[frameNumber] = renderer.renderScene(scenes[frameNumber], request.resultImageWidth,
							request.resultImageHeight);
					BufferedImage convertedImg = new BufferedImage(frames[frameNumber].getWidth(),
							frames[frameNumber].getHeight(), BufferedImage.TYPE_INT_ARGB);
					convertedImg.getGraphics().drawImage(frames[frameNumber], 0, 0, null);
					frames[frameNumber] = convertedImg;
				}
				if (GIF) {
					ImageUtil.saveGifAnimation(frames, request.pathToResultImage, GIF_FRAME_RATE);
				} else {
					VideoUtil.writeVideoOnlyVFR(request.pathToResultImage, frames, FRAME_RATE, QuickTimeWriter.VideoFormat.PNG);
				}
			}
			System.out.println("Render scene completed successfully.");

		} catch (IOException e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		} catch (RayTracerException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
	}
}
