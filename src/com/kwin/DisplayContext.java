package com.kwin;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.PixelFormat;

public class DisplayContext {
	
	private int DISPLAY_WIDTH = 800;
	private int DISPLAY_HEIGHT = 800;
	public String DISPLAY_TITLE = "Error - Not Named";
	
	private static DisplayContext instance = null;
	private Game game;
	public Log log;
	
	public static DisplayContext createInstance(int width, int height, Game game) {
		if (instance == null) {
			return new DisplayContext(width, height, game);
		}
		
		else {
			System.out.println("Instance already acquired!");
			return null;
		}
	}
	
	public static DisplayContext getInstance() {
		if (instance != null) 
			return instance;
		return null;
	}
	
	protected DisplayContext(int width, int height, Game game) {
		this.DISPLAY_WIDTH = width;
		this.DISPLAY_HEIGHT = height;
		this.game = game;
		
		log = Log.getInstance();
		log.write("Display Context initialized.");
	}
	
	public int Width() { return DISPLAY_WIDTH; }
	public int Height() { return DISPLAY_HEIGHT; }
	
	public void start() {
		try {
			Display.create();
			String maxGLVer = GL11.glGetString(GL11.GL_VERSION);
			System.out.println("Max OpenGL Version Available - " + maxGLVer);
			Display.destroy();
			
			PixelFormat pixelFormat = new PixelFormat();
			ContextAttribs contextAttributes = new ContextAttribs(3, 2);
			contextAttributes.withForwardCompatible(true);
			
			if (maxGLVer.equals("4.2.0")) {
				contextAttributes.withProfileCore(true);
				System.out.println("Core Profile Activated!");
			}
			
			log.write("Display Context was set to OpenGL 3.2.");
			
			Display.setDisplayMode(new DisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT));
			setDisplayMode(DISPLAY_WIDTH, DISPLAY_HEIGHT, true);
			Display.create(pixelFormat, contextAttributes);
			
			log.write("Display Characteristics - ");
			log.write("\tWidth  - " + DISPLAY_WIDTH);
			log.write("\tHeight - " + DISPLAY_HEIGHT);
			log.write("\tBPP    - " + Display.getDisplayMode().getBitsPerPixel());
			log.write("\tFreq   - " + Display.getDisplayMode().getFrequency());
			
			DISPLAY_TITLE = "OpenGL Version : " + GL11.glGetString(GL11.GL_VERSION);
			Display.setTitle(DISPLAY_TITLE);
		} 
		
		catch (LWJGLException e) {
			System.out.println("Error creating main Display Context.  Exiting application.");
			e.printStackTrace();
			System.exit(-1);
		}
		
		GL11.glViewport(0, 0, DISPLAY_WIDTH, DISPLAY_HEIGHT);
		
		game.Initialize(DISPLAY_WIDTH, DISPLAY_HEIGHT);
		
		while (!Display.isCloseRequested()) {
			if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
				break;
			}
			
			Display.sync(60);
			
			game.Update();
			game.Render();
			
			Display.update();
		}
		
		game.Dispose();
		log.flush();
		Display.destroy();
	}
	
	public void getAvailableDisplayModes() {
		DisplayMode[] modes = null;
		
		try { modes = Display.getAvailableDisplayModes(); } 
		
		catch (LWJGLException e) {
			e.printStackTrace();
			System.out.println("Unable to obtain Display Modes, exiting application.");
			System.exit(-1);
		}

		if (modes != null) {
			for (int i=0;i<modes.length;i++) {
			    DisplayMode current = modes[i];
			    System.out.println(current.getWidth() + "x" + current.getHeight() + "x" +
			                        current.getBitsPerPixel() + " " + current.getFrequency() + "Hz");
			}
		}
	}
	
	public void setDisplayMode(int width, int height, boolean fullscreen) {
	    if ((Display.getDisplayMode().getWidth() == width) && (Display.getDisplayMode().getHeight() == height) && (Display.isFullscreen() == fullscreen))
		    return;

	    try {
	        DisplayMode targetDisplayMode = null;
			
			if (fullscreen) {
			    DisplayMode[] modes = Display.getAvailableDisplayModes();
			    int freq = 0;
						
			    for (int i=0;i<modes.length;i++) {
			        DisplayMode current = modes[i];
							
			        if ((current.getWidth() == width) && (current.getHeight() == height)) {
			        	if ((targetDisplayMode == null) || (current.getFrequency() >= freq)) {
			        		if ((targetDisplayMode == null) || (current.getBitsPerPixel() > targetDisplayMode.getBitsPerPixel())) {
			        			targetDisplayMode = current;
			        			freq = targetDisplayMode.getFrequency();
		                    }
		                }
	
			        	if ((current.getBitsPerPixel() == Display.getDesktopDisplayMode().getBitsPerPixel()) && (current.getFrequency() == Display.getDesktopDisplayMode().getFrequency())) {
			        		targetDisplayMode = current;
		                    break;
			        	}
		            }
		        }
			}
			
			else targetDisplayMode = new DisplayMode(width,height);
			
		    if (targetDisplayMode == null) {
		    	System.out.println("Failed to find value mode: "+width+"x"+height+" fs="+fullscreen);
		        return;
		    }
	
		    Display.setDisplayMode(targetDisplayMode);
		    Display.setFullscreen(fullscreen);
				
	    } 
	    
	    catch (LWJGLException e) 
	    {
	        System.out.println("Unable to setup mode "+width+"x"+height+" fullscreen="+fullscreen + e);
	    }
	}
	
	public static void main(String[] args) {
		DisplayContext context = DisplayContext.createInstance(1920, 1080, new Game());
		context.start();
	}
}
