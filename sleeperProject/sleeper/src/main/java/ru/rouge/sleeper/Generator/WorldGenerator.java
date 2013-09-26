package ru.rouge.sleeper.Generator;

import android.util.Log;

import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXObject;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileSet;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.util.debug.Debug;

import java.util.ArrayList;
import java.util.HashSet;

import ru.rouge.sleeper.Managers.ResourceManager;
import ru.rouge.sleeper.Map.GameMap;
import ru.rouge.sleeper.Objects.Door;
import ru.rouge.sleeper.Utils.Coord;
import ru.rouge.sleeper.Utils.Rect;
import ru.rouge.sleeper.Utils.Size;
import ru.rouge.sleeper.Utils.Utils;
import ru.rouge.sleeper.WorldContext;

public final class WorldGenerator
{
	private final static String TAG = "WorldGenerator";
	
	public final static int TILE_NONE = 0;
	//public final static int TILE_DOOR = 0;		    //Надо будет заменить списком идентификаторов дверей
	
	public final static int WIDTH_CORIDOR = 3;		    //Минимальная ширина коридора
    public final static int MIN_LENGTH_CORRIDOR = 3;    //Минимальная длинна коридора
	
	private int chanceRoom = 0;     				    //Шансы выпадения комнаты и корридора
	private int numObjects = 0;						    //Количество комнат на карте
	private int currLevel = 0;                          //Текущий генерируемый уровен
	
	private WorldContext wContext;

	private ArrayList<ObjectOnMap> objectsMap;		    //Для последующего добавления предметов на карту, сундуков, монстров и т.д.
	private ArrayList<LevelDoor> mLevelDoors;		    //Для соединения всех комнат + разнообразие коридоров
    private int[] mCheckTile;                           //Вспомогательный массив, указывающий на то, что тайл проверен или нет

	public WorldGenerator(WorldContext cont)
	{
		this.wContext = cont;
		objectsMap = new ArrayList<ObjectOnMap>();//ArrayList<ObjectOnMap>();
		mLevelDoors = new ArrayList<LevelDoor>();
	}

    public WorldGenerator()
    {
        wContext = WorldContext.getInstance();
        objectsMap = new ArrayList<ObjectOnMap>();//ArrayList<ObjectOnMap>();
        mLevelDoors = new ArrayList<LevelDoor>();
    }
	
	public void startGeneration(int level)
	{
        Debug.i("Start generation with level = " + level);
		this.currLevel = level;
	}
	
	private int getCell(int x, int y, final int layer)
	{
        if(wContext.mWorld.mLevels.get(currLevel).getTMXLayers().get(layer).getTMXTile(x, y) == null)
            return TILE_NONE;
        else
            return wContext.mWorld.mLevels.get(currLevel).getTMXLayers().get(layer).getTMXTile(x, y).getGlobalTileID();
	}
	
	private void setCell(int x, int y, int id, final int layer)
	{
        assert(currLevel < wContext.mWorld.mLevels.size());
        assert(x < wContext.mWorld.mLevels.get(currLevel).getTileColumns() && x > 0);
        assert(y < wContext.mWorld.mLevels.get(currLevel).getTileRows() && y > 0);
		
        try
        {
            wContext.mWorld.mLevels.get(currLevel).getTMXLayers().get(layer).addTileByGlobalTileID(x, y, id, null);
        }
        catch (Exception e)
        {
            Debug.e(e);
        }
	}
	
	private void createDoor(int x, int y, int dir, boolean isFree)
	{
        if(x == 0 || x == 1 || x == wContext.mWorld.mLevels.get(currLevel).getTileColumns() || x == wContext.mWorld.mLevels.get(currLevel).getTileColumns()-1)
            return;
        if(y == 0 || y == 1 || y == wContext.mWorld.mLevels.get(currLevel).getTileRows() || y == wContext.mWorld.mLevels.get(currLevel).getTileRows()-1)
            return;

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

		Debug.i(TAG, "Setup doors begin");
		while(i < count)
		{
			dir = Utils.getRand(0, 3);
			if(dirs.size() == 0)			//Если все двери заняты и по каким-то причинам счетчик != count, то останавливаем расстановку дверей
				break;
			if(dirs.contains(dir))			//Проверка с исключением
			{
				Debug.i(TAG, "Choose side of room: dir = " + dir);
				//Проверяем на наличае двери на стене
				if(!isGetDoor(room, dir))
				{
					Debug.i(TAG, "Door is not setup yet");
					if(dir == ObjectOnMap.DIR_SOUTH || dir == ObjectOnMap.DIR_NORTH)//горизонталь
					{
						Debug.i(TAG, "Choose place on wall: from 1 till " + (room.mSize.getWidth()-1));
						place = Utils.getRand(1, room.mSize.getWidth()-2);//Выбираем место на стене, исключая угловые тайлы
						Debug.i(TAG, "The place was chosen: " + place);
						if(isCanSetTile(room, dir, place))
						{
							if(dir == ObjectOnMap.DIR_NORTH)
							{
                                if(Utils.typesWall.contains(getCell(room.mCoord.getX() + place, room.mCoord.getY()+1, GameMap.LAYER_WALLS)) || getDoorByCoord(room.mCoord.getX() + place, room.mCoord.getY()+1) != null)  //Посмотрим, что напротив двери внутри комнаты
                                {
                                    Debug.w(TAG, "Inner wall blocks to setup door");
                                    //Случайно сдвинемся на один тайл от внутренней стены
                                    if(Utils.getRand(0,1) == 0)
                                    {
                                        if(place != 1)
                                            place = place - 1;
                                        else
                                            place = place + 1;
                                    }
                                    else
                                    {
                                        if(place != room.mSize.getWidth()-2)
                                            place = place + 1;
                                        else
                                            place = place - 1;
                                    }
                                }
                                Debug.i(TAG, "Setup door by place: (" + (room.mCoord.getX() + place) + " , " + room.mCoord.getY() + ")");
                                createDoor(room.mCoord.getX() + place, room.mCoord.getY(), dir, true);
                            }
							else// if(dir == ObjectOnMap.DIR_SOUTH)
							{
                                if(Utils.typesWall.contains(getCell(room.mCoord.getX() + place, room.mCoord.getY() + (room.mSize.getHeight()-1) - 1, GameMap.LAYER_WALLS)) || getDoorByCoord(room.mCoord.getX() + place, room.mCoord.getY() + (room.mSize.getHeight()-1) - 1) != null)  //Посмотрим, что напротив двери внутри комнаты
                                {
                                    Debug.w(TAG, "Inner wall blocks to setup door");
                                    //Случайно сдвинемся на один тайл от внутренней стены
                                    if(Utils.getRand(0,1) == 0)
                                    {
                                        if(place != 1)
                                            place = place - 1;
                                        else
                                            place = place + 1;
                                    }
                                    else
                                    {
                                        if(place != room.mSize.getWidth()-2)
                                            place = place + 1;
                                        else
                                            place = place - 1;
                                    }
                                }
                                Debug.i(TAG, "Setup door by place: (" + (room.mCoord.getX() + place) + " , " + (room.mCoord.getY() + (room.mSize.getHeight()-1)) + ")");
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
                                if(Utils.typesWall.contains(getCell(room.mCoord.getX() + 1, room.mCoord.getY() + place, GameMap.LAYER_WALLS)) || getDoorByCoord(room.mCoord.getX() + 1, room.mCoord.getY() + place) != null)  //Посмотрим, что напротив двери внутри комнаты
                                {
                                    Debug.w(TAG, "Inner wall blocks to setup door");
                                    //Случайно сдвинемся на один тайл от внутренней стены
                                    if(Utils.getRand(0,1) == 0)
                                    {
                                        if(place != 1)
                                            place = place - 1;
                                        else
                                            place = place + 1;
                                    }
                                    else
                                    {
                                        if(place != room.mSize.getHeight()-2)
                                            place = place + 1;
                                        else
                                            place = place - 1;
                                    }
                                }

								Debug.i(TAG, "Setup door by place: (" + (room.mCoord.getX()) + " , " + (room.mCoord.getY() + place) + ")");
								createDoor(room.mCoord.getX(), room.mCoord.getY() + place, dir, true);
							}
							else// if(dir == ObjectOnMap.DIR_EAST)
							{
                                if(Utils.typesWall.contains(getCell(room.mCoord.getX() + (room.mSize.getWidth()-1) - 1, room.mCoord.getY() + place, GameMap.LAYER_WALLS)) || getDoorByCoord(room.mCoord.getX() + (room.mSize.getWidth()-1) - 1, room.mCoord.getY() + place) != null)  //Посмотрим, что напротив двери внутри комнаты
                                {
                                    Debug.w(TAG, "Inner wall blocks to setup door");
                                    //Случайно сдвинемся на один тайл от внутренней стены
                                    if(Utils.getRand(0,1) == 0)
                                    {
                                        if(place != 1)
                                            place = place - 1;
                                        else
                                            place = place + 1;
                                    }
                                    else
                                    {
                                        if(place != room.mSize.getHeight()-2)
                                            place = place + 1;
                                        else
                                            place = place - 1;
                                    }
                                }

								Debug.i(TAG, "Setup door by place: (" + (room.mCoord.getX() + (room.mSize.getWidth()-1)) + " , " + (room.mCoord.getY() + place) + ")");
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
     * Создаем дверь, как объект на карте со всей логикой двери
     * */
    private void createTiledDoor(LevelDoor door)
    {
        createTiledDoor(door.mCoord.getX(), door.mCoord.getY(), door.mDir);
    }

    private void createTiledDoor(final int x, final int y, final int dir)
    {
        boolean isVertical;
        if(dir == ObjectOnMap.DIR_EAST || dir == ObjectOnMap.DIR_WEST)
            isVertical = true;
        else
            isVertical = false;

        setCell(x, y, 0, GameMap.LAYER_WALLS);                //Убрали стену
        setCell(x, y, 16, GameMap.LAYER_FLOOR);               //Добавили пол

        Door newDoor = new Door(x*32, y*32, isVertical, false, ResourceManager.getInstance().mDoorsTexture, ResourceManager.getInstance().mVBO);
        wContext.mWorld.mDoors.add(newDoor);
        wContext.mWorld.mWakables[x][y].mIndexObject = wContext.mWorld.mDoors.size()-1;
    }

	/**
	 * Функция устанавливает комнату на уровне и заносит объект в список объектов на уровне. Также происходит проверка на возможность размещения комнате на уровне
	 * @param door - Свободная дверь, к которой присоединяем коридор
	 * @return Результат установки комнаты на карту
	 * */
	private boolean setRoom(LevelDoor door)
	{
        Debug.i("setRoom()");
		int x = 0, y = 0;

        Debug.i("ResourceManager.getInstance().mRooms.size() = " + ResourceManager.getInstance().mRooms.size());

		//Выберем случайную комнату из списка
		int roomID = Utils.getRand(0, ResourceManager.getInstance().mRooms.size()-1);
		Log.i(TAG, "Choose room from array by id: roomID = " + roomID);
		TMXTiledMap currRoom = ResourceManager.getInstance().mRooms.get(roomID);//wContext.rooms.get(roomID);
		Log.i(TAG, "Randomize exit from this room");
		int countExit = Utils.getRand(2, 4);
		Log.i(TAG, "Count of exits the room by id (" + roomID + ") = " + countExit);
        TMXTiledMap mCurrLevel = wContext.mWorld.mLevels.get(currLevel);
		
		if(door == null)
		{
            Debug.i(TAG, "door == null");
			//Вычислим координаты для первой комнаты, чтобы она была гарантированно в пределах уровня
			//x = Utils.getRand(0, wContext.world.getLevel(currLevel).getWidth()// - currRoom.getColumns*currRoom.getWidth());
			//y = Utils.getRand(0, wContext.world.getLevel(currLevel).getHeight()// - currRoom.getRows*currRoom.getHeight());
			x = 22;
			y = 22;

            //Добавим в комнату точку рождения игрока
            Debug.e(TAG, "Create spawn player point");
            TMXObject playerSpawn = new TMXObject("player_spawn", "player", 23*32, 23*32, 32, 32);
            wContext.mWorld.mSpawns.add(playerSpawn);
            Debug.e(TAG, "Done spawn player point");
		}
		else
		{
            Debug.i(TAG, "door != null");
            Debug.i(TAG, "door.x = " + door.mCoord.getX() + ", door.y = " + door.mCoord.getY() + ", door.dir = " + door.mDir);
            Debug.i(TAG, "room.x = " + currRoom.getTileColumns() + ", room.y = " + currRoom.getTileRows());
			//Вычислим координаты для комнаты, относительно двери
			
			int place;
            Debug.i("=============Calculate place=============");
			//Вычислим тайл, на котором поставим дверь
			if(door.mDir == ObjectOnMap.DIR_NORTH || door.mDir == ObjectOnMap.DIR_SOUTH)
			{
                //Проверяем, вместится ли комната по у от двери до края комнаты
                if(currRoom.getTileRows() > door.mCoord.getY())
                {
                    Debug.e(TAG, "Room not fit in row from door coords. NORTH");
                    return false;
                }
                if(currRoom.getTileRows() > (mCurrLevel.getTileRows() - door.mCoord.getY()))
                {
                    Debug.e(TAG, "Room not fit in row from door coords. SOUTH");
                    return false;
                }

                //Динамически изменяем пределы place, чтобы x-place > 0 и x+place <= currRum.getTileColumns()
                int min, max;       //Пределы для выбора места на стене
                if(currRoom.getTileColumns() > door.mCoord.getX())
                {
                    min = 1;                    //минимальное значение
                    max = door.mCoord.getX();   //максимальное значение
                }
                else if(currRoom.getTileColumns() > (mCurrLevel.getTileColumns() - door.mCoord.getX()))
                {
                    min = currRoom.getTileColumns() - (mCurrLevel.getTileColumns() - door.mCoord.getX());
                    max = currRoom.getTileColumns()-2;
                }
                else
                {
                    min = 1;
                    max = currRoom.getTileColumns()-2;      //-2 - стены не учитываем, только по полу
                }
                place = Utils.getRand(min, max);

                //Проверка на препятствия внутри комнаты напротив двери
                if(door.mDir == ObjectOnMap.DIR_NORTH)
                {
                    //if(Utils.typesWall.contains(getCell(door.mCoord.getX() - place, door.mCoord.getY() - (currRoom.getTileRows() - 1)-1, GameMap.LAYER_WALLS)) || getDoorByCoord(door.mCoord.getX() - place, door.mCoord.getY() - (currRoom.getTileRows() - 1)-1) != null)  //Посмотрим, что напротив двери внутри комнаты
                    //if(Utils.typesWall.contains(getCell(door.mCoord.getX(), door.mCoord.getY() - 1, GameMap.LAYER_WALLS)) || getDoorByCoord(door.mCoord.getX(), door.mCoord.getY() - 1) != null)  //Посмотрим, что напротив двери внутри комнаты
                    if(Utils.typesWall.contains(currRoom.getTMXLayers().get(GameMap.LAYER_WALLS).getTMXTile(place, (currRoom.getTileRows() - 1)-1).getGlobalTileID()) || getDoorByCoord(door.mCoord.getX(), door.mCoord.getY() - 1) != null)  //Посмотрим, что напротив двери внутри комнаты
                    {
                        Debug.w(TAG, "setRoom: Inner wall blocks to setup door, DIR_EAST");
                        //Случайно сдвинемся на один тайл от внутренней стены
                        if(Utils.getRand(0,1) == 0)
                        {
                            //if(place != 1)
                            if(place != min)
                                place = place - 1;
                            else
                                place = place + 1;
                        }
                        else
                        {
                            //if(place != currRoom.getTileColumns()-2)
                            if(place != max)
                                place = place + 1;
                            else
                                place = place - 1;
                        }
                    }

                    x = door.mCoord.getX() - place;
                    y = door.mCoord.getY() - (currRoom.getTileRows() - 1);
                }
                else if(door.mDir == ObjectOnMap.DIR_SOUTH)
                {
                    //if(Utils.typesWall.contains(getCell(door.mCoord.getX() - place, door.mCoord.getY()+1, GameMap.LAYER_WALLS)) || getDoorByCoord(door.mCoord.getX() - place, door.mCoord.getY()+1) != null)  //Посмотрим, что напротив двери внутри комнаты
                    //if(Utils.typesWall.contains(getCell(door.mCoord.getX(), door.mCoord.getY()+1, GameMap.LAYER_WALLS)) || getDoorByCoord(door.mCoord.getX(), door.mCoord.getY()+1) != null)  //Посмотрим, что напротив двери внутри комнаты
                    if(Utils.typesWall.contains(currRoom.getTMXLayers().get(GameMap.LAYER_WALLS).getTMXTile(place, 1).getGlobalTileID()) || getDoorByCoord(door.mCoord.getX(), door.mCoord.getY()+1) != null)  //Посмотрим, что напротив двери внутри комнаты
                    {
                        Debug.w(TAG, "setRoom: Inner wall blocks to setup door, DIR_EAST");
                        //Случайно сдвинемся на один тайл от внутренней стены
                        if(Utils.getRand(0,1) == 0)
                        {
                            //if(place != 1)
                            if(place != min)
                                place = place - 1;
                            else
                                place = place + 1;
                        }
                        else
                        {
                            //if(place != currRoom.getTileColumns()-2)
                            if(place != max)
                                place = place + 1;
                            else
                                place = place - 1;
                        }
                    }

                    x = door.mCoord.getX() - place;
                    y = door.mCoord.getY();
                }
			}
			else if(door.mDir == ObjectOnMap.DIR_WEST || door.mDir == ObjectOnMap.DIR_EAST)
			{
                if(currRoom.getTileColumns() > door.mCoord.getX())
                {
                    Debug.e(TAG, "Room not fix in row from door coords. WEST");
                    return false;
                }
                if(currRoom.getTileColumns() > (mCurrLevel.getTileColumns() - door.mCoord.getX()))
                {
                    Debug.e(TAG, "Room not fix in row from door coords. EAST");
                    return false;
                }

                //Динамически изменяем пределы place, чтобы y-place > 0 и y+place <= currRum.getTileRows()
                int min, max;                       //Пределы для выбора места на стене
                if(currRoom.getTileRows() > door.mCoord.getY())
                {
                    min = 1;                            //Минимальное значение
                    max = door.mCoord.getY();           //Максимальное значение
                }
                else if(currRoom.getTileRows() > (mCurrLevel.getTileRows() - door.mCoord.getY()))
                {
                    min = currRoom.getTileRows() - (mCurrLevel.getTileRows() - door.mCoord.getY());
                    max = currRoom.getTileRows()-2;
                }
                else
                {
                    min = 1;
                    max = currRoom.getTileRows()-2;
                }
				place = Utils.getRand(min, max);

                //Проверка на препятствия внутри комнаты напротив двери
                if(door.mDir == ObjectOnMap.DIR_EAST)
                {
                    //if(Utils.typesWall.contains(getCell(door.mCoord.getX()+1, door.mCoord.getY() - place, GameMap.LAYER_WALLS)) || getDoorByCoord(door.mCoord.getX()+1, door.mCoord.getY() - place) != null)  //Посмотрим, что напротив двери внутри комнаты
                    //if(Utils.typesWall.contains(getCell(door.mCoord.getX()+1, door.mCoord.getY(), GameMap.LAYER_WALLS)) || getDoorByCoord(door.mCoord.getX()+1, door.mCoord.getY()) != null)  //Посмотрим, что напротив двери внутри комнаты
                    if(Utils.typesWall.contains(currRoom.getTMXLayers().get(GameMap.LAYER_WALLS).getTMXTile(1, place).getGlobalTileID()) || getDoorByCoord(door.mCoord.getX()+1, door.mCoord.getY()) != null)  //Посмотрим, что напротив двери внутри комнаты
                    {
                        Debug.w(TAG, "setRoom: Inner wall blocks to setup door, DIR_EAST");
                        //Случайно сдвинемся на один тайл от внутренней стены
                        if(Utils.getRand(0,1) == 0)
                        {
                            //if(place != 1)
                            if(place != min)
                                place = place - 1;
                            else
                                place = place + 1;
                        }
                        else
                        {
                            //if(place != currRoom.getTileRows()-2)
                            if(place != max)
                                place = place + 1;
                            else
                                place = place - 1;
                        }
                    }

                    x = door.mCoord.getX();
                    y = door.mCoord.getY() - place;
                }
                else if(door.mDir == ObjectOnMap.DIR_WEST)
                {
                    //if(Utils.typesWall.contains(getCell(door.mCoord.getX() - (currRoom.getTileColumns() - 1) - 1, door.mCoord.getY() - place, GameMap.LAYER_WALLS)) || getDoorByCoord(door.mCoord.getX()-1, door.mCoord.getY() - place) != null)  //Посмотрим, что напротив двери внутри комнаты
                    //if(Utils.typesWall.contains(getCell(door.mCoord.getX() - 1, door.mCoord.getY(), GameMap.LAYER_WALLS)) || getDoorByCoord(door.mCoord.getX()-1, door.mCoord.getY()) != null)  //Посмотрим, что напротив двери внутри комнаты
                    if(Utils.typesWall.contains(currRoom.getTMXLayers().get(GameMap.LAYER_WALLS).getTMXTile((currRoom.getTileColumns() - 1) - 1, place).getGlobalTileID()) || getDoorByCoord(door.mCoord.getX()-1, door.mCoord.getY()) != null)  //Посмотрим, что напротив двери внутри комнаты
                    {
                        Debug.w(TAG, "setRoom: Inner wall blocks to setup door, DIR_WEST");
                        //Случайно сдвинемся на один тайл от внутренней стены
                        if(Utils.getRand(0,1) == 0)
                        {
                            //if(place != 1)
                            if(place != min)
                                place = place - 1;
                            else
                                place = place + 1;
                        }
                        else
                        {
                            //if(place != currRoom.getTileRows()-2)
                            if(place != max)
                                place = place + 1;
                            else
                                place = place - 1;
                        }
                    }

                    x = door.mCoord.getX() - (currRoom.getTileColumns() - 1);
                    y = door.mCoord.getY() - place;
                }
			}
			//Теперь посчитаем отступы по х и по у

            Debug.i("=============Calculate place done=============");

			countExit--;			//Одну дверь установили
		}

        Debug.i(TAG, "currLevel = " + currLevel);
        Debug.i(TAG, "get mLevel size = " + wContext.mWorld.mLevels.size());
        Debug.i(TAG, "currRoom.getTileRows() = " + currRoom.getTileRows());
        Debug.i(TAG, "currRoom.getTileColumns() = " + currRoom.getTileColumns());
        Debug.i(TAG, "Try to locate room in map");
        for(int ytemp = y; ytemp < y + currRoom.getTileRows(); ytemp++)
		{
            if(ytemp < 0 || ytemp > wContext.mWorld.mLevels.get(currLevel).getTileRows()-1)
			{
                Debug.e(TAG, "The room does not fit by height: ytemp = " + ytemp + ", levelHeight = " + wContext.mWorld.mLevels.get(currLevel).getTileRows());
				return false;
			}
            for(int xtemp = x; xtemp < x+currRoom.getTileColumns(); xtemp++)
			{
                if(xtemp < 0 || xtemp > wContext.mWorld.mLevels.get(currLevel).getTileColumns()-1)
				{
                    Debug.e(TAG, "The room does not fit by width: xtemp = " + xtemp + ", levelWidth = " + wContext.mWorld.mLevels.get(currLevel).getTileColumns());
					return false;
				}
				if((getCell(xtemp, ytemp, GameMap.LAYER_FLOOR) != TILE_NONE) && (!Utils.typesWall.contains(getCell(xtemp, ytemp, GameMap.LAYER_FLOOR))) && (getDoorByCoord(xtemp, ytemp) == null)/*(getCell(xtemp, ytemp) != TILE_DOOR)*/)
				{
					Debug.e(TAG, "The room crosses a unique tile by coordinates : (" + xtemp + " , " + ytemp + ") with tile id = " + getCell(xtemp, ytemp, GameMap.LAYER_WALLS));
					return false;
				}
			}
		}
        Debug.i(TAG, "Locate done");
		
		Debug.i(TAG, "Fill Map by tile of the room");
        for(int ytemp = 0; ytemp < currRoom.getTileRows(); ytemp++)
		{
            for(int xtemp = 0; xtemp < currRoom.getTileColumns(); xtemp++)
			{
				LevelDoor ld = getDoorByCoord(xtemp + x, ytemp + y);
				if(ld == null)
                {
                    setCell(xtemp + x, ytemp + y, currRoom.getTMXLayers().get(GameMap.LAYER_FLOOR).getTMXTile(xtemp, ytemp).getGlobalTileID(), GameMap.LAYER_FLOOR);
                    setCell(xtemp + x, ytemp + y, currRoom.getTMXLayers().get(GameMap.LAYER_WALLS).getTMXTile(xtemp, ytemp).getGlobalTileID(), GameMap.LAYER_WALLS);
                }
				else
					ld.isFree = false;
			}
		}
		if(door != null)
		{
			Debug.i(TAG, "Set common door");
            //Добавляем общую дверь
            createTiledDoor(door);
		}
		
        Rect rectRoom = new Rect(x, y, currRoom.getTileColumns(), currRoom.getTileRows());
		setupDoors(rectRoom, countExit);	//Расставим все двери в комнате
		
		//Добавим комнаты к списку объектов на уровне
        ObjectOnMap object = new ObjectOnMap(ObjectOnMap.TYPE_ROOM, new Coord(x, y), new Size(currRoom.getTileColumns(), currRoom.getTileRows()), countExit, false, ObjectOnMap.DIR_NONE);
		objectsMap.add(object);
		
		//Укажем, что дверь теперь занята, если ее передали в функцию
		if(door != null)
			door.isFree = false;
		
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
		
		Debug.i(TAG, "----------------Начинаем генерировать коридор---------------------");
        int length = Utils.getRand(3, wContext.mWorld.mLevels.get(currLevel).getTileColumns()/2)+1;	//Сгенерируем длину коридора = половине ширины уровня; +1 для отрисовки стены в конце коридора
		Debug.i(TAG, "The corridor length = " + length);
		int direction = -1;																	//Направление построения коридора
		//int newDirection = -1;															//Если есть повороты, то тут мы определяем новый поворот коридора
		int x = 0, y = 0;																	//Координаты начала построения коридора
		int kx = 0, ky = 0;																	//Коэфиценты, позволяющие создать правильное направление генерации коридора
        TMXTiledMap mCurrLevel = wContext.mWorld.mLevels.get(currLevel);					//Текущий уровень
		int mTurns = Utils.getRand(0, 3);													//Количество поворотов коридора
		
		Debug.i(TAG, "Count of rounds of corridor = " + mTurns);
		Debug.i(TAG, "Calculate margins relative door");
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
				Debug.i(TAG, "Draw to east!!!!");
                //Удалим дверь и не будем строить коридор, если его длинна меньше MIN_LENGTH_CORRIDOR
                if((x + length) > mCurrLevel.getTileColumns() && (mCurrLevel.getTileColumns() - x) < MIN_LENGTH_CORRIDOR)
                {
                    Debug.e(TAG, "Length of corridor less than 3!!!!");
                    deleteDoor(door);
                    return false;
                }
			}
			else
			{
				x = door.mCoord.getX()-1;
				kx = -1;
				Debug.i(TAG, "Draw to west!!!!");
                //Удалим дверь и не будем строить коридор, если его длинна меньше MIN_LENGTH_CORRIDOR
                if((x - length) < 0 && x < MIN_LENGTH_CORRIDOR)
                {
                    Debug.e(TAG, "Length of corridor less than 3!!!!");
                    deleteDoor(door);
                    return false;
                }
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
				Debug.i(TAG, "Draw to south!!!!");
                //Удалим дверь и не будем строить коридор, если его длинна меньше MIN_LENGTH_CORRIDOR
                if((y + length) > mCurrLevel.getTileRows() && (mCurrLevel.getTileRows() - y) < MIN_LENGTH_CORRIDOR)
                {
                    Debug.e(TAG, "Length of corridor less than 3!!!!");
                    deleteDoor(door);
                    return false;
                }
			}
			else
			{
				y = door.mCoord.getY()-1;
				ky = -1;
				Debug.i(TAG, "Draw to north!!!!");
                //Удалим дверь и не будем строить коридор, если его длинна меньше MIN_LENGTH_CORRIDOR
                if((y - length) < 0 && y < MIN_LENGTH_CORRIDOR)
                {
                    Debug.e(TAG, "Length of corridor less than 3!!!!");
                    deleteDoor(door);
                    return false;
                }
			}
		}
		Debug.i(TAG, "kx = " + kx);
		Debug.i(TAG, "ky = " + ky);
		Debug.i(TAG, "x = " + x);
		Debug.i(TAG, "y = " + y);
		
		//Отрисовка коридора в одном направлении
		if(!drawDirCoridor(length, x, y, kx, ky, direction, mCurrLevel))
			mTurns = 0;

        Debug.i(TAG, "Draw common door");
        //Создание общей двери
        createTiledDoor(door);
		
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
                else if(x >= mCurrLevel.getTileColumns()-3)
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
                else if(y >= mCurrLevel.getTileRows()-3)
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
				int buf;
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
				int buf;
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
			
            length = Utils.getRand(2, wContext.mWorld.mLevels.get(currLevel).getTileColumns()/2)+1;
			Debug.i(TAG, "Turn" + t + " : Длина коридора = " + length);
		
			//Отрисовка коридора в одном направлении
			if(!drawDirCoridor(length, x, y, kx, ky, direction, mCurrLevel))
				mTurns = 0;
		}
		
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
    private boolean drawDirCoridor(int length, int x, int y, int kx, int ky, int direction, TMXTiledMap mCurrLevel)
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
				endY = y + currY - ky;
			}
			else if (direction == ObjectOnMap.DIR_EAST || direction == ObjectOnMap.DIR_WEST)
			{
				currX = (i / WIDTH_CORIDOR) * kx;
				currY = (i % WIDTH_CORIDOR) * ky;
				endX = x + currX - kx;
				endY = y + 1;
			}

			LevelDoor ld = getDoorByCoord(x + currX, y + currY);
            if((x+currX)>= mCurrLevel.getTileColumns() || (x+currX) < 0 || (y+currY)>= mCurrLevel.getTileRows() || (y+currY)<0)
			{
				Debug.e(TAG, "Out of bounds of level!! Set wall in the end");
				int wallID = -1;
				if(direction == ObjectOnMap.DIR_NORTH || direction == ObjectOnMap.DIR_SOUTH)
				{
					wallID = 1;
				}
				else if(direction == ObjectOnMap.DIR_WEST || direction == ObjectOnMap.DIR_EAST)
				{
					wallID = 2;
				}
				setCell(endX, endY, wallID, GameMap.LAYER_WALLS);
                setCell(endX, endY, TILE_NONE, GameMap.LAYER_FLOOR);
				return false;
			}
			if((direction == ObjectOnMap.DIR_NORTH || direction == ObjectOnMap.DIR_SOUTH) && oldCurrY != currY)
				isGoNext = isCanGo(x, y+currY, direction);
			else if((direction == ObjectOnMap.DIR_WEST || direction == ObjectOnMap.DIR_EAST) && oldCurrX != currX)
				isGoNext = isCanGo(x+currX, y, direction);
			if(isGoNext > 0)
			{
				Debug.e(TAG, "Unique tile!! Stop create corridor");
				if(isGoNext == 2)//Нам нужна дверь в конце коридора
				{
					int localEndX = 0, localEndY = 0;		//Дверь будет ставится на текущем ряду коридора
					if(direction == ObjectOnMap.DIR_NORTH || direction == ObjectOnMap.DIR_SOUTH)
					{
						localEndX = x+1;
						localEndY = y+currY;

                        //Проверка, не на углу ли коридора дверь, если так, то удаляем ее
                        LevelDoor door = getDoorByCoord(x, localEndY);
                        if(door != null && door.isFree)
                            deleteDoor(door);

                        door = getDoorByCoord(x+2, localEndY);
                        if(door != null && door.isFree)
                            deleteDoor(door);
					}
					else if(direction == ObjectOnMap.DIR_EAST || direction == ObjectOnMap.DIR_WEST)
					{
						localEndX = x+currX;
						localEndY = y+1;

                        //Проверка, не на углу ли коридора дверь, если так, то удаляем ее
                        LevelDoor door = getDoorByCoord(localEndX, y);
                        if(door != null)
                            deleteDoor(door);

                        door = getDoorByCoord(localEndX, y+2);
                        if(door != null)
                            deleteDoor(door);
					}
                    //Если мы попали в ранее выствленную дверь
                    LevelDoor d = getDoorByCoord(localEndX, localEndY);
                    if(d != null && d.isFree)
                        deleteDoor(d);
                    if(d == null || d.isFree)
                    {
                        if(i > 1)
                        {
                            createDoor(localEndX, localEndY, direction, false);
                            createTiledDoor(localEndX, localEndY, direction);
                        }
                        else//Если за дверью сразу стена, то просто удалим стену и создадим там пол
                        {
                            if(direction == ObjectOnMap.DIR_NORTH || direction == ObjectOnMap.DIR_SOUTH)
                            {
                                localEndX = x+1;
                                localEndY = y+currY+currY;
                            }
                            else if(direction == ObjectOnMap.DIR_EAST || direction == ObjectOnMap.DIR_WEST)
                            {
                                localEndX = x+currX + currX;
                                localEndY = y+1;
                            }
                            setCell(localEndX, localEndY, TILE_NONE, GameMap.LAYER_WALLS);
                            setCell(localEndX, localEndY, 16, GameMap.LAYER_FLOOR);
                        }
                    }
                }
				else if(isGoNext == 1)//Нам нужно поставить стену
				{
					int localEndX = 0, localEndY = 0;		//Стена поставится на предыдущем ряду коридора
					int doorID = -1;
					if(direction == ObjectOnMap.DIR_NORTH || direction == ObjectOnMap.DIR_SOUTH)
					{
						localEndX = x+1;
						localEndY = y+currY-ky;
						doorID = 1;
					}
					else if(direction == ObjectOnMap.DIR_EAST || direction == ObjectOnMap.DIR_WEST)
					{
						localEndX = x+currX-kx;
						localEndY = y+1;
						doorID = 2;
					}
					setCell(localEndX, localEndY, doorID, GameMap.LAYER_WALLS);
                    setCell(localEndX, localEndY, TILE_NONE, GameMap.LAYER_FLOOR);
				}
				return false;
			}
			else
			{
				int what = -1;
                int layer = 0;
				if(direction == ObjectOnMap.DIR_EAST || direction == ObjectOnMap.DIR_WEST)
					if(currY == 0 || currY == (WIDTH_CORIDOR-1))//Тело коридора(стены)
                    {
						what = 1;
                        layer = GameMap.LAYER_WALLS;
                    }
					else if(currY == 1 && Math.abs(currX) == (length-1))//Конец коридора
					{
						what = 2;
                        layer = GameMap.LAYER_WALLS;
						//Добавим в конце коридора дверь в список
						createDoor(x + currX, y + currY, direction, true);
					}
					else//Тело коридора(пол)
                    {
						what = 16;
                        layer = GameMap.LAYER_FLOOR;
                    }
				else if(direction == ObjectOnMap.DIR_NORTH || direction == ObjectOnMap.DIR_SOUTH)
					if(currX == 0 || currX == (WIDTH_CORIDOR-1))//Тело коридора(стены)
                    {
						what = 2;
                        layer = GameMap.LAYER_WALLS;
                    }
					else if(currX == 1 && Math.abs(currY) == (length-1))//Конец коридора
					{
						what = 1;
                        layer = GameMap.LAYER_WALLS;
						createDoor(x + currX, y + currY, direction, true);
					}
					else//Тело коридора(пол)
                    {
						what = 16;
                        layer = GameMap.LAYER_FLOOR;
                    }
				
				if(ld == null)
                {
                    setCell(x + currX, y + currY, TILE_NONE, GameMap.LAYER_FLOOR);
                    setCell(x + currX, y + currY, TILE_NONE, GameMap.LAYER_WALLS);
					setCell(x + currX, y + currY, what, layer);
                }
				else
                {
					ld.isFree = false;
                }
			}
			oldCurrX = currX;
			oldCurrY = currY;
		}
		return true;
	}
	
	/**
     * Функция правит идентификаторы стен после их установки
	 * */
	public void correctWallIDs()
	{
        Debug.i("Modify Tiles", "Start correct tiles ids");
        Debug.i("Modify Tiles", "********************/BEGIN/***********************");

        TMXTiledMap mCurrLevel = wContext.mWorld.mLevels.get(currLevel);
        int length = mCurrLevel.getTileColumns() * mCurrLevel.getTileRows();

        Debug.i("Init mCheckTile");
        if(mCheckTile == null)
        {
            mCheckTile = new int[mCurrLevel.getTileColumns() * mCurrLevel.getTileRows()];
            for(int k = 0; k < mCurrLevel.getTileColumns() * mCurrLevel.getTileRows(); k++)
            {
                mCheckTile[k] = 0;
            }
        }
        Debug.i("Init done");

        for(int i = 0; i < length; i++)
        {
            int row = i / mCurrLevel.getTileColumns();
            int column = i % mCurrLevel.getTileColumns();

            //Считаем для тайла, если над ним нет стены, нет двери и сам тайл - пол
            if(getCell(column, row, GameMap.LAYER_WALLS) == TILE_NONE && getCell(column,row, GameMap.LAYER_FLOOR) != TILE_NONE && getDoorByCoord(column, row)==null)
            {
                //Считаем идентификаторы для группы тайлов вокруг текущего
                calculateIDS(column, row);
            }
        }

        Debug.i("Modify Tiles", "********************/END/***********************");
	}
	
	/**
     * Подсчет по шаблонам номеров тайлов вокруг заданного базового
     * @param column - x координата базового тайла
     * @param row - y координата базового тайла
	 * */
	private void calculateIDS(int column, int row)
	{
        //Первый тайл верхний левый: column - 1; row -1
        //Для него проверка по нижнему и правому тайлам: (column-1; row) и (column; row-1)
        Debug.i("Modify Tiles", "calculateIDS: getCell("+column+", "+row+", GameMap.LAYER_FLOOR) = " + getCell(column, row, GameMap.LAYER_FLOOR));
        Debug.i("Modify Tiles", "calculateIDS: getCell("+column+", "+row+", GameMap.LAYER_WALLS) = " + getCell(column, row, GameMap.LAYER_WALLS));

        if(Utils.typesWall.contains(getCell(column-1, row-1, GameMap.LAYER_WALLS)))
        {
            Debug.i("Modify Tiles", "calculateIDS: 1 tile with column = " + (column-1) + " row = " + (row-1));
            int numWalls = 0;//0 - нет информации, не меняем, 1 - нет информации, не меняем, 2 - нет информации, не меняем, 3 - угол(низ, право)
            //Проверим первый тайл
            if(Utils.typesWall.contains(getCell(column-1, row, GameMap.LAYER_WALLS)) || (Utils.typesFloor.contains(getCell(column-1, row, GameMap.LAYER_FLOOR)) && getDoorByCoord(column-1, row) != null))
            {
                numWalls += 1;
            }
            //Проверим второй тайл
            if(Utils.typesWall.contains(getCell(column, row-1, GameMap.LAYER_WALLS)) || (Utils.typesFloor.contains(getCell(column, row-1, GameMap.LAYER_FLOOR)) && getDoorByCoord(column, row-1) != null))
            {
                numWalls += 2;
            }
            //Применим шаблон
            Debug.i("Modify Tiles", "calculateIDS: numWalls = " + numWalls);
            if(numWalls == 3)
                modifyTileIDS(getCell(column-1, row-1, GameMap.LAYER_WALLS), 5, column-1, row-1, 0);
        }

        //2ой тайл верхний средний: column; row -1
        if(Utils.typesWall.contains(getCell(column, row-1, GameMap.LAYER_WALLS)))
        {
            Debug.i("Modify Tiles", "calculateIDS: 2 tile with column = " + (column) + " row = " + (row-1));
            int numWalls = 0;//0 - нет тайлов вокруг(тупик вниз), 1 - тупик вправо, 2 - тупик влево, 3 - гориз стена
            //Проверим первый тайл
            if(Utils.typesWall.contains(getCell(column-1, row-1, GameMap.LAYER_WALLS)) || (Utils.typesFloor.contains(getCell(column-1, row-1, GameMap.LAYER_FLOOR)) && getDoorByCoord(column-1, row-1) != null))
            {
                numWalls += 1;
            }
            //Проверим второй тайл
            if(Utils.typesWall.contains(getCell(column+1, row-1, GameMap.LAYER_WALLS)) || (Utils.typesFloor.contains(getCell(column+1, row-1, GameMap.LAYER_FLOOR)) && getDoorByCoord(column+1, row-1) != null))
            {
                numWalls += 2;
            }
            //Применим шаблон
            Debug.i("Modify Tiles", "calculateIDS: numWalls = " + numWalls);
            if(numWalls == 0)
                modifyTileIDS(getCell(column, row - 1, GameMap.LAYER_WALLS), 10, column, row - 1, 0);
            else if(numWalls == 1)
                modifyTileIDS(getCell(column, row - 1, GameMap.LAYER_WALLS), 8, column, row - 1, 0);
            else if(numWalls == 2)
                modifyTileIDS(getCell(column, row - 1, GameMap.LAYER_WALLS), 9, column, row - 1, 0);
            else if(numWalls == 3)
                modifyTileIDS(getCell(column, row - 1, GameMap.LAYER_WALLS), 1, column, row - 1, 0);
        }

        //3ий тайл верхний правый: column+1; row -1
        if(Utils.typesWall.contains(getCell(column+1, row-1, GameMap.LAYER_WALLS)))
        {
            Debug.i("Modify Tiles", "calculateIDS: 3 tile with column = " + (column+1) + " row = " + (row-1));
            int numWalls = 0;//0 - нет тайлов вокруг(ничего не делаем), 1 - горизонтальный, 2 - вертикальный, 3 - угол(лево, вниз)
            //Проверим первый тайл
            if(Utils.typesWall.contains(getCell(column, row-1, GameMap.LAYER_WALLS)) || (Utils.typesFloor.contains(getCell(column, row-1, GameMap.LAYER_FLOOR)) && getDoorByCoord(column, row-1) != null))
            {
                numWalls += 1;
            }
            //Проверим второй тайл
            if(Utils.typesWall.contains(getCell(column+1, row, GameMap.LAYER_WALLS)) || (Utils.typesFloor.contains(getCell(column+1, row, GameMap.LAYER_FLOOR)) && getDoorByCoord(column+1, row) != null))
            {
                numWalls += 2;
            }
            //Применим шаблон
            Debug.i("Modify Tiles", "calculateIDS: numWalls = " + numWalls);
            if(numWalls == 3)
                modifyTileIDS(getCell(column+1, row-1, GameMap.LAYER_WALLS), 6, column+1, row-1, 1);
        }

        //4ый тайл средний левый: column-1; row
        if(Utils.typesWall.contains(getCell(column-1, row, GameMap.LAYER_WALLS)))
        {
            Debug.i("Modify Tiles", "calculateIDS: 4 tile with column = " + (column-1) + " row = " + (row));
            int numWalls = 0;//0 - нет тайлов вокруг(тупик вправо), 1 - тупик вниз, 2 - тупик вверх, 3 - вертикальный
            //Проверим первый тайл
            if(Utils.typesWall.contains(getCell(column-1, row-1, GameMap.LAYER_WALLS)) || (Utils.typesFloor.contains(getCell(column-1, row-1, GameMap.LAYER_FLOOR)) && getDoorByCoord(column-1, row-1) != null))
            {
                numWalls += 1;
            }
            //Проверим второй тайл
            if(Utils.typesWall.contains(getCell(column-1, row+1, GameMap.LAYER_WALLS)) || (Utils.typesFloor.contains(getCell(column-1, row+1, GameMap.LAYER_FLOOR)) && getDoorByCoord(column-1, row+1) != null))
            {
                numWalls += 2;
            }
            //Применим шаблон
            Debug.i("Modify Tiles", "calculateIDS: numWalls = " + numWalls);
            if(numWalls == 0)
                modifyTileIDS(getCell(column - 1, row, GameMap.LAYER_WALLS), 8, column - 1, row, 2);
            else if(numWalls == 1)
                modifyTileIDS(getCell(column - 1, row, GameMap.LAYER_WALLS), 10, column - 1, row, 2);
            else if(numWalls == 2)
                modifyTileIDS(getCell(column - 1, row, GameMap.LAYER_WALLS), 11, column - 1, row, 2);
            else if(numWalls == 3)
                modifyTileIDS(getCell(column - 1, row, GameMap.LAYER_WALLS), 2, column - 1, row, 2);
        }

        //5ый тайл средний правый: column-1; row
        if(Utils.typesWall.contains(getCell(column+1, row, GameMap.LAYER_WALLS)))
        {
            Debug.i("Modify Tiles", "calculateIDS: 5 tile with column = " + (column+1) + " row = " + (row));
            int numWalls = 0;//0 - нет тайлов вокруг(тупик влево), 1 - тупик вниз, 2 - тупик вверх, 3 - вертикальный
            //Проверим первый тайл
            if(Utils.typesWall.contains(getCell(column+1, row-1, GameMap.LAYER_WALLS)) || (Utils.typesFloor.contains(getCell(column+1, row-1, GameMap.LAYER_FLOOR)) && getDoorByCoord(column+1, row-1) != null))
            {
                numWalls += 1;
            }
            //Проверим второй тайл
            if(Utils.typesWall.contains(getCell(column+1, row+1, GameMap.LAYER_WALLS)) || (Utils.typesFloor.contains(getCell(column+1, row+1, GameMap.LAYER_FLOOR)) && getDoorByCoord(column+1, row+1) != null))
            {
                numWalls += 2;
            }
            //Применим шаблон
            Debug.i("Modify Tiles", "calculateIDS: numWalls = " + numWalls);
            if(numWalls == 0)
                modifyTileIDS(getCell(column + 1, row, GameMap.LAYER_WALLS), 9, column + 1, row, 3);
            else if(numWalls == 1)
                modifyTileIDS(getCell(column + 1, row, GameMap.LAYER_WALLS), 10, column + 1, row, 3);
            else if(numWalls == 2)
                modifyTileIDS(getCell(column + 1, row, GameMap.LAYER_WALLS), 11, column + 1, row, 3);
            else if(numWalls == 3)
                modifyTileIDS(getCell(column + 1, row, GameMap.LAYER_WALLS), 2, column + 1, row, 3);
        }

        //6ой тайл нижний левый: column-1; row+1
        if(Utils.typesWall.contains(getCell(column-1, row+1, GameMap.LAYER_WALLS)))
        {
            Debug.i("Modify Tiles", "calculateIDS: 6 tile with column = " + (column-1) + " row = " + (row+1));
            int numWalls = 0;//0 - нет тайлов вокруг(ничего не ставим), 1 - нет информации, ничего, 2 - нет информации, ничего, 3 - угол(верх, право)
            //Проверим первый тайл
            if(Utils.typesWall.contains(getCell(column-1, row, GameMap.LAYER_WALLS)) || (Utils.typesFloor.contains(getCell(column-1, row, GameMap.LAYER_FLOOR)) && getDoorByCoord(column-1, row) != null))
            {
                numWalls += 1;
            }
            //Проверим второй тайл
            if(Utils.typesWall.contains(getCell(column, row+1, GameMap.LAYER_WALLS)) || (Utils.typesFloor.contains(getCell(column, row+1, GameMap.LAYER_FLOOR)) && getDoorByCoord(column, row+1) != null))
            {
                numWalls += 2;
            }
            //Применим шаблон
            Debug.i("Modify Tiles", "calculateIDS: numWalls = " + numWalls);
            if(numWalls == 3)
                modifyTileIDS(getCell(column-1, row+1, GameMap.LAYER_WALLS), 4, column-1, row+1, 2);
        }

        //7ой тайл нижний средний: column; row+1
        if(Utils.typesWall.contains(getCell(column, row+1, GameMap.LAYER_WALLS)))
        {
            Debug.i("Modify Tiles", "calculateIDS: 7 tile with column = " + (column) + " row = " + (row+1));
            int numWalls = 0;//0 - нет тайлов вокруг(тупик вверх), 1 - тупик вправо, 2 - тупик влево, 3 - горизонтальный
            //Проверим первый тайл
            if(Utils.typesWall.contains(getCell(column-1, row+1, GameMap.LAYER_WALLS)) || (Utils.typesFloor.contains(getCell(column-1, row+1, GameMap.LAYER_FLOOR)) && getDoorByCoord(column-1, row+1) != null))
            {
                numWalls += 1;
            }
            //Проверим второй тайл
            if(Utils.typesWall.contains(getCell(column+1, row+1, GameMap.LAYER_WALLS)) || (Utils.typesFloor.contains(getCell(column+1, row+1, GameMap.LAYER_FLOOR)) && getDoorByCoord(column+1, row+1) != null))
            {
                numWalls += 2;
            }
            //Применим шаблон
            Debug.i("Modify Tiles", "calculateIDS: numWalls = " + numWalls);
            if(numWalls == 0)
                modifyTileIDS(getCell(column, row + 1, GameMap.LAYER_WALLS), 11, column, row + 1, 1);
            else if(numWalls == 1)
                modifyTileIDS(getCell(column, row + 1, GameMap.LAYER_WALLS), 8, column, row + 1, 1);
            else if(numWalls == 2)
                modifyTileIDS(getCell(column, row + 1, GameMap.LAYER_WALLS), 9, column, row + 1, 1);
            else if(numWalls == 3)
                modifyTileIDS(getCell(column, row + 1, GameMap.LAYER_WALLS), 1, column, row + 1, 1);
        }

        //8ой тайл нижний правый: column+1; row+1
        if(Utils.typesWall.contains(getCell(column+1, row+1, GameMap.LAYER_WALLS)))
        {
            Debug.i("Modify Tiles", "calculateIDS: 8 tile with column = " + (column+1) + " row = " + (row+1));
            int numWalls = 0;//0 - нет тайлов вокруг(ничего не ставим), 1 - нет информации, ничего, 2 - нет информации, ничего, 3 - угол(лево, вверх)
            //Проверим первый тайл
            if(Utils.typesWall.contains(getCell(column, row+1, GameMap.LAYER_WALLS)) || (Utils.typesFloor.contains(getCell(column, row+1, GameMap.LAYER_FLOOR)) && getDoorByCoord(column, row+1) != null))
            {
                numWalls += 1;
            }
            //Проверим второй тайл
            if(Utils.typesWall.contains(getCell(column+1, row, GameMap.LAYER_WALLS)) || (Utils.typesFloor.contains(getCell(column+1, row, GameMap.LAYER_FLOOR)) && getDoorByCoord(column+1, row) != null))
            {
                numWalls += 2;
            }
            //Применим шаблон
            Debug.i("Modify Tiles", "calculateIDS: numWalls = " + numWalls);
            if(numWalls == 3)
                modifyTileIDS(getCell(column+1, row+1, GameMap.LAYER_WALLS), 7, column+1, row+1, 3);
        }
	}

    /**
     * Испльзуя шаблоны, получаем настоящий идентификатор тайла. Передаем идентификаторы записанный в массиве тайлов карты и посчитанный функцией calculateIDS()
     * Работает только со слоем стен.
     * @param current - идентификатор тайла сохраненный на карте
     * @param calculated - идентификатор тайла посчитанный функцией calculateIDS()
     * @param column - х координата тайла
     * @param row - y координата тайла
     * @param quad - 0 - верх/верх-лево, 1- низ/верх-право, 2 - лево/низ-лево, 3 - право/низ-право
     * */
    private void modifyTileIDS(final int current, final int calculated, final int column, final int row, final int quad)
    {
        //Если не проверяли тайл ранее, то перепишем на посчитанный и отметим, как проверенный
        if(mCheckTile[column + row*wContext.mWorld.mLevels.get(currLevel).getTileColumns()] == 0)
        {
            setCell(column, row, calculated, GameMap.LAYER_WALLS);
            mCheckTile[column + row*wContext.mWorld.mLevels.get(currLevel).getTileColumns()] = 1;
        }
        else if(mCheckTile[column + row*wContext.mWorld.mLevels.get(currLevel).getTileColumns()] == 1)
        {
            //Если проверяли ранее, то смотрим на шаблоны
            Debug.i("Modify Tiles", "modifyTileIDS: mCheckTile["+(column + row*column)+"] = " + mCheckTile[column + row*column]);

            //Исключения
            if(calculated == current)   //Если идентификаторы равны, то и менять нечего
                return;

            //Изменения
            switch (current)
            {
                case 1:
                    if(calculated == 4 || calculated == 7)
                        setCell(column, row, 13, GameMap.LAYER_WALLS);
                    else if(calculated == 5 || calculated == 6)
                        setCell(column, row, 12, GameMap.LAYER_WALLS);
                    break;
                case 2:
                    if(calculated == 4 || calculated == 5)
                        setCell(column, row, 14, GameMap.LAYER_WALLS);
                    else if(calculated == 7 || calculated == 6)
                        setCell(column, row, 15, GameMap.LAYER_WALLS);
                    break;
                case 4:
                    if(calculated == 1 || calculated == 7)
                        setCell(column, row, 13, GameMap.LAYER_WALLS);
                    else if(calculated == 2 || calculated == 5)
                        setCell(column, row, 14, GameMap.LAYER_WALLS);
                    else if(calculated == 6)
                        setCell(column, row, 3, GameMap.LAYER_WALLS);
                    break;
                case 5:
                    if(calculated == 2 || calculated == 4)
                        setCell(column, row, 14, GameMap.LAYER_WALLS);
                    else if(calculated == 1 || calculated == 6)
                        setCell(column, row, 12, GameMap.LAYER_WALLS);
                    else if(calculated == 7)
                        setCell(column, row, 3, GameMap.LAYER_WALLS);
                    break;
                case 6:
                    if(calculated == 1 || calculated == 5)
                        setCell(column, row, 12, GameMap.LAYER_WALLS);
                    else if(calculated == 2 || calculated == 7)
                        setCell(column, row, 15, GameMap.LAYER_WALLS);
                    else if(calculated == 4)
                        setCell(column, row, 3, GameMap.LAYER_WALLS);
                    break;
                case 7:
                    if(calculated == 2 || calculated == 6)
                        setCell(column, row, 15, GameMap.LAYER_WALLS);
                    else if(calculated == 1 || calculated == 4)
                        setCell(column, row, 13, GameMap.LAYER_WALLS);
                    else if(calculated == 5)
                        setCell(column, row, 3, GameMap.LAYER_WALLS);
                    break;
                case 8:
                    if(calculated == 11)
                        setCell(column, row, 6, GameMap.LAYER_WALLS);
                    else if(calculated == 10)
                        setCell(column, row, 7, GameMap.LAYER_WALLS);
                    break;
                case 9:
                    if(calculated == 11)
                        setCell(column, row, 5, GameMap.LAYER_WALLS);
                    else if(calculated == 10)
                        setCell(column, row, 4, GameMap.LAYER_WALLS);
                    break;
                case 10:
                    if(calculated == 8)
                        setCell(column, row, 7, GameMap.LAYER_WALLS);
                    else if(calculated == 9)
                        setCell(column, row, 4, GameMap.LAYER_WALLS);
                    break;
                case 11:
                    if(calculated == 9)
                        setCell(column, row, 5, GameMap.LAYER_WALLS);
                    else if(calculated == 8)
                        setCell(column, row, 6, GameMap.LAYER_WALLS);
                    break;
                case 12:
                    if(calculated == 7 || calculated == 4 || calculated == 2 || calculated == 11)
                        setCell(column, row, 3, GameMap.LAYER_WALLS);
                    break;
                case 13:
                    if(calculated == 6 || calculated == 5 || calculated == 2 || calculated == 10)
                        setCell(column, row, 3, GameMap.LAYER_WALLS);
                    break;
                case 14:
                    if(calculated == 6 || calculated == 7 || calculated == 1 || calculated == 9)
                        setCell(column, row, 3, GameMap.LAYER_WALLS);
                    break;
                case 15:
                    if(calculated == 4 || calculated == 5 || calculated == 1 || calculated == 8)
                        setCell(column, row, 3, GameMap.LAYER_WALLS);
                    break;
            }
        }
    }
	
	/** Функция проверяет, что находится перед коридором и говорит можно ли дальше его строить
	 * @param x - Координата х
	 * @param y - Координата у
	 * @param dir - Направление построения
	 * @return 0 - Можно продолжить строительство, 1 - нельзя и нужна стена, 2 - нельзя и нужна дверь, 3 - стоп строительство без стен и дверей
	 * */
	private int isCanGo(int x, int y, int dir)
	{
		int kx, ky;
		if(dir == ObjectOnMap.DIR_NORTH || dir == ObjectOnMap.DIR_SOUTH)
		{
			if(dir == ObjectOnMap.DIR_NORTH)
				ky = -1;
			else
				ky = 1;
			if(getCell(x+1, y, GameMap.LAYER_WALLS) == TILE_NONE)//Если по центру коридора нет ничего, то продолжаем строить его
				return 0;
			else if(Utils.typesWall.contains(getCell(x, y, GameMap.LAYER_WALLS)) && Utils.typesWall.contains(getCell(x+1, y, GameMap.LAYER_WALLS)) && Utils.typesWall.contains(getCell(x+2, y, GameMap.LAYER_WALLS)))//Если наткнулись на стену по всей ширине коридора
			{
				Debug.i(TAG, "-------------==============------------");
				Debug.i(TAG, "isCanGo: x = " + x + ", y = " + y);
				Debug.i(TAG, "isCanGo: -1: " + getCell(x, y-ky, GameMap.LAYER_WALLS) + " " + getCell(x+1, y-ky, GameMap.LAYER_WALLS) + " " + getCell(x+2, y-ky, GameMap.LAYER_WALLS));
				Debug.i(TAG, "isCanGo:  0: " + getCell(x, y, GameMap.LAYER_WALLS) + " " + getCell(x+1, y, GameMap.LAYER_WALLS) + " " + getCell(x+2, y, GameMap.LAYER_WALLS));
				Debug.i(TAG, "isCanGo:  1: " + getCell(x, y+ky, GameMap.LAYER_WALLS) + " " + getCell(x+1, y+ky, GameMap.LAYER_WALLS) + " " + getCell(x+2, y+ky, GameMap.LAYER_WALLS));
				Debug.i(TAG, "-------------==============------------");
				//Если следующий ряд свободен от стен и от дверей, то можем поставить на стену дверь
				if(Utils.typesFloor.contains(getCell(x+1, y+ky, GameMap.LAYER_FLOOR)) && getDoorByCoord(x+1, y+ky) == null && getCell(x+1, y+ky, GameMap.LAYER_WALLS) == TILE_NONE)
					return 2;
				else if(getCell(x+1, y+ky, GameMap.LAYER_WALLS) == TILE_NONE)//Если строим поворот, то начнем со стены, потому тут продолжим строительство
					return 0;
                else if(Utils.typesWall.contains(getCell(x+1, y+ky, GameMap.LAYER_WALLS)))//Если по всей ширине стена и посреди на следующем шаге стена, то заканчиваем строительство коридора
                {
                    //Если побокам коридора есть дверь в конце его создания, то удалим такие
                    LevelDoor ld = getDoorByCoord(x, y);
                    if(ld != null)
                        deleteDoor(ld);

                    ld = getDoorByCoord(x+2, y);
                    if(ld != null)
                        deleteDoor(ld);
                    return 1;
                }
				//else if(Utils.typesWall.contains(getCell(x, y+1*ky, GameMap.LAYER_WALLS)) && Utils.typesWall.contains(getCell(x+1, y+1*ky, GameMap.LAYER_WALLS)) && Utils.typesWall.contains(getCell(x+2, y+1*ky, GameMap.LAYER_WALLS)))
					//return 0;
			}
            //Если перед коридором стоит стена и посреди дверь, то остановим просто строительство коридора
            else if(Utils.typesWall.contains(getCell(x, y, GameMap.LAYER_WALLS)) && getDoorByCoord(x+1, y) != null && Utils.typesWall.contains(getCell(x+2, y, GameMap.LAYER_WALLS)))
            {
                Debug.i("Stop. Wall with door in the center.");
                return 3;
            }
			else
            {
                //Если побокам коридора есть дверь в конце его создания, то удалим такие
                LevelDoor ld = getDoorByCoord(x, y);
                if(ld != null)
                    deleteDoor(ld);

                ld = getDoorByCoord(x+2, y);
                if(ld != null)
                    deleteDoor(ld);
				return 1;
            }
		}
		else if(dir == ObjectOnMap.DIR_WEST || dir == ObjectOnMap.DIR_EAST)
		{
			if(dir == ObjectOnMap.DIR_WEST)
				kx = -1;
			else
				kx = 1;
			if(getCell(x, y+1, GameMap.LAYER_WALLS) == TILE_NONE)
				return 0;
			else if(Utils.typesWall.contains(getCell(x, y, GameMap.LAYER_WALLS)) && Utils.typesWall.contains(getCell(x, y+1, GameMap.LAYER_WALLS)) && Utils.typesWall.contains(getCell(x, y+2, GameMap.LAYER_WALLS)))
			{
				Debug.i(TAG, "-------------==============------------");
				Debug.i(TAG, "isCanGo: x = " + x + ", y = " + y);
				Debug.i(TAG, "isCanGo: -1: " + getCell(x-kx, y, GameMap.LAYER_WALLS) + " " + getCell(x-kx, y+1, GameMap.LAYER_WALLS) + " " + getCell(x-kx, y+2, GameMap.LAYER_WALLS));
				Debug.i(TAG, "isCanGo:  0: " + getCell(x, y, GameMap.LAYER_WALLS) + " " + getCell(x, y+1, GameMap.LAYER_WALLS) + " " + getCell(x, y+2, GameMap.LAYER_WALLS));
				Debug.i(TAG, "isCanGo:  1: " + getCell(x+kx, y, GameMap.LAYER_WALLS) + " " + getCell(x+kx, y+2, GameMap.LAYER_WALLS) + " " + getCell(x+kx, y+2, GameMap.LAYER_WALLS));
				Debug.i(TAG, "-------------==============------------");
				if(Utils.typesFloor.contains(getCell(x+kx, y+1, GameMap.LAYER_FLOOR)) && getDoorByCoord(x+kx, y+1) == null)
					return 2;
				else if(getCell(x+kx, y+1, GameMap.LAYER_WALLS) == TILE_NONE)
					return 0;
                else if(Utils.typesWall.contains(getCell(x+kx, y+1, GameMap.LAYER_WALLS)))
                {
                    //Если побокам коридора есть дверь в конце его создания, то удалим такие
                    LevelDoor ld = getDoorByCoord(x, y);
                    if(ld != null)
                        deleteDoor(ld);

                    ld = getDoorByCoord(x, y+2);
                    if(ld != null)
                        deleteDoor(ld);
                    return 1;
                }
				//else if(Utils.typesWall.contains(getCell(x+1*kx, y, GameMap.LAYER_WALLS)) && Utils.typesWall.contains(getCell(x+1*kx, y+1, GameMap.LAYER_WALLS)) && Utils.typesWall.contains(getCell(x+1*kx, y+2, GameMap.LAYER_WALLS)))
					//return 0;
			}
            //Если перед коридором стоит стена и посреди дверь, то остановим просто строительство коридора
            else if(Utils.typesWall.contains(getCell(x, y, GameMap.LAYER_WALLS)) && getDoorByCoord(x, y+1) != null && Utils.typesWall.contains(getCell(x, y+2, GameMap.LAYER_WALLS)))
            {
                Debug.i("Stop. Wall with door in the center.");
                return 3;
            }
			else
            {
                //Если побокам коридора есть дверь в конце его создания, то удалим такие
                LevelDoor ld = getDoorByCoord(x, y);
                if(ld != null)
                    deleteDoor(ld);

                ld = getDoorByCoord(x, y+2);
                if(ld != null)
                    deleteDoor(ld);
				return 1;
            }
		}
			
		return 1;
	}
	
	/**Функция проверяет наличие свободных комнат и возвращает первую свободную из списка
	 * */
	private LevelDoor getFreeDoor()
	{
		Debug.i(TAG, "Try to get free door");
		if(mLevelDoors.size() == 0)
			return null;
		
		for(LevelDoor ld : mLevelDoors)
		{
			if(ld.isFree)
			{
				Debug.i(TAG, "Get door by coords: (" + ld.mCoord.getX() + ", " + ld.mCoord.getY() + ")");
				return ld;			//Вернем свободную комнату
			}
			else 
			{
				Debug.e(TAG, "Door by coords: (" + ld.mCoord.getX() + ", " + ld.mCoord.getY() + ") is not free!");
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

    /**
     * Удаляем существующую дверь из списка
     * @param d - удаляемая дверь
     * */
    private void deleteDoor(LevelDoor d)
    {
        if(d != null)
            mLevelDoors.remove(d);
    }
	
	private void clearDoors()
	{
		ArrayList<LevelDoor> buf = new ArrayList<LevelDoor>();
        //Создадим вспомогательный список
		for(LevelDoor door : mLevelDoors)
		{
			buf.add(door);
		}

        //Пройдемся по вспомогательному списку и удалим все свободные двери
		for(LevelDoor ld : buf)
		{
			if(ld.isFree)
			{
				mLevelDoors.remove(ld);
			}
		}
	}
	
	public boolean generateNewLevel()
	{
        Debug.i("generateNewLevel()");
        ArrayList<TMXTiledMap> mGameLevels;

        wContext.mWorld.mLevels = new ArrayList<TMXTiledMap>();           //Временное решение создания списка уровней
        mGameLevels = wContext.mWorld.mLevels;
		
		//for(int i = 0; i < wContext.world.getCountLevels(); i++)							//Создадим карты для всех уровней разом
		for(int i = 0; i < 1; i++)															//Создадим карты для всех уровней разом
		{
			//numObjects = Utils.getRand(wContext.world.MINROOMS, wContext.world.MAXROOMS);	//Определимся с максимальным количеством объектов на уровне
			numObjects = 9;
			chanceRoom = Utils.getRand(Utils.MINCHANCEROOM, Utils.MAXCHANCEROOM);			//Шанс выпадения комнаты
			//chanceCorridor = 100 - chanceRoom;											//Шанс выпадение коридора
			//int width_level = Utils.getRand(World.MINLEVELWIDTH, World.MAXLEVELWIDTH);	//Ширина уровня(х)
			int width_level = 50;															//Ширина уровня(х)
			//int height_level = Utils.getRand(World.MINLEVELHEIGHT, World.MAXLEVELHEIGHT);	//Высота уровня(y)
			int height_level = 60;															//Высота уровня(y)

            //int tries = 0;					//Количество попыток поставить объект на карте =)
			//boolean isFreePlace = true;		//Если еще свободное место на карте(можно ли еще воткнуть туда хоть что-то из комнат)
			//boolean isFreeObjects = true;		//Если нет свободных объектов, то завершим формирование уровня
			int countObjects = 0;				//Текущее количество объектов на карте
			
			TMXTiledMap newLevel = new TMXTiledMap(height_level, width_level, 32, 32);						//Создадим уровень
            TMXLayer floor = new TMXLayer(newLevel, width_level, height_level, "floor", ResourceManager.getInstance().mVBO);
            newLevel.getTMXLayers().add(floor);
            TMXLayer wall = new TMXLayer(newLevel, width_level, height_level, "wall", ResourceManager.getInstance().mVBO);
            newLevel.getTMXLayers().add(wall);
            TMXTileSet set = new TMXTileSet(1, "walls_stone", 32, 32, 2, 1, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
            set.setImageSource(wContext.getAssetManager(), wContext.getTextureManager(), "tileset/walls_stone.png", null);
            newLevel.getTMXTileSets().add(set);
			//wContext.world.addLevel(newLevel);
            mGameLevels.add(newLevel);
			
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
                    Debug.i(TAG, "-----------=================Create new first Room=======================------------------");
					setRoom(mLD);
                    Debug.i(TAG, "-----------=================First Room created=======================------------------");
				}
				else if(mLD != null)
				{
					//Если есть дверь, то вычислим, что дальше ставить на уровень
					int chance = Utils.getRand(0, 100);		//Посчитаем шанс выпадения
					if(chance <= chanceRoom)
					{
						//Выпала комната, поставим ее
                        Debug.i(TAG, "-----------=================Create new Room, objectsMap.size() = " + objectsMap.size() + "=======================------------------");
						setRoom(mLD);
                        Debug.i(TAG, "-----------=================New Room created=======================------------------");
					}
					else if(chance > chanceRoom && chance <= 100)
					{
						//Выпал коридор, поставим его
                        Debug.i(TAG, "-----------=================Create new Corridor, objectsMap.size() = " + objectsMap.size() + "=======================------------------");
						setCorridor(mLD);
                        Debug.i(TAG, "-----------=================New Corridor created=======================------------------");
					}
				}
				
				countObjects = objectsMap.size();
				if(countObjects >= numObjects)
					break;
			}
			//currLevel = wContext.world.getLevels().size();
		}
		
		//Удалим все двери, что свободны
        Debug.i("Delete all free doors");
		clearDoors();
        Debug.i("Calculate ids of tiles in level");
        correctWallIDs();
		Debug.i(TAG, "Number of generated objects = " + objectsMap.size());
		
		return true;
	}
	
	/**
	 * Пометки дверей для генератора, на основе их будут создаваться реальные двери
     * */
	private class LevelDoor
	{
		public Coord mCoord;        //место на уровне, где находится дверь
		public boolean isFree;      //ведет ли дверь вникуда или за ней есть коридор или комната
		public int mDir;
	}
}
