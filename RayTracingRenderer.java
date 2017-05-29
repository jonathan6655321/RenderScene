package RenderScene;

import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.stream.IntStream;

public class RayTracingRenderer implements IRenderer {
	private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

	public RayTracingRenderer() {
		System.out.println("Creating new renderer: Ray Tracing Renderer.");
	}

	public BufferedImage renderScene(Scene scene, int resultImageWidth, int resultImageHeight) {

		System.out.println("Rendered scene dimensions: " + resultImageWidth + "x" + resultImageHeight);

		int superSampledWidth = resultImageWidth * scene.getSuperSampling();
		int superSampledHeight = resultImageHeight * scene.getSuperSampling();

		System.out.println("Super sampaling dimensions: " + superSampledWidth + "x" + superSampledHeight);

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
		scene.getCamera().initScreenParams(resultImageHeight, resultImageWidth);
		scene.setBinarySearchObjects();
		byte[] imageRGBData = new byte[resultImageWidth * resultImageHeight * 3];
		int rowsHandaledPerIteration = resultImageWidth / NUMBER_OF_CORES;
		IntStream.rangeClosed(0, 1 + resultImageWidth / rowsHandaledPerIteration).parallel().forEach(row1 -> {
			int max = Math.min(rowsHandaledPerIteration * (row1 + 1), resultImageWidth);
			Random rnd = new Random();
			for (int row = row1 * rowsHandaledPerIteration; row < max; row++) {
				for (int col = 0; col < resultImageHeight; col++) {
					Ray firstRay = scene.getCamera().getRayWhichLeavesFromPixel((double) row - 0.5 + rnd.nextDouble(),
							(double) col - 0.5 + rnd.nextDouble());
					
					
					byte[] color = RayTracingUtil.getColorFromRay(scene, firstRay, 0, null).getColorByteArray();
					System.arraycopy(color, 0, imageRGBData, ((row * resultImageWidth) + col) * 3, 3);
				}
			}
		});
		return imageRGBData;
	}

}
