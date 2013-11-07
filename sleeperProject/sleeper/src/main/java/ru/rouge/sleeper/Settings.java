package ru.rouge.sleeper;

/**
 * Created by 1 on 21.10.13.
 *
 * Настройки
 */
public class Settings
{
    //-----------------------------
    //CONSTANTS
    //-----------------------------
    public final static int TILE_SIZE = 32;             //Глобальное значение размера тайла в игре

    //-----------------------------
    //VARIABLES
    //-----------------------------
    private boolean isFastPlayer;
    private boolean isWarFog;
    private boolean isFPS;
    private boolean isDebugButton;

    //-----------------------------
    //CONSTRUCTORS
    //-----------------------------
    public Settings()
    {
        //Set by Default
        isFastPlayer = false;
        isWarFog = true;
        isFPS = true;
        isDebugButton = false;
    }

    //-----------------------------
    //CLASS METHODS
    //-----------------------------

    //-----------------------------
    //GETTERS/SETTERS
    //-----------------------------

    public void setFastPlayer(boolean isFast)
    {
        this.isFastPlayer = isFast;
    }

    public boolean isFastPlayer()
    {
        return isFastPlayer;
    }

    public boolean isWarFog()
    {
        return isWarFog;
    }

    public void setWarFog(boolean warFog)
    {
        isWarFog = warFog;
    }

    public boolean isFPS()  {  return isFPS;  }

    public void setFPS(boolean isFPS)  { this.isFPS = isFPS;  }

    public boolean isDebugButton() {  return isDebugButton;  }

    public void setDebugButton(boolean isDebugButton) {  this.isDebugButton = isDebugButton;  }

    //-----------------------------
    //INNER CLASSES
    //-----------------------------
}
