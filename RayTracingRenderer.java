package RenderScene;

import java.util.stream.IntStream;

public class RayTracingRenderer implements IRenderer {
	private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();

	public boolean renderScene(Scene scene, String pathToResultImage, int resultImageWidth, int resultImageHeight) {

		int superSampledWidth = resultImageWidth * scene.getSuperSampling();
		int superSampledHeight = resultImageHeight * scene.getSuperSampling();

		byte[] superSampledRGBData = renderSceneToRGBByteArray(scene, superSampledWidth, superSampledHeight);

		byte[] imageRGBData = ImageUtil.getImageRGBDataFromSuperSample(scene.getSuperSampling(), superSampledRGBData,
				superSampledWidth, superSampledHeight);

		return ImageUtil.saveImage(resultImageWidth, imageRGBData, pathToResultImage);
	}

	private static byte[] renderSceneToRGBByteArray(Scene scene, int resultImageWidth, int resultImageHeight) {
		scene.getCamera().initScreenParams(resultImageHeight, resultImageWidth);
		byte[] imageRGBData = new byte[resultImageWidth * resultImageHeight * 3];
		int rowsHandaledPerIteration = resultImageWidth / NUMBER_OF_CORES;
		IntStream.rangeClosed(0, 1 + resultImageWidth / rowsHandaledPerIteration).parallel().forEach(row1 -> {
			int max = Math.min(rowsHandaledPerIteration * (row1 + 1), resultImageWidth);
			for (int row = row1 * rowsHandaledPerIteration; row < max; row++) {
				for (int col = 0; col < resultImageHeight; col++) {
					Ray firstRay = scene.getCamera().getRayWhichLeavesFromPixel(row, col, resultImageHeight,
							resultImageWidth);

					byte[] color = RayTracingUtil.getColorFromRay(scene, firstRay, 0, null).getColorByteArray();
					System.arraycopy(color, 0, imageRGBData, ((row * resultImageWidth) + col) * 3, 3);
				}
			}
		});
		return imageRGBData;
	}

}
