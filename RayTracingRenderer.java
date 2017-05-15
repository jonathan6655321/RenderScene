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
	final double CLOSE_DOUBLE = 0.000000001;

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
			backGroundColor.multiplyByConstant(transparency);
			rayColor.add(backGroundColor);
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

		specularAndDiffuseColor.multiplyByConstant(1 - transparency);
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
