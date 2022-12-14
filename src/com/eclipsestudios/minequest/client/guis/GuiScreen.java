package com.eclipsestudios.minequest.client.guis;

import java.util.List;

import com.eclipsestudios.minequest.client.fonts.Font;
import com.eclipsestudios.minequest.client.input.Input;
import com.eclipsestudios.minequest.client.rendering.Tessellator;
import com.eclipsestudios.minequest.client.rendering.TextRenderer;
import com.eclipsestudios.minequest.world.ItemStack;
import com.eclipsestudios.minequest.world.items.Item;
import org.lwjgl.opengl.GL11;

import com.eclipsestudios.minequest.client.MineQuest;
import com.eclipsestudios.minequest.world.blocks.Block;
import com.eclipsestudios.minequest.world.entities.EntityItemDrop;
import com.eclipsestudios.minequest.world.entities.EntityPlayer;

import java.util.ArrayList;

public abstract class GuiScreen {

	private List<Gui> guis;
	protected ItemStack mouseItem;
	
	public GuiScreen() {
		
		guis = new ArrayList<>();
		mouseItem = new ItemStack(Block.air, 1);
	}
	
	protected void addGui(Gui gui) {
		guis.add(gui);
	}
	
	public final void render(Tessellator t) {
		onRender(t);
		for (Gui gui : guis) {

			gui.render(t);
		}
		
		Input input = MineQuest.instance.getInput();
		
		Item item = mouseItem.getItem();
		int count = mouseItem.getCount();

		if (item.getID() != Block.air.getID() && count != 0) {

			GL11.glPushMatrix();
			item.renderInInventory(t, input.getMouseX() - 30, input.getMouseY() - 30, 100);
			GL11.glPopMatrix();
			
			if (count != 1) {

				if (count >= 0 && count < 10) {

					TextRenderer.renderString(t, Integer.toString(count), Font.minecraftia, input.getMouseX() - 30 + 50, input.getMouseY() - 30 + 25, 130, 0.6f);
				} else {

					TextRenderer.renderString(t, Integer.toString(count), Font.minecraftia, input.getMouseX() - 30 + 35, input.getMouseY() - 30 + 25, 130, 0.6f);
				}
			}
		}
	}
	
	public final void exit() {
		if (mouseItem.getCount() != 0) {

			EntityPlayer player = MineQuest.instance.getPlayer();
			MineQuest.instance.getWorld().getEntityManager().addEntity(new EntityItemDrop(mouseItem, player.x, player.y, player.z));
		}

		onExit();
	}
	
	private void onExit() {}
	
	public void update() {
		for (Gui gui : guis) {

			gui.onUpdate();
		}
		onUpdate();
	}
	
	private void onUpdate() {}
	
	public abstract void initGuis();
	public abstract void onRender(Tessellator t);
	
	public void onResize() {
		guis.clear();
		initGuis();
	}
	
	public ItemStack getItemStackOnMouse() {
		return mouseItem;
	}
}
