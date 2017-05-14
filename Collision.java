package RenderScene;

public class Collision {
	
	private RenderableObject collisionObject;
	private Vector3D collisionPoint;
	private Vector3D normalToCollisionPoint;
	
	public Collision(RenderableObject collisionObject, Vector3D collisionPoint, Vector3D normalToCollisionPoint)
	{
		this.collisionObject = collisionObject;
		this.collisionPoint = collisionPoint;
		this.normalToCollisionPoint = normalToCollisionPoint;
	}
}
