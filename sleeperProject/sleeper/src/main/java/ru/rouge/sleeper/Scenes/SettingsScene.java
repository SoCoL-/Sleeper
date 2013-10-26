package ru.rouge.sleeper.Scenes;

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

    @Override
    public void createScene()
    {
        mPlayerSpeed = new CheckBox(50, 30, WorldContext.getInstance().mScreenWidth, 60, "Fast Player", ResourceManager.getInstance().mVBO)
        {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
            {
                if(pSceneTouchEvent.isActionDown())
                {
                    mPlayerSpeed.setCheck(!mPlayerSpeed.isCheck());
                    WorldContext.getInstance().mSettings.setFastPlayer(mPlayerSpeed.isCheck());
                }
                return true;
            }
        };

        mWarFog = new CheckBox(50, 80, WorldContext.getInstance().mScreenWidth, 60, "Fog of war", ResourceManager.getInstance().mVBO)
        {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
            {
                if(pSceneTouchEvent.isActionDown())
                {
                    mWarFog.setCheck(!mWarFog.isCheck());
                    WorldContext.getInstance().mSettings.setWarFog(mWarFog.isCheck());
                }
                return true;
            }
        };

        if(WorldContext.getInstance().mSettings.isFastPlayer())
            mPlayerSpeed.setCheck(true);
        if(WorldContext.getInstance().mSettings.isWarFog())
            mWarFog.setCheck(true);

        registerTouchArea(mPlayerSpeed);
        registerTouchArea(mWarFog);
        attachChild(mPlayerSpeed);
        attachChild(mWarFog);
    }

    @Override
    public void OnKeyBackPressed()
    {
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
        this.detachSelf();
    }
}
