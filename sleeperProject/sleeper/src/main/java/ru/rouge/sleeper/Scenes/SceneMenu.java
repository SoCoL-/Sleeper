package ru.rouge.sleeper.Scenes;

import android.content.Context;
import android.content.SharedPreferences;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.util.GLState;
import org.andengine.util.debug.Debug;

import ru.rouge.sleeper.Managers.ResourceManager;
import ru.rouge.sleeper.Managers.ScenesManager;
import ru.rouge.sleeper.WorldContext;

/**
 * Created by 1 on 18.06.13.
 * Описание сцены меню
 */
public final class SceneMenu extends MainScene implements MenuScene.IOnMenuItemClickListener
{

	//-----------------------------
	//CONSTANTS
	//-----------------------------

	//Идентификаторы кнопок
	private final int BTN_RESUME 	= 0;
	private final int BTN_PLAY 		= BTN_RESUME + 1;
	private final int BTN_LOAD 		= BTN_PLAY + 1;
	private final int BTN_OPTIONS 	= BTN_LOAD + 1;
	private final int BTN_CREDITS 	= BTN_OPTIONS + 1;
	private final int BTN_EXIT 		= BTN_CREDITS + 1;

	//-----------------------------
	//VARIABLES
	//-----------------------------

	private WorldContext mWorldContext;
	private ResourceManager mResManager;
	private MenuScene mMenu;

	//-----------------------------
	//CONSTRUCTORS
	//-----------------------------


	//-----------------------------
	//SUPERCLASS METHODS
	//-----------------------------

	@Override
	public void createScene()
	{
        Debug.e("Menu: createScene()");
		this.mWorldContext = WorldContext.getInstance();
		this.mResManager = ResourceManager.getInstance();
		this.mResManager.loadMenuRes();
		createBackground();
		createMenu();
        //loadSettings();
	}

	@Override
	public void OnKeyBackPressed()
	{
        destroy();
		System.exit(0);
	}

	@Override
	public ScenesManager.SceneTypes getSceneType()
	{
        Debug.e("Menu: getSceneType()");
		return ScenesManager.SceneTypes.SCENE_MENU;
	}

	@Override
	public void dispposeScene()
	{
        WorldContext.getInstance().getEngine().runOnUpdateThread(new Runnable()
        {
            @Override
            public void run()
            {
                Debug.e("Menu: dispposeScene()");
                mResManager.unloadMenuRes();
                mMenu.detachSelf();
                mMenu.dispose();
                detachSelf();
                dispose();
            }
        });
	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem, float pMenuItemLocalX, float pMenuItemLocalY)
	{
        Debug.e("Menu: onMenuItemClicked()");
        switch(pMenuItem.getID())
        {
            case BTN_PLAY:
                //Если нажали "Новая игра", то начнем загружать все сначала
                WorldContext.getInstance().isNewGame = true;
                ScenesManager.getInstance().setLoadScene();
                break;
            case BTN_LOAD:
                WorldContext.getInstance().isNewGame = false;
                break;
            case BTN_OPTIONS:
                ScenesManager.getInstance().setSettingsScene();
                break;
            case BTN_EXIT:
                destroy();
                System.exit(0);
                break;
        }
		return false;
	}

	//-----------------------------
	//CLASS METHODS
	//-----------------------------

	private void createBackground()
	{
        Debug.e("Menu: createBackground()");
		attachChild(new Sprite(mWorldContext.mScreenWidth/2 - mResManager.mMenuBackground.getWidth()/2, mWorldContext.mScreenHeight/2 - mResManager.mMenuBackground.getHeight()/2, mResManager.mMenuBackground.getWidth(), mResManager.mMenuBackground.getHeight(), mResManager.mMenuBackground, mResManager.mVBO)
		{
			@Override
			protected void preDraw(GLState glState, Camera mCamera)
			{
				super.preDraw(glState, mCamera);
				glState.enableDither();
			}
		});

	}

	private void createMenu()
	{
        Debug.e("Menu: createMenu()");
		this.mMenu = new MenuScene(mWorldContext.getCamera());
		this.mMenu.setPosition(0, 0);

		final IMenuItem newBtn = new ScaleMenuItemDecorator(new SpriteMenuItem(BTN_PLAY, mResManager.mBtnNew, mResManager.mVBO), 1.2f, 1.0f);
		mMenu.addMenuItem(newBtn);
        final IMenuItem loadBtn = new ScaleMenuItemDecorator(new SpriteMenuItem(BTN_LOAD, mResManager.mBtnLoad, mResManager.mVBO), 1.2f, 1.0f);
        mMenu.addMenuItem(loadBtn);
        final IMenuItem optionsBtn = new ScaleMenuItemDecorator(new SpriteMenuItem(BTN_OPTIONS, mResManager.mBtnOptions, mResManager.mVBO), 1.2f, 1.0f);
        mMenu.addMenuItem(optionsBtn);
        final IMenuItem creditsBtn = new ScaleMenuItemDecorator(new SpriteMenuItem(BTN_CREDITS, mResManager.mBtnCredits, mResManager.mVBO), 1.2f, 1.0f);
        mMenu.addMenuItem(creditsBtn);
        final IMenuItem exitBtn = new ScaleMenuItemDecorator(new SpriteMenuItem(BTN_EXIT, mResManager.mBtnExit, mResManager.mVBO), 1.2f, 1.0f);
        mMenu.addMenuItem(exitBtn);

		mMenu.buildAnimations();
		mMenu.setBackgroundEnabled(false);

		//newBtn.setPosition(newBtn.getX(), newBtn.getY()*3);
        //loadBtn.setPosition(loadBtn.getX(), loadBtn.getY()*2);
        //optionsBtn.setPosition(optionsBtn.getX(), optionsBtn.getY() - 10);
		//creditsBtn.setPosition(creditsBtn.getX(), creditsBtn.getY() - 20);
		//exitBtn.setPosition(exitBtn.getX(), exitBtn.getY() - 40);

		mMenu.setOnMenuItemClickListener(this);

		setChildScene(mMenu);
	}

    private void destroy()
    {
        Debug.e("Menu: destroy()");
        ResourceManager.getInstance().freeResources();
        ScenesManager.getInstance().freeResources();
        WorldContext.getInstance().destroy();
        dispose();
    }

	//-----------------------------
	//GETTERS/SETTERS
	//-----------------------------

	//-----------------------------
	//INNER CLASSES
	//-----------------------------
}
