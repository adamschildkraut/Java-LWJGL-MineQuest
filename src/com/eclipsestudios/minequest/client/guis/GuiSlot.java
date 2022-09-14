package com.eclipsestudios.minequest.client.guis;

import com.eclipsestudios.minequest.client.fonts.Font;
import com.eclipsestudios.minequest.client.rendering.Tessellator;
import com.eclipsestudios.minequest.client.rendering.TextRenderer;
import com.eclipsestudios.minequest.world.Inventory;
import com.eclipsestudios.minequest.world.ItemStack;
import com.eclipsestudios.minequest.world.items.Item;
import org.lwjgl.opengl.GL11;

import com.eclipsestudios.minequest.world.blocks.Block;

public class GuiSlot extends GuiButton {

	private Inventory inventory;
	private ItemStack mouseItem;
	private int slotIndex;
	
	public GuiSlot(int x, int y, int depth, Inventory inventory, int slotIndex, ItemStack mouseItem) {
		super(x, y, depth, 57, 63);

		this.inventory = inventory;
		this.slotIndex = slotIndex;
		this.mouseItem = mouseItem;
	}

	@Override
	public void onLeftClick() {
		if (mouseItem != null) {
			
			if (inventory.getItemStack(slotIndex).getItem().getID() != mouseItem.getItem().getID()) {
				
				ItemStack stack = new ItemStack(Block.air, 0);
				stack.set(inventory.getItemStack(slotIndex));
				inventory.setItemStack(slotIndex, mouseItem);
				mouseItem.set(stack);
			} else {
				
				inventory.getItemStack(slotIndex).addStack(mouseItem);
			}
		}
	}
	
	@Override
	public void onRightClick() {
		if (mouseItem != null) {
			
			inventory.getItemStack(slotIndex).addStack(mouseItem, 1);
		}
	}
	

	@Override
	public void render(Tessellator t) {

		Item item = inventory.getItemStack(slotIndex).getItem();
		int count = inventory.getItemStack(slotIndex).getCount();
		if (item.getID() != Block.air.getID() && count != 0) {

			GL11.glPushMatrix();
			item.renderInInventory(t, x, y, 20);
			GL11.glPopMatrix();
			
			if (count != 1) {

				if (count >= 0 && count < 10) {

					TextRenderer.renderString(t, Integer.toString(inventory.getItemStack(slotIndex).getCount()), Font.minecraftia, x + 50, y + 25, 100, 0.6f);
				} else {

					TextRenderer.renderString(t, Integer.toString(inventory.getItemStack(slotIndex).getCount()), Font.minecraftia, x + 35, y + 25, 100, 0.6f);
				}
			}
		}
	}
}
