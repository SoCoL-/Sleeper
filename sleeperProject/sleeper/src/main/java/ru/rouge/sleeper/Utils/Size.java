package ru.rouge.sleeper.Utils;

public final class Size 
{
	private int width;
	private int height;
	
	public Size()
	{
		setWidth(0);
		setHeight(0);
	}
	
	public Size(int w, int h)
	{
		setWidth(w);
		setHeight(h);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}
}
