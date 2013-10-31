package ru.rouge.sleeper.Objects;

import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import ru.rouge.sleeper.Generator.WorldGenerator;
import ru.rouge.sleeper.WorldContext;

/**
 * Created by 1 on 30.10.13.
 * Лестница - переход между уровнями
 */
public class Stair extends BaseObject
{

    //-----------------------------
    //CONSTANTS
    //-----------------------------

    //-----------------------------
    //VARIABLES
    //-----------------------------

    private boolean isUp;               //Вверх или вниз ведет лестница

    //-----------------------------
    //CONSTRUCTOR
    //-----------------------------

    public Stair(float pX, float pY, boolean isUp, ITiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager)
    {
        super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);

        this.isUp = isUp;

        setCurrIndexTile();
    }

    //-----------------------------
    //CLASS METHODS
    //-----------------------------

    private void setCurrIndexTile()
    {
        int index;
        if(isUp)
            index = 0;
        else
            index = 1;

        setCurrentTileIndex(index);
    }

    public boolean doOnStair()
    {
        if(isUp)
            WorldContext.getInstance().mLevelManager.nextLevel();
        else
            WorldContext.getInstance().mLevelManager.pervLevel();

        return true;
    }

    //-----------------------------
    //GETTERS/SETTERS
    //-----------------------------

    public boolean isUp()
    {
        return isUp;
    }

    public void setUp(boolean isUp)
    {
        this.isUp = isUp;
    }

    //-----------------------------
    //INNER CLAASES
    //-----------------------------
}
