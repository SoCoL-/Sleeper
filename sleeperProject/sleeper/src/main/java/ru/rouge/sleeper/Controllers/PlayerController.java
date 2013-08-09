package ru.rouge.sleeper.Controllers;

import org.andengine.input.touch.TouchEvent;

import ru.rouge.sleeper.Objects.Player;
import ru.rouge.sleeper.WorldContext;

/**
 * Created by 1 on 10.08.13.
 * Класс будет контролировать все действия игрока(перемещение, столкновения с объектами, ...)
 */
public class PlayerController
{
    //-------------------------------
    //CONSTANTS
    //-------------------------------

    //-------------------------------
    //VARIABLES
    //-------------------------------

    private WorldContext mWContext;

    private Player mPlayer;

    //-------------------------------
    //CONSTRUCTORS
    //-------------------------------

    public PlayerController()
    {
        this.mWContext = WorldContext.getInstance();
    }

    //-------------------------------
    //SUPER METHODS
    //-------------------------------

    //-------------------------------
    //METHODS
    //-------------------------------

    public void movePlayer(TouchEvent pSceneTouchEvent)
    {
        float destCoordX = pSceneTouchEvent.getX();
        float destCoordY = pSceneTouchEvent.getY();


    }

    //-------------------------------
    //GETTERS/SETTERS
    //-------------------------------

    //-------------------------------
    //INNER CLASSES
    //-------------------------------
}
