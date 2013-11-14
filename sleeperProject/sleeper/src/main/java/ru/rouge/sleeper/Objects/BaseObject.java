package ru.rouge.sleeper.Objects;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

import ru.rouge.sleeper.Map.GameMap;
import ru.rouge.sleeper.WorldContext;

/**
 * Created by 1 on 30.10.13.
 * Базовый интерактивный объект
 */
public class BaseObject extends TiledSprite
{
    //-----------------------------
    //CONSTANTS
    //-----------------------------

    //-----------------------------
    //VARIABLES
    //-----------------------------

    private String mName;               //Имя объекта
    private String mMessage;            //Описание объекта

    //-----------------------------
    //CONSTRUCTOR
    //-----------------------------

    public BaseObject(float pX, float pY, ITiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager)
    {
        super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
    }

    //-----------------------------
    //CLASS METHODS
    //-----------------------------

    @Override
    protected void draw(GLState pGLState, Camera pCamera)
    {
        int col = (int) getX()/32;
        int row = (int) getY()/32;
        try
        {
            if(WorldContext.getInstance().mWorld.mLevels.get(WorldContext.getInstance().mWorld.mCurrentLevel).getTMXLayers().get(GameMap.LAYER_FLOOR).getTMXTile(col, row) == null)
                return;

        if(WorldContext.getInstance().mWorld.mLevels.get(WorldContext.getInstance().mWorld.mCurrentLevel).getTMXLayers().get(GameMap.LAYER_FLOOR).getTMXTile(col, row).isVisible())
        {
            super.draw(pGLState, pCamera);
        }
        }
        catch (Exception e)
        {
            Debug.e(e);
        }
    }
    //-----------------------------
    //GETTERS/SETTERS
    //-----------------------------

    public void setObjectName(String name) { this.mName = name; }

    public String getObjectName() { return this.mName; }

    public void setObjectMessage(String message) { this.mMessage = message; }

    public String getObjectMessage() { return this.mMessage; }

    //-----------------------------
    //INNER CLAASES
    //-----------------------------
}
