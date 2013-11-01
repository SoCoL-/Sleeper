package ru.rouge.sleeper.Controllers;

import org.andengine.util.debug.Debug;

import java.util.ArrayList;

import ru.rouge.sleeper.Objects.BaseObject;
import ru.rouge.sleeper.Objects.Door;
import ru.rouge.sleeper.Objects.Stair;
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

    //-----------------------------
    //CONSTRUCTOR
    //-----------------------------

    public ObjectController()
    {}

    //-----------------------------
    //CLASS METHODS
    //-----------------------------

    public boolean workWithObject(int index)
    {
        boolean rez = false;

        //Получим наш объект из списка
        ArrayList<BaseObject> objects = WorldContext.getInstance().mWorld.mObjects.get(WorldContext.getInstance().mWorld.mCurrentLevel);
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
