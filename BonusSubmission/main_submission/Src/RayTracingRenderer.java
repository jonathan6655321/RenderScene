package RenderScene;

import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.IntStream;

import sun.misc.Lock;

public class RayTracingRenderer implements IRenderer {
	private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

	public RayTracingRenderer() {
		System.out.println("Creating new renderer: 			Ray Tracing Renderer.");
	}

	public BufferedImage renderScene(Scene scene, int resultImageWidth, int resultImageHeight) {

		int superSampledWidth = resultImageWidth * scene.getSuperSampling();
		int superSampledHeight = resultImageHeight * scene.getSuperSampling();

		System.out.println("Rendered scene dimensions:		" + resultImageWidth + "x" + resultImageHeight);
		System.out.println("Super sampaling dimensions:		" + superSampledWidth + "x" + superSampledHeight);
		System.out.print("Starting rendering...			");

		long startTime = System.nanoTime();
		byte[] superSampledRGBData = renderSceneToRGBByteArray(scene, superSampledWidth, superSampledHeight);
		long endTime = System.nanoTime();

		System.out.println("Finished!");
		System.out.println("Time:					" + (((double) (endTime - startTime)) / 1000000000) + " Seconds.");
		System.out.print("Creating image...			");

		byte[] imageRGBData = ImageUtil.getImageRGBDataFromSuperSample(scene.getSuperSampling(), superSampledRGBData,
				superSampledWidth, superSampledHeight);
		BufferedImage image = ImageUtil.bytes2RGB(resultImageWidth, imageRGBData);

		System.out.println("Finished!");

		return image;
	}

	private static byte[] renderSceneToRGBByteArray(Scene scene, int resultImageWidth, int resultImageHeight) {
		initSceneForRendering(scene, resultImageWidth, resultImageHeight);
		byte[] imageRGBData = new byte[resultImageWidth * resultImageHeight * 3];
		Random rnd = new Random();

		int[] currentPixel = new int[] { 0, 0 };//row,col

		// make NUMBER_OF_CORES new threads.
		IntStream.rangeClosed(1, NUMBER_OF_CORES).parallel().forEach(core -> {
			int rowInt, colInt;
			double rowDouble, colDouble;
			while (true) {
				synchronized (rnd) {//loop logic for multithread :)
					if (currentPixel[1] >= resultImageWidth) {
						currentPixel[1] = 0;
						currentPixel[0]++;
					}
					if (currentPixel[0] >= resultImageHeight) {
						break;
					}
					rowInt = currentPixel[0];
					colInt = currentPixel[1];
					rowDouble = rowInt - 0.5 + rnd.nextDouble();
					colDouble = colInt - 0.5 + rnd.nextDouble();
					currentPixel[1]++;
				}

				Ray firstRay = scene.getCamera().getRayWhichLeavesFromPixel(rowDouble, colDouble);
				byte[] color = RayTracingUtil.getColorFromRay(scene, firstRay, 0, null).getColorByteArray();
				System.arraycopy(color, 0, imageRGBData, ((rowInt * resultImageWidth) + colInt) * 3, 3);
			}
		});
		return imageRGBData;
	}

	private static void initSceneForRendering(Scene scene, int resultImageWidth, int resultImageHeight) {
		scene.getCamera().initScreenParams(resultImageHeight, resultImageWidth);
		scene.setBinarySearchObjects();
	}
}
