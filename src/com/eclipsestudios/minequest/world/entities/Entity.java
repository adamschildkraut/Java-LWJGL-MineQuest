package com.eclipsestudios.minequest.world.entities;

import java.util.ArrayList;
import java.util.List;

import com.eclipsestudios.minequest.client.rendering.Tessellator;
import com.eclipsestudios.minequest.utils.maths.AABB;
import com.eclipsestudios.minequest.utils.maths.Maths;
import com.eclipsestudios.minequest.world.Inventory;
import com.eclipsestudios.minequest.world.World;
import com.eclipsestudios.minequest.world.blocks.Block;
import com.eclipsestudios.minequest.world.blocks.BlockFluid;

public abstract class Entity {
	
	public float x, y, z;
	public float rx, ry, rz;
	public float vx, vy, vz;
	public boolean onGround;
	public boolean inFluid;
	public boolean headInFluid;
	public byte fluidID;
	private Inventory inventory;
	public boolean hasHitBlock;
	
	private List<EntityUpdateEvent> updates;
	
	public Entity(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.rx = 0;
		this.ry = 0;
		this.rz = 0;
		this.vx = 0;
		this.vy = 0;
		this.vz = 0;
		
		this.onGround = false;
		this.inventory = null;
		this.updates = new ArrayList<>();
	}
	
	public abstract void onUpdate(World world);
	public void onRender(Tessellator t, World world) {}
	
	public void moveFront(float amount) {
		
		this.vz += amount * (float)Math.cos(Math.toRadians(ry));
		this.vx -= amount * (float)Math.sin(Math.toRadians(ry));
	}
	
	public void moveRight(float amount) {
		
		this.vx += amount * (float)Math.cos(Math.toRadians(ry));
		this.vz += amount * (float)Math.sin(Math.toRadians(ry));
	}
	
	public final void update(World world) {
		
		for (EntityUpdateEvent event : updates) {

			event.invoke(world, this);
		}
		onUpdate(world);
		
		float lastVY = vy;
		
		rx = Maths.clampRotation(rx);
		ry = Maths.clampRotation(ry);
		rz = Maths.clampRotation(rz);
		
		AABB hitbox = new AABB();
		getHitbox(hitbox);
		hitbox.move(x, y, z);
		
		AABB blockHitbox = new AABB();
		
		for (int i = (int)(x + vx) - 2; i <= (int)(x + vx) + 2; i++) {

			for (int j = (int)(y + vy) - 2; j <= (int)(y + vy) + 2; j++) {

				for (int k = (int)(z + vz) - 2; k <= (int)(z + vz) + 2; k++) {

					Block block = world.getBlock(i, j, k);
					
					if (block.isSolid()) {

						block.getHitbox(blockHitbox);
						blockHitbox.move(i, j, k);
						vx = blockHitbox.clipXCoord(hitbox, vx);
					}	
				}
			}
		}
		
		hitbox.move(vx, 0, 0);
		
		
		for (int i = (int)(x + vx) - 2; i <= (int)(x + vx) + 2; i++) {

			for (int j = (int)(y + vy) - 2; j <= (int)(y + vy) + 2; j++) {

				for (int k = (int)(z + vz) - 2; k <= (int)(z + vz) + 2; k++) {

					Block block = world.getBlock(i, j, k);
					
					if (block.isSolid()) {

						block.getHitbox(blockHitbox);
						blockHitbox.move(i, j, k);
						vz = blockHitbox.clipZCoord(hitbox, vz);
					}				
				}
			}
		}
		
		hitbox.move(0, 0, vz);
		
		for (int i = (int)(x + vx) - 2; i <= (int)(x + vx) + 2; i++) {

			for (int j = (int)(y + vy) - 2; j <= (int)(y + vy) + 2; j++) {

				for (int k = (int)(z + vz) - 2; k <= (int)(z + vz) + 2; k++) {
					
					Block block = world.getBlock(i, j, k);
					
					if (block.isSolid()) {

						block.getHitbox(blockHitbox);
						blockHitbox.move(i, j, k);
						vy = blockHitbox.clipYCoord(hitbox, vy);
						hasHitBlock = true;
					}
				}
			}
		}
		
		headInFluid = false;
		inFluid = false;

		for (int i = (int)(x + vx) - 2; i <= (int)(x + vx) + 2; i++) {

			for (int j = (int)(y + vy) - 2; j <= (int)(y + vy) + 2; j++) {

				for (int k = (int)(z + vz) - 2; k <= (int)(z + vz) + 2; k++) {
					
					Block block = world.getBlock(i, j, k);
					
					if (block instanceof BlockFluid) {

						block.getHitbox(blockHitbox);
						blockHitbox.move(i, j, k);
						if (blockHitbox.collide(new AABB(x, y - 0.15f, z, x, y - 0.15f, z))) {

							((BlockFluid) block).onEntityHeadInFluid(this);
							headInFluid = true;
							inFluid = true;
							fluidID = block.getID();
						} else if(hitbox.collide(blockHitbox)) {

							((BlockFluid) block).onEntityInFluid(this);
							inFluid = true;
							fluidID = block.getID();
						}
					}
				}
			}
		}
		
		hitbox.move(0, vy, 0);

		onGround = lastVY < 0 && vy == 0;
		
		this.x += vx;
		this.y += vy;
		this.z += vz;
	}

	public boolean hasHitBlock() {
		System.out.println("Player has hit block in Entity class");
		return hasHitBlock;
	}
	
	public void getHitbox(AABB dest) {}

	public float getXPosition() {
		return x;
	}

	public float setXPosition(float x) {
		return this.x = x;
	}

	public float getYPosition() {
		return y;
	}

	public float setYPosition(float y) {
		return this.y = y;
	}

	public float getZPosition() {
		return z;
	}

	public float setZPosition(float z) {
		return this.z = z;
	}

	protected void setInventory(Inventory inventory) {
		this.inventory = inventory;
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	
	public boolean hasInventory() {
		return inventory != null;
	}
	
	public boolean shouldDestroy() {
		return false; 
	}
	
	public void addUpdateEvent(EntityUpdateEvent event) {
		updates.add(event);
	}
}
