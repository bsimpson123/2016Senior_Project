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
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.Color;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
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
	///** indicated whether that game should be running in full-screen mode. Default is false. */
	//private boolean Global.fullscreen = false;
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
		ConfigurationScreen = 2
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
			//new String[] { "main_menu_background","media/main_menu_background.png"},
			new String[] { "main_menu_background", "media/title_68e14f.png" },
			new String[] { "uibox", "media/UI_Boxes.png" },
			new String[] { "title", "media/TitleDisplay.png" },
			new String[] { "Text", "media/Mode_Text.png"},
			new String[] { "overlay", "media/blackoverlay.png" }, 
			new String[] { "bomb_numbers", "media/numbers_small.png" },
			new String[] { "pause_text", "media/pause_text.png"},
			new String[] { "heart", "media/tileRed_36_small.png"},
			new String[] { "white_ui_controls", "media/sheet_white2x.png"}
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
	

	private final String[] menuOptions = new String[] {
		"Options",
		"Credits",
		"Exit"
	};
	private int[] menuOptionOffset = new int[3];
	
	private final String[] modeOptions = new String[] {
		"Standard Mode",
		"Puzzle Mode"
	};
	private int[] modeOptionOffset = new int[2];
	
	private long mouseDelay = Global.inputReadDelayTimer;
	private Thread gameModeLoader = null;
	
	/** The time remaining (milliseconds) until the next movement input can be read. */
	private long movementInputDelay = 0;
	
	private GameMode game;
	
	private int GameModeType = 0;
	private Sprite[] GameModeArrows = new Sprite[2];
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
			System.out.println("Unable to enter Global.fullscreen, continuing in windowed mode.");
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
			Display.setFullscreen(Global.fullscreen);
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
			glEnable(GL_SMOOTH);
	        glShadeModel(GL11.GL_SMOOTH);

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
		Configuration.setupDefaultValues();

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
		GameModeArrows[0] = new Sprite(
				Global.textureMap.get("white_ui_controls"),
				new int[] { 300, 600 },
				new int[] { 100, 100 },
				new int[] { 50, 50 }
			);
		GameModeArrows[1] = new Sprite(
				Global.textureMap.get("white_ui_controls"),
				new int[] { 200, 300 },
				new int[] { 100, 100 },
				new int[] { 50, 50 }
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
		
		for (int i = 0; i < menuOptions.length; i++) {
			menuOptionOffset[i] = Global.getFont24DrawSize(menuOptions[i]) / 2;
		}
		for (int i = 0; i < modeOptions.length; i++) {
			modeOptionOffset[i] = Global.getFont24DrawSize(modeOptions[i]) / 2;
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
				if (cursorPos == 0) {
					inputGameModeMenu();
				}
				if (Global.getControlActive(Global.GameControl.UP)) {
					cursorPos--;
					Global.sounds.playSoundEffect("button_click");
					//if (cursorPos == 0) {
					//	inputGameModeMenu();
					//}
					if (cursorPos < 0) {
						cursorPos = 3;
					}
					movementInputDelay = Global.inputReadDelayTimer;
				}
				if (Global.getControlActive(Global.GameControl.DOWN)) {
					cursorPos++;
					Global.sounds.playSoundEffect("button_click");

					//if (cursorPos == 0) {
					//	inputGameModeMenu();
					//}
					if (cursorPos > 3) {
						cursorPos = 0;
					}
					movementInputDelay = Global.inputReadDelayTimer;
				}
				if (Global.getControlActive(Global.GameControl.CANCEL)) { // Cancel key moves the cursor to the program exit button
					cursorPos = 3;
				}
	
			if (Global.getControlActive(Global.GameControl.SELECT)) {
				switch (cursorPos) {
					case 0: // Game play
						switch (GameModeType) {
							case 0:
								game = new BlockBreakStandard();
								break;
							case 1:
								game = new BlockPuzzleMode();
								break;
							default:
								break;
						}
						activeGameMode = GameModeSelection;
						break;
					case 1: // Config
						game = new Configuration();
						activeGameMode = ConfigurationScreen;
						break;
					case 2: // Credits
						
						break;
					case 3:
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
			
			// Draw the option boxes
			Global.menuButtonShader.bind();
			for (int i = 0; i < 4; i++) {
				Global.uiTransWhite.draw(430, 380 + i * 70, 190, 48);
			}
			Color.white.bind();
			//Global.drawStringDefaultFont(430, 380, "Standard Mode", Color.black);
			if (cursorPos == 0) {
				GameModeArrows[0].draw(390, 378);
				Global.drawFont24(525 - modeOptionOffset[GameModeType], 393, modeOptions[GameModeType], Color.white);
				GameModeArrows[1].draw(610, 378);
				//selector[1].draw(390, 387 + cursorPos * 70);
				//selector[0].draw(620, 387 + cursorPos * 70);

			} else {
					Global.drawFont24(525 - modeOptionOffset[GameModeType], 393, modeOptions[GameModeType], Color.black);
			}
			for (int i = 0; i < 3; i++) {
				if (cursorPos == (i + 1)) {
					Global.drawFont24(525 - menuOptionOffset[i], 463 + i * 70, menuOptions[i], Color.white);
				} else {
					Global.drawFont24(525 - menuOptionOffset[i], 463 + i * 70, menuOptions[i], Color.black);
				}
			}
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
	
	/*
	 * @Author Brock
	 */
	private void inputGameModeMenu() {
		movementInputDelay -= Global.delta;
		
		if (movementInputDelay > 0) { return; }
		
		if (Global.getControlActive(Global.GameControl.LEFT)) {
			GameModeType--;
			Global.sounds.playSoundEffect("button_click"); 
			if (GameModeType < 0) { GameModeType = 1; }
			movementInputDelay = Global.inputReadDelayTimer;
		} else if (Global.getControlActive(Global.GameControl.RIGHT)) {
			GameModeType++;
			Global.sounds.playSoundEffect("button_click"); 
			if (GameModeType > 1) { GameModeType = 0; }
			movementInputDelay = Global.inputReadDelayTimer;
		} 		
	}
	
	public Game(boolean runFullscreen) {
		Global.fullscreen = runFullscreen;
		initGL(); // setup OpenGL
		initComponents(); // setup game variables
	}
	
	public void run() {
		while (gameRunning) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();
			Keyboard.poll();
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
