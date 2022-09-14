package com.eclipsestudios.minequest.world.entities;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.eclipsestudios.minequest.world.World;

public class EntityManager {

	private List<Entity> entities;
	private List<Entity> addingEntities;
	private World world;
	
	public EntityManager(World world) {
		
		this.world = world;
		entities = new ArrayList<>();
		addingEntities = new ArrayList<>();
	}
	
	public void addEntity(Entity entity) {
		addingEntities.add(entity);
	}
	
	public List<Entity> getEntities() {
		return entities;
	}
	
	public void update() {

		entities.addAll(addingEntities);
		
		addingEntities.clear();
		
		Iterator<Entity> iterator = entities.iterator();
		while (iterator.hasNext()) {

			Entity entity = iterator.next();
			if (entity.shouldDestroy()) {

				iterator.remove();
				continue;
			}
			entity.update(world);
		}
	}
}
