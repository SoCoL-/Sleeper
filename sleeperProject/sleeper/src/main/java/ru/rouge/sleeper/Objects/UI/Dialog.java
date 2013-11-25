package ru.rouge.sleeper.Objects.UI;

import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.CameraScene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.AutoWrap;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.debug.Debug;

import ru.rouge.sleeper.Managers.ResourceManager;
import ru.rouge.sleeper.Managers.ScenesManager;
import ru.rouge.sleeper.Scenes.MainGameScene;
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

    private Rectangle mExitButton;
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

        mExitButton = new Rectangle(pWidth - 42, pY + 10, 32, 32, ResourceManager.getInstance().mVBO)
        {
            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY)
            {
                Debug.i("Click by Dialog Exit button");
                ScenesManager.getInstance().getCurrentScene().clearChildScene();
                ((MainGameScene)ScenesManager.getInstance().getCurrentScene()).setDialogMode();
                Dialog.this.dispose();
                return true;
            }
        };
        registerTouchArea(mExitButton);
        attachChild(mExitButton);
    }

    //-----------------------------
    //Getters/Setters
    //-----------------------------

    public void setTextDialog(String mess)
    {
        this.mMesage = mess;
    }

    //-----------------------------
    //Inner Classes
    //-----------------------------
}
