package RenderScene;

public class RenderablePlane extends RenderableObject {
	Vector3D planeNormal;
	double planeOffset;

	public RenderablePlane(Vector3D planeNormal, double planeOffset, Material material) {
		super(material);
		this.planeNormal = planeNormal;
		this.planeOffset = planeOffset;
	}

	@Override
	public Vector3D getNormalAtPoint(Vector3D point) {
		return planeNormal;
	}

}
