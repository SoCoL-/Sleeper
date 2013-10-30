package ru.rouge.sleeper.Controllers;

import android.view.MotionEvent;

import org.andengine.input.touch.TouchEvent;
import org.andengine.util.debug.Debug;

import ru.rouge.sleeper.Objects.Player;
import ru.rouge.sleeper.Utils.Directions;

/**
 * Created by Evgenij on 12.08.13.
 *
 * Описание поведения героя
 */
public final class PlayerControllers
{
	//---------------------------------
	//CONSTANTS
	//---------------------------------

    private final static int VALUE_POSITION = 30;   //Порог, после которого проверяется направление движения персонажа

	//---------------------------------
	//VARIABLES
	//---------------------------------

    private float mCenterX, mCenterY;               //Координаты, относительно которых будем проверять направление движения
	private Player mPlayer;                         //Персонаж, для контроля его состояния
    Directions curDir;                              //Текущее направление сдвига пальца
	//---------------------------------
	//SUPER
	//---------------------------------

	//---------------------------------
	//METHODS
	//---------------------------------

	public PlayerControllers()	{}

	public void move(TouchEvent pSceneTouchEvent)
	{
        if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
        {
            //Запомним точку отчета направления движения персонажа
            Debug.i("ACTION_DOWN");
            mCenterX = pSceneTouchEvent.getX();
            mCenterY = pSceneTouchEvent.getY();
            //mCurrentStepTime = 0;
            curDir = Directions.DIR_NONE;
            Debug.i("Нажали по координатам: х = " + mCenterX + ", y = " + mCenterY);
        }
        else if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_MOVE)
        {
            if(Math.abs(mCenterX - pSceneTouchEvent.getX()) > VALUE_POSITION || Math.abs(mCenterY - pSceneTouchEvent.getY()) > VALUE_POSITION)
            {
                //Debug.i("PlayerController: mCenterX = " + mCenterX + " , pSceneTouchEvent.getX() = " + pSceneTouchEvent.getX() + " , mCenterY = " + mCenterY + " , pSceneTouchEvent.getY() = " + pSceneTouchEvent.getY());
                Debug.i("Сдвинули больше порогового значения");
                //Вычислим новое направление персонажа
                curDir = getDirection(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
                //Debug.i("PlayerController: curDir = " + curDir);
                //Debug.i("PlayerController: mPlayer.getDirection() = " + mPlayer.getDirection());
                if(mPlayer.isMove() && curDir == mPlayer.getDirection())
                {
                    Debug.i("Мы уже движемся в этом направлении");
                    //Запоминаем текущую точку отчета направления
                    mCenterX = pSceneTouchEvent.getX();
                    mCenterY = pSceneTouchEvent.getY();
                    //Debug.i("Запомним новый центр x = " + mCenterX + ", y = " + mCenterY);
                    return;
                }

                if(curDir != mPlayer.getDirection())
                {
                    mPlayer.setNewDirection(curDir);
                }

                if(!mPlayer.isMove())
                    mPlayer.setMove(true);

                //Запоминаем текущую точку отчета направления
                mCenterX = pSceneTouchEvent.getX();
                mCenterY = pSceneTouchEvent.getY();
                //Debug.i("Запомним новый центр x = " + mCenterX + ", y = " + mCenterY);
            }
        }
        else if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_UP)
        {
            Debug.i("ACTION_UP");
            Debug.i("Прекратили управлять перемещением персонажа");
            //Очистим точку отчета
            mCenterX = 0;
            mCenterY = 0;

            mPlayer.setNewDirection(Directions.DIR_NONE);
            mPlayer.setMove(false);
            curDir = Directions.DIR_NONE;
        }
	}

	//---------------------------------
	//GETTERS/SETTERS
	//---------------------------------

    private Directions getDirection(float x, float y)
    {
        Directions rez = null;

        Debug.i("PlayerController: Вычисляем направление");

        if(Math.abs(mCenterX - x) > 30 && mCenterX - x > 0)
        {
            rez = Directions.DIR_WEST;
            Debug.i("PlayerController: Запад");
        }
        else if(Math.abs(mCenterX - x) > 30 && mCenterX - x < 0)
        {
            rez = Directions.DIR_EAST;
            Debug.i("PlayerController: Восток");
        }
        else if(Math.abs(mCenterY - y) > 30 && mCenterY - y > 0)
        {
            rez = Directions.DIR_NORTH;
            Debug.i("PlayerController: Север");
        }
        else if(Math.abs(mCenterY - y) > 30 && mCenterY - y < 0)
        {
            rez = Directions.DIR_SOUTH;
            Debug.i("PlayerController: Юг");
        }

        return rez;
    }

	public void setPlayer(Player player)
	{
		this.mPlayer = player;
	}
	//---------------------------------
	//INNER CLASSES
	//---------------------------------        
}
