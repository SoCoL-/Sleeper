package ru.rouge.sleeper.Objects.UI;

import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;

import ru.rouge.sleeper.Managers.ResourceManager;
import ru.rouge.sleeper.WorldContext;

/**
 * Created by Evgenij on 08.11.13.
 * Диалог в игре
 */
public class Dialog extends CameraScene
{
    //-----------------------------
    //Constants
    //-----------------------------

    //-----------------------------
    //Variables
    //-----------------------------

    private Text mTextMess;
    private String mMesage;

    //-----------------------------
    //Ctors
    //-----------------------------

    public Dialog(float pX, float pY, float pWidth, float pHeight, VertexBufferObjectManager pVertexBufferObjectManager)
    {
        super(WorldContext.getInstance().getCamera());
        this.setPosition(pX, pY);
        setBackgroundEnabled(false);

        createDialog(0, 0, pWidth, pHeight, pVertexBufferObjectManager);
    }

    //-----------------------------
    //Methods
    //-----------------------------

    private void createDialog(float pX, float pY, float pWidth, float pHeight, VertexBufferObjectManager pVertexBufferObjectManager)
    {
        attachChild(new Sprite(pX, pY, pWidth, pHeight, ResourceManager.getInstance().mDialogBackground, pVertexBufferObjectManager));
    }

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    public void setTextDialog(String mess)
    {
        this.mMesage = mess;
        attachChild(new Text(50, 50, ResourceManager.getInstance().mGameFont, mess, 1000, new TextOptions(AutoWrap.LETTERS, (float)450, HorizontalAlign.CENTER, Text.LEADING_DEFAULT), ResourceManager.getInstance().mVBO));
    }

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
