package ru.rouge.sleeper.Objects.UI;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.debug.Debug;

import ru.rouge.sleeper.Managers.ResourceManager;
import ru.rouge.sleeper.Map.GameMap;
import ru.rouge.sleeper.WorldContext;

/**
 * Created by Evgenij on 07.11.13.
 * Графический вспомогательный интерфейс игры
 */
public class GameHUD extends HUD
{
    //-----------------------------
    //Constants
    //-----------------------------

    //-----------------------------
    //Variables
    //-----------------------------
    private boolean isShowWalls;            //Отображать слой стен или нет

    //-----------------------------
    //Ctors
    //-----------------------------

    public GameHUD()
    {
        isShowWalls = true;
    }

    //-----------------------------
    //Methods
    //-----------------------------

    /**
     * Добавляем к интерфейсу отладочную кнопку
     * */
    public void addDebugButton()
    {
        final Rectangle btnHud = new Rectangle(5, 5, 32, 32, ResourceManager.getInstance().mVBO)
        {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
            {
                Debug.i("Click by rectangle in the HUD");
                if(pSceneTouchEvent.isActionDown())
                {
                    Debug.i("Before change isShowWalls = " + isShowWalls);
                    isShowWalls = !isShowWalls;
                    WorldContext.getInstance().mWorld.mLevels.get(WorldContext.getInstance().mWorld.mCurrentLevel).getTMXLayers().get(GameMap.LAYER_WALLS).setVisible(isShowWalls);
                    Debug.i("After change isShowWalls = " + isShowWalls);
                }
                return true;
            }
        };

        registerTouchArea(btnHud);
        attachChild(btnHud);
    }

    /**
     * Добавляем к интерфейсу счетчик кадров всекунду и текущий уровень
     * */
    public void addFPS()
    {
        final Text mTextFPS = new Text(50, 5, ResourceManager.getInstance().mGameFont, "FPS: , ", "FPS: XXXXX, Level: X".length(), ResourceManager.getInstance().mVBO);
        attachChild(mTextFPS);
        registerUpdateHandler(new TimerHandler(1 / 20.0f, true, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                mTextFPS.setText("FPS: " + String.format("%.2f", WorldContext.getInstance().mFPSCounter.getFPS()) + ", Level: " + WorldContext.getInstance().mWorld.mCurrentLevel);
            }
        }));
    }

    public void addBackground()
    {
        this.attachChild(new Sprite(0, 0, 500, 98, ResourceManager.getInstance().mHUDBackground, ResourceManager.getInstance().mVBO));
    }

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
