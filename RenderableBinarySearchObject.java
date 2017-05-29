package RenderScene;

import java.util.ArrayList;

public class RenderableBinarySearchObject {
	public static int SPLIT_INTO = 5;
	private ArrayList<RenderableObject> renderableObjects = new ArrayList<>();
	private ArrayList<RenderableBinarySearchObject> renderableBinarySearchObject = new ArrayList<>();
	private double radius = 0;
	private Vector3D location = null;

	private boolean isSet = true;

	public boolean addRenderableObject(RenderableObject rObject) {
		if (rObject.isFinite() == false) {
			return false;
		}
		renderableObjects.add(rObject);
		isSet = false;
		return true;
	}

	public void manageBinarySearch() {
		if (isSet) {
			return;
		}
		isSet = true;
		if (location == null) {
			location = new Vector3D(0, 0, 0);
			for (RenderableObject r : renderableObjects) {
				location = Vector3D.add(location, r.getObjetCenter());
			}
			location = location.getVectorMultipliedByConstant(((double) (1)) / ((double) (renderableObjects.size())));
		}

		for (RenderableObject r : renderableObjects) {
			radius = Math.max(radius, r.getMaxDistanceFromPoint(location));
		}

		if (renderableBinarySearchObject.size() + renderableObjects.size() < SPLIT_INTO * SPLIT_INTO) {
			return;
		}

		if (renderableBinarySearchObject.size() == SPLIT_INTO) {

			int i = 0;
			for (RenderableObject r : renderableObjects) {
				renderableBinarySearchObject.get(i % SPLIT_INTO).addRenderableObject(r);
			}
			renderableObjects.clear();
		} else {

			while (renderableBinarySearchObject.size() < SPLIT_INTO) {
				renderableBinarySearchObject.add(new RenderableBinarySearchObject());
			}

			int itemInGroup = renderableObjects.size() / SPLIT_INTO;
			for (int i = 0; i < SPLIT_INTO; i++) {
				for (int j = 0; j <= itemInGroup; j++) {
					if (renderableObjects.isEmpty()) {
						break;
					} else {
						renderableBinarySearchObject.get(i).addRenderableObject(renderableObjects.remove(0));
					}
				}
			}
			for (RenderableBinarySearchObject r : renderableBinarySearchObject) {
				r.manageBinarySearch();
			}
		}
	}

	public ArrayList<Collision> getCollision(Ray ray) {
		// faster!
		if (renderableObjects.isEmpty() && renderableBinarySearchObject.isEmpty()) {
			return null;
		}
		manageBinarySearch();
		if (location == null) {
			int i = 0;
		}
		double[] p1 = ray.startPosition.vector, p2 = ray.direction.vector;
		double[] tv = location.vector;
		double x = tv[0] - p1[0];
		double y = tv[1] - p1[1];
		double z = tv[2] - p1[2];
		double dotproduct = (x * p2[0] + y * p2[1] + z * p2[2]) / (p2[0] * p2[0] + p2[1] * p2[1] + p2[2] * p2[2]);
		x -= p2[0] * dotproduct;
		y -= p2[1] * dotproduct;
		z -= p2[2] * dotproduct;
		dotproduct = x * x + y * y + z * z;
		if (dotproduct > radius * radius) {
			return null;
		}

		ArrayList<Collision> collisions = new ArrayList<>();

		Collision collision;
		for (RenderableObject r : renderableObjects) {
			collision = r.getCollision(ray);
			if (collision != null) {
				collisions.add(collision);
			}
		}

		ArrayList<Collision> collisionArr;
		for (RenderableBinarySearchObject r : renderableBinarySearchObject) {
			collisionArr = r.getCollision(ray);
			if (collisionArr != null) {
				collisions.addAll(collisionArr);
			}
		}
		return collisions;

	}

}
