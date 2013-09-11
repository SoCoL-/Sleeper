package ru.rouge.sleeper.Utils;

import org.andengine.util.debug.Debug;

import java.util.Date;
import java.util.HashSet;
import java.util.Random;

/**
 * @author Evgeny
 * Тут будут располагаться вспомогательные функции, которые могут пригодиться в разных частях кода
 * */
public final class Utils
{
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
}
