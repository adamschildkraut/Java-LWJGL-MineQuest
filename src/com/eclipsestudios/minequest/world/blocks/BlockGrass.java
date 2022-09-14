package com.eclipsestudios.minequest.world.blocks;

import com.eclipsestudios.minequest.client.audio.Sound;
import com.eclipsestudios.minequest.utils.Utils;
import com.eclipsestudios.minequest.world.ItemStack;
import com.eclipsestudios.minequest.world.Sounds;
import com.eclipsestudios.minequest.world.World;
import com.eclipsestudios.minequest.world.entities.Entity;

public class BlockGrass extends Block {

	public BlockGrass() {
	
	}
	
	@Override
	public void onBreak(World world, int x, int y, int z, Entity breaker) {
		

	}
	
	@Override
	public int getTexture(int i) {

		switch (i) {
		case Utils.TOP:
			return 3;
		case Utils.BOTTOM:
			return 5;
		default:
			return 4;
		}
	}
	
	@Override
	public ItemStack getDrop() {
		return new ItemStack(dirt, 1);
	}
	
	@Override
	public Sound getBlockBreakingSound() {
		return Sounds.dirtBreaking;
	}
}
