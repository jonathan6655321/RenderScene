package RenderScene;

public class RayTracingRenderer implements IRenderer {

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

		for (int row = 0; row < resultImageHeight; row++) {
			for (int col = 0; col < resultImageWidth; col++) {
				Ray firstRay = scene.getCamera().getRayWhichLeavesFromPixel(row, col, resultImageHeight,
						resultImageWidth);

				byte[] color = RayTracingUtil.getColorFromRay(scene, firstRay, 0, null).getColorByteArray();
				System.arraycopy(color, 0, imageRGBData, ((row * resultImageWidth) + col) * 3, 3);
			}
		}
		return imageRGBData;
	}

}
