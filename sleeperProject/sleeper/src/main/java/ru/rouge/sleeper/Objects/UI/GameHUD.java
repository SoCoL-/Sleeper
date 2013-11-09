package ru.rouge.sleeper.Objects.UI;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.background.EntityBackground;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.debug.Debug;

import ru.rouge.sleeper.Managers.ResourceManager;
import ru.rouge.sleeper.Map.GameMap;
import ru.rouge.sleeper.Utils.Views.ProgressView;
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
    private WorldContext mWC;               //Игровой контекст
    private int mWidth;                     //Ширина подложки
    private int mHeight;                    //Высота подложки

    //-----------------------------
    //Ctors
    //-----------------------------

    public GameHUD()
    {
        isShowWalls = true;
        mWC = WorldContext.getInstance();
    }

    //-----------------------------
    //Methods
    //-----------------------------

    /**
     * Добавление нужных элементов в интерфейс и расчет высоты и ширины его
     * */
    public void initHUD()
    {
        addCommonUI();
        if(mWC.mSettings.isDebugButton())
            addDebugButton();
        if(mWC.mSettings.isFPS())
            addFPS();

        addBackground();
    }

    /**
     * Добавляем основной интерфейс
     * */
    private void addCommonUI()
    {
        mHeight = 37;
        mWidth = 370;

        final Rectangle btnInventory = new Rectangle(5, 5, 32, 32, ResourceManager.getInstance().mVBO)
        {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
            {
                Debug.i("Click by rectangle in the HUD");
                if(pSceneTouchEvent.isActionDown())
                {
                    //TODO Открыть сцену с инвентарем
                    Debug.i("Open Inventory of Player");
                }
                return true;
            }
        };
        btnInventory.setZIndex(10);

        registerTouchArea(btnInventory);
        attachChild(btnInventory);

        final ProgressView health = new ProgressView(60, 5, 200, 16, ResourceManager.getInstance().mVBO);
        health.setMaxProgress(30);
        health.setCurrentProgress(25);
        health.setProgressColor(255, 0, 0, 255);
        health.setBackgroundColor(0, 0, 0, 255);
        health.setZIndex(10);
        this.attachChild(health);

        final ProgressView mana = new ProgressView(60, 26, 200, 16, ResourceManager.getInstance().mVBO);
        mana.setMaxProgress(30);
        mana.setCurrentProgress(30);
        mana.setProgressColor(0, 0, 255, 255);
        mana.setBackgroundColor(0, 0, 0, 255);
        mana.setZIndex(10);
        this.attachChild(mana);

        final Text mTextFPS = new Text(275, 7, ResourceManager.getInstance().mGameFont, "Level: ", "Level: X".length(), ResourceManager.getInstance().mVBO);
        attachChild(mTextFPS);
        registerUpdateHandler(new TimerHandler(1 / 20.0f, true, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                mTextFPS.setText("Level: " + WorldContext.getInstance().mWorld.mCurrentLevel);
            }
        }));
    }

    /**
     * Добавляем к интерфейсу отладочную кнопку
     * */
    private void addDebugButton()
    {
        mHeight = 79;
        final Rectangle btnHud = new Rectangle(5, 40, 32, 32, ResourceManager.getInstance().mVBO)
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
        btnHud.setZIndex(10);

        registerTouchArea(btnHud);
        attachChild(btnHud);
    }

    /**
     * Добавляем к интерфейсу счетчик кадров всекунду и текущий уровень
     * */
    private void addFPS()
    {
        mWidth = 400;
        final Text mTextFPS = new Text(275, 7 + 7 + 32 , ResourceManager.getInstance().mGameFont, "FPS: ", "FPS: XXXXX".length(), ResourceManager.getInstance().mVBO);
        attachChild(mTextFPS);
        registerUpdateHandler(new TimerHandler(1 / 20.0f, true, new ITimerCallback()
        {
            @Override
            public void onTimePassed(TimerHandler pTimerHandler)
            {
                mTextFPS.setText("FPS: " + String.format("%.2f", WorldContext.getInstance().mFPSCounter.getFPS()));
            }
        }));
    }

    private void addBackground()
    {
        Sprite bgrd = new Sprite(0, 0, mWidth, mHeight, ResourceManager.getInstance().mHUDBackground, ResourceManager.getInstance().mVBO);
        bgrd.setZIndex(0);
        this.attachChild(bgrd);
        this.sortChildren();
    }

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
