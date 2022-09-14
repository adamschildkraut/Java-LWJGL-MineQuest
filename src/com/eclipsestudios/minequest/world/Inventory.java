package com.eclipsestudios.minequest.world;

import com.eclipsestudios.minequest.world.blocks.Block;

public class Inventory {

	private ItemStack[] items;
	
	public Inventory(int slotCount) {
		
		items = new ItemStack[slotCount];
		for (int i = 0; i < slotCount; i++) {

			items[i] = new ItemStack(Block.air, (byte)0);
		}
	}
	
	
	public ItemStack getItemStack(int slot) {
		return items[slot];
	}
	
	public void setItemStack(int slot, ItemStack stack) {
		if (slot < items.length) {

			items[slot].setItem(stack.getItem());
			items[slot].setCount(stack.getCount());
		}
	}

	
	public boolean add(ItemStack stack) {

		if (stack != null) {

			for (ItemStack itemStack : items) {

				if (itemStack.getItem().getID() == stack.getItem().getID()) {

					if (itemStack.addStack(stack)) {
						return true;
					}
				}
			}

			for (ItemStack item : items) {

				if (item.addStack(stack)) {
					return true;
				}
			}
		}

		return false;
	}
}
