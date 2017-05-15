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

	public ArrayList<LightSource> getLightSources() {
		return lightSourcesInScene;
	}

	public void addRenderableObject(RenderableObject renderableObject) {
		objectsInScene.add(renderableObject);
	}

	public ArrayList<RenderableObject> getObjectsInScene() {
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

	public Collision getFirstCollisionWithRay(Ray ray, RenderableObject objectToIgnore) {
		Collision minCollision = null;
		double minCollisionDistance = Double.POSITIVE_INFINITY;
		for (RenderableObject rObj : getObjectsInScene()) {
			if (objectToIgnore != rObj) {
				Collision collision = rObj.getCollision(ray);
				if (collision != null) {
					double collitionDistance = Vector3D.getPointsDistance(collision.getCollisionPoint(),
							ray.startPosition);
					if (collitionDistance < minCollisionDistance) {
						minCollisionDistance = collitionDistance;
						minCollision = collision;
					}
				}
			}
		}
		return minCollision;
	}

	public ArrayList<Collision> getAllCollision(Ray ray, double minCollisionDistance, RenderableObject objectToIgnore) {
		ArrayList<Collision> collisions = new ArrayList<>();
		for (RenderableObject rObj : getObjectsInScene()) {
			if (objectToIgnore != rObj) {
				Collision collision = rObj.getCollision(ray);
				if (collision != null) {
					double collitionDistance = Vector3D.getPointsDistance(collision.getCollisionPoint(),
							ray.startPosition);
					if (collitionDistance < minCollisionDistance) {
						collisions.add(collision);
					}
				}
			}
		}
		return collisions;
	}

}
