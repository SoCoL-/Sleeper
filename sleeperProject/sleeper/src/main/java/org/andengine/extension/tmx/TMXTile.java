package org.andengine.extension.tmx;

import org.andengine.opengl.texture.region.ITextureRegion;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 10:39:48 - 05.08.2010
 */
public class TMXTile
{
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	int mGlobalTileID;
	private final int mTileRow;
	private final int mTileColumn;
	private final float mTileWidth;
	private final float mTileHeight;
	ITextureRegion mTextureRegion;

	// ===========================================================
	// Constructors
	// ===========================================================

	public TMXTile(final int pGlobalTileID, final int pTileColumn, final int pTileRow, final float pTileWidth, final float pTileHeight, final ITextureRegion pTextureRegion) {
		this.mGlobalTileID = pGlobalTileID;
		this.mTileRow = pTileRow;
		this.mTileColumn = pTileColumn;
		this.mTileWidth = pTileWidth;
		this.mTileHeight = pTileHeight;
		this.mTextureRegion = pTextureRegion;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public int getGlobalTileID() {
		return this.mGlobalTileID;
	}

	public int getTileRow() {
		return this.mTileRow;
	}

	public int getTileColumn() {
		return this.mTileColumn;
	}

	public float getTileX() {
		return this.mTileColumn * this.mTileWidth;
	}

	public float getTileY() {
		return this.mTileRow * this.mTileHeight;
	}

	public float getTileWidth() {
		return this.mTileWidth;
	}

	public float getTileHeight() {
		return this.mTileHeight;
	}

	public ITextureRegion getTextureRegion() {
		return this.mTextureRegion;
	}

	/**
	 * Note this will also set the {@link org.andengine.opengl.texture.region.ITextureRegion} with the associated pGlobalTileID of the {@link org.andengine.extension.tmx.TMXTiledMap}.
	 * @param pTMXTiledMap
	 * @param pGlobalTileID
	 */
	public void setGlobalTileID(final TMXTiledMap pTMXTiledMap, final int pGlobalTileID) {
		this.mGlobalTileID = pGlobalTileID;
		this.mTextureRegion = pTMXTiledMap.getTextureRegionFromGlobalTileID(pGlobalTileID);
	}

	/**
	 * You'd probably want to call {@link TMXTile#setGlobalTileID(org.andengine.extension.tmx.TMXTiledMap, int)} instead.
	 * @param pTextureRegion
	 */
	public void setTextureRegion(final ITextureRegion pTextureRegion) {
		this.mTextureRegion = pTextureRegion;
	}

	public TMXProperties<TMXTileProperty> getTMXTileProperties(final TMXTiledMap pTMXTiledMap) {
		return pTMXTiledMap.getTMXTileProperties(this.mGlobalTileID);
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
