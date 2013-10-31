package ru.rouge.sleeper.Objects;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

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
        if(WorldContext.getInstance().mWorld.mLevels.get(0).getTMXLayers().get(GameMap.LAYER_FLOOR).getTMXTile(col, row).isVisible())
            super.draw(pGLState, pCamera);
    }

    //-----------------------------
    //GETTERS/SETTERS
    //-----------------------------

    //-----------------------------
    //INNER CLAASES
    //-----------------------------
}
