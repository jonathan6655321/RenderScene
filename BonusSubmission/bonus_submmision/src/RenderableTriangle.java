package RenderScene;

public class RenderableTriangle extends RenderablePlane {
	private Vector3D triangleVertex;
	private Vector3D[] triangleVectors = new Vector3D[2];
	private Vector3D[] vertices = new Vector3D[2];
	private double[] vectorsMagnitude = new double[2];

	public RenderableTriangle(Vector3D[] vertices, Material material) {
		super(getNormalFromPoints(vertices), vertices[0], material);
		this.vertices = vertices;
		this.triangleVertex = vertices[0];
		for (int i = 0; i < 2; i++) {
			this.triangleVectors[i] = Vector3D.subtract(vertices[i + 1], triangleVertex);
			this.vectorsMagnitude[i] = this.triangleVectors[i].getMagnitude();
		}
	}

	private static Vector3D getNormalFromPoints(Vector3D[] vertices) {
		Vector3D vector1 = Vector3D.subtract(vertices[1], vertices[0]);
		Vector3D vector2 = Vector3D.subtract(vertices[2], vertices[0]);

		return Vector3D.crossProduct(vector1, vector2);
	}

	@Override
	public Collision getCollision(Ray ray) {

		Collision collision = super.getCollision(ray);
		if (collision == null) {
			return null;
		}

		// is I inside T?
		double uu, uv, vv, wu, wv, D;
		uu = Vector3D.dotProduct(triangleVectors[0], triangleVectors[0]);
		uv = Vector3D.dotProduct(triangleVectors[0], triangleVectors[1]);
		vv = Vector3D.dotProduct(triangleVectors[1], triangleVectors[1]);
		Vector3D w = Vector3D.subtract(collision.getCollisionPoint(), triangleVertex);
		wu = Vector3D.dotProduct(w, triangleVectors[0]);
		wv = Vector3D.dotProduct(w, triangleVectors[1]);
		D = uv * uv - uu * vv;

		// get and test parametric coords
		double s, t;
		s = (uv * wv - vv * wu) / D;
		if (s < 0.0 || s > 1.0) // I is outside T
			return null;
		t = (uv * wu - uu * wv) / D;
		if (t < 0.0 || (s + t) > 1.0) // I is outside T
			return null;

		return collision; // I is in T

	}

	@Override
	public boolean isFinite() {
		return true;
	}

	@Override
	public double getMaxDistanceFromPoint(Vector3D point1) {
		double maxDistance = 0;
		for (Vector3D point2 : vertices) {
			maxDistance = Math.max(Vector3D.getPointsDistance(point1, point2), maxDistance);
		}
		return maxDistance;
	}

	@Override
	public Vector3D getObjetCenter() {
		Vector3D point1 = Vector3D.add(Vector3D.add(vertices[0], vertices[1]), vertices[2]);
		point1 = point1.getVectorMultipliedByConstant((((double) 1) / (double) 3));
		return point1;
	}
}
