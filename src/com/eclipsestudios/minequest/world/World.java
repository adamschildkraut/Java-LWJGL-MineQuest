package com.eclipsestudios.minequest.world;

import java.nio.IntBuffer;
import java.util.Stack;


import com.eclipsestudios.minequest.world.chunk.Chunk;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import com.eclipsestudios.minequest.client.MineQuest;
import com.eclipsestudios.minequest.client.rendering.Tessellator;
import com.eclipsestudios.minequest.utils.Utils;
import com.eclipsestudios.minequest.utils.maths.Maths;
import com.eclipsestudios.minequest.world.blocks.Block;
import com.eclipsestudios.minequest.world.entities.Entity;
import com.eclipsestudios.minequest.world.entities.EntityItemDrop;
import com.eclipsestudios.minequest.world.entities.EntityManager;
import com.eclipsestudios.minequest.world.entities.EntityPlayer;

public class World {
	public static final int MAX_LOADED_CHUNKS = 16;
	public static final float GRAVITY_FORCE = 0.01f;
	
	private Chunk[] chunks;
	
	private WorldGenerator generator;
	private EntityManager entityManager;
	
	private IntBuffer selectionBuffer = BufferUtils.createIntBuffer(10000);
	private IntBuffer viewportBuffer = BufferUtils.createIntBuffer(16);
	private Stack<Chunk> updatingChunks;
	
	public World() {
		
		chunks = new Chunk[MAX_LOADED_CHUNKS * MAX_LOADED_CHUNKS];
		updatingChunks = new Stack<>();
		
		generator = new WorldGenerator(this);
		entityManager = new EntityManager(this);
		
		for (int x = 0; x < MAX_LOADED_CHUNKS; x++) {

			for (int z = 0; z < MAX_LOADED_CHUNKS; z++) {

				chunks[x + z * MAX_LOADED_CHUNKS] = new Chunk(x, z, this);
			}
		}
		
		for (int x = 0; x < MAX_LOADED_CHUNKS; x++) {

			for (int z = 0; z < MAX_LOADED_CHUNKS; z++) {

				if (!chunks[x + z * MAX_LOADED_CHUNKS].load(this)) {

					generator.generateChunk(x, z);
					chunks[x + z * MAX_LOADED_CHUNKS].save();
				}
			}
		}
	}
	
	public void regenerateChunks(EntityPlayer player) {
		
		if (updatingChunks.size() != 0) {

			Chunk chunk = updatingChunks.firstElement();
			chunk.update();
			updatingChunks.removeElementAt(0);
		}
		
		for (Chunk chunk : chunks) {
			
			if (chunk.getZ() * 16 - player.z < -MAX_LOADED_CHUNKS * 16 / 2) {

				chunk.save();
				chunk.recreate(chunk.getX(), chunk.getZ() + MAX_LOADED_CHUNKS);
				if (!chunk.load(this)) {

					generator.generateChunk(chunk.getX(), chunk.getZ());
					break;
				}
			}
			
			if (chunk.getZ() * 16 - player.z > MAX_LOADED_CHUNKS * 16 / 2) {

				chunk.save();
				chunk.recreate(chunk.getX(), chunk.getZ() - MAX_LOADED_CHUNKS);
				if (!chunk.load(this)) {

					generator.generateChunk(chunk.getX(), chunk.getZ());
					break;
				}
			}
			
			if (chunk.getX() * 16 - player.x < -MAX_LOADED_CHUNKS * 16 / 2) {

				chunk.save();
				chunk.recreate(chunk.getX() + MAX_LOADED_CHUNKS, chunk.getZ());
				if (!chunk.load(this)) {

					generator.generateChunk(chunk.getX(), chunk.getZ());
					break;
				}
			}
			
			if (chunk.getX() * 16 - player.x > MAX_LOADED_CHUNKS * 16 / 2) {

				chunk.save();
				chunk.recreate(chunk.getX() - MAX_LOADED_CHUNKS, chunk.getZ());
				if (!chunk.load(this)) {

					generator.generateChunk(chunk.getX(), chunk.getZ());
					break;
				}
			}
		}
	}
	
	public void update() {
		
		entityManager.update();
	}
	
	public void save() {
		for (Chunk chunk : chunks) {

			chunk.save();
		}
	}
	
	public void updateChunk(Chunk chunk) {
		if (!updatingChunks.contains(chunk)) {
			
			updatingChunks.push(chunk);
		}
	}
	
	public Chunk getChunk(int x, int z) {
		
		int a = -((x / MAX_LOADED_CHUNKS) - (x < 0 && x % MAX_LOADED_CHUNKS != 0 ? 1 : 0)) * MAX_LOADED_CHUNKS;
		int b = -((z / MAX_LOADED_CHUNKS) - (z < 0 && z % MAX_LOADED_CHUNKS != 0 ? 1 : 0)) * MAX_LOADED_CHUNKS;
		
		Chunk chunk = chunks[(x + a) + (z + b) * MAX_LOADED_CHUNKS];
		
		if (chunk.getX() == x && chunk.getZ() == z) {
			return chunk;
		}

		return null;
	}
	
	public Block getBlock(int x, int y, int z) {
		Chunk chunk = getChunk(x >> 4, z >> 4);
		if (chunk == null) {
			return Block.stone;
		}
		return Block.registry.getItemAsBlock(chunk.getBlock(x - chunk.getX() * 16, y, z - chunk.getZ() * 16));
	}
	
	public void setBlock(int x, int y, int z, Block block) {
		Chunk schunk = getChunk(x >> 4, z >> 4);
		if (schunk == null) {
			return;
		}
		Chunk chunk;
		
		int bx = x - schunk.getX() * 16, bz = z - schunk.getZ() * 16;
		
		if (block.isTransparent()) {
			
			if(bz == 0) {
				chunk = getChunk(x >> 4, (z - 1) >> 4);
				if (chunk != null) {
					
					updateChunk(chunk);
				}
			} else if (bz == 15) {
				chunk = getChunk(x >> 4, (z + 1) >> 4);
				if (chunk != null) {
					updateChunk(chunk);
				}
			}
			
			if (bx == 0) {
				chunk = getChunk((x - 1) >> 4, z >> 4);
				if (chunk != null) {
					updateChunk(chunk);
				}
			}  if(bx == 15) {
				chunk = getChunk((x + 1) >> 4, z >> 4);
				if (chunk != null) {
					updateChunk(chunk);
				}
			}
			schunk.setBlock(bx, y, bz, block);
		} else {
			
			schunk.setBlock(bx, y, bz, block);
			
			if (bz == 0) {
				chunk = getChunk(x >> 4, (z - 1) >> 4);
				if (chunk != null) {
					
					updateChunk(chunk);
				}
			} else if (bz == 15) {
				chunk = getChunk(x >> 4, (z + 1) >> 4);
				if (chunk != null) {
					updateChunk(chunk);
				}
			}
			
			if (bx == 0) {
				chunk = getChunk((x - 1) >> 4, z >> 4);
				if (chunk != null) {
					updateChunk(chunk);
				}
			} else if (bx == 15) {
				chunk = getChunk((x + 1) >> 4, z >> 4);
				if (chunk != null) {
					updateChunk(chunk);
				}
			}
		}
	}
	
	public void placeBlock(int x, int y, int z, Block block, Entity placer) {
		setBlock(x, y, z, block);
		block.onPlace(this, x, y, z, placer);
	}
	
	public void breakBlock(int x, int y, int z, Entity breaker, boolean shouldDropItem) {
		
		MineQuest.instance.getSoundSystem().playSound(false, getBlock(x, y, z).getBlockBreakingSound(), x, y, z, 1.0f);
		if (shouldDropItem) {

			getEntityManager().addEntity(new EntityItemDrop(getBlock(x, y, z).getDrop(), x, y, z));
		}
		getBlock(x, y, z).onBreak(this, x, y, z, breaker);
		setBlock(x, y, z, Block.air);
	}
	
	public EntityManager getEntityManager() {
		return entityManager;
	}
	
	public Chunk[] getChunks() {
		return chunks;
	}
	
	public BlockHit pick(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, EntityPlayer player) {
		
		GL11.glSelectBuffer(selectionBuffer);	
		GL11.glGetIntegerv(GL11.GL_VIEWPORT, viewportBuffer);
		
		float mouseX = MineQuest.instance.getWindow().getWidth() / 2.0f;
		float mouseY = MineQuest.instance.getWindow().getHeight() / 2.0f;
		
		GL11.glRenderMode(GL11.GL_SELECT);
		
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		
		GL11.glPushMatrix();
		
		GL11.glMatrixMode(GL11.GL_PROJECTION);
		GL11.glLoadIdentity();
		GLU.gluPickMatrix(mouseX, mouseY, 1, 1, viewportBuffer);
		Maths.perspective(1.5f, (float)MineQuest.instance.getWindow().getWidth() / (float)MineQuest.instance.getWindow().getHeight(), 0.1f, 1000);
		
		GL11.glMatrixMode(GL11.GL_MODELVIEW);
		GL11.glLoadIdentity();
		GL11.glRotatef(player.getCamXRotation(), 1, 0, 0);
		GL11.glRotatef(player.ry, 0, 1, 0);
		GL11.glTranslatef(-player.x, -player.y, -player.z);
		
		GL11.glInitNames();
		
		for (int x = minX; x <= maxX; x++) {

			GL11.glPushName(x);
			for (int y = minY; y <= maxY; y++) {

				GL11.glPushName(y);
				for (int z = minZ; z <= maxZ; z++) {

					GL11.glPushName(z);
					if (getBlock(x, y, z).isSolid()) {
						
						for(int f = 0; f < 6; f++) {
							
							GL11.glPushName(f);
							
							if (getBlock(x + (f == Utils.RIGHT ? 1 : (f == Utils.LEFT ? -1 : 0)), y + (f == Utils.TOP ? 1 : (f == Utils.BOTTOM ? -1 : 0)), z + (f == Utils.FRONT ? 1 : (f == Utils.BACK ? -1 : 0))).isTransparent()) {
								
								Tessellator.INSTANCE.cube.setFace(f, 0, 0, 0, 0);
								Tessellator.INSTANCE.cube.cube(-0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, x, y, z);
								
								Tessellator.INSTANCE.render();
							}
							
							GL11.glPopName();
						}
					}
					GL11.glPopName();
				}
				GL11.glPopName();
			}
			GL11.glPopName();
		}

		GL11.glPopMatrix();
		
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_CULL_FACE);

		int hits = GL11.glRenderMode(GL11.GL_RENDER);
		int x, y, z;
		int face;
		
		if(hits == 0) {
			return null;
		}

		int near = 2000000000;
		int index = 3;
		
		for (int i = 0; i < hits; i++) {
			
			if(selectionBuffer.get(i * 7 + 2) < near) {
				
				near = selectionBuffer.get(i * 7 + 2);
				index = i * 7 + 3;
			}
		}
		
		x = selectionBuffer.get(index);
		y = selectionBuffer.get(index + 1);
		z = selectionBuffer.get(index + 2);
		face = selectionBuffer.get(index + 3);
		
		selectionBuffer.clear();
		viewportBuffer.clear();
		
		return new BlockHit(face, x, y, z);
	}
}
