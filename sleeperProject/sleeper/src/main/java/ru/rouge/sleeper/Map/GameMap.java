package ru.rouge.sleeper.Map;

import org.andengine.entity.modifier.PathModifier;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXObject;
import org.andengine.extension.tmx.TMXObjectGroup;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.util.debug.Debug;

import java.util.ArrayList;

import ru.rouge.sleeper.Managers.ResourceManager;
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

	private final String OBJECT_NAME_PORTAL = "portal";
	private final String OBJECT_NAME_PLAYERSPAWN = "player_spawn";

	private final String LAYER_WAKABLE = "wakable";//???

	//-----------------------------
	//VARIABLES
	//-----------------------------

	public TMXTiledMap mTMXMap;
	public boolean[][] mWakables;
	public ArrayList<TMXObject> mPortals;
	public ArrayList<TMXObject> mSpawns;

	//-----------------------------
	//CONSTRUCTORS
	//-----------------------------

	public GameMap(final TMXLoader loader)
	{
		try
		{
			mTMXMap = loader.loadFromAsset("tmx/map_test2.tmx");
			if(mTMXMap == null)
				Debug.e("not load map with name = " + "map_test.tmx");

			Debug.e("mTMXMap.getTileColumns() = " + mTMXMap.getTileColumns());
			Debug.e("mTMXMap.getTileRows() = " + mTMXMap.getTileRows());

			mWakables = new boolean[mTMXMap.getTileColumns()][mTMXMap.getTileRows()];
			for(int i = 0; i < mTMXMap.getTileColumns(); i++)
				for(int j = 0; j < mTMXMap.getTileRows(); j++)
					mWakables[i][j] = false;
            Debug.e("Init walkables done! ");

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

		//Тестовое добавление тайла в 1 строку и 1 столбец с индексом 18 и без свойств
		mTMXMap.getTMXLayers().get(LAYER_FLOOR).addTileByGlobalTileID(1, 1, 18, null);

		//Инициализация карты проходимости
		Debug.e("walkable init");
		TMXLayer floor = mTMXMap.getTMXLayers().get(LAYER_FLOOR);
		for(int i = 0; i < floor.getTileColumns(); i++)
		{
			for(int j = 0; j < floor.getTileRows(); j++)
			{
				if(floor.getTMXTile(i, j) != null && floor.getTMXTile(i, j).getGlobalTileID() == 18)
				{
					Debug.e("i = " + i);
					Debug.e("j = " + j);
					Debug.e("floor.getTMXTileAT(i, j) = " + floor.getTMXTile(i, j).getGlobalTileID());
					mWakables[j][i] = true;
				}
			}
		}
		Debug.e("Walkable map is done!");
        Debug.e("mWakables[6][10] = " + mWakables[6][10]);
        Debug.e("mWakables[6][9] = " + mWakables[6][9]);
        Debug.e("mWakables[6][10] = " + mWakables[10][6]);
        Debug.e("mWakables[6][9] = " + mWakables[9][6]);

		ArrayList<TMXObject> mMapObjects = new ArrayList<TMXObject>();
		for(TMXObjectGroup objects : mTMXMap.getTMXObjectGroups())
		{
			mMapObjects.addAll(objects.getTMXObjects());
		}

		this.mSpawns = getObjectsTile(OBJECT_NAME_PLAYERSPAWN, mMapObjects);
		this.mPortals = getObjectsTile(OBJECT_NAME_PORTAL, mMapObjects);

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

	//-----------------------------
	//GETTERS/SETTERS
	//-----------------------------

	//-----------------------------
	//INNER CLASSES
	//-----------------------------
}
