package RenderScene;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class RenderableBinarySearchObject {
	private final static int SPLIT_INTO = 3;
	private final static Random rnd = new Random();

	private List<RenderableObject> renderableObjects = new LinkedList<>();
	private List<RenderableBinarySearchObject> renderableBinarySearchObject = new LinkedList<>();

	private RenderableSphere sphere = null;

	public boolean addRenderableObject(RenderableObject rObject) {
		if (rObject.isFinite() == false) {
			return false;
		}
		renderableObjects.add(rObject);
		return true;
	}

	public boolean isEmpty() {
		return renderableObjects.isEmpty() && renderableBinarySearchObject.isEmpty();
	}

	public void manageBinarySearch() {
		setSphere();
		if (renderableBinarySearchObject.size() + renderableObjects.size() < SPLIT_INTO * 2) {
			return;
		}

		Vector3D[] locations = generateRandomLocations();

		double minDistance;
		int minIndex;
		for (RenderableObject rObj : renderableObjects) {
			minDistance = Double.MAX_VALUE;
			minIndex = 0;
			for (int i = 0; i < SPLIT_INTO; i++) {
				double currentDistance = Vector3D.getPointsDistance(rObj.getObjetCenter(), locations[i]);
				if (currentDistance < minDistance) {
					minDistance = currentDistance;
					minIndex = i;
				}
			}
			renderableBinarySearchObject.get(minIndex).addRenderableObject(rObj);
		}
		renderableObjects.clear();

		removeEmptyrendeRableBinarySearchObject();
		if (renderableBinarySearchObject.size() != 1) {
			for (RenderableBinarySearchObject r : renderableBinarySearchObject) {
				r.manageBinarySearch();
			}
		}else{
			renderableObjects = renderableBinarySearchObject.get(0).renderableObjects;
			renderableBinarySearchObject.clear();
		}
	}

	public void addCollisions(Ray ray, double minCollisionDistance, List<Collision> collisions,
			RenderableObject objectToIgnore) {
		if (isEmpty()) {
			return;
		}
		Collision collision = sphere.getCollision(ray);
		if (collision == null || Vector3D.getPointsDistance(collision.getCollisionPoint(),
				ray.startPosition) > minCollisionDistance) {
			if (Vector3D.getPointsDistance(sphere.getObjetCenter(), ray.startPosition) > sphere.getRadius()) {
				return;
			}
		}

		for (RenderableObject rObj : renderableObjects) {
			if (rObj != objectToIgnore) {
				collision = rObj.getCollision(ray);
				if (collision != null && Vector3D.getPointsDistance(collision.getCollisionPoint(),
						ray.startPosition) < minCollisionDistance) {
					collisions.add(collision);
				}
			}
		}

		for (RenderableBinarySearchObject r : renderableBinarySearchObject) {
			r.addCollisions(ray, minCollisionDistance, collisions, objectToIgnore);
		}
		return;

	}

	private void setSphere() {
		double radius = 0;
		Vector3D location = new Vector3D(0, 0, 0);

		for (RenderableObject r : renderableObjects) {
			location = Vector3D.add(location, r.getObjetCenter());
		}
		location = location.getVectorMultipliedByConstant(((double) (1)) / ((double) (renderableObjects.size())));

		for (RenderableObject r : renderableObjects) {
			radius = Math.max(radius, r.getMaxDistanceFromPoint(location));
		}
		sphere = new RenderableSphere(location, radius, null);
	}

	private Vector3D[] generateRandomLocations() {
		Vector3D[] locations = new Vector3D[SPLIT_INTO];

		for (int i = 0; i < SPLIT_INTO; i++) {
			renderableBinarySearchObject.add(new RenderableBinarySearchObject());
			locations[i] = renderableObjects.get(rnd.nextInt(renderableObjects.size())).getObjetCenter();
		}

		return locations;
	}

	private void removeEmptyrendeRableBinarySearchObject() {
		int i = 0;
		while (i < renderableBinarySearchObject.size()) {
			if (renderableBinarySearchObject.get(i).isEmpty()) {
				renderableBinarySearchObject.remove(i);
			} else {
				i++;
			}
		}
	}
}
