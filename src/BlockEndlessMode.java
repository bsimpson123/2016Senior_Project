import java.io.IOException;
import java.util.HashMap;


import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

/*Author Mario hernandez*/
public class BlockEndlessMode implements GameMode{
	protected LoadState currentState = LoadState.NOT_LOADED;
	protected HashMap<String, Texture> localTexMap = new HashMap<String, Texture>(10);
	protected int cursorPos = 0;
	private BlockBreakLevel playLevel;

	protected int[] blockOffSet = new int[] { 32, 32 };
	private long movementInputDelay = Global.inputReadDelayTimer;
	
	private boolean pageBack = false;
	/** The current game mode within the main logic loop. */

	protected String[][] texLoadList = new String[][] {
		new String[] { "ui_base", "media/UIpackSheet_transparent.png" },
		new String[] { "ui_stdmode", "media/StandardMode_UI.png" },
		new String[] { "bg_space_1", "media/space_bg_1064bfa.png" },
		new String[] { "number_white", "media/numbers_sheet_white.png" }, 
		new String[] { "energy_empty", "media/energy_bar_empty.png" },
		new String[] { "energybar", "media/energy_bar.png" },
		new String[] { "nLevel", "media/gNextlevel.png" },
		new String[] { "ex_game_screen", "media/game_screen.png"},
		new String[] { "Text", "media/Mode_Text.png"},
		new String[] { "white_ui_controls" , "media/sheet_white2x.png" },
		new String[] { "new_test", "media/image1.png"},
		new String[] { "bigsky", "media/bigsky_cedf10.png" }
	};
	
	private Sprite GameSelector_background;
	private Sprite ex_screen;
	private final String[] menuOptions = new String[] {
		"Play",
		"Practice",
		"High Score",
		"Back"
	};
	private int[] menuOptionOffset = new int[4];

	private Sprite[] pracArrows = new Sprite[2];

	private int maxUnlocked = 1;
	
	public BlockEndlessMode() {
		for (int i = 0; i < menuOptions.length; i++) {
			menuOptionOffset[i] = Global.getFont24DrawSize(menuOptions[i]) / 2;
		}
	}
	
	@Override
	public void initialize() {
		currentState = LoadState.LOADING_ASSETS;
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
					 Global.writeToLog(String.format("Attempting to load multiple textures to key [%s]", ref[0]));
					 Global.writeToLog(String.format("Texture resource [%s] not loaded.", ref[1]) );
				 }
				 localTexMap.put(source, tex);
			 } catch (IOException e) {
				 Global.writeToLog(String.format("Unable to load texture resource %s\n", source) );
				 e.printStackTrace();
				 System.exit(-1);
			 }
		}
// author Brock
		
		GameSelector_background = new Sprite(
				Global.textureMap.get("main_menu_background"),
				new int[] {0,0},
				new int[] {1024,768},
				new int[] {1024,768}
			);
		
		ex_screen = new Sprite (
				localTexMap.get("ex_game_screen"),
				new int[] { 0,0 },
				new int[] { 1425, 768 },
				new int[] { 1425, 600 }
				);
		
		BlockBreakLevel.buildStaticAssets(localTexMap);
		
		int offset = 0;
		for (int i = 0; i < BlockEndlessLevel.numbers.length; i++) {
			offset = i * 24 - 1;
			BlockEndlessLevel.numbers[i] = new Sprite(
					localTexMap.get("number_white"),
					new int[] { offset, 0 },
					new int[] { 24, 30 },
					new int[] { 24, 30 }
				);
		}
		pracArrows[0] = new Sprite(
				Global.textureMap.get("white_ui_controls"),
				new int[] { 300, 600 },
				new int[] { 100, 100 },
				new int[] { 50, 50 }
			);
		pracArrows[1] = new Sprite(
				Global.textureMap.get("white_ui_controls"),
				new int[] { 200, 300 },
				new int[] { 100, 100 },
				new int[] { 50, 50 }
			);
		currentState = LoadState.LOADING_DONE;
		return;
	}

	@Override
	public LoadState getState() {
		return currentState;
	}

	@Override
	public void run() {
		currentState = LoadState.READY;
		
		if (playLevel != null) {
			if (!playLevel.levelFinished) {
				playLevel.run();
			} else if (playLevel.level == 0) { 
				playLevel = null;
			} else {
				if (maxUnlocked < playLevel.level) { maxUnlocked = playLevel.level; }
				if (playLevel.gameOver || playLevel.practice) {
					movementInputDelay = Global.inputReadDelayTimer;
					playLevel = null;
				} else {
					loadLevel(playLevel.level + 1);
				}
			}
		} else {
// @author Brock
			GameSelector_background.draw(0, 0);
			moveCursorMain();
			for (int i = 0; i < menuOptions.length; i++) {
				Global.menuButtonShader.bind();
				Global.uiTransWhite.draw(180, 180 + i * 70, 190, 48);
				Color.white.bind();
				if (cursorPos == i) {
					if (i == 0 && Global.getControlActive(Global.GameControl.LEFT)) {
						Global.drawFont24(275, 195, "Grid Builder", Color.white, true);
					} else {
						Global.drawFont24(275 - menuOptionOffset[i], 195 + i * 70, menuOptions[i], Color.white);
					}
				} else {
					Global.drawFont24(275 - menuOptionOffset[i], 195 + i * 70, menuOptions[i], Color.black);
				}
			}
			switch (cursorPos) {
				case 0:
					ex_screen.draw(450, 150);
					break;
			}
		}
		
		if (pageBack) { cleanup(); }
	}
	
	@Override
	public void cleanup() {
		for (Texture ref : localTexMap.values()) {
			ref.release();
		}
		localTexMap.clear();
		currentState = LoadState.FINALIZED;
	}
	
	/**
	 * @author Brock
	 */
	private void moveCursorMain() {
		if (movementInputDelay <= 0) {
			if (Global.getControlActive(Global.GameControl.UP)) {
				cursorPos--;
				Global.sounds.playSoundEffect("button_click");
				if (cursorPos < 0) {
					
					cursorPos = 3;
				}
				movementInputDelay = Global.inputReadDelayTimer;
			}
			if (Global.getControlActive(Global.GameControl.DOWN)) {
				cursorPos++;
				Global.sounds.playSoundEffect("button_click");

				if (cursorPos > 3) {
					cursorPos = 0;
				}
				movementInputDelay = Global.inputReadDelayTimer;
			}
			if (Global.getControlActive(Global.GameControl.CANCEL)) { // Cancel key moves the cursor to the program exit button
				cursorPos = 1;
			}
			if (Global.getControlActive(Global.GameControl.SELECT)) {
				switch (cursorPos) {
					case 0: // normal mode
						if (Global.getControlActive(Global.GameControl.LEFT)) {
							playLevel = new BlockStandardLevelBuilder(-1);
							playLevel.levelTitle = "Build Mode";
						} else {
							loadLevel(1);
						}
						BlockBreakLevel.score = 0;
						break;
					case 3: // exit
					default:
						pageBack = true;
						break;
				}
				movementInputDelay = 2 * Global.inputReadDelayTimer;
			}
		} else if (movementInputDelay > 0) {
			movementInputDelay -= Global.delta;
		}
	}
	
	private void loadLevel(int levelID) {
		playLevel = new BlockEndlessLevel(levelID);
	}	
}