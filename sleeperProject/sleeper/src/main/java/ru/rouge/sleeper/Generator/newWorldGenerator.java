package ru.rouge.sleeper.Generator;

import java.util.ArrayList;

import ru.rouge.sleeper.Managers.ResourceManager;
import ru.rouge.sleeper.Map.GameMap;
import ru.rouge.sleeper.Utils.Coord;
import ru.rouge.sleeper.Utils.Directions;
import ru.rouge.sleeper.Utils.Size;
import ru.rouge.sleeper.Utils.Utils;
import ru.rouge.sleeper.WorldContext;

/**
 * Created by Evgenij Savchik on 04.12.13.
 * Генератор, работающий по новому принципу
 */
public class newWorldGenerator
{
    //-----------------------------
    //CONSTANTS
    //-----------------------------

    private static final int THEME_SETTLES_PEOPLE   = 0;
    private static final int THEME_OLD_SETTLES      = 1;
    private static final int THEME_SETTLES_PLANTS   = 2;
    private static final int THEME_ORC_SETTLES      = 3;
    private static final int THEME_MAGIC_SETTLES    = 4;
    private static final int THEME_DWARF_SETTLES    = 5;
    private static final int THEME_DROW_SETTLES     = 6;

    //-----------------------------
    //VARIABLES
    //-----------------------------

    //Основные менеджеры игры
    private final WorldContext mContext;
    private final ResourceManager mResManager;
    //end

    private ArrayList<DoorTemplate> mDoorsTempl;    //Список шаблонов дверей, на место которых будут ставится настоящие двери
    private ArrayList<ObjectOnMap> mObjects;        //Список комнат установленных
    private int mThemeOfDungeon;                    //Тематика уровня
    private int mCurrLevel;                         //Текущий генерируемый уровень

    private int[] mThemes = new int[] {             //Список тем для генерации
            THEME_SETTLES_PEOPLE,
            THEME_OLD_SETTLES,
            THEME_SETTLES_PLANTS,
            THEME_ORC_SETTLES,
            THEME_MAGIC_SETTLES,
            THEME_DWARF_SETTLES,
            THEME_DROW_SETTLES
    };

    //-----------------------------
    //CONSTRUCTOR
    //-----------------------------

    public newWorldGenerator()
    {
        mContext = WorldContext.getInstance();
        mResManager = ResourceManager.getInstance();
        mDoorsTempl = new ArrayList<DoorTemplate>();
        mObjects = new ArrayList<ObjectOnMap>();
    }

    //-----------------------------
    //CLASS METHODS
    //-----------------------------

    public void generateNewLevel()
    {
        generate();
    }

    private void generate()
    {
        mObjects.clear();
        mDoorsTempl.clear();

        mCurrLevel = mContext.mWorld.mCurrentLevel;
        mThemeOfDungeon = Utils.getRand(0, mThemes.length);
        int width_level = Utils.getRand(GameMap.MINLEVELWIDTH, GameMap.MAXLEVELWIDTH);	//Ширина уровня(х)
        int height_level = Utils.getRand(GameMap.MINLEVELHEIGHT, GameMap.MAXLEVELHEIGHT);	//Высота уровня(y)

        //TODO Выбрать тайлсет, загрузить нужные комнаты, создать карту уровня
    }

    //-----------------------------
    //GETTERS/SETTERS
    //-----------------------------

    //-----------------------------
    //INNER CLASSES
    //-----------------------------

    private final class ObjectOnMap
    {
        public Coord mCoord;            //Строка и столбец начала комнаты
        public Size mSize;              //Размер комнаты в тайлах
        public boolean isConnected;     //Добавлена ли комната в список проходимых
        public boolean isStair;         //Установлена ли лестница
        public int mType;               //Тип комнаты (квестовая, уникальная, общая)
    }

    private final class DoorTemplate
    {
        public Coord mCoord;
        public Directions mDir;
        public boolean isFree;
    }
}
