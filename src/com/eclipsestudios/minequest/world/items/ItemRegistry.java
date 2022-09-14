package com.eclipsestudios.minequest.world.items;

import com.eclipsestudios.minequest.world.Registry;
import com.eclipsestudios.minequest.world.blocks.Block;

public class ItemRegistry extends Registry<Byte, Item> {
	
	public void registerItem(byte id, Item item) {
		register(id, item);
		item.register(id);
	}
	
	public void registerBlockAsItem(byte id, Block block) {
		register(id, block);
		block.register(id);
	}
	
	public Block getItemAsBlock(byte id) {
		if (get(id) instanceof Block) {
			return (Block)get(id);
		}
		
		return null;
	}
}
