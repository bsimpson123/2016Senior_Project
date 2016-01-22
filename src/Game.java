import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class Game {
	/* Base game variables. These are needed for the basics of the framework to function. */
	/** Hash map for referencing textures that have been loaded. */
	private HashMap<String, Texture> textureMap = new HashMap<String, Texture>();
	/** Hash map for referencing audio files that have been loaded. */
	private HashMap<String, Audio> soundMap = new HashMap<String, Audio>();
	/** True if game logic need to be applied this loop, normally as a result of a game event. */
	private boolean logicRequiredThisLoop = false;
	/** Indicates whether the game is to continue running and processing logic. Set to false to end the program. */
	private boolean gameRunning = true;
	/** The time at which the last rendering loop started from the point of view of the game logic. */
	private long lastLoopTime = getTime();
	/** The time since the last record of FPS. */
	private long lastFpsTime;
	/** The recorded fps. */
	private int fps;
	/** indicated whether that game should be running in full-screen mode. Default is false. */
	private boolean fullscreen = false;
	/** Mouse movement left(-) or right(+) */
	private int mouseX = 0;
	/** Mouse movement up(-) or down(+) */
	private int mouseY = 0;

	/* Static game control and logic variables */
	private static long timerTicksPerSecond = Sys.getTimerResolution();
	
	/* Static texture variables. These should only be altered during program startup and shutdown/cleanup */
	private static Texture blocks;
	
	/** Sets which menu overlay is active in place of the main game draw. 
	 * Use peek() to determine the current menu without discarding the value.*/
	private static Stack<Integer> activeMenu = new Stack<Integer>();
	
	/* game control settings */
	/** Indicates whether the keyboard can be for input in the game */
	private final boolean useKeyboard = true;
	/** Indicates whether the mouse can be used for input in the game */
	private final boolean useMouse = false;
	/** Indicates whether a game controller can be used for input in the game */
	private final boolean useController = false;
	/** Set whether the mouse cursor should be captured during game play */
	private final boolean captureMouse = true;

	/* Game specific settings */
	/** The text that is shown in the title of the window */
	private final String WINDOW_TITLE = "Block Breaker";
	/** The draw width of the OpenGL window area */
	private int width = 800;
	/** The draw height of the OpenGL window area */
	private int height = 600;
	
	/* Define sound variables */ 
	// TODO: Add all sound/music variable objects
	/** List of all sounds that can be played */
	private String[] soundEffectResource = { };
	/** List of all background sounds that will be played */
	private String[] soundBackgroundResource = { };
	
	/** All texture objects that will be used */
	private String[] texLoadList = { };
	
	/**
	 * Get the high resolution time in milliseconds
	 * @return The high resolution time in milliseconds
	 */
	public static long getTime() {
		// we get the "timer ticks" from the high resolution timer
		// multiply by 1000 so our end result is in milliseconds
		// then divide by the number of ticks in a second giving
		// a clear time in milliseconds
		return (Sys.getTime() * 1000) / timerTicksPerSecond;
	}
	
	/**
	 * Sleep for a fixed number of milliseconds.
	 * @param duration The amount of time in milliseconds to sleep for
	 */
	public static void sleep(long duration) {
		try {
			Thread.sleep( (duration * timerTicksPerSecond) / 1000);
		} catch (InterruptedException e) {
			// do nothing
		}
	}
	
	private boolean setDisplayMode() {
		try {
			// get modes
			DisplayMode [] dm = org.lwjgl.util.Display.getAvailableDisplayModes(width, height, -1, -1, -1, -1, 60, 60);
			org.lwjgl.util.Display.setDisplayMode(dm, new String[] {
					"width=" + width, 
					"height=" + height,
					"freq=" + 60,
					"bpp=" + org.lwjgl.opengl.Display.getDisplayMode().getBitsPerPixel()
			});
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Unable to enter fullscreen, continuing in windowed mode.");
		}
		return false;
	}

	
	/**
	 * Initialize OpenGL components
	 */
	private void initGL() {
		try {
			setDisplayMode();
			Display.setTitle(WINDOW_TITLE);
			Display.setFullscreen(fullscreen);
			Display.create();
			
			// enable textures since they are needed for sprites
			glEnable(GL_TEXTURE_2D);
			
			glClearColor(0f, 0f, 0f, 0f);
			
			// disable the OpenGL depth test since only 2D graphics are used
			glDisable(GL_DEPTH_TEST);
			
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			
			glOrtho(0, width, height, 1, -1, 1);
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();
			glViewport(0, 0, width, height);
			
			// Enable Alpha graphics processing
			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

			
		} catch (LWJGLException le) {
			System.out.println("Game exiting - exception in initialization:");
			le.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Initialize game-specific variables: player data, enemy data, sounds, and textures.
	 */
	private void initComponents() {
		// grab the mouse (hides the cursor while playing)
		Mouse.setGrabbed(captureMouse);
		// TODO: add all game variables to be loaded/initialized at the start of the program
		// Setup default game controls.
		Global.setKeyMap(Global.GameControl.UP, Keyboard.KEY_UP);
		Global.setKeyMap(Global.GameControl.DOWN, Keyboard.KEY_DOWN);
		Global.setKeyMap(Global.GameControl.LEFT, Keyboard.KEY_LEFT);
		Global.setKeyMap(Global.GameControl.RIGHT, Keyboard.KEY_RIGHT);
		Global.setKeyMap(Global.GameControl.SELECT, Keyboard.KEY_X);
		Global.setKeyMap(Global.GameControl.SELECT, Keyboard.KEY_RETURN);
		Global.setKeyMap(Global.GameControl.CANCEL, Keyboard.KEY_W);
		Global.setKeyMap(Global.GameControl.CANCEL, Keyboard.KEY_ESCAPE);
		
		
		
		
		activeMenu.push(0);
		activeMenu.push(1);
		
		// Load all used textures into memory so the game will not be slowed down by loading textures later
		Texture tex;
		String type; // holds file type extension
		String source; // absolute file path to resource
		for (String ref : texLoadList) {
			type = ref.substring(ref.lastIndexOf('.')).toUpperCase();
			tex = null;
			source = ref;
			 try {
				 source = FileResource.requestResource(ref);
				 tex = TextureLoader.getTexture(type, ResourceLoader.getResourceAsStream(ref));
			 } catch (IOException e) {
				 System.out.printf("Unable to load texture resource %s\n", source);
				 e.printStackTrace();
				 System.exit(-1);
			 }
			 textureMap.put(source, tex);
		}
		Audio sound;
		for (String ref : soundEffectResource) {
			sound = null;
			source = ref;
			try {
				source = FileResource.requestResource(ref);
				type = source.substring(source.lastIndexOf('.')).toUpperCase(); 
				sound = AudioLoader.getAudio(type, ResourceLoader.getResourceAsStream(source));
				soundMap.put(ref, sound);
			} catch (IOException e) {
				System.out.printf("Unable to load sound resource: %s\n%s", source, e.getMessage());
				System.out.println(e.getMessage());
			}
		}
		
		
	}
	
	
	/*
	public static String requestResource(String file) throws IOException { 
		Path resourcePath;
		Path reqPath = Paths.get(file).toAbsolutePath();
		System.out.printf("Requesting resource: %s...", file);
		resourcePath = reqPath.toRealPath(LinkOption.NOFOLLOW_LINKS);
		System.out.println("Success.");
		System.out.printf("\t%s\n", resourcePath.toString());
		return resourcePath.toString();
	} //*/

	
	/**
	 * Attempts to get a preloaded texture. If the file reference has not already been loaded
	 * as a texture the game attempts to do so and adds the new texture to the texture map
	 * @param reference File reference to the texture to load.
	 * @return The loaded OpenGL texture
	 */
	public Texture getTexture(String reference) {
		Texture tex = null;
		String ref, type;
		
		
		
		try {
			ref = FileResource.requestResource(reference);
			tex = textureMap.get(reference);
			if (tex != null) { return tex; }
			type = ref.substring(ref.lastIndexOf('.')).toUpperCase();
			tex = TextureLoader.getTexture(type, ResourceLoader.getResourceAsStream(ref));
		} catch (IOException e) {
			System.out.println("Unable to load resource.");
			e.printStackTrace();
			System.exit(-1);
		}
		textureMap.put(reference, tex);
		return tex;
		
	}
	

	/**
	 * Handles input actions for the main loop
	 */
	private void processInput() {
		/* Get mouse movement on the X and Y axis
		 * Can only call getDX and getDY once each per game loop.
		 * Additional calls will return 0 or a very small value as there will have been 
		 * zero or minimal movement of the mouse within the specific game loop. 
		 */
		if (useMouse) {
			mouseX = Mouse.getDX();
			mouseY = Mouse.getDY();
			int moveX = 0;
			int moveY = 0;
		}

		
		// TODO: add required input checks and game response to actions
		// basic up/down/left/right movement checks plus a fire/shoot action:
		if (Global.getControlActive(Global.GameControl.LEFT)) { 
			;
		}
		if (Global.getControlActive(Global.GameControl.RIGHT)) { 
			;
		}
		if (Global.getControlActive(Global.GameControl.UP)) {
			;
		}
		if (Global.getControlActive(Global.GameControl.DOWN)) {
			;
		}
		if (Global.getControlActive(Global.GameControl.SELECT)) {
			;
		}
		if (Global.getControlActive(Global.GameControl.CANCEL)) {
			gameRunning = false;
		}
		if (Global.getControlActive(Global.GameControl.SPECIAL)) {
			;
		}
		// TODO: any additional process logic that needs to be performed
		
	}
	
	/**
	 * Determine the delay time since the last frame render, get the player input,
	 * move and draw entities, add, and remove necessary entities from the lists 
	 * of maintained entities. 
	 */
	private void renderGL() {
		// cap framerate to 60 fps
		Display.sync(60);
		/* determine how long it has been since the last update
		 * this will be used to calculate how far the entities
		 * should move this loop  */
		/* The time in milliseconds since the last update */
		long delta = getTime() - lastLoopTime;
		lastLoopTime = getTime();
		lastFpsTime += delta;
		fps++;
		// update the FPS counter if a second has passed
		if (lastFpsTime >= 1000) {
			Display.setTitle( String.format("%s (FPS: %d)", WINDOW_TITLE, fps) );
			lastFpsTime = 0;
			fps = 0;
		}

		/* check and handle input controls */
		processInput();
		
		/* Checks for an active menu that will be drawn in place of normal game code. 
		 * Menu comments are only suggestions, and any number can be added. Each menu
		 * function is responsible for changing the activeMenu variable to represent
		 * the current function call
		 */
		if (activeMenu.isEmpty()) { activeMenu.push(0); }
		switch (activeMenu.peek()) {
		case 0: 
			break;
		case 1: // Title screen
			// TODO: function call to display startup title screen
			return ;
		case 2: // Pause screen
			// TODO: function call to display pause screen during gameplay
			return ;
		default:
			break;
		}
		
		
		
		/* move and draw all entities */

		/* remove any entity that has been marked for removal */

		/* add new Entities that were created this loop */


		/* If a game event has indicated that game logic should be
		 * resolved, cycle though every entity requesting that each
		 * one's logic be considered.
		 */
		if (logicRequiredThisLoop) {
			// Perform special logic

		}
		
		// an exit key is strongly recommended if mouse capture is enabled
		if ( Display.isCloseRequested() || Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) {
			gameRunning = false; // indicate that the game is no longer running
		}
		
	}
	
	/**
	 * Loads a sound file into memory for faster access at runtime.
	 * @param file 
	 * @return true if the file was loaded successfully
	 */
	private boolean loadSound(String file) {
		String source = file, type;
		Audio sound = soundMap.get(file);
		if (sound != null) { return true; } // sound has already been loaded
		
		try {
			source = FileResource.requestResource(file);
			type = source.substring(source.lastIndexOf('.')).toUpperCase(); 
			sound = AudioLoader.getAudio(type, ResourceLoader.getResourceAsStream(source));
			soundMap.put(file, sound);
		} catch (IOException e) {
			System.out.printf("Unable to load sound resource: %s\n%s", source, e.getMessage());
			return false;
		}
		return true;
	}

	public Audio getSound(String ref) {
		return soundMap.get(ref);
	}
	
	/**
	 * Plays the audio as a sound effect. No effect if the value passed is null.
	 * @param sfx The audio to be played
	 */
	private void playSoundEffect(String ref) {
		Audio sfx = getSound(ref);
		if (sfx == null) { return; } // check that sfx is a defined sound object
		sfx.playAsSoundEffect(1.0f, 1.0f, false);
		return ;
	}
	
	/**
	 * Plays the audio as repeating background music. No effect if the value passed is null.
	 * @param music The audio to be played in the background
	 */
	private void playSoundMusic(Audio music) {
		if (music == null) { return; }
		// TODO: check for and stop previously playing music if any
		music.playAsMusic(1.0f, 1.0f, true);
		return ;
	}

	public Game(boolean runFullscreen) {
		fullscreen = runFullscreen;
		initGL(); // setup OpenGL
		initComponents(); // setup game variables
	}
	
	public void run() {
		while (gameRunning) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();
			
			renderGL();
			Display.update();
		}
		
	}
	
	public static void main(String[] args) {
		System.out.println("Use -fullscreen for fullscreen mode.");
		new Game( (args.length > 0) && args[0].equalsIgnoreCase("-fullscreen") ).run();

	}

}
