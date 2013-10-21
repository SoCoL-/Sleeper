package ru.rouge.sleeper;

/**
 * Created by 1 on 21.10.13.
 *
 * Настройки
 */
public class Settings
{
    private boolean isFastPlayer;

    public Settings()
    {
        //Set by Default
        isFastPlayer = false;
    }

    public void setFastPlayer(boolean isFast)
    {
        this.isFastPlayer = isFast;
    }

    public boolean isFastPlayer()
    {
        return isFastPlayer;
    }
}
