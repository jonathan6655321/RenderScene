package RenderScene;

public class Material {
	public Color diffuseColor, specularColor, reflectionColor;
	int phongSpecularity;
	double transperancy;

	public Material(Color diffuseColor, Color specularColor, Color reflectionColor, int phongSpecularity,
			double transperancy) {
		this.diffuseColor = diffuseColor;
		this.specularColor = specularColor;
		this.reflectionColor = reflectionColor;
		this.phongSpecularity = phongSpecularity;
		this.transperancy = transperancy;
	}
}
