package ru.rouge.sleeper.Objects;

import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

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

	//IUpdateHandler animationControl;

	//-----------------------------
	//CONSTRUCTORS
	//-----------------------------

	public Player(float pX, float pY, ITiledTextureRegion pTiledTextureRegion, VertexBufferObjectManager pVertexBufferObjectManager)
	{
		super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);

		/*animationControl = new IUpdateHandler()
		{
			@Override
			public void onUpdate(float pSecondsElapsed)
			{
				int Loop = -1;
				int AnimFrames[] = {0,1,2,3,4,5,6,7,8,9,10,11,12};//,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31};
				long times[] = {150,150,150,150,150,150,150,150,150,150,150,150,150};//,150,150,150,150,150,150,150,150,150,150,150,150,150,150,150,150,150,150,150};
				//Player.this.animate(150, Loop);
				Player.this.animate(100);
			}

			@Override
			public void reset()
			{

			}
		};

		//this.unregisterUpdateHandler(animationControl);
		this.registerUpdateHandler(animationControl);
		WorldContext.getInstance().getCamera().updateChaseEntity();*/
	}

	//-----------------------------
	//CLASS METHODS
	//-----------------------------

	//-----------------------------
	//GETTERS/SETTERS
	//-----------------------------

	//-----------------------------
	//INNER CLASSES
	//-----------------------------
}
