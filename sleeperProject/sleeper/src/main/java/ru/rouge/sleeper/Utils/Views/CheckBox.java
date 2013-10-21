package ru.rouge.sleeper.Utils.Views;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.text.Text;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import ru.rouge.sleeper.Managers.ResourceManager;

/**
 * Created by 1 on 21.10.13.
 *
 * Попытка создать свой чекбокс
 */
public class CheckBox extends Rectangle
{
    private boolean isCheck; //By default is false
    private final String mText;
    private Rectangle mCheckPicture;

    public CheckBox(float pX, float pY, float pWidth, float pHeight, String text, VertexBufferObjectManager pVertexBufferObjectManager)
    {
        super(pX, pY, pWidth, pHeight, pVertexBufferObjectManager);
        Debug.i("Create checkBox");
        this.isCheck = false;
        this.mText = text;
        this.setAlpha(0);
        Debug.i("Start add views to checkbox");
        addView(pVertexBufferObjectManager);
    }

    private void addView(VertexBufferObjectManager vbo)
    {
        mCheckPicture = new Rectangle(0, 0, 32, 32, vbo);
        setColor();
        Debug.i("Added first element");

        Text mName = new Text(50, 0, ResourceManager.getInstance().mMenuFont, mText, vbo);
        Debug.i("Added second element");

        this.attachChild(mCheckPicture);
        this.attachChild(mName);
        Debug.i("Done add views");
    }

    private void setColor()
    {
        if(mCheckPicture == null)
            return;

        if(isCheck)
            mCheckPicture.setColor(new Color(0,1.f,0));
        else
            mCheckPicture.setColor(new Color(1.f,1.f,1.f));
    }

    public boolean isCheck()
    {
        return isCheck;
    }

    public void setCheck(boolean check)
    {
        isCheck = check;
        setColor();
    }
}
