import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.zip.DataFormatException;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class BlockBreakStandard implements GameMode {
	protected LoadState currentState = LoadState.NOT_LOADED;
	protected HashMap<String, Texture> localTexMap = new HashMap<String, Texture>(10);
	protected int cursorPos = 0;
	//protected long inputDelay = Global.inputReadDelayTimer;
	private BlockStandardLevel playLevel;

	// Level variables. These may be moved/removed if level play is moved to separated class object.
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
		new String[] { "nLevel", "media/gNextlevel.png" },
		new String[] { "ex_game_screen", "media/game_screen.png"},
		new String[] { "Text", "media/Mode_Text.png"},
		new String[] { "white_ui_controls" , "media/sheet_white2x.png" },
		new String[] { "new_test", "media/image1.png"},
		new String[] { "bigsky", "media/bigsky_cedf10.png" }
	};
	
	private Sprite GameSelector_background;
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
	private final String[] menuOptions = new String[] {
		"Play",
		"Practice",
		"High Score",
		"Back"
	};
	private int[] menuOptionOffset = new int[4];
	
	//private boolean selectPractice = false;
	private Sprite pracBox;
	private Sprite[] pracArrows = new Sprite[2];
	private int pracLevel = 1;
	private int pracMax = 20;
	private int lastLevel = 1;
	private int maxUnlocked = 1;
	
	private boolean newHighScore = false;
	private String hsNameEntry = "";
	private boolean preClearComplete = false;

	
	public BlockBreakStandard() {
		// TODO: set or load any custom environment variables
		// do not load assets at this point
		for (int i = 0; i < menuOptions.length; i++) {
			menuOptionOffset[i] = Global.getFont24DrawSize(menuOptions[i]) / 2;
		}
		for (int i = 0; i < 10; i++) {
			hsRecords.add(HighScoreRecord.getNewEmptyRecord());
		}
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
		BlockStandardLevel.cursor = new Sprite(
				Global.textureMap.get("blocksheet"),
				new int[] { 240, 0 },
				new int[] { 32, 32 },
				blockOffSet
			);
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
		
		hsBack = localTexMap.get("bigsky");
		// Update mode state when asset loading is completed
		currentState = LoadState.LOADING_DONE;
		
		loadPrefs();
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
			if (!playLevel.levelFinished) {
				playLevel.run();
			} else {
				if (maxUnlocked < playLevel.level) { maxUnlocked = playLevel.level; }
				if (playLevel.gameOver || playLevel.practice) {
					// TODO: selectPractice = false;
					movementInputDelay = Global.inputReadDelayTimer;
					if (!playLevel.practice) {
						for (int i = 9; i >= 0; i--) {
							if (hsRecords.get(i).getScore() < BlockStandardLevel.score) {
								newHighScore = true;
								showHighScore = true;
								hsNameEntry = "";
								preClearComplete = false;
								break;
							}
						}
					}
					lastLevel = playLevel.level;
					playLevel = null;
				} else {
					// load next level
					// TODO: add test for at last level and return to menu
					loadLevel(playLevel.level + 1);
				}
			}
		} else if (showHighScore) {
			showHighScores();
			if (newHighScore) {
				// TODO: get high score user data
				if (!preClearComplete) {
					int c = Keyboard.getNumKeyboardEvents();
					if (c > 0) {
						while (Keyboard.next()) {
							c = Keyboard.getEventKey();
						}
					} else {
						preClearComplete = true;
					}
				} else if (Keyboard.isKeyDown(Keyboard.KEY_RETURN)) {
					hsRecords.add(
						new HighScoreRecord(
							hsNameEntry,
							LocalDateTime.now(),
							BlockStandardLevel.score,
							lastLevel
						)
					);
					Collections.sort(hsRecords);
					while (hsRecords.size() > 10) {
						hsRecords.remove(10);
					}
					newHighScore = false;
				} else if (Keyboard.isKeyDown(Keyboard.KEY_BACK)) {
					if (hsNameEntry.length() > 0) {
						hsNameEntry = hsNameEntry.substring(0, hsNameEntry.length() - 1);
					}
				} else {
					while (Keyboard.next()) {
						char c = Keyboard.getEventCharacter();
						
						if (hsNameEntry.length() > 40) {
							// do nothing
						} else if (Character.isLetter(c)) {
							hsNameEntry += Character.toUpperCase(c);
						} else if (Character.isSpaceChar(c)) {
							hsNameEntry += ' ';
						} 
					}
				}
				Global.uiBlue.draw(256, 256, 512, 376);
				Global.drawFont24(276, 276, "Level: " + Integer.toString(lastLevel), Color.white);
				Global.drawFont24(276, 310, "Score:", Color.white);
				Global.drawFont24(276, 334, Integer.toString(BlockStandardLevel.score), Color.white);
				Global.uiBlueSel.draw(276, 360, 472, 48);
				Global.drawFont24(282, 386, hsNameEntry + '_', Color.black);
				
			} else if (movementInputDelay <= 0 && Global.getControlActive(Global.GameControl.CANCEL)) {
				movementInputDelay = 2 * Global.inputReadDelayTimer;
				showHighScore = false;
			} else {
				movementInputDelay -= Global.delta;
			}
			
		} else {
// @author Brock
			GameSelector_background.draw(0, 0);
			moveCursorMain();
			for (int i = 0; i < menuOptions.length; i++) {
				//optionBox.draw(180, 180 + i * 70);
				Global.menuButtonShader.bind();
				Global.uiTransWhite.draw(180, 180 + i * 70, 190, 48);
				Color.white.bind();
				if (cursorPos == i) {
					Global.drawFont24(275 - menuOptionOffset[i], 195 + i * 70, menuOptions[i], Color.white);
				} else {
					Global.drawFont24(275 - menuOptionOffset[i], 195 + i * 70, menuOptions[i], Color.black);
				}
			}
			switch (cursorPos) {
				case 0:
					ex_screen.draw(450, 150);
					break;
				case 1:
					// TODO: add practice mode preview
					break;
				case 2: 
					// TODO: add high score screen preview
					break;
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
		savePrefs();
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
						loadLevel(1);
						BlockStandardLevel.score = 0;
						//activeGameMode = BlockMatchStandard;
						break;
					case 1: // practice mode
						if (pracLevel > maxUnlocked) { break; }
						loadLevel(pracLevel);
						playLevel.practice = true;
						movementInputDelay = Global.inputReadDelayTimer;
						BlockStandardLevel.score = 0;
						//selectPractice = true;
						break;
					case 2: // high score
						showHighScore = true;
						newHighScore = false;
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
				playLevel = new BlockStandardLevelex3(localTexMap);//BlockStandardLevel01(localTexMap);
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
			case 6:
				playLevel = new BlockStandardLevel06(localTexMap);
				break;
			case 7:
				playLevel = new BlockStandardLevel07(localTexMap);
				break;
			case 8:
				playLevel = new BlockStandardLevel08(localTexMap);
				break;
			case 9:
				playLevel = new BlockStandardLevel09(localTexMap);
				break;
			case 10: 
				playLevel = new BlockStandardLevel10(localTexMap);
				break;
			case 11:
				playLevel = new BlockStandardLevel11(localTexMap);
				break;
			case 12:
				playLevel = new BlockStandardLevel12(localTexMap);
				break;
			case 13:
				playLevel = new BlockStandardLevel13(localTexMap);
				break;
			case 14:
				playLevel = new BlockStandardLevel14(localTexMap);
				break;
			case 15:
				playLevel = new BlockStandardLevel15(localTexMap);
				break;
			case 16:
				playLevel = new BlockStandardLevel16(localTexMap);
				break;
			case 17:
				playLevel = new BlockStandardLevel17(localTexMap);
				break;
			case 18:
				playLevel = new BlockStandardLevel18(localTexMap);
				break;
			case 19:
				playLevel = new BlockStandardLevel19(localTexMap);
				break;
			case 20:
				playLevel = new BlockStandardLevel20(localTexMap);
				break;
			default:
				Global.writeToLog( String.format("Attempting to load invalid standard mode play level: %d", levelID) , true );
				return ;
		}
		playLevel.level = levelID;
		playLevel.levelTitle = String.format("Level %02d", playLevel.level);
	}
		
	private void inputPracMenu() {
		movementInputDelay -= Global.delta;
		if (movementInputDelay > 0) { return; }
		if (Global.getControlActive(Global.GameControl.LEFT)) {
			pracLevel--;
			Global.sounds.playSoundEffect("button_click"); //Mario
			if (pracLevel < 1) { pracLevel = pracMax; }
			movementInputDelay = Global.inputReadDelayTimer;
		} else if (Global.getControlActive(Global.GameControl.RIGHT)) {
			pracLevel++;
			Global.sounds.playSoundEffect("button_click"); //Mario
			if (pracLevel > pracMax) { pracLevel = 1; }
			movementInputDelay = Global.inputReadDelayTimer;
		} 		
	}

	private static final int pracOffset = 375;
	private static final int pracSelectDrop = 248;
	
	private void drawPracticeSelect() {
		String num = Integer.toString(pracLevel);
		int numOffset = Global.getNumbers24DrawSize(num) / 2;
		Color numCol = pracLevel > this.maxUnlocked ? Color.gray : Color.white;
		
		pracArrows[0].draw(pracOffset, 248);
		pracBox.draw(pracOffset + 40, 250);
		//BlockStandardLevel.numbers[pracLevel].draw(525, 255);
		Global.drawNumbers24(pracOffset + 65 - numOffset, pracSelectDrop + 12, num, numCol);
		pracArrows[1].draw(pracOffset + 80, 248);
	}
	
	// TODO: move high score vars to top after finished implementation
	private boolean showHighScore = false;
	private final int hsBarSpace = 10;
	private final int hsBarHeight = 48;
	private final int hsMargin = 30;
	private List<HighScoreRecord> hsRecords = new ArrayList<HighScoreRecord>(10);
	private Texture hsBack;
	private int[] hsBackShift = new int[] { 1, 0 };
	private float[] hsBackDraw = new float[] { 1024 / 4096f, 768 / 1024f };

	private void showHighScores() {
		int drawWidth = 1024 - 2 * hsMargin;
		int interval = hsBarHeight + hsBarSpace;
		int firstDrop = 100;
		int limit = 10;
		
		if (hsBackShift[0] == 1) {
			hsBackShift[1] += Global.delta >> 4;
			if (hsBackShift[1] > 3072) {
				hsBackShift[1] = 3072;
				hsBackShift[0] = -1;
			}
		} else {
			hsBackShift[1] -= Global.delta >> 4;
			if (hsBackShift[1] < 0) {
				hsBackShift[1] = 0;
				hsBackShift[0] = 1;
			}
		}
		
		float left = hsBackShift[1] / 4096f;
		hsBack.bind();
		glBegin(GL_QUADS); {
			glTexCoord2f(left, 0f);
			glVertex2i(0, 0);
			
			glTexCoord2f(left, hsBackDraw[1]);
			glVertex2i(0, 768);
			
			glTexCoord2f(left + hsBackDraw[0], hsBackDraw[1]);
			glVertex2i(1024, 768);
			
			glTexCoord2f(left + hsBackDraw[0], 0f);
			glVertex2i(1024, 0);

		} glEnd();
		TextureImpl.bindNone();
		
		Color 
			boxColor = Color.cyan,
			textColor = Color.black,
			resetColor = Color.white;
		
		Global.drawFont48(512 - 98, 25, "High Score", Color.white);
		HighScoreRecord hsr;
		int scoreOff = 0;
		for (int i = 0; i < limit; i++) {
			hsr = hsRecords.get(i);
			boxColor.bind();
			Global.uiTransWhite.draw(hsMargin, firstDrop + i * interval, drawWidth, hsBarHeight);
			resetColor.bind();
			Global.drawFont24(hsMargin + 10, firstDrop + i * interval + 15, hsr.getName(), textColor);
			scoreOff = Global.getNumbers24DrawSize(hsr.getScoreAsString()); 
			//Global.drawNumbers24(hsMargin + 560, firstDrop + i * interval + 15, hsr.getScoreAsString(), textColor);
			Global.drawNumbers24(hsMargin + 740 - scoreOff, firstDrop + i * interval + 15, hsr.getScoreAsString(), textColor);
			Global.drawNumbers24(hsMargin + 770, firstDrop + i * interval + 15, hsr.getDate(), textColor);
			//Global.drawFont24(hsMargin + 920, firstDrop + i * interval + 15, hsr.getLevel(), textColor);
		}
		Color.white.bind();
		//Global.menuButtonShader.bind();
		Global.uiTransWhite.draw(512 - 250, 700, 500, 48);
		//Color.white.bind();
		Global.drawFont24(512, 715, "Press [CANCEL] to return to game menu.", Color.black, true);
	}
	
	/**
	 * Load custom values and high scores from file. Loads default values for
	 * high scores if data is not present or corrupted.
	 */
	private void loadPrefs() {
		BufferedReader prefFile;
		String line;
		HighScoreRecord hsr;

		try {
			prefFile = new BufferedReader(new FileReader("standard.pref"));
			line = prefFile.readLine();
			while (line != null) {
				if (line.compareToIgnoreCase("[HighScore]") == 0) {
					try {
						line = prefFile.readLine();
						while (line != null) {
							hsr = HighScoreRecord.getNewEmptyRecord();
							hsr.readRecord(line);
							hsRecords.add(hsr);
							line = prefFile.readLine();
						}
					} catch (DataFormatException dfe) {
						continue;
					}
				} else if (line.compareToIgnoreCase("[TopLevel]") == 0) {
					line = prefFile.readLine();
					try {
						maxUnlocked = Integer.parseInt(line);
					} catch (NumberFormatException nfe) {
						maxUnlocked = 1;
					}
				}
				line = prefFile.readLine();
			}
			prefFile.close();
		} catch (IOException err) {
			
		} finally {
			Collections.sort(hsRecords);
			while (hsRecords.size() > 10) {
				hsRecords.remove(10);
			}
		}
		
	}
	
	/** 
	 * Saves custom values and high scores to file.
	 */
	private void savePrefs() {
		try {
			BufferedWriter prefFile = new BufferedWriter(new FileWriter("standard.pref"));
			prefFile.write("[TopLevel]");
			prefFile.newLine();
			prefFile.write(Integer.toString(maxUnlocked));
			prefFile.newLine();
			prefFile.write("[HighScore]");
			prefFile.newLine();
			for(HighScoreRecord hsr : hsRecords) {
				prefFile.write(hsr.toString());
				prefFile.newLine();
			}
			prefFile.close();
		} catch (IOException e) {
			Global.writeToLog("Error opening Standard mode preferences file for writing.", true);
			e.printStackTrace();
		}
		
	}
	
}

