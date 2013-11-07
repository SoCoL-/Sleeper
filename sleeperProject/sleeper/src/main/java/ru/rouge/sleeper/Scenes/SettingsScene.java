package ru.rouge.sleeper.Scenes;

import android.content.Context;
import android.content.SharedPreferences;

import org.andengine.input.touch.TouchEvent;
import org.andengine.util.adt.pool.RunnablePoolItem;

import ru.rouge.sleeper.Managers.ResourceManager;
import ru.rouge.sleeper.Managers.ScenesManager;
import ru.rouge.sleeper.Utils.Views.CheckBox;
import ru.rouge.sleeper.WorldContext;

/**
 * Created by 1 on 21.10.13.
 * Окно настроек
 */
public class SettingsScene extends MainScene
{
    private CheckBox mPlayerSpeed, mWarFog, mDebug, mFps;
    private WorldContext mWContext;

    @Override
    public void createScene()
    {
        mWContext = WorldContext.getInstance();

        mPlayerSpeed = new CheckBox(50, 30, mWContext.mScreenWidth, 60, "Fast Player", ResourceManager.getInstance().mVBO)
        {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
            {
                if(pSceneTouchEvent.isActionDown())
                {
                    mPlayerSpeed.setCheck(!mPlayerSpeed.isCheck());
                    mWContext.mSettings.setFastPlayer(mPlayerSpeed.isCheck());
                }
                return true;
            }
        };

        mWarFog = new CheckBox(50, 80, mWContext.mScreenWidth, 60, "Fog of war", ResourceManager.getInstance().mVBO)
        {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
            {
                if(pSceneTouchEvent.isActionDown())
                {
                    mWarFog.setCheck(!mWarFog.isCheck());
                    mWContext.mSettings.setWarFog(mWarFog.isCheck());
                }
                return true;
            }
        };

        mFps = new CheckBox(50, 130, mWContext.mScreenWidth, 60, "FPS", ResourceManager.getInstance().mVBO)
        {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
            {
                if(pSceneTouchEvent.isActionDown())
                {
                    mFps.setCheck(!mFps.isCheck());
                    mWContext.mSettings.setFPS(mFps.isCheck());
                    if(mFps.isCheck())
                        mWContext.mWorld.mHUD.addFPS();
                }
                return true;
            }
        };

        mDebug = new CheckBox(50, 180, mWContext.mScreenWidth, 60, "DebugButton", ResourceManager.getInstance().mVBO)
        {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
            {
                if(pSceneTouchEvent.isActionDown())
                {
                    mDebug.setCheck(!mDebug.isCheck());
                    mWContext.mSettings.setDebugButton(mDebug.isCheck());
                    if(mDebug.isCheck())
                        mWContext.mWorld.mHUD.addDebugButton();
                }
                return true;
            }
        };

        if(mWContext.mSettings.isFastPlayer())
            mPlayerSpeed.setCheck(true);
        if(mWContext.mSettings.isWarFog())
            mWarFog.setCheck(true);
        if(mWContext.mSettings.isFPS())
            mFps.setCheck(true);
        if(mWContext.mSettings.isDebugButton())
            mDebug.setCheck(true);

        registerTouchArea(mPlayerSpeed);
        registerTouchArea(mWarFog);
        registerTouchArea(mFps);
        registerTouchArea(mDebug);
        attachChild(mPlayerSpeed);
        attachChild(mWarFog);
        attachChild(mFps);
        attachChild(mDebug);
    }

    private void saveSettings()
    {
        SharedPreferences preferences = mWContext.getContext().getSharedPreferences("GameSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit= preferences.edit();

        edit.putBoolean("FastPlayer", mWContext.mSettings.isFastPlayer());
        edit.putBoolean("WarFog", mWContext.mSettings.isWarFog());
        edit.putBoolean("FPS", mWContext.mSettings.isFPS());
        edit.putBoolean("DebugButton", mWContext.mSettings.isDebugButton());
        edit.commit();
    }

    @Override
    public void OnKeyBackPressed()
    {
        //Сохраним настройки
        saveSettings();

        //Возврат в игровое меню
        ScenesManager.getInstance().setMenuScene();
    }

    @Override
    public ScenesManager.SceneTypes getSceneType()
    {
        return ScenesManager.SceneTypes.SCENE_SETTINGS;
    }

    @Override
    public void dispposeScene()
    {
        mWContext.getEngine().runOnUpdateThread(new Runnable()
        {
            @Override
            public void run()
            {
                detachChildren();
                detachSelf();
            }
        });
    }
}
