import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class BlockBreakStandard implements GameMode {
	protected LoadState currentState = LoadState.NOT_LOADED;
	protected HashMap<String, Texture> localTexMap = new HashMap<String, Texture>(10);
	protected int cursorPos = 0;
	//protected long inputDelay = Global.inputReadDelayTimer;
	private BlockStandardLevel playLevel;

	// Level variables. These may be moved/removed if level play is moved to separated class object.
	protected Sprite cursor;
	protected int[] blockOffSet = new int[] { 32, 32 };
	private long movementInputDelay = Global.inputReadDelayTimer;
	private long actionDelay = Global.inputReadDelayTimer;
	
	private boolean pageBack = false;
	/** The current game mode within the main logic loop. */

	protected String[][] texLoadList = new String[][] {
		new String[] { "ui_base", "media/UIpackSheet_transparent.png" },
		new String[] { "ui_stdmode", "media/StandardMode_UI.png" },
		new String[] { "bg_space_1", "media/space_bg_1064bfa.png" },
		new String[] { "number_white", "media/numbers_sheet_white.png" }, 
		new String[] { "energy_empty", "media/energy_bar_empty.png" },
		new String[] { "energybar", "media/energy_bar.png" },
		new String[] { "white_ui_controls", "media/sheet_white2x.png"},
		new String[] { "nLevel", "media/gNextlevel.png" },
		new String[] { "ex_game_screen", "media/game_screen.png"},
		new String[] { "Text", "media/Mode_Text.png"},
		new String[] { "white_ui_controls" , "media/sheet_white2x.png" },
		new String[] { "new_test", "media/image1.png"}
	};
	
	private final int GameModeSelection = 0,
		BlockMatchStandard = 1,
		PracticeMode = 2,
		HighScore = 3
		;
	int activeGameMode = GameModeSelection;

	private int optionBoxOffset = 0;
	private Sprite GameSelector_background;
	private Sprite optionBox_2;
	private Sprite[] selector = new Sprite[2];
	private Sprite optionBox;
	private Sprite play_unselect;
	private Sprite prac_unselect;
	private Sprite gamemode_unselect;
	private Sprite back_unselect;
	
	private Sprite play_select;
	private Sprite prac_select;
	private Sprite gamemode_select;
	private Sprite back_select;
	
	private Sprite label_unselect[];
	private Sprite label_select[];
	private Sprite button_select[];
	private Sprite ex_screen;
	
	//private boolean selectPractice = false;
	private Sprite pracBox;
	private Sprite[] pracArrows = new Sprite[2];
	private int pracLevel = 1;
	private int pracMax = 5;
	 
	public BlockBreakStandard() {
		// TODO: set or load any custom environment variables
		// do not load assets at this point
	}
	
	@Override
	public void initialize() {
		// This should always be the first line
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
		//moveClick = new GameSounds(GameSounds.soundType.SOUND, "media/click3.ogg");
		
		GameSelector_background = new Sprite(
				Global.textureMap.get("main_menu_background"),
				new int[] {0,0},
				new int[] {1024,768},
				new int[] {1024,768}
			);
		optionBox_2 = new Sprite(
				Global.textureMap.get("green_ui"),
				new int[] {0,143},
				new int[] {190,48},
				new int[] {190,48}
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
		
		play_unselect = new Sprite(
				localTexMap.get("new_test"),
				new int[] { 0, 0 },
				new int[] { 190, 30 },
				new int[] { 190, 48 }
			);
		play_select = new Sprite(
				localTexMap.get("new_test"),
				new int[] { 190, 0 },
				new int[] { 190, 30 },
				new int[] { 190, 48 }
			);
		
		prac_unselect = new Sprite(
				localTexMap.get("new_test"),
				new int[] { 0, 30 },
				new int[] { 190, 30 },
				new int[] { 190, 48 }
			);
		prac_select = new Sprite(
				localTexMap.get("new_test"),
				new int[] { 190, 30 },
				new int[] { 190, 30 },
				new int[] { 190, 48 }
			);
		
		gamemode_unselect = new Sprite(
				localTexMap.get("new_test"),
				new int[] { 0, 60 },
				new int[] { 190, 30 },
				new int[] { 190, 48 }
			);
		gamemode_select = new Sprite(
				localTexMap.get("new_test"),
				new int[] { 190, 60 },
				new int[] { 190, 27 },
				new int[] { 190, 48 }
			);
		
		back_unselect = new Sprite(
				localTexMap.get("new_test"),
				new int[] { 0, 90 },
				new int[] { 190, 30 },
				new int[] { 190, 48 }
			);
		back_select = new Sprite(
				localTexMap.get("new_test"),
				new int[] { 190, 90 },
				new int[] { 190, 30 },
				new int[] { 190, 48 }
			);
		ex_screen = new Sprite (
				localTexMap.get("ex_game_screen"),
				new int[] { 0,0 },
				new int[] { 1425, 768 },
				new int[] { 1425, 600 }
				);
		
		
		//author: Mario
		BlockStandardLevel.nLevel = new Sprite(
				localTexMap.get("nLevel"),
				new int[] { 0, 0 },
				new int[] { 190, 48 },
				new int[] { 190, 48 }
			);
		// author: John
		BlockStandardLevel.overlay = new Sprite(
				Global.textureMap.get("overlay"),
				new int[] { 0, 0 },
				new int[] { 1024, 768 },
				new int[] { 1024, 768 }
			);		
		cursor = new Sprite(
				Global.textureMap.get("blocksheet"),
				new int[] { 240, 0 },
				new int[] { 32, 32 },
				blockOffSet
			);
		BlockStandardLevel.cursor = cursor;
		BlockStandardLevel.shiftLR[0] = new Sprite( // left indicator
				localTexMap.get("white_ui_controls"),
				new int[] { 300, 600 },
				new int[] { 100, 100 },
				new int[] { 100, 100 }
			);
		BlockStandardLevel.shiftLR[1] = new Sprite( // right indicator
				localTexMap.get("white_ui_controls"),
				new int[] { 200, 300 },
				new int[] { 100, 100 },
				new int[] { 100, 100 }
			);
		BlockStandardLevel.emptyEnergy = new Sprite(
				localTexMap.get("energy_empty"),
				new int[] { 0, 0 },
				new int[] { 512, 32 },
				new int[] { 640, 32 }
			);
		BlockStandardLevel.energyBar = localTexMap.get("energybar");
		int offset = 0;
		for (int i = 0; i < BlockStandardLevel.numbers.length; i++) {
			offset = i * 24 - 1;
			BlockStandardLevel.numbers[i] = new Sprite(
					localTexMap.get("number_white"),
					new int[] { offset, 0 },
					new int[] { 24, 30 },
					new int[] { 24, 30 }
				);
		}
		pracBox = new Sprite(
				Global.textureMap.get("blue_ui"),
				new int[] { 290, 94 },
				new int[] { 49, 45 },
				new int[] { 49, 45 }
			);
		pracArrows[0] = new Sprite(
				localTexMap.get("white_ui_controls"),
				new int[] { 300, 600 },
				new int[] { 100, 100 },
				new int[] { 50, 50 }
			);
		pracArrows[1] = new Sprite(
				localTexMap.get("white_ui_controls"),
				new int[] { 200, 300 },
				new int[] { 100, 100 },
				new int[] { 50, 50 }
			);
		// Update mode state when asset loading is completed
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
		//movementInputDelay = Global.inputReadDelayTimer;

		
		if (playLevel != null) {
			playLevel.run();
			if (playLevel.levelFinished) {
				if (playLevel.gameOver || playLevel.practice) {
					// TODO: selectPractice = false;
					playLevel = null;
					movementInputDelay = Global.inputReadDelayTimer;
				} else {
					// load next level
					// TODO: add test for at last level and return to menu
					loadLevel(playLevel.level + 1);
				}
				
			}
		} else if (showHighScore) {
			showHighScores();
			if (Global.getControlActive(Global.GameControl.CANCEL)) {
				movementInputDelay = 2 * Global.inputReadDelayTimer;
				showHighScore = false;
			}
		} else {
// @author Brock
			GameSelector_background.draw(0, 0);
			// TODO: remove if unnecessary
			/*if (selectPractice) {
				inputPracMenu();
			} else {
				moveCursorMain();
			} //*/
			moveCursorMain();
			

			optionBoxOffset = 0;
			if (cursorPos == 0) {
				// PLAY
				optionBox.draw(180, 180);
				play_select.draw(180, 180);

				// PRACTICE
				optionBox.draw(180, 250);
				prac_unselect.draw(180, 250);
				
				// GAME MODE
				optionBox.draw(180, 320);
				gamemode_unselect.draw(180, 325);
				
				// BACK
				optionBox.draw(180, 390);
				back_unselect.draw(180, 395);
				
				ex_screen.draw(450, 150);
				
				//selector[0].draw(160 + optionBoxOffset, 187 + cursorPos * 70);
				//selector[1].draw(351 + optionBoxOffset, 187 + cursorPos * 70);
			} 
			if (cursorPos == 1) {
				// PLAY
				optionBox.draw(180, 180);
				play_unselect.draw(180, 180);
				
				// PRACTICE
				optionBox.draw(180, 250);
				prac_select.draw(180, 250);
				
				// GAME MODE
				optionBox.draw(180, 320);
				gamemode_unselect.draw(180, 325);
				
				// BACK
				optionBox.draw(180, 390);
				back_unselect.draw(180, 395);
				
				//selector[0].draw(160 + optionBoxOffset, 187 + cursorPos * 70);
				//selector[1].draw(351 + optionBoxOffset, 187 + cursorPos * 70);
			}
			if (cursorPos == 2) {
				// PLAY
				optionBox.draw(180, 180);
				play_unselect.draw(180, 180);
				
				// PRACTICE
				optionBox.draw(180, 250);
				prac_unselect.draw(180, 250);
				
				// GAME MODE
				optionBox.draw(180, 320);
				gamemode_select.draw(180, 325);
				
				// BACK
				optionBox.draw(180, 390);
				back_unselect.draw(180, 395);
				
				//selector[0].draw(160 + optionBoxOffset, 187 + cursorPos * 70);
				//selector[1].draw(351 + optionBoxOffset, 187 + cursorPos * 70);
			}
			if (cursorPos == 3) {
				// PLAY
				optionBox.draw(180, 180);
				play_unselect.draw(180, 180);
				
				// PRACTICE
				optionBox.draw(180, 250);
				prac_unselect.draw(180, 250);
				
				// GAME MODE
				optionBox.draw(180, 320);
				gamemode_unselect.draw(180, 325);
				
				// BACK
				optionBox.draw(180, 390);
				back_select.draw(180, 395);
				
				//selector[0].draw(160 + optionBoxOffset, 187 + cursorPos * 70);
				//selector[1].draw(351 + optionBoxOffset, 187 + cursorPos * 70);
			}
			
			// author John
			if (cursorPos == 1) {
				drawPracticeSelect();
			}
		}
		
		if (pageBack) { cleanup(); }
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
				cursorPos = 3;
					//BlockStandardLevel.gamePaused = false;
				
			}
			if (Global.getControlActive(Global.GameControl.PAUSE)) {
				//BlockStandardLevel.gamePaused = true;
			}
			if (cursorPos == 1) { // practice selected, not confirmed
				inputPracMenu();
			}
			if (Global.getControlActive(Global.GameControl.SELECT)) {
				switch (cursorPos) {
					case 0: // normal mode
						playLevel = new BlockStandardLevel01(localTexMap);
						BlockStandardLevel.score = 0;
						//activeGameMode = BlockMatchStandard;
						break;
					case 1: // practice mode
						loadLevel(pracLevel);
						playLevel.practice = true;
						movementInputDelay = Global.inputReadDelayTimer;
						BlockStandardLevel.score = 0;
						//selectPractice = true;
						break;
					case 2: // high score
						showHighScore = true;
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
		switch (levelID) {
			case 1:
				playLevel = new BlockStandardLevel01(localTexMap);
				break;
			case 2:
				playLevel = new BlockStandardLevel02(localTexMap);
				break;
			case 3:
				playLevel = new BlockStandardLevel03(localTexMap);
				break;
			case 4:
				playLevel = new BlockStandardLevel04(localTexMap);
				break;
			case 5:
				playLevel = new BlockStandardLevel05(localTexMap);
				break;
			default:
				Global.writeToLog( String.format("Attempting to load invalid standard mode play level: %d", levelID) , true );
				break;
		}
		playLevel.level = levelID;
	}
		
	private void inputPracMenu() {
		movementInputDelay -= Global.delta;
		if (movementInputDelay > 0) { return; }
		if (Global.getControlActive(Global.GameControl.LEFT)) {
			pracLevel--;
			if (pracLevel < 1) { pracLevel = pracMax; }
			movementInputDelay = Global.inputReadDelayTimer;
		} else if (Global.getControlActive(Global.GameControl.RIGHT)) {
			pracLevel++;
			if (pracLevel > pracMax) { pracLevel = 1; }
			movementInputDelay = Global.inputReadDelayTimer;
		} 		
	}

	private void drawPracticeSelect() {
		pracBox.draw(510, 250);
		BlockStandardLevel.numbers[pracLevel].draw(525, 255);
		pracArrows[0].draw(470, 248);
		pracArrows[1].draw(550, 248);
	}
	
	// TODO: move high score vars to top after finished implementation
	private boolean showHighScore = false;
	private final int hsBarSpace = 10;
	private final int hsBarHeight = 48;
	private final int hsMargin = 30;
	private List<HighScoreRecord> hsRecords = new ArrayList<HighScoreRecord>(10);

	private void showHighScores() {
		int drawWidth = 1024 - 2 * hsMargin;
		int interval = hsBarHeight + hsBarSpace;
		int firstDrop = 100;
		int limit = 10;
		org.newdawn.slick.Color 
			boxColor = org.newdawn.slick.Color.cyan,
			textColor = org.newdawn.slick.Color.black,
			resetColor = org.newdawn.slick.Color.white;
		
		for (int i = 0; i < limit; i++) {
			boxColor.bind();
			Global.uiTransWhite.draw(hsMargin, firstDrop + i * interval, drawWidth, hsBarHeight);
			resetColor.bind();
			Global.drawStringDefaultFont(hsMargin + 10, firstDrop + i * interval + 10, "High Score Name", textColor);
			Global.drawStringDefaultFont(hsMargin + 580, firstDrop + i * interval + 10, "01234567890123", textColor);
			Global.drawStringDefaultFont(hsMargin + 780, firstDrop + i * interval + 10, "00/00/2016", textColor);
			Global.drawStringDefaultFont(hsMargin + 920, firstDrop + i * interval + 10, "01", textColor);
			
			
		}
		org.newdawn.slick.Color.white.bind();
	}
	
	/**
	 * Load custom values and high scores from file. Loads default values for
	 * high scores if data is not present or corrupted.
	 */
	private void loadPrefs() {
		BufferedReader prefFile;
		hsRecords.clear();
	}
	
	/** 
	 * Saves custom values and high scores to file.
	 */
	private void savePrefs() {
		
	}
	
}

