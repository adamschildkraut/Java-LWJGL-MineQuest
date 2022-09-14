package com.eclipsestudios.minequest.client.guis;

import com.eclipsestudios.minequest.client.MineQuest;
import com.eclipsestudios.minequest.client.input.MouseButton;

public abstract class GuiButton extends Gui {
	
	private int width, height;
	
	GuiButton(int x, int y, int depth, int width, int height) {
		super(x, y, depth);
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void onUpdate() {
		if(onHover()) {
			if(MineQuest.instance.getInput().getMouseButtonDown(MouseButton.LEFT)) {
				onLeftClick();
			}else if(MineQuest.instance.getInput().getMouseButtonDown(MouseButton.RIGHT)) {
				onRightClick();
			}
		}
	}
	
	private boolean onHover() {
		int mouseX = (int)MineQuest.instance.getInput().getMouseX();
		int mouseY = (int)MineQuest.instance.getInput().getMouseY();
		return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
	}

	public abstract void onLeftClick();
	public abstract void onRightClick();
}
