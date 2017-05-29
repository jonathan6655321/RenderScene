package RenderScene;

import java.util.ArrayList;

public class Scene {

	private ArrayList<RenderableObject> objectsInScene = new ArrayList<>();
	private RenderableBinarySearchObject binarySearchObjects = new RenderableBinarySearchObject();
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
		if (!binarySearchObjects.addRenderableObject(renderableObject)) {
			objectsInScene.add(renderableObject);
		}
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
		ArrayList<Collision> collisions = getAllCollision(ray);

		for (Collision collision : collisions) {
			if (objectToIgnore != collision.getCollisionObject()) {
				double collitionDistance = Vector3D.getPointsDistance(collision.getCollisionPoint(), ray.startPosition);
				if (collitionDistance < minCollisionDistance) {
					minCollisionDistance = collitionDistance;
					minCollision = collision;
				}
			}
		}

		return minCollision;
	}

	public ArrayList<Collision> getAllCollision(Ray ray, double minCollisionDistance, RenderableObject objectToIgnore) {
		ArrayList<Collision> collisions = getAllCollision(ray);

		int i = 0;
		while (i < collisions.size()) {
			if (collisions.get(i).getCollisionObject() == objectToIgnore) {
				collisions.remove(i);
				continue;
			}
			if (Vector3D.getPointsDistance(collisions.get(i).getCollisionPoint(),
					ray.startPosition) < minCollisionDistance) {
				collisions.remove(i);
				continue;
			}
			i++;
		}
		return collisions;
	}

	public ArrayList<Collision> getAllCollision(Ray ray) {
		ArrayList<Collision> collisions = new ArrayList<>();
		for (RenderableObject rObj : objectsInScene) {
			Collision collision = rObj.getCollision(ray);
			if (collision != null) {
				collisions.add(collision);
			}
		}
		ArrayList<Collision> collisionsArr = binarySearchObjects.getCollision(ray);
		if (collisionsArr != null) {
			collisions.addAll(collisionsArr);
		}
		return collisions;
	}

	public void setBinarySearchObjects(){
		binarySearchObjects.manageBinarySearch();
	}
}
