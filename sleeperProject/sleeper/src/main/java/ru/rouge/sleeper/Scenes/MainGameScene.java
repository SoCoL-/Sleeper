package ru.rouge.sleeper.Scenes;

import android.view.MotionEvent;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.debug.Debug;
import org.andengine.util.color.Color;

import ru.rouge.sleeper.Managers.ResourceManager;
import ru.rouge.sleeper.Managers.ScenesManager;
import ru.rouge.sleeper.WorldContext;

/**
 * Created by Masa on 21.06.13.
 * Отрисовка самой игры
 */
public final class MainGameScene extends MainScene
{
	final long TIME_LONG_TOUCH = 600;

	private float mTouchX = 0, mTouchY = 0, mTouchOffsetX = 0, mTouchOffsetY = 0;
	private long timeToTouch;														//Для создания longPress

	@Override
    public void createScene()
    {
		Debug.e("Create MainGame");
        setBackground(new Background(Color.BLACK));
		Debug.e("Set background");
		if(WorldContext.getInstance() == null)
			Debug.e("WorldContext.getInstance() == null Oo");
		setOnSceneTouchListener(new IOnSceneTouchListener()
		{
			@Override
			public boolean onSceneTouchEvent(Scene pScene, TouchEvent pSceneTouchEvent)
			{
				if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
				{
					mTouchX = pSceneTouchEvent.getMotionEvent().getX();
					mTouchY = pSceneTouchEvent.getMotionEvent().getY();
					timeToTouch = System.currentTimeMillis();
				}
				else if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_MOVE)
				{
					if((System.currentTimeMillis() - timeToTouch) < TIME_LONG_TOUCH)
						return false;

					//Двигаем карту по долгому нажатию на ней
					float newX = pSceneTouchEvent.getMotionEvent().getRawX();
					float newY = pSceneTouchEvent.getMotionEvent().getRawY();

					mTouchOffsetX = (newX - mTouchX);
					mTouchOffsetY = (newY - mTouchY);

					float newScrollX = WorldContext.getInstance().getCamera().getCenterX() - mTouchOffsetX;
					float newScrollY = WorldContext.getInstance().getCamera().getCenterY() - mTouchOffsetY;

					WorldContext.getInstance().getCamera().setCenter(newScrollX, newScrollY);

					mTouchX = newX;
					mTouchY = newY;
				}
                else if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_UP)
                {
                    if((System.currentTimeMillis() - timeToTouch) < TIME_LONG_TOUCH)
                    {
                        WorldContext.getInstance().mPlayer.animate(new long[]{200, 200, 200, 200, 200, 200, 200}, 7, 13, true);
                    }
                }

				return true;
			}
		});

		showWorld();
    }

	public void showWorld()
	{
        try
        {
		Debug.e("Check world");
		if(WorldContext.getInstance().mWorld != null)
		{
			Debug.e("Check layers");
			if(WorldContext.getInstance().mWorld.mTMXMap.getTMXLayers() == null)
				Debug.e("WorldContext.getInstance().mWorld.getTMXLayers() == null Oo");

			Debug.e("Get layer 0");
			TMXLayer ground = WorldContext.getInstance().mWorld.mTMXMap.getTMXLayers().get(0);
			Debug.e("Check layer 0");
			if(ground == null)
				Debug.e("WorldContext.getInstance().mWorld.getTMXLayers().get(0) = null Oo");
			Debug.e(" layer columns =  " + ground.getTileColumns());
			Debug.e(" layer name =  " + ground.getName());
			Debug.e("Attach layer 0");
			attachChild(ground);
			attachChild(WorldContext.getInstance().mWorld.mTMXMap.getTMXLayers().get(1));
			attachChild(WorldContext.getInstance().mPlayer);
			attachChild(new Text(100, 100, ResourceManager.getInstance().mGameFont, "Main Game", ResourceManager.getInstance().mVBO));
			Debug.e("1 layer show");
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
        //TODO возврат в игровое меню
		ScenesManager.getInstance().setScene(new SceneMenu());
    }

    @Override
    public ScenesManager.SceneTypes getSceneType()
    {
        return ScenesManager.SceneTypes.SCENE_GAME;
    }

    @Override
    public void dispposeScene()
    {
        detachChild(WorldContext.getInstance().mWorld.mTMXMap.getTMXLayers().get(0));
        detachChild(WorldContext.getInstance().mWorld.mTMXMap.getTMXLayers().get(1));
        WorldContext.getInstance().mWorld.mTMXMap.getTMXTileSets().clear();
        WorldContext.getInstance().mWorld.mTMXMap = null;
        WorldContext.getInstance().mWorld = null;
    }
}
