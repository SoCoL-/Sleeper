package ru.rouge.sleeper.Scenes.SubScenes;

import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.sprite.Sprite;

import ru.rouge.sleeper.Managers.ResourceManager;
import ru.rouge.sleeper.WorldContext;

/**
 * Created by 1 on 09.11.13.
 * Игровое меню
 */
public class GameMenu extends MenuScene
{
    //-----------------------------
    //CONSTANTS
    //-----------------------------

    public static final int BTN_RESUME = 0;
    public static final int BTN_SAVE = BTN_RESUME + 1;
    public static final int BTN_LOAD = BTN_SAVE + 1;
    public static final int BTN_EXIT = BTN_LOAD + 1;

    //-----------------------------
    //VARIABLES
    //-----------------------------

    private ResourceManager mRes;

    //-----------------------------
    //CONSTRUCTOR
    //-----------------------------

    public GameMenu()
    {
        super(WorldContext.getInstance().getCamera());
        this.setPosition(0, 0);

        mRes = ResourceManager.getInstance();
        mRes.loadMenuRes();

        createMenuItems();
    }

    //-----------------------------
    //CLASS METHODS
    //-----------------------------

    private void createMenuItems()
    {
        //Создаем фон
        attachChild(new Sprite(WorldContext.getInstance().mScreenWidth/2 - 135, WorldContext.getInstance().mScreenHeight/2 - 185, 270, 370, mRes.mHUDBackground, mRes.mVBO));

        //Добавим элементы меню
        final IMenuItem btnResume = new ScaleMenuItemDecorator(new SpriteMenuItem(BTN_RESUME, mRes.mBtnResume, mRes.mVBO), 1.2f, 1.0f);
        this.addMenuItem(btnResume);
        final IMenuItem btnSave = new ScaleMenuItemDecorator(new SpriteMenuItem(BTN_SAVE, mRes.mBtnLoad, mRes.mVBO), 1.2f, 1.0f);
        this.addMenuItem(btnSave);
        final IMenuItem btnLoad = new ScaleMenuItemDecorator(new SpriteMenuItem(BTN_LOAD, mRes.mBtnLoad, mRes.mVBO), 1.2f, 1.0f);
        this.addMenuItem(btnLoad);
        final IMenuItem btnExit = new ScaleMenuItemDecorator(new SpriteMenuItem(BTN_EXIT, mRes.mBtnExit, mRes.mVBO), 1.2f, 1.0f);
        this.addMenuItem(btnExit);

        this.buildAnimations();
        this.setBackgroundEnabled(false);
        this.setOnSceneTouchListener(this);

    }

    public void destroyMenu()
    {
        mRes.unloadMenuRes();
    }

    //-----------------------------
    //GETTERS/SETTERS
    //-----------------------------

    //-----------------------------
    //INNER CLAASES
    //-----------------------------
}
