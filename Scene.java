package RenderScene;

import java.util.ArrayList;

public class Scene {

	private ArrayList<RenderableObject> objectsInScene = new ArrayList<>();
	private ArrayList<LightSource> lightSourcesInScene = new ArrayList<>();
	private Camera camera;

	public void setCamera(Camera camera) {
		this.camera = camera;
	}

	public void addLightSource(LightSource lightSource) {
		lightSourcesInScene.add(lightSource);
	}

	public void addRenderableObject(RenderableObject renderableObject) {
		objectsInScene.add(renderableObject);
	}
}
