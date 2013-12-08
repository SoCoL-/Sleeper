package ru.rouge.sleeper.Utils;

import android.os.Environment;

import org.andengine.util.debug.Debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;

import ru.rouge.sleeper.Generator.newWorldGenerator;

/**
 * @author Evgeny
 * Тут будут располагаться вспомогательные функции, которые могут пригодиться в разных частях кода
 * */
public final class Utils
{
    //Шанс присоединения к свободной двери комнаты
    public final static int MINCHANCEROOM = 40;
    public final static int MAXCHANCEROOM = 50;


	private static long oldSeed = 0;								//Зерно, для случайной генерации числа

	/**Тут будут расположены множества, определяющие тип поверхности уровня(стены, пол, детали интерьера, двери, т.д.)
	 * */
	public static HashSet<Integer> typesWall = new HashSet<Integer>();
	public static HashSet<Integer> typesFloor = new HashSet<Integer>();
	
	/**
	 * Функция пытается создать совершенно случайное число
	 * min - минимальное значение (включительно)
	 * max - максимальное значение (включительно)
	 * */
	public static int getRand(int min, int max)
	{
		int rand = 0;
		
		if(min < 0)
		{
			Debug.e("Минимальное значение меньше нуля!");
			return 0;
		}
		if(max < 0)
		{
			Debug.e("Максимальное значение меньше нуля!");
			return 0;
		}
		
		Date now = new Date();
		long seed = now.getTime() + oldSeed;
		oldSeed = seed;
		
		Random randomizer = new Random(seed);
		int n = max - min + 1;
		rand = randomizer.nextInt(n);
		if(rand < 0)
			rand = -rand;
		
		return min + rand;
	}

    public static void printLevel(ArrayList<newWorldGenerator.ObjectOnMap> obj, int width, int height)
    {
        File file = new File(Environment.getExternalStorageDirectory() + File.separator + "output.txt");

        if(!file.exists())
            try
            {
                file.createNewFile();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        int[][] map = new int[height][width];
        for(int i = 0; i < height; i++)
            for(int j = 0; j < width; j++)
                map[i][j] = 0;

        for(newWorldGenerator.ObjectOnMap o : obj)
        {
            for(int r = 0; r < o.mSize.getHeight(); r++)
                for(int c = 0; c < o.mSize.getWidth(); c++)
                {
                    map[r+o.mCoord.getY()][c+o.mCoord.getX()] = o.mIndex+1;
                }
        }

        try
        {
            OutputStream mSaveFile = new FileOutputStream(file);
            OutputStreamWriter osw = new OutputStreamWriter(mSaveFile);

            osw.write("width = " + width + "\n");
            osw.write("height = " + width + "\n");
            osw.write("objects = " + obj.size() + "\n");

            for(int i = 0; i < height; i++)
            {
                for(int j = 0; j < width; j++)
                {
                    osw.write("" + map[i][j]);
                }
                osw.write("\n");
            }

            osw.flush();
            osw.close();
            mSaveFile.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
