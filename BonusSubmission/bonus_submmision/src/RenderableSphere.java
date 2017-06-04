package RenderScene;

public class RenderableSphere extends RenderableObject {
	Vector3D sphereCenterPosition;
	double sphereRadius;

	public RenderableSphere(Vector3D sphereCenterPosition, double sphereRadius, Material material) {
		super(material);
		this.sphereCenterPosition = sphereCenterPosition;
		this.sphereRadius = sphereRadius;
	}

	public Vector3D getNormalAtPoint(Vector3D point) {
		Vector3D normal = new Vector3D(sphereCenterPosition, point);
		normal.normalize();
		return normal;
	}

	/*
	 * see: https://en.wikipedia.org/wiki/Line%E2%80%93sphere_intersection
	 */
	public Collision getCollision(Ray ray) {
		ray.direction.normalize();

		// see link to understand notation
		Vector3D oMinusC = Vector3D.subtract(ray.startPosition, this.sphereCenterPosition);
		double iDotOMinusC = Vector3D.dotProduct(ray.direction, oMinusC);

		double determinant = Math.pow(iDotOMinusC, 2) - Math.pow(oMinusC.getMagnitude(), 2) + Math.pow(sphereRadius, 2);

		Vector3D collisionPoint;
		double distanceFromRayOriginToCollision;
		if (determinant < 0) {
			return null;
		} else if (determinant == 0) // one collision point line is "MESHIK"
		{
			distanceFromRayOriginToCollision = -iDotOMinusC;
			if (distanceFromRayOriginToCollision <= 0) // TODO <= or <?
			{
				return null;
			} else {
				collisionPoint = Vector3D.add(ray.startPosition,
						ray.direction.getVectorInSameDirectionWithMagnitude(distanceFromRayOriginToCollision));
				return new Collision(this, collisionPoint);
			}
		} else // determinant > 0
		{
			double distanceFromRayOriginToCollision1 = -iDotOMinusC + Math.sqrt(determinant);
			double distanceFromRayOriginToCollision2 = -iDotOMinusC - Math.sqrt(determinant);

			// TODO
			if ((distanceFromRayOriginToCollision1 <= 0) && (distanceFromRayOriginToCollision2 <= 0)) {
				return null;
			} else if ((distanceFromRayOriginToCollision1 > 0) && (distanceFromRayOriginToCollision2 > 0)) {
				// get the closer of the two points
				distanceFromRayOriginToCollision = Math.min(distanceFromRayOriginToCollision1,
						distanceFromRayOriginToCollision2);

				collisionPoint = Vector3D.add(ray.startPosition,
						ray.direction.getVectorInSameDirectionWithMagnitude(distanceFromRayOriginToCollision));
				return new Collision(this, collisionPoint);
			} else // collision from inside the sphere! WE DONT NEED THIS!!
			{
				return null;
//				// get the positive distance of the two collisions (in the
//				// direction of the ray)
//				distanceFromRayOriginToCollision = Math.max(distanceFromRayOriginToCollision1,
//						distanceFromRayOriginToCollision2);
//
//				collisionPoint = Vector3D.add(ray.startPosition,
//						ray.direction.getVectorInSameDirectionWithMagnitude(distanceFromRayOriginToCollision));
//				return new Collision(this, collisionPoint, getNormalAtPoint(collisionPoint).getReversedVector());
			}
		}
	}

	@Override
	public boolean isFinite() {
		return true;
	}

	@Override
	public double getMaxDistanceFromPoint(Vector3D point1) {
		return Vector3D.getPointsDistance(point1, sphereCenterPosition) + sphereRadius;
	}

	@Override
	public Vector3D getObjetCenter() {
		return sphereCenterPosition;
	}

	public double getRadius() {
		return sphereRadius;
	}

}
