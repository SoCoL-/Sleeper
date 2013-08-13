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
	private GameMap mGameMap;
	//private float currStepPlayerX;
	//private float currStepTime;
	//---------------------------------
	//SUPER
	//---------------------------------

	//---------------------------------
	//METHODS
	//---------------------------------

	public PlayerControllers()
	{
		//this.mPlayer = WorldContext.getInstance().mPlayer;
		//this.mGameMap = WorldContext.getInstance().mWorld;
		//this.currStepTime = 0f;
	}

	public void move(TouchEvent pSceneTouchEvent)
	{
		final float currPlayerX = mPlayer.getX();
		final float currPlayerY = mPlayer.getY();
		Debug.e("Player coords = " + currPlayerX + ", " + currPlayerY);

		float destinationPosX = pSceneTouchEvent.getX();
		float destinationPosY = pSceneTouchEvent.getY();

		Debug.e("Destination coords = " + destinationPosX + ", " + destinationPosY);

		float deltaX = destinationPosX - currPlayerX;
		float deltaY = destinationPosY - currPlayerY;

		if(Math.abs(deltaX) > Math.abs(deltaY))
		{
			//Move left/right
			if(deltaX > 0)	//right
			{
				Debug.e("Move right");
				mPlayer.animatePlayer(Directions.DIR_EAST);
			}
			else			//left
			{
				Debug.e("Move left");
				mPlayer.animatePlayer(Directions.DIR_WEST);
			}
		}
		else
		{
			//Move up/down

			if(deltaY > 0)	//down
			{
				Debug.e("Move down");
                mPlayer.animatePlayer(Directions.DIR_SOUTH);
			}
			else			//up
			{
				Debug.e("Move up");
                mPlayer.animatePlayer(Directions.DIR_NORTH);
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

	/*public void setWorld(GameMap map)
	{
		this.mGameMap = map;
	}*/

	//---------------------------------
	//INNER CLASSES
	//---------------------------------        
}
