package com.eclipsestudios.minequest.world.entities;

import com.eclipsestudios.minequest.client.MineQuest;
import com.eclipsestudios.minequest.client.Texture;
import com.eclipsestudios.minequest.client.input.Input;
import com.eclipsestudios.minequest.client.input.KeyCode;
import com.eclipsestudios.minequest.client.input.MouseButton;
import com.eclipsestudios.minequest.client.rendering.Tessellator;
import com.eclipsestudios.minequest.client.screens.PlayerInventoryScreen;
import com.eclipsestudios.minequest.client.screens.PlayingScreen;
import com.eclipsestudios.minequest.utils.Timer;
import com.eclipsestudios.minequest.utils.Utils;
import com.eclipsestudios.minequest.utils.maths.AABB;
import com.eclipsestudios.minequest.utils.maths.Maths;
import com.eclipsestudios.minequest.world.BlockHit;
import com.eclipsestudios.minequest.world.Inventory;
import com.eclipsestudios.minequest.world.ItemStack;
import com.eclipsestudios.minequest.world.World;
import com.eclipsestudios.minequest.world.blocks.Block;
import com.eclipsestudios.minequest.world.items.Item;

public class EntityPlayer extends EntityLiving {

	private float speed = 0.075f;
	private float sensitivity = 1;
	private float camRx;
	
	private int selectedSlot;
	
	private float jumpForce = 0.2f;
	
	private Timer breakingTime;
	private Timer mouseButtonTimer;
	private Timer healthTimer;
	
	private BlockHit breakingBlock;
	
	public EntityPlayer(float x, float y, float z) {
		super(x, y, z, 20, 20);
		
		this.camRx = 0;
		this.mouseButtonTimer = new Timer();
		this.healthTimer = new Timer();
		
		this.selectedSlot = 0;
		setInventory(new Inventory(9 * 5));
		
		getInventory().add(new ItemStack(Item.apple, 5));
		getInventory().add(new ItemStack(Item.shears, 1));
		
		this.breakingTime = new Timer();
	}

	
	@Override
	public void onUpdate(World world) {

		MineQuest.instance.getSoundSystem().setListenerPosition(x, y, z);
		MineQuest.instance.getSoundSystem().setListenerAngle(ry);
		
		this.vz = 0;
		this.vx = 0;
				
		final Input input = MineQuest.instance.getInput();
		
		if (input.getKeyDown(KeyCode.KEY_T)) {
			world.getEntityManager().addEntity(new EntityZombie(x, y, z));
		}
		
		if (input.getKeyDown(KeyCode.KEY_E)) {
			if (MineQuest.instance.currentScreen() instanceof PlayerInventoryScreen) {
				MineQuest.instance.showScreen(new PlayingScreen());
			} else {
				MineQuest.instance.showScreen(new PlayerInventoryScreen());
			}
		}
		
		if (!MineQuest.instance.paused) {
			
			this.ry += input.getMouseDX() * sensitivity;
			this.camRx -= input.getMouseDY() * sensitivity;
			
			camRx = Maths.clamp(camRx, -90, 90);

			selectedSlot -= input.getScrollDY();

			if (input.getKeyDown(KeyCode.KEY_1)) {
				selectedSlot = 0;
			}  else if (input.getKeyDown(KeyCode.KEY_2)) {
				selectedSlot = 1;
			}  else if (input.getKeyDown(KeyCode.KEY_3)) {
				selectedSlot = 2;
			}  else if (input.getKeyDown(KeyCode.KEY_4)) {
				selectedSlot = 3;
			}  else if (input.getKeyDown(KeyCode.KEY_5)) {
				selectedSlot = 4;
			}  else if (input.getKeyDown(KeyCode.KEY_6)) {
				selectedSlot = 5;
			}  else if (input.getKeyDown(KeyCode.KEY_7)) {
				selectedSlot = 6;
			}  else if (input.getKeyDown(KeyCode.KEY_8)) {
				selectedSlot = 7;
			} else if (input.getKeyDown(KeyCode.KEY_9)) {
				selectedSlot = 8;
			}
			
			if (selectedSlot >= 9) {
				selectedSlot -= 9;
			} else if (selectedSlot < 0) {
				selectedSlot += 9;
			}
			
			if (input.getKeyDown(KeyCode.KEY_G)) {
				setHealth(getHealth() - 1);
			}

			if (input.getKey(KeyCode.KEY_W)) {
				if (input.getKey(KeyCode.KEY_LEFT_SHIFT)) {
					moveFront((float)(-speed * 2));
					System.out.println("Sprint");
				} else {
					moveFront(-speed);
					System.out.println("Walk");
				}
			}
			if (input.getKey(KeyCode.KEY_A)) {
				moveRight(-speed);
			}
			if (input.getKey(KeyCode.KEY_S)) {
				moveFront(speed);
			}
			if (input.getKey(KeyCode.KEY_D)) {
				moveRight(speed);
			}

			if (input.getKey(KeyCode.KEY_SPACE) && headInFluid) {

				if (vy < jumpForce) {

					vy = jumpForce / 2.0f;
				}
			} else if(input.getKey(KeyCode.KEY_SPACE) && onGround) {
				if(vy < jumpForce) {
					
					vy += jumpForce;
				}
			}
			
			vy -= World.GRAVITY_FORCE;
			
			if (!input.getMouseButton(MouseButton.LEFT)) {

				breakingTime.reset();
				breakingBlock = null;
			}
			
			if (input.getMouseButton(MouseButton.LEFT)) {
				
				BlockHit hit = world.pick((int)x - 8, (int)y - 8, (int)z - 8, (int)x + 8, (int)y + 8, (int)z + 8, this);

				if (hit != null) {

					if (world.getBlock(hit.x, hit.y, hit.z).isBreakable()) {

						if (breakingTime.getTimeMilli() >= world.getBlock(hit.x, hit.y, hit.z).getBreakingTime()) {

							if (world.getBlock(hit.x, hit.y, hit.z) == Block.leaves && !(getInventory().getItemStack(selectedSlot).getItem() == Item.shears)) {
								world.breakBlock(hit.x, hit.y, hit.z, this, false);
							} else {
								world.breakBlock(hit.x, hit.y, hit.z, this, true);
							}

							breakingBlock = null;
							breakingTime.reset();

						} else {

							if (breakingBlock == null || (breakingBlock.x != hit.x || breakingBlock.y != hit.y || breakingBlock.z != breakingBlock.z)) {

								breakingTime.reset();
							}

							breakingBlock = hit;
						}
					}
				}
			} else if (input.getMouseButton(MouseButton.RIGHT)) {
				
				if (mouseButtonTimer.getTimeMilli() >= 250) {

					if (getInventory().getItemStack(selectedSlot).getItem() == Block.air) {
						return;
					}
					
					if (getInventory().getItemStack(selectedSlot).getItem() instanceof Block) {
						
						BlockHit hit = world.pick((int)x - 8, (int)y - 8, (int)z - 8, (int)x + 8, (int)y + 8, (int)z + 8, this);
						if (hit != null) {

							AABB blockHitbox = new AABB();
							Block.oakWood.getHitbox(blockHitbox);
							blockHitbox.move(hit.x + Utils.x(hit.face), hit.y + Utils.y(hit.face), hit.z + Utils.z(hit.face));
							
							AABB hitbox = new AABB();
							getHitbox(hitbox);
							hitbox.move(x, y, z);
							
							if (!hitbox.collide(blockHitbox)) {
								world.placeBlock(hit.x + Utils.x(hit.face), hit.y + Utils.y(hit.face), hit.z + Utils.z(hit.face), (Block)getInventory().getItemStack(selectedSlot).getItem(), this);
								getInventory().getItemStack(selectedSlot).setCount(getInventory().getItemStack(selectedSlot).getCount() - 1);
							}
						}
						
					}	
					
					getInventory().getItemStack(selectedSlot).getItem().onUse(getInventory().getItemStack(selectedSlot), this);
					
					mouseButtonTimer.reset();
				}
			}
		} else {
			this.vy = 0;
		}

		if (world.getBlock((int)getXPosition(), (int)getYPosition() - 2, (int)getZPosition()) != Block.air && this.vy < -0.3) {
			setHealth((int)((float)getHealth() + (this.vy * 3)));
		}

		if (getHealth() <= 0) {
			setHealth(20);
			setXPosition(MineQuest.instance.spawnX);
			setYPosition(MineQuest.instance.spawnY + 4);
			setZPosition(MineQuest.instance.spawnZ);
		}
	}
	
	@Override
	public void onRender(Tessellator t, World world) {
		
		if (breakingBlock != null) {
			
			Block.atlas.bind();
			
			int offset = (int)Maths.clamp((int)(breakingTime.getTimeMilli() / (float)world.getBlock(breakingBlock.x, breakingBlock.y, breakingBlock.z).getBreakingTime() * 4), 0, 4);
			
			t.cube.setAllFaces(offset / 16.0f, 1 - 1 / 16.0f, (offset + 1) / 16.0f, 1);
			t.cube.cube(-0.501f, -0.501f, -0.501f, 0.501f, 0.501f, 0.501f, breakingBlock.x, breakingBlock.y, breakingBlock.z);
			t.render();
			
			Texture.unbind();
		}
	}
	
	public int getSelectedSlot() {
		return selectedSlot;
	}
	
	public float getCamXRotation() {
		return camRx;
	}
	
	@Override
	public void getHitbox(AABB dest) {
		dest.set(-0.25f, -1.5f, -0.25f, 0.25f, 0.24f, 0.25f);
	}
}
