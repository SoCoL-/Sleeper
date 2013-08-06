package ru.rouge.sleeper.Objects;

import org.andengine.entity.modifier.PathModifier;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Evgenij Savchik
 * Created by 1 on 05.07.13.
 *
 * Описание базового класса анимированного объекта
 *
 */
public class BaseAnimObject extends AnimatedSprite
{
	//-----------------------------
	//CONSTANTS
	//-----------------------------

	//-----------------------------
	//VARIABLES
	//-----------------------------

	private PathModifier.Path mPath;

	//-----------------------------
	//CONSTRUCTORS
	//-----------------------------

	public BaseAnimObject(float pX, float pY, ITiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager)
	{
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
	}

	//-----------------------------
	//CLASS METHODS
	//-----------------------------

	//-----------------------------
	//GETTERS/SETTERS
	//-----------------------------

	public PathModifier.Path getPath()
	{
		return mPath;
	}

	public void setPath(PathModifier.Path mPath)
	{
		this.mPath = mPath;
	}

	//-----------------------------
	//INNER CLASSES
	//-----------------------------
}
