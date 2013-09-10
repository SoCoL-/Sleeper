package ru.rouge.sleeper.Objects;

import android.os.SystemClock;

import org.andengine.entity.text.Text;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import ru.rouge.sleeper.Managers.ResourceManager;
import ru.rouge.sleeper.Managers.ScenesManager;
import ru.rouge.sleeper.Map.GameMap;
import ru.rouge.sleeper.Scenes.MainGameScene;
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

	final private long STEP_TIME = 10;					//Время одного шага в миллисекундах
	final private int NEXT_DESTINATION_TILE_WIDTH;		//Размер пути( = ширина тайла) на который надо переместиться за один раз
	//Время анимации каждого кадра бега в одном направлении
	final private long[] ANIM_TIMINGS_RUN = {130, 130, 130, 130, 130, 130, 130, 130};

	//-----------------------------
	//VARIABLES
	//-----------------------------

	private Directions mDir;							//Направление движения персонажа
	private Directions mOldDir;							//Старое направление движения
	public boolean isMove;								//Движется ли персонаж
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
            if(SystemClock.elapsedRealtime() >= mCurrentStepTime + STEP_TIME)//Определяет скорость движения
			{
				if(mLength <= 0)
				{
					//Debug.e("Stop move!");
                    if(isMoveLoop)
                    {
                        //Debug.i("isMoveLoop = " + isMoveLoop);
                        boolean isGo = isCanGo();
                        if(isGo)
                        {
                            //Debug.e("Resume move!");
                            if(mOldDir != mDir)
                                animatePlayer();

                            mLength = NEXT_DESTINATION_TILE_WIDTH;
                        }
                        else
                        {
                           // Debug.e("Not Resume move!");
                            isMove = false;
                            isMoveLoop = false;
                            mLength = 0;
                            mOldDir = mDir;
                            mDir = Directions.DIR_NONE;
                            mKX = 0;
                            mKY = 0;
                            stopAnimation();
                            setAnimateDirection();
                            return;
                        }
                    }
					else
					{
						//Debug.e("Stop animation!!!!!!");
						isMove = false;
						mOldDir = mDir;
						mDir = Directions.DIR_NONE;
                        mKX = 0;
                        mKY = 0;
						stopAnimation();
						setAnimateDirection();
                        return;
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
	 * Можно ли пройти на тайл по координатам
	 * @return true - можно идти, иначе нет
	 * */
	private boolean isCanGo()
	{
		float x, y;
		int kx = 0, ky = 0;

		Debug.e("isCanGo");
		if(mDir == Directions.DIR_EAST)
		{
			ky = 0;
			kx = 1;
		}
		else if(mDir == Directions.DIR_NORTH)
		{
			kx = 0;
			ky = -1;
		}
		else if(mDir == Directions.DIR_SOUTH)
		{
			kx = 0;
			ky = 1;
		}
		else if(mDir == Directions.DIR_WEST)
		{
			ky = 0;
			kx = -1;
		}

		Debug.e("kx = " + kx + ", ky = " + ky);
		Debug.e("PlayerX = " + getX() + ", PlayerY = " + getY());

		x = getX() + 32 * kx;
		y = getY() + 32 * ky;
		Debug.e("x = " + x + ", y = " + y);

		GameMap gm = WorldContext.getInstance().mWorld;
        float[] coord = gm.mTMXMap.getTMXLayers().get(GameMap.LAYER_FLOOR).convertSceneToLocalCoordinates(getX(), getY());
        Debug.e("coord[x] = " + coord[0] + ", coord[y] = " + coord[1]);
		int TileColumn = (int) x / 32;
		int TileRow = (int)y / 32;
		Debug.e("TileColumn = " + TileColumn + ", TileRow = " + TileRow);
		Debug.e("gm.mWakables[TileRow][TileColumn] = " + gm.mWakables[TileRow][TileColumn].isWalkable);
        Debug.e("gm.mWakables[TileRow][TileColumn].index = " + gm.mWakables[TileRow][TileColumn].mIndexObject);

        boolean isWalk = false;

        if(gm.mWakables[TileRow][TileColumn].mIndexObject == -1)
        {
            Debug.i("Нет объектов на пути, возвращаем значение карты проходимости");
            isWalk = gm.mWakables[TileRow][TileColumn].isWalkable;
        }
        else if(gm.mWakables[TileRow][TileColumn].mIndexObject > -1)
        {
            Debug.i("Object!!! Try to identify it");
            Door d = gm.mDoors.get(gm.mWakables[TileRow][TileColumn].mIndexObject);
            Debug.i("This is door");
            if(d.isOpen())
            {
                Debug.i("Door is Open, move enable");
                isWalk = true;
            }
            else
            {
                if(!d.isLocked())
                {
                    Debug.i("Door is Closed, move disable, open door");
                    d.setOpen(true);
                    isWalk = false;
                }
                else
                {
                    Debug.i("Door is Locked, move disable, can't open door");
                    Text errorText = new Text(0, 10, ResourceManager.getInstance().mGameFont, "This door is locked!!", ResourceManager.getInstance().mVBO);
                    ((MainGameScene)ScenesManager.getInstance().getCurrentScene()).mHUD.attachChild(errorText);
                }
            }
        }

        return  isWalk;
	}

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
		//Debug.e("Calculate animation direction");
		this.isMove = true;

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
	 * Меняем направление движения персонажа
	 * */
	public void setNewDirection(Directions dir)
	{
		if(mDir == Directions.DIR_NONE)
			isMove = true;

		mOldDir = mDir;
		mDir = dir;
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
