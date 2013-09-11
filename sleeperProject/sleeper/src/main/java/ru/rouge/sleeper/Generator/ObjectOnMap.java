package ru.rouge.sleeper.Generator;

import ru.rouge.sleeper.Utils.Coord;
import ru.rouge.sleeper.Utils.Size;

/**
 * 
 * @author Евгений
 * 
 * Данный класс содержит в себе информацию об объекте, расположенном на карте(корридор, комната)
 */
public class ObjectOnMap
{
	//Тип объекта на уровне
	public static final int TYPE_NONE 		= 0;
	public static final int TYPE_ROOM 		= 1;
	public static final int TYPE_CORRIDOR 	= 2;
	
	//Напрвление объекта(для корридора)
	public static final int DIR_SOUTH	 = 0;
	public static final int DIR_NORTH	 = 1;
	public static final int DIR_WEST	 = 2;
	public static final int DIR_EAST	 = 3;
	public static final int DIR_NONE	 = 4;
	
	private int					type;				//Тип объекта
	private Coord begin;				//Начальная точка(верхний левый угол)
	private Size size;				//Размер объекта(в тайлах)
	private int 				countExit;			//Количество выходов для комнаты
	private int 				currExit;			//Текущее количество занятых выходов
	private boolean				isFree;				//Если нет больше выходов(комната) или нет больше перегибов(корридор), то объект свободен
	private int					direction;			//Направление корридора
	//private ArrayList<Cell> 	ids;				//Список идентификаторов тайлов комнаты/коридора
	
	public ObjectOnMap(int mType, Coord mBegin, Size mSize, int mCount, boolean isFree, int mDir/*, ArrayList<Cell> mIds*/)
	{
		if(mType < TYPE_NONE || mType > TYPE_CORRIDOR)
			mType = TYPE_NONE;
		if(mDir < DIR_SOUTH || mDir > DIR_NONE)
			mDir = DIR_SOUTH;
		
		this.setType(mType);
		this.setBegin(mBegin);
		this.setSize(mSize);
		this.setCountExit(mCount);
		this.setFree(isFree);
		this.setDirection(mDir);
		//this.setIds(mIds);
		this.setCurrExit(0);
	}
	
	/**Поиск двери на стене (надо обдумать целесообразность этого решения)
	 * 
	 * @param girection - Указываем, на какой стене искать дверь
	 * @return 			- Возвращаем true, если дверь есть
	 * */
	/*public boolean isGetDoor(int girection)
	{
		int begin = 0;
		int end = 0;
		
		switch(girection)
		{
		case DIR_SOUTH:
			begin = (size.getHeight()-1) * size.getWidth();
			end = size.getHeight() * size.getWidth();
			for(int i = begin; i < end; i++)
			{
				if(ids.get(i).id == WorldGenerator.TILE_DOOR)
					return true;
			}
			break;
		case DIR_NORTH:
			begin = 0;
			end = size.getWidth();
			for(int i = begin; i < end; i++)
			{
				if(ids.get(i).id == WorldGenerator.TILE_DOOR)
					return true;
			}
			break;
		case DIR_WEST:
			//TODO
			break;
		case DIR_EAST:
			//TODO
			break;
		}
		
		return false;
	}*/

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Coord getBegin() {
		return begin;
	}

	public void setBegin(Coord begin) {
		this.begin = begin;
	}

	public Size getSize() {
		return size;
	}

	public void setSize(Size size) {
		this.size = size;
	}

	public int getCountExit() {
		return countExit;
	}

	public void setCountExit(int countExit) {
		this.countExit = countExit;
	}

	public boolean isFree() {
		return isFree;
	}

	public void setFree(boolean isFree) {
		this.isFree = isFree;
	}

	public int getDirection() {
		return direction;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	/*public ArrayList<Cell> getIds() {
		return ids;
	}

	public void setIds(ArrayList<Cell> ids) {
		this.ids = ids;
	}*/

	public int getCurrExit() {
		return currExit;
	}

	public void setCurrExit(int currExit) {
		this.currExit = currExit;
	}
}
