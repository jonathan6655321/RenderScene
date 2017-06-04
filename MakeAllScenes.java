package RenderScene;

import RenderScene.Bonus.convertOBJToParserableFormat;

public class MakeAllScenes {
	public final static String defultFolder = convertOBJToParserableFormat.IDO_FOLDER;
	public final static String sceneFolder = "Scenes\\";
	public final static String[] filesToRender = { "Transparency", "Pool", "Room1", "Room10", "Spheres", "Triangle",
			"Triangle2" };
	public final static String fileType = ".txt";
	public final static String destFolder = "MyScenes\\";
	public final static String destFileType = ".png";

	public static void main(String[] args) {
		for (String fileToRender : filesToRender) {
			String srcFile = defultFolder + sceneFolder + fileToRender + fileType;
			String dstFile = defultFolder + destFolder + fileToRender + destFileType;
			Main.main(new String[] { srcFile, dstFile });
		}
	}
}
