package ru.rouge.sleeper.Utils;

public final class Coord
{
	private int x;
	private int y;
	
	public Coord()
	{
		setX(0);
		setY(0);
	}
	
	public Coord(int x, int y)
	{
		setX(x);
		setY(y);
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
}
