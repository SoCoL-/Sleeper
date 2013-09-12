package ru.rouge.sleeper.Scenes;

import android.os.AsyncTask;

import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import ru.rouge.sleeper.Generator.WorldGenerator;
import ru.rouge.sleeper.Managers.ResourceManager;
import ru.rouge.sleeper.Managers.ScenesManager;
import ru.rouge.sleeper.Map.GameMap;
import ru.rouge.sleeper.WorldContext;

/**
 * Created by Masa on 20.06.13.
 * Загрузка всего мира
 */
public final class LoadScene extends MainScene
{
	private WorldContext mWC;
	private ResourceManager mRM;

    private WorldGenerator mGenerator = new WorldGenerator();

    @Override
    public void createScene()
    {
		this.mWC = WorldContext.getInstance();
		this.mRM = ResourceManager.getInstance();
		Debug.e("on LoadScene create()");
        setBackground(new Background(Color.GREEN));
		Debug.e("on LoadScene setBackground");
		attachChild(new Text(mWC.mScreenWidth/2 - mRM.mGameFont.getTexture().getWidth()/2, mWC.mScreenHeight/2 - mRM.mGameFont.getTexture().getHeight()/2, mRM.mGameFont, "Loading...", mRM.mVBO));
		Debug.e("on LoadScene load map");
		loadingGameStructures();
    }

	public void loadingGameStructures()
	{
		//TODO Загружаем уровни и всякие другие классы
		Debug.e("in load map, start AsyncTask");

		mWC.getContext().runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				new AsyncTask<String, String, String>()
				{

					@Override
					protected String doInBackground(String... strings)
					{
						mRM.loadGameRes();

						final TMXLoader loader = new TMXLoader(mWC.getAssetManager(), mWC.getTextureManager(), TextureOptions.BILINEAR_PREMULTIPLYALPHA, ResourceManager.getInstance().mVBO);
						Debug.e("on LoadScene load TMXLoader");
						try
						{
							Debug.e("on LoadScene before load map");
							mWC.mWorld = new GameMap(loader);
							Debug.e("on LoadScene load map");

                            //Начало генерации уровней - Тестовая версия
                            mGenerator.startGeneration(0);
                            mGenerator.generateNewLevel();
                            //Конец
						}
						catch (Exception ex)
						{
							Debug.e(ex.toString());
						}
						return "";
					}

					@Override
					protected void onPostExecute(String s)
					{
						Debug.e("Loaded map");
						float heightMap = mWC.mWorld.mTMXMap.getTileRows() * mWC.mWorld.mTMXMap.getTileHeight();
						float widthMap = mWC.mWorld.mTMXMap.getTileColumns() * mWC.mWorld.mTMXMap.getTileWidth();
						Debug.e("on LoadScene get width and height of map");
						mWC.getCamera().setBounds(-100, -100, widthMap, heightMap);
						mWC.getCamera().setBoundsEnabled(true);
						Debug.e("on LoadScene setup camera bounds");
						//ScenesManager.getInstance().setScene(new MainGameScene());
                        ScenesManager.getInstance().setGameScene();
						super.onPostExecute(s);
					}
				}.execute("");
			}
		});
	}

    @Override
    public void OnKeyBackPressed()
    {
        //TODO Ничего не делаем, игра началась грузиться и прерывать нельзя
    }

    @Override
    public ScenesManager.SceneTypes getSceneType()
    {
        return ScenesManager.SceneTypes.SCENE_LOAD;
    }

    @Override
    public void dispposeScene()
    {
		Debug.e("on LoadScene dispose scene");
        this.detachSelf();
        this.dispose();
        Debug.e("LoadScene: after disposing");
    }
}
