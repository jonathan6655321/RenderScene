package RenderScene;

public class Color {
	public double red, green, blue;

	public Color(double red, double green, double blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
		boundComponentsToOne();
	}

	public byte[] getColorByteArray() {
		return new byte[] { (byte) (red * 255), (byte) (green * 255), (byte) (blue * 255) };
	}

	// adds to original color
	public void add(Color addedColor) {
		this.red += addedColor.red;
		this.blue += addedColor.blue;
		this.green += addedColor.green;
		this.boundComponentsToOne();
		return;
	}

	public Color getColorMultiplyByConstant(double constant) {
		return new Color(this.red * constant, this.green * constant, this.blue * constant);
	}

	public Color getColorMultiplyByColor(Color color2) {
		return new Color(this.red * color2.red, this.green * color2.green, this.blue * color2.blue);
	}

	private void boundComponentsToOne() {
		this.red = Math.min(this.red, 1);
		this.blue = Math.min(this.blue, 1);
		this.green = Math.min(this.green, 1);
		return;
	}

	public boolean isNotZero() {
		return this.red != 0 || this.blue != 0 || this.green != 0;
	}
}
