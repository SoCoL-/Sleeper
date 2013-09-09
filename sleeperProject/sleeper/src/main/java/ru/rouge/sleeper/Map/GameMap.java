package ru.rouge.sleeper.Map;

import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXObject;
import org.andengine.extension.tmx.TMXObjectGroup;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.util.debug.Debug;

import java.util.ArrayList;

import ru.rouge.sleeper.Managers.ResourceManager;
import ru.rouge.sleeper.Objects.Door;
import ru.rouge.sleeper.Objects.Player;
import ru.rouge.sleeper.WorldContext;

/**
 * Evgenij Savchik
 * Created by 1 on 05.07.13.
 */
public final class GameMap
{
	//-----------------------------
	//CONSTANTS
	//-----------------------------

	public final static int LAYER_FLOOR = 0;
	public final static int LAYER_WALLS = 1;
    public final static int LAYER_ABOVE = 2;

	private final String OBJECT_NAME_PORTAL = "portal";
	private final String OBJECT_NAME_PLAYERSPAWN = "player_spawn";
    private final String OBJECT_NAME_DOOR = "door_wood";

	//-----------------------------
	//VARIABLES
	//-----------------------------

	public TMXTiledMap mTMXMap;                 //Графическая часть карты
	//public boolean[][] mWakables;           //Физическая часть карты (ссылки на массив объектов + проходимость обычная)
    public PhysicMapCell[][] mWakables;         //Физическая часть карты (ссылки на массив объектов + проходимость обычная)
	public ArrayList<TMXObject> mPortals;
	public ArrayList<TMXObject> mSpawns;
    public ArrayList<Door> mDoors;              //Пока что список дверей, надо будет переделать в список объектов статических

	//-----------------------------
	//CONSTRUCTORS
	//-----------------------------

	public GameMap(final TMXLoader loader)
	{
		try
		{
			mTMXMap = loader.loadFromAsset("tmx/map_test3.tmx");
			if(mTMXMap == null)
				Debug.e("not load map with name = " + "map_test.tmx");

			Debug.e("mTMXMap.getTileColumns() = " + mTMXMap.getTileColumns());
			Debug.e("mTMXMap.getTileRows() = " + mTMXMap.getTileRows());

			//mWakables = new boolean[mTMXMap.getTileColumns()][mTMXMap.getTileRows()];
            mWakables = new PhysicMapCell[mTMXMap.getTileColumns()][mTMXMap.getTileRows()];
			for(int i = 0; i < mTMXMap.getTileColumns(); i++)
				for(int j = 0; j < mTMXMap.getTileRows(); j++)
                {
                    mWakables[i][j] = new PhysicMapCell();
					mWakables[i][j].isWalkable = false;
                    mWakables[i][j].mIndexObject = -1;
                }
            Debug.e("Init walkables done! ");

            mDoors = new ArrayList<Door>();

			prepareMap();
		}
		catch (TMXLoadException e)
		{
			Debug.e(e);
		}
		catch (Exception ex)
		{
			Debug.e(ex.toString());
		}
	}

	//-----------------------------
	//CLASS METHODS
	//-----------------------------

	private void prepareMap()
	{
		Debug.e("prepareMap()");

		//Отключам отрисовку тайлов вне камеры
		for(int i = 0; i < mTMXMap.getTMXLayers().size(); i++)
		{
			mTMXMap.getTMXLayers().get(i).setCullingEnabled(true);
		}

        //Тест создания уровня вручную
        //createTestRoom(mTMXMap);

		//Инициализация карты проходимости
		Debug.e("walkable init");
		TMXLayer floor = mTMXMap.getTMXLayers().get(LAYER_FLOOR);
		for(int i = 0; i < floor.getTileColumns(); i++)
		{
			for(int j = 0; j < floor.getTileRows(); j++)
			{
				if(floor.getTMXTile(i, j) != null && floor.getTMXTile(i, j).getGlobalTileID() == 16)
				{
					//Debug.e("i = " + i);
					//Debug.e("j = " + j);
					//Debug.e("floor.getTMXTileAT(i, j) = " + floor.getTMXTile(i, j).getGlobalTileID());
					mWakables[j][i].isWalkable = true;
				}
			}
		}
		Debug.e("Walkable map is done!");
        Debug.e("mWakables[6][10] = " + mWakables[6][10].isWalkable);
        Debug.e("mWakables[6][9] = " + mWakables[6][9].isWalkable);
        Debug.e("mWakables[6][10] = " + mWakables[10][6].isWalkable);
        Debug.e("mWakables[6][9] = " + mWakables[9][6].isWalkable);

		ArrayList<TMXObject> mMapObjects = new ArrayList<TMXObject>();
		for(TMXObjectGroup objects : mTMXMap.getTMXObjectGroups())
		{
			mMapObjects.addAll(objects.getTMXObjects());
		}

		this.mSpawns = getObjectsTile(OBJECT_NAME_PLAYERSPAWN, mMapObjects);
		this.mPortals = getObjectsTile(OBJECT_NAME_PORTAL, mMapObjects);
        ArrayList<TMXObject> objDoors = getObjectsTile(OBJECT_NAME_DOOR, mMapObjects);
        Debug.e("objDoors.size() = " + objDoors.size());

        setDoors(objDoors);

		///setup Player
		TMXObject playerSpawn = mSpawns.get(0);
		Debug.e("playerSpawn.getX() = " + playerSpawn.getX());
		Debug.e("playerSpawn.getY() = " + playerSpawn.getY());
		try
		{
			WorldContext.getInstance().mPlayer = new Player(playerSpawn.getX(), playerSpawn.getY(), ResourceManager.getInstance().mHeroTexture, ResourceManager.getInstance().mVBO);
            WorldContext.getInstance().getCamera().setChaseEntity(WorldContext.getInstance().mPlayer);
            WorldContext.getInstance().mPlayerContr.setPlayer(WorldContext.getInstance().mPlayer);
		}
		catch(Exception e)
		{
			Debug.e(e);
		}
	}

	private ArrayList<TMXObject> getObjectsTile(final String name, ArrayList<TMXObject> mapObjects)
	{
		Debug.e("getObjectsTile()");
		ArrayList<TMXObject> rezult = new ArrayList<TMXObject>();
		for(TMXObject object : mapObjects)
		{
			if(object.getName().equals(name))
				rezult.add(object);
		}

		return rezult;
	}

    private void setDoors(ArrayList<TMXObject> doors)
    {
        for(TMXObject o : doors)
        {
            Debug.i("object.height = " + o.getHeight() + " object.width = " + o.getWidth());
            Debug.i("object.x = " + o.getX() + " object.y = " + o.getY());

            Debug.i("mTMXMap.getTileColumns() = " + mTMXMap.getTileColumns() + " mTMXMap.getTileRows() = " + mTMXMap.getTileRows());

            Door newDoor = new Door(o.getX(), o.getY(), true, ResourceManager.getInstance().mDoorsTexture, ResourceManager.getInstance().mVBO);
            mDoors.add(newDoor);

            int tileColumn = o.getX()/o.getWidth();
            int tileRow = o.getY()/o.getHeight();
            Debug.i("mDoors.size() = " + mDoors.size());
            mWakables[tileRow][tileColumn].mIndexObject = mDoors.size()-1;
            //mTMXMap.getTMXLayers().get(LAYER_WALLS).addTileByGlobalTileID(tileColumn, tileRow+1, 14, null);
            //mTMXMap.getTMXLayers().get(LAYER_WALLS).addTileByGlobalTileID(tileColumn, tileRow-1, 14, null);
        }
    }

    private void createTestRoom(TMXTiledMap map)
    {
        for(int i = 30; i < 46; i++)//y
        {
            for(int j = 1; j < 15; j++)
            {
                map.getTMXLayers().get(LAYER_FLOOR).addTileByGlobalTileID(i, j, 16, null);
            }
        }

        map.getTMXLayers().get(LAYER_FLOOR).addTileByGlobalTileID(26, 16, 16, null);
        map.getTMXLayers().get(LAYER_WALLS).addTileByGlobalTileID(26, 16, 0, null);
        //map.getTMXLayers().get(LAYER_ABOVE).addTileByGlobalTileID(26, 16, 17, null);
        map.getTMXLayers().get(LAYER_FLOOR).addTileByGlobalTileID(27, 16, 16, null);
        map.getTMXLayers().get(LAYER_FLOOR).addTileByGlobalTileID(27, 15, 16, null);
        map.getTMXLayers().get(LAYER_FLOOR).addTileByGlobalTileID(27, 14, 16, null);
        map.getTMXLayers().get(LAYER_FLOOR).addTileByGlobalTileID(28, 14, 16, null);
        map.getTMXLayers().get(LAYER_FLOOR).addTileByGlobalTileID(29, 14, 16, null);
    }

	//-----------------------------
	//GETTERS/SETTERS
	//-----------------------------

	//-----------------------------
	//INNER CLASSES
	//-----------------------------
}
