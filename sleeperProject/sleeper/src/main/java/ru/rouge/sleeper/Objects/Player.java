package ru.rouge.sleeper.Objects;

import android.os.SystemClock;

import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import ru.rouge.sleeper.Map.GameMap;
import ru.rouge.sleeper.Map.PhysicMapCell;
import ru.rouge.sleeper.Utils.Directions;
import ru.rouge.sleeper.WorldContext;

/**
 * Evgenij Savchik
 * Created by 1 on 05.07.13.
 *
 * Собственно сам игрок
 */
public class Player extends BaseAnimObject
{
	//-----------------------------
	//CONSTANTS
	//-----------------------------

    private final static long STEP_TIME = 10;					//Время одного шага в миллисекундах
	//Время анимации каждого кадра бега в одном направлении
    private final static long[] ANIM_TIMINGS_RUN = {100, 100, 100, 100, 100, 100, 100, 100};
    private final static long[] ANIM_TIMINGS_RUN_FAST = {50, 50, 50, 50, 50, 50, 50, 50};

	//-----------------------------
	//VARIABLES
	//-----------------------------

	private Directions mDir;							//Направление движения персонажа
	private Directions mOldDir;							//Старое направление движения
    private Directions mNextDir;                        //Следующее направление движения, если еще доходим до середины тайла
    public float mNextX, mNextY;                        //Координаты назначения игрока
	private boolean isMove;								//Движется ли персонаж
	private int mKX, mKY;								//Коэфиценты для движения персонажа(отнимать или прибавлять скорость покоординатно)
	private long mCurrentStepTime;						//Прошедшее время с момента последнего шага
    private long[] mCurrRun;                            //Текущая скорость перемещения

	//-----------------------------
	//CONSTRUCTORS
	//-----------------------------

	public Player(float pX, float pY, ITiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager)
	{
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
        if(WorldContext.getInstance().mSettings.isFastPlayer())
        {
            mSpeed = 2f;
            mCurrRun = ANIM_TIMINGS_RUN_FAST;
        }
        else
        {
            mSpeed = 1f;
            mCurrRun = ANIM_TIMINGS_RUN;
        }
		this.isMove = false;
		mDir = Directions.DIR_NONE;
		mOldDir = Directions.DIR_EAST;
        mNextDir = Directions.DIR_NONE;
		mKX = 0;
		mKY = 0;
        mNextX = pX;
        mNextY = pY;
		mCurrentStepTime = 0;
		setAnimateDirection();
	}

	//-----------------------------
	//SUPER METHODS
	//-----------------------------

	@Override
	protected void onManagedUpdate(float pSecondsElapsed)
	{
        if(getX() != mNextX || getY() != mNextY)//Если есть куда двигаться, то будем двигать героя
        {
            if(SystemClock.elapsedRealtime() >= mCurrentStepTime + STEP_TIME)//Определяет скорость движения
            {
                //Debug.i("getX() = " + getX() + " , getY() = " + getY());
                //Debug.i("mNextX = " + mNextX + " , mNextY = " + mNextY);
                //Открываем неисследованные тайлы
                if(((getX() == mNextX + mKX * 32/2)||(getY() == mNextY + mKY * 32/2)) && WorldContext.getInstance().mSettings.isWarFog())
                {
                    int column = (int)getX()/32;
                    if(mKX == 1)
                        column += 1;
                    int row = (int)getY()/32;
                    if(mKY == 1)
                        row += 1;
                    WorldContext.getInstance().mWorld.mLevels.get(0).getTMXLayers().get(GameMap.LAYER_FLOOR).setVisibleTiles(column, row);
                    WorldContext.getInstance().mWorld.mLevels.get(0).getTMXLayers().get(GameMap.LAYER_WALLS).setVisibleTiles(column, row);
                    WorldContext.getInstance().mWorld.mLevels.get(0).getTMXLayers().get(GameMap.LAYER_ABOVE).setVisibleTiles(column, row);
                }

                float nextCoordX = getX() + mSpeed*mKX;
                float nextCoordY = getY() + mSpeed*mKY;
                setPosition(nextCoordX, nextCoordY);

                mCurrentStepTime = SystemClock.elapsedRealtime();
            }
        }
        else if(getX() == mNextX && getY() == mNextY && isMove)
        {
            Debug.i("Движение продолжается, посчитаем следующие тайлы");

            //Проверим, было ли изменение в движении
            if(mNextDir != Directions.DIR_NONE && mNextDir != mDir)
            {
                setNewDirection(mNextDir);
                animatePlayer();
            }

            checkFreeTile();
        }
        else if(!isMove)
        {
            //Если пришли на место, то остановим анимацию
            if(isAnimationRunning())
            {
                stopAnimation();
                setAnimateDirection();
                mNextX = getX();
                mNextY = getY();
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
	 * Анимируем персонаж согласно выбранному направлению
	 * */
	public void animatePlayer()
	{
		switch (mDir)
		{
			case DIR_EAST:
				this.animate(mCurrRun, 8, 15, true);//run right
				break;
			case DIR_WEST:
				this.animate(mCurrRun, 0, 7, true);//run left
				break;
			case DIR_SOUTH:
				this.animate(mCurrRun, 24, 31, true);//run down
				break;
			case DIR_NORTH:
				this.animate(mCurrRun, 16, 23, true);//run up
				break;
		}
	}

    /**
     * Проверка на проходимость тайла. Каждый раз после завершения хотьбы до следующего тайла.
     * */
    private void checkFreeTile()
    {
        int playerPosX, playerPosY;
        PhysicMapCell[][] mWalkable = WorldContext.getInstance().mWorld.mWakables.get(WorldContext.getInstance().mWorld.mCurrentLevel);

        //Определим координаты игрока
        playerPosX = (int)getX()/32;
        playerPosY = (int)getY()/32;
        Debug.i("Игрок стоит на месте в x = " + playerPosX + ", y = " + playerPosY);

        //проверим тайл, куда попасть пытаемся
        playerPosX += mKX;
        playerPosY += mKY;
        Debug.i("Проверяем проходимость для x = " + playerPosX + ", y = " + playerPosY);

        if(mWalkable[playerPosX][playerPosY].isWalkable && mWalkable[playerPosX][playerPosY].mIndexObject == -1)
        {
            //Продолжаем движение покарте
            mNextX = getX() + 32 * mKX;
            mNextY = getY() + 32 * mKY;
            Debug.i("mNextX = " + mNextX + ", mNextY = " + mNextY);

            Debug.i("Нет препятствий");
            setMove(true);
        }
        else if(!mWalkable[playerPosX][playerPosY].isWalkable)
        {
            mNextX = getX();
            mNextY = getY();
            Debug.i("Препятствие!!!!!");
            setMove(false);
        }
        else if(mWalkable[playerPosX][playerPosY].isWalkable && mWalkable[playerPosX][playerPosY].mIndexObject != -1)
        {
            Debug.i("Интерактивный объект!!!!!");
            if(WorldContext.getInstance().mObjectController.workWithObject(mWalkable[playerPosX][playerPosY].mIndexObject))
            {
                //Продолжаем движение покарте
                mNextX = getX() + 32 * mKX;
                mNextY = getY() + 32 * mKY;
                Debug.i("mNextX = " + mNextX + ", mNextY = " + mNextY);

                Debug.i("Есть объект, на него можно зайти");
                setMove(true);
            }
            else
            {
                mNextX = getX();
                mNextY = getY();
                Debug.i("Объект непроходим!!!!");
                setMove(false);
            }
        }
    }
	//-----------------------------
	//GETTERS/SETTERS
	//-----------------------------

	/**
	 * Меняем направление движения персонажа
     * @param dir - новое направление
	 * */
	public void setNewDirection(Directions dir)
	{
        Debug.i("Меняем направление движения");
        //Debug.i("getX() = " + getX() + " , mNextX = " + mNextX + " , getY() = " + getY() + " , mNextY = " + mNextY);
        if(getX() != mNextX || getY() != mNextY)
        {
            mNextDir = dir;
            Debug.i("Player: Еще движемся и потому запомним направление");
            Debug.i("Player: mNextDir = " + mNextDir);
        }
        else
        {
            mOldDir = mDir;
            mDir = dir;
            mNextDir = dir;

            if(dir == Directions.DIR_EAST)
            {
                Debug.i("Player: Восток");
                mKY = 0;
                mKX = 1;
            }
            else if(dir == Directions.DIR_WEST)
            {
                Debug.i("Player: Запад");
                mKY = 0;
                mKX = -1;
            }
            else if(dir == Directions.DIR_NORTH)
            {
                Debug.i("Player: Север");
                mKX = 0;
                mKY = -1;
            }
            else if(dir == Directions.DIR_SOUTH)
            {
                Debug.i("Player: Юг");
                mKX = 0;
                mKY = 1;
            }
            else if(dir == Directions.DIR_NONE)
            {
                Debug.i("Player: Без направления");
                mKX = 0;
                mKY = 0;
            }
        }
	}

    /**
     * Начинаем движение персонажа или заканчиваем его
     * @param isMove - Флаг движения персонажа, если false, то стоит на месте
     * */
    public void setMove(boolean isMove)
    {
        if(isMove)
        {
            Debug.i("Начали движение игрока");
            this.isMove = true;
            animatePlayer();
        }
        else
        {
            Debug.i("Стоп! Доходим до последней точки.");
            this.isMove = false;
        }
    }

    /**
     * Установка игрока на текущую позицию
     * @param x новая позиция игрока
     * @param y новая позиция игрока
     * */
    public void setPlayerPosition(float x, float y)
    {
        setY(y);
        setX(x);
        mNextX = x;
        mNextY = y;
    }

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
