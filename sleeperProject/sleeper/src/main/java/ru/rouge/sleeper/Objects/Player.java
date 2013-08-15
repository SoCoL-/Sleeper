package ru.rouge.sleeper.Objects;

import android.os.SystemClock;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

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
	//Время анимации каждого кадра бега в одном направлении
	final private long[] ANIM_TIMINGS_RUN = {200, 200, 200, 200, 200, 200, 200, 200};

	//-----------------------------
	//VARIABLES
	//-----------------------------

	private Directions mDir;							//Направление движения персонажа
	private Directions mOldDir;							//Старое направление движения
	private boolean isMove;								//Движется ли персонаж
	public boolean isMoveLoop;							//Если мы нажали и не отпускаем, то персонаж движется все время в заданном направлении
	private float mLength;								//Длина пути персонажа
	private int mKX, mKY;								//Коэфиценты для движения персонажа(отнимать или прибавлять скорость покоординатно)
	private long mCurrentStepTime;						//Прошедшее время с момента последнего шага


	//-----------------------------
	//CONSTRUCTORS
	//-----------------------------

	public Player(float pX, float pY, ITiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager)
	{
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		mSpeed = 1f;
		this.isMove = false;
		mDir = Directions.DIR_NONE;
		mOldDir = Directions.DIR_EAST;
		mKX = 0;
		mKY = 0;
		mLength = 0;
		mCurrentStepTime = 0;
		NEXT_DESTINATION_TILE_WIDTH = 32;
		setAnimateDirection();
	}

	//-----------------------------
	//SUPER METHODS
	//-----------------------------

	@Override
	protected void onManagedUpdate(float pSecondsElapsed)
	{
		if(isMove)			//Обработка движения персонажа
		{
            //Debug.e("isMove");
            if(SystemClock.elapsedRealtime() >= mCurrentStepTime + STEP_TIME)
			{
				if(mLength <= 0)
				{
					//Debug.e("Stop move!");
					if(isMoveLoop)
					{
						//Debug.e("Resume move!");
						if(mOldDir != mDir)
							animatePlayer();

						mLength = NEXT_DESTINATION_TILE_WIDTH;
					}
					else if(!isMoveLoop)
					{
						//Debug.e("Stop animation!!!!!!");
						isMove = false;
						mOldDir = mDir;
						mDir = Directions.DIR_NONE;
						stopAnimation();
						setAnimateDirection();
					}
				}

				//Debug.e("Step");
				float nextCoordX = getX() + mSpeed*mKX;
				float nextCoordY = getY() + mSpeed*mKY;
                //Debug.e("nextCoordX = " + nextCoordX);
                //Debug.e("nextCoordY = " + nextCoordY);

				setPosition(nextCoordX, nextCoordY);

				mCurrentStepTime = SystemClock.elapsedRealtime();
				mLength -= mSpeed;
                //Debug.e("mLength = " + mLength);
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
			case DIR_NONE:
				switch (mOldDir)
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
				break;
		}
	}

	/**
	 * Меняем направление движения персонажа
	 * */
	public void setNewDirection(Directions dir)
	{
		//if(mDir != Directions.DIR_NONE)
			//mOldDir = mDir;
		//else
		if(mDir == Directions.DIR_NONE)
		{
			//mOldDir = dir;
			isMove = true;
		}
		mOldDir = mDir;
		mDir = dir;
	}

	/**
	 * Анимируем персонаж согласно выбранному направлению
	 * */
	public void animatePlayer()
	{
		//Debug.e("Calculate animation direction");
		this.isMove = true;

		//Debug.e("mDir = " + mDir);

		switch (mDir)
		{
			case DIR_EAST:
				this.animate(ANIM_TIMINGS_RUN, 8, 15, true);//run right

				mKY = 0;
				mKX = 1;

				break;
			case DIR_WEST:
				this.animate(ANIM_TIMINGS_RUN, 0, 7, true);//run left

				mKY = 0;
				mKX = -1;

				break;
			case DIR_SOUTH:
				this.animate(ANIM_TIMINGS_RUN, 24, 31, true);//run down

				mKX = 0;
                mKY = 1;

				break;
			case DIR_NORTH:
				this.animate(ANIM_TIMINGS_RUN, 16, 23, true);//run up

				mKX = 0;
                mKY = -1;

				break;
		}
	}

	//-----------------------------
	//GETTERS/SETTERS
	//-----------------------------

	/**
	 * Находится ли в движении персонаж?
	 * @return true - движется, false - иначе
	 * */
	public boolean isMove()
	{
		return this.isMove;
	}

	public Directions getDirection()
	{
		return this.mDir;
	}

	//-----------------------------
	//INNER CLASSES
	//-----------------------------
}
