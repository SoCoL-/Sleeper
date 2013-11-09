package ru.rouge.sleeper.Utils.Views;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

/**
 * Created by 1 on 09.11.13.
 * Прогресс бар
 */
public class ProgressView extends Rectangle
{
    //-----------------------------
    //CONSTANTS
    //-----------------------------

    //-----------------------------
    //VARIABLES
    //-----------------------------

    private float mPixelsPerPercentRatio;       //Единици изменения прогресса
    private Rectangle mRectProgress;            //Сам прогресс

    //-----------------------------
    //CONSTRUCTOR
    //-----------------------------

    public ProgressView(float pX, float pY, float pWidth, float pHeight, VertexBufferObjectManager pVertexBufferObjectManager)
    {
        super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);

        this.mRectProgress = new Rectangle(0, 0, pWidth, pHeight, pVertexBufferObjectManager);
        attachChild(mRectProgress);
    }

    //-----------------------------
    //CLASS METHODS
    //-----------------------------

    //-----------------------------
    //GETTERS/SETTERS
    //-----------------------------

    /**
     * Выставим цвет фона
     * */
    public void setBackgroundColor(int red, int green, int blue, int alpha)
    {
        this.setColor(new Color(red/255, green/255, blue/255));
    }

    /**
     * Выставим цвет прогресса
     * */
    public void setProgressColor(int red, int green, int blue, int alpha)
    {
        //this.mRectProgress.setColor(red/255f, green/255f, blue/255f, alpha/255f);
        this.mRectProgress.setColor(new Color(red/255, green/255, blue/255));
    }

    /**
     * Выставим максимальный предел прогресса
     * */
    public void setMaxProgress(int max)
    {
        this.mPixelsPerPercentRatio = this.mRectProgress.getWidth() / max;
    }

    /**
     * Выставим текущее значение прогресса
     * */
    public void setCurrentProgress(int current)
    {
        this.mRectProgress.setWidth(mPixelsPerPercentRatio * current);
    }

    //-----------------------------
    //INNER CLAASES
    //-----------------------------
}
