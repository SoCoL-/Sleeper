package ru.rouge.sleeper;

import android.app.Activity;
import android.content.res.AssetManager;
import android.content.res.Resources;

import org.andengine.engine.Engine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.opengl.font.FontManager;
import org.andengine.opengl.texture.TextureManager;

import ru.rouge.sleeper.Controllers.PlayerControllers;
import ru.rouge.sleeper.Map.GameMap;
import ru.rouge.sleeper.Objects.Player;

/**
 * Created by 1 on 18.06.13. Eugene Savchik
 * В классе будут хранится все важные для игры переменные
 */
public final class WorldContext
{
	//---------------------------------------------
	// CONSTANTS
	//---------------------------------------------

	//---------------------------------------------
	// VARIABLES
	//---------------------------------------------

	private static WorldContext instance;

	private BoundCamera     mCamera;                    //Камера игры
	private TextureManager  mTextureManager;            //Менеджер текстур
	private Activity 		mContext;					//Контекст окна
	private Engine			mEngine;					//Указатель на движек
    private FontManager     mFontManager;               //Менеджер шрифтов
    private AssetManager    mAssetManager;              //Менеджер файлов в assets

	public Resources mResourceManager;					//Менеджер ресурсов фндроида
	public PlayerControllers mPlayerContr;

	public float mScreenWidth;                          //Текущая ширина экрана
	public float mScreenHeight;                         //Текущая высота экрана

	public GameMap mWorld;								//Весь мир игры =)
	public Player mPlayer;								//Игрок
	public boolean isNewGame = true;					//Создали новую или згрузили?

	//-----------------------------
	//CONSTRUCTORS
	//-----------------------------

	//---------------------------------------------
	// CLASS METHODS
	//---------------------------------------------

	/**
	 * Заполним контекст данными
	 * @param camera - Игровая камера
	 */
	public void setWorldContext(BoundCamera camera, TextureManager tm, Activity c, Engine e, FontManager fm, AssetManager am)
    {
        this.mCamera = camera;
		this.mTextureManager = tm;
		this.mContext = c;
		this.mEngine = e;
        this.mFontManager = fm;
        this.mAssetManager = am;

		this.mResourceManager = this.mContext.getResources();
		this.mPlayerContr = new PlayerControllers();
    }

	/**
	 * Освободим ресурсы иры
	 * */
	public void destroy()
	{
		this.mCamera = null;
        this.mTextureManager = null;
        this.mContext = null;
        this.mEngine = null;
        this.mFontManager = null;
        this.mAssetManager = null;

		if(instance != null)
			instance = null;
	}

    //---------------------------------------------
    // GETTERS/SETTERS
    //---------------------------------------------

	/**
	 * Метод получения игровой камеры
	 * @return - возвращает игровую камеру
	 */
    public BoundCamera getCamera()
    {
        return this.mCamera;
    }

	/**
	 * Возвращаем указатель на движок
	 * @return - указатель на движок
	 */
	public Engine getEngine()
	{
		return this.mEngine;
	}

	/**
	 * Возвращаем указатель на менеджер текстур
	 * @return - менеджер текстур или null
	 */
	public TextureManager getTextureManager()
	{
		return this.mTextureManager;
	}

	/**
	 * Возвращаем указатель на контекст окна
	 * @return - контекст окна или null
	 */
	public Activity getContext()
	{
		return mContext;
	}

    /**
     * Возвращает менеджер шрифтов
     * @return - менеджер шрифтов или null
     */
    public FontManager getFontManager()
    {
        return mFontManager;
    }

    /**
     * Возвращает менеджер ассетов
     * @return - менеджер ассетов или null
     */
    public AssetManager getAssetManager()
    {
        return this.mAssetManager;
    }

	/**
	 * Получение указателя на класс
	 * @return - возвращает указатель на класс
	 */
    public static WorldContext getInstance()
    {
        if(instance == null)
            instance = new WorldContext();

        return instance;
    }

	//-----------------------------
	//INNER CLASSES
	//-----------------------------
}
