package ru.rouge.sleeper.Managers;

import org.andengine.extension.tmx.TMXObject;
import org.andengine.util.debug.Debug;

import java.util.ArrayList;

import ru.rouge.sleeper.Generator.WorldGenerator;
import ru.rouge.sleeper.Map.GameMap;
import ru.rouge.sleeper.WorldContext;

/**
 * Created by 1 on 31.10.13.
 * Менеджер, отвечающий за загрузку нового уровня
 */
public class LevelManager
{
    //-----------------------------
    //CONSTANTS
    //-----------------------------

    //-----------------------------
    //VARIABLES
    //-----------------------------

    private final GameMap mGameWorld;               //Весь мир игры
    private WorldGenerator mGenerator;              //Генератор уровней

    //-----------------------------
    //CONSTRUCTOR
    //-----------------------------

    public LevelManager()
    {
        this.mGameWorld = WorldContext.getInstance().mWorld;
        this.mGenerator = new WorldGenerator();
    }

    //-----------------------------
    //CLASS METHODS
    //-----------------------------

    /**
     * Переходим на уровень ниже
     * */
    public void nextLevel()
    {
        //Проверка на превышение максимального уровня
        if(mGameWorld.mCurrentLevel >= mGameWorld.MAX_LEVELS)
        {
            mGameWorld.mCurrentLevel = mGameWorld.MAX_LEVELS;
            return;
        }

        //Увеличиваем значение текущего уровня
        if(mGameWorld.mLevels.size() > 0 && mGameWorld.mLevels.size()-1 < mGameWorld.mCurrentLevel + 1)
        {
            Debug.i("Первый уровень существует, нового нет еще, сгенерируем");
            mGameWorld.mCurrentLevel ++;
            mGenerator.generateNewLevel();
        }
        else if(mGameWorld.mLevels.size() == 0)
        {
            Debug.i("Начали новую игру, сгенерируем первый уровень");
            mGenerator.generateNewLevel();
        }
        else if(mGameWorld.mLevels.size()-1 >= mGameWorld.mCurrentLevel + 1)
        {
            Debug.i("Мы переходим на сгенерированный уровень");
            mGameWorld.mCurrentLevel ++;
        }

        //Надо найти все точки возрождения на уровне, поставить туда игрока
        ArrayList<TMXObject> playerSpawn = WorldContext.getInstance().mWorld.mSpawns.get(WorldContext.getInstance().mWorld.mCurrentLevel);
        for(TMXObject spawn : playerSpawn)
        {
            if(spawn.getName().contains("player_spawn") && spawn.getType().equals("down"))
            {
                String name_spawn = spawn.getName();
                name_spawn = name_spawn.substring("player_spawn_".length(), name_spawn.length());
                Debug.i("Вырезали все лишнее из названия точки респа = " + name_spawn);
                int num = Integer.parseInt(name_spawn);
                if(num == mGameWorld.mCurrentLevel)
                {
                    Debug.i("Нашли нужный респ, поставим игрока в (" + spawn.getX() + ", " + spawn.getY() + ")");
                    WorldContext.getInstance().mPlayer.stopAnimation();
                    WorldContext.getInstance().mPlayer.setMove(false);
                    WorldContext.getInstance().mPlayer.setPlayerPosition(spawn.getX(), spawn.getY());
                    /*int tileX = spawn.getX()/32;
                    int tileY = spawn.getY()/32;
                    WorldContext.getInstance().mWorld.mLevels.get(WorldContext.getInstance().mWorld.mCurrentLevel).getTMXLayers().get(GameMap.LAYER_FLOOR).setVisibleTiles(tileX, tileY);
                    WorldContext.getInstance().mWorld.mLevels.get(WorldContext.getInstance().mWorld.mCurrentLevel).getTMXLayers().get(GameMap.LAYER_WALLS).setVisibleTiles(tileX, tileY);
                    WorldContext.getInstance().mWorld.mLevels.get(WorldContext.getInstance().mWorld.mCurrentLevel).getTMXLayers().get(GameMap.LAYER_ABOVE).setVisibleTiles(tileX, tileY);*/
                    Debug.i("Игрока поставили на место");
                }
            }
        }

        //Обновим сцену с новым уровнем
        if(mGameWorld.mCurrentLevel > 0)
            ScenesManager.getInstance().updateMainScene();
    }

    /**
     * переходим на уровень выше
     * */
    public void pervLevel()
    {
        //Проверка на минимальное значение уровня
        if(mGameWorld.mCurrentLevel <= 0)
        {
            mGameWorld.mCurrentLevel = 0;
            return;
        }

        mGameWorld.mCurrentLevel --;

        //Надо найти все точки возрождения на уровне, поставить туда игрока
        ArrayList<TMXObject> playerSpawn = WorldContext.getInstance().mWorld.mSpawns.get(WorldContext.getInstance().mWorld.mCurrentLevel);
        for(TMXObject spawn : playerSpawn)
        {
            if(spawn.getName().contains("player_spawn") && spawn.getType().equals("up"))
            {
                String name_spawn = spawn.getName();
                name_spawn = name_spawn.substring("player_spawn_".length(), name_spawn.length());
                Debug.i("Вырезали все лишнее из названия точки респа = " + name_spawn);
                int num = Integer.parseInt(name_spawn);
                if(num == mGameWorld.mCurrentLevel)
                {
                    Debug.i("Нашли нужный респ, поставим игрока в (" + spawn.getX() + ", " + spawn.getY() + ")");
                    WorldContext.getInstance().mPlayer.stopAnimation();
                    WorldContext.getInstance().mPlayer.setMove(false);
                    WorldContext.getInstance().mPlayer.setPlayerPosition(spawn.getX(), spawn.getY());
                    Debug.i("Игрока поставили на место");
                }
            }
        }

        //Обновим сцену с новым уровнем
        ScenesManager.getInstance().updateMainScene();
    }

    //-----------------------------
    //GETTERS/SETTERS
    //-----------------------------

    //-----------------------------
    //INNER CLAASES
    //-----------------------------
}
