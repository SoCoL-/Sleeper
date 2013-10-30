package ru.rouge.sleeper;

import android.app.Application;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;

/**
 * Created by 1 on 30.10.13.
 * Отлов ощибок
 */

@ReportsCrashes(formKey="b3cbd1392a0294f91925467d52cfe1b0")
public class MyApplication extends Application
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

    //-----------------------------
    //CLASS METHODS
    //-----------------------------

    @Override
    public void onCreate()
    {
        ACRA.init(this);
        ACRA.getErrorReporter().setReportSender(new HockeySender());

        super.onCreate();
    }

    //-----------------------------
    //GETTERS/SETTERS
    //-----------------------------

    //-----------------------------
    //INNER CLAASES
    //-----------------------------
}
