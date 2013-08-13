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
				//currStepPlayerX = currPlayerX;
				//currStepTime = SystemClock.elapsedRealtime();

				/*IUpdateHandler updatePosition = new IUpdateHandler()
				{
					@Override
					public void onUpdate(float pSecondsElapsed)
					{
						float currStepPlayerY = mPlayer.getY();

						//while(currStepPlayerX < (currPlayerX + 32) && (currStepTime + STEP_TIME) <= SystemClock.elapsedRealtime())//32 is Tile width
						//{
							//Debug.e("currStepPlayerX = " + currStepPlayerX);
							//mPlayer.setPosition(currStepPlayerX, currStepPlayerY);
							currStepPlayerX = currStepPlayerX + mPlayer.mSpeed;
							currStepTime = SystemClock.elapsedRealtime();
						//}

						/*if(currStepPlayerX >= (currPlayerX + 32))
						{
							Debug.e("Stop animation!!!!!");
							mPlayer.stopAnimation();
							mPlayer.unregisterUpdateHandler(this);
						}*/
					/*}

					@Override
					public void reset()
					{

					}
				};*/
				/*mPlayer.unregisterUpdateHandler(updatePosition);
				mPlayer.registerUpdateHandler(updatePosition);*/
			}
			else			//left
			{
				Debug.e("Move left");
				mPlayer.animatePlayer(Directions.DIR_WEST);
				/*currStepPlayerX = currPlayerX;
				currStepTime = SystemClock.elapsedRealtime();

				IUpdateHandler updatePosition = new IUpdateHandler()
				{
					@Override
					public void onUpdate(float pSecondsElapsed)
					{
						float currStepPlayerY = mPlayer.getY();

						//while(currStepPlayerX > (currPlayerX - 32) && (currStepTime + STEP_TIME) <= SystemClock.elapsedRealtime())//32 is Tile width
						{
							Debug.e("currStepPlayerX = " + currStepPlayerX);
							mPlayer.setPosition(currStepPlayerX, currStepPlayerY);
							currStepPlayerX = currStepPlayerX - mPlayer.mSpeed;
							currStepTime = SystemClock.elapsedRealtime();
						}

						if(currStepPlayerX <= (currPlayerX - 32))
						{
							Debug.e("Stop animation!!!!!");
							mPlayer.stopAnimation();
							mPlayer.unregisterUpdateHandler(this);
						}
					}

					@Override
					public void reset()
					{

					}
				};
				mPlayer.unregisterUpdateHandler(updatePosition);
				mPlayer.registerUpdateHandler(updatePosition);*/
			}
		}
		else
		{
			//Move up/down

			if(deltaY > 0)	//down
			{
				Debug.e("Move down");
			}
			else			//up
			{
				Debug.e("Move up");
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

	public void setWorld(GameMap map)
	{
		this.mGameMap = map;
	}

	//---------------------------------
	//INNER CLASSES
	//---------------------------------        
}
