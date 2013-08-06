package ru.rouge.sleeper.Scenes;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;

import ru.rouge.sleeper.Managers.ResourceManager;
import ru.rouge.sleeper.Managers.ScenesManager;
import ru.rouge.sleeper.WorldContext;

/**
 * Created by 1 on 19.06.13.
 * Будет показывать лого всех, кто учавствовал в создании игры
 */
public final class IntroScene extends MainScene
{
	//-----------------------------
	//CONSTANTS
	//-----------------------------

	//-----------------------------
	//VARIABLES
	//-----------------------------

	private Sprite mSplash;

	//-----------------------------
	//CONSTRUCTORS
	//-----------------------------

	//-----------------------------
	//CLASS METHODS
	//-----------------------------

	@Override
	public void createScene()
	{
		WorldContext wc = WorldContext.getInstance();
		ResourceManager rm = ResourceManager.getInstance();
		mSplash = new Sprite(wc.mScreenWidth/2 - rm.mSplashRegion.getWidth()/2, wc.mScreenHeight/2 - rm.mSplashRegion.getHeight()/2, rm.mSplashRegion.getWidth(), rm.mSplashRegion.getHeight(), rm.mSplashRegion, rm.mVBO)
		{
			protected void preDraw(GLState glState, Camera c)
			{
				super.preDraw(glState, c);
				glState.enableDither();
			}
		};

        //mSplash.setScale(1.5f);
        //mSplash.setPosition(WorldContext.getInstance().mScreenWidth/2, WorldContext.getInstance().mScreenHeight/2);
        attachChild(mSplash);
	}

	@Override
	public void OnKeyBackPressed()
	{

	}

	@Override
	public ScenesManager.SceneTypes getSceneType()
	{
		return ScenesManager.SceneTypes.SCENE_SPLASH;
	}

	@Override
	public void dispposeScene()
	{
		mSplash.detachSelf();
		mSplash.dispose();
		this.detachSelf();
		this.dispose();
	}

	//-----------------------------
	//GETTERS/SETTERS
	//-----------------------------

	//-----------------------------
	//INNER CLASSES
	//-----------------------------
}
