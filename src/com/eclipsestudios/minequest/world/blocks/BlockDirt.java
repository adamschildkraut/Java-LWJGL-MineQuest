package com.eclipsestudios.minequest.world.blocks;

import com.eclipsestudios.minequest.client.audio.Sound;
import com.eclipsestudios.minequest.world.Sounds;

public class BlockDirt extends Block {

	@Override
	public int getTexture(int i) {
		return 5;
	}
	
	public Sound getBlockBreakingSound() {
		return Sounds.dirtBreaking;
	}
}
