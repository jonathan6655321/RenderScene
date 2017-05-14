package RenderScene;

import java.util.ArrayList;

public class Scene {

	private ArrayList<RenderableObject> objectsInScene = new ArrayList<>();
	private ArrayList<LightSource> lightSourcesInScene = new ArrayList<>();
	private Camera camera;
	private Color backgroundColor;
	private int rootNumberOfShadowRay;
	private int maximumNumberOfRecurtsions;
	private int superSampling;

	public Scene(Color backgroundColor, int rootNumberOfShadowRay, int maximumNumberOfRecurtsions, int superSampling) {
		this.backgroundColor = backgroundColor;
		this.rootNumberOfShadowRay = rootNumberOfShadowRay;
		this.maximumNumberOfRecurtsions = maximumNumberOfRecurtsions;
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
	
	public ArrayList<RenderableObject> getObjectsInScene(){
		return objectsInScene;
	}

	public Camera getCamera() {
		return this.camera;
	}

	public int getSuperSampling() {
		return superSampling;
	}

	public int getMaximumNumberOfRecursions() {
		return maximumNumberOfRecurtsions;
	}

	public int getRootNumberOfShadowRay() {
		return rootNumberOfShadowRay;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}
	
	public Collision getFirstCollision(Ray ray, RenderableObject objectToIgnore){
		Collision minCollision = null;
		for (RenderableObject rObj : getObjectsInScene()) {			
			Collision collision = rObj.getCollision(ray);
			if (collision != null) {
				if(minCollision==null){
					minCollision = collision;
				}else if()
			}
				
	}
	
}
