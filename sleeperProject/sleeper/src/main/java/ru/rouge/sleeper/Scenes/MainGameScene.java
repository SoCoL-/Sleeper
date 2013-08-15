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
				WorldContext wc = WorldContext.getInstance();

				if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_DOWN)
				{
					Debug.e("Action Down is enabled");
					wc.mPlayer.isMoveLoop = true;
					try
					{
						wc.mPlayerContr.move(pSceneTouchEvent);
					}
					catch(Exception e)
					{
						Debug.e(e);
					}

				}
				else if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_MOVE)
				{
					Debug.e("Action Move is enabled");

					try
					{
						wc.mPlayerContr.move(pSceneTouchEvent);
					}
					catch(Exception e)
					{
						Debug.e(e);
					}
				}
                else if(pSceneTouchEvent.getAction() == MotionEvent.ACTION_UP)
                {
					Debug.e("Action Up is enabled");
					wc.mPlayer.isMoveLoop = false;
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
			if(ground == null)
				Debug.e("WorldContext.getInstance().mWorld.getTMXLayers().get(0) = null Oo");
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
		ScenesManager.getInstance().setScene(ScenesManager.SceneTypes.SCENE_MENU);
    }

    @Override
    public ScenesManager.SceneTypes getSceneType()
    {
        return ScenesManager.SceneTypes.SCENE_GAME;
    }

    @Override
    public void disposeScene()
    {
		Debug.e("on MainGameScene dispose scene");
        detachChild(WorldContext.getInstance().mWorld.mTMXMap.getTMXLayers().get(0));
        detachChild(WorldContext.getInstance().mWorld.mTMXMap.getTMXLayers().get(1));
		detachChild(WorldContext.getInstance().mPlayer);
        WorldContext.getInstance().mWorld.mTMXMap.getTMXTileSets().clear();
        WorldContext.getInstance().mWorld.mTMXMap = null;
        WorldContext.getInstance().mWorld = null;
		this.detachSelf();
		this.dispose();
    }
}
