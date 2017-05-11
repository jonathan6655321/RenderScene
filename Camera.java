package RenderScene;

public class Camera {
	private float[] position = new float[3]; 
	private float[] lookAtVector = new float[3]; // will also be the view plane normal
	private float[] upVector = new float[3]; // direction cameras top points at
	private float screenWidthRelativeToScene;
	private float screenDistance;
	
	
	
	public Ray getRayWhichLeavesFromPixel(int col, int row, int imageHeight, int imageWidth)
	{
		return null;
	}
	
	
	
}
