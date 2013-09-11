package ru.rouge.sleeper.Generator;

import org.andengine.util.debug.Debug;

import java.util.ArrayList;
import java.util.HashSet;

import ru.rouge.sleeper.Utils.Coord;
import ru.rouge.sleeper.Utils.Rect;
import ru.rouge.sleeper.Utils.Utils;
import ru.rouge.sleeper.WorldContext;

public final class WorldGenerator
{
	private final static String TAG = "WorldGenerator";
	
	public final static int TILE_NONE = -1;
	public final static int TILE_DOOR = 0;		    //Надо будет заменить списком идентификаторов дверей
	
	public final static int WIDTH_CORIDOR = 3;		//Минимальная ширина коридора
	
	private int chanceRoom = 0;     				//Шансы выпадения комнаты и корридора
	private int numObjects = 0;						//Количество комнат на карте
	private int currLevel = 0;
	
	private WorldContext wContext;
	private ArrayList<ObjectOnMap> objectsMap;		//Для последующего добавления предметов на карту, сундуков, монстров и т.д.
	private ArrayList<LevelDoor> mLevelDoors;		//Для соединения всех комнат + разнообразие коридоров
	
	public WorldGenerator(WorldContext cont)
	{
		this.wContext = cont;
		objectsMap = new ArrayList<ObjectOnMap>();//ArrayList<ObjectOnMap>();
		mLevelDoors = new ArrayList<LevelDoor>();
	}
	
	public void startGeneration(int level)
	{
		this.currLevel = level;
	}
	
	private int getCell(int x, int y)
	{
		//return wContext.world.getLevel(currLevel).getCell(x, y);
        return 0;
	}
	
	private void setCell(int x, int y, int id)
	{
		/*assert(currLevel < wContext.world.getLevels().size());
		assert(x < wContext.world.getLevel(currLevel).getWidth() && x > 0);
		assert(y < wContext.world.getLevel(currLevel).getHeight() && y > 0);
		
		wContext.world.getLevel(currLevel).setCellID(x, y, id);*/
	}
	
	private void createDoor(int x, int y, int dir, boolean isFree)
	{
		LevelDoor ld = new LevelDoor();
		ld.mCoord = new Coord(x, y);
		ld.mDir = dir;
		ld.isFree = isFree;
		mLevelDoors.add(ld);
	}
	
	/**Функция установки всех дверей случайно
	 * @param room  - комната, которая устанавливается
	 * @param count - количество выходов из комнаты*/
	private void setupDoors(Rect room, int count)
	{
		int dir = -1;		//Стена, на которой надо поставить дверь
		int place = -1;		//Номер тайла на стене, куда ставим дверь
		int i = 0;			//Счетчик
		HashSet<Integer> dirs = new HashSet<Integer>(); //Список сторон, куда можно поставить дверь
		dirs.add(0);	//SOUTH
		dirs.add(1);	//NORTH
		dirs.add(2);	//EAST
		dirs.add(3);	//WEST

		Debug.i(TAG, "Начало расстановки дверей");
		while(i < count)
		{
			dir = Utils.getRand(0, 3);
			if(dirs.size() == 0)			//Если все двери заняты и по каким-то причинам счетчик != count, то останавливаем расстановку дверей
				break;
			if(dirs.contains(dir))			//Проверка с исключением
			{
				Debug.i(TAG, "Выберем сторону комнаты: dir = " + dir);
				//Проверяем на наличае двери на стене
				if(!isGetDoor(room, dir))
				{
					Debug.i(TAG, "Двери нет еще");
					if(dir == ObjectOnMap.DIR_SOUTH || dir == ObjectOnMap.DIR_NORTH)//горизонталь
					{
						Debug.i(TAG, "Выберем место на стене: от 1 до " + (room.mSize.getWidth()-1));
						place = Utils.getRand(1, room.mSize.getWidth()-2);//Выбираем место на стене, исключая угловые тайлы
						Debug.i(TAG, "Место выбрано: " + place);
						if(isCanSetTile(room, dir, place))
						{
							if(dir == ObjectOnMap.DIR_NORTH)
							{
								Debug.i(TAG, "Поставим дверь на место: (" + (room.mCoord.getX() + place) + " , " + room.mCoord.getY() + ")");
								createDoor(room.mCoord.getX() + place, room.mCoord.getY(), dir, true);
							}
							else if(dir == ObjectOnMap.DIR_SOUTH)
							{
								Debug.i(TAG, "Поставим дверь на место: (" + (room.mCoord.getX() + place) + " , " + (room.mCoord.getY() + (room.mSize.getHeight()-1)) + ")");
								createDoor(room.mCoord.getX() + place, room.mCoord.getY() + (room.mSize.getHeight()-1), dir, true);
							}
							i++;
						}
					}
					else if(dir == ObjectOnMap.DIR_EAST || dir == ObjectOnMap.DIR_WEST)//вертикаль
					{
						Debug.i(TAG, "Выберем место на стене: от 1 до " + (room.mSize.getWidth()-1));
						place = Utils.getRand(1, room.mSize.getHeight()-2);//Выбираем место на стене, исключая угловые тайлы
						Debug.i(TAG, "Место выбрано: " + place);
						if(isCanSetTile(room, dir, place))
						{
							if(dir == ObjectOnMap.DIR_WEST)
							{
								Debug.i(TAG, "Поставим дверь на место: (" + (room.mCoord.getX()) + " , " + (room.mCoord.getY() + place) + ")");
								createDoor(room.mCoord.getX(), room.mCoord.getY() + place, dir, true);
							}
							else if(dir == ObjectOnMap.DIR_EAST)
							{
								Debug.i(TAG, "Поставим дверь на место: (" + (room.mCoord.getX() + (room.mSize.getWidth()-1)) + " , " + (room.mCoord.getY() + place) + ")");
								createDoor(room.mCoord.getX() + (room.mSize.getWidth()-1), room.mCoord.getY() + place, dir, true);
							}
							i++;
						}
					}
				}
				dirs.remove(dir);
			}
		}
	}
	
	/**Функция проверки возможности установки тайла на заданное место
	 * Размещению может помешать какой-то механизм на стене, важный предмет или какой-то тайл уникальный
	 * */
	private boolean isCanSetTile(Rect room, int dir, int place)
	{
		//TODO Сделать несколько слоев на уровне и заполнить проверку на возможность размещения там двери
		return true;
	}
	
	/**Функция проверки наличия двери на стене*/
	private boolean isGetDoor(Rect room, int dir)
	{
		Debug.i(TAG, "Проверим наличае дверей на стене: dir = " + dir);
		switch(dir)
		{
		case ObjectOnMap.DIR_NORTH:
			for(int i = 0; i < room.mSize.getWidth(); i++)
			{
				Debug.i(TAG, "Проверка на северной стороне: x = " + (room.mCoord.getX() + i) + "; y = " + room.mCoord.getY());
				if(getDoorByCoord(room.mCoord.getX() + i, room.mCoord.getY()) != null)
				{
					Debug.e(TAG, "Дверь существует на северной стороне");
					return true;
				}
			}
			return false;
		case ObjectOnMap.DIR_SOUTH:
			for(int i = 0; i < room.mSize.getWidth(); i++)
			{
				Debug.i(TAG, "Проверка на южной стороне: x = " + (room.mCoord.getX() + i) + "; y = " + (room.mCoord.getY() + (room.mSize.getHeight()-1)));
				if(getDoorByCoord(room.mCoord.getX() + i, room.mCoord.getY() + (room.mSize.getHeight()-1)) != null)
				{
					Debug.e(TAG, "Дверь существует на южной стороне");
					return true;
				}
			}
			return false;
		case ObjectOnMap.DIR_WEST:
			for(int i = 0; i < room.mSize.getHeight(); i++)
			{
				Debug.i(TAG, "Проверка на западной стороне: x = " + room.mCoord.getX() + "; y = " + (room.mCoord.getY() + i));
				if(getDoorByCoord(room.mCoord.getX(), room.mCoord.getY() + i) != null)
				{
					Debug.e(TAG, "Дверь существует на западной стороне");
					return true;
				}
			}
			return false;
		case ObjectOnMap.DIR_EAST:
			for(int i = 0; i < room.mSize.getHeight(); i++)
			{
				Debug.i(TAG, "Проверка на восточной стороне: x = " + (room.mCoord.getX() + room.mSize.getWidth()) + "; y = " + (room.mCoord.getY() + i));
				if(getDoorByCoord(room.mCoord.getX() + (room.mSize.getWidth()-1), room.mCoord.getY() + i) != null)
				{
					Debug.e(TAG, "Дверь существует на восточной стороне");
					return true;
				}
			}
			return false;
		}
		return true;
	}
	
	/**
	 * Функция устанавливает комнату на уровне и заносит объект в список объектов на уровне. Также происходит проверка на возможность размещения комнате на уровне
	 * @param door - Свободная дверь, к которой присоединяем коридор
	 * @return Результат установки комнаты на карту
	 * */
	private boolean setRoom(LevelDoor door)
	{
		/*int x = 0, y = 0;
		
		//Выберем случайную комнаты из списка 
		int roomID = Utils.getRand(0, wContext.rooms.size()-1);
		Debug.i(TAG, "Выбрали комнату из списка с идентификатором: roomID = " + roomID);
		Room currRoom = wContext.rooms.get(roomID);
		Debug.i(TAG, "Посчитаем количество выходов для текущей комнаты");
		int countExit = Utils.getRand(2, 4);
		Debug.i(TAG, "Количество выходов у комнаты с индексом (" + roomID + ") = " + countExit);
		
		if(door == null)
		{
			//Вычислим координаты для первой комнаты
			//x = Utils.getRand(0, wContext.world.getLevel(currLevel).getWidth());
			//y = Utils.getRand(0, wContext.world.getLevel(currLevel).getHeight());
			x = 22;
			y = 22;
		}
		else
		{
			//Вычислим координаты для комнаты, относительно двери
			
			int place = 0;
			//Вычислим тайл, на котором поставим дверь
			if(door.mDir == ObjectOnMap.DIR_NORTH || door.mDir == ObjectOnMap.DIR_SOUTH)
			{
				place = Utils.getRand(1, currRoom.getSize().getWidth()-2);
			}
			else if(door.mDir == ObjectOnMap.DIR_WEST || door.mDir == ObjectOnMap.DIR_EAST)
			{
				place = Utils.getRand(1, currRoom.getSize().getHeight()-2);
			}
			//Теперь посчитаем отступы по х и по у
			if(door.mDir == ObjectOnMap.DIR_EAST)
			{
				x = door.mCoord.getX();
				y = door.mCoord.getY() - place;
			}
			else if(door.mDir == ObjectOnMap.DIR_NORTH)
			{
				x = door.mCoord.getX() - place;
				y = door.mCoord.getY() - (currRoom.getSize().getHeight() - 1);
			}
			else if(door.mDir == ObjectOnMap.DIR_SOUTH)
			{
				x = door.mCoord.getX() - place;
				y = door.mCoord.getY();
			}
			else if(door.mDir == ObjectOnMap.DIR_WEST)
			{
				x = door.mCoord.getX() - (currRoom.getSize().getWidth() - 1);
				y = door.mCoord.getY() - place;
			}
			
			countExit--;			//Одну дверь установили
		}
		
		for(int ytemp = y; ytemp < y + currRoom.getSize().getHeight(); ytemp++)
		{
			if(ytemp < 0 || ytemp > wContext.world.getLevel(currLevel).getHeight()-1)
			{
				Debug.e(TAG, "Комната не вмещается по высоте: ytemp = " + ytemp + ", levelHeight = " + wContext.world.getLevel(currLevel).getHeight());
				return false;
			}
			for(int xtemp = x; xtemp < x+currRoom.getSize().getWidth(); xtemp++)
			{
				if(xtemp < 0 || xtemp > wContext.world.getLevel(currLevel).getWidth()-1)
				{
					Debug.e(TAG, "Комната не вмещается по ширине: xtemp = " + xtemp + ", levelWidth = " + wContext.world.getLevel(currLevel).getWidth());
					return false;
				}
				if((getCell(xtemp, ytemp) != TILE_NONE) && (!Utils.typesWall.contains(getCell(xtemp, ytemp))) && (getCell(xtemp, ytemp) != TILE_DOOR))
				{
					Debug.e(TAG, "Комната пересекает уникальный тайл по координатам: (" + xtemp + " , " + ytemp + ") с индексом тайла = " + getCell(xtemp, ytemp));
					return false;
				}
			}
		}
		
		Debug.i(TAG, "Комната может быть размещена, заполним уровень тайлами комнаты");
		for(int ytemp = 0; ytemp < currRoom.getSize().getHeight(); ytemp++)
		{
			for(int xtemp = 0; xtemp < currRoom.getSize().getWidth(); xtemp++)
			{
				LevelDoor ld = getDoorByCoord(xtemp + x, ytemp + y);
				if(ld == null)
					setCell(xtemp + x, ytemp + y, currRoom.getRoomID(xtemp, ytemp));
				else
					ld.isFree = false;
			}
			//correctWallIDs(currRoom.getSize().getWidth(), x, ytemp+y-1, true);
		}
		//correctWallIDs(currRoom.getSize().getWidth(), x, (currRoom.getSize().getHeight()-1) + y, true);
		if(door != null)
		{
			Debug.i(TAG, "Нарисуем общую дверь");
			setCell(door.mCoord.getX(), door.mCoord.getY(), TILE_DOOR);
		}
		
		Rect rectRoom = new Rect(x, y, currRoom.getSize().getWidth(), currRoom.getSize().getHeight());
		setupDoors(rectRoom, countExit);	//Расставим все двери в комнате
		
		//Добавим комнаты к списку объектов на уровне
		ObjectOnMap object = new ObjectOnMap(ObjectOnMap.TYPE_ROOM, new Coord(x, y), currRoom.getSize(), countExit, false, ObjectOnMap.DIR_NONE, currRoom.getIds());
		objectsMap.add(object);
		
		//Укажем, что дверь теперь занята, если ее передали в функцию
		if(door != null)
			door.isFree = false;*/
		
		return true;
	}
	
	/**Функция создания на уровне коридора со всеми переломами
	 * @param door - комната, с которой начнется коридор
	 * @return true - если коридор создан успешно
	 * */
	private boolean setCorridor(LevelDoor door)
	{
		if(door == null)
			return false;
		
		/*Debug.i(TAG, "----------------Начинаем генерировать коридор---------------------");
		int length = Utils.getRand(2, wContext.world.getLevel(currLevel).getWidth()/2)+1;	//Сгенерируем длину коридора = половине ширины уровня; +1 для отрисовки стены в конце коридора
		Debug.i(TAG, "Длина коридора = " + length);
		int direction = -1;																	//Направление построения коридора
		//int newDirection = -1;															//Если есть повороты, то тут мы определяем новый поворот коридора
		int x = 0, y = 0;																	//Координаты начала построения коридора
		int kx = 0, ky = 0;																	//Коэфиценты, позволяющие создать правильное направление генерации коридора
		Level mCurrLevel = wContext.world.getLevel(currLevel);								//Текущий уровень
		int mTurns = Utils.getRand(0, 3);													//Количество поворотов коридора
		
		Debug.i(TAG, "Количество переломов коридора = " + mTurns);
		Debug.i(TAG, "Расчитываем отступы относительно двери");
		direction = door.mDir;
		//newDirection = direction;
		door.isFree = false;

		//Посчитаем отступы по х и по у относительно двери
		if((door.mDir == ObjectOnMap.DIR_EAST) || (door.mDir == ObjectOnMap.DIR_WEST))
		{
			if(door.mDir == ObjectOnMap.DIR_EAST)
			{
				x = door.mCoord.getX()+1;
				kx = 1;
				Debug.i(TAG, "Рисуем на восток!!!!");
			}
			else
			{
				x = door.mCoord.getX()-1;
				kx = -1;
				Debug.i(TAG, "Рисуем на запад!!!!");
			}
			y = door.mCoord.getY() - 1;
			ky = 1;
		}
		else if((door.mDir == ObjectOnMap.DIR_SOUTH) || (door.mDir == ObjectOnMap.DIR_NORTH))
		{
			x = door.mCoord.getX() - 1;
			kx = 1;
			if(door.mDir == ObjectOnMap.DIR_SOUTH)
			{
				y = door.mCoord.getY()+1;
				ky = 1;
				Debug.i(TAG, "Рисуем на юг!!!!");
			}
			else
			{
				y = door.mCoord.getY()-1;
				ky = -1;
				Debug.i(TAG, "Рисуем на север!!!!");
			}
		}
		Debug.i(TAG, "kx = " + kx);
		Debug.i(TAG, "ky = " + ky);
		Debug.i(TAG, "x = " + x);
		Debug.i(TAG, "y = " + y);
		
		Debug.i(TAG, "Нарисуем общую дверь");
		setCell(door.mCoord.getX(), door.mCoord.getY(), TILE_DOOR);

		//Отрисовка коридора в одном направлении
		if(!drawDirCoridor(length, x, y, kx, ky, direction, mCurrLevel))
			mTurns = 0;
		
		//Нарисуем все поворты коридора
		for(int t = 0; t < mTurns; t++)
		{
			//Вычислим новое направление коридора и длину его
			if(direction == ObjectOnMap.DIR_NORTH || direction == ObjectOnMap.DIR_SOUTH)
			{
				if(x == 0)
				{
					direction = ObjectOnMap.DIR_EAST;
				}
				else if(x >= mCurrLevel.getWidth()-3)
				{
					direction = ObjectOnMap.DIR_WEST;
				}
				else
				{
					int r = Utils.getRand(0, 1);
					if(r == 1)
						direction = ObjectOnMap.DIR_EAST;
					else
						direction = ObjectOnMap.DIR_WEST;
				}
			}
			else if(direction == ObjectOnMap.DIR_EAST || direction == ObjectOnMap.DIR_WEST)
			{
				if(y == 0)
				{
					direction = ObjectOnMap.DIR_SOUTH;
				}
				else if(y >= mCurrLevel.getHeight()-3)
				{
					direction = ObjectOnMap.DIR_NORTH;
				}
				else
				{
					int r = Utils.getRand(0, 1);
					if(r == 0)
						direction = ObjectOnMap.DIR_SOUTH;
					else
						direction = ObjectOnMap.DIR_NORTH;
				}
			}
			Debug.i(TAG, "Turn" + t + " : Direction = " + direction);
			
			//Расчитаем коэфиценты и начальные координаты коридора
			if(direction == ObjectOnMap.DIR_NORTH || direction == ObjectOnMap.DIR_SOUTH)
			{
				int buf = 0;
				if(kx > 0)
					buf = (length - WIDTH_CORIDOR) * kx;
				else
					buf = (length - 1) * kx;
				x = x + buf;
				kx = 1;
				if(direction == ObjectOnMap.DIR_NORTH)
				{
					ky = -1;
				}
				else
				{
					ky = 1;
					y = y + 2;
				}
			}
			else if(direction == ObjectOnMap.DIR_EAST || direction == ObjectOnMap.DIR_WEST)
			{
				int buf = 0;
				if(ky > 0)
					buf = (length - WIDTH_CORIDOR) * ky;
				else
					buf = (length - 1) * ky;
				y = y + buf;
				ky = 1;
				if(direction == ObjectOnMap.DIR_EAST)
				{
					kx = 1;
					x = x + 2;
				}
				else
				{
					kx = -1;
				}
			}
			Debug.i(TAG, "Turn" + t + " : kx = " + kx);
			Debug.i(TAG, "Turn" + t + " : ky = " + ky);
			Debug.i(TAG, "Turn" + t + " : x = " + x);
			Debug.i(TAG, "Turn" + t + " : y = " + y);
			
			length = Utils.getRand(2, wContext.world.getLevel(currLevel).getWidth()/2)+1;
			Debug.i(TAG, "Turn" + t + " : Длина коридора = " + length);
		
			//Отрисовка коридора в одном направлении
			if(!drawDirCoridor(length, x, y, kx, ky, direction, mCurrLevel))
				mTurns = 0;
		}*/
		
		return true;
	}
	
	/**Функция отрисовки коридора в одном направлении до первого перелома с контролем препятствий
	 * @param length - длина коридора
	 * @param x - координата начала отрисовки коридора по х
	 * @param y - координата начала отрисовки коридора по у
	 * @param kx - коэфицент направления по х
	 * @param ky - коэфицент направления по у
	 * @param direction - направление построения коридора
	 * @param mCurrLevel - текущий уровень
	 * @return false - если наткнулись на препятствие
	 * */
	/*private boolean drawDirCoridor(int length, int x, int y, int kx, int ky, int direction, Level mCurrLevel)
	{
		int oldCurrX = -1, oldCurrY = -1;					//Для отслеживания изменения новой строки в отрисовке коридора
		int currX = 0, currY = 0;							//Текущий тайл коридора(0,1,2,..)
		int endX = 0, endY = 0;								//Отслеживание текщего конца коридора(Например: при у = 2 текущий конец = (1,2); при у = 5 - (1,5))
		int isGoNext = -1;									//Результат проверки дальнейшего построения коридора
		//Отрисовка коридора в одном направлении
		for(int i = 0; i < length*WIDTH_CORIDOR; i++)
		{
			if(direction == ObjectOnMap.DIR_NORTH || direction == ObjectOnMap.DIR_SOUTH)
			{
				currX = (i % WIDTH_CORIDOR) * kx;
				currY = (i / WIDTH_CORIDOR) * ky;
				endX = x + 1;
				endY = y + currY - 1 * ky;
				//Utils.log(TAG, "currX = " + currX);
				//Utils.log(TAG, "currY = " + currY);
			}
			else if (direction == ObjectOnMap.DIR_EAST || direction == ObjectOnMap.DIR_WEST)
			{
				currX = (i / WIDTH_CORIDOR) * kx;
				currY = (i % WIDTH_CORIDOR) * ky;
				endX = x + currX - 1 * kx;
				endY = y + 1;
				//Utils.log(TAG, "currX = " + i);
				//Utils.log(TAG, "currY = " + currY);
			}
			//Utils.log(TAG, "--------------------------------------------------");
			
			LevelDoor ld = getDoorByCoord(x + currX, y + currY);
			if((x+currX)>= mCurrLevel.getWidth() || (x+currX) < 0 || (y+currY)>= mCurrLevel.getHeight() || (y+currY)<0)
			{
				Debug.e(TAG, "Вышли за пределы размеров уровня!! Установим в конце коридора стену");
				int wallID = -1;
				boolean isVertical = false;
				int correctX = 0, correctY = 0;
				if(direction == ObjectOnMap.DIR_NORTH || direction == ObjectOnMap.DIR_SOUTH)
				{
					wallID = 7;
					isVertical = true;
					correctX = endX - 1;
					correctY = endY;
				}
				else if(direction == ObjectOnMap.DIR_WEST || direction == ObjectOnMap.DIR_EAST)
				{
					wallID = 3;
					isVertical = false;
					correctX = endX;
					correctY = endY - 1;
				}
				setCell(endX, endY, wallID);
				//correctWallIDs(3, correctX, correctY, isVertical);
				return false;
			}
			if((direction == ObjectOnMap.DIR_NORTH || direction == ObjectOnMap.DIR_SOUTH) && oldCurrY != currY)
				isGoNext = isCanGo(x, y+currY, direction);
			else if((direction == ObjectOnMap.DIR_WEST || direction == ObjectOnMap.DIR_EAST) && oldCurrX != currX)
				isGoNext = isCanGo(x+currX, y, direction);
			if(isGoNext > 0)
			{
				Debug.e(TAG, "Налетели на уникальный тайл. Остановим создание коридора");
				if(isGoNext == 2)//Нам нужна дверь в конце коридора
				{
					int localEndX = 0, localEndY = 0;		//Дверь будет ставится на текущем ряду коридора
					if(direction == ObjectOnMap.DIR_NORTH || direction == ObjectOnMap.DIR_SOUTH)
					{
						localEndX = x+1;
						localEndY = y+currY;
					}
					else if(direction == ObjectOnMap.DIR_EAST || direction == ObjectOnMap.DIR_WEST)
					{
						localEndX = x+currX;
						localEndY = y+1;
					}
					createDoor(localEndX, localEndY, direction, false);
					setCell(localEndX, localEndY, TILE_DOOR);
				}
				else if(isGoNext == 1)//Нам нужно поставить стену
				{
					int localEndX = 0, localEndY = 0;		//Стена поставится на предыдущем ряду коридора
					int doorID = -1;
					if(direction == ObjectOnMap.DIR_NORTH || direction == ObjectOnMap.DIR_SOUTH)
					{
						localEndX = x+1;
						localEndY = y+currY-1*ky;
						doorID = 7;
					}
					else if(direction == ObjectOnMap.DIR_EAST || direction == ObjectOnMap.DIR_WEST)
					{
						localEndX = x+currX-1*kx;
						localEndY = y+1;
						doorID = 3;
					}
					setCell(localEndX, localEndY, doorID);
				}
				return false;
			}
			else
			{
				int what = -1;
				if(direction == ObjectOnMap.DIR_EAST || direction == ObjectOnMap.DIR_WEST)
					if(currY == 0 || currY == (WIDTH_CORIDOR-1))//Тело коридора(стены)
						//what = TILE_WALL;
						what = 7;
					else if(currY == 1 && Math.abs(currX) == (length-1))//Конец коридора
					{
						//what = TILE_WALL;
						what = 3;
						//Добавим в конце коридора дверь в список
						createDoor(x + currX, y + currY, direction, true);
					}
					else//Тело коридора(пол)
						//what = TILE_FLOOR;
						what = 1;
				else if(direction == ObjectOnMap.DIR_NORTH || direction == ObjectOnMap.DIR_SOUTH)
					if(currX == 0 || currX == (WIDTH_CORIDOR-1))//Тело коридора(стены)
						//what = TILE_WALL;
						what = 3;
					else if(currX == 1 && Math.abs(currY) == (length-1))//Конец коридора
					{
						//what = TILE_WALL;
						what = 7;
						createDoor(x + currX, y + currY, direction, true);
					}
					else//Тело коридора(пол)
						//what = TILE_FLOOR;
						what = 1;
				
				if(ld == null)
					setCell(x + currX, y + currY, what);
				else
					ld.isFree = false;
			}
			oldCurrX = currX;
			oldCurrY = currY;
		}
		return true;
	}*/
	
	/**Функция правит идентификаторы стен после их установки
	 * @param width - ширина линии проверяемых тайлов
	 * @param x - координата начала проверяемой линии
	 * @param y - координата начала проверяемой линии
	 * @param isVertical - направление строительства комнаты/коридора
	 * */
	public void correctWallIDs(int width, int x, int y, boolean isVertical)
	{
		/*Описание:
		 * каждому тайлу стены вокруг текущего(если текущий тайл = стене и идентификаторы соседнего тайла и текущего не равны)
		 * предоставляется свой вес. 
		 * Верхний = 1,
		 * левый = 2,
		 * правый = 8,
		 * нижний = 4
		 * Сложив все суммы можно будет узнать, какой тайл стены можно будет подставить на текущее место 
		 */
		/*Debug.i(TAG, "*****************************************************************");
		Debug.i(TAG, "Начали корректировать тайлы");
		Debug.i(TAG, "isVertical = " + isVertical);
		Debug.i(TAG, "x = " + x + " , y = " + y);
		Debug.i(TAG, "width = " + width);
		
		int currX = 0, currY = 0;															//Координаты текущего идентификатора
		int summa = 0;																		//Сумма всех весов
		int wallID = -1;
		for(int i = 0; i < width; i++)
		{
			if(isVertical)
			{
				currX = x + i;
				currY = y;
			}
			else
			{
				currX = x;
				currY = y + i;
			}
			if(currX < 0 || currX >= wContext.world.getLevel(currLevel).getWidth())
			{
				Debug.e(TAG, "Ошибка(х). Координаты вышли за пределы игрового поля!!!!!! :(currX, currY) = (" + currX + " , " + currY + ")");
				return;
			}
			if(currY < 0 || currY >= wContext.world.getLevel(currLevel).getHeight())
			{
				Debug.e(TAG, "Ошибка(y). Координаты вышли за пределы игрового поля!!!!!! :(currX, currY) = (" + currX + " , " + currY + ")");
				return;
			}
			
			Debug.i(TAG, "getCell(currX, currY) = " + getCell(currX, currY));
			Debug.i(TAG, "currX = " + currX + " , currY = " + currY);
			wallID = getCell(currX, currY);	//Если суммы нет в списке, то не меняем тайл
			if(Utils.typesWall.contains(getCell(currX, currY)))
			{
				if(currX != 0 && Utils.typesWall.contains(getCell(currX-1, currY)))//Если текущий тайл не самый левый, то посмотрим на тайл слева
					summa += 2;//Если тайл слева - стена, то прибавим к сумме вес
					//summa += getWeight(currX-1, currY, ObjectOnMap.DIR_WEST);
				if(currY != 0 && Utils.typesWall.contains(getCell(currX, currY-1)))//Если текущий тайл не самый верхний, то посмотрим на тайл сверху
					summa += 1;//Если тайл сверху - стена, то прибавим к сумме вес
					//summa += getWeight(currX, currY-1, ObjectOnMap.DIR_NORTH);
				if((currX < wContext.world.getLevel(currLevel).getWidth()-1) && Utils.typesWall.contains(getCell(currX+1, currY)))//Если текущий тайл не самый правый, то посмотрим на тайл справа
					summa += 8;//Если тайл справа - стена, то прибавим к сумме вес
					//summa += getWeight(currX+1, currY, ObjectOnMap.DIR_EAST);
				if((currY < wContext.world.getLevel(currLevel).getHeight()-1) && Utils.typesWall.contains(getCell(currX, currY+1)))//Если текущий тайл не самый нижний, то посмотрим на тайл снизу
					summa += 4;//Если тайл снизу - стена, то прибавим к сумме вес
					//summa += getWeight(currX, currY+1, ObjectOnMap.DIR_SOUTH);
			}
			Debug.i(TAG, "summa = " + summa);

            wallID = summa;*/
			/*switch(summa)
			{
			case 32:
				wallID = 7;
				break;
			case 23:
				wallID = 3;
				break;
			case 27:
				wallID = 4;
				break;
			case 28:
				wallID = 8;
				break;
			case 30:
				wallID = 6;
				break;
			case 25:
				wallID = 9;
				break;
			case 45:
				wallID = 10;
				break;
			case 42:
				wallID = 11;
				break;
			case 38:
				break;
			case 40:
				break;
			case 55:
				break;
			case 15:
				break;
			case 17:
				break;
			case 13:
				break;
			case 10:
				break;
			}*/
			
			/*setCell(currX, currY, wallID);
			summa = 0;
		}
		Debug.i(TAG, "Корректировка завершена");
		Debug.i(TAG, "*****************************************************************");*/
	}
	
	/**Функция проверяет соседние тайлы на наличаезапрещенных комбинаций и возвращает нужный вес
	 * @param x - координата тайла, относительно которого будет происходить проверка
	 * @param y - координата тайла, относительно которого будет происходить проверка
	 * @param direction - слева или справа от изначального тайла проверяем
	 * */
	private int getWeight(int x, int y, int direction)
	{
		/*final int WEIGHT_UP = 10;
		final int WEIGHT_LEFT = 15;
		final int WEIGHT_RIGHT = 17;
		final int WEIGHT_BOTTOM = 13;
		
		int localWeight = 0;			//Вес для опрееления учитывания текущего проверяемого тайла(-1 и 0, то учитываем)
		
		if(direction == ObjectOnMap.DIR_EAST || direction == ObjectOnMap.DIR_WEST)
		{
			if(y == 0)//Если проверяем на верхнем краю карты
			{
				if(Utils.typesWall.contains(getCell(x, y+1)))
					localWeight += 1;
				else if(Utils.typesFloor.contains(getCell(x, y+1)) || getCell(x, y+1) == TILE_DOOR)
					localWeight += -1;
				//else localWeight += 0;
			}
			else if(y == wContext.world.getLevel(currLevel).getHeight()-1)//если проверяем на нижнем краю карты
			{
				if(Utils.typesWall.contains(getCell(x, y-1)))
					localWeight += 1;
				else if(Utils.typesFloor.contains(getCell(x, y-1)) || getCell(x, y-1) == TILE_DOOR)
					localWeight += -1;
			}
			else//если проверяем не с краев
			{
				//Проверим верхний тайл
				if(Utils.typesWall.contains(getCell(x, y-1)))
					localWeight += 1;
				else if(Utils.typesFloor.contains(getCell(x, y-1)) || getCell(x, y-1) == TILE_DOOR)
					localWeight += -1;
				//ПРроверим нижний тайл
				if(Utils.typesWall.contains(getCell(x, y+1)))
					localWeight += 1;
				else if(Utils.typesFloor.contains(getCell(x, y+1)) || getCell(x, y+1) == TILE_DOOR)
					localWeight += -1;
			}
		}
		else if(direction == ObjectOnMap.DIR_SOUTH || direction == ObjectOnMap.DIR_NORTH)
		{
			if(x == 0)//если проверяем на левом краю карты
			{
				if(Utils.typesWall.contains(getCell(x+1, y)))
					localWeight += 1;
				else if(Utils.typesFloor.contains(getCell(x+1, y)) || getCell(x+1, y) == TILE_DOOR)
					localWeight += -1;
			}
			else if(x == wContext.world.getLevel(currLevel).getWidth()-1)//если проверяем на правом краю карты
			{
				if(Utils.typesWall.contains(getCell(x-1, y)))
					localWeight += 1;
				else if(Utils.typesFloor.contains(getCell(x-1, y)) || getCell(x-1, y) == TILE_DOOR)
					localWeight += -1;
			}
			else//если проверяем не с краев карты
			{
				//Проверим левый тайл
				if(Utils.typesWall.contains(getCell(x-1, y)))
					localWeight += 1;
				else if(Utils.typesFloor.contains(getCell(x-1, y)) || getCell(x-1, y) == TILE_DOOR)
					localWeight += -1;
				//Проверим правый тайл
				if(Utils.typesWall.contains(getCell(x+1, y)))
					localWeight += 1;
				else if(Utils.typesFloor.contains(getCell(x+1, y)) || getCell(x+1, y) == TILE_DOOR)
					localWeight += -1;
			}
		}
		
		if(localWeight > 0)
			return 0;
		if(localWeight <= 0)
		{
			if(direction == ObjectOnMap.DIR_EAST)
				return WEIGHT_RIGHT;
			if(direction == ObjectOnMap.DIR_WEST)
				return WEIGHT_LEFT;
			if(direction == ObjectOnMap.DIR_NORTH)
				return WEIGHT_UP;
			if(direction == ObjectOnMap.DIR_SOUTH)
				return WEIGHT_BOTTOM;
		}
		
		/*if(isVertical)
		{
			if(y == 0 && Utils.typesWall.contains(getCell(x, y+1)))
				return 0;
			else if(y == 0 && !Utils.typesWall.contains(getCell(x, y+1)))
			{
				if(left)
					return WEIGHT_LEFT;
				else
					return WEIGHT_RIGHT;
			}
				
				
			if(y != 0 && Utils.typesWall.contains(getCell(x, y-1)))
			{
				if(y < wContext.world.getLevel(currLevel).getHeight()-1)
					
			}
		}
		else
		{
			
		}*/
		
		return -1;
	}
	
	/** Функция проверяет, что находится перед коридором и говорит можно ли дальше его строить
	 * @param x - Координата х
	 * @param y - Координата у
	 * @param dir - Направление построения
	 * @return 0 - Можно продолжить строительство, 1 - нельзя и нужна стена, 2 - нельзя и нужна дверь
	 * */
	private int isCanGo(int x, int y, int dir)
	{
		int kx = 0, ky = 0;
		if(dir == ObjectOnMap.DIR_NORTH || dir == ObjectOnMap.DIR_SOUTH)
		{
			if(dir == ObjectOnMap.DIR_NORTH)
				ky = -1;
			else
				ky = 1;
			if(getCell(x+1, y) == TILE_NONE)//Если по центру коридора нет ничего, то продолжаем строить его
				return 0;
			else if(Utils.typesWall.contains(getCell(x, y)) && Utils.typesWall.contains(getCell(x+1, y)) && Utils.typesWall.contains(getCell(x+2, y)))//Если наткнулись на стену по всей ширине коридора
			{
				Debug.i(TAG, "-------------==============------------");
				Debug.i(TAG, "isCanGo: x = " + x + ", y = " + y);
				Debug.i(TAG, "isCanGo: 0: " + getCell(x, y-1*ky) + " " + getCell(x+1, y-1*ky) + " " + getCell(x+2, y-1*ky));
				Debug.i(TAG, "isCanGo: 1: " + getCell(x, y) + " " + getCell(x+1, y) + " " + getCell(x+2, y));
				Debug.i(TAG, "isCanGo: 2: " + getCell(x, y+1*ky) + " " + getCell(x+1, y+1*ky) + " " + getCell(x+2, y+1*ky));
				Debug.i(TAG, "-------------==============------------");
				//Если следующий ряд свободен от стен и от дверей, то можем поставить на стену дверь
				if(Utils.typesFloor.contains(getCell(x+1, y+1*ky)))
					return 2;
				else if(getCell(x+1, y+1*ky) == TILE_NONE)//Если строим поворот, то начнем со стены, потому тут продолжим строительство
					return 0;
				else if(Utils.typesWall.contains(getCell(x, y+1*ky)) && Utils.typesWall.contains(getCell(x+1, y+1*ky)) && Utils.typesWall.contains(getCell(x+2, y+1*ky)))
					return 0;
			}
			else
				return 1;
		}
		else if(dir == ObjectOnMap.DIR_WEST || dir == ObjectOnMap.DIR_EAST)
		{
			if(dir == ObjectOnMap.DIR_WEST)
				kx = -1;
			else
				kx = 1;
			if(getCell(x, y+1) == TILE_NONE)
				return 0;
			else if(Utils.typesWall.contains(getCell(x, y)) && Utils.typesWall.contains(getCell(x, y+1)) && Utils.typesWall.contains(getCell(x, y+2)))
			{
				Debug.i(TAG, "-------------==============------------");
				Debug.i(TAG, "isCanGo: x = " + x + ", y = " + y);
				Debug.i(TAG, "isCanGo: 0: " + getCell(x-1*kx, y) + " " + getCell(x-1*kx, y+1) + " " + getCell(x-1*kx, y+2));
				Debug.i(TAG, "isCanGo: 1: " + getCell(x, y) + " " + getCell(x, y+1) + " " + getCell(x, y+2));
				Debug.i(TAG, "isCanGo: 2: " + getCell(x+1*kx, y) + " " + getCell(x+1*kx, y+2) + " " + getCell(x+1*kx, y+2));
				Debug.i(TAG, "-------------==============------------");
				if(Utils.typesFloor.contains(getCell(x+1*kx, y+1)))
					return 2;
				else if(getCell(x+1*kx, y+1) == TILE_NONE)
					return 0;
				else if(Utils.typesWall.contains(getCell(x+1*kx, y)) && Utils.typesWall.contains(getCell(x+1*kx, y+1)) && Utils.typesWall.contains(getCell(x+1*kx, y+2)))
					return 0;
			}
			else
				return 1;
		}
			
		return 1;
	}
	
	/**Функция проверяет наличие свободных комнат и возвращает первую свободную из списка
	 * */
	private LevelDoor getFreeDoor()
	{
		Debug.i(TAG, "Пытаемся получить свободную дверь");
		if(mLevelDoors.size() == 0)
			return null;
		
		for(LevelDoor ld : mLevelDoors)
		{
			if(ld.isFree)
			{
				Debug.i(TAG, "Получили дверь по координатам: (" + ld.mCoord.getX() + ", " + ld.mCoord.getY() + ")");
				return ld;			//Вернем свободную комнату
			}
			else 
			{
				Debug.e(TAG, "Дверь по координатам: (" + ld.mCoord.getX() + ", " + ld.mCoord.getY() + ") занята!");
			}
		}
		return null;
	}

    /**Находит дверь по координатам на уровне
     * @param x - Координата двери
     * @param y - Координата двери
     * */
	private LevelDoor getDoorByCoord(int x, int y)
	{
		for(LevelDoor ld : mLevelDoors)
		{
			if(ld.mCoord.getX() == x && ld.mCoord.getY() == y)
				return ld;
		}
		return null;
	}
	
	private void clearDoors()
	{
		ArrayList<LevelDoor> buf = new ArrayList<LevelDoor>();
		for(int i = 0; i < mLevelDoors.size(); i++)
		{
			buf.add(mLevelDoors.get(i));
		}
		//for(LevelDoor ld : mLevelDoors)
		for(int i = 0; i < buf.size(); i++)
		{
			LevelDoor ld = buf.get(i);
			if(ld.isFree)
			{
				//setCell(ld.mCoord.getX(), ld.mCoord.getY(), TILE_NONE);
				mLevelDoors.remove(ld);
			}
		}
	}
	
	public boolean generateNewLevel()
	{
		/*wContext.world = new World();														//Создадим хранилище уровней
		
		//for(int i = 0; i < wContext.world.getCountLevels(); i++)							//Создадим карты для всех уровней разом
		for(int i = 0; i < 1; i++)															//Создадим карты для всех уровней разом
		{
			//numObjects = Utils.getRand(wContext.world.MINROOMS, wContext.world.MAXROOMS);	//Определимся с максимальным количеством объектов на уровне
			numObjects = 5;
			chanceRoom = Utils.getRand(World.MINCHANCEROOM, World.MAXCHANCEROOM);			//Шанс выпадения комнаты
			//chanceCorridor = 100 - chanceRoom;											//Шанс выпадение коридора
			//int width_level = Utils.getRand(World.MINLEVELWIDTH, World.MAXLEVELWIDTH);	//Ширина уровня(х)
			int width_level = 48;															//Ширина уровня(х)
			//int height_level = Utils.getRand(World.MINLEVELHEIGHT, World.MAXLEVELHEIGHT);	//Высота уровня(y)
			int height_level = 48;															//Высота уровня(y)
			
			//int tries = 0;					//Количество попыток поставить объект на карте =)
			//boolean isFreePlace = true;		//Если еще свободное место на карте(можно ли еще воткнуть туда хоть что-то из комнат)
			//boolean isFreeObjects = true;		//Если нет свободных объектов, то завершим формирование уровня
			int countObjects = 0;				//Текущее количество объектов на карте
			
			Level newLevel = new Level(width_level, height_level);						//Создадим уровень
			wContext.world.addLevel(newLevel);
			
			while(true)//Займемся добавлением всех объектов на уровень, пока у нас есть свободное место
			{
				//ObjectOnMap currObject;				//Первый свободный объект на уровне, к которому будем прибавлять новые объекты
				LevelDoor mLD = null;
				
				//currObject = getFreeObject();
				mLD = getFreeDoor();
				
				if(mLD == null && objectsMap.size() > 0)
				{
					//Если нет больше свободных дверей, а комнаты/коридоры стоят на карте, то закончим формирование уровня
					break;
				}
				
				if(mLD == null)
				{
					//Если нет дверей на карте и нет объектов, то поставим первой комнату
					setRoom(mLD);
				}
				else if(mLD != null)
				{
					//Если есть дверь, то вычислим, что дальше ставить на уровень
					int chance = Utils.getRand(0, 100);		//Посчитаем шанс выпадения
					if(chance <= chanceRoom)
					{
						//Выпала комната, поставим ее
						setRoom(mLD);
					}
					else if(chance > chanceRoom && chance <= 100)
					{
						//Выпал коридор, поставим его
						setCorridor(mLD);
					}
				}
				
				countObjects = objectsMap.size();
				if(countObjects >= numObjects)
					break;
			}
			//currLevel = wContext.world.getLevels().size();
		}
		
		//Удалим все двери, что свободны
		clearDoors();
		Debug.i(TAG, "Сгенерировано объектов на карте = " + objectsMap.size());*/
		
		return true;
	}
	
	/**
	 * Дополнительный класс для хранения дверей на уровне*/
	private class LevelDoor
	{
		public Coord mCoord;        //место на уровне, где находится дверь
		public boolean isFree;      //ведет ли дверь вникуда или за ней есть коридор или комната
		public int mDir;
	}
}
