package ru.rouge.sleeper.Scenes;

import org.andengine.entity.scene.Scene;

import ru.rouge.sleeper.Managers.ScenesManager;

/**
 * Created by 1 on 18.06.13.
 * Базовый класс сцены
 */
public abstract class MainScene extends Scene
{
	//-----------------------------
	//CONSTANTS
	//-----------------------------

	//-----------------------------
	//VARIABLES
	//-----------------------------

	//-----------------------------
	//CONSTRUCTORS
	//-----------------------------

	public MainScene()
	{
		createScene();
	}

	//-----------------------------
	//CLASS METHODS
	//-----------------------------

	/**
	 * Создание сцены
	 */
	public abstract void createScene();

	/**
	 * Обработчик нажатия кнопки "назад"
	 */
	public abstract void OnKeyBackPressed();

	/**
	 * Возвращает ип сцены
	 * @return - тип сцены
	 */
	public abstract ScenesManager.SceneTypes getSceneType();

	/**
	 * Уничтожение сцены
	 */
	public abstract void dispposeScene();

	//-----------------------------
	//GETTERS/SETTERS
	//-----------------------------

	//-----------------------------
	//INNER CLASSES
	//-----------------------------
}
