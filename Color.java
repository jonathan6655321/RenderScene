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
	
	// adds to original color
	public void add(Color addedColor)
	{
		this.red += addedColor.red;
		this.blue += addedColor.blue;
		this.green += addedColor.green;
		this.boundComponentsToOne();
		return;
	}
	
	public void multiplyByConstant(double c)
	{
		this.red *= c;
		this.blue *= c;
		this.green *= c;
		this.boundComponentsToOne();
		return;
	}
	
	private void boundComponentsToOne()
	{
		this.red = Math.min(this.red, 1);
		this.blue = Math.min(this.blue, 1);
		this.green = Math.min(this.green, 1);
		return;
	}
	
	
}
