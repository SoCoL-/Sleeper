package ru.rouge.sleeper.Controllers;

import java.util.ArrayList;

import ru.rouge.sleeper.Objects.Door;
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
        ArrayList<Door> doors = WorldContext.getInstance().mWorld.mDoors;
        Door currObject = doors.get(index);

        //Определим, что за объект

        //Попробуем выполнить над ним действие
        if(currObject.isOpen())
            rez = true;
        else if(currObject.isLocked())
        {
            rez = false;
            //TODO сделать сообщение в HUD
        }
        else if(!currObject.isOpen() && !currObject.isLocked())
        {
            currObject.setOpen(true);
            rez = true;
        }

        return rez;
    }

    //-----------------------------
    //GETTERS/SETTERS
    //-----------------------------

    //-----------------------------
    //INNER CLAASES
    //-----------------------------
}
