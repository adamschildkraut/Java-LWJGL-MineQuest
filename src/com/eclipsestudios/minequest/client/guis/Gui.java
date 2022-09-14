package com.eclipsestudios.minequest.client.guis;

import com.eclipsestudios.minequest.client.rendering.Tessellator;

public abstract class Gui {

	public int x, y, depth;
	
	protected Gui(int x, int y, int depth) {
		
		this.x = x;
		this.y = y;
	}
	
	public void onUpdate() {}
	
	public abstract void render(Tessellator t);
}
