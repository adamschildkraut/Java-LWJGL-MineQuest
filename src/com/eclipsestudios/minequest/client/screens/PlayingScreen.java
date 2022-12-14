package com.eclipsestudios.minequest.client.screens;

import com.eclipsestudios.minequest.client.MineQuest;
import com.eclipsestudios.minequest.client.Texture;
import com.eclipsestudios.minequest.client.guis.GuiScreen;
import com.eclipsestudios.minequest.client.guis.GuiSlot;
import com.eclipsestudios.minequest.client.rendering.Tessellator;
import com.eclipsestudios.minequest.world.entities.EntityPlayer;

public class PlayingScreen extends GuiScreen {

	private static Texture hotbarTexture = Texture.loadAsset("guis/hotbar");
	private static Texture healthTexture = Texture.loadAsset("guis/health");
	private static Texture crossTexture = Texture.loadAsset("guis/guiCross");

	private static final int SIZE = 5;
	
	@Override
	public void initGuis() {

		int wWidth = MineQuest.instance.getWindow().getWidth();
		
		//Add hotbar slots
		for (int x = 0; x < 9; x++) {
		
			int i = wWidth / 2 - ((145 * SIZE) / 2) + (int)(SIZE * 2.6);
			int j = 5 + (int)(SIZE * 1.8);
		
			addGui(new GuiSlot(i + (x * 80), j, 20, MineQuest.instance.getPlayer().getInventory(), x, null));
		}
		
		MineQuest.instance.getInput().hideCursor(true);
		MineQuest.instance.paused = false;
	}

	@Override
	public void onRender(Tessellator t) {

		EntityPlayer player = MineQuest.instance.getPlayer();

		int wHeight = MineQuest.instance.getWindow().getHeight();
		int wWidth = MineQuest.instance.getWindow().getWidth();

		//Draw cross
		crossTexture.bind();

		t.rect.rectUV(0, 0, 1, 0.9f);
		t.rect.rect(wWidth / 2 - (15), wHeight / 2 - (15), 0, 30, 30);

		t.render();

		Texture.unbind();

		//Draw hotbar
		hotbarTexture.bind();
		
		t.rect.rectUV(0, 0, 1 - (17 / 162.0f), 1);
		t.rect.rect(wWidth / 2 - (145 * SIZE / 2), 5, 0, 145 * SIZE, 16 * SIZE);	
		
		t.rect.rectUV(1 - (17 / 162.0f), 0, 1, 1);
		t.rect.rect(wWidth / 2 - (145.0f * 5 / 2) + MineQuest.instance.getPlayer().getSelectedSlot() * 80, 5, 1, 17 * 5, 16 * 5);	
		
		t.render();
		
		Texture.unbind();
		
		//Draw health
		healthTexture.bind();
		
		for (int i = 1; i <= 10; i++) {
			if (player.getHealth() >= i * 2 - 1 && player.getHealth() < i * 2) {

				t.rect.rectUV(1 / 3.0f, 0, 1 / 3.0f * 2, 1);
			} else if (player.getHealth() < i * 2){

				t.rect.rectUV(1 / 3.0f * 2, 0, 1 / 3.0f * 3, 1);
			} else {

				t.rect.rectUV(0, 0, 1 / 3.0f, 1);
			}

			t.rect.rect(wWidth / 2 - (16 / 2) - (145 * SIZE / 2) + 20 + 42 * (i - 1), 100, 0, (int)(16 * 2.5), (int)(16 * 2.5));	
			
			t.render();
		}
		
		Texture.unbind();
	}
}
