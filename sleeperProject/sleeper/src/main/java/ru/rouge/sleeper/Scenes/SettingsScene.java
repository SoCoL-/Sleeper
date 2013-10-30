package ru.rouge.sleeper.Scenes;

import android.content.Context;
import android.content.SharedPreferences;

import org.andengine.input.touch.TouchEvent;

import ru.rouge.sleeper.Managers.ResourceManager;
import ru.rouge.sleeper.Managers.ScenesManager;
import ru.rouge.sleeper.Settings;
import ru.rouge.sleeper.Utils.Views.CheckBox;
import ru.rouge.sleeper.WorldContext;

/**
 * Created by 1 on 21.10.13.
 * Окно настроек
 */
public class SettingsScene extends MainScene
{
    private CheckBox mPlayerSpeed, mWarFog;
    private WorldContext mWContext;

    @Override
    public void createScene()
    {
        mWContext = WorldContext.getInstance();

        loadSettings();

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

        if(mWContext.mSettings.isFastPlayer())
            mPlayerSpeed.setCheck(true);
        if(mWContext.mSettings.isWarFog())
            mWarFog.setCheck(true);

        registerTouchArea(mPlayerSpeed);
        registerTouchArea(mWarFog);
        attachChild(mPlayerSpeed);
        attachChild(mWarFog);
    }

    private void loadSettings()
    {
        SharedPreferences preferences = mWContext.getContext().getSharedPreferences("GameSettings", Context.MODE_PRIVATE);

        mWContext.mSettings.setFastPlayer(preferences.getBoolean("FastPlayer", false));
        mWContext.mSettings.setWarFog(preferences.getBoolean("WarFog", true));
    }

    private void saveSettings()
    {
        SharedPreferences preferences = mWContext.getContext().getSharedPreferences("GameSettings", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit= preferences.edit();

        edit.putBoolean("FastPlayer", mWContext.mSettings.isFastPlayer());
        edit.putBoolean("WarFog", mWContext.mSettings.isWarFog());
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
        this.detachChildren();
        this.detachSelf();
    }
}
