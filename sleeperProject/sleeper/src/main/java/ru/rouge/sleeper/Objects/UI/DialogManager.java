package ru.rouge.sleeper.Objects.UI;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import ru.rouge.sleeper.Managers.ScenesManager;
import ru.rouge.sleeper.R;
import ru.rouge.sleeper.Scenes.MainGameScene;

/**
 * Created by 1 on 19.11.13.
 * Тестовый класс
 */
public class DialogManager
{
    //-----------------------------
    //CONSTANTS
    //-----------------------------

    //-----------------------------
    //VARIABLES
    //-----------------------------

    private Activity mActivity;
    private Dialog mDialog;
    private ArrayAdapter<String> mAdapter;
    private ImageView mBtnExit;
    private boolean isShowDialog;

    //-----------------------------
    //CONSTRUCTOR
    //-----------------------------

    public DialogManager(final Activity c)
    {
        this.mActivity = c;
        isShowDialog = false;
        c.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                mDialog = new Dialog(c);
                mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                mDialog.setCancelable(false);
                mDialog.setContentView(R.layout.game_dialog);

                //View container = c.getLayoutInflater().inflate(R.layout.game_dialog, null);
                mBtnExit = (ImageView)mDialog.findViewById(R.id.GameDialog_Exit);
                ListView mListDialog = (ListView)mDialog.findViewById(R.id.GameDialog_List);
                mAdapter = new ArrayAdapter<String>(c, android.R.layout.simple_list_item_1);
                mListDialog.setAdapter(mAdapter);

                mBtnExit.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        resetDialog();
                        ((MainGameScene)ScenesManager.getInstance().getCurrentScene()).setPause(false);
                        mDialog.dismiss();
                    }
                });
            }
        });
    }

    //-----------------------------
    //CLASS METHODS
    //-----------------------------

    public void addMessageToDialog(final String message)
    {
        mActivity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                resetDialog();
                mAdapter.add(message);
                mDialog.show();
                isShowDialog = true;
            }
        });
    }

    public void resetDialog()
    {
        mAdapter.clear();
    }

    public void closeDialog()
    {
        if(mDialog != null && mDialog.isShowing())
        {
            mDialog.dismiss();
            isShowDialog = false;
        }
    }



    //-----------------------------
    //GETTERS/SETTERS
    //-----------------------------

    public boolean isShowDialog() {
        return isShowDialog;
    }

    public void setShowDialog(boolean isShowDialog)
    {
        this.isShowDialog = isShowDialog;
    }

    //-----------------------------
    //INNER CLAASES
    //-----------------------------
}
