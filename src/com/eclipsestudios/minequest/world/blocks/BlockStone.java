package com.eclipsestudios.minequest.world.blocks;

import com.eclipsestudios.minequest.world.ItemStack;

public class BlockStone extends Block {

	public BlockStone() {
		
	}
	
	@Override
	public int getTexture(int i) {
		return 1;
	}

	@Override
	public ItemStack getDrop() {
		return new ItemStack(cobblestone, 1);
	}
}
