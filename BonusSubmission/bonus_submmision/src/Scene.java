package RenderScene;

import java.util.LinkedList;
import java.util.List;

public class Scene {
	private static boolean USE_BINARY_TREE = true;

	private List<RenderableObject> objectsInScene = new LinkedList<>();
	private RenderableBinarySearchObject binarySearchObjects = new RenderableBinarySearchObject();
	private List<LightSource> lightSourcesInScene = new LinkedList<>();
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

	public List<LightSource> getLightSources() {
		return lightSourcesInScene;
	}

	public void addRenderableObject(RenderableObject renderableObject) {
		if(USE_BINARY_TREE){
			if (!binarySearchObjects.addRenderableObject(renderableObject)) {
				objectsInScene.add(renderableObject);
			}
		}else{
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
		List<Collision> collisions = getAllCollision(ray, Double.MAX_VALUE, objectToIgnore);

		for (Collision collision : collisions) {
			double collitionDistance = Vector3D.getPointsDistance(collision.getCollisionPoint(), ray.startPosition);
			if (collitionDistance < minCollisionDistance) {
				minCollisionDistance = collitionDistance;
				minCollision = collision;
			}
		}

		return minCollision;
	}

	public List<Collision> getAllCollision(Ray ray, double minCollisionDistance, RenderableObject objectToIgnore) {
		List<Collision> collisions = new LinkedList<>();
		for (RenderableObject rObj : objectsInScene) {
			if (rObj != objectToIgnore) {
				Collision collision = rObj.getCollision(ray);
				if (collision != null && Vector3D.getPointsDistance(collision.getCollisionPoint(),
						ray.startPosition) < minCollisionDistance) {
					collisions.add(collision);
				}
			}
		}
		if (USE_BINARY_TREE)
			binarySearchObjects.addCollisions(ray, minCollisionDistance, collisions, objectToIgnore);

		return collisions;
	}

	public List<Collision> getAllCollision(Ray ray) {
		return getAllCollision(ray, Double.MAX_VALUE, null);
	}

	public void setBinarySearchObjects() {
		if (USE_BINARY_TREE)
			binarySearchObjects.manageBinarySearch();
	}
}
