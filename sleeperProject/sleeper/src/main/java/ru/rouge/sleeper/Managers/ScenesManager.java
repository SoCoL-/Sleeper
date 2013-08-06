package ru.rouge.sleeper.Managers;

import org.andengine.ui.IGameInterface;
import org.andengine.util.debug.Debug;

import ru.rouge.sleeper.Scenes.IntroScene;
import ru.rouge.sleeper.Scenes.LoadScene;
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
		ResourceManager.getInstance().unloadSplashRes();
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

	public void setScene(SceneTypes type)
	{
		switch(type)
		{
			case SCENE_MENU:
				setScene(mMenuScene);
				break;
			case SCENE_LOAD:
				setScene(mLoadScene);
				break;
			case SCENE_GAME:
				setScene(mMainGameScene);
				break;
		}
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
		setScene(SceneTypes.SCENE_MENU);
		disposeSplash();
	}

	public void setMainGameScene()
	{
		mLoadScene = new LoadScene();
		setScene(SceneTypes.SCENE_LOAD);
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
