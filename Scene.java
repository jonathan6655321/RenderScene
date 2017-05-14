package RenderScene;

import java.util.ArrayList;

public class Scene {

	private ArrayList<RenderableObject> objectsInScene = new ArrayList<>();
	private ArrayList<LightSource> lightSourcesInScene = new ArrayList<>();
	private Camera camera;
	private Color backgroundColor;
	int rootNumberOfShadowRay;
	int maximumNumberOfRecurtsions;
	int superSampling;

	public Scene(Color backgroundColor, int rootNumberOfShadowRay, int maximumNumberOfRecurtsions, int superSampling) {
		this.backgroundColor = backgroundColor;
		this.rootNumberOfShadowRay = rootNumberOfShadowRay;
		this.maximumNumberOfRecurtsions = superSampling;
		this.superSampling = superSampling;
	}

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public void addLightSource(LightSource lightSource) {
		lightSourcesInScene.add(lightSource);
	}

	public void addRenderableObject(RenderableObject renderableObject) {
		objectsInScene.add(renderableObject);
	}

	public Camera getCamera() {
		return this.camera;
	}
}
