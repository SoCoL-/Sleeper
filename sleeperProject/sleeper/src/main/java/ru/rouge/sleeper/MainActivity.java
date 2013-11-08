package ru.rouge.sleeper;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.KeyEvent;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

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
import org.andengine.util.debug.Debug;

import java.io.IOException;

import ru.rouge.sleeper.Managers.ResourceManager;
import ru.rouge.sleeper.Managers.ScenesManager;

public class MainActivity extends BaseGameActivity
{
    final static public int CAMERA_WIDTH = 800;
    final static public int CAMERA_HEIGHT = 480;

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

        EngineOptions eo = new EngineOptions(true, ScreenOrientation.LANDSCAPE_SENSOR, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
        eo.getRenderOptions().setDithering(true);
        eo.getRenderOptions().setMultiSampling(true);

		return eo;
	}

    private void loadSettings()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        WorldContext.getInstance().mSettings.setFastPlayer(preferences.getBoolean("FastPlayer", false));
        WorldContext.getInstance().mSettings.setWarFog(preferences.getBoolean("WarFog", true));
        WorldContext.getInstance().mSettings.setFPS(preferences.getBoolean("FPS", true));
        WorldContext.getInstance().mSettings.setDebugButton(preferences.getBoolean("DebugButton", false));
    }


    @Override
	public void onCreateResources(OnCreateResourcesCallback pOnCreateResourcesCallback) throws IOException
	{
        try
        {
		    ResourceManager.getInstance().setManager(getVertexBufferObjectManager());
		    WorldContext.getInstance().setWorldContext(this.mCamera, this.getTextureManager(), this, this.getEngine(), this.getFontManager(), this.getAssets());
            loadSettings();
        }
        catch(Exception e)
        {
            Debug.e(e);
        }
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

    @Override
    public void onResume()
    {
        super.onResume();
        checkForCrashes();
        checkForUpdates();
    }

    private void checkForCrashes()
    {
        //CrashManager.register(this, "b3cbd1392a0294f91925467d52cfe1b0");
    }

    private void checkForUpdates()
    {
        // Remove this for store builds!
        //UpdateManager.register(this, "b3cbd1392a0294f91925467d52cfe1b0");
    }
}
