package com.eclipsestudios.minequest.client.rendering;

import com.eclipsestudios.minequest.utils.maths.Maths;
import com.eclipsestudios.minequest.world.World;
import org.lwjgl.opengl.GL11;

import com.eclipsestudios.minequest.client.MineQuest;
import com.eclipsestudios.minequest.client.Texture;
import com.eclipsestudios.minequest.world.entities.Entity;
import com.eclipsestudios.minequest.world.entities.EntityManager;
import com.eclipsestudios.minequest.world.entities.EntityPlayer;

public class EntityRenderer {
	
	public static void render(EntityManager manager, EntityPlayer player, World world) {
		
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glAlphaFunc(GL11.GL_GREATER, 0.0f);
		
		GL11.glPushMatrix();
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		Maths.perspective(1.5f, (float)MineQuest.instance.getWindow().getWidth() / (float)MineQuest.instance.getWindow().getHeight(), 0.1f, 1000);
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glRotatef(player.getCamXRotation(), 1, 0, 0);
		GL11.glRotatef(player.ry, 0, 1, 0);
		GL11.glTranslatef(-player.x, -player.y, -player.z);
		
		for (Entity entity : manager.getEntities()) {

			GL11.glPushMatrix();
			entity.onRender(Tessellator.INSTANCE, world);
			GL11.glPopMatrix();
		}
		
		GL11.glPopMatrix();
		
		Texture.unbind();
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		
		GL11.glDisable(GL11.GL_ALPHA_TEST);
	}
}
