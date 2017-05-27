package RenderScene;
import java.awt.image.BufferedImage;

public interface IRenderer {
	public abstract BufferedImage renderScene(Scene scene, int resultImageWidth, int resultImageHeight);
}
