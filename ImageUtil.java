package RenderScene;

import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageUtil {
	public static byte[] getImageRGBDataFromSuperSample(int superSamplingLevel, byte[] superSampledRGBData,
			int superSampledWidth, int superSampledHeight) {
		int imageWidth = (superSampledWidth / superSamplingLevel);
		int imageHeight = (superSampledHeight / superSamplingLevel);
		byte[] imageRGBData = new byte[imageHeight * imageWidth * 3];

		int pixelRow, pixelCol;
		byte[] rgb;
		for (int i = 0; i < imageRGBData.length / 3; i++) {
			pixelRow = i / imageWidth;
			pixelCol = i % imageWidth;
			rgb = calculatePixelColorFromSuperSample(superSamplingLevel, pixelRow, pixelCol, superSampledRGBData,
					superSampledWidth);
			System.arraycopy(rgb, 0, imageRGBData, i * 3, 3);
		}
		return imageRGBData;
	}

	private static byte[] calculatePixelColorFromSuperSample(int superSamplingLevel, int pixelRow, int pixelCol,
			byte[] superSampledRGBData, int superSampledWidth) {

		byte[] rgb = { 0, 0, 0 };
		int offsetDueToFullRows = pixelRow * superSampledWidth * superSamplingLevel * 3;
		int offsetInLastRow = pixelCol * superSamplingLevel * 3;
		int startLocationInSuperSample = offsetDueToFullRows + offsetInLastRow;

		int[] rgbInt = new int[3];
		for (int i = 0; i < superSamplingLevel; i++) {
			for (int j = 0; j < superSamplingLevel; j++) {
				int currentIndex = startLocationInSuperSample + (i * superSampledWidth * 3) + (j * 3);
				for (int k = 0; k < 3; k++) {
					rgbInt[k] += superSampledRGBData[currentIndex + k];
					if (superSampledRGBData[currentIndex + k] < 0) {
						rgbInt[k] += 256;
					}
				}
			}
		}
		for (int k = 0; k < 3; k++) {
			rgb[k] = (byte) (rgbInt[k] / (superSamplingLevel * superSamplingLevel));
		}
		return rgb;
	}

	/*
	 * Saves RGB data as an image in png format to the specified location.
	 */
	public static boolean saveImage(int width, byte[] rgbData, String fileName) {
		try {

			BufferedImage image = bytes2RGB(width, rgbData);
			ImageIO.write(image, "png", new File(fileName));

		} catch (IOException e) {
			System.out.println("ERROR SAVING FILE: " + e.getMessage());
			return false;
		}
		return true;

	}

	/*
	 * Producing a BufferedImage that can be saved as png from a byte array of
	 * RGB values.
	 */
	private static BufferedImage bytes2RGB(int width, byte[] buffer) {
		int height = buffer.length / width / 3;
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		ColorModel cm = new ComponentColorModel(cs, false, false, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
		SampleModel sm = cm.createCompatibleSampleModel(width, height);
		DataBufferByte db = new DataBufferByte(buffer, width * height);
		WritableRaster raster = Raster.createWritableRaster(sm, db, null);
		BufferedImage result = new BufferedImage(cm, raster, false, null);

		return result;
	}
}
