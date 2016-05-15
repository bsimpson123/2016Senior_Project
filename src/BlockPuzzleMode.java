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

/*
 * @Author Brock
 */

public class BlockPuzzleMode implements GameMode {
	protected LoadState currentState = LoadState.NOT_LOADED;
	protected static HashMap<String, Texture> localTexMap = new HashMap<String, Texture>(10);
	protected int cursorPos = 0;
	//protected long inputDelay = Global.inputReadDelayTimer;
	//private PuzzleModeLevel playLevel;
	private PuzzleBreakLevel playLevel;
	//private PuzzleModeLevel playLevelDisplay;

	// Level variables. These may be moved/removed if level play is moved to separated class object.
	protected int[] blockOffSet = new int[] { 32, 32 };
	private long movementInputDelay = Global.inputReadDelayTimer;
	private long actionDelay = Global.inputReadDelayTimer;
	
	//protected int[] levelArray = new int[3];
	//private HighScoreRecord[] hsLevelArray = new HighScoreRecord[3];
	//protected ArrayList<HighScoreRecord>[] hsLevelArray = (ArrayList<HighScoreRecord>[])new ArrayList[2];
	//protected ArrayList<HighScoreRecord>[] hsLevelArray = new ArrayList<HighScoreRecord>[2];
	
	
	private boolean pageBack = false;
	/** The current game mode within the main logic loop. */

	protected static Sprite ex_screen;
	
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
		new String[] { "new_test", "media/image1.png"},
		new String[] { "bigsky", "media/bigsky_cedf10.png" },
		new String[] { "Gold_Star", "media/star_gold.png"},
		new String[] { "Silver_Star", "media/star_silver.png"},
		new String[] { "ex_game_screen", "media/game_screen.png"},
		new String[] { "Chall_Star", "media/star_bronze.png"}
	};
	public GridColumn[] puzzleGrids;
	private Sprite GameSelector_background;
	private Sprite gridOverLay;
	
	private final String[] menuOptions = new String[] {
		"Play",
		"Level Select",
		"Achievements",
		"Play Guide",
		"Back"
	};
	private int[] menuOptionOffset = new int[5];
	
	//private boolean selectPractice = false;
	protected static Sprite Yellow_star;
	protected static Sprite Yellow_star_small;
	protected Sprite Silver_star;
	protected static Sprite Challenge_star;
	
	private Sprite pracBox;
	private Sprite[] pracArrows = new Sprite[2];
	private int pracLevel = 1;
	private int pracMax = 20;//PuzzleModeLevel.nLevels;
	private int lastLevel = 1;
	private int maxUnlocked = 1;
	private int levelSelect = 0;
	
	private boolean newHighScore = false;
	private String hsNameEntry = "";
	private boolean preClearComplete = false;
	private int level = 1;
	protected static int[] medals = PuzzleBreakLevel.medals;//PuzzleBreakLevel.medals;//PuzzleModeLevel.medals;
	
	protected List<GridColumn[]> gridDispLevel = new ArrayList<GridColumn[]>();

	//protected List<PuzzleModeLevel> gridDisplay = new ArrayList<PuzzleModeLevel>();
	protected List<PuzzleBreakLevel> gridDisplay = new ArrayList<PuzzleBreakLevel>();
	
	public BlockPuzzleMode() {
		
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

		//for (int i = 0; i < PuzzleModeLevel.nLevels; i++) {

		for (int i = 0; i <= pracMax; i++) {
			
			loadLevel(i);
			gridDisplay.add(playLevel);
			//if (gridDispLevel.size() <= 0) {
			
			gridDispLevel.add(GridColumn.copyGrid(playLevel.grid));
			Global.writeToLog(String.format("init grid: %d", i),true );
			playLevel = null;
		//	preloadLevel(i);
		//}
			//gridDisplay.add(playLevel.buildGridPuzzle(i));
			//gridDisplay.add(selectDisplayGrid(i));
			//gridDisplay.add(i,loadLevel(i));
			//playLevel = null;
			//gridDispLevel.add(GridColumn.copyGrid(gridDisplay.get(i).grid));
			
		}
		PuzzleBreakLevel.buildStaticAssets(localTexMap);
		GameSelector_background = new Sprite(
				Global.textureMap.get("main_menu_background"),
				new int[] {0,0},
				new int[] {1024,768},
				new int[] {1024,768}
			);
		Yellow_star = new Sprite(
				localTexMap.get("Gold_Star"),
				new int[] {0,0},
				new int[] {31,30},
				new int[] {41,40}
			);
		Challenge_star = new Sprite(
				localTexMap.get("Chall_Star"),
				new int[] {0,0},
				new int[] {31,30},
				new int[] {41,40}
			);	

		/*Yellow_star = new Sprite(
				localTexMap.get("Gold_Star"),
				new int[] {0,0},
				new int[] {41,74},
				new int[] {60, 94}
				//new int[] {41,74}
			);*/
		Yellow_star_small = new Sprite(
				localTexMap.get("Silver_Star"),
				new int[] {0,0},
				new int[] {129,120},
				new int[] {129,120}
			);
		ex_screen = new Sprite (
				localTexMap.get("ex_game_screen"),
				new int[] { 0,0 },
				new int[] { 1425, 768 },
				new int[] { 1425, 600 }
				);
		Silver_star = new Sprite(
				localTexMap.get("Silver_Star"),
				new int[] {0,0},
				new int[] {31,30},
				new int[] {41,40}
			);
		for (int i = 0; i < medals.length; i++) {
			medals[i] = 0;
		}
		
				
		//author: Mario
		PuzzleModeLevel.nLevel = new Sprite(
				localTexMap.get("nLevel"),
				new int[] { 0, 0 },
				new int[] { 190, 48 },
				new int[] { 190, 48 }
			);
		// author: John
		PuzzleModeLevel.overlay = new Sprite(
				Global.textureMap.get("overlay"),
				new int[] { 0, 0 },
				new int[] { 1024, 768 },
				new int[] { 1024, 768 }
			);		
		gridOverLay = new Sprite (
				Global.textureMap.get("overlay"),
				new int[] {0,0},
				new int[] {1024,768},
				new int[] {480,480}
				);
		PuzzleModeLevel.cursor = new Sprite(
				Global.textureMap.get("blocksheet"),
				new int[] { 240, 0 },
				new int[] { 32, 32 },
				blockOffSet
			);
		PuzzleModeLevel.shiftLR[0] = new Sprite( // left indicator
				localTexMap.get("white_ui_controls"),
				new int[] { 300, 600 },
				new int[] { 100, 100 },
				new int[] { 100, 100 }
			);
		PuzzleModeLevel.shiftLR[1] = new Sprite( // right indicator
				localTexMap.get("white_ui_controls"),
				new int[] { 200, 300 },
				new int[] { 100, 100 },
				new int[] { 100, 100 }
			);
		PuzzleModeLevel.emptyEnergy = new Sprite(
				localTexMap.get("energy_empty"),
				new int[] { 0, 0 },
				new int[] { 512, 32 },
				new int[] { 640, 32 }
			);
		PuzzleModeLevel.energyBar = localTexMap.get("energybar");
		int offset = 0;
		for (int i = 0; i < PuzzleModeLevel.numbers.length; i++) {
			offset = i * 24 - 1;
			PuzzleModeLevel.numbers[i] = new Sprite(
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
		//Yellow_star.draw(medalOffset * j + 500, 250);
		//medalOffset -= 5;

		//playLevel = gridDisplay.get(1);
		if (playLevel != null) {

			//playLevel = gridDisplay.get(index);
			if (!playLevel.levelFinished) {
				//gridDisplay.get(playLevel.level).run();
				//playLevel.remainClears = -1;
				//playLevel.remainClears = gridDisplay.get(playLevel.level).totalClears;
				playLevel.run();
			} else if (playLevel.level == 0){
				playLevel = null;
			}
			else {

				//if (playLevel.levelFinished) {

				//}
				if (maxUnlocked < playLevel.level) { maxUnlocked = playLevel.level; }
				if (playLevel.gameOver || playLevel.practice) {
				//	gridDisplay.get(playLevel.level).levelFinished = false;
					// TODO: selectPractice = false;
					movementInputDelay = Global.inputReadDelayTimer;
					//if (!playLevel.practice) {
						//for (int i = 0; i < pracMax; i++) {
							//	preloadLevel(i);
							//}
							//gridDisplay.clear();
							//	selectDisplayGrid(i);
							//}
						//for (int i = 9; i >= 0; i--) {
							/*if (hsRecords.get(i).getScore() < PuzzleModeLevel.score) {
								newHighScore = true;
								showHighScore = true;
								hsNameEntry = "";
								preClearComplete = false;
								break;
							}*/
						//}
					//}
					lastLevel = playLevel.level;
					gridDisplay.get(lastLevel).levelFinished = false;
					gridDisplay.get(lastLevel).gamePaused = false;
					gridDisplay.get(lastLevel).gameOver = false;
					//gridDisplay.get(lastLevel).levelComplete = false;
					//gridDispLevel.get(lastLevel) = gridDisplay.get(lastLevel).grid;
					
					
					//gridDisplay.get(lastLevel).buildGrid(lastLevel);
					medals = PuzzleBreakLevel.medals;
					//gridDisplay.get(lastLevel).buildGridPuzzle(lastLevel);
					/*if (gridDisplay.get(playLevel.level).gameOver) {
						gridDisplay.get(lastLevel).levelFinished = false;
						gridDisplay.get(lastLevel).gameOver = false;
						
					}*/
					//gridDisplay.get(lastLevel).levelFinished = false;
					//gridDisplay.get(playLevel.level).resetMoves = true;
					///gridDisplay.get(playLevel.level).levelComplete = false;
					//gridDisplay.get(playLevel.level).gameOver = false;
					//gridDisplay.get(playLevel.level).remainClears = gridDisplay.get(playLevel.level).totalClears;
					playLevel = null;
				} else {
					
					//lastLevel = playLevel.level;
					//gridDisplay.get(lastLevel).levelFinished = false;
					//gridDisplay.get(lastLevel).gamePaused = false;
					//gridDisplay.get(lastLevel).gameOver = false;
					playLevel.levelFinished = false;
					playLevel.gamePaused = false;
					playLevel.gameOver = false;
					//playLevel.buildGrid(playLevel.level);
					//gridDispLevel.add(playLevel.level,playLevel.grid);
					
					// load next level
					// TODO: add test for at last level and return to menu
					playLevel = gridDisplay.get(playLevel.level + 1);
					//loadLevel(playLevel.level + 1);
					//gridDisplay.get(playLevel.level).levelFinished = false;
					//preloadLevel(playLevel.level + 1);
					
					//playLevel = gridDisplay.get(playLevel.level + 1);
					
					//levelSelect = gridDisplay.get(playLevel.level + 1).level;
					//playLevel = null;
					//playLevel = gridDisplay.get(playLevel.level + 1);
					//levelSelect = playLevel.level;
					//playLevel.level = gridDisplay.get(playLevel.level - 1).level;
					//playLevel.levelTitle = String.format("Level %02d", gridDisplay.get(playLevel.level - 1).level);
				}
			}
		} else if (showHighScore) {
			showHighScores();
			
			if (movementInputDelay <= 0 && Global.getControlActive(Global.GameControl.CANCEL)) {
				movementInputDelay = 2 * Global.inputReadDelayTimer;
				showHighScore = false;
			} else {
				movementInputDelay -= Global.delta;
			}
			
			
		} else if (showPuzzleGuide) {
			drawPuzzlePlayGuide();
			if (movementInputDelay <= 0 && Global.getControlActive(Global.GameControl.CANCEL)) {
				movementInputDelay = 2 * Global.inputReadDelayTimer;
				showPuzzleGuide = false;
			} else {
				movementInputDelay -= Global.delta;
			}
		}
		else {
// @author Brock
			
			GameSelector_background.draw(0, 0);
			moveCursorMain();
			
			for (int i = 0; i < menuOptions.length; i++) {
				//optionBox.draw(180, 180 + i * 70);
				Global.menuButtonShader.bind();
				Global.uiTransWhite.draw(180, 180 + i * 70, 190, 48);
				Color.white.bind();
				Global.drawFont48(400, 100, "Puzzle Mode", Color.white);
				if (cursorPos == i) {
					Global.drawFont24(275 - menuOptionOffset[i], 195 + i * 70, menuOptions[i], Color.white);
				} else {
					Global.drawFont24(275 - menuOptionOffset[i], 195 + i * 70, menuOptions[i], Color.black);
				}
			}
			switch (cursorPos) {
				case 0:
					//ex_screen.draw(450, 150);
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
					
					cursorPos = 4;
				}
				movementInputDelay = Global.inputReadDelayTimer;
			}
			if (Global.getControlActive(Global.GameControl.DOWN)) {
				cursorPos++;
				Global.sounds.playSoundEffect("button_click");

				if (cursorPos > 4) {
					cursorPos = 0;
				}
				movementInputDelay = Global.inputReadDelayTimer;
			}
			if (Global.getControlActive(Global.GameControl.CANCEL)) { // Cancel key moves the cursor to the program exit button
				cursorPos = 4;
					//PuzzleModeLevel.gamePaused = false;
				
			}
			if (Global.getControlActive(Global.GameControl.PAUSE)) {
				//PuzzleModeLevel.gamePaused = true;
			}
			if (cursorPos == 1) { // practice selected, not confirmed
				inputPracMenu();
			}
			if (Global.getControlActive(Global.GameControl.SELECT)) {
				switch (cursorPos) {
					case 0: // normal mode
						//playLevel = gridDisplay.get(1);
						loadLevel(1);
						PuzzleBreakLevel.score = 0;
						
						break;
					case 1: // practice mode
						if (pracLevel > maxUnlocked) { break; }
						loadLevel(pracLevel);
						
						movementInputDelay = Global.inputReadDelayTimer;
						PuzzleBreakLevel.score = 0;
						//selectPractice = true;
						break;
					case 2: // high score
						showHighScore = true;
						newHighScore = false;
						break;
					case 3: 
						showPuzzleGuide = true;
						break;
					case 4: // exit
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
		playLevel = new PuzzleBreakLevel(levelID);
		/*switch (levelID) {
			case 1:
				playLevel = new PuzzleModeLevel01(localTexMap);
				//gridDisplay.add(playLevel.grid);
				break;
			case 2:
				playLevel = new PuzzleModeLevel02(localTexMap);
				//gridDisplay.add(playLevel.grid);
				break;
			case 3:
				playLevel = new PuzzleModeLevel03(localTexMap);
				//gridDisplay.add(playLevel.grid);
				break;
			case 4:
				playLevel = new PuzzleModeLevel04(localTexMap);
				//gridDisplay.add(playLevel.grid);
				break;
			case 5:
				playLevel = new PuzzleModeLevel05(localTexMap);
				//gridDisplay.add(playLevel.grid);
				break;
			case 6:
				playLevel = new PuzzleModeLevel06(localTexMap);
				//gridDisplay.add(playLevel.grid);
				break;
			case 7:
				playLevel = new PuzzleModeLevel07(localTexMap);
				//gridDisplay.add(playLevel.grid);
				break;
			case 8:
				playLevel = new PuzzleModeLevel08(localTexMap);
				//gridDisplay.add(playLevel.grid);
				break;
			case 9:
				playLevel = new PuzzleModeLevel09(localTexMap);
				//gridDisplay.add(playLevel.grid);
				break;
			case 10:
				playLevel = new PuzzleModeLevel10(localTexMap);
				//gridDisplay.add(playLevel.grid);
				break;
			case 11:
				playLevel = new PuzzleModeLevelTemplate(localTexMap);
				//gridDisplay.add(playLevel.grid);
				break;
			default:
				//playLevel = new PuzzleModeLevelTemplate(localTexMap);
				Global.writeToLog( String.format("Attempting to load invalid standard mode play level: %d", levelID) , true );
				//break;
				return ;
		}
		//if (levelID > 0) {
		playLevel.level = levelID;
		//gridDisplay.
		playLevel.levelTitle = String.format("Level %02d", levelID);*/
		//gridDisplay.add(playLevel);
		//return playLevel;
		//}
		
		//return playLevel;
		//playLevel = null;
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
	protected static int medalOffset = 45;
	
	/*private void drawPracticeSelect() {
		String num = Integer.toString(pracLevel);
		int numOffset = Global.getNumbers24DrawSize(num) / 2;
		Color numCol = pracLevel > this.maxUnlocked ? Color.gray : Color.white;
		
		pracArrows[0].draw(pracOffset, 248);
		//pracBox.draw(pracOffset + 40, 250);
		Global.menuButtonShader.bind();
		Global.uiTransWhite.draw(pracOffset + 40, 250, 49, 45);
		Color.white.bind();
		//Global.uiGreen.draw(pracOffset + 40, 250, 49, 45);
		//PuzzleModeLevel.numbers[pracLevel].draw(525, 255);
		Global.drawNumbers24(pracOffset + 65 - numOffset, pracSelectDrop + 12, num, numCol);
		pracArrows[1].draw(pracOffset + 80, 248);
		//for (int i = 0; i < medals.length; i++) {
		if (pracLevel <= PuzzleModeLevel.nLevels) {
			if (medals[pracLevel] > 0) {
				for (int j = 1; j <= medals[pracLevel]; j++) {
					Yellow_star.draw(medalOffset * j + 500, 250);
					//medalOffset -= 5;
				}
			}
		}
		//}
	}*/
	private GridColumn[] grid;
	private int[] gridSize;
	private int[] gridBasePos;
	private int[] blockSize;
	
	private void drawGridDisplay(GridColumn[] grid) {
		blockSize = new int[] { 24, 24 };
		gridSize = new int[] { 20, 20 }; // default grid size is { 20, 20 }
		//gridBasePos = new int[] { 20, Global.glEnvHeight - blockSize[1] - 50 };
		gridBasePos = new int[] { pracOffset + 82, 700 };
		
		// The old grid draw functions will not work with the new grid management algorithm, the math will not move the blocks the same
		for (int i = 0; i < grid.length; i++) {
			for (int k = 0; k < grid[0].blocks.length; k++) {
				if (grid[i].blocks[k] == null) {
					continue;
				} else {
					grid[i].blocks[k].draw(
							gridBasePos[0] + blockSize[0] * i,
							(gridBasePos[1] - blockSize[1] * k),
							blockSize
							);
				}
			}
		}
		
	}
	
	private void drawPracticeSelect() {
		String num = Integer.toString(pracLevel);
		int numOffset = Global.getNumbers24DrawSize(num) / 2;
		Color numCol = pracLevel > this.maxUnlocked ? Color.gray : Color.white;
		
		//selectDisplayGrid(pracLevel);
		
		pracArrows[0].draw(pracOffset, 410);
		//pracBox.draw(pracOffset + 40, 250);
		Global.menuButtonShader.bind();
		//Global.uiTransWhite.draw(pracOffset + 40, 180, 565, 450);
		//Global.uiTransWhite.draw(pracOffset + 40, 180, 565, 525);
		Global.uiTransWhite.draw(pracOffset + 40, 180, 565, 575);
		Color.white.bind();
		//ex_screen.draw(pracOffset + 65, 243);
		
		//drawGridDisplay(gridDisplay.get(pracLevel + 1));
		//playLevelDisplay = gridDisplay.get(pracLevel);

//		drawGridDisplay(gridDisplay.get(pracLevel).grid);
		drawGridDisplay(gridDispLevel.get(pracLevel));
		
		//Global.uiGreen.draw(pracOffset + 40, 250, 49, 45);
		//PuzzleModeLevel.numbers[pracLevel].draw(525, 255);
		Global.drawFont48(pracOffset + 80, pracSelectDrop - 50, "Level ", numCol);
		Global.drawNumbers48(pracOffset + 195, pracSelectDrop - 55, num, numCol);
		if (numCol == Color.gray) {
			gridOverLay.draw(pracOffset + 82, 244);
		}
		//Global.drawNumbers24(pracOffset + 165, pracSelectDrop - 50, num, numCol);
		pracArrows[1].draw(pracOffset + 595, 410);
		//for (int i = 0; i < medals.length; i++) {
		//if (pracLevel <= PuzzleModeLevel.nLevels) {
		
			if (medals[pracLevel] > 0) {
				if (medals[pracLevel] == 4) {
					for (int j = 1; j <= medals[pracLevel]; j++) {
						if (j == 4) {
							Yellow_star.draw(medalOffset * j + 700, pracSelectDrop - 55);
						} else {
							Silver_star.draw(medalOffset * j + 700, pracSelectDrop - 55);
						}
						//Yellow_star.draw(medalOffset * j + 700, 652);
						//medalOffset -= 5;
					}
				} else {
					for (int j = 1; j <= 4; j++) {
						//Yellow_star.draw(medalOffset * j + 100, firstDrop + i * interval + 5);
						//medalOffset -= 5;
					
					//else if ((4 - medals[j]) <= 0){
						if (j <= medals[pracLevel]) {
							Color.white.bind();
							Silver_star.draw(medalOffset * j + 700, pracSelectDrop - 55);
						} else {
							//for (int k = 1; k <= fillRemainStars; k++) {
								Color.black.bind();
								Silver_star.draw(medalOffset * j + 700, pracSelectDrop - 55);
								Color.white.bind();
							//}
						}
					}
				}
			}
			else {
				for (int k = 1; k <= 4; k++) {
					Color.black.bind();
					Silver_star.draw(medalOffset * k + 700, pracSelectDrop - 55);	
					Color.white.bind();
				}
			} 
		//}
		//}
	}	
	// TODO: move high score vars to top after finished implementation
	private boolean showHighScore = false;
	private final int hsBarSpace = 10;
	private final int hsBarHeight = 48;
	private int hsMargin = 30;
	private List<HighScoreRecord> hsRecords = new ArrayList<HighScoreRecord>(10);
	private Texture hsBack;
	private int[] hsBackShift = new int[] { 1, 0 };
	private float[] hsBackDraw = new float[] { 1024 / 4096f, 768 / 1024f };
	private int starMargin = 50;
	private int fillRemainStars = 0;
	private int adjustment = 0;//hsMargin;

	private void showHighScores() {
		int drawWidth = 1024 - 725;
		int interval = hsBarHeight + hsBarSpace;
		//int firstDrop = 100;
		int firstDrop = 52;
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
			//textColor = Color.black,
			resetColor = Color.white;
		
		Global.drawFont48(512 - 98, 25, "High Score", Color.white);
		//HighScoreRecord hsr;
		//int scoreOff = 0;
		
		int levelCounter = 0;
		//int currentLevel = 0;
		//for (int i = 1; i < medals.length; i++) {
		for (int k = 0; k < 3; k++) {
		for (int i = 1; i <= 10; i++) {
			//hsr = hsRecords.get(i);
			//hsr = ((ArrayList<HighScoreRecord>) hsLevelArray[i]).get(i);

				levelCounter++;
				//if (i < medals.length){
				//	currentLevel++;
				//}
				boxColor.bind();
				Global.uiTransWhite.draw((drawWidth + 35) * k + hsMargin , firstDrop + i * interval, drawWidth, hsBarHeight);
				resetColor.bind();
				Global.drawFont24((drawWidth + 35) * k + 10 + hsMargin , firstDrop + i * interval + 15, "Level " + (levelCounter), Color.white);


			if (i >= medals.length || levelCounter >= medals.length) {
				for (int j = 1; j <= 4; j++) {
					Color.black.bind();
					Silver_star.draw(((drawWidth + 35) * k) + (medalOffset * j + 100), firstDrop + i * interval + 3);
				}
			} else if (i < medals.length && levelCounter < medals.length){
				if (medals[levelCounter] == 4) {
					//if (i == 3) {
						for (int j = 1; j <= medals[levelCounter]; j++) {
	
							
							if (j == 4) {
								Color.white.bind();
								Yellow_star.draw(((drawWidth + 35) * k) + (medalOffset * j + 100), firstDrop + i * interval + 3);
							} else {
								Color.white.bind();
								Silver_star.draw(((drawWidth + 35) * k) + (medalOffset * j + 100), firstDrop + i * interval + 3);
							}
							/* else {
								for (int k = 1; k <= 4; k++) {
									Color.black.bind();
									Yellow_star.draw(medalOffset * k + 100, firstDrop + i * interval + 5);
								}
							}*/
							//medalOffset -= 5;
						//}
						}
						
				} else {
					fillRemainStars = 5 - medals[i];
					for (int j = 1; j <= 4; j++) {
						//Yellow_star.draw(medalOffset * j + 100, firstDrop + i * interval + 5);
						//medalOffset -= 5;
					
					//else if ((4 - medals[j]) <= 0){
						if (j <= medals[levelCounter]) {
							Color.white.bind();
							Silver_star.draw(((drawWidth + 35) * k) + (medalOffset * j + 100), firstDrop + i * interval + 3);
						} else {
							//for (int k = 1; k <= fillRemainStars; k++) {
								Color.black.bind();
								Silver_star.draw(((drawWidth + 35) * k) + (medalOffset * j + 100), firstDrop + i * interval + 3);
							//}
						}
					}
					//}
				}
			}
			//}
			//Global.uiTransWhite.draw(hsMargin, firstDrop + i * interval, drawWidth, hsBarHeight);
			//resetColor.bind();
			//Global.drawFont24(hsMargin + 10, firstDrop + i * interval + 15, hsr.getName(), textColor);
			//scoreOff = Global.getNumbers24DrawSize(hsr.getScoreAsString()); 
			//Global.drawNumbers24(hsMargin + 560, firstDrop + i * interval + 15, hsr.getScoreAsString(), textColor);
			//Global.drawNumbers24(hsMargin + 740 - scoreOff, firstDrop + i * interval + 15, hsr.getScoreAsString(), textColor);
			//Global.drawNumbers24(hsMargin + 770, firstDrop + i * interval + 15, hsr.getDate(), textColor);
			//Global.drawFont24(hsMargin + 920, firstDrop + i * interval + 15, hsr.getLevel(), textColor);
		}
		}
		Color.white.bind();
		//Global.menuButtonShader.bind();
		Global.uiTransWhite.draw(512 - 250, 700, 500, 48);
		//Color.white.bind();
		Global.drawFont24(512, 715, "Press [CANCEL] to return to game menu.", Color.black, true);
	}
	
	private String[] starInfo = new String[] {
			"-> The player is given stars upon completing a level",
			"      based on score and speed of completetion",
			"-> Three basic stars are given, then a player can",
			"      achieve a challenge star for completing an extra goal",
			
	};
	private String[] starTag = new String[] {
			"Standard Star",
			"Challenge Star"
	};
	private String[] basicInfo = new String[] {
			"-> Each level the user is given ruleson how to complete the level",
			"-> The level will state how to beat the level."
	};
	private boolean showPuzzleGuide = false;
	private int guideOffset = 40;
	private int starGuideOffset = 180;
	private final int screenWidth = 512; 
	/**
	 * This function draws the play guide for puzzle mode.
	 */
	protected void drawPuzzlePlayGuide() {
		GameSelector_background.draw(0, 0);
		
		Global.drawFont48(screenWidth - 250, 25, "Puzzle Mode Play Guide", Color.white);
		// Puzzle mode basic information
		Global.uiGreen.draw(screenWidth - 455, 115, screenWidth + 400, 100);
		Global.drawFont24(screenWidth - 450, 90, "Puzzle Mode Basic Rules", Color.white);
		for (int i = 0; i < basicInfo.length; i++) {
			Global.drawFont24(screenWidth - 450, guideOffset * i + 120, basicInfo[i], Color.white);	
		}
		
		// Scoring System Info
		Global.drawFont24(screenWidth - 455, 225, "Scoring System", Color.white);
		for (int i = 0; i < starInfo.length; i++ ) {
			Global.drawFont24(screenWidth - 450, guideOffset * i + 255, starInfo[i], Color.white);
		}
		for (int j = 0; j < starTag.length; j++) {
			if (j == 0) {
				Global.drawFont24(starGuideOffset * j + (screenWidth - 420), 405, starTag[j], Color.white);
				Yellow_star.draw(140, 425);
			} else {
				Global.drawFont24(starGuideOffset * j + (screenWidth - 420), 405, starTag[j], Color.white);
				Challenge_star.draw(325, 425);	
			}
			
		}
	}
	/**
	 * Load custom values and high scores from file. Loads default values for
	 * high scores if data is not present or corrupted.
	 */
	private void loadPrefs() {
		BufferedReader prefFile;
		String line;
		HighScoreRecord hsr;
		int i = 1;
		


		try {
			prefFile = new BufferedReader(new FileReader("puzzle.pref"));
			line = prefFile.readLine();
			while (line != null && i < medals.length) {
				//for (int i = 1; i < medals.length; i++) {
					//if (line != null) {
					if (line.compareToIgnoreCase("[HighScore" + i +"]") == 0) {
						try {
							line = prefFile.readLine();
							//while (line != null) {
							medals[i] = Integer.parseInt(line);
								
							line = prefFile.readLine();
							//}
						} catch (NumberFormatException nfe) {
							medals[i] = 0;
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
				//}
				i++;
				line = prefFile.readLine();
			}
			//}
			prefFile.close();
		} catch (IOException err) {
			
		} /*finally {
			Collections.sort(hsRecords);
			while (hsRecords.size() > 10) {
				hsRecords.remove(10);
			}
		}*/
		
	}
	
	/** 
	 * Saves custom values and high scores to file.
	 */
	private void savePrefs() {
		try {
			BufferedWriter prefFile = new BufferedWriter(new FileWriter("puzzle.pref"));
			prefFile.write("[TopLevel]");
			prefFile.newLine();
			prefFile.write(Integer.toString(maxUnlocked));
			prefFile.newLine();
			for (int i = 1; i < pracMax + 1; i++) {
				prefFile.write("[HighScore" + i +"]");
				//if (maxUnlocked == playLevel.level) {
					prefFile.newLine();
					///for(HighScoreRecord hsr : hsRecords) {
					//	prefFile.write(hsr.toString());
					if (i < medals.length) {
						prefFile.write(Integer.toString(medals[i]).toString());
						
					} else if (i >= medals.length){
						prefFile.write("0");
					}
					prefFile.newLine();
					//}
				//}
			}
			prefFile.close();
		} catch (IOException e) {
			Global.writeToLog("Error opening Standard mode preferences file for writing.", true);
			e.printStackTrace();
		}
		
	}
	
}

