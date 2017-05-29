package RenderScene;

public abstract class RenderableObject {
	private Material material;
	
	public abstract Vector3D getNormalAtPoint(Vector3D point);
	public abstract Collision getCollision(Ray ray);

	public RenderableObject(Material material){
		this.material = material;
	}
	
	public abstract boolean isFinite();
	
	public abstract double getMaxDistanceFromPoint(Vector3D point1);

	
	public abstract Vector3D getObjetCenter();
	
	public Material getMaterial() {
		return material;
	}
}
