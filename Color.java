package RenderScene;

public class Color {
	public double red, green, blue;

	public Color(double red, double green, double blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public static byte[] getColorByteArray(Color color) {
		return new byte[] { (byte) (color.red * 255), (byte) (color.green * 255), (byte) (color.blue * 255) };
	}
}
