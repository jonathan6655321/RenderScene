package RenderScene;

import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

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
		LinkedBlockingQueue<int[]> pixels = createPixelsQueue_forMultiProcessing(resultImageWidth,
				resultImageHeight);
		byte[] imageRGBData = new byte[resultImageWidth * resultImageHeight * 3];
		
		// make NUMBER_OF_CORES new threads.
		// each polling pixel a time and rendering it.
		IntStream.rangeClosed(1, NUMBER_OF_CORES).parallel().forEach(core -> {
			int[] pixel;
			int row, col;
			double row1, col1;
			Random rnd = new Random();
			while (true) {
				pixel = pixels.poll();
				if (pixel == null) {// finished!
					break;
				}
				row = pixel[1];
				col = pixel[0];
				row1 = row - 0.5 + rnd.nextDouble();
				col1 = col - 0.5 + rnd.nextDouble();

				Ray firstRay = scene.getCamera().getRayWhichLeavesFromPixel(row1, col1);
				byte[] color = RayTracingUtil.getColorFromRay(scene, firstRay, 0, null).getColorByteArray();
				System.arraycopy(color, 0, imageRGBData, ((row * resultImageWidth) + col) * 3, 3);
			}
		});
		return imageRGBData;
	}

	private static void initSceneForRendering(Scene scene, int resultImageWidth, int resultImageHeight) {
		scene.getCamera().initScreenParams(resultImageHeight, resultImageWidth);
		scene.setBinarySearchObjects();
	}

	private static LinkedBlockingQueue<int[]> createPixelsQueue_forMultiProcessing(int resultImageWidth,
			int resultImageHeight) {
		LinkedBlockingQueue<int[]> pixels = new LinkedBlockingQueue<>();
		for (int i = 0; i < resultImageWidth; i++) {
			for (int j = 0; j < resultImageHeight; j++) {
				pixels.add(new int[] { i, j });
			}
		}
		return pixels;
	}
}
