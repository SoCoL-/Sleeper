package ru.rouge.sleeper.Managers;

import android.graphics.Color;

import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.Texture;
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
	public static final int PLAYER_ID = 0;

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

	private BitmapTextureAtlas	mMenuBackgroundAtlas;
	public ITextureRegion 		mMenuBackground;

	//Characters
    private BitmapTextureAtlas              mPlayerTexture;
	public TiledTextureRegion 				mHeroTexture;
	//public  TiledTextureRegion              mEnemy1Texture;
	//public  TiledTextureRegion              mEnemy2Texture;
	//public  TiledTextureRegion              mEnemy3Texture;
	//private TexturePackTextureRegionLibrary mCharactersTexturePackLibrary;

    private BitmapTextureAtlas              mDoorsAtlas;
    public  TiledTextureRegion              mDoorsTexture;

    public Font mGameFont;
    private ITexture mGameFontAtlas;

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

		//TODO Перенести нафиг в начальную инициализацию
		FontFactory.setAssetBasePath("fonts/");
		mGameFontAtlas = new BitmapTextureAtlas(WorldContext.getInstance().getTextureManager(), 256, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		mGameFont = FontFactory.createFromAsset(WorldContext.getInstance().getFontManager(), mGameFontAtlas, WorldContext.getInstance().getAssetManager(), "NeverwinterNights.ttf", 24, true, Color.WHITE);
		mGameFont.load();
	}

	/**
	 * Освобождаем память от ресурсов экрана заставки
	 */
	public void unloadSplashRes()
	{
		mSplashAtlas.unload();
		mSplashRegion = null;
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

		mMenuBackgroundAtlas = new BitmapTextureAtlas(wc.getTextureManager(), 400, 400, TextureOptions.BILINEAR);
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
		if(mMenuBuildableAtlas != null)
			mMenuBuildableAtlas.unload();
		mMenuBackground = null;
		mBtnCredits = null;
		mBtnResume = null;
		mBtnOptions = null;
		mBtnLoad = null;
		mBtnExit = null;
		mBtnNew = null;
	}

	/**Загружаем игровые текстуры
	 * */
	public void loadGameRes()
	{
		WorldContext wc = WorldContext.getInstance();
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        this.mPlayerTexture = new BitmapTextureAtlas(wc.getTextureManager(), 256, 128, TextureOptions.DEFAULT);
        this.mHeroTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mPlayerTexture, wc.getAssetManager(), "characters/hero.png", 0, 0, 8, 4);
        this.mPlayerTexture.load();

        this.mDoorsAtlas = new BitmapTextureAtlas(wc.getTextureManager(), 64, 64, TextureOptions.DEFAULT);
        this.mDoorsTexture = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mDoorsAtlas, wc.getAssetManager(), "door/door1.png", 0, 0, 2, 2);
        this.mDoorsAtlas.load();
	}

	/**
	 * Освобождение занятых ресурсов
	 */
	public void freeResources()
	{
		//TODO Тут будем освобождать ресурсы игры
        mGameFont.unload();
        mGameFont = null;
        mGameFontAtlas.unload();
        mGameFontAtlas = null;
		mHeroTexture = null;
        mPlayerTexture.unload();
        mPlayerTexture = null;
        mDoorsAtlas.unload();
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

	//-----------------------------
	//INNER CLASSES
	//-----------------------------
}
