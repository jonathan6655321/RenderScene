package RenderScene;

public class Main {
		
	public static void main(String[] args) {
		try {
			Request request = new Request(args);
			Scene scene = SceneParser.getSceneFromTextFile(request.pathToSceneDescription);
			IRenderer renderer = new RayTracingRenderer();
			renderer.renderScene(scene, request.pathToResultImage, 
					request.resultImageWidth, request.resultImageHeight);
			
			} catch (IOException e) {
			System.out.println(e.getMessage());
			} catch (RayTracerException e) {
				System.out.println(e.getMessage());
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
	}




}
