package ru.rouge.sleeper.Controllers;

import org.andengine.input.touch.TouchEvent;
import org.andengine.util.debug.Debug;

import java.util.ArrayList;

import ru.rouge.sleeper.Managers.ResourceManager;
import ru.rouge.sleeper.Managers.ScenesManager;
import ru.rouge.sleeper.Objects.BaseObject;
import ru.rouge.sleeper.Objects.Door;
import ru.rouge.sleeper.Objects.Stair;
import ru.rouge.sleeper.Objects.UI.Dialog;
import ru.rouge.sleeper.Scenes.MainGameScene;
import ru.rouge.sleeper.WorldContext;

/**
 * Created by 1 on 30.10.13.
 * Обработка действий игрока над интерактивными объектами
 */
public class ObjectController
{
    //-----------------------------
    //CONSTANTS
    //-----------------------------

    //-----------------------------
    //VARIABLES
    //-----------------------------

    private WorldContext mWC;

    //-----------------------------
    //CONSTRUCTOR
    //-----------------------------

    public ObjectController()
    {
        mWC = WorldContext.getInstance();
    }

    //-----------------------------
    //CLASS METHODS
    //-----------------------------

    public void onLongClick(TouchEvent pSceneTouchEvent)
    {
        int currX = (int)pSceneTouchEvent.getX() / 32;
        int currY = (int)pSceneTouchEvent.getY() / 32;

        if(currX < 0 || currY < 0)
            return;

        if(currX >= mWC.mWorld.mLevels.get(mWC.mWorld.mCurrentLevel).getTileColumns())
            return;
        if(currY >= mWC.mWorld.mLevels.get(mWC.mWorld.mCurrentLevel).getTileRows())
            return;

        if(mWC.mWorld.mWakables.get(mWC.mWorld.mCurrentLevel)[currX][currY].mIndexObject == -1)
            return;

        int objectIndex = mWC.mWorld.mWakables.get(mWC.mWorld.mCurrentLevel)[currX][currY].mIndexObject;
        BaseObject currObject = mWC.mWorld.mObjects.get(mWC.mWorld.mCurrentLevel).get(objectIndex);

        /**
         * Тестовая проба
         * */
        ((MainGameScene)ScenesManager.getInstance().getCurrentScene()).setDialogMode();
        //((MainGameScene)ScenesManager.getInstance().getCurrentScene()).setPause(true);
        //mWC.mDialogManager.addMessageToDialog(currObject.getObjectMessage());
        Dialog d = new Dialog(125, 90, 550, 300, ResourceManager.getInstance().mVBO);
        d.setTextDialog(currObject.getObjectMessage());
        ScenesManager.getInstance().getCurrentScene().setChildScene(d, false, true, true);

      }

    /**
     * Обработаем взаимодействие с предметом, если мы на него зашли
     * */
    public boolean workWithObject(int index)
    {
        boolean rez = false;

        //Получим наш объект из списка
        ArrayList<BaseObject> objects = mWC.mWorld.mObjects.get(mWC.mWorld.mCurrentLevel);
        BaseObject currObject = objects.get(index);

        //Определим, что за объект
        if(currObject instanceof Door)//Попробуем выполнить над ним действие
            rez = openDoor((Door)currObject);
        else if(currObject instanceof Stair)
            rez = doStair((Stair) currObject);

        return rez;
    }

    /**
     * Пытаемся открыть дверь
     * @param door - дверь, что открываем
     * @return true - если открыли, иначе false
     * */
    private boolean openDoor(Door door)
    {
        boolean rez = false;
        if(door.isOpen())
            rez = true;
        else if(door.isLocked())
        {
            rez = false;
            //TODO сделать сообщение в HUD
        }
        else if(!door.isOpen() && !door.isLocked())
        {
            door.setOpen(true);
            rez = true;
        }
        return rez;
    }

    private boolean doStair(Stair s)
    {
        Debug.i("Лестница, сгенерируем новый уровень!!");
        boolean rez;
        rez = s.doOnStair();
        return rez;
    }

    //-----------------------------
    //GETTERS/SETTERS
    //-----------------------------

    //-----------------------------
    //INNER CLAASES
    //-----------------------------
}
