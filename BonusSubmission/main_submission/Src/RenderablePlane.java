package RenderScene;

public class RenderablePlane extends RenderableObject {
	Vector3D planeNormal;
	double planeOffset;

	public RenderablePlane(Vector3D planeNormal, double planeOffset, Material material) {
		super(material);
		this.planeOffset = planeOffset * planeNormal.getMagnitude();
		this.planeNormal = planeNormal;
		this.planeNormal.normalize();
		if (this.planeOffset < 0) {
			this.planeOffset *= -1;
			this.planeNormal = this.planeNormal.getReversedVector();
		}

	}

	public RenderablePlane(Vector3D planeNormal, Vector3D pointOnPlane, Material material) {
		super(material);
		this.planeNormal = planeNormal;
		this.planeNormal.normalize();
		this.planeOffset = Vector3D.dotProduct(planeNormal, pointOnPlane);
		if (this.planeOffset < 0) {
			this.planeOffset *= -1;
			this.planeNormal = this.planeNormal.getReversedVector();
		}
	}

	@Override
	public Vector3D getNormalAtPoint(Vector3D point) {
		return planeNormal;
	}

	@Override
	public Collision getCollision(Ray ray) {
		double rayStartPointOffsetFromPlane = planeOffset - Vector3D.dotProduct(ray.startPosition, planeNormal);
		if (rayStartPointOffsetFromPlane == 0) {
			return new Collision(this, ray.startPosition);
		}
		double normalComponentInRayDirection = Vector3D.dotProduct(ray.direction, planeNormal);
		double numberOfDirectionVectorsToPlane = rayStartPointOffsetFromPlane / normalComponentInRayDirection;
		if (numberOfDirectionVectorsToPlane < 0 || numberOfDirectionVectorsToPlane == Double.POSITIVE_INFINITY) {// no
																													// collision
																													// in
																													// this
																													// direction.
			return null;
		}
		Vector3D vectorToPlaneCollision = ray.direction.getVectorMultipliedByConstant(numberOfDirectionVectorsToPlane);

		Vector3D collisionPoint = Vector3D.add(ray.startPosition, vectorToPlaneCollision);
		if (Vector3D.dotProduct(planeNormal, ray.startPosition) - planeOffset > 0) {
			return new Collision(this, collisionPoint);
		} else {
			return new Collision(this, collisionPoint, planeNormal.getReversedVector());
		}
	}

	@Override
	public boolean isFinite() {
		return false;
	}

	@Override
	public double getMaxDistanceFromPoint(Vector3D point1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Vector3D getObjetCenter() {
		return null;
	}

}
