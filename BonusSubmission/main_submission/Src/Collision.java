package RenderScene;

public class Collision {

	private RenderableObject collisionObject;
	private Vector3D collisionPoint;
	private Vector3D normalToCollisionPoint = null;

	public Collision(RenderableObject collisionObject, Vector3D collisionPoint) {
		this.collisionObject = collisionObject;
		this.collisionPoint = collisionPoint;
	}

	public Collision(RenderableObject collisionObject, Vector3D collisionPoint, Vector3D normalToCollisionPoint) {
		this.collisionObject = collisionObject;
		this.collisionPoint = collisionPoint;
		this.normalToCollisionPoint = normalToCollisionPoint;
	}

	public Vector3D getNormalToCollisionPoint() {
		if (normalToCollisionPoint == null) {
			normalToCollisionPoint = collisionObject.getNormalAtPoint(collisionPoint);
			normalToCollisionPoint.normalize();
		}
		return normalToCollisionPoint;
	}

	public Vector3D getCollisionPoint() {
		return collisionPoint;
	}

	public RenderableObject getCollisionObject() {
		return collisionObject;
	}
}
