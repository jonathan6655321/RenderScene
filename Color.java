package RenderScene;

public class Color {
	public double red, green, blue;

	public Color(double red, double green, double blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public byte[] getColorByteArray() {
		return new byte[] { (byte) (red * 255), (byte) (green * 255), (byte) (blue * 255) };
	}
}
