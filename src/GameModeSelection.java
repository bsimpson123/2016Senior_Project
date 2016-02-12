import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.util.HashMap;

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

//import GameMode.LoadState;
/**
 * Base game class. This class is the first to run during program startup and acts
 * as the root of the game control logic.
 * @author Brock
 */
public class GameModeSelection implements GameMode{
	protected LoadState currentState = LoadState.NOT_LOADED;
	protected HashMap<String, Texture> localTexMap = new HashMap<String, Texture>(10);

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

	
	private int optionBoxOffset = 0;
	private boolean offset = false;
	/* Static game control and logic variables */
	private static long timerTicksPerSecond = Sys.getTimerResolution();
	
	/**
	 * Enumeration for the current game mode when determining logic control within the main loop.
	 * Additional values can be added as new game modes are developed.
	 * @author John Ojala
	 */
	private final int GameModeSelection = 0,
		BlockMatchStandard = 1,
		PracticeMode = 2,
		HighScore = 3
		;
	private boolean pageBack = false;
	/** The current game mode within the main logic loop. */
	int activeGameMode = GameModeSelection;
	
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
			new String[] { "yellowtiles", "media/spritesheet_tilesYellow.png" }
	};
	
	/* Menu display and control variables */
	private int cursorPos = 0;
	private Sprite[] selector = new Sprite[2];
	private Sprite optionFrameTop;
	private Sprite optionFrameMid;
	private Sprite optionFrameBottom;
	private Sprite optionBox;
	
	//protected long movementInputDelay = Global.inputReadDelayTimer;

	private long mouseDelay = Global.inputReadDelayTimer;
	
	private Thread gameModeLoader = null;
	
	/** The time remaining (milliseconds) until the next movement input can be read. */
	private long movementInputDelay = Global.inputReadDelayTimer;
	
	private GameMode game;
	
	public GameModeSelection() {
		
	}
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
	


	
	/**
	 * Attempts to get a preloaded texture. Attempting to request a texture that
	 * has not been loaded will return null.
	 * @param reference File reference to the texture to load.
	 * @return The loaded OpenGL texture, null if the referenced texture was not available.
	 */
	public Texture getTexture(String reference) {
		if (Global.textureMap.containsKey(reference)) {
			return Global.textureMap.get(reference);
		}
		return null;
	}
	
	
	
	

	public GameModeSelection(boolean runFullscreen) {
		fullscreen = runFullscreen;
		//initGL(); // setup OpenGL
		//initComponents(); // setup game variables
	}
	
	public void run() {
		currentState = LoadState.READY;
		//movementInputDelay = Global.inputReadDelayTimer;

		optionBoxOffset = 100;
		if (cursorPos == 0) {
			optionBox.draw(180 + optionBoxOffset, 180);
			optionBox.draw(180, 250);
			optionBox.draw(180, 320);
			optionBox.draw(180, 390);
			
			selector[0].draw(160 + optionBoxOffset, 187 + cursorPos * 70);
			selector[1].draw(351 + optionBoxOffset, 187 + cursorPos * 70);
		} 
		if (cursorPos == 1) {
			optionBox.draw(180, 180);
			optionBox.draw(180 + optionBoxOffset, 250);
			optionBox.draw(180, 320);
			optionBox.draw(180, 390);
			
			selector[0].draw(160 + optionBoxOffset, 187 + cursorPos * 70);
			selector[1].draw(351 + optionBoxOffset, 187 + cursorPos * 70);
		}
		if (cursorPos == 2) {
		optionBox.draw(180, 180);
		optionBox.draw(180, 250);
		optionBox.draw(180 + optionBoxOffset, 320);
		optionBox.draw(180, 390);
		
		selector[0].draw(160 + optionBoxOffset, 187 + cursorPos * 70);
		selector[1].draw(351 + optionBoxOffset, 187 + cursorPos * 70);
		}
		if (cursorPos == 3) {
			optionBox.draw(180, 180);
			optionBox.draw(180, 250);
			optionBox.draw(180, 320);
			optionBox.draw(180 + optionBoxOffset, 390);
			
			selector[0].draw(160 + optionBoxOffset, 187 + cursorPos * 70);
			selector[1].draw(351 + optionBoxOffset, 187 + cursorPos * 70);
		}
		switch(activeGameMode) {
			case GameModeSelection:
				moveCursor();
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
					activeGameMode = GameModeSelection;
					movementInputDelay = Global.inputReadDelayTimer;
					cursorPos = 0;
					break;
				default:
					break;
				}
				break;
				
		}

		if (pageBack) {
			cleanup();
		}
		selector[0].draw(new int[] { mouseX, mouseY }, new int[] { 64, 64 });
		
		
		/*while (gameRunning) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glMatrixMode(GL_MODELVIEW);
			glLoadIdentity();
			
			//renderGL();
			Display.update();
		}
		// release all textures loaded
		for (String ref : Global.textureMap.keySet()) {
			Global.textureMap.get(ref).release();
		}
		Global.textureMap.clear();*/
	}
	
	/*public static void main(String[] args) {
		System.out.println("Use -fullscreen for fullscreen mode.");
		new Game( (args.length > 0) && args[0].equalsIgnoreCase("-fullscreen") ).run();

	}*/
	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		currentState = LoadState.LOADING_ASSETS;
		// TODO Auto-generated method stub
		Texture tex;
		String type; // holds file type extension
		String source; // absolute file path to resource
		for (String ref[] : texLoadList) {
			// Load local textures
			type = ref[1].substring(ref[1].lastIndexOf('.')).toUpperCase();
			tex = null;
			source = ref[1];
			 try {
				 source = FileResource.requestResource(ref[1]);
				 tex = TextureLoader.getTexture(type, ResourceLoader.getResourceAsStream(ref[1]));
				 if ( localTexMap.putIfAbsent(ref[0], tex) != null) {
					 // report error, attempting to add duplicate key entry
					 System.out.printf("Attempting to load multiple textures to key [%s]", ref[0]);
					 System.out.printf("Texture resource [%s] not loaded.", ref[1]);
				 }
				 localTexMap.put(source, tex);
			 } catch (IOException e) {
				 System.out.printf("Unable to load texture resource %s\n", source);
				 e.printStackTrace();
				 System.exit(-1);
			 }
		}
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
		Block.initializeBlocks(Global.textureMap);
		
		currentState = LoadState.LOADING_DONE;
		return;
	}
	@Override
	public LoadState getState() {
		// TODO Auto-generated method stub
		return currentState;
	}
	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		for (Texture ref : localTexMap.values()) {
			ref.release();
		}
		localTexMap.clear();
		
		/* Indicate that the game mode had complete unloading and is ready to
		 * return control to previous control loop.
		 */
		currentState = LoadState.FINALIZED;
	}
	
	public void moveCursor() {
		if (movementInputDelay <= 0) {
		if (Global.getControlActive(Global.GameControl.UP)) {
			cursorPos--;
			if (cursorPos < 0) {
				
				cursorPos = 3;
			}
			movementInputDelay = Global.inputReadDelayTimer;
		}
		if (Global.getControlActive(Global.GameControl.DOWN)) {
			cursorPos++;
			if (cursorPos > 3) {
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
					game = new BlockBreakStandard();
					activeGameMode = BlockMatchStandard;
					break;
				case 1:
					game = new BlockBreakStandard();
					activeGameMode = BlockMatchStandard;
					break;
					
				case 2:
					//game = 
					//pageBack = true;
					//activeGameMode = MainMenu;
					//gameRunning = false;
					break;
				case 3:
					pageBack = true;
					break;
			}
			
		}} else if (movementInputDelay > 0) {
			movementInputDelay -= Global.delta;
	}

		}
		
	

}

/*class GameModeLoader implements Runnable {
	private final GameMode mode;
	
	public GameModeLoader(GameMode gm) {
		mode = gm;
		System.out.println("Debug: Loader thread constructor called.");
	}
	
	public void run() {
		System.out.println("Debug: Loader thread active.");
		mode.initialize();
	}
}*/
