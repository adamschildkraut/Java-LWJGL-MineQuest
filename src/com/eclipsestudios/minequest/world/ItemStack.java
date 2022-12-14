package com.eclipsestudios.minequest.world;

import com.eclipsestudios.minequest.utils.maths.Maths;
import com.eclipsestudios.minequest.world.blocks.Block;
import com.eclipsestudios.minequest.world.items.Item;

public class ItemStack {

	private Item item;
	private int count;
	
	public ItemStack(Item item, int count) {
		this.item = item;
		setCount(count);
	}
	
	public Item getItem() {
		return item;
	}
	
	public void setItem(Item item) {
		this.item = item;
	}
	
	public int getCount() {
		if (item.getID() == Block.air.getID()) {
			return 0;
		}
		return count;
	}
	
	public void setCount(int i) {
		this.count = (int)Maths.clamp(i, 0, 64);
		if (count == 0) {

			item = Block.air;
		}
	}
	
	public boolean addStack(ItemStack stack) {

		if (getItem().getID() == Block.air.getID()) {

			setItem(stack.getItem());
		}
		
		if (stack.getItem().getID() == item.getID()) {
			
			int lastCount = getCount();
			setCount(getCount() + stack.getCount());
			stack.setCount(stack.getCount() - (getCount() - lastCount));

			return stack.getCount() == 0;
		}
		
		return false;
	}
	
	public boolean addStack(ItemStack stack, int count) {

		if (getItem().getID() == Block.air.getID()) {

			setItem(stack.getItem());
		}
		
		if (stack.getItem().getID() == item.getID()) {
			
			int lastStackCount = stack.getCount();
			setCount(getCount() + count);
			stack.setCount(stack.getCount() - count);

			return stack.getCount() == lastStackCount - count;
		}
		
		return false;
	}
	
	@Override
	public ItemStack clone() {
		ItemStack stack = new ItemStack(null, 0);
		stack.set(this);
		return stack;
	}
	
	public void set(ItemStack stack) {
		setItem(stack.item);
		setCount(stack.count);
	}
}
