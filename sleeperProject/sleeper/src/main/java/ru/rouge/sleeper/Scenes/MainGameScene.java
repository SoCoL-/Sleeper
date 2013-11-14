package ru.rouge.sleeper.Scenes;

import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.input.touch.TouchEvent;
import org.andengine.util.debug.Debug;
import org.andengine.util.color.Color;

import ru.rouge.sleeper.MainActivity;
import ru.rouge.sleeper.Managers.ResourceManager;
import ru.rouge.sleeper.Managers.ScenesManager;
import ru.rouge.sleeper.Map.GameMap;
import ru.rouge.sleeper.Objects.BaseObject;
import ru.rouge.sleeper.Scenes.SubScenes.GameMenu;
import ru.rouge.sleeper.WorldContext;

/**
 * Created by Masa on 21.06.13.
 * Отрисовка самой игры
 */
public final class MainGameScene extends MainScene
{
    private boolean isChangeScene;
    private boolean isGameMenu;
    private boolean isPause;
    private boolean isDialogOpen;
    private GameMenu mGameMenu;
    //private MenuScene.IOnMenuItemClickListener mMenuListener;

	@Override
    public void createScene()
    {
        isChangeScene = false;
        isGameMenu = false;
        isDialogOpen = false;
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
                WorldContext.getInstance().mPlayerContr.move(pSceneTouchEvent);
				return true;
			}
		});

        WorldContext.getInstance().getEngine().runOnUpdateThread(new Runnable()
        {
            @Override
            public void run()
            {
                showWorld();
            }
        });
    }

	public void showWorld()
	{
        final int currLevel = WorldContext.getInstance().mWorld.mCurrentLevel;

        WorldContext.getInstance().getCamera().setChaseEntity(WorldContext.getInstance().mPlayer);
        Debug.e("Уровень подземелья = " + currLevel);
        try
        {
		Debug.e("Check world");
		if(WorldContext.getInstance().mWorld != null)
		{
            Debug.e("Add floor");
            attachChild(WorldContext.getInstance().mWorld.mLevels.get(currLevel).getTMXLayers().get(GameMap.LAYER_FLOOR));
            Debug.e("Add walls");
            attachChild(WorldContext.getInstance().mWorld.mLevels.get(currLevel).getTMXLayers().get(GameMap.LAYER_WALLS));
            Debug.e("Add above");
            attachChild(WorldContext.getInstance().mWorld.mLevels.get(currLevel).getTMXLayers().get(GameMap.LAYER_ABOVE));
            Debug.e("Add all doors");
            for(BaseObject d : WorldContext.getInstance().mWorld.mObjects.get(currLevel))
            {
                //if(!(d instanceof Door))
                    attachChild(d);
            }

            Debug.e("Add player");
		    attachChild(WorldContext.getInstance().mPlayer);
            /*for(BaseObject d : WorldContext.getInstance().mWorld.mObjects.get(currLevel))
                if(d instanceof Door)
                    attachChild(d);*/

            WorldContext.getInstance().getCamera().setHUD(WorldContext.getInstance().mWorld.mHUD);
            isChangeScene = true;
        }
		else
			Debug.e("world is null");
        }
        catch (Exception e)
        {
            Debug.e(e);
        }
        Debug.e("Add done");
	}

    public void clearScene()
    {
        this.detachChildren();
        WorldContext.getInstance().getCamera().setHUD(null);
    }

    public void setDialogMode()
    {
        isDialogOpen = true;
        setPause(true);
    }

    /**
     * Ставим/снимаем игру на паузу
     * @param pause - пауза или нет
     * */
    public void setPause(boolean pause)
    {
        isPause = pause;
    }

    public void setChange()
    {
        isChangeScene = false;
    }

    private void destroyMenu()
    {
        mGameMenu.back();
        mGameMenu = null;
        isGameMenu = false;
    }

    @Override
    protected void onManagedUpdate(float pSecondsElapsed)
    {
        if(isChangeScene)
        {
            if(!isPause)
            {
                if(WorldContext.getInstance().mWorld.mCurrentLevel == 1)
                    Debug.i("Update enable");
                super.onManagedUpdate(pSecondsElapsed);
            }
            else
            {
                if(getChildScene() != null)
                    getChildScene().onUpdate(pSecondsElapsed);
            }
        }
    }

    @Override
    public void OnKeyBackPressed()
    {
        //Возврат в игровое меню
        Debug.e("back press");
        try
        {
            //ScenesManager.getInstance().setMenuScene();
            if(!isGameMenu && !isDialogOpen)
            {
                mGameMenu = new GameMenu();
                MenuScene.IOnMenuItemClickListener mMenuListener = new MenuScene.IOnMenuItemClickListener()
                {
                    @Override
                    public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY)
                    {
                        switch(pMenuItem.getID())
                        {
                            case GameMenu.BTN_RESUME:
                                mGameMenu.destroyMenu();
                                destroyMenu();
                                break;

                            case GameMenu.BTN_SAVE:
                                break;

                            case GameMenu.BTN_LOAD:
                                break;

                            case GameMenu.BTN_EXIT:
                                ScenesManager.getInstance().setMenuScene();
                                destroyMenu();
                                break;
                        }
                        return false;
                    }
                };
                mGameMenu.setOnMenuItemClickListener(mMenuListener);
                setChildScene(mGameMenu, false, true, true);
                setPause(true);
                isGameMenu = true;
            }
            else if(isGameMenu && !isDialogOpen)
            {
                mGameMenu.destroyMenu();
                destroyMenu();
                setPause(false);
            }
            else if(!isGameMenu && isDialogOpen)
            {
                MainGameScene.this.clearChildScene();
                setPause(false);
                isDialogOpen = false;
            }
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
        WorldContext.getInstance().getEngine().runOnUpdateThread(new Runnable()
        {
            @Override
            public void run()
            {
                Debug.e("on MainGameScene dispose scene");
                isChangeScene = false;
                isGameMenu = false;
                ResourceManager.getInstance().unloadGameRes();
                detachChild(WorldContext.getInstance().mPlayer);
                detachChildren();
                WorldContext.getInstance().mWorld.mLevels.clear();
                //WorldContext.getInstance().mWorld.mLevels = null;
                WorldContext.getInstance().mWorld.mSpawns.clear();
                //WorldContext.getInstance().mWorld.mSpawns = null;
                WorldContext.getInstance().mWorld.mWakables.clear();
                //WorldContext.getInstance().mWorld.mWakables = null;
                WorldContext.getInstance().mWorld.mObjects.clear();
                //WorldContext.getInstance().mWorld.mObjects = null;
                WorldContext.getInstance().mWorld.mCurrentLevel = 0;
                //WorldContext.getInstance().mWorld = null;
                WorldContext.getInstance().getCamera().setHUD(null);
                WorldContext.getInstance().getCamera().setBoundsEnabled(false);
                WorldContext.getInstance().getCamera().clearUpdateHandlers();
                WorldContext.getInstance().getCamera().setChaseEntity(null);
                WorldContext.getInstance().getCamera().setCenter(MainActivity.CAMERA_WIDTH/2, MainActivity.CAMERA_HEIGHT/2);
                detachSelf();
                dispose();
                Debug.e("MainGameScene: after disposing");
            }
        });
    }
}
