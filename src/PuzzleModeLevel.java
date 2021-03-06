import static org.lwjgl.opengl.GL11.*;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.Color;
/**
 * This class serves as the base class for all Block Breaker Puzzle Mode levels,
 * and defines and abstracts many of the functions that many level design simpler.
 * @author Brock Simpson
 */
public abstract class PuzzleModeLevel {
	protected static Sprite[] numbers = new Sprite[10]; 
	protected static int score;
	protected static Sprite pauseCursor;
	protected static Sprite cursor;
	protected static Sprite[] shiftLR = new Sprite[2];
	protected static Sprite nLevel;
	protected static Sprite overlay;
	
	private static int scoreDisplay = 0;
	private static int change = 0;
	private static int movesChange = 0;
	private static long scoreUpdateDelayTimer = 50l;
	private static long scoreUpdateDelay = scoreUpdateDelayTimer;
	private static long movesUpdateDelayTimer = 50l;
	private static long movesUpdateDelay = movesUpdateDelayTimer;

	protected Sprite levelDisplay;
	protected Sprite background;
	protected Sprite userInterface;
	protected static Sprite emptyEnergy; //empty energy bar
	protected static Texture energyBar; //energy bar
	protected int energyMax = 100000;
	protected int energy = energyMax;
	protected float energyGainMultiplier = 1.0f;
	private int energyDisplay = energyMax;

	// grid variables
	protected GridColumn[] grid;
	protected int[] gridSize;
	protected int[] gridBasePos;
	// grid shifting variables
	private boolean gridShiftActive = false;
	private boolean blockDropActive = false;
	private boolean gridMoving = false;
	protected int gridShiftDir = 1;
	private long shiftActionDelayTimer = 1000l;
	private long gridShiftActionDelay = shiftActionDelayTimer;
	// grid queue variables
	protected Block[] queue;
	/** Sets the timer between each queue shift. Lower values will cause the queue to move faster. */
	private long queueStepDelayTimer = 500l;
	private long queueStepDelay = queueStepDelayTimer;
	private int queueStepReq = 4;
	private int queueStepCount = 0;
	private int queueCount = 0;
	private int queueLimit = 5;
	private final long queueManualShiftDelayTimer = 250l;
	private long queueManualShiftDelay = queueManualShiftDelayTimer;
	private boolean queueHold = false;
	
	private boolean heartSpecialActive = false;
	protected int[] cursorGridPos = new int[] { 0, 0 };
	protected int heartCursorPos = 0;
	protected int[] blockSize;
	protected int blocksRemaining = 0;
	
	protected static int totalClears = 0;
	protected static int remainClears = -1;//totalClears;
	private static int movesDisplay = 0;
	protected boolean resetMoves = false;
	protected boolean noRemainClears = false;
	
	private boolean gamePaused = false;
	private long inputDelay = Global.inputReadDelayTimer * 2;
	private long actionDelay = Global.inputReadDelayTimer * 2;
	protected String levelTitle; 
	protected int level = 1;
	/** Sets sets the multiplier to apply to all score additions/subtractions. */
	protected float levelMultiplier = 1.0f;
	/** Used to contain removed block counts for grid clears. */
	protected int counter = 0;

	/** Indicates if the level is over and should no longer be called. 
	 * Set to <code>true</code> to advance the level or return to the game mode screen. */
	protected boolean levelFinished = false;
	/** Indicates if the finished level was completed successfully. 
	 * A <code>true</code> value will not allow the next level to load. */
	protected boolean gameOver = false;
	/** Indicates if the game play for the level is completed. If <code>true</code>
	 * the level is over, but the game is not ready to advance to the next level. */
	protected boolean levelComplete = false;
	/** Indicates whether or not the level is a practice level. Levels played in practice mode will not
	 * cause further level advancement or allow for high score recording when competed. */
	protected boolean practice = false;
	
	/** Indicates whether or not the input has been delayed for end of level detection. */
	private boolean endLevelDelayed = false;
	
	protected int colorID;
	protected boolean specialMenu;
	protected boolean clearColor;
	protected Sprite heartCursor;
	protected int colorCount = 0;
	
	/**
	 * Game control variables
	 */
	protected boolean clearMoves = false;
	protected boolean clearCertColor = false;
	
	protected static int nLevels = 2;
	protected static int[] medals = new int[nLevels + 1];
	protected int levelMedal = 0;
	protected int oldMedal = 0;
	
	protected boolean noMoves = false;
	protected static int sumMoves = 0;
	protected String[] clearsString = new String[] {
		"0",
		"1",
		"2",
		"3",
		"4",
		"5",
		"6"
	};
	
	protected static boolean standCond = true;
	protected static boolean specCond = false;
	
	/*private Sprite optionFrameMid = new Sprite(
			Global.textureMap.get("blue_ui"),
			new int[] { 0, 59 },
			new int[] { 190, 20 },
			new int[] { 250, 250 }
		);
	private Sprite hoverBox = new Sprite(
			Global.textureMap.get("green_ui"),
			new int[] { 0, 144 },
			new int[] { 190, 48 },
			new int[] { 190, 48 }
		);*/
	
	protected int pauseCursorPos = 0;
	
	/*private Sprite pauseBox = new Sprite(
			Global.textureMap.get("green_ui"),
			new int[] { 0, 0 },
			new int[] { 190, 48 },
			new int[] { 180, 38 }
		);*/
	
	//private Sprite[] pauseText = new Sprite[2];
	
	/*private Sprite pause_RE_sel = new Sprite(
			Global.textureMap.get("pause_text"),
			new int[] { 180, 0 },
			new int[] { 180, 26 },
			new int[] { 180, 38 }
		);
	private Sprite pause_RE_unsel = new Sprite(
			Global.textureMap.get("pause_text"),
			new int[] { 0, 0 },
			new int[] { 180, 26 },
			new int[] { 180, 38 }
		);
	private Sprite pause_EX_sel = new Sprite(
			Global.textureMap.get("pause_text"),
			new int[] { 180, 26 },
			new int[] { 180, 26 },
			new int[] { 180, 38 }
		);
	private Sprite pause_EX_unsel = new Sprite(
			Global.textureMap.get("pause_text"),
			new int[] { 0, 26 },
			new int[] { 180, 26 },
			new int[] { 180, 38 }
		);*/
	
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
	
	private Block[] heartMenuBlocks = new Block[6];

	
	/**
	 * Scoring System variables
	 * 
	 */
	protected static boolean useScore = false;
	protected static boolean useTime = false;
	protected static int scoreMedal1 = 2500;
	protected static int scoreMedal2 = 5000;
	protected static int scoreMedal3 = 25000;
	/*public PuzzleModeLevel(HashMap<String,Texture> rootTexMap) {
		level = 1;
		// TODO: [CUSTOM] set background and user interface sprites
		// if these sprite must be defined or the game will crash at runtime
		background = new Sprite(
				rootTexMap.get("bg_space_1"),
				new int[] { 0, 0 },
				new int[] { 1024, 768 },
				new int[] { Global.glEnvWidth, Global.glEnvHeight }
			);
		
		userInterface = new Sprite(
				rootTexMap.get("ui_stdmode"), // default interface texture
				new int[] { 0, 0 },
				new int[] { 1024, 768 },
				new int[] { Global.glEnvWidth, Global.glEnvHeight }
			);
		// TODO: [CUSTOM] set the score multiplier for the level
		// default value: 1.0f
		levelMultiplier = 1.5f;
		
		// TODO: [CUSTOM] set the energy gain multiplier. default value is 1.0f
		energyGainMultiplier = 1.0f;
		
		// TODO: [CUSTOM] set the block size and grid size
		// valid block dimensions are { 16, 32, 64 }
		// valid grid sizes (respective to block size) are { 40, 20, 10 }
		blockSize = new int[] { 32, 32 }; // default block size is { 32, 32 }
		gridSize = new int[] { 20, 20 }; // default grid size is { 20, 20 }
		// create the grid with x-dimension as specified above
		grid = new GridColumn[gridSize[0]];
		queue = new Block[gridSize[0]];
		// build the grid according the level difficulty
		buildGrid();
		// set the grid draw starting position derived from grid and block size
		gridBasePos = new int[] { 20, Global.glEnvHeight - blockSize[1] - 50 };
		// set the cursor starting position in the center of the grid
		cursorGridPos[0] = grid.length / 2;
		cursorGridPos[1] = grid[0].blocks.length / 2;
		// set energy max if not default
		energy = energyMax = 200000;	
	}*/
	
	//@Override
	/*protected void buildGrid() {
		int r = 0;
		Global.rand.setSeed(LocalDateTime.now().getNano());
		for (int i = 0; i < grid.length; i++) {
			grid[i] = new GridColumn(gridSize[1]);
			for (int k = 0; k < grid[0].blocks.length; k++) {
				// TODO: [CUSTOM] define the randomly generated blocks rate of appearance
				r = Global.rand.nextInt(2);
				grid[i].blocks[k] = new Block(Block.BlockType.BLOCK, r);
			}
		}
		// set the block count for the level
		blocksRemaining = grid.length * grid[0].blocks.length;
		// TODO: [CUSTOM] add any custom/special blocks that have limited generation (rocks, trash, wedge, etc.)
		// remember to decrease blocksRemaining for each such block added
		//grid[4].blocks[Global.rand.nextInt(20)] = new Block(Block.BlockType.HEART);
		//grid[16].blocks[Global.rand.nextInt(20)] = new Block(Block.BlockType.HEART);
	}*/

	//@Override
	protected Block getQueueBlock() {
		Block b = null;
		// TODO: [CUSTOM] define the type and rate of blocks that are added to the grid via the queue
		b = new Block(Block.BlockType.BLOCK, Global.rand.nextInt(2));

		return b;		
	}

	
/*	public void run() {
		// decrement delay variables
		queueManualShiftDelay -= Global.delta;
		gridShiftActionDelay -= Global.delta;
		actionDelay -= Global.delta;
		inputDelay -= Global.delta;
		// Draw all background elements. These should always be the first items drawn to screen. 
		background.draw(0, 0);
		counter = 0;
		
		if (blocksRemaining == 0) {
			levelComplete = true;
			if (!endLevelDelayed) {
				endLevelDelayed = true;
				inputDelay = Global.inputReadDelayTimer * 2;
			}
		} else if (energy == 0) {
			// game over
			gameOver = true;
		}
		
	}//*/

	
	public void run() {
		// decrement delay variables
		queueManualShiftDelay -= Global.delta;
		gridShiftActionDelay -= Global.delta;
		actionDelay -= Global.delta;
		inputDelay -= Global.delta;
		/* Draw all background elements. These should always be the first items drawn to screen. */
		background.draw(0, 0);
		counter = 0;
		//remainClears = totalClears;
		//drawTopLevelUI();
		
		//oldMedal = medals[level];

		
		/**
		 * funtion for ending conditions
		 */
		//if (!gridMoving || !Global.waitForGridMovement) {
			if (blocksRemaining > 0 && remainClears > 0) {
				// If not out of clears but no moves left, then game over
				int xMax = grid.length - 1;
				int yMax = grid[0].blocks.length - 1;
				sumMoves = 0;
				for (int i = 0; i < xMax; i++) {
					for (int j = 0; j < yMax; j++) {
						if (grid[i].blocks[j] == null) {
							continue;
						} else {
							//sumMoves += checkGridMovesRemain(i, j, grid, grid[i].blocks[j].colorID);
							sumMoves += checkGrid(i, j);
						}
					}				
				}
				//sumMoves = 0;
				if ( sumMoves <= 1  && movesUpdateDelay == 0 ) {
					noMoves = true;
					gameOver = true;
					pauseCursorPos = 0;
				} else if (sumMoves >= 2  && movesUpdateDelay == 0) {
					sumMoves = 0;
				}
				
				/*else if (sumMoves > 1) {
					sumMoves = 0;
				}*/
	
				//sumMoves = 0;
	 
	
			} 
	//	}

		if (blocksRemaining == 1 && movesUpdateDelay == 0 && remainClears > 0) {
	        // game over with one block remaining
			noMoves = true;
			gameOver = true;
			pauseCursorPos = 0;
		}
		endingConditions();
		/*if (blocksRemaining == 0 && (remainClears >= 0)&& movesUpdateDelay == 0) {
			levelComplete = true;
			if (!endLevelDelayed) {
				endLevelDelayed = true;
				pauseCursorPos = 0;
				score += remainClears >> 6;
				
				scoringSystem();

				energy = 0;
				pauseCursorPos = 0;
				inputDelay = Global.inputReadDelayTimer * 2;
			}
			pauseCursorPos = 0;
		} else if (blocksRemaining > 0 && remainClears == 0 && movesUpdateDelay == 0) {
			// game over
			//if (blockDropDelay == 0) {
				noRemainClears = true;
				gameOver = true;
				pauseCursorPos = 0;
				
				//inputDelay = Global.inputReadDelayTimer;
			//}
			
		} //else if (noMoves && movesUpdateDelay == 0 && !blocksMoving){
			// Game over is no moves are remaining
			//	noMoves = true;
			//	gameOver = true;
			//	pauseCursorPos = 0;
			
		//}
		else if (blocksRemaining > 0 && remainClears > 0 && !blocksMoving && movesUpdateDelay == 0) {
			// If not out of clears but no moves left, then game over
			int xMax = grid.length - 1;
			int yMax = grid[0].blocks.length - 1;
			sumMoves = 0;
			for (int i = 0; i < xMax; i++) {
				for (int j = 0; j < yMax; j++) {
					if (grid[i].blocks[j] == null) {
						continue;
					} else {
						sumMoves += checkGridMovesRemain(i, j, grid[i].blocks[j].colorID);		
					}
				}				
			}
			//sumMoves = 0;
			if ( sumMoves == 0 ) {
				noMoves = true;
				gameOver = true;
				pauseCursorPos = 0;
			}

			sumMoves = 0;
 

		} else if (blocksRemaining == 1 && movesUpdateDelay == 0 && remainClears > 0 && !blocksMoving) {
	        // game over with one block remaining
			noMoves = true;
			gameOver = true;
			pauseCursorPos = 0;
		}*/
		// draw the grid and handle grid mechanics and input if the game is not paused
		if (!gamePaused && !gameOver && !levelComplete) {
			//processQueue();

			energy -= Global.delta;
			if (energy < 0) { energy = 0; }
			if (energy > energyMax) { energy = energyMax; }
			// draw the grid, return value indicates if there are blocks still falling from the last clear
			processGridBlocks(grid);
			this.drawGridRework(grid);
			gridMoving = blocksMoving;
			//drawGrid(500);
			//if (movesUpdateDelay == 0) {
			
			//}

			// for cursor surrounding block
			cursor.draw(
				gridBasePos[0] + blockSize[0] * cursorGridPos[0],
				gridBasePos[1] - blockSize[1] * cursorGridPos[1],
				blockSize
			);
			
			// check if heart special control is active and handle accordingly
			if (heartSpecialActive) {
				/**
				 * @author Brock
				 */
				DrawHeartSelector(); 
				heartMenuControls();
				//inputDelay = Global.inputReadDelayTimer * 2;
				if (clearColor) {
					counter = activateHeartBlock(cursorGridPos);
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
					//blocksMoving = false;
					checkCommonControls();
					// DEBUG: back out of the game to the main menu. not to be included in finished levels
					if (Global.getControlActive(Global.GameControl.CANCEL)) {
						levelFinished = true;
						gameOver = true;
						//remainClears = -1;
					}
				}
			}
		}
		// draw the top-level UI frame, score and other elements
		drawTopLevelUI();
		//if (useTime) {
		//	drawEnergy();
		//}
		//drawEnergy();
	}

	/**
	 * Checks the grid for blocks of the same color sharing edges, and marks those blocks
	 * for removal.
	 * @param grid The 2-dimensional grid of blocks
	 * @param xy 2-element array containing the starting index locations for the search
	 * @return The total number of blocks found
	 * @author John
	 */
	protected final int checkGrid(int[] xy) {
		if (grid[xy[0]].blocks[xy[1]] == null) { return 0; }
		if (grid[xy[0]].blocks[xy[1]].type != Block.BlockType.BLOCK) { return 0; }
		return checkGrid(xy[0], xy[1], grid[xy[0]].blocks[xy[1]].colorID);
	}
	
	protected final int checkGrid(int xc, int yc ) {
		if (grid[xc].blocks[yc] == null) { return 0; }
		if (grid[xc].blocks[yc].type == Block.BlockType.BOMB)  {return 2; }
		if (grid[xc].blocks[yc].type == Block.BlockType.STAR)  {return 2; }
		if (grid[xc].blocks[yc].type == Block.BlockType.HEART)  {return 2; }
		if (grid[xc].blocks[yc].type != Block.BlockType.BLOCK) { return 0; }
		return checkGrid(xc, yc, grid[xc].blocks[yc].colorID);
	}
	
	protected final int checkGridMovesRemain(int xc, int yc, GridColumn[] grid, int colorID) {
		int sum = 0;
		
		//GridColumn gc = grid[x];
		
		if (xc > 0 && grid[xc-1].blocks[yc] != null && grid[xc-1].blocks[yc].colorID == colorID) {
			sum++;
		}
		if (yc > 0 && grid[xc].blocks[yc-1] != null && grid[xc].blocks[yc-1].colorID == colorID) {
			sum++;
		}
		if ( (xc + 1) < grid.length && grid[xc+1].blocks[yc] != null && grid[xc+1].blocks[yc].colorID == colorID) {
			sum++;
		}
		if ( (yc + 1) < grid[0].blocks.length && grid[xc].blocks[yc+1] != null && grid[xc].blocks[yc+1].colorID == colorID) {
			sum++;
		}
		
		/*if (xc > 0 && grid[xc-1].blocks[yc] != null && grid[xc-1].blocks[yc].colorID == colorID) {
			sum ++;
		}
		if (yc > 0 && grid[xc].blocks[yc-1] != null && grid[xc].blocks[yc-1].colorID == colorID) {
			sum ++;
		}
		if ( (xc + 1) < grid.length && grid[xc+1].blocks[yc] != null && grid[xc+1].blocks[yc].colorID == colorID) {
			sum ++;
		}
		if ( (yc + 1) < grid[0].blocks.length && grid[xc].blocks[yc+1] != null && grid[xc].blocks[yc+1].colorID == colorID) {
			sum ++;
		}*/

		return sum;
	}
	
	/**
	 * @author Brock
	 * Function for the scoring system
	 */
	protected void scoringSystem() {
		if (useScore) {
			if (score <= scoreMedal1) {
				//medals[level] = 1;
				levelMedal = 1;
			} else if (score > scoreMedal1 && score <= scoreMedal2) {
				//medals[level] = 2;
				levelMedal = 2;
			} else if (score >= scoreMedal2) {
				//medals[level] = 3;
				levelMedal = 3;
			}
			
		}
		if (useTime) {
			if (energy > 0 && levelMedal == 3) {
				//medals[level] = 4;
				levelMedal = 4;
			}
		}/* else if (useScore && useTime) {
			if (score <= scoreMedal1 && medals[level] < 3) {
				medals[level] = 1;
			} else if (score > scoreMedal1 && score <= scoreMedal2 && medals[level] < 3) {
				medals[level] = 2;
			} else if (score > scoreMedal2) {
				medals[level] = 3;
			}
			if (energy > 0) {
				medals[level] = 4;
			}
		}*/

	}
	
	/**
	 * @author Brock
	 */
	protected void endingConditions() {
		if (standCond) {
			//if (movesUpdateDelay == 0) {
				if (blocksRemaining == 0 && remainClears > 0 && movesUpdateDelay == 0) {
					levelComplete = true;
					if (!endLevelDelayed) {
						
						endLevelDelayed = true;
						pauseCursorPos = 0;
						score += remainClears >> 6;
						
	
						
						scoringSystem();
						if (levelMedal >= medals[level]) {
							medals[level] = levelMedal;
						}
						//medals[level] = (levelMedal >= medals[level]) ? levelMedal : oldMedal;
	
						//energy = 0;
						pauseCursorPos = 0;
						inputDelay = Global.inputReadDelayTimer * 2;
					}
					pauseCursorPos = 0;
				} 
				if (blocksRemaining > 0 && remainClears == 0 && movesUpdateDelay == 0) {
					// game over
					//if (blockDropDelay == 0) {
						noRemainClears = true;
						gameOver = true;
						pauseCursorPos = 0;
						
						//inputDelay = Global.inputReadDelayTimer;
					//}
					
					/**
					 * @author Brock
					 */
				} /*else if (noMoves && movesUpdateDelay == 0 && !blocksMoving){
					// Game over is no moves are remaining
						noMoves = true;
						gameOver = true;
						pauseCursorPos = 0;
					
				}*/
			//}
		} else if (specCond) {

			
		}
		

	}
	
	/**
	 * fuction to reset all variables that need to be reset
	 */
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
			buildGrid();
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
	 * @param xc - x coordinate
	 * @param yc - y coordinate
	 * @param colorID - id for color of block
	 * @return sum
	 */
	private final int checkGridMovesRemain(int xc, int yc, final int colorID) {
		int sum = 0;
		
		if (xc > 0 && grid[xc-1].blocks[yc] != null && grid[xc-1].blocks[yc].colorID == colorID) {
			sum ++;
		}
		if (yc > 0 && grid[xc].blocks[yc-1] != null && grid[xc].blocks[yc-1].colorID == colorID) {
			sum ++;
		}
		if ( (xc + 1) < grid.length && grid[xc+1].blocks[yc] != null && grid[xc+1].blocks[yc].colorID == colorID) {
			sum ++;
		}
		if ( (yc + 1) < grid[0].blocks.length && grid[xc].blocks[yc+1] != null && grid[xc].blocks[yc+1].colorID == colorID) {
			sum ++;
		}
		/*else {
			sum = 0;
		}*/
		return sum;
	}

	/**
	 * @author John 
	 */
	private final int checkGrid(int xc, int yc, final int colorID) {
		int sum = 1;
		if (grid[xc].blocks[yc] == null || grid[xc].blocks[yc].checked) {
			
			return 0;
		}
		grid[xc].blocks[yc].checked = true;
		if (grid[xc].blocks[yc].colorID != colorID) {
			
			return 0;
		}
		if (grid[xc].blocks[yc].type != Block.BlockType.BLOCK) {
			
			return 0;
		}
		grid[xc].blocks[yc].clearMark = true;
		
		sum = 1;
		if (xc > 0) {
			sum += checkGrid(xc - 1, yc, colorID);
		}
		if (yc > 0) {
			sum += checkGrid(xc, yc - 1, colorID);
		}
		if ( (xc + 1) < grid.length) {
			sum += checkGrid(xc + 1, yc, colorID);
		}
		if ( (yc + 1) < grid[0].blocks.length) {
			sum += checkGrid(xc, yc + 1, colorID);
		}
		return sum;
	}

	/**
	 * @author John
	 */
	protected void drawScore() {
		if (score > 0) {
			scoreUpdateDelay -= Global.delta;
			if (scoreUpdateDelay <= 0 && score != scoreDisplay) {
				if (scoreDisplay < score) { // most common case, score is increasing
					change = (score - scoreDisplay) >> 2;
					if (change == 0) { change = 4; }
					scoreDisplay += change;
					if (scoreDisplay > score) { scoreDisplay = score; }
				} else { // score decreasing
					change = (scoreDisplay - score) >> 2;
					if (change == 0) { change = 4; }
					scoreDisplay -= change;
					if (scoreDisplay < score) { scoreDisplay = score; }
				}
				scoreUpdateDelay = scoreUpdateDelayTimer;
			}
		} else { 
			score = 0;
			scoreDisplay = score; 
		}
		char[] strScore = Integer.toString(scoreDisplay).toCharArray();
		int offsetX = 948;
		int yPos = 125;
		for (int i = strScore.length - 1; i >= 0; i--) {
			getNumber(strScore[i]).draw(offsetX, yPos);
			offsetX -= 24;
		}
		for (int i = strScore.length; i < 11; i++) {
			numbers[0].draw(offsetX, yPos);
			offsetX -= 24;
		}
	}
	
	/**
	 * @author John 
	 */
	private Sprite getNumber(char c) {
		switch (c) {
			case '0':
				return numbers[0];
			case '1':
				return numbers[1];
			case '2':
				return numbers[2];
			case '3':
				return numbers[3];
			case '4':
				return numbers[4];
			case '5':
				return numbers[5];
			case '6':
				return numbers[6];
			case '7':
				return numbers[7];
			case '8':
				return numbers[8];
			case '9':
			default:
				return numbers[9];
		}
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
			//	remainClears = totalClears;
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
				movesUpdateDelay = movesUpdateDelayTimer * 2;//Global.inputReadDelayTimer;//movesUpdateDelayTimer;
			} else if (movesUpdateDelay > 0) {
				movesUpdateDelay -= Global.delta;
			}
			//	movesUpdateDelay -= Global.delta;
			//}

				//else {
			//movesUpdateDelay -= Global.delta;
			//	inputDelay -= Global.delta;
			//}
		} 
		else {
			remainClears = totalClears;
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
	protected void drawTopLevelUI() {
		Global.uiTransWhite.draw(700, 16, 300, 56);
		Color.green.bind();
		Global.uiTransWhite.draw(700, 72, 300, 96);
		Color.white.bind();
		userInterface.draw(0,0);
		//remainClears = 6;
		Global.drawFont48(710, 25, levelTitle, Color.white);
		Global.drawFont48(710, 80, "Score", Color.white);
		Global.drawFont48(730, 400, "Clears left", Color.white);
		Global.uiGreen.draw(800, 445, 80, 44);
		//Global.drawFont48(710, 450, clearsString[remainClears], Color.white);
		//numbers[remainClears].draw(710, 450);
		int offsetX = 860;
		int yPos = 16;
		int[] numResize = new int[] { 30, 40 };
		drawScore();
		drawMovesRemain();
		Global.uiGreen.draw(680, 500, 100, 100);
		if (gridShiftDir == 1) {
			shiftLR[1].draw(680, 500);
		} else {
			shiftLR[0].draw(680, 500);
		}
		if (useTime) {
			drawEnergy();
		}

		if (levelComplete) {
			//drawGrid();
			// TODO: level complete code
			//overlay.draw(0, 0);
			//nLevel.draw(200, 200);

			levelFinishedControls();
			/*optionFrameMid.draw(412,250); //180 250
			if (pauseCursorPos == 0) {
				pauseBox.draw(457, 372);
				//hoverBox.draw(210, 310);
				pauseBox.draw(457, 312);
				Global.drawFont24(488, 312, "Next Level", Color.white);
				Global.drawFont24(512, 372, "Quit", Color.black);
			}
			
			if (pauseCursorPos == 1) {
				//hoverBox.draw(210, 370);
				pauseBox.draw(457, 372);
				pauseBox.draw(457, 312);
				Global.drawFont24(512, 372, "Quit", Color.white);
				Global.drawFont24(488, 312, "Next Level", Color.black);
			}*/
			overlay.draw(0, 0);
			//gameOverControls();
			for (int i = 0; i < levelCompleteOptions.length; i++) {
				levelCompleteOptionSize[i] = Global.getFont24DrawSize(levelCompleteOptions[i]) / 2;
			}
			//Color.lightGray.bind();
			Color.white.bind();
			//Color.transparent.bind();
			//Global.uiWhite.draw(180, 200, 512, 384);
			Global.uiWhite.draw(180, 280, 512, 250);
			
			for (int i = 0; i < levelCompleteOptions.length; i++) {
				//Color.blue.bind();
				//Global.uiWhite.draw(288, 224, 192, 48); // left button
				//Global.uiWhite.draw(546, 224, 192, 48); // right button
				//Global.uiWhite.draw(425, 480, 192, 48); // bottom button
				Global.menuButtonShader.bind();
				Global.uiTransWhite.draw(212, 305 + i * 70, 190, 48);
				//Color.white.bind();
				//Global.uiWhite.draw(288, 288, 452, 192);
				//Global.uiWhite.draw(288, 288, 452, 170);
	
				if (pauseCursorPos == i) {
					//Color.transparent.bind();
					//Color..bind();
					//Global.uiTransWhite.draw(212, 305 + i * 70, 190, 48);
					//Global.uiGreen.draw(212, 305 + i * 70, 190, 48);
					Global.drawFont24(305 - levelCompleteOptionSize[i], 319 + i * 70, levelCompleteOptions[i], Color.white);
					Color.white.bind();
				} else {
					Global.drawFont24(305 - levelCompleteOptionSize[i], 319 + i * 70, levelCompleteOptions[i], Color.black);
				}
					/*if (pauseCursorPos == 0) {
						Global.drawFont24(330, 240, "Next Level", Color.white);
						Global.drawFont24(425, 480, "Restart", Color.black);
						Global.drawFont24(618, 240, "Quit", Color.black);
						Global.drawFont24(415, 380, "Congrats", Color.black);
						//Global.drawFont24(308, 350, "your current score.",Color.black);
					}
					if (pauseCursorPos == 1) {
						Global.drawFont24(330, 240, "NextLevel", Color.black);
						Global.drawFont24(425, 480, "Restart", Color.white);
						Global.drawFont24(618, 240, "Quit", Color.black);
						Global.drawFont24(440, 380, "Quit the level.", Color.black);
					}
					if (pauseCursorPos == 2) {
						Global.drawFont24(330, 240, "NextLevel", Color.black);
						Global.drawFont24(425, 480, "Restart", Color.black);
						Global.drawFont24(618, 240, "Quit", Color.white);
						Global.drawFont24(440, 380, "Quit the level.", Color.black);
					}*/
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
						BlockPuzzleMode.Challenge_star.draw(BlockPuzzleMode.medalOffset * j + 425, 415);
					} else {
						BlockPuzzleMode.Yellow_star.draw(BlockPuzzleMode.medalOffset * j + 425, 415);
					}
					//medalOffset -= 5;
				}
			} //else {
				
			//}
			//if (actionDelay < 0 && Global.getControlActive(Global.GameControl.SELECT)) {
			//	levelFinished = true;
			//}
			// placeholder for level advancement
		} else if (gamePaused) {
            /**
             *  @author Brock
             */
			pauseControls();

			overlay.draw(0, 0);
			//optionFrameMid.draw(180, 250);
			//Global.menuButtonShader.bind();
			//Global.uiTransWhite.draw(180, 280, 250, 250);
			Global.uiWhite.draw(180, 280, 250, 250);
			for (int i = 0; i < pauseOptions.length; i++) {
				pauseOptionSize[i] = Global.getFont24DrawSize(pauseOptions[i]) / 2;
			}
			
			for (int i = 0; i < pauseOptions.length; i++ ) {
				
				/*if (pauseCursorPos == i) {
					//hoverBox.draw(210, 310 + i * 70);
					//pauseBox.draw(215, 312 + i * 70);	
				} else {
					//pauseBox.draw(215, 312 + i * 70);
				}*/
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
			drawGrid();
			showGameOver();
		}
	}
	
	private String goText = "Game Over";
	private String goTextPrac = "Practice Over";
	private int goTextSize;
	private int goTextPracSize;

	/**
	 * @author Brock
	 */
	private void showGameOver() {
		gameOverControls();
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
				
			/*} else if (noRemainClears) {
				if (pauseCursorPos == i) {
					Global.drawFont24(305 - gameOverOptionsSize[i], 355 + i * 70, gameOverOptions[i], Color.white);
					switch (pauseCursorPos) {
						case 0:
							Global.drawFont24(500, 389, "No remaining clears!", Color.black);
							break;
						case 1:
							Global.drawFont24(500, 389, "Try Again?", Color.black);
							break;
						default:
								break;
					}
				} else {
					Global.drawFont24(305 - gameOverOptionsSize[i], 355 + i * 70, gameOverOptions[i], Color.black);
				}
				
			} *//*else {
				if (pauseCursorPos == i) {
					Global.drawFont24(305 - gameOverOptionsSize[i], 355 + i * 70, gameOverOptions[i], Color.white);
				} else {
					Global.drawFont24(305 - gameOverOptionsSize[i], 355 + i * 70, gameOverOptions[i], Color.black);
				}
			}*/


				/*if (pauseCursorPos == 0) {
					Global.drawFont24(330, 240, "Next Level", Color.white);
					Global.drawFont24(425, 480, "Restart", Color.black);
					Global.drawFont24(618, 240, "Quit", Color.black);
					Global.drawFont24(415, 380, "Congrats", Color.black);
					//Global.drawFont24(308, 350, "your current score.",Color.black);
				}
				if (pauseCursorPos == 1) {
					Global.drawFont24(330, 240, "NextLevel", Color.black);
					Global.drawFont24(425, 480, "Restart", Color.white);
					Global.drawFont24(618, 240, "Quit", Color.black);
					Global.drawFont24(440, 380, "Quit the level.", Color.black);
				}
				if (pauseCursorPos == 2) {
					Global.drawFont24(330, 240, "NextLevel", Color.black);
					Global.drawFont24(425, 480, "Restart", Color.black);
					Global.drawFont24(618, 240, "Quit", Color.white);
					Global.drawFont24(440, 380, "Quit the level.", Color.black);
				}*/
		}		

		

		
		if (Global.getControlActive(Global.GameControl.CANCEL)) {
			this.levelFinished = true;
			Global.actionDelay = Global.inputReadDelayTimer;
		}
	}
	
/*	private void showGameOver() {
		overlay.draw(0, 0);
		gameOverControls();
		
		Color.white.bind();
		Global.uiWhite.draw(256, 192, 512, 384);
		//Color.green.bind();
		Global.menuButtonShader.bind();
		//Global.uiTransWhite.draw(212, 305 + i * 70, 190, 48);
		Global.uiTransWhite.draw(288, 224, 192, 48); // left button
		Global.uiTransWhite.draw(546, 224, 192, 48); // right button
		//Color.white.bind();
		Color.gray.bind();
		Global.uiWhite.draw(288, 288, 452, 192);
		
		if (noMoves) {
			if (pauseCursorPos == 0) {
				Global.drawFont24(330, 240, "Restart", Color.white);
				Global.drawFont24(618, 240, "Quit", Color.black);
				Global.drawFont24(415, 380, "No more moves left", Color.black);
				//Global.drawFont24(308, 350, "your current score.",Color.black);
			}
			
			if (pauseCursorPos == 1) {
				Global.drawFont24(330, 240, "Restart", Color.black);
				Global.drawFont24(618, 240, "Quit", Color.white);
				Global.drawFont24(440, 380, "Quit the level.", Color.black);
			}
			
			if (Global.getControlActive(Global.GameControl.CANCEL)) {
				this.levelFinished = true;
				Global.actionDelay = Global.inputReadDelayTimer;
			}
		} else if (noRemainClears) {
			if (pauseCursorPos == 0) {
				Global.drawFont24(330, 240, "Restart", Color.white);
				Global.drawFont24(618, 240, "Quit", Color.black);
				Global.drawFont24(415, 380, "No more clears left", Color.black);
				//Global.drawFont24(308, 350, "your current score.",Color.black);
			}
			
			if (pauseCursorPos == 1) {
				Global.drawFont24(330, 240, "Restart", Color.black);
				Global.drawFont24(618, 240, "Quit", Color.white);
				Global.drawFont24(440, 380, "Quit the level.", Color.black);
			}
			
			if (Global.getControlActive(Global.GameControl.CANCEL)) {
				this.levelFinished = true;
				Global.actionDelay = Global.inputReadDelayTimer;
			}
		}else {
			if (pauseCursorPos == 0) {
				Global.drawFont24(330, 240, "Continue?", Color.white);
				Global.drawFont24(618, 240, "Quit", Color.black);
				//Global.drawFont24(304, 320, "Continue from this level with half of", Color.black);
				//Global.drawFont24(308, 350, "your current score.",Color.black);
			}
			
			if (pauseCursorPos == 1) {
				Global.drawFont24(330, 240, "Continue?", Color.black);
				Global.drawFont24(618, 240, "Quit", Color.white);
				Global.drawFont24(440, 380, "Quit the level.", Color.black);
			}
			
			if (Global.getControlActive(Global.GameControl.CANCEL)) {
				this.levelFinished = true;
				Global.actionDelay = Global.inputReadDelayTimer;
			}
		}
	}*/
	
	
	protected abstract void buildGrid(); 

	/**
	 * Draw the contents of the <code>Block</code> grid to the screen and
	 * adjusts the position of moving blocks.
	 * @param shiftRate The drop rate in pixels/second for falling blocks
	 * @return true if blocks are currently falling within the grid, false
	 * if no blocks are currently falling 
	 * @author John
	 */
	protected final boolean drawGrid(int shiftRate) {
		gridShiftActionDelay -= Global.delta;
		queueManualShiftDelay -= Global.delta;
		int[] gridBasePos = new int[] { 20, Global.glEnvHeight - blockSize[1] - 50 }; // distance from the left top for the bottom-left of the grid display
		//int dropRate = 20; // millisecond time for a falling block to cover 1 space
		blockDropActive = false;
		gridShiftActive = false;
		int blockMoveRate = (int)(Global.delta * shiftRate) / 1000;
		int columnMoveRate = (int) (Global.delta * shiftRate) / 500; // columns move 2x as fast as blocks
		// adjust falling block offsets
		for (int i = 0; i < grid.length; i++) {
			for (int k = 0; k < grid[0].blocks.length; k++) {
				if (grid[i].blocks[k] != null) {
					if (grid[i].blocks[k].dropDistance > 0) {
						grid[i].blocks[k].dropDistance -= blockMoveRate;
						if (grid[i].blocks[k].dropDistance < 0) { 
							grid[i].blocks[k].dropDistance = 0; 
						} else {
							blockDropActive = true;
						}
					}
				}
			}
		}
		// adjust grid column offsets if no blocks are falling
		if (!blockDropActive) {
			for (int i = 0; i < grid.length; i++) {
				if (grid[i].columnOffset != 0) {
					if (gridShiftDir == 1) { // right-shift
						grid[i].columnOffset += columnMoveRate;
						if (grid[i].columnOffset >= 0) { 
							grid[i].columnOffset = 0; 
						} else {
							gridShiftActive = true;
						}
					} else { // left-shift
						grid[i].columnOffset -= columnMoveRate;
						if (grid[i].columnOffset <= 0) {
							grid[i].columnOffset = 0;
						} else {
							gridShiftActive = true;
						}
					}
				}
			}
		}
		// draw the grid
		for (int i = 0; i < grid.length; i++) {
			for (int k = 0; k < grid[0].blocks.length; k++) {
				if (grid[i].blocks[k] != null) {
					grid[i].blocks[k].draw(
							gridBasePos[0] + blockSize[0] * i + grid[i].columnOffset,
							gridBasePos[1] - blockSize[1] * k - grid[i].blocks[k].dropDistance,
							blockSize
						);
					grid[i].blocks[k].checked = false;
					grid[i].blocks[k].clearMark = false;
				}
			}
		}
		drawQueue();
		return (blockDropActive || gridShiftActive);
	}
	
	protected int[] wedgePos = new int[] { -1, -1 };
	private final long blockDropDelayTimer = 16l; // 32 is approx. 30/sec
	private long blockDropDelay = blockDropDelayTimer;
	private final int blockMoveRate = 8;
	private boolean blocksMoving = false;
	private boolean cascadeGridShift = false;
	
	protected void processGridBlocks(GridColumn[] grid) {
		blockDropDelay -= Global.delta;
		if (blockDropDelay > 0) { return ; }
		blockDropDelay += blockDropDelayTimer;
		blocksMoving = false;
		int starBlockCounter = 0; // if 2 or more star blocks are present on the grid, they will be checked for sharing edges after all movement is completed
		
		int xMax = grid.length - 1;
		int yMax = grid[0].blocks.length - 1;
		// contains the position of the topmost block per column. will be checked when attempting to move under a wedge block
		int[] topblock = new int[grid.length];
		for (int i = 0; i < topblock.length; i++) { topblock[i] = -1; }
		GridColumn gc;
		for (int x = 0; x <= xMax; x++) {
			gc = grid[x];
			for (int y = 0; y <= yMax; y++) {
				if (gc.blocks[y] == null) { continue; }
				
				gc.blocks[y].checked = false; // reset checked flag each loop
				gc.blocks[y].clearMark = false; // reset clear mark each loop
				//if (gc.blocks[y].type == Block.BlockType.WEDGE) { } else 
				if (y == 0) { continue; } // do not check for fall if at bottom row
				if (gc.blocks[y].type != Block.BlockType.WEDGE) {
					if (gc.blocks[y].type == Block.BlockType.STAR) { starBlockCounter++; }
					// set block as moving. this value will be reset if the block cannot fall.
					if (!cascadeGridShift) { gc.blocks[y].dropDistance += blockMoveRate; }
					
					if (gc.blocks[y-1] == null) { // space below is empty
						// set block as moving. this value will be reset if the block cannot fall.
						if (cascadeGridShift) { gc.blocks[y].dropDistance += blockMoveRate; }
						if (gc.blocks[y].dropDistance > blockSize[1]) {
							gc.blocks[y].dropDistance -= blockSize[1];
							if (y == 1) { // move into last row check
								gc.blocks[y].dropDistance = 0;
							}
							gc.blocks[y-1] = gc.blocks[y].clone();
							gc.blocks[y] = null;
						}
						blocksMoving = true;
					} else if (gc.blocks[y].dropDistance > gc.blocks[y-1].dropDistance) {
						gc.blocks[y].dropDistance = gc.blocks[y-1].dropDistance;
						if (gc.blocks[y].dropDistance > 0) {
							blocksMoving = true; 
						}
					} else if (gc.blocks[y-1].dropDistance > 0) { // check if block below is moving
						
					} else {
						gc.blocks[y].dropDistance = 0;
						//blocksMoving = false;
					}
					if (gc.blocks[y] != null) {
						if (x != wedgePos[0] || y < wedgePos[1]) {
							topblock[x] = y;
						}
					}
				}
			} // end for(y)
		} // end for(x)
		
		// wedge block mechanics
		if (wedgePos[0] >= 0 && wedgePos[1] >= 0) {
			int xc = wedgePos[0], yc = wedgePos[1];
			GridColumn moveFrom = grid[xc];
			GridColumn moveTo = null;
			// if the grid shift is disabled (set to 0), the wedge will just stop blocks from falling
			// logic assumes that the wedge is never against an edge
			if (grid[xc].blocks[yc+1] == null || gridShiftDir == 0) { } // no block to move or grid shift disabled
			else {
				if (gridShiftDir == 1) { // right-shift
					if (topblock[xc+1] <= yc) { moveTo = grid[xc+1]; }
				} else if (gridShiftDir == -1) { // left-shift
					if (topblock[xc-1] <= yc) { moveTo = grid[xc-1]; }
				}
				
				if (moveTo != null && moveTo.blocks[yc] == null && moveTo.blocks[yc+1] == null) {
					moveTo.blocks[yc+1] = moveFrom.blocks[yc+1].clone();
					moveFrom.blocks[yc+1] = null;
					blocksMoving = true;
				}
			}
		}
		
		if (blocksMoving) {  // check for falling blocks before horizontal movement 
			return; 
		}
		
		// left-right grid shift after all block fall mechanics have been handled
		GridColumn emptyset = new GridColumn(grid[0].blocks.length);
		if (gridShiftDir == 1) { // right-shift
			// next should always be (xc + 1)
			for (int xc = xMax - 1, next = xMax; xc >= 0; xc--, next--) {
				
				// check conditions that prevent column movement
				if (grid[xc].blocks[0] == null) { continue; }
				if (cascadeGridShift && grid[next].blocks[0] != null) { continue; } // cascading forces each column to wait for the next to be empty
				if (grid[xc].blocks[0].type == Block.BlockType.ROCK) { continue; } // rock in current column, do no shift
				if (grid[next].blocks[0] != null && grid[next].columnOffset == 0) { // no room to move (this should also match next column rock blocks) 
					grid[xc].columnOffset = 0;
					continue; 
				}
				if (next == wedgePos[0] && topblock[xc] >= wedgePos[1]) { // the column is too tall to fit under a wedge block
					grid[xc].columnOffset = 0;
					continue;
				}
				grid[xc].columnOffset += blockMoveRate;
				blocksMoving = true;
				if (grid[xc].columnOffset >= blockSize[0]) {
					grid[xc].columnOffset -= blockSize[0];
					if (next == wedgePos[0]) { // moving into the column with wedge block
						for (int k = 0; k < wedgePos[1]; k++) {
							grid[next].blocks[k] = grid[xc].blocks[k];
							grid[xc].blocks[k] = null;
						}
						
					} else if (xc == wedgePos[0]) { // moving out of column with wedge block
						for (int k = 0; k < wedgePos[1]; k++) {
							grid[next].blocks[k] = grid[xc].blocks[k];
							grid[xc].blocks[k] = null;
						}
					} else {
						grid[next] = grid[xc];
						grid[xc] = emptyset.clone();
					}
				}
			}
		} else if (gridShiftDir == -1) { // left-shift
			// next should always be (xc - 1)
			for (int xc = 1, next = 0; xc <= xMax; xc++, next++) {
				
				// check conditions that prevent column movement
				if (grid[xc].blocks[0] == null) { continue; }
				if (cascadeGridShift && grid[next].blocks[0] != null) { continue; } // cascading forces each column to wait for the next to be empty
				if (grid[xc].blocks[0].type == Block.BlockType.ROCK) { continue; } // rock in current column, do no shift
				if (grid[next].blocks[0] != null && grid[next].columnOffset == 0) { // no room to move (this should also match next column rock blocks) 
					grid[xc].columnOffset = 0;
					continue; 
				}
				if (next == wedgePos[0] && topblock[xc] >= wedgePos[1]) { // the column is too tall to fit under a wedge block
					grid[xc].columnOffset = 0;
					continue;
				}
				grid[xc].columnOffset -= blockMoveRate;
				blocksMoving = true;
				if (grid[xc].columnOffset <= -blockSize[0]) {
					grid[xc].columnOffset += blockSize[0];
					if (next == wedgePos[0]) { // moving into the column with wedge block
						for (int k = 0; k < wedgePos[1]; k++) {
							grid[next].blocks[k] = grid[xc].blocks[k];
							grid[xc].blocks[k] = null;
						}
						
					} else if (xc == wedgePos[0]) { // moving out of column with wedge block
						for (int k = 0; k < wedgePos[1]; k++) {
							grid[next].blocks[k] = grid[xc].blocks[k];
							grid[xc].blocks[k] = null;
						}
					} else {
						grid[next] = grid[xc];
						grid[xc] = emptyset.clone();
					}
				}
			}
		}
		if (blocksMoving || starBlockCounter < 2) {
			return;
		}
		
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[0].blocks.length; y++) {
				if (grid[x].blocks[y] != null && grid[x].blocks[y].type == Block.BlockType.STAR) {
					// TODO: add activation call for star blocks found sharing an edge
					if (grid[x+1].blocks[y] != null && grid[x+1].blocks[y].type == Block.BlockType.STAR) {
						
					} else 
					if (grid[x-1].blocks[y] != null && grid[x+1].blocks[y].type == Block.BlockType.STAR) {
						
					} else 
					if (grid[x].blocks[y+1] != null && grid[x+1].blocks[y].type == Block.BlockType.STAR) {
						
					} else
					if (grid[x].blocks[y-1] != null && grid[x+1].blocks[y].type == Block.BlockType.STAR) {
						
					}
				}
			}
		}
		
	}
	
	/** Draws the grid to the screen u
	 * 
	 * @param grid
	 * @author John
	 */
	protected void drawGridRework(GridColumn[] grid) {
		// The old grid draw functions will not work with the new grid management algorithm, the math will not move the blocks the same
		for (int i = 0; i < grid.length; i++) {
			for (int k = 0; k < grid[0].blocks.length; k++) {
				if (grid[i].blocks[k] == null) { continue; }
				if (grid[i].blocks[k].type == Block.BlockType.WEDGE) { // wedge blocks are not drawn with grid column offset adjustment
					grid[i].blocks[k].draw(
							gridBasePos[0] + blockSize[0] * i,
							(gridBasePos[1] - blockSize[1] * k) + grid[i].blocks[k].dropDistance,
							blockSize
						);
				} else {
					grid[i].blocks[k].draw(
							gridBasePos[0] + blockSize[0] * i + grid[i].columnOffset,
							(gridBasePos[1] - blockSize[1] * k) + grid[i].blocks[k].dropDistance,
							blockSize
						);
				}
			}
		}
		drawQueue();
	}
	
	
	
	/**
	 * Draws the <code>Block</code> grid without processing any block movement.
	 * @author John
	 */
	protected final void drawGrid() {
		for (int i = 0; i < grid.length; i++) {
			for (int k = 0; k < grid[0].blocks.length; k++) {
				if (grid[i].blocks[k] != null) {
					grid[i].blocks[k].draw(
							gridBasePos[0] + blockSize[0] * i + grid[i].columnOffset,
							gridBasePos[1] - blockSize[1] * k - grid[i].blocks[k].dropDistance,
							blockSize
						);
					grid[i].blocks[k].checked = false;
					grid[i].blocks[k].clearMark = false;
				}
			}
		}
		drawQueue();
	}
	
	/**
	 * @author Brock
	 */
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
						resetVariables();
						/*gameOver = false;
						remainClears = totalClears;
						noMoves = false;
						noRemainClears = false;
						buildGrid();
						score = 0;
						gamePaused = false;
						energy = energyMax;
						inputDelay = Global.inputReadDelayTimer;*/	
						break;
						
					case 2:
						levelFinished = true;
						gameOver = true;
						inputDelay = Global.inputReadDelayTimer;	
						break;
				}
			}
		} else if (inputDelay > 0) {
			inputDelay -= Global.delta;
		}
	}
	
	protected void gameOverControls() {
		// Author: Mario
		if (inputDelay <= 0) {
			/*if (Global.getControlActive(Global.GameControl.LEFT) || Global.getControlActive(Global.GameControl.DOWN)) {
				pauseCursorPos = pauseCursorPos == 0 ? 1 : 0;
				inputDelay = Global.inputReadDelayTimer * 2;
			}
			if (Global.getControlActive(Global.GameControl.RIGHT) || Global.getControlActive(Global.GameControl.DOWN)) {
				pauseCursorPos = pauseCursorPos == 0 ? 1 : 0;
				inputDelay = Global.inputReadDelayTimer * 2;
			}*/
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
						resetVariables();
						/*gameOver = false;
						//energy = energyMax;
						//score = score/2;
						remainClears = totalClears;
						noMoves = false;
						noRemainClears = false;
						//resetMoves = true;
						buildGrid();
						score = 0;
						energy = energyMax;
						inputDelay = 4 * Global.inputReadDelayTimer;*/	
						break;
					case 1:
						gameOver = true;
						levelFinished = true;
						inputDelay = 4 * Global.inputReadDelayTimer;
						remainClears = -1;
						break;
				}
				//inputDelay = Global.inputReadDelayTimer * 2;
			}
		} else if (inputDelay > 0) {
			inputDelay -= Global.delta;
		}
	}
	
	/** @author Brock */
	protected void levelFinishedControls() {
		if (inputDelay <= 0) {
			/*if (Global.getControlActive(Global.GameControl.LEFT)) {
				pauseCursorPos--;
				if (pauseCursorPos < 0) {
					pauseCursorPos = 2;
				}
				inputDelay = Global.inputReadDelayTimer * 2;
			}
			if (Global.getControlActive(Global.GameControl.RIGHT)) {
				pauseCursorPos++;
				if (pauseCursorPos > 2) {
					pauseCursorPos = 0;
				}
				inputDelay = Global.inputReadDelayTimer * 2;
			}*/
			if (Global.getControlActive(Global.GameControl.DOWN)) {
				pauseCursorPos++;
				if (pauseCursorPos > 2) {
					pauseCursorPos = 0;
				}
				inputDelay = Global.inputReadDelayTimer * 2;
			}
			if (Global.getControlActive(Global.GameControl.UP)) {
				pauseCursorPos--;
				if (pauseCursorPos < 0) {
					pauseCursorPos = 2;
				}
				inputDelay = Global.inputReadDelayTimer * 2;
			}
			
			if (Global.getControlActive(Global.GameControl.SELECT)) {
				switch (pauseCursorPos) {
					case 0:
						levelFinished = true;
						gameOver = false;
						score = 0;
						//resetMoves = true;
						remainClears = -1;
						//remainClears = totalClears;
						inputDelay = 10 * Global.inputReadDelayTimer;	
						break;
	
					case 1:
						resetVariables();
						/*gameOver = false;
						levelFinished = false;
						levelComplete = false;
						//energy = energyMax;
						//score = score/2;
						remainClears = totalClears;
						noMoves = false;
						noRemainClears = false;
						buildGrid();
						score = 0;
						cursorGridPos[0] = grid.length / 2;
						cursorGridPos[1] = grid[0].blocks.length / 2;
						pauseCursorPos = 0;
						energy = energyMax;
						inputDelay = 10 * Global.inputReadDelayTimer;*/	
						break;
					case 2:
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

	/** Checks against movement inputs within the grid, and adjusts the cursor
	 * position accordingly.
	 * @author John
	 */
	protected void checkCommonControls() {
		if (Global.getControlActive(Global.GameControl.PAUSE)) {
			pauseCursorPos = 0;
			gamePaused = true;
			inputDelay = 1000l;
		} else if (Global.getControlActive(Global.GameControl.SPECIAL2)) {
			// queue control
			queueHold = true;
			if ( queueManualShiftDelay <= 0) {
				if (Global.getControlActive(Global.GameControl.LEFT)) {
					// shift queue left
					//shiftQueue(-1);
					queueManualShiftDelay = queueManualShiftDelayTimer;
				} else if (Global.getControlActive(Global.GameControl.RIGHT)) {
					// shift queue right
					//shiftQueue(1);
					queueManualShiftDelay = queueManualShiftDelayTimer;
				} else if (Global.getControlActive(Global.GameControl.DOWN)) {
					// drop (add to grid) queue
					//int overflow = addToGrid();
					//updateScore( overflow * -10 );
					queueManualShiftDelay = queueManualShiftDelayTimer;
				}
			}
		} else {
			//queueHold = false;
			// cursor control
			/*if (Global.getControlActive(Global.GameControl.SPECIAL1) && gridShiftActionDelay <= 0) {
				gridShiftActionDelay = shiftActionDelayTimer;
				gridShiftActive = true;
				gridShiftDir *= -1;
				shiftGridColumns();
				
			}*/
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
				if (!gridMoving && Global.getControlActive(Global.GameControl.SELECT) &&
						grid[cursorGridPos[0]].blocks[cursorGridPos[1]] != null) {
					counter = 0;
					processActivate();
					if (counter > 1 || grid[cursorGridPos[0]].blocks[cursorGridPos[1]].type == Block.BlockType.BOMB) {
						// decrease the blocksRemaining counter after blocks are cleared
						removeMarkedBlocks();
						//dropBlocks();
						//shiftGridColumns();
						remainClears -= 1;
						// action delay is only increased if an action was performed and the grid was changed
						// actionDelay = Global.inputReadDelayTimer;
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
	protected final void removeMarkedBlocks() {
		for (int xc = 0; xc < grid.length; xc++) {
			for (int yc = 0; yc < grid[0].blocks.length; yc++) {
				if (grid[xc].blocks[yc] != null && grid[xc].blocks[yc].clearMark) {
					grid[xc].blocks[yc] = null;
					blocksRemaining--;
					//remainClears--;
				}
				/*if (blocksRemaining > 0 && grid[xc].blocks[yc] != null) {
					sumMoves += checkGridMovesRemain(xc, yc, grid[xc].blocks[yc].colorID);
				}
				if (sumMoves == 0) {
					noMoves = true;
				}*/
			}
		}
	}
	
	/**
	 * Calculates and sets the drop distance for remaining blocks after blocks
	 * have been cleared from the grid. This method should be overridden if special
	 * blocks in play would prevent normal block falling behavior.
	 * @param blockDimensions the height of blocks used in the level. 
	 * Used to calculate the distance blocks will be offset. 
	 * @author John
	 */
	protected void dropBlocks() {
		int dropDist = 0;
		int slotDist = 0;
		for (int i = 0; i < grid.length; i++) {
			slotDist = 0;
			dropDist = 0;
			for (int k = 0; k < grid[0].blocks.length; k++) {
				if (grid[i].blocks[k] == null) {
					dropDist += blockSize[1];
					slotDist++;
				} else if (dropDist > 0) {
					grid[i].blocks[k].dropDistance = dropDist;
					grid[i].blocks[k-slotDist] = grid[i].blocks[k];
					grid[i].blocks[k] = null;
				}
			}
		}
		return ;
	}
	
	/**
	 * @author John
	 */
	protected void shiftGridColumns() {
		GridColumn emptyset = new GridColumn(grid[0].blocks.length);
		int colDist = 0, shiftDist = 0;
		if (gridShiftDir == 1) {
			for (int xc = grid.length - 1; xc >= 0; xc--) { // xCurrent, xPrevious
				if (grid[xc].blocks[0] == null) {
					colDist++;
					shiftDist += blockSize[0];
				} else if (grid[xc].blocks[0].type == Block.BlockType.ROCK) {
					// columns with ROCK blocks do not shift and stop other columns from moving past
					colDist = 0;
					shiftDist = 0;
					continue;
				} else if (shiftDist > 0) {
					grid[xc].columnOffset -= shiftDist; 
					grid[xc + colDist] = grid[xc];
					grid[xc] = emptyset.clone();
				}
			}
		} else if (gridShiftDir == -1) {
			for (int xc = 0; xc < grid.length; xc++) {
				if (grid[xc].blocks[0] == null) {
					colDist++;
					shiftDist += blockSize[0];
				} else if (grid[xc].blocks[0].type == Block.BlockType.ROCK) {
					// columns with ROCK blocks do not shift and stop other columns from moving past
					colDist = 0;
					shiftDist = 0;
					continue;
				} else if (shiftDist > 0) {
					grid[xc].columnOffset += shiftDist;
					grid[xc - colDist] = grid[xc];
					grid[xc] = emptyset.clone();
				}
			}
		}
		
		return ;
	}
	
	/**
	 * Adds blocks in queue to the grid at the top level. Returns the number of blocks 
	 * that could not be added (such as when the column is already full). 
	 * @param blockQueue <code>Block</code> array containing blocks to be added for each
	 * grid column.
	 * @return The number of blocks that could not be added to the grid.
	 * @author John
	 */
	protected int addToGrid() {
		int overflow = 0;
		int yMax = grid[0].blocks.length - 1;
		for (int x = 0; x < grid.length; x++) {
			if (queue[x] != null) {
				if (grid[x].blocks[yMax] == null) {
					grid[x].blocks[yMax] = queue[x];
					blocksRemaining++;
				} else {
					overflow++;
				}
				queue[x] = null;
			}
		}
		if (overflow < queueCount) {
			gridMoving = true;
		}
		//dropBlocks();
		//shiftGridColumns();
		queueCount = 0;
		return overflow;
	}
	
	//protected abstract Block getQueueBlock();
	
	protected void processActivate() {
		// TODO: score base value calculation is to be done within each case statement
		switch (grid[cursorGridPos[0]].blocks[cursorGridPos[1]].type) {
			case BLOCK:
				counter = checkGrid(cursorGridPos);
				int adj = (int)Math.pow(counter - 1, 2);
				updateScore(adj);
				//addEnergy(adj);
				//updateMoves(1);
				break;
			case BOMB:
				counter = activateBombBlock(cursorGridPos);
				updateScore(counter);
				addEnergy(counter);
				break;
			case HEART:
				heartSpecialActive = true;
				actionDelay = Global.inputReadDelayTimer * 3;
				energy += energyMax / 10; // regenerate 10% of max energy on use
				break;
			default: // block does not activate, do nothing
				break;
		}
		
	}
	
	/**
	 * Shifts the <code>Block</code> queue across the screen 
	 * @param direction 1 to shift the queue to the right, eles shift to the left
	 * @author John
	 */
	private void shiftQueue(int direction) {
		if (levelComplete) { return ; }
		int xMax = queue.length;
		int current, next;
		if (direction == 1) { // shift right
			for (int x = xMax - 1; x >=0; x--) {
				if (queue[x] == null) {
					current = x;
					next = (xMax + (x - 1)) % xMax;
					for (int i = 0; i < xMax; i++) {
						current = (xMax + (x - i)) % xMax;
						next = (xMax + (current - 1)) % xMax;
						queue[current] = queue[next];
						queue[next] = null;
					}
					break;
				}
			}
		} else {
			for (int x = 0; x < xMax; x++) {
				if (queue[x] == null) { // find first null space
					current = x;
					next = (x + 1) % xMax;
					for (int i = 0 ; i < xMax; i++) { // shift the queue
						current = (x + i) % xMax;
						next = (current + 1) % xMax;
						queue[current] = queue[next];
						queue[next] = null;
					}
					break;
				}
			}
			
		}
	}
	
	/**
	 * @author John
	 */
	protected void processQueue() {
		if (levelComplete) { return; }
		if (heartSpecialActive) { return; }
		queueStepDelay -= Global.delta;
		int xMax = queue.length;
		if (queueStepDelay > 0) { return; }
		queueStepDelay = queueStepDelayTimer; // reset step timer
		queueStepCount++;
		// shift the queue to the left by one block
		if (queueCount >= queueLimit && queueStepCount == 2) {
			int overflow = addToGrid();
			updateScore( overflow * -10 );
			queueCount = 0;
		} else if (!queueHold) {
			shiftQueue(-1);
		}
		if (queueStepCount < queueStepReq) { 
			return; 
		}
		queueStepCount = 0; // reset steps-remaining-until-block-add timer
		Block b = getQueueBlock();
		int firstNull = queue.length - 1;
		if (queue[firstNull] != null) {
			for (int x = xMax - 1; x > 0; x--) { // find closest null from right
				if (queue[x] == null) {
					firstNull = x;
					break;
				}
			}
			// shift right-most blocks to make room for new block
			for (int x = firstNull; x < queue.length - 1; x++) { 
				queue[x] = queue[x + 1];
			}
		}
		queue[queue.length - 1] = b;
		queueCount++;

	}
	
	/**
	 * Removes all instances of a specific block from the queue
	 * @param block The matching block color to be removed.
	 * @author John
	 */
	protected void removeFromQueue(int color) {
		for (int i = 0; i < queue.length; i++) {
			if (queue[i] != null && queue[i].type == Block.BlockType.BLOCK && queue[i].colorID == color) {
				queue[i] = null;
				queueCount--;
			}
		}
	}
	
	
	/**
	 * @author John
	 */
	protected void drawQueue() {
		int[] anchorPos = new int[] { 20, 40 };
		int offset = 0;
		for (int i = 0; i < queue.length; i++) {
			if (queue[i] != null) {
				queue[i].draw(anchorPos[0] + offset, anchorPos[1], blockSize);
			}
			offset += blockSize[0];
		}
		return ;
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
	
	/**
	 * @param baseAdjustment The base value to adjust score by, before applying the <code>levelMultiplier</code>
	 * @author John
	 */
	protected void updateScore(int baseAdjustment) {
		score += (int)Math.floor(baseAdjustment * levelMultiplier);
	}
	
	protected void updateMoves(int baseAdjustment) {
		remainClears -= baseAdjustment;
	}
	
	protected void addEnergy(int baseAdjustment) {
		energy += (int)Math.floor(baseAdjustment * energyGainMultiplier);
		if (energy > energyMax) { energy = energyMax; }
	}

	/**
	 * Marks for removal blocks within <code>radius</code> block distance (including diagonal).
	 * Recursively activates any other bomb blocks within the radius.
	 * @param radius the blocks radius from the bomb to clear
	 * @param pos starting position for the bomb block
	 * @return The number of blocks removed
	 * @author John
	 */
	protected int activateBombBlock(int[] pos) {
		int radius = grid[pos[0]].blocks[pos[1]].colorID;
		int xMin = pos[0] - radius, 
			xMax = pos[0] + radius,
			yMin = pos[1] - radius, 
			yMax = pos[1] + radius;
		int[] xEdge = new int[] { xMin, xMax }; // edge values before range checks
		int[] yEdge = new int[] { yMin, yMax };
		int cornerRadius = radius / 3;
		if (cornerRadius <= 0) { cornerRadius = 1; }
		int count = 0;
		if (xMin < 0) { xMin = 0; }
		if (xMax >= gridSize[0]) { xMax = gridSize[0] - 1; }
		if (yMin < 0) { yMin = 0; }
		if (yMax >= gridSize[1]) { yMax = gridSize[1] - 1; }
		// mark center bomb as cleared to prevent recursive calls to already activated bomb blocks
		grid[pos[0]].blocks[pos[1]].clearMark = true;
		count++;
		for (int i = xMin; i <= xMax; i++) {
			for (int k = yMin; k <= yMax; k++) {
				if (i < (xEdge[0] + cornerRadius) || i > (xEdge[1] - cornerRadius)) {
					if (k < (yEdge[0] + cornerRadius) || k > (yEdge[1] - cornerRadius)) {
						continue; // skip corner checks
					}
				}
				if (grid[i].blocks[k] != null && !grid[i].blocks[k].clearMark) {
					if (grid[i].blocks[k].type == Block.BlockType.BOMB) {
						count += activateBombBlock( new int[] { i, k } );
					} else if (grid[i].blocks[k].type == Block.BlockType.ROCK) { // ignore rock blocks
						continue;
					} else if (grid[i].blocks[k].type == Block.BlockType.WEDGE) { // ignore wedge blocks
						continue;
					} else {
						grid[i].blocks[k].clearMark = true;
						count++;
					}
				}
			}
		}
		return count;
	}
	

	/**
	 * Draw selector menu for heart special block
	 * @author Brock
	 */
	protected int DrawHeartSelector() {
		int colorID = 0;
		for (int i = 0; i < heartMenuBlocks.length; i++) {
			heartMenuBlocks[i] = new Block(Block.BlockType.BLOCK, i);
		}
		heartCursor = new Sprite (
			Global.textureMap.get("blocksheet"),
			new int[] { 240, 0 },
			new int[] { 32, 32 },
			new int[] { 32, 32 }
			);
		
		overlay.draw(0, 0);
		for (int j = 0; j < heartMenuBlocks.length; j++) {
			heartMenuBlocks[j].draw(100 + j * 32, 100);
			heartCursor.draw(100 + heartCursorPos * 32, 100);
		}
		return colorID;
	}
	
	/**
	 * Controls for heart selector menu
	 * @author Brock
	 */
	protected void heartMenuControls() {
		//actionDelay = Global.inputReadDelayTimer * 2;
		if (actionDelay <= 0) {
			if (Global.getControlActive(Global.GameControl.LEFT)) {
				heartCursorPos--;
				if (heartCursorPos < 0) {
					heartCursorPos = 5;
				}
				actionDelay = Global.inputReadDelayTimer * 2;
			}
			if (Global.getControlActive(Global.GameControl.RIGHT)) {
				heartCursorPos++;
				if (heartCursorPos > 5) {
					heartCursorPos = 0;
				}
				actionDelay = Global.inputReadDelayTimer * 2;
			}
			if (Global.getControlActive(Global.GameControl.CANCEL)) {
				specialMenu = true;
				actionDelay = Global.inputReadDelayTimer * 2;
	
			}
			if (Global.getControlActive(Global.GameControl.SELECT)) {
				switch(heartCursorPos) {
					case 0:
						colorID = 0;
						break;
					case 1:
						colorID = 1;
						break;
					case 2:
						colorID = 2;
						break;
					case 3:
						colorID = 3;
						break;
					case 4:
						colorID = 4;
						break;
					case 5:
						colorID = 5;
						break;
					default:
							break;
				}
				clearColor = true;
				actionDelay = Global.inputReadDelayTimer * 2;
			}  
		} else {
			actionDelay -= Global.delta;
		}
	}
	
	/**
	 * Marks blocks in the grid to be cleared for the color selected
	 * @author Brock
	 */
	protected int activateHeartBlock(int pos[]) {
		int count = 0;
		grid[pos[0]].blocks[pos[1]].clearMark = true;
		
		for (int i = 0; i < gridSize[0]; i++) {
			for (int j = 0; j < gridSize[1]; j++) {
				if (grid[i].blocks[j] == null) {
					continue;
				}
				if (grid[i].blocks[j].colorID == colorID && grid[i].blocks[j].type == Block.BlockType.BLOCK) {
					grid[i].blocks[j].clearMark = true;
					count++;
					colorCount++;
				} else {
					continue;
				}
			}
		}
		removeFromQueue(colorID);
		if (colorCount <= 0) {
			clearColor = false;
			return 0;
		} else {
			return count;
		}
	}
}

