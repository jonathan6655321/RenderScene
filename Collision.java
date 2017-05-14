package RenderScene;

public class Collision {
	
	private RenderableObject collisionObject;
	private Vector3D collisionPoint;
	private Vector3D normalToCollisionPoint = null;
	
	public Collision(RenderableObject collisionObject, Vector3D collisionPoint)
	{
		this.collisionObject = collisionObject;
		this.collisionPoint = collisionPoint;
	}
	
	public Vector3D getNormalToCollisionPoint(){
		if(normalToCollisionPoint == null){
			normalToCollisionPoint= collisionObject.getNormalAtPoint(collisionPoint);
		}
		return normalToCollisionPoint;
	}
}
