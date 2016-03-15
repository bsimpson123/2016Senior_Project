import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.lwjgl.input.Controller;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;
/**
 * Base game class. This class is the first to run during program startup and acts
 * as the root of the game control logic.
 * @author John
 */
public class Game {
	/* Base game variables. These are needed for the basics of the framework to function. */
	/** Hash map for referencing audio files that have been loaded. */
	private HashMap<String, Audio> soundMap = new HashMap<String, Audio>();
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
	
	/**
	 * Enumeration for the current game mode when determining logic control within the main loop.
	 * Additional values can be added as new game modes are developed.
	 * @author John Ojala
	 */
	private final int MainMenu = 0,
		GameModeSelection = 1,
		BlockMatchStandard = 2
		;
	
	/** The current game mode within the main logic loop. */
	int activeGameMode = MainMenu;
	
	/* game control settings */
	/** Indicates whether the keyboard can be for input in the game */
	@SuppressWarnings("unused")
	private final boolean useKeyboard = true;
	/** Indicates whether the mouse can be used for input in the game */
	private final boolean useMouse = false;
	/** Indicates whether a game controller can be used for input in the game */
	@SuppressWarnings("unused")
	private final boolean useController = false;
	/** Set whether the mouse cursor should be captured during game play */
	private final boolean captureMouse = false;
	/** Indicates the currently selected option at the main menu */
	private int mainMenuSelection = 0;

	/* Game specific settings */
	/** The text that is shown in the title of the window */
	private final String WINDOW_TITLE = "Block Breaker";
	
	/* Define sound variables */ 
	// TODO: Add all sound/music variable objects
	/** List of all sounds that can be played */
	private String[] soundEffectResource = { };
	/** List of all background sounds that will be played */
	private String[] soundBackgroundResource = { };
	
	/** All texture objects that will be used */
	private final String[][] texLoadList = {
			new String[] { "menubar", "media/mbar.png" },
			new String[] { "menucursor", "media/menu_selector.png" },
			new String[] { "blocksheet", "media/puzzleAssets_sheet.png" },
			new String[] { "menubartext", "media/mbartext.png" },
			new String[] { "blue_ui", "media/blueSheet.png" }, 
			new String[] { "green_ui", "media/greenSheet.png" },
			new String[] { "red_ui", "media/redSheet.png" },
			new String[] { "yellow_ui", "media/yellowSheet.png" },
			new String[] { "grey_ui", "media/greySheet.png" },
			new String[] { "yellowtiles", "media/spritesheet_tilesYellow.png" },
			new String[] { "main_menu_background","media/main_menu_background.png"},
			new String[] { "uibox", "media/UI_Boxes.png" },
			new String[] { "title", "media/TitleDisplay.png" },
			new String[] { "Text", "media/Mode_Text.png"},
			new String[] { "overlay", "media/blackoverlay.png" }
	};
	
	/* Menu display and control variables */
	private int cursorPos = 0;
	private Sprite[] selector = new Sprite[2];
	private Sprite optionFrameTop;
	private Sprite optionFrameMid;
	private Sprite optionFrameBottom;
	private Sprite optionBox;
	private Sprite menu_background;
	private Sprite title;
	
	private long mouseDelay = Global.inputReadDelayTimer;
	private Thread gameModeLoader = null;
	
	/** The time remaining (milliseconds) until the next movement input can be read. */
	private long movementInputDelay = 0;
	
	private GameMode game;
	
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
			DisplayMode [] dm = org.lwjgl.util.Display.getAvailableDisplayModes(Global.winWidth, Global.winHeight, -1, -1, -1, -1, 60, 60);
			org.lwjgl.util.Display.setDisplayMode(dm, new String[] {
					"width=" + Global.winWidth, 
					"height=" + Global.winHeight,
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
	 * Initialize OpenGL components and set OpenGL environment variables.
	 */
	private void initGL() {
		try {
			setDisplayMode();
			Display.setTitle(WINDOW_TITLE);
			Display.setFullscreen(fullscreen);
			Display.create();
			glViewport(0, 0, Global.glEnvWidth, Global.glEnvHeight);
			// Initialize GL matrices
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			// Sets 2D mode; no perspective
			glOrtho(0, Global.glEnvWidth, Global.glEnvHeight, 0, -1, 1);
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();
			// disable the OpenGL depth test since only 2D graphics are used
			glDisable(GL_DEPTH_TEST);
			glDisable(GL_LIGHTING);
			glDisable(GL_FOG);
			glDisable(GL_CULL_FACE);
			// clear the screen
			glClearColor(0f, 0f, 0f, 0f);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			// enable textures
			glEnable(GL_TEXTURE_2D);
			// Enable alpha processing for textures
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			glEnable(GL_BLEND);
		} catch (LWJGLException glErr) {
			System.out.println("Game exiting - exception in initialization:");
			glErr.printStackTrace();
			System.exit(-1);
		}
	}

	/**
	 * Initialize game-specific variables: player data, enemy data, sounds, and textures.
	 */
	private void initComponents() {
		// grab the mouse (hides the cursor while playing)
		Mouse.setGrabbed(captureMouse);
		Global.globalInit();
		// TODO: add all game variables to be loaded/initialized at the start of the program
		// Setup default game controls.
		Global.setKeyMap(Global.GameControl.UP, Keyboard.KEY_UP);
		Global.setKeyMap(Global.GameControl.DOWN, Keyboard.KEY_DOWN);
		Global.setKeyMap(Global.GameControl.LEFT, Keyboard.KEY_LEFT);
		Global.setKeyMap(Global.GameControl.RIGHT, Keyboard.KEY_RIGHT);
		Global.setKeyMap(Global.GameControl.SELECT, Keyboard.KEY_X);
		Global.setKeyMap(Global.GameControl.SELECT, Keyboard.KEY_RETURN);
		Global.setKeyMap(Global.GameControl.SELECT, Keyboard.KEY_SPACE);
		Global.setKeyMap(Global.GameControl.CANCEL, Keyboard.KEY_W);
		Global.setKeyMap(Global.GameControl.CANCEL, Keyboard.KEY_ESCAPE);
		Global.setKeyMap(Global.GameControl.PAUSE, Keyboard.KEY_F);
		Global.setKeyMap(Global.GameControl.SPECIAL1, Keyboard.KEY_A);
		Global.setKeyMap(Global.GameControl.SPECIAL2, Keyboard.KEY_D);
		
		// gamepad controls
		Controller gamepad = Global.getController();
		if (gamepad != null) {
			// add gamepad controls here;
			Global.setGamePadMap(Global.GameControl.SELECT, 1);
			Global.setGamePadMap(Global.GameControl.CANCEL, 3);
			Global.setGamePadMap(Global.GameControl.SPECIAL1, 2);
			Global.setGamePadMap(Global.GameControl.SPECIAL2, 0);
			Global.setGamePadMap(Global.GameControl.PAUSE, 9);
			
		}
		// Load all used textures into memory so the game will not be slowed down by loading textures later
		Texture tex;
		String type; // holds file type extension
		String source; // absolute file path to resource
		for (String ref[] : texLoadList) {
			type = ref[1].substring(ref[1].lastIndexOf('.')).toUpperCase();
			tex = null;
			source = ref[1];
			 try {
				source = FileResource.requestResource(ref[1]);
				tex = TextureLoader.getTexture(type, ResourceLoader.getResourceAsStream(ref[1]));
				if ( Global.textureMap.putIfAbsent(ref[0], tex) != null) {
					// report error, attempting to add duplicate key entry
					Global.writeToLog(
						String.format("Attempting to load multiple textures to key [%s]\n" + 
							"Texture resource [%s] not loaded.", ref[0], ref[1]), true);
				 }
			 } catch (IOException e) {
				 Global.writeToLog(String.format("Unable to load texture resource %s\n", source), true);
				 e.printStackTrace();
				 System.exit(-1);
			 }
		}
		
		Global.buildStandardUIBoxes();
		// TODO: Load all Sprite objects for menu navigation
		optionFrameTop = new Sprite(
				Global.textureMap.get("blue_ui"),
				new int[] { 0, 49 },
				new int[] { 190, 20 },
				new int[] { 250, 20 }
			);
		optionFrameMid = new Sprite(
				Global.textureMap.get("blue_ui"),
				new int[] { 0, 59 },
				new int[] { 190, 20 },
				new int[] { 250, 300 }
			);
		optionFrameBottom = new Sprite(
				Global.textureMap.get("blue_ui"),
				new int[] { 0, 69 },
				new int[] { 190, -20 },
				new int[] { 250, 20 }
			);
		
		optionBox = new Sprite(
				Global.textureMap.get("green_ui"),
				new int[] { 0, 0 },
				new int[] { 190, 48 },
				new int[] { 190, 48 }
			);
		
		selector[0] = new Sprite( // left-side arrow
				Global.textureMap.get("grey_ui"),
				new int[] { 39, 478 },
				new int[] { 38, 30 },
				new int[] { 38, 30 }
			);
		selector[1] = new Sprite( // right-side arrow
				Global.textureMap.get("grey_ui"),
				new int[] { 0, 478 },
				new int[] { 38, 30 },
				new int[] { 38, 30 }
			);
		menu_background = new Sprite(
				Global.textureMap.get("main_menu_background"),
				new int[] {0,0},
				new int[] {1024,768},
				new int[] {1024,768}
			);
		
		title = new Sprite(
				Global.textureMap.get("title"),
				new int[] { 0, 0 },
				new int[] { 1024, 256 },
				new int[] { 1024, 256 }
			);
		
		Audio sound;
		for (String ref : soundEffectResource) {
			sound = null;
			source = ref;
			try {
				source = FileResource.requestResource(ref);
				type = source.substring(source.lastIndexOf('.') + 1).toUpperCase(); 
				sound = AudioLoader.getAudio(type, ResourceLoader.getResourceAsStream(source));
				soundMap.put(ref, sound);
			} catch (IOException e) {
				Global.writeToLog(String.format("Unable to load sound resource: %s\n%s", source, e.getMessage()), true);
				System.out.println(e.getMessage());
			}
		}
		
		// TODO: add static class initializers
		Block.initializeBlocks(Global.textureMap);
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
		Global.delta = getTime() - lastLoopTime;
		lastLoopTime = getTime();
		lastFpsTime += Global.delta;
		fps++;
		// update the FPS counter if a second has passed
		if (lastFpsTime >= 1000) {
			Display.setTitle( String.format("%s (FPS: %d)", WINDOW_TITLE, fps) );
			lastFpsTime = 0;
			fps = 0;
		}

		// Screen location checking. this will output mouse click locations in /every/ gamemode to the console
		// This is a dev/debug feature and will not carry over to the final version
		if (mouseDelay <= 0) {
			if (Mouse.isButtonDown(0)) {
				mouseX = Mouse.getX();
				mouseY = Global.glEnvHeight - Mouse.getY();
				System.out.printf("Mouse click at %d, %d\n", mouseX, mouseY);
				mouseDelay = Global.inputReadDelayTimer;
			} 
		} else {
			mouseDelay -= Global.delta;
		}
		
		/* Checks for an active menu that will be drawn in place of normal game code. 
		 * Menu comments are only suggestions, and any number can be added. Each menu
		 * function is responsible for changing the activeMenu variable to represent
		 * the current function call
		 */
		switch (activeGameMode) {
		case MainMenu:
			if (movementInputDelay <= 0) {
				/*// Left and Right inputs do nothing at the main menu currently
				if (Global.getControlActive(Global.GameControl.LEFT)) { ; }
				if (Global.getControlActive(Global.GameControl.RIGHT)) { ; }
				//*/
				if (Global.getControlActive(Global.GameControl.UP)) {
					cursorPos--;
					if (cursorPos < 0) {
						cursorPos = 2;
					}
					movementInputDelay = Global.inputReadDelayTimer;
				}
				if (Global.getControlActive(Global.GameControl.DOWN)) {
					cursorPos++;
					if (cursorPos > 2) {
						cursorPos = 0;
					}
					movementInputDelay = Global.inputReadDelayTimer;
				}
				if (Global.getControlActive(Global.GameControl.CANCEL)) { // Cancel key moves the cursor to the program exit button
					cursorPos = 2;
				}
	
			if (Global.getControlActive(Global.GameControl.SELECT)) {
				switch (cursorPos) {
					case 0:
					case 1:
						game = new BlockBreakStandard();
						activeGameMode = GameModeSelection;
						break;
					case 2:
						gameRunning = false;
						break;
				}
			}
			} else if (movementInputDelay > 0) {
				movementInputDelay -= Global.delta;
			}
			// Draw the frame that will contain the option boxes
			menu_background.draw(0,0);
			title.draw(0, 50);
			optionFrameTop.draw(400, 350);
			optionFrameMid.draw(400, 370);
			optionFrameBottom.draw(400, 670);
			
			// Draw the option boxes
			optionBox.draw(430, 380);
			optionBox.draw(430, 450);
			optionBox.draw(430, 520);
			
			selector[0].draw(410, 387 + cursorPos * 70);
			selector[1].draw(600, 387 + cursorPos * 70);
			//selector[0].draw(new int[] { mouseX, mouseY }, new int[] { 64, 64 });
			//Global.uiBlue.draw(mouseX, mouseY, 240, 48);

			break;
		default:
			switch(game.getState()) { 
				case NOT_LOADED:
					gameModeLoader = new Thread( new GameModeLoader(game) );
					gameModeLoader.run();
					break;
				case LOADING_ASSETS:
					// TODO: game mode loading indicator
					break;
				case LOADING_DONE:
					try {
						gameModeLoader.join();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					// break statement is intentionally missing here
				case READY:
					game.run();
					break;
				case FINALIZED:
					game = null;
					activeGameMode = MainMenu;
					movementInputDelay = Global.inputReadDelayTimer;
					cursorPos = 0;
					break;
				default:
					break;
			}
			break;
		}
		
		// an exit key is strongly recommended if mouse capture is enabled
		if ( Display.isCloseRequested() ) {
			gameRunning = false; // indicate that the game is no longer running
		}
		
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
		AL.destroy();
		Global.globalFinalize();
		// release all textures loaded
	}
	
	public static void main(String[] args) {
		System.setProperty("java.library.path", new File("native/windows").getAbsolutePath());
		System.setProperty("org.lwjgl.librarypath", new File("native/windows/").getAbsolutePath());
		System.out.println("Use -fullscreen for fullscreen mode.");
		new Game( (args.length > 0) && args[0].equalsIgnoreCase("-fullscreen") ).run();

	}

}

class GameModeLoader implements Runnable {
	private final GameMode mode;
	
	public GameModeLoader(GameMode gm) {
		mode = gm;
		Global.writeToLog("Debug: Loader thread constructor called.");
	}
	
	public void run() {
		Global.writeToLog("Debug: Loader thread active.");
		mode.initialize();
	}
}
