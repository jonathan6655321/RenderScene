package RenderScene;

public class Camera {
	private Vector3D position;; 
	private Vector3D lookAtDirection; // will also be the view plane normal
	private Vector3D upDirection; // direction cameras top points at
	private double screenWidthRelativeToScene;
	private double screenDistance;
	
	//Screen params - initialized through setScreenParams. 
	private Vector3D screenCenterPosition;
	private double pixelWidth; // also height
	private Vector3D topLeftPixelPosition;
	private Vector3D onePixelRightDiff;
	private Vector3D onePixelDownVector;
	
	
	// TODO is upDirection perpendicular to LookAtDirection
	public void setScreenParams(int imageHeight, int imageWidth)
	{
		initScreenCenterPosition();
		pixelWidth = screenWidthRelativeToScene / imageWidth;
		initOnePixelDownDiff();
		
	}
	
	// iamge height, width is number of pixels 
	public Ray getRayWhichLeavesFromPixel(int col, int row, int imageHeight, int imageWidth)
	{
		Ray ray = new Ray();
		
		
		
		
		
		return null;
	}
	
	
	// Related to screen params init
	private void initScreenCenterPosition()
	{
		lookAtDirection.normalize();
		screenCenterPosition = Vector3D.add(position, lookAtDirection.getVectorMultipliedByConstant(screenDistance));
	}
	
	private void initOnePixelDownDiff()
	{
		
		upDirection.normalize();
		
		onePixelDownVector =  
				upDirection.getVectorMultipliedByConstant(pixelWidth).getReversedVector();
	}
	
}
