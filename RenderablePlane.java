package RenderScene;

public class RenderablePlane extends RenderableObject {
	Vector3D planeNormal;
	double planeOffset;

	public RenderablePlane(Vector3D planeNormal, double planeOffset, Material material) {
		super(material);
		this.planeOffset = planeOffset * planeNormal.getMagnitude();
		this.planeNormal = planeNormal;
		this.planeNormal.normalize();
	}

	@Override
	public Vector3D getNormalAtPoint(Vector3D point) {
		return planeNormal;
	}

	@Override
	public Vector3D getCollitionPoint(Ray ray) {
		double rayStartPointOffsetFromPlane= Vector3D.dotProduct(ray.startPosition, planeNormal) - planeOffset;
		
	}

}
