package com.eclipsestudios.minequest.world.blocks;

import com.eclipsestudios.minequest.utils.Utils;

public class BlockOakWood extends Block {

	@Override
	public int getTexture(int i) {
		
		switch(i) {
			case Utils.TOP:
				case Utils.BOTTOM:
					return 9;
			default:
			return 8;
		}
	}
}
