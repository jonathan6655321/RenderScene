package RenderScene;

public class LightSource {
	private Vector3D position;
	private double specularIntensity, shadowIntensity, lightWidth;
	private Color lightColor;

	public LightSource(Vector3D position, Color lightColor, double specularIntensity, double shadowIntensity,
			double lightWidth) {
		this.position = position;
		this.specularIntensity = specularIntensity;
		this.shadowIntensity = shadowIntensity;
		this.lightWidth = lightWidth;
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

	public double getLightWidth() {
		return lightWidth;
	}
}
