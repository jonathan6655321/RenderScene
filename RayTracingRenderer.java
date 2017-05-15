package RenderScene;

public class RayTracingRenderer implements IRenderer {
	final double CLOSE_DOUBLE = 0.00000001;

	public boolean renderScene(Scene scene, String pathToResultImage, int resultImageWidth, int resultImageHeight) {

		int superSampledWidth = resultImageWidth * scene.getSuperSampling();
		int superSampledHeight = resultImageHeight * scene.getSuperSampling();

		byte[] superSampledRGBData = renderSceneToRGBByteArray(scene, superSampledWidth, resultImageHeight);

		byte[] imageRGBData = ImageUtil.getImageRGBDataFromSuperSample(scene.getSuperSampling(), superSampledRGBData,
				superSampledWidth, superSampledHeight);

		return ImageUtil.saveImage(resultImageWidth, imageRGBData, pathToResultImage);
	}

	private byte[] renderSceneToRGBByteArray(Scene scene, int resultImageWidth, int resultImageHeight) {
		scene.getCamera().initScreenParams(resultImageHeight, resultImageWidth);
		byte[] imageRGBData = new byte[resultImageWidth * resultImageHeight * 3];

		for (int row = 0; row < resultImageHeight; row++) {
			for (int col = 0; col < resultImageWidth; col++) {
				Ray firstRay = scene.getCamera().getRayWhichLeavesFromPixel(row, col, resultImageHeight,
						resultImageWidth);

				byte[] color = getColorFromRay(scene, firstRay, 0, null).getColorByteArray();
				System.arraycopy(color, 0, imageRGBData, ((row * resultImageWidth) + col) * 3, 3);
			}
		}
		return imageRGBData;
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
		if (transparency != 0) {
			Color backGroundColor = getBackgroundColor(scene, ray, recursionDepth, firstCollision);
			if (backGroundColor != null) {
				backGroundColor = backGroundColor.getColorMultiplyByConstant(transparency);
				rayColor.add(backGroundColor);
			}
		}

		// // calculating reflective color component: TODO ??
		if (collisionObject.getMaterial().reflectionColor.isNotZero()) {
			Color reflectiveColor = getReflectiveColor(scene, ray, recursionDepth, firstCollision);
			if (reflectiveColor != null) {
				reflectiveColor = reflectiveColor
						.getColorMultiplyByColor(collisionObject.getMaterial().reflectionColor);
				rayColor.add(reflectiveColor);
			}
		}
		if (1 - transparency != 0) {
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
		}
		// Color += backgroundColor*transperancy (Color)
		// Color += (diffuse + specular) * (1 - transparency) (Color)
		// Color += (reflection color) * reflection (Color)
		return rayColor;

	}

	private static Color getSpecularColor(Scene scene, Ray rayToCollision, Collision collision) {
		Color color = new Color(0, 0, 0);
		for (LightSource lightSource : scene.getLightSources()) {
			color.add(getSpecularColorFromLightSource(scene, rayToCollision, collision, lightSource));
		}
		return color.getColorMultiplyByColor(collision.getCollisionObject().getMaterial().specularColor);

		// mirror the ray.

	}

	private static Color getSpecularColorFromLightSource(Scene scene, Ray rayToCollision, Collision collision,
			LightSource lightSource) {

		Ray rayFromLightSourceToCollision = new Ray(lightSource.getPosition(),
				Vector3D.subtract(collision.getCollisionPoint(), lightSource.getPosition()));
		Color fullyHitColor = lightSource.getLightColor();

		double precentOfFullyHitLight = getFullyHitLightPrecent_SoftShadow_Specular(scene, rayToCollision,
				rayFromLightSourceToCollision, collision, lightSource);
		return fullyHitColor.getColorMultiplyByConstant(precentOfFullyHitLight * lightSource.getShadowIntensity());

		// TODO: soft shadow here as well? probably not(because then we won't be
		// able to do mirrors.

		// Color fullyHitColor = lightSource.getLightColor()
		// .getColorMultiplyByConstant(phongValue *
		// lightSource.getSpecularIntensity());
		//
		// double precentOfFullyHitLight =
		// getFullyHitLight_CountingObjectTransperancy(scene,
		// rayFromLightSourceToCollision, collision, lightSource);
		// return
		// fullyHitColor.getColorMultiplyByConstant(precentOfFullyHitLight);

		// if (isNotCoveredFromLightSourcePoint(scene,
		// collision.getCollisionPoint(), rayFromLightSourceToCollision)) {
		// return colorFullyHit;
		// } else {
		// if
		// (isNotCoveredFromLightSourcePointBySameObject(collision.getCollisionObject(),
		// collision.getCollisionPoint(), rayFromLightSourceToCollision)) {
		// return colorFullyHit.getColorMultiplyByConstant(1 -
		// lightSource.getShadowIntensity());
		// }
		// return new Color(0, 0, 0);
		// }
	}

	private Color getDiffuseColor(Scene scene, Ray ray, Collision collision) {
		Color color = new Color(0, 0, 0);
		Vector3D collisionPoint = collision.getCollisionPoint();
		for (LightSource lightSource : scene.getLightSources()) {
			Ray rayFromLightSourceToCollision = lightSource.getRayToFromLightSourceToPoint(collisionPoint);

			double diffuseCoefficient = Vector3D.getCosineOfAngleBetweenVectors(rayFromLightSourceToCollision.direction,
					collision.getNormalToCollisionPoint());

			Color currentRayColor = getColorFromLightSourceAndCollision(scene, ray, rayFromLightSourceToCollision,
					collision, lightSource);

			currentRayColor = currentRayColor.getColorMultiplyByConstant(diffuseCoefficient);
			color.add(currentRayColor);
		}
		return color.getColorMultiplyByColor(collision.getCollisionObject().getMaterial().diffuseColor);
	}

	private Color getColorFromLightSourceAndCollision(Scene scene, Ray ray, Ray rayFromLightSourceToCollision,
			Collision collision, LightSource lightSource) {
		Color fullyHitColor = lightSource.getLightColor();
		double precentOfFullyHitLight = getLightRaySoftShadowPrecent(scene, rayFromLightSourceToCollision, collision,
				lightSource);
		return fullyHitColor.getColorMultiplyByConstant(precentOfFullyHitLight);

	}

	private double getLightRaySoftShadowPrecent(Scene scene, Ray rayFromLightSourceToCollision, Collision collision,
			LightSource lightSource) {
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
				double precentOfFullyHitLight = getFullyHitLight_CountingObjectTransperancy(scene,
						rayFromLightSourceToCollision, collision, lightSource);
				precent += precentOfFullyHitLight / numberOfShadowRay;
			}
		}

		return precent;
	}

	// private double getFullyHitLightPrecent_SoftShadow_Diffuse(Scene scene,
	// Ray rayFromLightSourceToCollision,
	// Collision collision, LightSource lightSource) {
	// Vector3D vectorX = null, vectorY;
	// int rootNumberOfShadowRay = scene.getRootNumberOfShadowRay();
	// for (int i = 0; i < 3; i++) {
	// vectorX = new Vector3D(0, 0, 0);
	// vectorX.vector[i] = 1;
	// if (Vector3D.dotProduct(vectorX, rayFromLightSourceToCollision.direction)
	// != 0) {
	// break;
	// }
	// }
	// vectorY = Vector3D.crossProduct(rayFromLightSourceToCollision.direction,
	// vectorX);
	// vectorX = Vector3D.crossProduct(rayFromLightSourceToCollision.direction,
	// vectorY);
	// vectorY = vectorY
	// .getVectorInSameDirectionWithMagnitude(2 * lightSource.getLightRadius() /
	// rootNumberOfShadowRay);
	// vectorX = vectorX
	// .getVectorInSameDirectionWithMagnitude(2 * lightSource.getLightRadius() /
	// rootNumberOfShadowRay);
	//
	// Vector3D lightSourcePoint;
	// double precent = 0;
	// for (int i = -(rootNumberOfShadowRay / 2); i < rootNumberOfShadowRay -
	// (rootNumberOfShadowRay / 2); i++) {
	// for (int j = -(rootNumberOfShadowRay / 2); j < rootNumberOfShadowRay -
	// (rootNumberOfShadowRay / 2); j++) {
	// lightSourcePoint = Vector3D.add(lightSource.getPosition(),
	// vectorX.getVectorMultipliedByConstant(i));
	// lightSourcePoint = Vector3D.add(lightSourcePoint,
	// vectorY.getVectorMultipliedByConstant(i));
	// rayFromLightSourceToCollision = new Ray(lightSourcePoint,
	// Vector3D.subtract(collision.getCollisionPoint(), lightSourcePoint));
	//
	// double cosAngle =
	// Math.abs(Vector3D.dotProduct(rayFromLightSourceToCollision.direction,
	// collision.getNormalToCollisionPoint()));
	// cosAngle /= rayFromLightSourceToCollision.direction.getMagnitude();
	//
	// double precentOfFullyHitLight =
	// getFullyHitLight_CountingObjectTransperancy(scene,
	// rayFromLightSourceToCollision, collision, lightSource);
	// precent += precentOfFullyHitLight * cosAngle * precentOfFullyHitLight
	// / (rootNumberOfShadowRay * rootNumberOfShadowRay);
	// }
	// }
	// return precent;
	// }

	private double getFullyHitLightPrecent_SoftShadow_Specular(Scene scene, Ray rayToCollision,
			Ray rayFromLightSourceToCollision, Collision collision, LightSource lightSource) {
		Vector3D vectorX = null, vectorY;
		int rootNumberOfShadowRay = scene.getRootNumberOfShadowRay();
		for (int i = 0; i < 3; i++) {
			vectorX = new Vector3D(0, 0, 0);
			vectorX.vector[i] = 1;
			if (Vector3D.dotProduct(vectorX, rayFromLightSourceToCollision.direction) != 0) {
				break;
			}
		}
		vectorY = Vector3D.crossProduct(rayFromLightSourceToCollision.direction, vectorX);
		vectorX = Vector3D.crossProduct(rayFromLightSourceToCollision.direction, vectorY);
		vectorY = vectorY
				.getVectorInSameDirectionWithMagnitude(2 * lightSource.getLightRadius() / rootNumberOfShadowRay);
		vectorX = vectorX
				.getVectorInSameDirectionWithMagnitude(2 * lightSource.getLightRadius() / rootNumberOfShadowRay);

		Vector3D lightSourcePoint;
		double precent = 0;
		for (int i = -(rootNumberOfShadowRay / 2); i < rootNumberOfShadowRay - (rootNumberOfShadowRay / 2); i++) {
			for (int j = -(rootNumberOfShadowRay / 2); j < rootNumberOfShadowRay - (rootNumberOfShadowRay / 2); j++) {
				lightSourcePoint = Vector3D.add(lightSource.getPosition(), vectorX.getVectorMultipliedByConstant(i));
				lightSourcePoint = Vector3D.add(lightSourcePoint, vectorY.getVectorMultipliedByConstant(i));
				rayFromLightSourceToCollision = new Ray(lightSourcePoint,
						Vector3D.subtract(collision.getCollisionPoint(), lightSourcePoint));

				Vector3D highlightVecotr = rayFromLightSourceToCollision.direction
						.getReflectionVector(collision.getNormalToCollisionPoint());
				highlightVecotr.normalize();

				// rayToCollision.direction.normalize();

				double cosAngle = Math.abs(Vector3D.dotProduct(rayToCollision.direction, highlightVecotr));
				cosAngle /= rayToCollision.direction.getMagnitude();
				double phongValue = Math.pow(cosAngle, collision.getCollisionObject().getMaterial().phongSpecularity);

				double precentOfFullyHitLight = getFullyHitLight_CountingObjectTransperancy(scene,
						rayFromLightSourceToCollision, collision, lightSource);
				precent += precentOfFullyHitLight * phongValue * precentOfFullyHitLight
						/ (rootNumberOfShadowRay * rootNumberOfShadowRay);
			}
		}
		return precent;
	}

	private double getFullyHitLight_CountingObjectTransperancy(Scene scene, Ray rayFromLightSourceToCollision,
			Collision collision, LightSource lightSource) {
		double precent = 1;
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
		if (isCoveredFromLightSourcePointBySameObject(collision.getCollisionObject(), collision.getCollisionPoint(),
				rayFromLightSourceToCollision)) {
			return 0;
		}
		return precent;
	}

	private boolean isCoveredFromLightSourcePointBySameObject(RenderableObject rObj, Vector3D pointToCheck,
			Ray rayFromLightSourceToPoint) {
		Collision collisionFromLightSource = rObj.getCollision(rayFromLightSourceToPoint);
		if (collisionFromLightSource == null) {
			// TODO:throw new RuntimeException("should be impossible. must
			// collide");
		}
		return (Vector3D.getPointsDistance(collisionFromLightSource.getCollisionPoint(), pointToCheck) > CLOSE_DOUBLE);
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
}
