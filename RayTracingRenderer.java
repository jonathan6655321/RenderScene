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

public class RayTracingRenderer implements IRenderer {
	final double CLOSE_DOUBLE = 0.000000001;
	final int SUPER_SAMPLING_LEVEL = 2;

	public boolean renderScene(Scene scene, String pathToResultImage, int resultImageWidth, int resultImageHeight) {

		int superSampledWidth = resultImageWidth*SUPER_SAMPLING_LEVEL;
		int superSampledHeight = resultImageHeight*SUPER_SAMPLING_LEVEL;
		
//		byte[] imageRGBData = new byte[resultImageWidth * resultImageHeight * 3];
		byte[] superSampledRGBData = new byte[superSampledWidth*superSampledHeight*3];
		
		for (int row = 0; row < superSampledHeight; row++) {
			for (int col = 0; col < superSampledWidth; col++) {
				// TODO super sampling
				Ray firstRay = scene.getCamera().getRayWhichLeavesFromPixel(row, col, resultImageHeight,
						resultImageWidth);

				byte[] color = getColorFromRay(scene, firstRay, 0, null).getColorByteArray();
				System.arraycopy(color, 0, superSampledRGBData, ((row * superSampledWidth) + col) * 3, 3);
			}
		}
		
		byte[] imageRGBData = getImageRGBDataFromSuperSample(superSampledRGBData, superSampledWidth, superSampledHeight);
		
		return saveImage(resultImageWidth, imageRGBData, pathToResultImage);
	}

	private Color getColorFromRay(Scene scene, Ray ray, int recursionDepth, RenderableObject objectToIgnore) {
		recursionDepth++;
		if (recursionDepth > scene.getMaximumNumberOfRecursions()) {
			return null;
		} // TODO

		Collision firstCollision = scene.getFirstCollision(ray, objectToIgnore);
		if (firstCollision == null) {
			return scene.getBackgroundColor();
		}

		Color rayColor = new Color(0, 0, 0);
		RenderableObject collisionObject = firstCollision.getCollisionObject();
		double transparency = collisionObject.getMaterial().transperancy;

		// calculating background color component:
		Color backGroundColor = getBackgroundColor(scene, ray, recursionDepth, firstCollision);
		if (backGroundColor != null) {
			backGroundColor = backGroundColor.getColorMultiplyByConstant(transparency);
			rayColor.add(backGroundColor);
		}

		// // calculating reflective color component: TODO ??
		Color reflectiveColor = getReflectiveColor(scene, ray, recursionDepth, firstCollision);
		if (reflectiveColor != null) {
			reflectiveColor = reflectiveColor.getColorMultiplyByColor(collisionObject.getMaterial().reflectionColor);
			rayColor.add(reflectiveColor);
		}

		Color specularAndDiffuseColor;
		{
			// calculate Diffuse Color Component:
			Color diffuseColor = getDiffuseColor(scene, ray, firstCollision);

			// calculate Specular Color Component:
			Color specularColor = getSpecularColor(scene, ray, firstCollision);

			// add them both
			diffuseColor.add(specularColor);
			specularAndDiffuseColor = diffuseColor;
		}

		specularAndDiffuseColor = specularAndDiffuseColor.getColorMultiplyByConstant(1 - transparency);
		rayColor.add(specularAndDiffuseColor);

		// Color += backgroundColor*transperancy (Color)
		// Color += (diffuse + specular) * (1 - transparency) (Color)
		// Color += (reflection color) * reflection (Color)
		return rayColor;

	}

	private Color getSpecularColor(Scene scene, Ray rayToCollision, Collision collision) {
		Color color = new Color(0, 0, 0);
		for (LightSource lightSource : scene.getLightSources()) {
			color.add(getSpecularColorFromLightSource(scene, rayToCollision, collision, lightSource));
		}
		return color.getColorMultiplyByColor(collision.getCollisionObject().getMaterial().specularColor);

		// mirror the ray.

	}

	private Color getSpecularColorFromLightSource(Scene scene, Ray rayToCollision, Collision collision,
			LightSource lightSource) {
		// TODO: soft shadow here as well? probably not(because then we won't be
		// able to do mirrors.
		Ray rayFromLightSourceToCollision = new Ray(lightSource.getPosition(),
				Vector3D.subtract(collision.getCollisionPoint(), lightSource.getPosition()));
		if (isNotCoveredFromLightSourcePoint(scene, collision.getCollisionPoint(), rayFromLightSourceToCollision)) {
			Vector3D highlightVecotr = rayFromLightSourceToCollision.direction
					.getReflectionVector(collision.getNormalToCollisionPoint());
			highlightVecotr.normalize();

			rayToCollision.direction.normalize();

			double cosAngle = Math.abs(Vector3D.dotProduct(rayToCollision.direction, highlightVecotr));
			double phongValue = Math.pow(cosAngle, collision.getCollisionObject().getMaterial().phongSpecularity);

			return lightSource.getLightColor()
					.getColorMultiplyByConstant(phongValue * lightSource.getSpecularIntensity());
		}
		return new Color(0, 0, 0);
	}

	private Color getDiffuseColor(Scene scene, Ray ray, Collision collision) {
		Color color = new Color(0, 0, 0);
		for (LightSource lightSource : scene.getLightSources()) {
			color.add(getDiffuseColorFromLightSource(scene, ray, collision, lightSource));
		}
		return color.getColorMultiplyByColor(collision.getCollisionObject().getMaterial().diffuseColor);
	}

	private Color getDiffuseColorFromLightSource(Scene scene, Ray ray, Collision collision, LightSource lightSource) {
		// TODO softShadow
		Ray rayFromLightSourceToCollision = new Ray(lightSource.getPosition(),
				Vector3D.subtract(collision.getCollisionPoint(), lightSource.getPosition()));
		rayFromLightSourceToCollision.direction.normalize();
		double cosAngle = Math.abs(
				Vector3D.dotProduct(rayFromLightSourceToCollision.direction, collision.getNormalToCollisionPoint()));
		Color fullyHitColor = lightSource.getLightColor().getColorMultiplyByConstant(cosAngle);

		if (isNotCoveredFromLightSourcePoint(scene, collision.getCollisionPoint(), rayFromLightSourceToCollision)) {
			return fullyHitColor;
		} else {
			return fullyHitColor.getColorMultiplyByConstant(1 - lightSource.getShadowIntensity());
		}
	}

	private boolean isNotCoveredFromLightSourcePoint(Scene scene, Vector3D pointToCheck,
			Ray rayFromLightSourceToPoint) {
		// TODO softShadow
		Collision collisionFromLightSource = scene.getFirstCollision(rayFromLightSourceToPoint, null);
		if (collisionFromLightSource == null) {
			throw new RuntimeException("should be impossible. must collide");
		}
		return (Vector3D.getPointsDistance(collisionFromLightSource.getCollisionPoint(), pointToCheck) < CLOSE_DOUBLE);
	}

	private Color getBackgroundColor(Scene scene, Ray ray, int recursionDepth, Collision collision) {
		Ray rayThroughObject = new Ray(collision.getCollisionPoint(), ray.direction);
		return getColorFromRay(scene, rayThroughObject, recursionDepth, collision.getCollisionObject());
	}

	private Color getReflectiveColor(Scene scene, Ray ray, int recursionDepth, Collision collision) {
		Ray reflectionRay = new Ray(collision.getCollisionPoint(),
				ray.direction.getReflectionVector(collision.getNormalToCollisionPoint()));
		return getColorFromRay(scene, reflectionRay, recursionDepth, collision.getCollisionObject());
	}

	private byte[] getImageRGBDataFromSuperSample(byte[] superSampledRGBData, int superSampledWidth, int superSampledHeight)
	{
		int imageWidth = (superSampledWidth/SUPER_SAMPLING_LEVEL);
		int imageHeight = (superSampledHeight/SUPER_SAMPLING_LEVEL);
		byte[] imageRGBData = new byte[imageHeight*imageWidth*3];
		
		int pixelRow,pixelCol;
		byte[] rgb;
		for(int i=0; i < imageRGBData.length/3; i+=3)
		{
			pixelRow = i / (imageWidth*3);
			pixelCol = i % (imageWidth*3);
			rgb = calculatePixelColorFromSuperSample(pixelRow, pixelCol, superSampledRGBData, superSampledWidth);
			System.arraycopy(rgb, 0, imageRGBData, i, 3);
		}
		return imageRGBData;
	}
	
	byte[] calculatePixelColorFromSuperSample(int pixelRow, int pixelCol, byte[] superSampledRGBData, int superSampledWidth)
	{
		byte[] rgb = {0,0,0};
		int offsetDueToFullRows = pixelRow*superSampledWidth*SUPER_SAMPLING_LEVEL*3;
		int offsetInLastRow = pixelCol*SUPER_SAMPLING_LEVEL*3;
		int startLocationInSuperSample = offsetDueToFullRows + offsetInLastRow;
		
		for (int i=0; i < SUPER_SAMPLING_LEVEL; i++)
		{
			for(int j=0; j < SUPER_SAMPLING_LEVEL; j++)
			{
				rgb[0] += superSampledRGBData[startLocationInSuperSample + (i*superSampledWidth*3) + (j*3)];
				rgb[1] += superSampledRGBData[startLocationInSuperSample + (i*superSampledWidth*3) + (j*3) + 1];
				rgb[2] += superSampledRGBData[startLocationInSuperSample + (i*superSampledWidth*3) + (j*3) + 2];
			}
		}
		rgb[0] /= SUPER_SAMPLING_LEVEL*SUPER_SAMPLING_LEVEL;
		rgb[1] /= SUPER_SAMPLING_LEVEL*SUPER_SAMPLING_LEVEL;
		rgb[2] /= SUPER_SAMPLING_LEVEL*SUPER_SAMPLING_LEVEL;
		return rgb;
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
