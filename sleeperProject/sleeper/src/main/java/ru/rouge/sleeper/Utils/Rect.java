package ru.rouge.sleeper.Utils;

/**Прямоугольная область карты
 * */
public final class Rect
{
	public Size mSize;
	public Coord mCoord;
	
	/**Конструктор
	 * @param c - Координата верхнего левого угла
	 * @param s - Размеры прямоугольника
	 * */
	public Rect(Size s, Coord c)
	{
		this.mCoord = c;
		this.mSize = s;
	}
	
	/**Конструктор
	 * */
	public Rect(int x, int y, int w, int h)
	{
		this.mCoord = new Coord(x, y);
		this.mSize = new Size(w, h);
	}
	
	public Rect()
	{
		mSize = new Size();
		mCoord = new Coord();
	}
}
