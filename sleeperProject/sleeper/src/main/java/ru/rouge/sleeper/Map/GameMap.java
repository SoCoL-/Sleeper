package ru.rouge.sleeper.Map;

import org.andengine.entity.modifier.PathModifier;
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

	private final String OBJECT_NAME_PORTAL = "portal";
	private final String OBJECT_NAME_PLAYERSPAWN = "player_spawn";

	private final String LAYER_WAKABLE = "wakable";

	//-----------------------------
	//VARIABLES
	//-----------------------------

	public TMXTiledMap mTMXMap;
	public ArrayList<TMXTile> mWakables;
	public ArrayList<TMXObject> mPortals;
	public ArrayList<TMXObject> mSpawns;

	//-----------------------------
	//CONSTRUCTORS
	//-----------------------------

	public GameMap(final TMXLoader loader)
	{
		try
		{
			mTMXMap = loader.loadFromAsset("tmx/map_test.tmx");
			if(mTMXMap == null)
				Debug.e("not load map with name = " + "map_test.tmx");
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
		ArrayList<TMXObject> mMapObjects = new ArrayList<TMXObject>();
		for(TMXObjectGroup objects : mTMXMap.getTMXObjectGroups())
		{
			mMapObjects.addAll(objects.getTMXObjects());
		}

		this.mSpawns = getObjectsTile(OBJECT_NAME_PLAYERSPAWN, mMapObjects);
		this.mPortals = getObjectsTile(OBJECT_NAME_PORTAL, mMapObjects);

		///TODO setup character
		TMXObject playerSpawn = mSpawns.get(0);
		Debug.e("playerSpawn.getX() = " + playerSpawn.getX());
		Debug.e("playerSpawn.getY() = " + playerSpawn.getY());
		try
		{
			WorldContext.getInstance().mPlayer = new Player(playerSpawn.getX(), playerSpawn.getY(), ResourceManager.getInstance().mHeroTexture, ResourceManager.getInstance().mVBO);
			//WorldContext.getInstance().mPlayer.setPath(new PathModifier.Path(2).to(290,290).to(290,290));

			Debug.e("PlayerController");
			//WorldContext.getInstance().mPlayerContr.setWorld(this);
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
