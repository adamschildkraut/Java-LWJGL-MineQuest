package com.eclipsestudios.minequest.world;

import com.eclipsestudios.minequest.client.audio.Sound;
import com.eclipsestudios.minequest.client.audio.SoundSystem;

public class Sounds {

	public static final Sound dirtBreaking = new Sound("dirt_breaking.ogg", false, Sound.ROLLOF);
	
	public static void init(SoundSystem system) {
		
		system.loadSound(dirtBreaking);
	}
}
