package RenderScene;

public abstract class RenderableObject {
	private Material material;
	
	public abstract Vector3D getNormalAtPoint(Vector3D point);
	public abstract Vector3D getCollisionPoint(Ray ray);

	public RenderableObject(Material material){
		this.material = material;
	}
	
	
	
	public Material getMaterial() {
		return material;
	}
}
