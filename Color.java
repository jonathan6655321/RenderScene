package RenderScene;

public class Color {
	public double red, green, blue;

	public Color(double red, double green, double blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public static byte[] getColorByteArray(Color color) {
		int redInt = (int) (color.red * 255);
		int greenInt = (int) (color.green * 255);
		int blueInt = (int) (color.blue * 255);
		return new byte[] { (byte) redInt, (byte) greenInt, (byte) blueInt };
	}
}
