package RenderScene;

import java.util.ArrayList;

public class RayTracingUtil {

	public static Color getColorFromRay(Scene scene, Ray ray, int recursionDepth, RenderableObject objectToIgnore) {
		recursionDepth++;
		if (recursionDepth > scene.getMaximumNumberOfRecursions()) {
			return null;
		}

		Collision collision = scene.getFirstCollisionWithRay(ray, objectToIgnore);
		if (collision == null) {
			return scene.getBackgroundColor();
		}

		Color rayColor = new Color(0, 0, 0);
		addBackgroundColor(rayColor, scene, ray, recursionDepth, collision);
		addRefleciveColor(rayColor, scene, ray, recursionDepth, collision);
		addSpecularAndDiffuseColor(rayColor, scene, ray, recursionDepth, collision);
		return rayColor;

	}

	private static void addBackgroundColor(Color rayColor, Scene scene, Ray ray, int recursionDepth,
			Collision collision) {
		double transparency = collision.getCollisionObject().getMaterial().transperancy;
		if (transparency != 0) {
			Color backGroundColor = getBackgroundColor(scene, ray, recursionDepth, collision);
			if (backGroundColor != null) {
				backGroundColor = backGroundColor.getColorMultiplyByConstant(transparency);
				rayColor.add(backGroundColor);
			}
		}
	}

	private static void addRefleciveColor(Color rayColor, Scene scene, Ray ray, int recursionDepth,
			Collision collision) {
		Color reflectionColor = collision.getCollisionObject().getMaterial().reflectionColor;
		if (reflectionColor.isNotZero()) {
			Color reflectiveColor = getReflectiveColor(scene, ray, recursionDepth, collision);
			if (reflectiveColor != null) {
				reflectiveColor = reflectiveColor.getColorMultiplyByColor(reflectionColor);
				rayColor.add(reflectiveColor);
			}
		}
	}

	private static Color getBackgroundColor(Scene scene, Ray ray, int recursionDepth, Collision collision) {
		Ray rayThroughObject = new Ray(collision.getCollisionPoint(), ray.direction);
		return getColorFromRay(scene, rayThroughObject, recursionDepth, collision.getCollisionObject());
	}

	private static Color getReflectiveColor(Scene scene, Ray ray, int recursionDepth, Collision collision) {
		Ray reflectionRay = new Ray(collision.getCollisionPoint(),
				ray.direction.getReflectionVector(collision.getNormalToCollisionPoint()));
		return getColorFromRay(scene, reflectionRay, recursionDepth, collision.getCollisionObject());
	}

	private static void addSpecularAndDiffuseColor(Color rayColor, Scene scene, Ray ray, int recursionDepth,
			Collision collision) {
		double transparency = collision.getCollisionObject().getMaterial().transperancy;
		if (1 - transparency != 0) {
			Color speculateColor = new Color(0, 0, 0);
			Color diffuseColor = new Color(0, 0, 0);
			for (LightSource lightSource : scene.getLightSources()) {
				Color lightSourceColorOnObject = getColorFromLightSourceAndCollision(scene, collision, lightSource);

				diffuseColor.add(getDiffuseColorFromCollisionAndLightSource(scene, collision, lightSource,
						lightSourceColorOnObject));
				speculateColor.add(getSpecularColorFromCollisionAndLightSource(scene, ray, collision, lightSource,
						lightSourceColorOnObject));
			}

			diffuseColor = diffuseColor.getColorMultiplyByConstant(1 - transparency);
			speculateColor = speculateColor.getColorMultiplyByConstant(1 - transparency);

			diffuseColor = diffuseColor
					.getColorMultiplyByColor(collision.getCollisionObject().getMaterial().diffuseColor);
			speculateColor = speculateColor
					.getColorMultiplyByColor(collision.getCollisionObject().getMaterial().specularColor);

			rayColor.add(diffuseColor);
			rayColor.add(speculateColor);

		}

	}

	private static Color getDiffuseColorFromCollisionAndLightSource(Scene scene, Collision collision,
			LightSource lightSource, Color lightSourceColorOnObject) {
		Vector3D collisionPoint = collision.getCollisionPoint();
		Ray rayFromLightSourceToCollision = lightSource.getRayToFromLightSourceToPoint(collisionPoint);

		double diffuseCoefficient = Vector3D.getCosineOfAngleBetweenVectors(rayFromLightSourceToCollision.direction,
				collision.getNormalToCollisionPoint());

		return lightSourceColorOnObject.getColorMultiplyByConstant(diffuseCoefficient);
	}

	private static Color getSpecularColorFromCollisionAndLightSource(Scene scene, Ray rayToCollision,
			Collision collision, LightSource lightSource, Color lightSourceColorOnObject) {
		Vector3D collisionPoint = collision.getCollisionPoint();
		Ray rayFromLightSourceToCollision = lightSource.getRayToFromLightSourceToPoint(collisionPoint);
		Vector3D highlightVecotr = rayFromLightSourceToCollision.direction
				.getReflectionVector(collision.getNormalToCollisionPoint());

		double cosAngle = Vector3D.getCosineOfAngleBetweenVectors(rayToCollision.direction, highlightVecotr);
		double phongValue = Math.pow(cosAngle, collision.getCollisionObject().getMaterial().phongSpecularity);

		lightSourceColorOnObject = lightSourceColorOnObject
				.getColorMultiplyByConstant(phongValue * lightSource.getSpecularIntensity());

		return lightSourceColorOnObject;

	}

	private static Color getColorFromLightSourceAndCollision(Scene scene, Collision collision,
			LightSource lightSource) {
		Ray rayFromLightSourceToCollision = lightSource.getRayToFromLightSourceToPoint(collision.getCollisionPoint());
		Color fullyHitColor = lightSource.getLightColor();
		double precentOfFullyHitLight = getLightRaySoftShadowPrecent(scene, rayFromLightSourceToCollision, collision,
				lightSource);
		return fullyHitColor.getColorMultiplyByConstant(precentOfFullyHitLight);
	}

	private static double getLightRaySoftShadowPrecent(Scene scene, Ray rayFromLightSourceToCollision,
			Collision collision, LightSource lightSource) {
		int rootNumberOfShadowRay = scene.getRootNumberOfShadowRay();
		int numberOfShadowRay = rootNumberOfShadowRay * rootNumberOfShadowRay;
		Vector3D[] vectors = Vector3D.getOrtogonalComplement(rayFromLightSourceToCollision.direction);
		// make both vector have the needed magnitude for the radius
		for (int i = 0; i < 2; i++)
			vectors[i] = vectors[i]
					.getVectorInSameDirectionWithMagnitude(2 * lightSource.getLightRadius() / rootNumberOfShadowRay);

		// for each source point check how much hit:
		Vector3D lightSourcePoint;
		double precent = 0;
		for (int row = -(rootNumberOfShadowRay / 2); row < rootNumberOfShadowRay - (rootNumberOfShadowRay / 2); row++) {
			for (int col = -(rootNumberOfShadowRay / 2); col < rootNumberOfShadowRay
					- (rootNumberOfShadowRay / 2); col++) {
				lightSourcePoint = Vector3D.addRowColToStartingPosition(lightSource.getPosition(), vectors[0],
						vectors[1], row, col);
				rayFromLightSourceToCollision = new Ray(lightSourcePoint,
						Vector3D.subtract(collision.getCollisionPoint(), lightSourcePoint));
				double precentOfFullyHitLight = getLightSourceHitCoefficence(scene, rayFromLightSourceToCollision,
						collision, lightSource);
				precent += precentOfFullyHitLight / numberOfShadowRay;
			}
		}

		return precent;
	}

	private static double getLightSourceHitCoefficence(Scene scene, Ray rayFromLightSourceToCollision,
			Collision collision, LightSource lightSource) {
		double precent = 1;
		if (isCoveredFromLightSourcePointBySameObject(collision, rayFromLightSourceToCollision)) {
			return 0;
		}
		double minDistance = Vector3D.getPointsDistance(rayFromLightSourceToCollision.startPosition,
				collision.getCollisionPoint());
		ArrayList<Collision> collisionsWithOtherObjectArray = scene.getAllCollision(rayFromLightSourceToCollision,
				minDistance, collision.getCollisionObject());
		for (Collision collisionWithOtherObject : collisionsWithOtherObjectArray) {
			precent *= collisionWithOtherObject.getCollisionObject().getMaterial().transperancy;
		}
		if (!collisionsWithOtherObjectArray.isEmpty()) {
			precent = Math.max(1 - lightSource.getShadowIntensity(), precent);
		}
		return precent;
	}

	private static boolean isCoveredFromLightSourcePointBySameObject(Collision collision,
			Ray rayFromLightSourceToPoint) {
		Collision collisionFromLightSource = collision.getCollisionObject().getCollision(rayFromLightSourceToPoint);
		if (collisionFromLightSource == null) {
			return false;
		}
		return !collisionFromLightSource.getCollisionPoint().equals(collision.getCollisionPoint());
	}

}
