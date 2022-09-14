package com.eclipsestudios.minequest.world.blocks;

import com.eclipsestudios.minequest.client.MineQuest;
import com.eclipsestudios.minequest.client.Texture;
import com.eclipsestudios.minequest.client.guis.Gui;
import com.eclipsestudios.minequest.client.rendering.GuiScreenRenderer;
import com.eclipsestudios.minequest.client.rendering.Tessellator;
import com.eclipsestudios.minequest.client.rendering.Window;
import com.eclipsestudios.minequest.world.entities.Entity;
import com.eclipsestudios.minequest.world.entities.EntityPlayer;

public class BlockWater extends BlockFluid {

	public static Gui underwaterGUI = new Gui(0, 0, 0) {
		
		@Override
		public void render(Tessellator t) {
			
			Window window = MineQuest.instance.getWindow();
			atlas.bind();
			
			t.setTextureDimensions(atlas);
			t.rect.rectUV(112, 16, 112 + 16, 16 + 16);
			t.rect.rect(0, 0, 0, window.getWidth(), window.getHeight());
			t.render();
			
			Texture.unbind();
		}
	};
	
	@Override
	public int getTexture(int i) {
		return 23;
	}
	
	@Override
	public void onEntityHeadInFluid(Entity entity) {
		
		if (entity instanceof EntityPlayer) {

			GuiScreenRenderer.renderGui(underwaterGUI);
		}
		if (entity.vy < 0) {

			entity.vy /= 1.025f;
		}
	}
	
	@Override
	public void onEntityInFluid(Entity entity) {
		if (entity.vy < 0) {
			entity.vy /= 1.025f;
		}
	}
}
