package RenderScene;

public class RenderableTriangle extends RenderablePlane {
	Vector3D triangleVertex;
	Vector3D[] triangleVectors = new Vector3D[3];
	double[] vectorsMagnitude = new double[3];

	public RenderableTriangle(Vector3D[] vertices, Material material) {
		super(getNormalFromPoints(vertices), vertices[0], material);
		this.triangleVertex = vertices[0];
		for (int i = 0; i < 2; i++) {
			this.triangleVectors[i] = Vector3D.subtract(vertices[i + 1], triangleVertex);
			this.vectorsMagnitude[i] = this.triangleVectors[i].getMagnitude();
			this.triangleVectors[i].normalize();
		}
		Vector3D avrageOfVertex1Vertex2 = Vector3D.add(vertices[1], vertices[2]).getVectorMultipliedByConstant(0.5);
		this.triangleVectors[2] =Vector3D.subtract(avrageOfVertex1Vertex2, triangleVertex);
		
		this.vectorsMagnitude[2] = this.triangleVectors[2].getMagnitude();
		this.triangleVectors[2].normalize();
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
		Vector3D vectorToColitionFromVertex = Vector3D.subtract(collision.getCollisionPoint(), triangleVertex);
		for (int i = 0; i < 3; i++) {
			double componentInCollisionVector = Vector3D.dotProduct(vectorToColitionFromVertex, triangleVectors[i]);
			if (componentInCollisionVector < 0 || componentInCollisionVector > vectorsMagnitude[i]) {
				return null;
			}
		}
		return collision;
	}

}
