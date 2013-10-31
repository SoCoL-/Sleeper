package ru.rouge.sleeper.Managers;

import org.andengine.util.debug.Debug;

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
        //if(mGameWorld.mLevels.size() > 0 && mGameWorld.mLevels.get(mGameWorld.mCurrentLevel+1) == null)
        if(mGameWorld.mLevels.size() > 0 && mGameWorld.mLevels.size() < mGameWorld.mCurrentLevel + 1)
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
        //else if(mGameWorld.mLevels.get(mGameWorld.mCurrentLevel+1) != null)
        else if(mGameWorld.mLevels.size() <= mGameWorld.mCurrentLevel + 1)
        {
            Debug.i("Мы переходим на сгенерированный уровень");
            mGameWorld.mCurrentLevel ++;
        }

        //TODO Надо найти все точки возрождения на уровне, поставить туда игрока

        //Обновим сцену с новым уровнем
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

        //TODO Надо найти все точки возрождения на уровне, поставить туда игрока

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
