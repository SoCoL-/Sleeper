package ru.rouge.sleeper.Objects;

import android.os.SystemClock;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import ru.rouge.sleeper.Utils.Directions;
import ru.rouge.sleeper.WorldContext;

/**
 * Evgenij Savchik
 * Created by 1 on 05.07.13.
 *
 * Собственно сам игрок
 */
public final class Player extends BaseAnimObject
{
	//-----------------------------
	//CONSTANTS
	//-----------------------------

	final private long STEP_TIME = 20;					//Время одного шага в миллисекундах
	final private int NEXT_DESTINATION_TILE_WIDTH;		//Размер пути( = ширина тайла) на который надо переместиться за один раз

	//-----------------------------
	//VARIABLES
	//-----------------------------

	private Directions mDir;							//Направление движения персонажа
	private boolean isMove;								//Движется ли персонаж
	private float mLength;								//Длина пути персонажа
	private int mKX, mKY;								//Коэфиценты для движения персонажа(отнимать или прибавлять скорость покоординатно)
	private long mCurrentStepTime;						//Прошедшее время с момента последнего шага


	//-----------------------------
	//CONSTRUCTORS
	//-----------------------------

	public Player(float pX, float pY, ITiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager)
	{
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		mSpeed = 0.5f;
		this.isMove = false;
		mDir = Directions.DIR_EAST;
		mKX = 0;
		mKY = 0;
		NEXT_DESTINATION_TILE_WIDTH = 32;
		setAnimateDirection();
	}

	//-----------------------------
	//SUPER METHODS
	//-----------------------------

	@Override
	protected void onManagedUpdate(float pSecondsElapsed)
	{
		//TODO
		if(isMove)			//Обработка движения персонажа
		{
			if(SystemClock.elapsedRealtime() >= mCurrentStepTime + STEP_TIME)
			{
				float nextCoordX = getX() + mSpeed*mKX;
				float nextCoordY = getY() + mSpeed*mKY;

				setPosition(nextCoordX, nextCoordY);

				mCurrentStepTime = SystemClock.elapsedRealtime();
				mLength -= mSpeed;
				if(mLength <= 0)
				{
					isMove = false;
					setAnimateDirection();
				}
			}
		}

		super.onManagedUpdate(pSecondsElapsed);
	}

	//-----------------------------
	//CLASS METHODS
	//-----------------------------

	/**
	*  Функция приводит персонаж на первый кадр выбранного направления, если он не движется
 	* */
	public void setAnimateDirection()
	{
		switch (mDir)
		{
			case DIR_WEST:
				this.setCurrentTileIndex(0);
				break;
			case DIR_SOUTH:
				this.setCurrentTileIndex(24);
				break;
			case DIR_EAST:
				this.setCurrentTileIndex(8);
				break;
			case DIR_NORTH:
				this.setCurrentTileIndex(16);
				break;
		}
	}

	/**
	 * Анимируем персонаж согласно выбранному направлению
	 * */
	public void animatePlayer(Directions dir)
	{
		if(this.isMove)
			return;

		mDir = dir;
		this.isMove = true;
		this.mLength = NEXT_DESTINATION_TILE_WIDTH;
		this.mCurrentStepTime = SystemClock.elapsedRealtime();

		switch (dir)
		{
			case DIR_EAST:
				this.animate(new long[]{150, 150, 150, 150, 150, 150, 150, 150}, 8, 15, true);//run right

				mKY = 0;
				mKX = 1;

				break;
			case DIR_WEST:
				this.animate(new long[]{150, 150, 150, 150, 150, 150, 150, 150}, 0, 7, true);//run left

				mKY = 0;
				mKX = -1;

				break;
			case DIR_SOUTH:
				this.animate(new long[]{150, 150, 150, 150, 150, 150, 150, 150}, 24, 31, true);//run down
				mKX = 0;
				break;
			case DIR_NORTH:
				this.animate(new long[]{150, 150, 150, 150, 150, 150, 150, 150}, 16, 23, true);//run up
				mKX = 0;
				break;
		}
	}

	//-----------------------------
	//GETTERS/SETTERS
	//-----------------------------

	//-----------------------------
	//INNER CLASSES
	//-----------------------------
}
