package RenderScene;

import java.io.IOException;
// jonathans args: 
// C:\Development\Graphics\temp\Transparency.txt C:\Development\Graphics\temp\trans.png 500 500
// C:\Development\Graphics\temp\yoda.txt C:\Development\Graphics\temp\yoda.png 500 500
public class Main {
		
	public static void main(String[] args) {
		try {
			Request request = new Request(args);
			Scene scene = SceneParser.getSceneFromTextFile(request);
			IRenderer renderer = new RayTracingRenderer();
			renderer.renderScene(scene, request.pathToResultImage, 
					request.resultImageWidth, request.resultImageHeight);
			System.out.println("Render scene completed successfully.");

			} catch (IOException e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			} catch (RayTracerException e) {
				System.out.println(e.getMessage());
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			}
	}
}
