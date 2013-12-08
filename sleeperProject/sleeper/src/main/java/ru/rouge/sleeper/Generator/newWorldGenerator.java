package ru.rouge.sleeper.Generator;

import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXObject;
import org.andengine.extension.tmx.TMXTileSet;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.TMXTiledMapProperty;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.util.debug.Debug;

import java.util.ArrayList;

import ru.rouge.sleeper.Managers.ResourceManager;
import ru.rouge.sleeper.Map.GameMap;
import ru.rouge.sleeper.Map.PhysicMapCell;
import ru.rouge.sleeper.Objects.BaseObject;
import ru.rouge.sleeper.Objects.Door;
import ru.rouge.sleeper.Utils.Coord;
import ru.rouge.sleeper.Utils.Directions;
import ru.rouge.sleeper.Utils.Rect;
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

    public final static int TILE_NONE = 0;

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
    private ArrayList<TMXTiledMap> mRoomsByType;    //Список комнат данной тематики
    private DungeonThemeInfo[] mDungeonInfo;        //Информация о теме подземелья

    //-----------------------------
    //CONSTRUCTOR
    //-----------------------------

    public newWorldGenerator()
    {
        mContext = WorldContext.getInstance();
        mResManager = ResourceManager.getInstance();
        mDoorsTempl = new ArrayList<DoorTemplate>();
        mObjects = new ArrayList<ObjectOnMap>();
        mRoomsByType = new ArrayList<TMXTiledMap>();

        mDungeonInfo = new DungeonThemeInfo[]
                {
                        new DungeonThemeInfo("civil", "walls_stone"),
                };
    }

    //-----------------------------
    //CLASS METHODS
    //-----------------------------

    private int getCell(final int x, final int y, final int layer)
    {
        if(x < 0 || x > mContext.mWorld.mLevels.get(mCurrLevel).getTMXLayers().get(layer).getTileColumns())
            return -1;
        if(y < 0 || y > mContext.mWorld.mLevels.get(mCurrLevel).getTMXLayers().get(layer).getTileRows())
            return -1;

        if(mContext.mWorld.mLevels.get(mCurrLevel).getTMXLayers().get(layer).getTMXTile(x, y) == null)
            return TILE_NONE;
        else
            return mContext.mWorld.mLevels.get(mCurrLevel).getTMXLayers().get(layer).getTMXTile(x, y).getGlobalTileID();
    }

    private void setCell(final int x, final int y, final int id, final int layer)
    {
        assert(mCurrLevel < mContext.mWorld.mLevels.size());
        assert(x < mContext.mWorld.mLevels.get(mCurrLevel).getTileColumns() && x > 0);
        assert(y < mContext.mWorld.mLevels.get(mCurrLevel).getTileRows() && y > 0);

        try
        {
            mContext.mWorld.mLevels.get(mCurrLevel).getTMXLayers().get(layer).addTileByGlobalTileID(x, y, id, null);
        }
        catch (Exception e)
        {
            Debug.e(e);
        }
    }

    /**
     * Создаем дверь, как объект на карте со всей логикой двери
     * */
    private void createTiledDoor(DoorTemplate door)
    {
        createTiledDoor(door.mCoord.getX(), door.mCoord.getY(), door.mDir);
    }

    private void createTiledDoor(final int x, final int y, final Directions dir)
    {
        boolean isVertical;
        int above_id;
        if(dir == Directions.DIR_EAST || dir == Directions.DIR_WEST)
        {
            isVertical = true;
            above_id = 18;
        }
        else
        {
            isVertical = false;
            above_id = 17;
        }

        setCell(x, y, 0, GameMap.LAYER_WALLS);                //Убрали стену
        setCell(x, y, 16, GameMap.LAYER_FLOOR);               //Добавили пол
        setCell(x, y, above_id, GameMap.LAYER_ABOVE);         //Добавили порог
        mContext.mWorld.mWakables.get(mCurrLevel)[x][y].isWalkable = true;

        Door newDoor = new Door(x*32, y*32, isVertical, false, ResourceManager.getInstance().mDoorsTexture, ResourceManager.getInstance().mVBO);
        mContext.mWorld.mObjects.get(mCurrLevel).add(newDoor);
        mContext.mWorld.mWakables.get(mCurrLevel)[x][y].mIndexObject = mContext.mWorld.mObjects.get(mCurrLevel).size()-1;
    }

    private void createListRooms()
    {
        ArrayList<TMXTiledMap> roomsByType = mResManager.getRoomsByType(mDungeonInfo[mThemeOfDungeon].mNameRoomTheme);

        int maxRoomsByLevel = 13;

        int questRooms = 1;
        int reqRooms = (maxRoomsByLevel - questRooms)/2;
        int commRooms = maxRoomsByLevel - reqRooms;

        ArrayList<TMXTiledMap> qRooms = new ArrayList<TMXTiledMap>();
        ArrayList<TMXTiledMap> rRooms = new ArrayList<TMXTiledMap>();
        ArrayList<TMXTiledMap> cRooms = new ArrayList<TMXTiledMap>();

        //Вытащим все комнаты темы по типам
        for(TMXTiledMap r : roomsByType)
        {
            if(r.getMapProperties().get("type").equals("quest"))
                qRooms.add(r);
            else if(r.getMapProperties().get("type").equals("uniq"))
                rRooms.add(r);
            else if(r.getMapProperties().get("type").equals("common"))
                cRooms.add(r);
        }

        //Устанавливаем нужное количество квестовых комнат в список
        while(questRooms != 0)
        {
            int roomIndex = Utils.getRand(0, qRooms.size()-1);
            mRoomsByType.add(qRooms.get(roomIndex));
            questRooms--;
        }

        while(reqRooms != 0)
        {
            int roomIndex = Utils.getRand(0, rRooms.size()-1);
            mRoomsByType.add(rRooms.get(roomIndex));
            reqRooms --;
        }

        while (commRooms != 0)
        {
            int roomIndex = Utils.getRand(0, cRooms.size()-1);
            mRoomsByType.add(cRooms.get(roomIndex));
            commRooms--;
        }
    }

    private void setupRooms()
    {
        //int countRooms = mRoomsByType.size()-1;
        boolean isNotPlace = false;
        TMXTiledMap currentLevel = mContext.mWorld.mLevels.get(mCurrLevel+1); //TODO Убрать +1

        for(int i = 0; i < mRoomsByType.size(); i++)
        {
            isNotPlace = true;
            TMXTiledMap room = mRoomsByType.get(i);
            if(room.getMapProperties().get("type").equals("quest"))
            {
                //Случайно поставим все квестовые комнаты на уровне
                boolean isCanPlace;
                int col, row;
                do
                {
                    col = Utils.getRand(0, currentLevel.getTileColumns() - room.getTileColumns());
                    row = Utils.getRand(0, currentLevel.getTileRows() - room.getTileRows());
                    if(intersectRooms(new Rect(col, row, room.getTileColumns(), room.getTileRows())))
                        isCanPlace = false;
                    else
                        isCanPlace = true;
                }
                while(!isCanPlace);

                mObjects.add(new ObjectOnMap(new Coord(col, row), new Size(room.getTileColumns(), room.getTileRows()), false, false, room.getMapProperties().get("type"), i));
                isNotPlace = false;
            }
            else
            {
                //Остальные комнаты поставим в свободные места на карте
                for(int r = 0; r < currentLevel.getTileRows() - room.getTileRows(); r++)
                {
                    for(int c = 0; c < currentLevel.getTileColumns() - room.getTileColumns(); c++)
                    {
                        if(!intersectRooms(new Rect(c, r, room.getTileColumns(), room.getTileRows())))
                        {
                            mObjects.add(new ObjectOnMap(new Coord(c, r), new Size(room.getTileColumns(), room.getTileRows()), false, false, room.getMapProperties().get("type"), i));
                            isNotPlace = false;
                            c = 50;
                            r = 50;
                        }
                    }
                }
            }

            if(isNotPlace)  //Если не смогли установить комнату, то завершим формирование уровня
                break;
        }

        Utils.printLevel(mObjects, currentLevel.getTileColumns(), currentLevel.getTileRows());
    }

    private boolean intersectRooms(Rect room)
    {
        int p1_x = room.mCoord.getX();
        int p1_y = room.mCoord.getY();
        int p2_x = room.mCoord.getX() + room.mSize.getWidth();
        int p2_y = room.mCoord.getY() + room.mSize.getHeight();
        boolean rez = false;

        for(ObjectOnMap obj : mObjects)
        {
            int p3_x = obj.mCoord.getX();
            int p3_y = obj.mCoord.getY();
            int p4_x = obj.mCoord.getX() + obj.mSize.getWidth();
            int p4_y = obj.mCoord.getY() + obj.mSize.getHeight();

            if(!(p2_y < p3_y || p1_y > p4_y || p2_x < p3_x || p1_x > p4_x))
            {
                rez = true;
                break;
            }
        }

        return rez;
    }

    public void generateNewLevel()
    {
        generate();
    }

    private void generate()
    {
        mObjects.clear();
        mDoorsTempl.clear();

        mCurrLevel = mContext.mWorld.mCurrentLevel;
        //mThemeOfDungeon = Utils.getRand(0, mDungeonInfo.length);
        mThemeOfDungeon = Utils.getRand(0, 0);  //TODO for test only
        //mRoomsByType = mResManager.getRoomsByType(mDungeonInfo[mThemeOfDungeon].mNameRoomTheme);

        int width_level = Utils.getRand(GameMap.MINLEVELWIDTH, GameMap.MAXLEVELWIDTH);	//Ширина уровня(х)
        int height_level = Utils.getRand(GameMap.MINLEVELHEIGHT, GameMap.MAXLEVELHEIGHT);	//Высота уровня(y)

        //Выбираем тайлсет, получаем от менеджера нужные комнаты, создаем карту уровня
        TMXTiledMap newLevel = new TMXTiledMap(height_level, width_level, 32, 32);//Создадим уровень
        TMXLayer floor = new TMXLayer(newLevel, width_level, height_level, "floor", ResourceManager.getInstance().mVBO);
        newLevel.getTMXLayers().add(floor);
        TMXLayer wall = new TMXLayer(newLevel, width_level, height_level, "wall", ResourceManager.getInstance().mVBO);
        newLevel.getTMXLayers().add(wall);
        TMXLayer above = new TMXLayer(newLevel, width_level, height_level, "above", ResourceManager.getInstance().mVBO);
        newLevel.getTMXLayers().add(above);
        TMXTileSet set = new TMXTileSet(1, mDungeonInfo[mThemeOfDungeon].mNameTileset, 32, 32, 2, 1, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        set.setImageSource(mContext.getAssetManager(), mContext.getTextureManager(), "tileset/" + mDungeonInfo[mThemeOfDungeon].mNameTileset + ".png", null);
        newLevel.getTMXTileSets().add(set);
        mContext.mWorld.mLevels.add(newLevel);

        PhysicMapCell[][] mWalkable = new PhysicMapCell[width_level][height_level];
        for(int k = 0; k < width_level; k++)
            for(int m = 0; m < height_level; m++)
            {
                mWalkable[k][m] = new PhysicMapCell();
                mWalkable[k][m].isWalkable = false;
                mWalkable[k][m].mIndexObject = -1;
            }
        mContext.mWorld.mWakables.add(mWalkable);

        ArrayList<BaseObject> objects = new ArrayList<BaseObject>();
        mContext.mWorld.mObjects.add(objects);

        ArrayList<TMXObject> spawn = new ArrayList<TMXObject>();
        mContext.mWorld.mSpawns.add(spawn);
        //Конец создания карты уровня

        //TODO Составим список комнат, которые должны быть расставлены на уровне
        createListRooms();

        //TODO Случайно расставим все комнаты с типом квест, потом поставим все остальные последовательно
        setupRooms();
    }

    //-----------------------------
    //GETTERS/SETTERS
    //-----------------------------

    //-----------------------------
    //INNER CLASSES
    //-----------------------------

    //Объект установленный на карту
    public final class ObjectOnMap
    {
        public Coord mCoord;            //Строка и столбец начала комнаты
        public Size mSize;              //Размер комнаты в тайлах
        public boolean isConnected;     //Добавлена ли комната в список проходимых
        public boolean isStair;         //Установлена ли лестница
        public String mType;            //Тип комнаты (квестовая, уникальная, общая)
        public int mIndex;              //Индекс комнаты из списка к расстановке

        public ObjectOnMap(Coord c, Size s, boolean isconnected, boolean isstair, String type, int index)
        {
            this.mCoord = c;
            this.mSize = s;
            this.isConnected = isconnected;
            this.isStair = isstair;
            this.mType = type;
            this.mIndex = index;
        }
    }

    //Шаблон двери, без тайла
    private final class DoorTemplate
    {
        public Coord mCoord;
        public Directions mDir;
        public boolean isFree;
    }

    //Информация о темах подземелья
    private final class DungeonThemeInfo
    {
        public String mNameTileset;
        public String mNameRoomTheme;

        public DungeonThemeInfo(String nameRoomTheme, String nameTileset)
        {
            this.mNameRoomTheme = nameRoomTheme;
            this.mNameTileset = nameTileset;
        }
    }
}
