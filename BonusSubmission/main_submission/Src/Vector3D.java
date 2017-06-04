package RenderScene;

public class Vector3D {
	static final double CLOSE_DOUBLE = 1e-10;
	public double[] vector = new double[3];

	public Vector3D(double x, double y, double z) {
		this.vector[0] = x;
		this.vector[1] = y;
		this.vector[2] = z;
	}

	public Vector3D(Vector3D startPoint, Vector3D endPoint) {
		this.vector = subtract(endPoint, startPoint).vector;
	}

	public static Vector3D add(Vector3D vector1, Vector3D vector2) {
		Vector3D sumVector = new Vector3D(vector1.vector[0] + vector2.vector[0], vector1.vector[1] + vector2.vector[1],
				vector1.vector[2] + vector2.vector[2]);

		return sumVector;
	}

	public static Vector3D subtract(Vector3D vector1, Vector3D vector2) {
		Vector3D diffVector = new Vector3D(vector1.vector[0] - vector2.vector[0], vector1.vector[1] - vector2.vector[1],
				vector1.vector[2] - vector2.vector[2]);

		return diffVector;
	}

	public static Vector3D crossProduct(Vector3D vector1, Vector3D vector2) {
		double newX = vector1.vector[1] * vector2.vector[2] - vector1.vector[2] * vector2.vector[1];
		double newY = vector1.vector[2] * vector2.vector[0] - vector1.vector[0] * vector2.vector[2];
		double newZ = vector1.vector[0] * vector2.vector[1] - vector1.vector[1] * vector2.vector[0];

		return new Vector3D(newX, newY, newZ);

	}

	public static double dotProduct(Vector3D vector1, Vector3D vector2) {
		double dotProductResult = 0;
		for (int i = 0; i < 3; i++) {
			dotProductResult += vector1.vector[i] * vector2.vector[i];
		}
		return dotProductResult;
	}

	public static Vector3D max(Vector3D vector1, Vector3D vector2) {
		if (vector1.getMagnitude() >= vector2.getMagnitude()) {
			return vector1;
		} else {
			return vector2;
		}
	}

	public double getMagnitude() {
		return Math.sqrt(Math.pow((this.vector[0]), 2) + Math.pow((this.vector[1]), 2) + Math.pow((this.vector[2]), 2));
	}

	public void multiplyByConstant(double c) {
		this.vector[0] = c * this.vector[0];
		this.vector[1] = c * this.vector[1];
		this.vector[2] = c * this.vector[2];
	}

	public Vector3D getVectorMultipliedByConstant(double c) {
		Vector3D v = new Vector3D(c * this.vector[0], c * this.vector[1], c * this.vector[2]);
		return v;
	}

	public Vector3D getReversedVector() {
		return getVectorMultipliedByConstant(-1);
	}

	// changes the vector!!
	public void normalize() {
		this.multiplyByConstant(((double) 1) / this.getMagnitude());
	}

	public Vector3D getNormalizedVector() {
		return this.getVectorMultipliedByConstant(((double) 1) / this.getMagnitude());
	}

	public Vector3D getVectorInSameDirectionWithMagnitude(double mag) {
		return this.getNormalizedVector().getVectorMultipliedByConstant(mag);
	}

	public static double getPointsDistance(Vector3D point1, Vector3D point2) {
		return Math.abs(Vector3D.subtract(point1, point2).getMagnitude());
	}

	public Vector3D getReflectionVector(Vector3D normal) {
		double d = -2 * dotProduct(this, normal);
		return add(normal.getVectorMultipliedByConstant(d), this);
	}

	public static double getCosineOfAngleBetweenVectors(Vector3D vector1, Vector3D vector2) {
		double cosAngle = Math.abs(dotProduct(vector1, vector2));
		cosAngle /= vector1.getMagnitude();
		cosAngle /= vector2.getMagnitude();
		return cosAngle;
	}

	public static Vector3D[] getOrtogonalComplement(Vector3D vector) {
		Vector3D vx = null, vy = null;
		for (int i = 0; i < 3; i++) {
			if (vector.vector[i] != 0) {
				vx = new Vector3D(1, 1, 1);
				vx.vector[i] = 0;
				break;
			}
		}
		vy = crossProduct(vx, vector);
		vx = crossProduct(vy, vector);
		vy.normalize();
		vx.normalize();
		return new Vector3D[] { vx, vy };
	}

	public static Vector3D addRowColToStartingPosition(Vector3D startPoint, Vector3D rowVector, Vector3D colVector,
			double row, double col) {
		Vector3D vrow = rowVector.getVectorMultipliedByConstant(row);
		Vector3D vcol = colVector.getVectorMultipliedByConstant(col);

		return add(add(startPoint, vrow), vcol);
	}

	public boolean equals(Vector3D point2) {
		return (getPointsDistance(this, point2) < CLOSE_DOUBLE);
	}
}
