package RenderScene;

public class Camera {
	private Vector3D cameraPosition; 
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
	
	
	public Camera(Vector3D cameraPosition, Vector3D lookAtPoint, Vector3D upDirection,
			double screenDistance, double screenWidthRelativeToScene, int imageHeightInPixels, int imageWidthInPixels) {
		this.cameraPosition = cameraPosition;
		this.lookAtDirection = Vector3D.subtract(lookAtPoint, cameraPosition);
		this.upDirection = fixUpDirection(lookAtDirection, upDirection);
		this.screenDistance = screenDistance;
		this.screenWidthRelativeToScene = screenWidthRelativeToScene;
	}

	public void initScreenParams(int imageHeightInPixels, int imageWidthInPixels)
	{
		initScreenCenterPosition();
		initPixelWidth(imageWidthInPixels);
		initOnePixelDownDiff();
		initOnePixelRightDiff();
		initTopLeftPixelPosition(imageHeightInPixels, imageWidthInPixels);
	}
	
	public Ray getRayWhichLeavesFromPixel(double row, double col)
	{
		Vector3D pixelAtRowColPosition = getPositionOfPixelAtRowCol(row, col);
		Vector3D rayStartPosition = pixelAtRowColPosition;
		Vector3D rayDirection = Vector3D.subtract(pixelAtRowColPosition, cameraPosition);
		return new Ray(rayStartPosition, rayDirection);
	}
	
	
	// Related to screen params init
	private void initScreenCenterPosition()
	{
		screenCenterPosition = Vector3D.add(cameraPosition, lookAtDirection.getVectorInSameDirectionWithMagnitude(screenDistance));
	}
	
	private void initPixelWidth(int imageWidthInPixels)
	{
		pixelWidth = screenWidthRelativeToScene / imageWidthInPixels;
	}
	
	private void initOnePixelDownDiff()
	{	
		onePixelDownVector =  
				upDirection.getVectorInSameDirectionWithMagnitude(pixelWidth).getReversedVector();
	}
	
	private void initOnePixelRightDiff()
	{
		Vector3D rightDirection = Vector3D.crossProduct(lookAtDirection.getReversedVector(), upDirection);// right hand rule
		onePixelRightDiff = rightDirection.getVectorInSameDirectionWithMagnitude(pixelWidth);
	}
	
	private void initTopLeftPixelPosition(int imageHeight, int imageWidth)
	{
		Vector3D diffFromCenterToTop = onePixelDownVector.getReversedVector().getVectorMultipliedByConstant(((double)imageHeight)/2);
		Vector3D diffFromCenterToLeft = onePixelRightDiff.getReversedVector().getVectorMultipliedByConstant(((double)imageWidth)/2);
		Vector3D diffFromCenterToTopLeftPixel = Vector3D.add(diffFromCenterToTop, diffFromCenterToLeft);
		
		topLeftPixelPosition = Vector3D.add(diffFromCenterToTopLeftPixel, screenCenterPosition);
	}
	
	public Vector3D getPositionOfPixelAtRowCol(double row, double col)
	{
		return Vector3D.addRowColToStartingPosition(topLeftPixelPosition, onePixelDownVector, onePixelRightDiff, row, col);
	}
	
	public Vector3D fixUpDirection(Vector3D lookAtDirection, Vector3D upDirection)
	{
		Vector3D firstCrossProd = Vector3D.crossProduct(lookAtDirection, upDirection);
		Vector3D secondCrossProd = Vector3D.crossProduct(lookAtDirection, firstCrossProd);
		return secondCrossProd.getVectorMultipliedByConstant(-1);
	}
}
