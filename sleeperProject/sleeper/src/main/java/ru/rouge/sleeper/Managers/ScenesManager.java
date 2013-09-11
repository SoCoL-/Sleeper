package ru.rouge.sleeper.Managers;

import org.andengine.ui.IGameInterface;
import org.andengine.util.debug.Debug;

import ru.rouge.sleeper.Scenes.IntroScene;
import ru.rouge.sleeper.Scenes.LoadScene;
import ru.rouge.sleeper.Scenes.MainGameScene;
import ru.rouge.sleeper.Scenes.MainScene;
import ru.rouge.sleeper.Scenes.SceneMenu;
import ru.rouge.sleeper.WorldContext;

/**
 * Created by 1 on 18.06.13.
 * Работа со всевозможными сценами
 */
public final class ScenesManager
{
    //-----------------------------
	//CONSTANTS
	//-----------------------------

	public enum SceneTypes
	{
		SCENE_SPLASH,
		SCENE_MENU,
		SCENE_LOAD,
		SCENE_GAME,
	}

	//-----------------------------
	//VARIABLES
	//-----------------------------

	private static ScenesManager instance;

	private MainScene mMenuScene;
	private MainScene mSplashScreen;
    private MainScene mLoadScene;
    private MainScene mMainGameScene;

	private SceneTypes mCurrentSceneType;
	private MainScene mCurrentScene;

	//-----------------------------
	//CONSTRUCTORS
	//-----------------------------

	//-----------------------------
	//CLASS METHODS
	//-----------------------------

	public void disposeSplash()
	{
        if(mSplashScreen != null)
        {
            ResourceManager.getInstance().unloadSplashRes();
            //mSplashScreen.detachSelf();
            mSplashScreen.dispposeScene();
            mSplashScreen = null;
        }
    }

    public void disposeMenuScene()
    {
        if(mMenuScene != null)
        {
            //mMenuScene.detachSelf();
            mMenuScene.dispposeScene();
            mMenuScene = null;
        }
    }

    public void disposeLoadScene()
    {
        if(mLoadScene != null)
        {
            //mLoadScene.detachSelf();
            mLoadScene.dispposeScene();
            mLoadScene = null;
        }
    }

    public void disposeMainGameScene()
    {
        if(mMainGameScene != null)
        {
            //mMainGameScene.detachSelf();
            mMainGameScene.dispposeScene();
            mMainGameScene = null;
        }
    }

    /**
	 * Освобождение занятых ресурсов
	 */
	public void freeResources()
	{
		//TODO Тут освободим занятые ресурсы

		if(instance != null)
			instance = null;
	}

	//-----------------------------
	//GETTERS/SETTERS
	//-----------------------------

	public void setScene(MainScene scene)
	{
		Debug.e("Set new scene");
		WorldContext.getInstance().getEngine().setScene(scene);
		Debug.e("Setted new scene with type = " + scene.getSceneType());
		this.mCurrentScene = scene;
		this.mCurrentSceneType = scene.getSceneType();
	}

	public void setSplash(IGameInterface.OnCreateSceneCallback i)
	{
		ResourceManager.getInstance().loadSplashRes();
		mSplashScreen = new IntroScene();
		mCurrentScene = mSplashScreen;
		i.onCreateSceneFinished(mSplashScreen);
	}

	public void setMenuScene()
	{
        mMenuScene = new SceneMenu();
        setScene(mMenuScene);
        disposeSplash();
        disposeMainGameScene();
    }

	public void setLoadScene()
	{
		mLoadScene = new LoadScene();
        setScene(mLoadScene);
        disposeMenuScene();
	}

    public void setGameScene()
    {
        mMainGameScene = new MainGameScene();
        setScene(mMainGameScene);
        disposeLoadScene();
    }

	public MainScene getCurrentScene()
	{
		return this.mCurrentScene;
	}

	/**
	 * Получение указателя на класс
	 * @return - возвращает указатель на класс
	 */
	public static ScenesManager getInstance()
	{
		if(instance == null)
			instance = new ScenesManager();
		return instance;
	}

	//-----------------------------
	//INNER CLASSES
	//-----------------------------
}
