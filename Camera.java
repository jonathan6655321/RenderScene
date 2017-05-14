package RenderScene;

import java.awt.image.CropImageFilter;

public class Camera {
	private Vector3D cameraPosition;; 
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
		initOnePixelRightDiff();
		initTopLeftPixelPosition(imageHeight, imageWidth);
	}
	
	// iamge height, width is number of pixels 
	public Ray getRayWhichLeavesFromPixel(int row, int col, int imageHeight, int imageWidth)
	{
		Ray ray = new Ray();
		Vector3D pixelAtRowColPosition = getPositionOfPixelAtRowCol(row, col);
		ray.direction = Vector3D.subtract(pixelAtRowColPosition, cameraPosition);
		ray.startPosition = pixelAtRowColPosition;
		return ray;
	}
	
	
	// Related to screen params init
	private void initScreenCenterPosition()
	{
		lookAtDirection.normalize();
		screenCenterPosition = Vector3D.add(cameraPosition, lookAtDirection.getVectorMultipliedByConstant(screenDistance));
	}
	
	private void initOnePixelDownDiff()
	{	
		onePixelDownVector =  
				upDirection.getVectorInSameDirectionWithMagnitude(pixelWidth).getReversedVector();
	}
	
	private void initOnePixelRightDiff()
	{
		Vector3D rightDirection = Vector3D.crossProduct(lookAtDirection.getReversedVector(), upDirection);
		onePixelRightDiff = rightDirection.getVectorInSameDirectionWithMagnitude(pixelWidth);
	}
	
	private void initTopLeftPixelPosition(int imageHeight, int imageWidth)
	{
		// TODO ceil?? 
		Vector3D diffFromCenterToTop = onePixelDownVector.getReversedVector().getVectorMultipliedByConstant(Math.ceil(imageHeight/2));
		Vector3D diffFromCenterToLeft = onePixelRightDiff.getReversedVector().getVectorMultipliedByConstant(Math.ceil(imageWidth/2));
		Vector3D diffFromCenterToTopLeftPixel = Vector3D.add(diffFromCenterToTop, diffFromCenterToLeft);
		
		topLeftPixelPosition = Vector3D.add(diffFromCenterToTopLeftPixel, screenCenterPosition);
	}
	
	public Vector3D getPositionOfPixelAtRowCol(int row, int col)
	{
		Vector3D diffToRight = onePixelRightDiff.getVectorMultipliedByConstant(col);
		Vector3D diffDown = onePixelDownVector.getVectorMultipliedByConstant(row);
		Vector3D diffToPixelAtRowColFromTopLeftPixel = Vector3D.add(diffToRight, diffDown);
		
		return Vector3D.add(diffToPixelAtRowColFromTopLeftPixel, topLeftPixelPosition);
	}
}
