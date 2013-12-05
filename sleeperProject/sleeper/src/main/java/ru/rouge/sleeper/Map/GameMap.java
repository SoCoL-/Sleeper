package ru.rouge.sleeper.Map;

import org.andengine.extension.tmx.TMXObject;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.util.debug.Debug;

import java.util.ArrayList;

import ru.rouge.sleeper.Objects.BaseObject;
import ru.rouge.sleeper.Objects.UI.GameHUD;
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
    public static final int MINLEVELWIDTH = 40;    //Минимальная ширина уровня
    public static final int MAXLEVELWIDTH = 80;    //Максимальная ширина уровня
    public static final int MINLEVELHEIGHT = 40;   //Максимальная высота уровня
    public static final int MAXLEVELHEIGHT = 80;   //Максимальная высота уровня

	public final static int LAYER_FLOOR = 0;
	public final static int LAYER_WALLS = 1;
    public final static int LAYER_ABOVE = 2;

	//-----------------------------
	//VARIABLES
	//-----------------------------

    public GameHUD mHUD;                                //Вспомогательный интерфейс игры
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

        this.mHUD = new GameHUD();
        this.mLevels = new ArrayList<TMXTiledMap>(MAX_LEVELS);
        this.mWakables = new ArrayList<PhysicMapCell[][]>(MAX_LEVELS);
        this.mObjects = new ArrayList<ArrayList<BaseObject>>(MAX_LEVELS);
        this.mSpawns = new ArrayList<ArrayList<TMXObject>>(MAX_LEVELS);

        Debug.i("GameMap is ready!");
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
