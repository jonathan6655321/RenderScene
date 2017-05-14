package RenderScene;

public class RenderableSphere extends RenderableObject{
	Vector3D sphereCenterPosition;
	double sphereRadius;
	
	public RenderableSphere(Vector3D sphereCenterPosition, double sphereRadius, Material material) {
		super(material);
		this.sphereCenterPosition = sphereCenterPosition;
		this.sphereRadius = sphereRadius;
	}

	@Override
	public Vector3D getNormalAtPoint(Vector3D point) {
		return new Vector3D(sphereCenterPosition, point);
	}

	@Override
	public Collision getCollision(Ray ray) {
		// TODO Auto-generated method stub
		return null;
	}

}
