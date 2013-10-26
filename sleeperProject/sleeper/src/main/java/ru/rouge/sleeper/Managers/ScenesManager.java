package ru.rouge.sleeper.Managers;

import org.andengine.ui.IGameInterface;
import org.andengine.util.debug.Debug;

import ru.rouge.sleeper.Scenes.IntroScene;
import ru.rouge.sleeper.Scenes.LoadScene;
import ru.rouge.sleeper.Scenes.MainGameScene;
import ru.rouge.sleeper.Scenes.MainScene;
import ru.rouge.sleeper.Scenes.SceneMenu;
import ru.rouge.sleeper.Scenes.SettingsScene;
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
        SCENE_SETTINGS,
	}

	//-----------------------------
	//VARIABLES
	//-----------------------------

	private static ScenesManager instance;

	private MainScene mMenuScene;
	private MainScene mSplashScreen;
    private MainScene mLoadScene;
    private MainScene mMainGameScene;
    private MainScene mSettingsScene;

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
            Debug.i("Try to dispose mSplashScreen");
            ResourceManager.getInstance().unloadSplashRes();
            mSplashScreen.dispposeScene();
            mSplashScreen = null;
        }
    }

    public void disposeMenuScene()
    {
        Debug.i("Try to dispose mMenuScene");
        if(mMenuScene != null)
        {
            mMenuScene.dispposeScene();
            mMenuScene = null;
        }
    }

    public void disposeLoadScene()
    {
        Debug.i("Try to dispose mLoadScene");
        if(mLoadScene != null)
        {
            mLoadScene.dispposeScene();
            mLoadScene = null;
        }
    }

    public void disposeMainGameScene()
    {
        Debug.i("Try to dispose mMainGameScene");
        if(mMainGameScene != null)
        {
            mMainGameScene.dispposeScene();
            mMainGameScene = null;
        }
    }

    public void disposeSettingsScene()
    {
        Debug.i("Try to dispose mSettingsScene");
        if(mSettingsScene != null)
        {
            mSettingsScene.dispposeScene();
            mSettingsScene = null;
        }
    }

    /**
	 * Освобождение занятых ресурсов
	 */
	public void freeResources()
	{
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
        Debug.i("Create and show mMenuScene");
        mMenuScene = new SceneMenu();
        Debug.i("Set scene to engine");
        setScene(mMenuScene);
        Debug.i("disposing");
        disposeSplash();
        disposeMainGameScene();
        disposeSettingsScene();
        Debug.i("end show mMenuScene");
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

    public void setSettingsScene()
    {
        mSettingsScene = new SettingsScene();
        setScene(mSettingsScene);
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
