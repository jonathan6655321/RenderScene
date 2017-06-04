package RenderScene;




public class Request {
	
	public static final int DEFAULT_RESULT_IMAGE_WIDTH = 500;
	public static final int DEFAULT_RESULT_IMAGE_HEIGHT = 500;
	
	public int resultImageWidth, resultImageHeight;
	public String pathToSceneDescription, pathToResultImage;
	
	public Request(String[] args) throws RayTracerException
	{
		
		if (args.length != 2 && args.length != 4)
		{
			throw new RayTracerException("Not enough arguments provided. Please specify an input scene file and an output image file for rendering.");			
		}
		else
		{
			this.pathToSceneDescription = args[0];
			this.pathToResultImage = args[1];			
			
			if (args.length == 2)
			{
				this.resultImageHeight = DEFAULT_RESULT_IMAGE_HEIGHT;
				this.resultImageWidth = DEFAULT_RESULT_IMAGE_WIDTH;
			}
			else if (args.length == 4 )
			{
				this.resultImageHeight = Integer.parseInt(args[2]);
				this.resultImageWidth = Integer.parseInt(args[3]);
			} 
		}
	}	
	
	
}
