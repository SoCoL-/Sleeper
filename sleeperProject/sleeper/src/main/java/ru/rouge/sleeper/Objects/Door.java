package ru.rouge.sleeper.Objects;

import org.andengine.entity.sprite.TiledSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.debug.Debug;

/**
 * Created by Evgenij on 09.09.13.
 *
 * Описание двери
 */
public class Door extends TiledSprite
{
    //-----------------------------
    //Constants
    //-----------------------------

    //-----------------------------
    //Variables
    //-----------------------------
    private boolean isOpen;             //Открыта или закрыта дверь
    private boolean isVertical;         //Вертикальная или горизонтальная
    private float X, Y;                 //Координаты двери (в пикселах)

    //-----------------------------
    //Ctors
    //-----------------------------

    public Door(float pX, float pY, boolean verical, ITiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager)
    {
        super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);

        setX(pX);
        setY(pY);
        setVertical(verical);
        setOpen(false);
    }

    //-----------------------------
    //Methods
    //-----------------------------

    /**
     * После открытия/закрытия двери выставляет нужный тайл двери для отрисовки
     * */
    private void setCurrentTile()
    {
        int tileNum = 0;

        if(isVertical)
        {
            Debug.i("isVertical +2");
            tileNum = 2;
        }

        if(isOpen)
        {
            Debug.i("isOpen +1");
            tileNum += 1;
        }

        if(tileNum > 3)
            tileNum = 3;

        Debug.i("tileNum = " + tileNum);

        setCurrentTileIndex(tileNum);
    }

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    public boolean isOpen()
    {
        return isOpen;
    }

    public void setOpen(boolean open)
    {
        isOpen = open;
        setCurrentTile();
    }

    @Override
    public float getY()
    {
        return Y;
    }

    @Override
    public void setY(float y)
    {
        Y = y;
    }

    @Override
    public float getX()
    {
        return X;
    }

    @Override
    public void setX(float x)
    {
        X = x;
    }

    public boolean isVertical()
    {
        return isVertical;
    }

    public void setVertical(boolean vertical)
    {
        isVertical = vertical;
    }

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
