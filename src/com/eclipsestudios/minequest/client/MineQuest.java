package com.eclipsestudios.minequest.client;

import com.eclipsestudios.minequest.client.audio.SoundSystem;
import com.eclipsestudios.minequest.client.guis.GuiScreen;
import com.eclipsestudios.minequest.client.input.Input;
import com.eclipsestudios.minequest.client.rendering.ChunkRenderer;
import com.eclipsestudios.minequest.client.rendering.EntityRenderer;
import com.eclipsestudios.minequest.client.rendering.GuiScreenRenderer;
import com.eclipsestudios.minequest.client.rendering.Window;
import com.eclipsestudios.minequest.client.screens.PlayingScreen;
import com.eclipsestudios.minequest.utils.Debug;
import com.eclipsestudios.minequest.utils.Timer;
import com.eclipsestudios.minequest.world.Sounds;
import com.eclipsestudios.minequest.world.World;
import com.eclipsestudios.minequest.world.blocks.Block;
import com.eclipsestudios.minequest.world.entities.EntityPlayer;
import org.lwjgl.opengl.GL;

public class MineQuest implements Runnable {
	public static MineQuest instance;

	private final int width, height;

	private boolean running;

	private Window window;
	private World world;
	public int spawnY = 255;
	public int spawnX = World.MAX_LOADED_CHUNKS * 16 / 2, spawnZ = World.MAX_LOADED_CHUNKS * 16 / 2;
	private Input input;
	private EntityPlayer player;
	public boolean paused;
	
	private SoundSystem soundSystem;

	private GuiScreen screen;

	private void init() {

		soundSystem = new SoundSystem();
		soundSystem.init();
		Sounds.init(soundSystem);

		world = new World();

		player = new EntityPlayer(0, 0, 0);
		while (world.getBlock((int)player.getXPosition(), spawnY, (int)player.getZPosition()) == Block.air) {
			spawnY--;
		}

		player.setXPosition(spawnX);
		player.setYPosition(spawnY + 4);
		player.setZPosition(spawnZ);

		Debug.info("Spawning in player...");
		world.getEntityManager().addEntity(player);

		input.hideCursor(true);

		showScreen(new PlayingScreen());

	}

	public void render() {

		world.regenerateChunks(player);
		ChunkRenderer.render(world.getChunks(), player);
		EntityRenderer.render(world.getEntityManager(), player, world);
		GuiScreenRenderer.render(screen);
		GuiScreenRenderer.render();
	}

	private void update() {
		GuiScreenRenderer.clearRenderingGuis();
		
		world.update();
		screen.update();
	}

	private void close() {

		soundSystem.shutDown();
		world.save();
	}

	
	@Override
	public void run() {

		window = new Window(this.width, this.height, "MineQuest Alpha 1.0.2", false);

		GL.createCapabilities();

		input = new Input(window);

		init();

		Timer secondsTimer = new Timer();
		Timer updateTimer = new Timer();
		int frames = 0;
		float updates = 0;

		while (running) {

			window.clear(0.5f, 0.8f, 1.0f);

			if (updateTimer.getTimeMilli() >= 1000 / 60) {

				update();
				input.update();
				window.pollEvents();
				updates++;
				updateTimer.subTimeMilli(1000 / 60);
			}

			if (window.shouldClose()) {
				running = false;
			}

			render();

			window.update();
			frames++;

			if (secondsTimer.getTimeMilli() >= 1000) {
				window.setTitle(window.getTitle().concat(" | FPS : " + frames + " | " + 1000 / updates + "ms/Frame"));
				frames = 0;
				updates = 0;
				secondsTimer.subTimeMilli(1000);
			}
		}
		
		close();
		
		window.close();
	}

	
	public void showScreen(GuiScreen screen) {
		if (this.screen != null) {

			this.screen.exit();
		}
		this.screen = screen;
		screen.initGuis();
	}
	
	public GuiScreen currentScreen() {
		return this.screen;
	}
	
	public EntityPlayer getPlayer() {
		return player;
	}
	
	public Window getWindow() {
		return window;
	}

	public Input getInput() {
		return input;
	}
	
	public SoundSystem getSoundSystem() {
		return soundSystem;
	}
	
	public World getWorld() {
		return world;
	}
	
	public boolean isRunning() {
		return running;
	}

	
	public MineQuest(int width, int height) {

		if (instance != null) {

			throw new RuntimeException("Tried to create multiple Minequest instances on same JVM");
		}

		instance = this;
		this.width = width;
		this.height = height;
		running = true;
		run();
	}
}
