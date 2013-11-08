package ru.rouge.sleeper.Managers;

import android.graphics.Color;

import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import java.util.ArrayList;

import ru.rouge.sleeper.Objects.Player;
import ru.rouge.sleeper.R;
import ru.rouge.sleeper.Utils.Utils;
import ru.rouge.sleeper.WorldContext;

/**
 * Created by 1 on 18.06.13. Eugene Savchik
 * Класс отвечает за всю загрузку графики
 */
public final class ResourceManager
{
	//-----------------------------
	//CONSTANTS
	//-----------------------------
	//Временно
	//public static final int PLAYER_ID = 0;

	//-----------------------------
	//VARIABLES
	//-----------------------------

	private static ResourceManager instance;

	public VertexBufferObjectManager mVBO;

	private BitmapTextureAtlas mSplashAtlas;
	public ITextureRegion mSplashRegion;

	private BuildableBitmapTextureAtlas mMenuBuildableAtlas;

	public ITextureRegion mBtnResume;
	public ITextureRegion mBtnNew;
	public ITextureRegion mBtnLoad;
	public ITextureRegion mBtnOptions;
	public ITextureRegion mBtnCredits;
	public ITextureRegion mBtnExit;

	//private BitmapTextureAtlas	mMenuBackgroundAtlas;
	public ITextureRegion 		mMenuBackground;

	//Characters
    private BitmapTextureAtlas              mPlayerTexture;
	public TiledTextureRegion 				mHeroTexture;

    private BitmapTextureAtlas              mDoorsAtlas;
    public  TiledTextureRegion              mDoorsTexture;

    private BitmapTextureAtlas              mStairsTextureAtlas;
    public  TiledTextureRegion              mStairsTexture;

    public Font mGameFont;
    private ITexture mGameFontAtlas;

    public Font mMenuFont;
    private ITexture mMenuFontAtlas;

    public ArrayList<TMXTiledMap> mRooms;

    private boolean isGameResLoad;                                  //Загружены ли ресурсы игры

	//-----------------------------
	//CONSTRUCTORS
	//-----------------------------

	//-----------------------------
	//CLASS METHODS
	//-----------------------------

	/**
	 * Подготовка менеджера к работе
	 * @param vbo - менеджер вертексного буффера
	 */
	public void setManager(VertexBufferObjectManager vbo)
	{
		this.mVBO = vbo;
        this.mRooms = new ArrayList<TMXTiledMap>();
        isGameResLoad = false;
    }

	/**
	 * Загружаем ресурсы экрана заставки
	 */
	public void loadSplashRes()
	{
		WorldContext wc = WorldContext.getInstance();
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("menu/");
		mSplashAtlas = new BitmapTextureAtlas(wc.getTextureManager(), 200, 200, TextureOptions.BILINEAR);
		mSplashRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mSplashAtlas, wc.getContext(), "logo.png", 0, 0);
		mSplashAtlas.load();

        FontFactory.setAssetBasePath("fonts/");
        mGameFontAtlas = new BitmapTextureAtlas(WorldContext.getInstance().getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        mGameFont = FontFactory.createFromAsset(WorldContext.getInstance().getFontManager(), mGameFontAtlas, WorldContext.getInstance().getAssetManager(), "arthur_gothic.ttf", 24, true, Color.WHITE);
        mGameFont.load();

        mMenuFontAtlas = new BitmapTextureAtlas(WorldContext.getInstance().getTextureManager(), 512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        mMenuFont = FontFactory.createFromAsset(WorldContext.getInstance().getFontManager(), mMenuFontAtlas, WorldContext.getInstance().getAssetManager(), "menu_font_1.ttf", 38, true, Color.WHITE);
        mMenuFont.load();
    }

	/**
	 * Освобождаем память от ресурсов экрана заставки
	 */
	public void unloadSplashRes()
	{
        if(mSplashAtlas != null)
        {
            mSplashAtlas.unload();
            mSplashRegion = null;
        }
	}

	public void loadMenuRes()
	{
		WorldContext wc = WorldContext.getInstance();
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("menu/");
		mMenuBuildableAtlas = new BuildableBitmapTextureAtlas(wc.getTextureManager(), 220, 480, TextureOptions.BILINEAR);
		mBtnCredits = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuBuildableAtlas, wc.getContext(), "btn_credits.png");
		mBtnOptions = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuBuildableAtlas, wc.getContext(), "btn_settings.png");
		mBtnResume = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuBuildableAtlas, wc.getContext(), "btn_resume.png");
		mBtnLoad = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuBuildableAtlas, wc.getContext(), "btn_load.png");
		mBtnExit = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuBuildableAtlas, wc.getContext(), "btn_exit.png");
		mBtnNew = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuBuildableAtlas, wc.getContext(), "btn_new.png");

        BitmapTextureAtlas mMenuBackgroundAtlas = new BitmapTextureAtlas(wc.getTextureManager(), 400, 400, TextureOptions.BILINEAR);
		mMenuBackground = BitmapTextureAtlasTextureRegionFactory.createFromAsset(mMenuBackgroundAtlas, wc.getContext(), "background.png", 0, 0);
        mMenuBackgroundAtlas.load();

		try
		{
			this.mMenuBuildableAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 0));
			this.mMenuBuildableAtlas.load();
		}
		catch (ITextureAtlasBuilder.TextureAtlasBuilderException e)
		{
			Debug.e(e.toString());
		}
	}

	public void unloadMenuRes()
	{
        Debug.i("Unload menu resources");
		if(mMenuBuildableAtlas != null)
			mMenuBuildableAtlas.unload();
		mMenuBackground = null;
		mBtnCredits = null;
		mBtnResume = null;
		mBtnOptions = null;
		mBtnLoad = null;
		mBtnExit = null;
		mBtnNew = null;
        Debug.i("Done unload");
	}

	/**
     * Загружаем игровые текстуры
	 * */
	public void loadGameRes()
	{
		WorldContext wc = WorldContext.getInstance();
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        this.mPlayerTexture = new BitmapTextureAtlas(wc.getTextureManager(), 256, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mHeroTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mPlayerTexture, wc.getAssetManager(), "characters/hero.png", 0, 0, 8, 4);
        this.mPlayerTexture.load();

        this.mDoorsAtlas = new BitmapTextureAtlas(wc.getTextureManager(), 64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mDoorsTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mDoorsAtlas, wc.getAssetManager(), "door/door1.png", 0, 0, 2, 2);
        this.mDoorsAtlas.load();

        this.mStairsTextureAtlas = new BitmapTextureAtlas(wc.getTextureManager(), 64, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        this.mStairsTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mStairsTextureAtlas, wc.getAssetManager(), "stairs.png", 0, 0, 2, 1);
        this.mStairsTextureAtlas.load();

        Utils.typesWall.add(1);
        Utils.typesWall.add(2);
        Utils.typesWall.add(3);
        Utils.typesWall.add(4);
        Utils.typesWall.add(5);
        Utils.typesWall.add(6);
        Utils.typesWall.add(7);
        Utils.typesWall.add(8);
        Utils.typesWall.add(9);
        Utils.typesWall.add(10);
        Utils.typesWall.add(11);
        Utils.typesWall.add(12);
        Utils.typesWall.add(13);
        Utils.typesWall.add(14);
        Utils.typesWall.add(15);

        Utils.typesFloor.add(16);

        Debug.i("Start load room");
        final TMXLoader loader = new TMXLoader(wc.getAssetManager(), wc.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, ResourceManager.getInstance().mVBO);
        String names[] = wc.getContext().getResources().getStringArray(R.array.rooms_name);
        if(mRooms == null)
            mRooms = new ArrayList<TMXTiledMap>(names.length);
        try
        {
            for(String name : names)
            {
                //loader.loadFromAsset("tmx/map_test3.tmx");
                mRooms.add(loader.loadFromAsset("tmx/" + name));
                Debug.i("Room loaded with name = " + name);
            }
        }
        catch(Exception e)
        {
            Debug.e(e);
        }

        WorldContext.getInstance().mPlayer = new Player(0, 0, ResourceManager.getInstance().mHeroTexture, ResourceManager.getInstance().mVBO);
        WorldContext.getInstance().mPlayerContr.setPlayer(WorldContext.getInstance().mPlayer);

        isGameResLoad = true;
	}

    /**
     * Освобождение игровых ресурсов
     * */
    public void unloadGameRes()
    {
        Debug.i("Unload game resources");
        mPlayerTexture.unload();
        mPlayerTexture = null;
        mHeroTexture = null;
        mDoorsAtlas.unload();
        mDoorsAtlas = null;
        mDoorsTexture = null;
        mStairsTextureAtlas.unload();
        mStairsTextureAtlas = null;
        mStairsTexture = null;
        mRooms.clear();
        mRooms = null;
        isGameResLoad = false;
        Debug.i("Done unload");
    }

	/**
	 * Освобождение занятых ресурсов
	 */
	public void freeResources()
	{
        mGameFont.unload();
        mGameFont = null;
        mGameFontAtlas.unload();
        mGameFontAtlas = null;
        mMenuFont.unload();
        mMenuFont = null;
        mMenuFontAtlas.unload();
        mMenuFontAtlas = null;
		mHeroTexture = null;
        //mPlayerTexture.unload();
        mPlayerTexture = null;
        //mDoorsAtlas.unload();
        mDoorsAtlas = null;

		if(instance != null)
			instance = null;
	}

	//-----------------------------
	//GETTERS/SETTERS
	//-----------------------------

	/**
	 * Получение указателя на класс
	 * @return - возвращает указатель на класс
	 */
    public static ResourceManager getInstance()
    {
        if(instance == null)
            instance = new ResourceManager();
        return instance;
    }

    public boolean isGameResLoad()
    {
        return isGameResLoad;
    }

    public void setGameResLoad(boolean isGameResLoad)
    {
        this.isGameResLoad = isGameResLoad;
    }

    //-----------------------------
	//INNER CLASSES
	//-----------------------------
}
