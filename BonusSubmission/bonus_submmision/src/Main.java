package RenderScene;

import java.awt.image.BufferedImage;
import java.io.IOException;

import RenderScene.movie_package.randelshofer.media.quicktime.QuickTimeWriter;

// jonathans args: 
// C:\Development\Graphics\temp\Transparency.txt C:\Development\Graphics\temp\trans.png 500 500
// C:\Development\Graphics\temp\yoda.txt C:\Development\Graphics\temp\yoda.png 500 500

/*TODO: delete this:
ido args:
"F:\Tau\Courses\Computer Science\2016-2017\Semester 2\Graphic\RenderScene\git\RenderScene\Scenes\Triangle.txt" "F:\Tau\Courses\Computer Science\2016-2017\Semester 2\Graphic\RenderScene\git\RenderScene\MyScenes\Triangle.png" 500 500
*/
public class Main {
	private final static int GIF_FRAME_RATE = 300;
	private final static double FRAME_RATE = 24;
	private final static boolean USE_GIF_FOR_MOVIE = false;

	public static void main(String[] args) {
		try {
			Request request = new Request(args);
			Scene[] scenes = SceneParser.getScenesFromTextFile(request);
			IRenderer renderer = new RayTracingRenderer();
			if (scenes.length == 1) {	//An image:
				BufferedImage resultImage = renderer.renderScene(scenes[0], request.resultImageWidth,
						request.resultImageHeight);
				ImageUtil.saveImage(resultImage, request.pathToResultImage);
			} else {					//An animation:
				BufferedImage[] frames = new BufferedImage[scenes.length];
				for (int frameNumber = 0; frameNumber < frames.length; frameNumber++) {
					System.out.println("Rendering frame number " + (frameNumber + 1) + ":");
					frames[frameNumber] = renderer.renderScene(scenes[frameNumber], request.resultImageWidth,
							request.resultImageHeight);
					
					// free memory:
					scenes[frameNumber] = null; 
					
					//for gif usage, transform to supported buffre image format:
					BufferedImage convertedImg = new BufferedImage(frames[frameNumber].getWidth(),
							frames[frameNumber].getHeight(), BufferedImage.TYPE_INT_ARGB); 
					convertedImg.getGraphics().drawImage(frames[frameNumber], 0, 0, null);
					
					frames[frameNumber] = convertedImg;
				}
				if (USE_GIF_FOR_MOVIE) {
					ImageUtil.saveGifAnimation(frames, request.pathToResultImage, GIF_FRAME_RATE);
				} else {
					VideoUtil.writeVideoOnlyVFR(request.pathToResultImage, frames, FRAME_RATE,
							QuickTimeWriter.VideoFormat.PNG);
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
