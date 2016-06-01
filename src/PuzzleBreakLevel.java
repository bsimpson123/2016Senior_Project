import static org.lwjgl.opengl.GL11.*;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.newdawn.slick.opengl.Texture;

//import Block.BlockType;

import org.newdawn.slick.Color;
import org.newdawn.slick.openal.Audio;

public class PuzzleBreakLevel extends BlockBreakLevel {

	//private static GameSounds soundbank;
	//private static Audio blockfall;
	
	//protected static Sprite[] numbers = new Sprite[10];
	//private static Sprite pauseCursor;
	//protected static Sprite cursor;
	//protected static Sprite[] shiftLR = new Sprite[2];
	//protected static Sprite overlay;
	
	//protected static int score;
	//private static int scoreDisplay = 0;
	//private static long scoreUpdateDelayTimer = 50l;
	//private static long scoreUpdateDelay = scoreUpdateDelayTimer;
	
	//protected static Sprite levelDisplay;
	//protected static Sprite background;
	//protected static Sprite userInterface;
	//protected static Sprite emptyEnergy;
	//protected static Texture energyBar;
	
	//protected int energyMax = 100000;
	//protected int energy = energyMax;
	private int energyDisplay = energyMax;
	//protected float energyGainMultiplier = 1.0f;
	
	// grid variables
	//protected GridColumn[] grid;
	//protected int[] gridBasePos;
	//protected int[] blockSize = new int[] { 32, 32 };
	/** Defines which direction the grid columns should shift where there is space between them.<br>
	 * 1 => right-shift, -1 => left-shift, 0 => do not shift grid columns */
	//protected int gridShiftDir = 1;
	/** The amount of time the player must wait between each switch of the grid direction. */
	private final long gridShiftActionDelayTimer = 1000;
	private long gridShiftActionDelay = gridShiftActionDelayTimer;
	//protected int blocksRemaining = 0;
	//protected int[] wedgePos = new int[] { -1, -1 };
	private final long blockDropDelayTimer = 16l; // 32 is approx. 30 times/sec
	//private long blockDropDelay = blockDropDelayTimer;
	//private final int blockMoveRate = 8;
	private boolean blocksMoving = false;
	
	// grid queue variables
	//private Block[] queue;
	/** Time delay between each 'step' for the queue, lower values will cause the queue to advance quicker */
	private long queueStepDelayTimer = 500;
	//private long queueStepDelay = queueStepDelayTimer;
	/** The number of 'empty' steps to take before adding a block to the queue. */
	//private int queueStepReq = 4;
	//private int queueStepCount = 0;
	//private int queueCount = 0;
	/** The number of blocks that should be in the queue before forcibly adding to the grid */
	//private int queueLimit = 5;
	private final long queueManualShiftDelayTimer = 200;
	//private long queueManualShiftDelay = queueManualShiftDelayTimer;
	//private boolean queueHold = false;
	/** If <code>true</code>, no queue processing will be done. */
	//protected boolean queueDisabled = false;
	
	private boolean heartSpecialActive = false;
	//protected int[] cursorGridPos = new int[] { 0, 0 };
	//private int heartCursorPos = 0;
	protected boolean gamePaused = false;
	private long inputDelay = Global.inputReadDelayTimer;
	private long actionDelay = Global.inputReadDelayTimer * 2;
	//protected String levelTitle;
	//protected final int level;
	
	/** Sets sets the multiplier to apply to all score additions/subtractions. */
	//protected float levelMultiplier = 1.0f;
	/** Indicates if the level is over and should no longer be called. 
	 * Set to <code>true</code> to advance the level or return to the game mode screen. */
	//protected boolean levelFinished = false;
	/** Indicates if the finished level was completed successfully. 
	 * A <code>true</code> value will not allow the next level to load. */
	//protected boolean gameOver = false;
	/** Indicates if the game play for the level is completed. If <code>true</code>
	 * the level is over, but the game is not ready to advance to the next level. */
//	protected boolean levelComplete = false;
	/** Indicates whether or not the level is a practice level. Levels played in practice mode will not
	 * cause further level advancement or allow for high score recording when competed. */
	//protected boolean practice = false;
	/** Indicates whether or not the input has been delayed for end of level detection. */
	private boolean endLevelDelayed = false;

	//private int heartSelectColor = 0;
	private boolean clearColor;
	//private Block[] heartMenuBlocks = new Block[Block.blockColorCount];
	
	private int pauseCursorPos = 0;
	
	//private int[] blockCounts = new int[Block.blockColorCount];
	//private int allowedColors = 0;
	//private int totalColors = 0;
	//protected int minColors = 2;
	//private int heartGenChance = 20;
	//private int bombGenChance = 20;
	private int levelMedals = 0;
	
	
	/**
	 * Puzzle Mode variables
	 */
	//private int level = 0;

	private int remainClears = -1;
	
	private int movesDisplay = 0;
	private static long movesUpdateDelayTimer = 50l;
	private static long movesUpdateDelay = movesUpdateDelayTimer;
	private static int movesChange = 0;
	private final String[] pauseOptions = new String[] {
			"Resume",
			"Restart",
			"Quit"
	};
	
	private final String[] levelCompleteOptions = new String[] {
			"Next Level",
			"Restart",
			"Quit"
	};
	
	private final String[] gameOverOptions = new String[] {
			"Restart",
			"Quit"
	};
	private int[] pauseOptionSize = new int[3];
	private int[] levelCompleteOptionSize = new int[3];
	private int[] gameOverOptionsSize = new int [2];
	protected static boolean standCond = true;
	protected static boolean specCond = false;
	protected int levelMedal = 0;
	protected int oldMedal = 0;
	//protected int[] levelClears = new int[nLevels + 1];
	
	protected boolean noMoves = false;
	protected static int sumMoves = 0;
	protected boolean noRemainClears = false;
	
	/**
	 * Scoring System variables
	 * 
	 */
	protected static boolean useScore = true;
	protected static boolean useTime = true;
	protected static int scoreMedal1 = 2500;
	protected static int scoreMedal2 = 5000;
	protected static int scoreMedal3 = 25000;


	
	protected static Sprite GoldStar;
	protected static Sprite ChallengeStar;
	protected static Sprite SilverStar;
	
	/**
	 * ending condition variables
	 */
	protected boolean specialCond = false;
	protected boolean standardCond = true;
	
	protected int[] coordinates = new int[2];
	
	protected int level;
	
	protected static int nLevels = 20;
	protected static int[] levelClears = new int[nLevels + 1];
	protected static int[] medals = new int[nLevels + 1];
	
	protected static int[] scoreMedal1st = new int[nLevels + 1];// = 2500;
	protected static int[] scoreMedal2nd = new int[nLevels + 1];// = 5000;
	protected static int[] scoreMedal3rd = new int[nLevels + 1];// = 25000;
	
	
	private int addScore = 0;
	
	private int medalOffset = 45;
		
	public static void buildStaticAssets(HashMap<String,Texture> localTexMap) {
		overlay = new Sprite(
				Global.textureMap.get("overlay"),
				new int[] { 0, 0 },
				new int[] { 1024, 768 },
				new int[] { 1024, 768 }
			);
		cursor = new Sprite(
				Global.textureMap.get("blocksheet"),
				new int[] { 240, 0 },
				new int[] { 32, 32 },
				new int[] { 32, 32 }
			);
		shiftLR[0] = new Sprite( // left indicator
				localTexMap.get("white_ui_controls"),
				new int[] { 300, 600 },
				new int[] { 100, 100 },
				new int[] { 100, 100 }
			);
		shiftLR[1] = new Sprite( // right indicator
				localTexMap.get("white_ui_controls"),
				new int[] { 200, 300 },
				new int[] { 100, 100 },
				new int[] { 100, 100 }
			);
		emptyEnergy = new Sprite(
				localTexMap.get("energy_empty"),
				new int[] { 0, 0 },
				new int[] { 512, 32 },
				new int[] { 640, 32 }
			);
		GoldStar = new Sprite(
				localTexMap.get("Gold_Star"),
				new int[] {0,0},
				new int[] {31,30},
				new int[] {41,40}
				);
		ChallengeStar = new Sprite(
				localTexMap.get("Chall_Star"),
				new int[] {0,0},
				new int[] {31,30},
				new int[] {41,40}
				);
		SilverStar = new Sprite(
				localTexMap.get("Silver_Star"),
				new int[] {0,0},
				new int[] {31,30},
				new int[] {41,40}
				);
		energyBar = localTexMap.get("energybar");
		int offset = 0;
		for (int i = 0; i < numbers.length; i++) {
			offset = i * 24 - 1;
			numbers[i] = new Sprite(
					localTexMap.get("number_white"),
					new int[] { offset, 0 },
					new int[] { 24, 30 },
					new int[] { 24, 30 }
				);
		}
		background = new Sprite(
				localTexMap.get("bg_space_1"),
				new int[] { 0, 0 },
				new int[] { 1024, 768 },
				new int[] { Global.glEnvWidth, Global.glEnvHeight }
			);
		
		userInterface = new Sprite(
				localTexMap.get("ui_stdmode"), // default interface texture
				new int[] { 0, 0 },
				new int[] { 1024, 768 },
				new int[] { Global.glEnvWidth, Global.glEnvHeight }
			);
		
		String[] soundList = new String[] {
				
		};
		
		//levelClears = levelClears;//new int[nLevels + 1];
		medals = BlockPuzzleMode.medals;//new int[nLevels + 1];
		//scoreMedal1st = new int[nLevels + 1];// = 2500;
		//scoreMedal2nd = new int[nLevels + 1];// = 5000;
		//scoreMedal3rd = new int[nLevels + 1];// = 25000;
		
	}
	
	
	public PuzzleBreakLevel(int levelSelect) { 
		super(levelSelect);

		//level = levelSelect;
		level = levelSelect;
		levelClears[level] = 5;
		buildGrid(level);
		levelTitle = String.format("Level %02d", level);
			
		//}

	} 
	
	private GridColumn[] buildGrid(String source) {
		return GridColumn.loadFromFile(source);
	}
	
	protected int scoreSystem(int levelScore) {
		//if (useScore) {
			/*if (score <= scoreMedal1) {
				//medals[level] = 1;
				levelMedal = 1;
			} else if (score > scoreMedal1 && score <= scoreMedal2) {
				//medals[level] = 2;
				levelMedal = 2;
			} else if (score >= scoreMedal2) {
				//medals[level] = 3;
				levelMedal = 3;
			}
		//} 
		//if (useTime) {
			if (energy > 0 && levelMedal == 3) {
				//medals[level] = 4;
				levelMedal = 4;
			}*/
			
			if (levelScore <= scoreMedal1st[level]) {
				//medals[level] = 1;
				levelMedals = 1;
			} else if (levelScore > scoreMedal1st[level] && levelScore <= scoreMedal2nd[level]) {
				//medals[level] = 2;
				levelMedals = 2;
			} else if (levelScore > scoreMedal2nd[level]) {
				//medals[level] = 3;
				levelMedals = 3;
			}
		//} 
		//if (useTime) {
			if (energy > 0 && levelMedals == 3) {
				//medals[level] = 4;
				levelMedals = 4;
			}
			return levelMedals;
		//}
	}
	
	protected void dropNewBlocks() {
		
		
	}
	
	protected void specialEndingConditions() {
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].blocks.length; j++) {
				//if (actionDelay == 0) {
				if (grid[i].blocks[j] == null) {
					continue;
				} 
				else if (grid[i].blocks[j].type == Block.BlockType.ROCK && j == 0) {
					//sumMoves += checkGridMovesRemain(i, j, grid, grid[i].blocks[j].colorID);
					//while (grid[i].blocks[j].dropDistance != 0) {
					levelComplete = true;
					if (!endLevelDelayed) {
						endLevelDelayed = true;
						pauseCursorPos = 0;
						score += remainClears >> 6;
						//addScore += remainClears >> 6;
						//updateScore(addScore);
						levelMedal = scoreSystem(score);
						if (levelMedal >= medals[level]) {
							medals[level] = levelMedal;
						}
						//energy = 0;
						//score = 0;
						inputDelay = Global.inputReadDelayTimer * 2;
						break;
					}
					//coordinates[0] = i;
					//coordinates[1] = j;
					//sumMoves += checkGrid(coordinates);
					
				} 
			//	else {
			//		sumMoves++;
			//		continue;
			//	}
				//}
				//}
			}				
		}
	}
	
	@Override
	protected void buildGrid(int levelSelect) {
		// set the energy amount for the level
		energy = energyMax = 20000;
		levelMultiplier = 1.0f;
		energyGainMultiplier = 1.0f;
		blockSize = new int[] { 32, 32 }; // default block size is { 32, 32 }
		// time between each 'step' for the queue, lower values will cause the queue to advance quicker
		//queueStepDelayTimer = 500;
		//queueStepDelay = queueStepDelayTimer;
		// the number of 'empty' steps to take before adding a block to the queue
		//queueStepReq = 4;
		// the number of blocks that should be in the queue before forcibly adding to the grid
		//queueLimit = 5;
		// disable the queue. no queue processing will be done if set to true
		queueDisabled = true;

		Global.rand.setSeed(LocalDateTime.now().getNano());

		//level = levelSelect;
		
		Block b = null;
		int r, rx, ry;
		// TODO: finish all level grid builds
		/* The switch/case statements below are for building the level-dependent grids.
		 * Variables for blocks remaining, wedge positioning, allowed block color generation, etc.,
		 * are calculated by setGridCounts() after the grid is built.
		 */
		switch (levelSelect) {
			case 1:
				levelClears[level] = 30;
				grid = new GridColumn[20];
				for (int i = 0; i < grid.length; i++) {
					grid[i] = new GridColumn(20);
					for (int k = 0; k < grid[0].blocks.length; k++) {
						//grid[i].blocks[k] = new Block(Block.BlockType.BLOCK, );
						
						grid[i].blocks[k] = null;
						//grid[i].blocks[k] = new Block(Block.BlockType.BLOCK, ((i + k) % 5) ^ 5);
						r = (i % 4) | (k % 4);
						grid[i].blocks[k] = new Block(Block.BlockType.BLOCK, r);
						//grid[i].blocks[k] = new Block(Block.BlockType.BLOCK,
						//		(i % 4) | (k % 3)
						//	);
						//if (i % 2 == 0 && k % 2 == 0) {
						//	r = 1;
						//} else {
						//	r = 2;
						//}
						//grid[i].blocks[k] = new Block(Block.BlockType.BLOCK, r);
						
					}
				}
				scoreMedal1st[level] = 56000;
				scoreMedal2nd[level] = 56200;
				scoreMedal3rd[level] = 25000;
				
				break;
			case 2:
				// 3 colors and many bombs
				grid = new GridColumn[20];
				for (int i = 0; i < grid.length; i++) {
					grid[i] = new GridColumn(20);
					for (int k = 0; k < grid[0].blocks.length; k++) {
						//r = Global.rand.nextInt(256);
						if (i % 2 == 0) {
							r = 1;
						} else if (k % 2 == 0){
							r = 2;
						} else {
							r = 3;
						}
											//} else {
						grid[i].blocks[k] = new Block(Block.BlockType.BLOCK, r);
						//}
						//grid[i].blocks[k] = b;
					}
				}
				levelClears[level] = 31;
				
				scoreMedal1st[level] = 5000;
				scoreMedal2nd[level] = 14000;
				scoreMedal3rd[level] = 25000;
				
				//scoreMedal1 = 5000;
				//scoreMedal2 = 14000;
				//scoreMedal3 = 152000;

				//levelClears[levelSelect] = 30;
				break;
			case 3:
				// 3 colors, no bombs
				grid = new GridColumn[20];
				for (int i = 0; i < grid.length; i++) {
					grid[i] = new GridColumn(20);
					for (int k = 0; k < grid[0].blocks.length; k++) {
						grid[i].blocks[k] = new Block(Block.BlockType.BLOCK, ((i + k) % 5) ^ 5);
						
						//grid[i].blocks[k] = new Block(Block.BlockType.BLOCK, Global.rand.nextInt(3));
					}
				}
				levelClears[level] = 50;
				scoreMedal1 = 2500;
				scoreMedal2 = 5000;
				scoreMedal3 = 25000;

				break;
			case 4:
				// 3 colors (2 new)
				grid = new GridColumn[20];
				for (int i = 0; i < grid.length; i++) {
					grid[i] = new GridColumn(20);
					for (int k = 0; k < grid[0].blocks.length; k++) {
						if ((k + i) % 2 == 0) {
							r = 1;
						} else if((k - i) % 2 == 1) { 
							r = 4;
						} else if (i % 2 == 0) {
							r = 1;
						}
						else {
							r = 2;
						}
						//if (r > 16) { 
							grid[i].blocks[k] = new Block(Block.BlockType.BLOCK, r);

						//grid[i].blocks[k] = new Block(Block.BlockType.BLOCK, Global.rand.nextInt(3) + 2);
					}
				}
				levelClears[level] = 50;
				scoreMedal1 = 2500;
				scoreMedal2 = 5000;
				scoreMedal3 = 25000;

				break;
			case 5:
				grid = GridColumn.loadFromFile("media/sp6.csv");
				scoreMedal1 = 2500;
				scoreMedal2 = 5000;
				scoreMedal3 = 25000;

				break;
			case 6:
				// 3 colors, first show of the wedge block, with heart block
				grid = new GridColumn[20];
				for (int i = 0; i < grid.length; i++) {
					grid[i] = new GridColumn(20);
					for (int k = 0; k < grid[0].blocks.length; k++) {
						grid[i].blocks[k] = new Block(Block.BlockType.BLOCK, 1);
					}
				}
				grid[0].blocks[1] = new Block(Block.BlockType.BLOCK, 2);
				grid[0].blocks[2] = new Block(Block.BlockType.BLOCK, 3);
				grid[0].blocks[3] = new Block(Block.BlockType.BLOCK, 4);
				//grid[i].blocks[k] = new Block(Block.BlockType.HEART);
				//rx = Global.rand.nextInt(10) + 5;
				//ry = Global.rand.nextInt(4) + 8;
				//grid[rx].blocks[ry] = new Block(Block.BlockType.WEDGE);
				scoreMedal1 = 2500;
				scoreMedal2 = 5000;
				scoreMedal3 = 25000;

				break;
			case 7:
				levelClears[level] = 40;
				// 3 colors, wedge, no starter heart block
				grid = new GridColumn[20];
				for (int i = 0; i < grid.length; i++) {
					grid[i] = new GridColumn(20);
					for (int k = 0; k < grid[0].blocks.length; k++) {
						grid[i].blocks[k] = new Block(Block.BlockType.BLOCK,
								(i % 4) | (k % 3)
							);
					}
				}
				//rx = Global.rand.nextInt(10) + 5;
				//ry = Global.rand.nextInt(4) + 8;
				//grid[rx].blocks[ry] = new Block(Block.BlockType.WEDGE);
				scoreMedal1 = 2500;
				scoreMedal2 = 5000;
				scoreMedal3 = 25000;

				break;
			case 8:
				// 3 colors (last 3), wedge
				grid = new GridColumn[20];
				for (int i = 0; i < grid.length; i++) {
					grid[i] = new GridColumn(20);
					for (int k = 0; k < grid[0].blocks.length; k++) {
						grid[i].blocks[k] = new Block(Block.BlockType.BLOCK, Global.rand.nextInt(3) + 3);
					}
				}
				rx = Global.rand.nextInt(10) + 5;
				ry = Global.rand.nextInt(4) + 8;
				grid[rx].blocks[ry] = new Block(Block.BlockType.WEDGE);
				scoreMedal1 = 2500;
				scoreMedal2 = 5000;
				scoreMedal3 = 25000;

				break;
			case 9:
				// 4 colors, no wedge
				grid = new GridColumn[20];
				for (int i = 0; i < grid.length; i++) {
					grid[i] = new GridColumn(20);
					for (int k = 0; k < grid[0].blocks.length; k++) {
						grid[i].blocks[k] = new Block(Block.BlockType.BLOCK, Global.rand.nextInt(4) + 1);
					}
				}
				scoreMedal1 = 2500;
				scoreMedal2 = 5000;
				scoreMedal3 = 25000;

				break;
			case 10:
				grid = GridColumn.loadFromFile("level2.dat");
				scoreMedal1 = 2500;
				scoreMedal2 = 5000;
				scoreMedal3 = 25000;
				levelClears[level] = 50;
				
				//grid = GridColumn.loadFromFile("media/sp6.csv");
				break;
			case 11:
				grid = GridColumn.loadFromFile("level6.dat");
				scoreMedal1 = 2500;
				scoreMedal2 = 5000;
				scoreMedal3 = 25000;
				levelClears[level] = 50;

				break;
			case 15:
				grid = GridColumn.loadFromFile("level4.dat");
				scoreMedal1 = 2500;
				scoreMedal2 = 5000;
				scoreMedal3 = 25000;
				levelClears[level] = 50;
				gridShiftDir = 0;

				break;
			case 20:
				grid = GridColumn.loadFromFile("level6.dat");
				scoreMedal1 = 2500;
				scoreMedal2 = 5000;
				scoreMedal3 = 25000;
				levelClears[level] = 50;

				break;
			default:
				grid = new GridColumn[20];
				for (int i = 0; i < grid.length; i++) {
					grid[i] = new GridColumn(20);
					for (int k = 0; k < grid[0].blocks.length; k++) {
						if (i % 2 == 0 && k % 2 == 0) {
							r = 1;
						} else {
							r = 2;
						}
						grid[i].blocks[k] = new Block(Block.BlockType.BLOCK, r);
					}
				}
				scoreMedal1 = 2500;
				scoreMedal2 = 5000;
				scoreMedal3 = 25000;

				break;
		}
		gridBasePos = new int[] { 20, Global.glEnvHeight - blockSize[1] - 50 };
		cursorGridPos[0] = grid.length / 2;
		cursorGridPos[1] = grid[0].blocks.length / 2;
		//queue = new Block[grid.length];
		setGridCounts();
	}
	
	@Override
	public void run() {
		// decrement input delay variables
		actionDelay -= Global.delta;
		inputDelay -= Global.delta;
		
		background.draw(0, 0);

		//Global.writeToLog( String.format("actionDelay: %d", actionDelay) , true );
		//scoreSystem();
		specialEndingConditions();
		if (blocksRemaining == 0 && remainClears > 0 && actionDelay <= 0) {
			levelComplete = true;
			//Global.writeToLog( String.format("levelComplete: %b", levelComplete) , true );
			if (!endLevelDelayed) {
				endLevelDelayed = true;
				pauseCursorPos = 0;
				score += remainClears >> 6;
				//addScore += remainClears >> 6;
				//updateScore(addScore);
				levelMedal = scoreSystem(score);
				if (levelMedal >= medals[level]) {
					medals[level] = levelMedal;
				}
				//energy = 0;
				//score = 0;
				inputDelay = Global.inputReadDelayTimer * 2;
			}
		} 
		if (blocksRemaining > 0 && remainClears == 0 && actionDelay == 0) {
			//if (!endLevelDelayed) {
			//	endLevelDelayed = true;
				gameOver = true;
				noRemainClears = true;
				pauseCursorPos = 0;
			//}
		}
		//if (energy == 0 && !gameOver) {
			// game over
		//	gameOver = true;
		//	pauseCursorPos = 0;
		//}
		
		//sumMoves = 0;
		/*if (blocksRemaining > 0 && remainClears > 0) {
			//sumMoves = 0;
			for (int i = 0; i < grid.length; i++) {
				for (int j = 0; j < grid[0].blocks.length; j++) {
					if (actionDelay <= 0) {
					//if (grid[i].blocks[j] == null) {
					//	continue;
					//} 
					//if (grid[i].blocks[j].type == Block.BlockType.BLOCK) {
						//sumMoves += checkGridMovesRemain(i, j, grid, grid[i].blocks[j].colorID);
						//while (grid[i].blocks[j].dropDistance != 0) {
						coordinates[0] = i;
						coordinates[1] = j;
						
						sumMoves += checkGrid(coordinates);
						Global.writeToLog( String.format("sumMoves: %d", sumMoves) , true );
						
					} 
				//	else {
				//		sumMoves++;
				//		continue;
				//	}
					//}
				//	}
				}				
			}
		}
			//sumMoves = 0;
			if ( sumMoves <= 0 && actionDelay <= 0) {
				noMoves = true;
				gameOver = true;
				pauseCursorPos = 0;
			} else if (sumMoves >= 1) {
				sumMoves = 0;
			}*/
		//}
		if (blocksRemaining == 1 && actionDelay == 0 && remainClears > 0) {
	        // game over with one block remaining
			noMoves = true;
			gameOver = true;
			pauseCursorPos = 0;
		}
		
		if (!gamePaused && !gameOver && !levelComplete) {
			// process active gameplay
			//queueManualShiftDelay -= Global.delta;
			gridShiftActionDelay -= Global.delta;
		
			//processQueue();
			if (!disableEnergy) {
				energy -= Global.delta;
				if (energy < 0) { energy = 0; }
				else if (energy > energyMax) { energy = energyMax; }
			}
			// draw the grid, return value indicates if there are blocks still falling from the last clear
			processGridBlocks(grid);
			drawGrid(grid);
			
			drawCursor();

			// check if heart special control is active and handle accordingly
			if (heartSpecialActive) {
				/** @author Brock */
				DrawHeartSelector(); 
				heartMenuControls();
				if (clearColor) {
					int counter = activateHeartBlock(cursorGridPos);
					updateScore(counter);
					addEnergy(counter);
					removeMarkedBlocks();
					//dropBlocks();
					//shiftGridColumns();
					heartSpecialActive = false;
					clearColor = false;

				}
			} else { // no special circumstance, handle input normally
				if (inputDelay <= 0l) {
					checkCommonControls();
				}
			}
		}
		drawTopLevelUI();
	}
	
	//protected final int checkGrid(int xc, int yc) {
	//	if (grid[xc].blocks[yc] == null) { return 0; }
	//	if (grid[xc].blocks[yc].type == Block.BlockType.BOMB)  {return 2; }
	//	if (grid[xc].blocks[yc].type == Block.BlockType.STAR)  {return 2; }
	//	if (grid[xc].blocks[yc].type == Block.BlockType.HEART)  {return 2; }
	//	if (grid[xc].blocks[yc].type != Block.BlockType.BLOCK) { return 0; }
	//	return checkGrid(xc, yc, grid[xc].blocks[yc].colorID);
	//}
	
	protected void resetVariables() {
		/*if (gameOver) {
			gameOver = false;
			//energy = energyMax;
			//score = score/2;
			remainClears = totalClears;
			noMoves = false;
			noRemainClears = false;
			//resetMoves = true;
			buildGrid();
			score = 0;
			energy = energyMax;
			inputDelay = 4 * Global.inputReadDelayTimer;
		} else if (gamePaused) {
			gameOver = false;
			remainClears = totalClears;
			noMoves = false;
			noRemainClears = false;
			buildGrid();
			score = 0;
			gamePaused = false;
			energy = energyMax;
			inputDelay = Global.inputReadDelayTimer;
		} else if (levelComplete) {*/
			gameOver = false;
			levelFinished = false;
			levelComplete = false;
			gamePaused = false;
			endLevelDelayed = false;
			levelMedal = 0;
			//energy = energyMax;
			//score = score/2;
			remainClears = -1;
			//remainClears = -1;
			noMoves = false;
			noRemainClears = false;
			buildGrid(level);
			pauseCursorPos = 0;
			score = 0;
			cursorGridPos[0] = grid.length / 2;
			cursorGridPos[1] = grid[0].blocks.length / 2;
			pauseCursorPos = 0;
			energy = energyMax;
			inputDelay = 3 * Global.inputReadDelayTimer;	
		//}
	}
	
	/**
	 * @author Brock
	 */
	protected void drawMovesRemain() {
		//if (remainClears == -1) {
		//	remainClears = totalClears;
		//	movesDisplay = remainClears;
		//}
		if (remainClears >= 0) {
			movesUpdateDelay -= Global.delta;
			//inputDelay -= Global.delta;
			//remainClears = 6;
			//if (resetMoves) {
			//	remainClears = levelClears[level];
			//	movesDisplay = remainClears;
			//	resetMoves = false;
			//}
			if (movesUpdateDelay <= 0 && remainClears != movesDisplay) {
				//if (movesDisplay < remainClears) {
				//	movesChange = (remainClears - movesDisplay) >> 2;
				//	if (movesChange == 0) { change = 1;}
				//	movesDisplay += movesChange;
				//	if (movesDisplay > remainClears) {movesDisplay = remainClears; }
				//} else {
					movesChange = (movesDisplay - remainClears) >> 2;
					if (movesChange == 0) { movesChange = 1; }
				    movesDisplay -= movesChange;
					if (movesDisplay < remainClears) { movesDisplay = remainClears; }
					//movesDisplay = remainClears;
				//}
				movesUpdateDelay = movesUpdateDelayTimer;//Global.inputReadDelayTimer;//movesUpdateDelayTimer;
			} //else if (movesUpdateDelay > 0) {
			//	movesUpdateDelay -= Global.delta;
			//}
			//	movesUpdateDelay -= Global.delta;
			//}

				//else {
			//movesUpdateDelay -= Global.delta;
			//	inputDelay -= Global.delta;
			//}
		} 
		else {

			movesUpdateDelay = movesUpdateDelayTimer * 2;
			remainClears = levelClears[level];
			movesDisplay = remainClears;
		}


		char[] strMoves = Integer.toString(movesDisplay).toCharArray();

		//String strMoves = Integer.toString(movesDisplay).toString();
		int offsetX = 840;//780;
		int yPos = 450;
		for (int i = strMoves.length - 1; i >= 0; i--) {
			getNumber(strMoves[i]).draw(offsetX, yPos);
			offsetX -= 24;
			
		}
		/*for (int i = strMoves.length() - 1; i >= 0; i--) {
			//getNumber(strMoves[i]).draw(offsetX, yPos);
			offsetX -= 24;
			Global.drawFont48(840, 450, strMoves, Color.white);
		}*/
		for (int i = strMoves.length; i < 2; i++) {
			numbers[0].draw(offsetX, yPos);
			offsetX -= 24;
		}
	}
	

	/**
	 * @author John
	 */
	@Override
	protected void drawTopLevelUI() {
		Global.uiRed.draw(700, 16, 300, 56);
		Global.uiBlue.draw(700, 72, 300, 96);
		userInterface.draw(0,0);
		Global.drawFont48(710, 25, levelTitle, Color.white);
		Global.drawFont48(710, 80, "Score", Color.white);
		/*int offsetX = 860;
		int yPos = 16;
		int[] numResize = new int[] { 30, 40 }; //*/
		drawScore();
		drawMovesRemain();
		Global.uiGreen.draw(680, 500, 100, 100);
		if (gridShiftDir == 1) {
			shiftLR[1].draw(680, 500);
		} else {
			shiftLR[0].draw(680, 500);
		}
		drawEnergy();

		if (levelComplete) {
			//drawGrid();
			overlay.draw(0, 0);
			//nLevel.draw(200, 200);
			levelFinishedControls();
			
			for (int i = 0; i < levelCompleteOptions.length; i++) {
				levelCompleteOptionSize[i] = Global.getFont24DrawSize(levelCompleteOptions[i]) / 2;
			}
			Color.white.bind();
			Global.uiWhite.draw(180, 280, 512, 250);
			
			for (int i = 0; i < levelCompleteOptions.length; i++) {
				Global.menuButtonShader.bind();
				Global.uiTransWhite.draw(212, 305 + i * 70, 190, 48);
				if (pauseCursorPos == i) {
					Global.drawFont24(305 - levelCompleteOptionSize[i], 319 + i * 70, levelCompleteOptions[i], Color.white);
					Color.white.bind();
				} else {
					Global.drawFont24(305 - levelCompleteOptionSize[i], 319 + i * 70, levelCompleteOptions[i], Color.black);
				}
			}		
			
			Color.lightGray.bind();
			Global.uiWhite.draw(420, 303, 252, 192);
			
			/*if (Global.getControlActive(Global.GameControl.CANCEL)) {
				this.levelFinished = true;
				Global.actionDelay = Global.inputReadDelayTimer;
			}*/
			Color.white.bind();
			//for (int j = 1; j <= medals[level]; j++) {
			if (levelMedal > 0) {
				for (int j = 1; j <= levelMedal; j++) {
					if (j == 4) {
						GoldStar.draw(medalOffset * j + 425, 415);
					} else {
						SilverStar.draw(medalOffset * j + 425, 415);
					}
					//medalOffset -= 5;
				}
			} //else {
			
			
			
			//pauseMenuFrame.draw(412,250); //180 250
			/*Global.uiBlue.draw(387, 250, 250, 250);
			Global.menuButtonShader.bind();
			Global.uiTransWhite.draw(417, 312, 190, 48);
			Global.uiTransWhite.draw(417, 372, 190, 48);
			Color.white.bind();
			if (pauseCursorPos == 0) {
				Global.drawFont24(512, 320, "Next Level", Color.white, true);
				Global.drawFont24(512, 380, "Quit", Color.black, true);
			} else if (pauseCursorPos == 1) {
				Global.drawFont24(512, 320, "Next Level", Color.black, true);
				Global.drawFont24(512, 380, "Quit", Color.white, true);
			}*/
				
			
			/* if (pauseCursorPos == 0) {
				//pauseBox.draw(457, 372);
				//hoverBox.draw(210, 310);
				pauseBox.draw(457, 312);
				Global.drawFont24(488, 312, "Next Level", Color.white);
				Global.drawFont24(512, 372, "Quit", Color.black);
			} else if (pauseCursorPos == 1) {
				//hoverBox.draw(210, 370);
				pauseBox.draw(457, 372);
				pauseBox.draw(457, 312);
				Global.drawFont24(512, 372, "Quit", Color.white);
				Global.drawFont24(488, 312, "Next Level", Color.black);
			} //*/
			//if (actionDelay < 0 && Global.getControlActive(Global.GameControl.SELECT)) {
			//	levelFinished = true;
			//}
			// placeholder for level advancement
		} else if (gamePaused) {
			/** @author Brock */
			pauseControls();

			overlay.draw(0, 0);
			/*Global.uiBlue.draw(387, 250, 250, 250);
			Global.menuButtonShader.bind();
			Global.uiTransWhite.draw(417, 312, 190, 48);
			Global.uiTransWhite.draw(417, 372, 190, 48);
			Color.white.bind();
			if (pauseCursorPos == 0) {
				Global.drawFont24(512, 320, "Resume", Color.white, true);
				Global.drawFont24(512, 380, "Quit", Color.black, true);
			} else if (pauseCursorPos == 1) {
				Global.drawFont24(512, 320, "Resume", Color.black, true);
				Global.drawFont24(512, 380, "Quit", Color.white, true);
			}*/

			Global.uiWhite.draw(180, 280, 250, 250);
			
			for (int i = 0; i < pauseOptions.length; i++) {
				pauseOptionSize[i] = Global.getFont24DrawSize(pauseOptions[i]) / 2;
			}
			for (int i = 0; i < pauseOptions.length; i++ ) {
				Global.menuButtonShader.bind();
				Global.uiTransWhite.draw(212, 305 + i * 70, 190, 48);
				Color.white.bind();
				if (pauseCursorPos == i) {
					Global.drawFont24(305 - pauseOptionSize[i], 319 + i * 70, pauseOptions[i], Color.white);
				} else {
					Global.drawFont24(305 - pauseOptionSize[i], 319 + i * 70, pauseOptions[i], Color.black);
				}
			} 
		} else if (gameOver) {
			//drawGrid(grid);
			
			showGameOver();
		}
	}
	
	/**
	 * @author Brock
	 */
	@Override
	protected void showGameOver() {
		overlay.draw(0, 0);
		gameOverControls();
		
		//Color.lightGray.bind();
		//Global.uiWhite.draw(256, 192, 512, 384);
		//Color.blue.bind();
		//Global.uiWhite.draw(288, 224, 192, 48); // left button
		//Global.uiWhite.draw(546, 224, 192, 48); // right button
		//Color.white.bind();
		//Global.uiWhite.draw(288, 288, 452, 192);
		
		for (int i = 0; i < gameOverOptions.length; i++) {
			gameOverOptionsSize[i] = Global.getFont24DrawSize(gameOverOptions[i]) / 2;
		}
		//Color.lightGray.bind();
		Color.white.bind();
		//Global.uiWhite.draw(180, 200, 512, 384);
		Global.uiWhite.draw(180, 280, 512, 250);

		//Color.white.bind();
		//Global.uiWhite.draw(288, 288, 452, 192);
		//Global.uiWhite.draw(288, 288, 452, 170);
		
		Color.lightGray.bind();
		Global.uiWhite.draw(420, 303, 252, 192);
		for (int i = 0; i < gameOverOptions.length; i++) {
			//Color.blue.bind();
			//Global.uiWhite.draw(288, 224, 192, 48); // left button
			//Global.uiWhite.draw(546, 224, 192, 48); // right button
			//Global.uiWhite.draw(425, 480, 192, 48); // bottom button
			Global.menuButtonShader.bind();
			Global.uiTransWhite.draw(212, 340 + i * 70, 190, 48);
			
			Color.white.bind();
			
			Global.drawFont24(490, 365, "GAME OVER", Color.black);
			//Global.drawFont24(500, 389, "Try Again", Color.black);
			//if (noMoves) {
				if (pauseCursorPos == i) {
					Global.drawFont24(305 - gameOverOptionsSize[i], 355 + i * 70, gameOverOptions[i], Color.white);
					if (noMoves) {
						Color.white.bind();
						switch (pauseCursorPos) {
							case 0:
								Global.drawFont24(442, 389, "No Remaining Moves!", Color.black);
								Global.drawFont24(500, 422, "Try Again?", Color.black);
								break;
							case 1:
								Global.drawFont24(500, 389, "Quit?", Color.black);
								break;
						}
					} else if (noRemainClears) {
						Color.white.bind();
						switch (pauseCursorPos) {
							case 0:
								Global.drawFont24(442, 389, "No Remaining Clears!", Color.black);
								Global.drawFont24(500, 422, "Try Again?", Color.black);
								break;
							case 1:
								Global.drawFont24(530, 389, "Quit?", Color.black);
								break;
						} 
					} 
				} else {
					Global.drawFont24(305 - gameOverOptionsSize[i], 355 + i * 70, gameOverOptions[i], Color.black);
				}
		}
		
		/*if (pauseCursorPos == 0) {
			Global.drawFont24(330, 240, "Continue?", Color.white);
			Global.drawFont24(618, 240, "Quit", Color.black);
			Global.drawFont24(304, 320, "Continue from this level with half of", Color.black);
			Global.drawFont24(308, 350, "your current score.",Color.black);
		}
		
		if (pauseCursorPos == 1) {
			Global.drawFont24(330, 240, "Continue?", Color.black);
			Global.drawFont24(618, 240, "Quit", Color.white);
			Global.drawFont24(440, 380, "Quit the level.", Color.black);
		}
		
		if (Global.getControlActive(Global.GameControl.CANCEL)) {
			this.levelFinished = true;
			Global.actionDelay = Global.inputReadDelayTimer;
		}*/
	}
	
	/** 
	 * @author Brock
	 * 
	 */
	@Override
	protected void pauseControls() {
		if (inputDelay <= 0) {
			if (Global.getControlActive(Global.GameControl.UP)) {
				pauseCursorPos--;
				if (pauseCursorPos < 0) {
						pauseCursorPos = 2;
				}
				inputDelay = Global.inputReadDelayTimer * 2;
			}
			if (Global.getControlActive(Global.GameControl.DOWN)) {
				pauseCursorPos++;
				if (pauseCursorPos > 2) {
					pauseCursorPos = 0;
				}
				inputDelay = Global.inputReadDelayTimer * 2;
			}
			if (Global.getControlActive(Global.GameControl.CANCEL)) { // Cancel key moves the cursor to the program exit button
				gamePaused = false;
				inputDelay = Global.inputReadDelayTimer * 2;
			}
			if (Global.getControlActive(Global.GameControl.PAUSE)) { // Cancel key moves the cursor to the program exit button
				gamePaused = false;
				inputDelay = Global.inputReadDelayTimer * 2;		
			}
			if (Global.getControlActive(Global.GameControl.SELECT)) {
				switch (pauseCursorPos) {
					case 0:
						gamePaused = false;
						inputDelay = Global.inputReadDelayTimer;	
						break;
					case 1:
						buildGrid(level);
						gamePaused = false;
						remainClears = -1;
						//levelFinished = true;
						//gameOver = true;
						score = 0;
						inputDelay = Global.inputReadDelayTimer;	
						break;
					case 2:
						levelFinished = true;
						gameOver = true;
						buildGrid(level);
						remainClears = -1;
						score = 0;
						inputDelay = Global.inputReadDelayTimer;
						break;
				}
			}
		} else if (inputDelay > 0) {
			inputDelay -= Global.delta;
		}
	}
	
	/** @author Mario */
	@Override
	protected void gameOverControls() {
		if (inputDelay <= 0) {
			if (Global.getControlActive(Global.GameControl.UP)) {
				//pauseCursorPos = pauseCursorPos == 0 ? 1 : 0;
				pauseCursorPos--;
				if (pauseCursorPos < 0) {
						pauseCursorPos = 1;
				}
				inputDelay = Global.inputReadDelayTimer * 2;
			}
			if (Global.getControlActive(Global.GameControl.DOWN)) {
				//pauseCursorPos = pauseCursorPos == 0 ? 1 : 0;
				pauseCursorPos++;
				if (pauseCursorPos > 1) {
						pauseCursorPos = 0;
				}
				inputDelay = Global.inputReadDelayTimer * 2;
			}
			if (Global.getControlActive(Global.GameControl.SELECT)) {
				switch (pauseCursorPos) {
					case 0:
						//resetVariables();
						buildGrid(level);
						gameOver = false;
						remainClears = -1;
						score = 0;
						//levelFinished = false;
						inputDelay = 10 * Global.inputReadDelayTimer;	
						break;
					case 1:
						gameOver = true;
						levelFinished = true;
						inputDelay = 10 * Global.inputReadDelayTimer;
						break;
				}
			}
		} else if (inputDelay > 0) {
			inputDelay -= Global.delta;
		}
	}
	
	/** @author Mario */
	@Override
	protected void levelFinishedControls() {
		if (inputDelay <= 0) {
			if (Global.getControlActive(Global.GameControl.UP)) {
				pauseCursorPos--;
				if (pauseCursorPos < 0) {
					pauseCursorPos = 2;
				}
				inputDelay = Global.inputReadDelayTimer * 2;
			}
			if (Global.getControlActive(Global.GameControl.DOWN)) {
				pauseCursorPos++;
				if (pauseCursorPos > 2) {
					pauseCursorPos = 0;
				}
				inputDelay = Global.inputReadDelayTimer * 2;
			}
			if (Global.getControlActive(Global.GameControl.SELECT)) {
				switch (pauseCursorPos) {
					case 0:
						levelFinished = true;
						gameOver = false;
						score = 0;
						inputDelay = 10 * Global.inputReadDelayTimer;	
						break;
	
					case 1:
						levelFinished = false;
						gameOver = false;
						levelComplete = false;
						buildGrid(level);
						remainClears = -1;
						levelMedal = 0;
						score = 0;
						energy = energyMax;
						cursorGridPos[0] = grid.length / 2;
						cursorGridPos[1] = grid[0].blocks.length / 2;
						movesUpdateDelay = movesUpdateDelayTimer * 2;
						//gameOver = true;
						//levelFinished = true;
						inputDelay = 10 * Global.inputReadDelayTimer;	
						break;
					case 2:
						gameOver = true;
						levelFinished = true;
						buildGrid(level);
						remainClears = -1;
						score = 0;
						//buildGrid(level);
						inputDelay = 10 * Global.inputReadDelayTimer;
				}
			}
		} else if (inputDelay > 0) {
			inputDelay -= Global.delta;
		}
	}

	/** Checks against movement inputs within the grid, and adjusts the cursor
	 * position accordingly.
	 * @author John
	 */
	@Override
	protected void checkCommonControls() {
		if (Global.getControlActive(Global.GameControl.PAUSE)) {
			pauseCursorPos = 0;
			gamePaused = true;
			inputDelay = 1000l;
		}  else {
			//queueHold = false;
			// cursor control
			if (Global.getControlActive(Global.GameControl.UP)) {
				cursorGridPos[1]++;
				if (cursorGridPos[1] >= grid[0].blocks.length) {
					cursorGridPos[1] = grid[0].blocks.length - 1;
				}
				inputDelay = Global.inputReadDelayTimer;
			} else
			if (Global.getControlActive(Global.GameControl.DOWN)) {
				if (cursorGridPos[1] > 0) {
					cursorGridPos[1]--;
				}
				inputDelay = Global.inputReadDelayTimer;
			} 
			if (Global.getControlActive(Global.GameControl.LEFT)) {
				if (cursorGridPos[0] > 0) {
					cursorGridPos[0]--;
				}
				inputDelay = Global.inputReadDelayTimer;
			} else
			if (Global.getControlActive(Global.GameControl.RIGHT)) {
				cursorGridPos[0]++;
				if (cursorGridPos[0] >= grid.length) {
					cursorGridPos[0] = grid.length - 1;
				}
				inputDelay = Global.inputReadDelayTimer;
			}
			if (actionDelay <= 0) {
				if ((!blocksMoving || !Global.waitForGridMovement) &&
						Global.getControlActive(Global.GameControl.SELECT) &&
						grid[cursorGridPos[0]].blocks[cursorGridPos[1]] != null) {
					int counter = processActivate();
					if (counter > 1 || grid[cursorGridPos[0]].blocks[cursorGridPos[1]].type == Block.BlockType.BOMB || 
							grid[cursorGridPos[0]].blocks[cursorGridPos[1]].type == Block.BlockType.STAR) {
						// decrease the blocksRemaining counter after blocks are cleared
						remainClears--;
						removeMarkedBlocks();
						dropNewBlocks();
					}
					if (actionDelay < Global.inputReadDelayTimer) {
						actionDelay = Global.inputReadDelayTimer;
					}
				}
			}
		}
	}

	/**
	 * @author John
	 */
	//@Override
	protected int processActivate() {
		int counter = 0;
		switch (grid[cursorGridPos[0]].blocks[cursorGridPos[1]].type) {
			case BLOCK:
				counter = checkGrid(cursorGridPos);
				int adj = (int)Math.pow(counter - 1, 2);
				updateScore(adj);
				//addEnergy(adj);
				break;
			case BOMB:
				counter = activateBombBlock(cursorGridPos);
				updateScore(counter);
				//addEnergy(counter);
				break;
			case HEART:
				heartSpecialActive = true;
				actionDelay = Global.inputReadDelayTimer * 3;
				//addEnergy(energyMax / 10); // regenerate 10% of max energy on use
				break;
			case STAR:
				counter = activateStarBlock(cursorGridPos, false);
				if (counter > 0) {
					actionDelay = Global.inputReadDelayTimer;
					updateScore(50);
				}
				break;
			default: // block does not activate, do nothing
				return 0;
		}
		//remainClears--;
		return counter;
	}


	/**
	 * @author Mario
	 */
	protected void drawEnergy() {
		float percent;
		emptyEnergy.draw(20, 740);
		energyBar.bind();
		if (energy > energyDisplay) {
			energyDisplay += Global.delta * 100;
		} else if (energy == 0 && energyDisplay > 0) {
			energyDisplay -= Global.delta * 100;
		} else {
			energyDisplay = energy;
		}
		
		if (energy == 0) {
			percent = 0;
		} else {
			percent = (float) energyDisplay/(float) energyMax;
		}
		glPushMatrix();
		glTranslatef(20,740,0); // x y z
		glBegin(GL_QUADS);
		{
			glTexCoord2f(0,0);
			glVertex2i(0,0);
			
			glTexCoord2f(percent,0);
			glVertex2i((int) (percent*640),0);
			
			glTexCoord2f(percent,1);
			glVertex2i((int)(percent*640),32);
			
			glTexCoord2f(0,1);
			glVertex2i(0,32);
		}
		glEnd();
		glPopMatrix();
	}


}
