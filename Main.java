package RenderScene;

public class Main {
	public static void main(String[] args) {
		String pathToSceneDescription = args[0];
		String pathToResultImage = args[1];
		int resultImageHeight = Integer.parseInt(args[2]);
		int resultImageWidth = Integer.parseInt(args[3]);
		
		
		Scene scene = SceneParser.getSceneFromTextFile(pathToSceneDescription);
		IRenderer renderer = new RayTracingRenderer();
		renderer.renderScene(scene, pathToResultImage, resultImageWidth, resultImageHeight);
	}
}
