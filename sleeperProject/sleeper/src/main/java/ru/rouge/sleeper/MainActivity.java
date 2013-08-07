package ru.rouge.sleeper;

import android.view.KeyEvent;

import org.andengine.engine.Engine;
import org.andengine.engine.LimitedFPSEngine;
import org.andengine.engine.camera.BoundCamera;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.util.FPSLogger;
import org.andengine.ui.activity.BaseGameActivity;

import java.io.IOException;

import ru.rouge.sleeper.Managers.ResourceManager;
import ru.rouge.sleeper.Managers.ScenesManager;

public class MainActivity extends BaseGameActivity
{
    final static private int CAMERA_WIDTH = 800;
    final static private int CAMERA_HEIGHT = 480;

	private BoundCamera mCamera;

	@Override
	public Engine onCreateEngine(EngineOptions pEngineOptions)
	{
		return new LimitedFPSEngine(pEngineOptions, 60);
	}

	@Override
	public EngineOptions onCreateEngineOptions()
	{
		//float width = getResources().getDisplayMetrics().widthPixels;
		//float height = getResources().getDisplayMetrics().heightPixels;
		//WorldContext.getInstance().mScreenWidth = width;
		//WorldContext.getInstance().mScreenHeight = height;
        WorldContext.getInstance().mScreenWidth = CAMERA_WIDTH;
        WorldContext.getInstance().mScreenHeight = CAMERA_HEIGHT;

		this.mCamera = new BoundCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		return new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
	}

	@Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException
	{
		ResourceManager.getInstance().setManager(getVertexBufferObjectManager());
		WorldContext.getInstance().setWorldContext(this.mCamera, this.getTextureManager(), this, this.getEngine(), this.getFontManager(), this.getAssets());
		pOnCreateResourcesCallback.onCreateResourcesFinished();
	}

	@Override
	public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws IOException
	{
		this.mEngine.registerUpdateHandler(new FPSLogger());
		ScenesManager.getInstance().setSplash(pOnCreateSceneCallback);
	}

	@Override
	public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws IOException
	{
		mEngine.registerUpdateHandler(new TimerHandler(2f, new ITimerCallback()
		{
			public void onTimePassed(final TimerHandler pTimerHandler)
			{
				mEngine.unregisterUpdateHandler(pTimerHandler);
				ScenesManager.getInstance().setMenuScene();
			}
		}));
		pOnPopulateSceneCallback.onPopulateSceneFinished();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if(keyCode == KeyEvent.KEYCODE_BACK)
		{
			ScenesManager.getInstance().getCurrentScene().OnKeyBackPressed();
		}

		return false;
	}
}
