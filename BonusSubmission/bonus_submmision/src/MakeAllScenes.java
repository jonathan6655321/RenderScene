package RenderScene;

public class MakeAllScenes {
	private static String IDO_FOLDER = "F:\\Tau\\Courses\\Computer Science\\2016-2017\\Semester 2\\Graphic\\RenderScene\\git\\RenderScene\\";
	//private static String JOHNATHAN_FOLDER = "C:\\Development\\Graphics\\RenderScene\\";
	private final static String defultFolder = IDO_FOLDER;
	private final static String sceneFolder = "Scenes\\";
	private final static String[] filesToRender = { "Transparency", "Pool", "Room1", "Room10", "Spheres", "Triangle",
	"Triangle2" };
	private final static String fileType = ".txt";
	private final static String destFolder = "MyScenes\\";
	private final static String destFileType = ".png";

	public static void main(String[] args) {
		for (String fileToRender : filesToRender) {
			String srcFile = defultFolder + sceneFolder + fileToRender + fileType;
			String dstFile = defultFolder + destFolder + fileToRender + destFileType;
			Main.main(new String[] { srcFile, dstFile });
		}
	}
}
