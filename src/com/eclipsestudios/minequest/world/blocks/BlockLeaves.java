package com.eclipsestudios.minequest.world.blocks;

public class BlockLeaves extends Block {

	@Override
	public boolean isTransparent() {
		return true;
	}
	
	@Override
	public int getTexture(int i) {
		return 10;
	}
}
