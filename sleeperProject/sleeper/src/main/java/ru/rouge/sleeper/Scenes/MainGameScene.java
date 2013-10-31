package ru.rouge.sleeper.Scenes;

import org.andengine.engine.camera.hud.HUD;
import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.entity.util.FPSCounter;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.debug.Debug;
import org.andengine.util.color.Color;

import ru.rouge.sleeper.MainActivity;
import ru.rouge.sleeper.Managers.ResourceManager;
import ru.rouge.sleeper.Managers.ScenesManager;
import ru.rouge.sleeper.Map.GameMap;
import ru.rouge.sleeper.Objects.BaseObject;
import ru.rouge.sleeper.Objects.Door;
import ru.rouge.sleeper.Objects.Stair;
import ru.rouge.sleeper.WorldContext;

/**
 * Created by Masa on 21.06.13.
 * Отрисовка самой игры
 */
public final class MainGameScene extends MainScene
{

    private boolean isShowWalls = true;             //Для отладки
    public HUD mHUD;

	@Override
    public void createScene()
    {
		Debug.e("Create MainGame");
        setBackground(new Background(Color.BLACK));
		Debug.e("Set background");
		if(WorldContext.getInstance() == null)
			Debug.e("WorldContext.getInstance() == null Oo");

        mHUD = new HUD();

		setOnSceneTouchListener(new IOnSceneTouchListener()
		{
			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
			{
                WorldContext.getInstance().mPlayerContr.move(pSceneTouchEvent);
				return true;
			}
		});

		showWorld();
    }

	public void showWorld()
	{
        final int currLevel = WorldContext.getInstance().mWorld.mCurrentLevel;
        Debug.e("Уровень подземелья = " + currLevel);
        try
        {
		Debug.e("Check world");
		if(WorldContext.getInstance().mWorld != null)
		{
            attachChild(WorldContext.getInstance().mWorld.mLevels.get(currLevel).getTMXLayers().get(GameMap.LAYER_FLOOR));
            attachChild(WorldContext.getInstance().mWorld.mLevels.get(currLevel).getTMXLayers().get(GameMap.LAYER_WALLS));
            for(BaseObject d : WorldContext.getInstance().mWorld.mObjects.get(currLevel))
            {
                if(!(d instanceof Door))
                    attachChild(d);
            }
			attachChild(WorldContext.getInstance().mPlayer);
            for(BaseObject d : WorldContext.getInstance().mWorld.mObjects.get(currLevel))
                if(d instanceof Door)
                    attachChild(d);

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
                        WorldContext.getInstance().mWorld.mLevels.get(0).getTMXLayers().get(GameMap.LAYER_WALLS).setVisible(isShowWalls);
                        Debug.i("After change isShowWalls = " + isShowWalls);
                    }

                    //return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
                    return true;
                }
            };

            mHUD.registerTouchArea(btnHud);
            mHUD.attachChild(btnHud);

            //Test add text to HUD. Need Class!!!
            WorldContext.getInstance().mFPSCounter = new FPSCounter();
            WorldContext.getInstance().getEngine().registerUpdateHandler(WorldContext.getInstance().mFPSCounter);

            final Text mTextFPS = new Text(50, 5, ResourceManager.getInstance().mGameFont, "FPS: , ", "FPS: XXXXX, X".length(), ResourceManager.getInstance().mVBO);
            mHUD.attachChild(mTextFPS);
            registerUpdateHandler(new TimerHandler(1 / 20.0f, true, new ITimerCallback()
            {
                @Override
                public void onTimePassed(TimerHandler pTimerHandler)
                {
                    mTextFPS.setText("FPS: " + String.format("%.2f", WorldContext.getInstance().mFPSCounter.getFPS()) + ", " + currLevel);
                }
            }));

            Debug.e("Set HUD");
            WorldContext.getInstance().getCamera().setHUD(mHUD);
		}
		else
			Debug.e("world is null");
        }
        catch (Exception e)
        {
            Debug.e(e);
        }
	}

    @Override
    public void OnKeyBackPressed()
    {
        //Возврат в игровое меню
        Debug.e("back press");
        try
        {
            ScenesManager.getInstance().setMenuScene();
        }
        catch (Exception e)
        {
            Debug.e(e);
        }
        Debug.e("back press done");
    }

    @Override
    public ScenesManager.SceneTypes getSceneType()
    {
        return ScenesManager.SceneTypes.SCENE_GAME;
    }

    @Override
    public void dispposeScene()
    {
		Debug.e("on MainGameScene dispose scene");
        ResourceManager.getInstance().unloadGameRes();
		detachChild(WorldContext.getInstance().mPlayer);
        detachChildren();
        WorldContext.getInstance().mWorld.mLevels.clear();
        WorldContext.getInstance().mWorld.mLevels = null;
        WorldContext.getInstance().mWorld = null;
        WorldContext.getInstance().getCamera().setHUD(null);
        WorldContext.getInstance().getCamera().setBoundsEnabled(false);
        WorldContext.getInstance().getCamera().clearUpdateHandlers();
        WorldContext.getInstance().getCamera().setChaseEntity(null);
        WorldContext.getInstance().getCamera().setCenter(MainActivity.CAMERA_WIDTH/2, MainActivity.CAMERA_HEIGHT/2);
		this.detachSelf();
		this.dispose();
        Debug.e("MainGameScene: after disposing");
    }
}
