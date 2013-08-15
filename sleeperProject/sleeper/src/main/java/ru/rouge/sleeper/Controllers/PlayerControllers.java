package ru.rouge.sleeper.Controllers;

import org.andengine.input.touch.TouchEvent;
import org.andengine.util.debug.Debug;

import ru.rouge.sleeper.Map.GameMap;
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

	//---------------------------------
	//VARIABLES
	//---------------------------------

	private Player mPlayer;
	//---------------------------------
	//SUPER
	//---------------------------------

	//---------------------------------
	//METHODS
	//---------------------------------

	public PlayerControllers()	{}

	public void move(TouchEvent pSceneTouchEvent)
	{
		final float currPlayerX = mPlayer.getX();
		final float currPlayerY = mPlayer.getY();

		float destinationPosX = pSceneTouchEvent.getX();
		float destinationPosY = pSceneTouchEvent.getY();

		Directions mPlayerDir = mPlayer.getDirection();

		float deltaX = destinationPosX - currPlayerX;
		float deltaY = destinationPosY - currPlayerY;

		if(Math.abs(deltaX) > Math.abs(deltaY))
		{
			//Move left/right
			if(deltaX > 0)	//right
			{
				if(mPlayerDir == Directions.DIR_EAST)
					return;

				Debug.e("Move right");
				mPlayer.setNewDirection(Directions.DIR_EAST);
			}
			else			//left
			{
				if(mPlayerDir == Directions.DIR_WEST)
					return;

				Debug.e("Move left");
				mPlayer.setNewDirection(Directions.DIR_WEST);
			}
		}
		else
		{
			//Move up/down
			if(deltaY > 0)	//down
			{
				if(mPlayerDir == Directions.DIR_SOUTH)
					return;

				Debug.e("Move down");
				mPlayer.setNewDirection(Directions.DIR_SOUTH);
			}
			else			//up
			{
				if(mPlayerDir == Directions.DIR_NORTH)
					return;

				Debug.e("Move up");
				mPlayer.setNewDirection(Directions.DIR_NORTH);
			}
		}
	}

	//---------------------------------
	//GETTERS/SETTERS
	//---------------------------------

	public void setPlayer(Player player)
	{
		this.mPlayer = player;
	}
	//---------------------------------
	//INNER CLASSES
	//---------------------------------        
}
