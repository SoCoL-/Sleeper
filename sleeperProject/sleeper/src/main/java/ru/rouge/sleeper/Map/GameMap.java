package ru.rouge.sleeper.Map;

import org.andengine.extension.tmx.TMXObject;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.util.debug.Debug;

import java.util.ArrayList;

import ru.rouge.sleeper.Objects.BaseObject;
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

    public final int MAX_LEVELS = 5;     //Максимальное количество уровней в игре

	public final static int LAYER_FLOOR = 0;
	public final static int LAYER_WALLS = 1;
    public final static int LAYER_ABOVE = 2;

	//-----------------------------
	//VARIABLES
	//-----------------------------

    public ArrayList<TMXTiledMap> mLevels;              //Уровни подземелья
    public ArrayList<PhysicMapCell[][]> mWakables;      //Физическая часть карты (ссылки на массив объектов + проходимость обычная)
	public ArrayList<ArrayList<TMXObject>> mSpawns;
    public ArrayList<ArrayList<BaseObject>> mObjects;   //Список интерактивных объектов
    public int mCurrentLevel;                           //Текущий уровень подземелья

	//-----------------------------
	//CONSTRUCTORS
	//-----------------------------

	public GameMap()
	{
        Debug.i("Create GameMap");
        if(WorldContext.getInstance().isNewGame)
            mCurrentLevel = 0;

        this.mLevels = new ArrayList<TMXTiledMap>(MAX_LEVELS);
        this.mWakables = new ArrayList<PhysicMapCell[][]>(MAX_LEVELS);
        this.mObjects = new ArrayList<ArrayList<BaseObject>>(MAX_LEVELS);
        this.mSpawns = new ArrayList<ArrayList<TMXObject>>(MAX_LEVELS);

        Debug.i("GameMap is ready!");
		/*try
		{
            Debug.i("GameMap : Create mWakables");
            mWakables = new PhysicMapCell[50][60];
			for(int i = 0; i < 50; i++)
				for(int j = 0; j < 60; j++)
                {
                    mWakables[i][j] = new PhysicMapCell();
					mWakables[i][j].isWalkable = false;
                    mWakables[i][j].mIndexObject = -1;
                }
            Debug.e("Init walkables done! ");

            mObjects = new ArrayList<BaseObject>();
            mSpawns = new ArrayList<TMXObject>();
		}
		catch (Exception ex)
		{
			Debug.e(ex.toString());
		}*/
	}

	//-----------------------------
	//CLASS METHODS
	//-----------------------------

	//-----------------------------
	//GETTERS/SETTERS
	//-----------------------------

	//-----------------------------
	//INNER CLASSES
	//-----------------------------
}
