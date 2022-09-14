package com.eclipsestudios.minequest.client.rendering;

import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import javax.swing.*;

public class Window {

	private String title;
	private int width, height;
	
	private long id;
	
	public Window(int width, int height, String title, boolean vsync) {
		
		this.title = title;
		this.width = width;
		this.height = height;
		
		glfwInit();

		if (System.getProperty("os.name").contains("Mac")) {
			glfwWindowHint(GLFW_SAMPLES, 4);
			glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
			glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
			glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
			glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

			this.title = this.title.concat(" - Mac OSX");
		} else {
			this.title = this.title.concat(" - Windows");
		}

		id = glfwCreateWindow(this.width, this.height, this.title, 0, 0);

		if (id == 0) {
			glfwTerminate();
			System.out.println("ERROR: Unable to create GLFW window!");
		}

		glfwMakeContextCurrent(id);
		
		if (!vsync) {
			glfwSwapInterval(0);
		}

		glfwShowWindow(id);

		GL.createCapabilities();

		GL11.glViewport(0, 0, this.width, this.height);
		
		Window window = this;
		
		glfwSetFramebufferSizeCallback(id, new GLFWFramebufferSizeCallback() {
			@Override
			public void invoke(long id, int width, int height) {
				
				window.width = width;
				window.height = height;
				GL11.glViewport(0, 0, width, height);
			}
		});

		if (GL11.glGetString(GL11.GL_VERSION).contains("3.")) {
			JOptionPane.showMessageDialog(null, "ERROR: OpenGL 3.0 or higher is required to run MineQuest.\n You have OpeGL Version: " + glfwGetVersionString() + " installed!");
		}
	}
	
	public void clear(float r, float g, float b) {
		
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
		GL11.glClearColor(r, g, b, 1);
	}
	
	public void update() {
		
		glfwSwapBuffers(this.id);
	}
	
	public void pollEvents() {
		glfwPollEvents();
	}
	
	public boolean shouldClose() {
		return glfwWindowShouldClose(this.id);
	}
	
	public void close() {
		glfwTerminate();
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}

	public void setTitle(String updatedTitle) {
		glfwSetWindowTitle(id, updatedTitle);
	}
	
	public String getTitle() {
		return title;
	}
	
	public long getID() {
		return id;
	}
}
