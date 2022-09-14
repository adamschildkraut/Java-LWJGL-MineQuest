package com.eclipsestudios.minequest.world.entities;

import com.eclipsestudios.minequest.world.World;

public interface EntityUpdateEvent {

	void invoke(World world, Entity entity);
}
