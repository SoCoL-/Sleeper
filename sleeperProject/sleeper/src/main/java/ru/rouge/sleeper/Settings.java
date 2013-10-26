package ru.rouge.sleeper;

/**
 * Created by 1 on 21.10.13.
 *
 * Настройки
 */
public class Settings
{
    private boolean isFastPlayer;
    private boolean isWarFog;

    public Settings()
    {
        //Set by Default
        isFastPlayer = false;
        isWarFog = true;
    }

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
}
