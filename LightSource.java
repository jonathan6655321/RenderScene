package RenderScene;

public class LightSource {
	private Vector3D position;
	private double specularIntensity, shadowIntensity, lightRadius;
	private Color lightColor;

	public LightSource(Vector3D position, Color lightColor, double specularIntensity, double shadowIntensity,
			double lightRadius) {
		this.position = position;
		this.specularIntensity = specularIntensity;
		this.shadowIntensity = shadowIntensity;
		this.lightRadius = lightRadius;
		this.lightColor = lightColor;
	}

	public Vector3D getPosition() {
		return position;
	}

	public double getSpecularIntensity() {
		return specularIntensity;
	}

	public double getShadowIntensity() {
		return shadowIntensity;
	}

	public Color getLightColor() {
		return lightColor;
	}

	public double getLightRadius() {
		return lightRadius;
	}
	
	public Ray getRayToFromLightSourceToPoint(Vector3D point){
		return new Ray(position, Vector3D.subtract(point, position));
	}
}
