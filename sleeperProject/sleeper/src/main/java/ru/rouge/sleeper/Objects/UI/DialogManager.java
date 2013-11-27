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
import android.widget.TextView;

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
    //private ArrayAdapter<String> mAdapter;
    private ImageView mBtnExit, mImgObj;
    private TextView mMessage, mStats;
    private boolean isShowDialog;

    //-----------------------------
    //CONSTRUCTOR
    //-----------------------------

    public DialogManager(final Activity c)
    {
        this.mActivity = c;
        setShowDialog(false);
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
                mStats = (TextView)mDialog.findViewById(R.id.GameDialog_Stats);
                mImgObj = (ImageView)mDialog.findViewById(R.id.GameDialog_Img);
                mMessage = (TextView)mDialog.findViewById(R.id.GameDialog_Message);
                //ListView mListDialog = (ListView)mDialog.findViewById(R.id.GameDialog_List);
                //mAdapter = new ArrayAdapter<String>(c, android.R.layout.simple_list_item_1);
                //mListDialog.setAdapter(mAdapter);

                mBtnExit.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        resetDialog();
                        ((MainGameScene)ScenesManager.getInstance().getCurrentScene()).setPause(false);
                        mDialog.dismiss();
                        setShowDialog(false);
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
                //mAdapter.add(message);
                mMessage.setText(message);
                mStats.setVisibility(View.GONE);
                mImgObj.setVisibility(View.GONE);
                mDialog.show();
                setShowDialog(true);
            }
        });
    }

    public void resetDialog()
    {
        mMessage.setText("");
        mMessage.setVisibility(View.VISIBLE);
        mStats.setText("");
        mStats.setVisibility(View.VISIBLE);
        mImgObj.setVisibility(View.VISIBLE);
    }

    public void closeDialog()
    {
        if(mDialog != null && mDialog.isShowing())
        {
            mDialog.dismiss();
            setShowDialog(false);
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
