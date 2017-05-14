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
import java.util.Arrays;

import javax.imageio.ImageIO;

public class RayTracingRenderer implements IRenderer {

	public boolean renderScene(Scene scene, String pathToResultImage, int resultImageWidth, int resultImageHeight) {

		byte[] imageRGBData = new byte[resultImageWidth * resultImageHeight * 3];

		for (int row = 0; row < resultImageHeight; row++) {
			for (int col = 0; col < resultImageWidth; col++) {
				// TODO super sampling
				Ray firstRay = scene.getCamera().getRayWhichLeavesFromPixel(row, col, resultImageHeight,
						resultImageWidth);

				byte[] color = getColorFromRay(scene, firstRay, 0, null).getColorByteArray();
				System.arraycopy(color, 0, imageRGBData, (row * resultImageWidth + col) * 3, 3);
			}
		}
		return saveImage(resultImageWidth, imageRGBData, pathToResultImage);
	}

	private Color getColorFromRay(Scene scene, Ray ray, int recursionDepth, RenderableObject objectToIgnore) {
		recursionDepth++;
		if(recursionDepth>scene.getMaximumNumberOfRecursions()){return null;} //TODO
		
		Collision firstCollision = scene.getFirstCollision(ray, objectToIgnore);
		if(firstCollision == null){
			return scene.getBackgroundColor();
		}
		Color rayColor = new Color(0,0,0);
		RenderableObject collisionObject = firstCollision.getcollisionObject();
		double transparency = collisionObject.getMaterial().transperancy;
		
		// calculating background color component: 
		Color backGroundColor = getBackgroundColor(scene, ray, recursionDepth, firstCollision);
		backGroundColor.multiplyByConstant(transparency);
		rayColor.add(backGroundColor);
		
		// calculating reflective color component:
		Color reflectiveColor = getReflectiveColor(scene, ray, recursionDepth, firstCollision);
		reflectiveColor.multiplyByColor(collisionObject.getMaterial().reflectionColor);
		rayColor.add(reflectiveColor);
		
		
		//Color += backgroundColor*transperancy (Color)
		//Color += (diffuse + specular) * (1 - transparency) (Color)
		//Color += (reflection color) * reflection (Color)
		
	}

	private Color getBackgroundColor(Scene scene, Ray ray, int recursionDepth, Collision collision)
	{
		Ray rayThroughObject = new Ray(collision.getCollisionPoint(), ray.direction);
		return getColorFromRay(scene, rayThroughObject, recursionDepth, collision.getcollisionObject());
	}
	
	private Color getReflectiveColor(Scene scene, Ray ray, int recursionDepth, Collision collision)
	{
		Ray reflectionRay = new Ray(collision.getCollisionPoint(), 
				ray.direction.getReflectionVector(collision.getNormalToCollisionPoint()));
		return getColorFromRay(scene, reflectionRay, recursionDepth, collision.getcollisionObject());
	}
	
	/*
	 * Saves RGB data as an image in png format to the specified location.
	 */
	private static boolean saveImage(int width, byte[] rgbData, String fileName) {
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
