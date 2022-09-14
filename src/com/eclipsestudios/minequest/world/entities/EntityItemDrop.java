package com.eclipsestudios.minequest.world.entities;

import com.eclipsestudios.minequest.client.rendering.Tessellator;
import com.eclipsestudios.minequest.utils.Timer;
import com.eclipsestudios.minequest.utils.maths.AABB;
import com.eclipsestudios.minequest.utils.maths.Maths;
import com.eclipsestudios.minequest.world.ItemStack;
import com.eclipsestudios.minequest.world.World;
import org.lwjgl.opengl.GL11;

public class EntityItemDrop extends Entity {

	private ItemStack stack;
	private float yOffset;
	private Timer timer;
	private Entity target;
	
	public EntityItemDrop(ItemStack stack, float x, float y, float z) {
		super(x, y, z);

		this.stack = stack.clone();
		this.timer = new Timer();
	}

	@Override
	public void onUpdate(World world) {
		
		yOffset = (float)Math.cos(System.currentTimeMillis() / 512.0) / 10.0f + 0.2f;
		
		ry++;
		
		vy -= World.GRAVITY_FORCE;
		
		if(timer.getTimeMilli() > 1000) {
			for(Entity entity : world.getEntityManager().getEntities()) {
				if(Maths.distance(x, y, z, entity.x, entity.y, entity.z) <= 3f) {
					if(entity.hasInventory()) {
						target = entity;
					}
				}
			}
		}
		
		if(target != null) {
			x += (target.x - x) / 10.0f;
			y += (target.y - y) / 10.0f;
			z += (target.z - z) / 10.0f;
			
			AABB targetHitbox = new AABB();
			target.getHitbox(targetHitbox);
			targetHitbox.move(target.x, target.y, target.z);
			AABB hitbox = new AABB();
			getHitbox(hitbox);
			hitbox.move(x, y, z);
			if(targetHitbox.collide(hitbox)) {
				target.getInventory().add(stack);
			}
		}
	}
	
	@Override
	public void onRender(Tessellator t, World world) {

		GL11.glTranslatef(x, y + yOffset, z);
		GL11.glRotatef(ry, 0, 1, 0);
		
		stack.getItem().renderDrop(t);
	}
	
	@Override
	public void getHitbox(AABB dest) {
		dest.set(-0.2f, -0.2f, -0.2f, 0.2f, 0.2f, 0.2f);
	}
	
	@Override
	public boolean shouldDestroy() {

		return stack.getCount() == 0;
	}
}
