package RenderScene;

import javax.management.RuntimeErrorException;

import sun.invoke.util.VerifyType;

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
		Vector3D thirdVector = Vector3D.subtract(vertices[1], vertices[2]);

		Vector3D medianVector = Vector3D.subtract(avrageOfVertex1Vertex2, triangleVertex);
		Vector3D perpendicularVector = Vector3D.crossProduct(thirdVector, planeNormal);
		perpendicularVector.normalize();

		
		double pendicularVectorComponentInMedian = Vector3D.dotProduct(perpendicularVector, medianVector);
		perpendicularVector = perpendicularVector.getVectorMultipliedByConstant(pendicularVectorComponentInMedian);
		
		double senityCheck = Vector3D.dotProduct(perpendicularVector, thirdVector);

		this.triangleVectors[2] = perpendicularVector;
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
		
		/*
		
		Vector3D vectorToColitionFromVertex = Vector3D.subtract(collision.getCollisionPoint(), triangleVertex);
		for (int i = 0; i < 3; i++) {
			double componentInCollisionVector = Vector3D.dotProduct(vectorToColitionFromVertex, triangleVectors[i]);
			if (componentInCollisionVector < 0 || componentInCollisionVector > vectorsMagnitude[i]) {
				return null;
			}
		}
		return collision;*/
		
	    // is I inside T?
	    double    uu, uv, vv, wu, wv, D;
	    uu = Vector3D.dotProduct(triangleVectors[0],triangleVectors[0]);
	    uv = Vector3D.dotProduct(triangleVectors[0],triangleVectors[1]);
	    vv = Vector3D.dotProduct(triangleVectors[1],triangleVectors[1]);
	    Vector3D w = Vector3D.subtract(collision.getCollisionPoint(), triangleVertex);
	    wu = Vector3D.dotProduct(w,triangleVectors[0]);
	    wv = Vector3D.dotProduct(w,triangleVectors[1]);
	    D = uv * uv - uu * vv;

	    // get and test parametric coords
	    double s, t;
	    s = (uv * wv - vv * wu) / D;
	    if (s < 0.0 || s > 1.0)         // I is outside T
	        return null;
	    t = (uv * wu - uu * wv) / D;
	    if (t < 0.0 || (s + t) > 1.0)  // I is outside T
	        return null;

	    return collision;                       // I is in T

	}

}
