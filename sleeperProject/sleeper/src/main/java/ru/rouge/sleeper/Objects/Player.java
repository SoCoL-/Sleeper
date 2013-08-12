package ru.rouge.sleeper.Objects;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import ru.rouge.sleeper.Utils.Directions;
import ru.rouge.sleeper.WorldContext;

/**
 * Evgenij Savchik
 * Created by 1 on 05.07.13.
 *
 * Собственно сам игрок
 */
public final class Player extends BaseAnimObject
{
	//-----------------------------
	//CONSTANTS
	//-----------------------------

	//-----------------------------
	//VARIABLES
	//-----------------------------

	private Directions mDir;

	//-----------------------------
	//CONSTRUCTORS
	//-----------------------------

	public Player(float pX, float pY, ITiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager)
	{
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
		mSpeed = 0.5f;
	}

	//-----------------------------
	//CLASS METHODS
	//-----------------------------

	public void animatePlayer(Directions dir)
	{
		if(mDir == dir)
			return;

		mDir = dir;

		switch (dir)
		{
			case DIR_EAST:
				this.animate(new long[]{150, 150, 150, 150, 150, 150, 150, 150}, 8, 15, true);//run right
				break;
			case DIR_WEST:
				this.animate(new long[]{150, 150, 150, 150, 150, 150, 150, 150}, 0, 7, true);//run left
				break;
			case DIR_SOUTH:
				this.animate(new long[]{150, 150, 150, 150, 150, 150, 150, 150}, 24, 31, true);//run down
				break;
			case DIR_NORTH:
				this.animate(new long[]{150, 150, 150, 150, 150, 150, 150, 150}, 16, 23, true);//run up
				break;
		}
	}

	//-----------------------------
	//GETTERS/SETTERS
	//-----------------------------

	//-----------------------------
	//INNER CLASSES
	//-----------------------------
}
