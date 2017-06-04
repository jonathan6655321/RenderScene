package RenderScene;

public class Ray {

	public Vector3D startPosition; 
	public Vector3D direction;
	
	
	public Ray(Vector3D startPosition, Vector3D direction)
	{
		this.startPosition = startPosition;
		this.direction = direction;
	}
}
