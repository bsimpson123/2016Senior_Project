import static org.lwjgl.opengl.GL11.*;

import java.time.LocalDateTime;
import java.util.HashMap;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.Color;
import org.newdawn.slick.openal.Audio;

public class BlockBreakLevel {
	private static GameSounds soundbank;
	private static Audio blockfall;
	
	protected static Sprite[] numbers = new Sprite[10];
	//private static Sprite pauseCursor;
	protected static Sprite cursor;
	protected static Sprite[] shiftLR = new Sprite[2];
	protected static Sprite overlay;
	
	protected static int score;
	private static int scoreDisplay = 0;
	private static long scoreUpdateDelayTimer = 50l;
	private static long scoreUpdateDelay = scoreUpdateDelayTimer;
	
	protected static Sprite levelDisplay;
	protected static Sprite background;
	protected static Sprite userInterface;
	protected static Sprite emptyEnergy;
	protected static Texture energyBar;
	
	protected int energyMax = 100000;
	protected int energy = energyMax;
	private int energyDisplay = energyMax;
	protected float energyGainMultiplier = 1.0f;
	protected boolean disableEnergy = false;
	
	// grid variables
	protected GridColumn[] grid;
	protected int[] gridBasePos;
	protected int[] blockSize = new int[] { 32, 32 };
	/** Defines which direction the grid columns should shift where there is space between them.<br>
	 * 1 => right-shift, -1 => left-shift, 0 => do not shift grid columns */
	private int gridShiftDir = 1;
	/** The amount of time the player must wait between each switch of the grid direction. */
	private final long gridShiftActionDelayTimer = 1000;
	private long gridShiftActionDelay = gridShiftActionDelayTimer;
	protected int blocksRemaining = 0;
	protected int[] wedgePos = new int[] { -1, -1 };
	private final long blockDropDelayTimer = 16l; // 32 is approx. 30 times/sec
	private long blockDropDelay = blockDropDelayTimer;
	private final int blockMoveRate = 8;
	private boolean blocksMoving = false;
	
	// grid queue variables
	private Block[] queue;
	/** Time delay between each 'step' for the queue, lower values will cause the queue to advance quicker */
	protected long queueStepDelayTimer = 500;
	private long queueStepDelay = queueStepDelayTimer;
	/** The number of 'empty' steps to take before adding a block to the queue. */
	protected int queueStepReq = 4;
	private int queueStepCount = 0;
	private int queueCount = 0;
	/** The number of blocks that should be in the queue before forcibly adding to the grid */
	private int queueLimit = 5;
	private final long queueManualShiftDelayTimer = 200;
	private long queueManualShiftDelay = queueManualShiftDelayTimer;
	private boolean queueHold = false;
	/** If <code>true</code>, no queue processing will be done. */
	protected boolean queueDisabled = false;
	
	private boolean heartSpecialActive = false;
	protected int[] cursorGridPos = new int[] { 0, 0 };
	private int heartCursorPos = 0;
	private boolean gamePaused = false;
	private long inputDelay = Global.inputReadDelayTimer;
	private long actionDelay = Global.inputReadDelayTimer * 2;
	protected String levelTitle;
	protected final int level;
	
	/** Sets sets the multiplier to apply to all score additions/subtractions. */
	protected float levelMultiplier = 1.0f;
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

	private int heartSelectColor = 0;
	private boolean clearColor;
	private Block[] heartMenuBlocks = new Block[Block.blockColorCount];
	
	private int pauseCursorPos = 0;
	
	private int[] blockCounts = new int[Block.blockColorCount];
	private int allowedColors = 0;
	private int totalColors = 0;
	protected int minColors = 2;
	private int heartGenChance = 20;
	private int bombGenChance = 20;

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
		
		
	}
	
	public BlockBreakLevel(int levelSelect) { 
		level = levelSelect;
		buildGrid(level);
		levelTitle = String.format("Level %02d", level);
	} 
	
	protected void buildGrid(int levelSelect) {
		// set the energy amount for the level
		energy = energyMax = 200000;
		levelMultiplier = 1.0f;
		energyGainMultiplier = 1.0f;
		blockSize = new int[] { 32, 32 }; // default block size is { 32, 32 }
		// time between each 'step' for the queue, lower values will cause the queue to advance quicker
		queueStepDelayTimer = 500;
		queueStepDelay = queueStepDelayTimer;
		// the number of 'empty' steps to take before adding a block to the queue
		queueStepReq = 4;
		// the number of blocks that should be in the queue before forcibly adding to the grid
		queueLimit = 5;
		// disable the queue. no queue processing will be done if set to true
		queueDisabled = false;
		// disable energy use. the bar will not show and energy will not decrease during gameplay
		disableEnergy = false;
		minColors = 2;

		Global.rand.setSeed(LocalDateTime.now().getNano());

		
		Block b = null;
		int r, rx, ry;
		// TODO: finish all level grid builds
		/* The switch/case statements below are for building the level-dependent grids.
		 * Variables for blocks remaining, wedge positioning, allowed block color generation, etc.,
		 * are calculated by setGridCounts() after the grid is built.
		 */
		switch (levelSelect) {
			case 1:
				grid = new GridColumn[20];
				for (int i = 0; i < grid.length; i++) {
					grid[i] = new GridColumn(20);
					for (int k = 0; k < grid[0].blocks.length; k++) {
						//grid[i].blocks[k] = new Block(Block.BlockType.BLOCK, );
						grid[i].blocks[k] = new Block(Block.BlockType.BLOCK,
								(i % 4) | (k % 3)
							);
					}
				}
				break;
			case 2:
				// 3 colors and many bombs
				grid = new GridColumn[20];
				for (int i = 0; i < grid.length; i++) {
					grid[i] = new GridColumn(20);
					for (int k = 0; k < grid[0].blocks.length; k++) {
						r = Global.rand.nextInt(256);
						if (r > 16) { 
							b = new Block(Block.BlockType.BLOCK, Global.rand.nextInt(3));
						} else {
							b = new Block(Block.BlockType.BOMB, Global.rand.nextInt(3) + 2);
						}
						grid[i].blocks[k] = b;
					}
				}
				break;
			case 3:
				// 3 colors, no bombs
				grid = new GridColumn[20];
				for (int i = 0; i < grid.length; i++) {
					grid[i] = new GridColumn(20);
					for (int k = 0; k < grid[0].blocks.length; k++) {
						grid[i].blocks[k] = new Block(Block.BlockType.BLOCK, Global.rand.nextInt(3));
					}
				}
				break;
			case 4:
				// 3 colors (2 new)
				grid = new GridColumn[20];
				for (int i = 0; i < grid.length; i++) {
					grid[i] = new GridColumn(20);
					for (int k = 0; k < grid[0].blocks.length; k++) {
						grid[i].blocks[k] = new Block(Block.BlockType.BLOCK, Global.rand.nextInt(3) + 2);
					}
				}
				break;
			case 5:
				grid = GridColumn.loadFromFile("media/sp2.csv");
				break;
			case 6:
				// 3 colors, first show of the wedge block, with heart block
				grid = new GridColumn[20];
				for (int i = 0; i < grid.length; i++) {
					grid[i] = new GridColumn(20);
					for (int k = 0; k < grid[0].blocks.length; k++) {
						grid[i].blocks[k] = new Block(Block.BlockType.BLOCK, Global.rand.nextInt(3));
					}
				}
				grid[Global.rand.nextInt(grid.length)].blocks[Global.rand.nextInt(grid[0].blocks.length)] = new Block(Block.BlockType.HEART);
				rx = Global.rand.nextInt(10) + 5;
				ry = Global.rand.nextInt(4) + 8;
				grid[rx].blocks[ry] = new Block(Block.BlockType.WEDGE);
				break;
			case 7:
				// 3 colors, wedge, no starter heart block
				grid = new GridColumn[20];
				for (int i = 0; i < grid.length; i++) {
					grid[i] = new GridColumn(20);
					for (int k = 0; k < grid[0].blocks.length; k++) {
						b = new Block(Block.BlockType.BLOCK, Global.rand.nextInt(3));
						grid[i].blocks[k] = b;
					}
				}
				rx = Global.rand.nextInt(10) + 5;
				ry = Global.rand.nextInt(4) + 8;
				grid[rx].blocks[ry] = new Block(Block.BlockType.WEDGE);
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
				break;
			case 10:
				grid = GridColumn.loadFromFile("media/sp9.csv");
				break;
			case 11:
				
				grid = new GridColumn[20];
				for (int i = 0; i < grid.length; i++) {
					grid[i] = new GridColumn(20);
					for (int k = 0; k < grid[0].blocks.length; k++) {
						
					}
				}
				break;
			case 12:
				queueDisabled = true;
				grid = GridColumn.loadFromFile("2-hit-go.dat");
				break;
			case 15:
				grid = GridColumn.loadFromFile("media/sp7.csv");
				break;
			case 20:
				grid = GridColumn.loadFromFile("media/sp4.csv");
				break;
			default:
				return;
		}
		gridBasePos = new int[] { 20, Global.glEnvHeight - blockSize[1] - 50 };
		cursorGridPos[0] = grid.length / 2;
		cursorGridPos[1] = grid[0].blocks.length / 2;
		queue = new Block[grid.length];
		setGridCounts();
	}
	
	public void run() {
		// decrement input delay variables
		actionDelay -= Global.delta;
		inputDelay -= Global.delta;
		
		background.draw(0, 0);
		
		if (blocksRemaining == 0) {
			levelComplete = true;
			if (!endLevelDelayed) {
				endLevelDelayed = true;
				pauseCursorPos = 0;
				score += energy >> 6;
				energy = 0;
				inputDelay = Global.inputReadDelayTimer;
			}
		} else if (energy == 0 && !gameOver && !disableEnergy) {
			// game over
			gameOver = true;
			pauseCursorPos = 0;
		}
		
		if (!gamePaused && !gameOver && !levelComplete) {
			// process active gameplay
			queueManualShiftDelay -= Global.delta;
			gridShiftActionDelay -= Global.delta;
			if (!disableEnergy) { 
				energy -= Global.delta; 
				if (energy < 0) { energy = 0; }
				else if (energy > energyMax) { energy = energyMax; }
			}
			processQueue();
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
	
	protected void drawCursor() {
		cursor.draw(
				gridBasePos[0] + blockSize[0] * cursorGridPos[0],
				gridBasePos[1] - blockSize[1] * cursorGridPos[1],
				blockSize
			);
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

	/**
	 * @author John 
	 */
	private final int checkGrid(int xc, int yc, final int colorID) {
		int sum = 0;
		if (grid[xc].blocks[yc] == null || grid[xc].blocks[yc].checked) {
			return 0;
		}
		grid[xc].blocks[yc].checked = true;
		
		if (grid[xc].blocks[yc].dropDistance != 0) { return 0; }
		if (grid[xc].columnOffset != 0) { return 0; }
		if (grid[xc].blocks[yc].colorID != colorID) { return 0; }
		if (grid[xc].blocks[yc].type != Block.BlockType.BLOCK) { return 0; }
		
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
				int change;
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
	protected Sprite getNumber(char c) {
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
	 * @author John
	 */
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
			levelFinishedControls();
			Global.uiBlue.draw(387, 250, 250, 250);
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
			}
		} else if (gamePaused) {
			/** @author Brock */
			pauseControls();

			overlay.draw(0, 0);
			Global.uiBlue.draw(387, 250, 250, 250);
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
			}
		} else if (gameOver) {
			drawGrid(grid);
			showGameOver();
		}
	}
	
	protected void showGameOver() {
		overlay.draw(0, 0);
		gameOverControls();
		
		Color.lightGray.bind();
		Global.uiWhite.draw(256, 192, 512, 384);
		Color.blue.bind();
		Global.uiWhite.draw(288, 224, 192, 48); // left button
		Global.uiWhite.draw(546, 224, 192, 48); // right button
		Color.white.bind();
		Global.uiWhite.draw(288, 288, 452, 192);
		
		if (pauseCursorPos == 0) {
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
		}
	}

	/**
	 * Moves and processes grid blocks.
	 * @param grid
	 * @author John
	 */
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
					if (!Global.useBlockCascading) { gc.blocks[y].dropDistance += blockMoveRate; }
					
					if (gc.blocks[y-1] == null) { // space below is empty
						// set block as moving. this value will be reset if the block cannot fall.
						if (Global.useBlockCascading) { gc.blocks[y].dropDistance += blockMoveRate; }
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
				if (Global.useBlockCascading && grid[next].blocks[0] != null) { // cascading forces each column to wait for the next to be empty
					grid[xc].columnOffset = 0;
					continue; 
				} 
				if (grid[xc].blocks[0].type == Block.BlockType.ROCK) { // rock in current column, do no shift 
					grid[xc].columnOffset = 0;
					continue; 
				} 
				if (grid[next].blocks[0] != null && grid[next].columnOffset < grid[xc].columnOffset) { // no room to move or trying to move ahead (this should also match next column rock blocks) 
					grid[xc].columnOffset = grid[next].columnOffset;
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
				if (Global.useBlockCascading && grid[next].blocks[0] != null) { // cascading forces each column to wait for the next to be empty 
					grid[xc].columnOffset = 0;
					continue; 
				} 
				if (grid[xc].blocks[0].type == Block.BlockType.ROCK) { // rock in current column, do no shift 
					grid[xc].columnOffset = 0;
					continue; 
				} 
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
		int clears = 0;
		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[0].blocks.length; y++) {
				if (grid[x].blocks[y] != null && grid[x].blocks[y].type == Block.BlockType.STAR) {
					// TODO: add activation call for star blocks found sharing an edge
					// if (grid[x].blocks[y].clearMark) { continue; } // block has already been processed
					if ( (x + 1) < grid.length && grid[x+1].blocks[y] != null && grid[x+1].blocks[y].type == Block.BlockType.STAR) { 
						clears += activateStarBlock(new int[] { x, y }, true);
					} else 
					if (x > 0 && grid[x-1].blocks[y] != null && grid[x-1].blocks[y].type == Block.BlockType.STAR) { 
						clears += activateStarBlock(new int[] { x, y }, true);
					} else 
					if ( (y + 1) < grid[x].blocks.length && grid[x].blocks[y+1] != null && grid[x].blocks[y+1].type == Block.BlockType.STAR) {
						clears += activateStarBlock(new int[] { x, y }, true);
					} else
					if (y > 0 && grid[x].blocks[y-1] != null && grid[x].blocks[y-1].type == Block.BlockType.STAR) {
						clears += activateStarBlock(new int[] { x, y }, true);
					}
				}
			}
		}
		if (clears > 0) {
			removeMarkedBlocks();
		}
		
	}

	/** Draws the grid to the screen. Calculates block offsets used by the updated
	 *  grid management algorithm.
	 * @param grid
	 * @author John
	 */
	protected void drawGrid(GridColumn[] grid) {
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

	protected int activateStarBlock(int[] pos, boolean eventActivation) {
		if (grid[pos[0]].blocks[pos[1]] == null) { return 0; }
		if (!eventActivation && pos[1] > 0) { return 0; } // manual activation requires the star to be at the bottom row
		int count = 1;
		int xMin = (pos[0] - 1) < 0 ? 0 : pos[0] - 1;
		int xMax = (pos[0] + 1) >= grid.length ? grid.length - 1 : pos[0] + 1;
		int yMin = (pos[1] - 1) < 0 ? 0 : pos[1] - 1;
		int yMax = (pos[1] + 1) >= grid[0].blocks.length ? grid[0].blocks.length - 1 : pos[1] + 1;
		grid[pos[0]].blocks[pos[1]].clearMark = true;
		for (int xx = xMin; xx <= xMax; xx++) {
			for (int yy = yMin; yy <= yMax; yy++) {
				if (grid[xx].blocks[yy] == null) { continue; }
				if (grid[xx].blocks[yy].type == Block.BlockType.ROCK) { continue; }
				if (grid[xx].blocks[yy].clearMark) { continue; }
				if (grid[xx].blocks[yy].type == Block.BlockType.STAR) {
					count += activateStarBlock(new int[] { xx, yy }, true);
					continue;
				}
				if (grid[xx].blocks[yy].type == Block.BlockType.BOMB) {
					count += activateBombBlock(new int[] { xx, yy });
					continue;
				}
				// no exception check for wedge blocks. star blocks can remove wedge blocks, though bombs cannot
				grid[xx].blocks[yy].clearMark = true;
				count++;
			}
		}
		return count;
	}
	
	/** @author Brock */
	protected void pauseControls() {
		if (inputDelay <= 0) {
			if (Global.getControlActive(Global.GameControl.UP)) {
				pauseCursorPos--;
				if (pauseCursorPos < 0) {
						pauseCursorPos = 1;
				}
				inputDelay = Global.inputReadDelayTimer * 2;
			}
			if (Global.getControlActive(Global.GameControl.DOWN)) {
				pauseCursorPos++;
				if (pauseCursorPos > 1) {
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
	
	/** @author Mario */
	protected void gameOverControls() {
		if (inputDelay <= 0) {
			if (Global.getControlActive(Global.GameControl.LEFT) || Global.getControlActive(Global.GameControl.DOWN)) {
				pauseCursorPos = pauseCursorPos == 0 ? 1 : 0;
				inputDelay = Global.inputReadDelayTimer * 2;
			}
			if (Global.getControlActive(Global.GameControl.RIGHT) || Global.getControlActive(Global.GameControl.DOWN)) {
				pauseCursorPos = pauseCursorPos == 0 ? 1 : 0;
				inputDelay = Global.inputReadDelayTimer * 2;
			}
			if (Global.getControlActive(Global.GameControl.SELECT)) {
				switch (pauseCursorPos) {
					case 0:
						gameOver = false;
						energy = energyMax;
						score = score/2;
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
	protected void levelFinishedControls() {
		if (inputDelay <= 0) {
			if (Global.getControlActive(Global.GameControl.UP)) {
				pauseCursorPos--;
				if (pauseCursorPos < 0) {
					pauseCursorPos = 1;
				}
				inputDelay = Global.inputReadDelayTimer * 2;
			}
			if (Global.getControlActive(Global.GameControl.DOWN)) {
				pauseCursorPos++;
				if (pauseCursorPos > 1) {
					pauseCursorPos = 0;
				}
				inputDelay = Global.inputReadDelayTimer * 2;
			}
			if (Global.getControlActive(Global.GameControl.SELECT)) {
				switch (pauseCursorPos) {
					case 0:
						levelFinished = true;
						gameOver = false;
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
					shiftQueue(-1);
					queueManualShiftDelay = queueManualShiftDelayTimer;
				} else if (Global.getControlActive(Global.GameControl.RIGHT)) {
					// shift queue right
					shiftQueue(1);
					queueManualShiftDelay = queueManualShiftDelayTimer;
				} else if (Global.getControlActive(Global.GameControl.DOWN)) {
					// drop (add to grid) queue
					int overflow = addToGrid();
					updateScore( overflow * -10 );
					queueManualShiftDelay = queueManualShiftDelayTimer;
				}
			}
		} else {
			queueHold = false;
			// cursor control
			if (Global.getControlActive(Global.GameControl.SPECIAL1) && gridShiftActionDelay <= 0) {
				gridShiftActionDelay = gridShiftActionDelayTimer;
				blocksMoving = true;
				gridShiftDir *= -1;
			}
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
					if (counter > 1 
							|| grid[cursorGridPos[0]].blocks[cursorGridPos[1]].type == Block.BlockType.BOMB
							|| grid[cursorGridPos[0]].blocks[cursorGridPos[1]].type == Block.BlockType.STAR ) {
						// decrease the blocksRemaining counter after blocks are cleared
						removeMarkedBlocks();
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
					if (grid[xc].blocks[yc].type == Block.BlockType.BLOCK) {
						int cid = grid[xc].blocks[yc].colorID;
						blockCounts[cid]--;
						if (blockCounts[cid] == 0 && totalColors > minColors) {
							totalColors--;
							removeFromQueue(cid, Block.BlockType.STAR);
							allowedColors = allowedColors ^ (1 << cid);
						}
					}
					grid[xc].blocks[yc] = null;
					blocksRemaining--;
				}
			}
		}
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
					if (queue[x].type == Block.BlockType.BLOCK) {
						blockCounts[queue[x].colorID]++;
					}
				} else {
					overflow++;
				}
				queue[x] = null;
			}
		}
		queueCount = 0;
		return overflow;
	}

	/**
	 * Generates a new <code>Block</code> object that is either a special block
	 * or a normal block with an allowed color for the current state of the gameplay
	 * @author John
	 */
	private Block getQueueBlock() {
		Block b = null;
		int r;
		r = Global.rand.nextInt(10000);
		if (r < heartGenChance) {
			b = new Block(Block.BlockType.HEART);
		} else if (r < (heartGenChance + bombGenChance)) {
			b = new Block(Block.BlockType.BOMB, Global.rand.nextInt(4)); // 75% chance for size 2 bomb, 25% size 3
		} else {
			int[] list = new int[Block.blockColorCount];
			int bsc, count = 0;
			for (int i = 0; i < list.length; i++) {
				bsc = 1 << i;
				if ( (allowedColors & bsc) == bsc) {
					list[count] = i;
					count++;
				}
			}
			b = new Block(Block.BlockType.BLOCK, list[Global.rand.nextInt(count)]);
		}
		return b;
	}

	/**
	 * @author John
	 */
	protected int processActivate() {
		int counter = 0;
		switch (grid[cursorGridPos[0]].blocks[cursorGridPos[1]].type) {
			case BLOCK:
				counter = checkGrid(cursorGridPos);
				int adj = (int)Math.pow(counter - 1, 2);
				updateScore(adj);
				addEnergy(adj);
				break;
			case BOMB:
				counter = activateBombBlock(cursorGridPos);
				updateScore(counter);
				addEnergy(counter);
				break;
			case HEART:
				heartSpecialActive = true;
				actionDelay = Global.inputReadDelayTimer * 3;
				addEnergy(energyMax / 10); // regenerate 10% of max energy on use
				break;
			case STAR:
				counter = activateStarBlock(cursorGridPos, false);
				if (counter > 0) {
					actionDelay = Global.inputReadDelayTimer;
					updateScore(50);
				}
				break;
			default: // block does not activate, do nothing
				break;
		}
		return counter;
	}

	/**
	 * Shifts the <code>Block</code> queue across the screen 
	 * @param direction 1 to shift the queue to the right, else shift to the left
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
		if (queueDisabled) { return; }
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
	 * Removes all instances of a specific block color from the queue
	 * @param block The matching block color to be removed.
	 * @author John
	 */
	protected void removeFromQueue(int color) {
		if (queueDisabled) { return; }
		for (int i = 0; i < queue.length; i++) {
			if (queue[i] != null && queue[i].type == Block.BlockType.BLOCK && queue[i].colorID == color) {
				queue[i] = null;
				queueCount--;
			}
		}
	}
	
	/**
	 * Removes all instance of a specific block color from the queue. 
	 * Blocks removed in this way are replaced with a new <code>Block</code> of type <code>replaceType</code>.
	 * @param color
	 * @param replaceType
	 */
	protected void removeFromQueue(int color, Block.BlockType replaceType) {
		if (queueDisabled) { return; }
		for (int i = 0; i < queue.length; i++) {
			if (queue[i] != null && queue[i].type == Block.BlockType.BLOCK && queue[i].colorID == color) {
				queue[i] = new Block(replaceType);
			}
		}
	}

	/**
	 * Draw the queue across the screen above the grid
	 * @author John
	 */
	protected void drawQueue() {
		if (queueDisabled) { return; }
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
		if (disableEnergy) { return; }
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

		int cornerRadius = radius / 3;
		if (cornerRadius <= 0) { cornerRadius = 1; }
		int count = 0;
		int dist;
		int flex = radius / 2;
		if (xMin < 0) { xMin = 0; }
		if (xMax >= grid.length) { xMax = grid.length - 1; }
		if (yMin < 0) { yMin = 0; }
		if (yMax >= grid[0].blocks.length) { yMax = grid[0].blocks.length - 1; }
		// mark center bomb as cleared to prevent recursive calls to already activated bomb blocks
		grid[pos[0]].blocks[pos[1]].clearMark = true;
		count++;
		for (int i = xMin; i <= xMax; i++) {
			for (int k = yMin; k <= yMax; k++) {
				dist = Math.abs(i - pos[0]) + Math.abs(k - pos[1]) - flex;
				if (dist > radius) { continue; }
				if (grid[i].blocks[k] != null && !grid[i].blocks[k].clearMark) {
					if (grid[i].blocks[k].type == Block.BlockType.BOMB) {
						count += activateBombBlock( new int[] { i, k } );
					} else if (grid[i].blocks[k].type == Block.BlockType.STAR) {
						count += activateStarBlock(new int[] {i, k }, true);
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
		overlay.draw(0, 0);
		for (int j = 0; j < heartMenuBlocks.length; j++) {
			heartMenuBlocks[j].draw(100 + j * 32, 100);
			cursor.draw(100 + heartCursorPos * 32, 100);
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
				heartSpecialActive = true;
				actionDelay = Global.inputReadDelayTimer * 2;
	
			}
			if (Global.getControlActive(Global.GameControl.SELECT)) {
				switch(heartCursorPos) {
					case 0:
						heartSelectColor = 0;
						break;
					case 1:
						heartSelectColor = 1;
						break;
					case 2:
						heartSelectColor = 2;
						break;
					case 3:
						heartSelectColor = 3;
						break;
					case 4:
						heartSelectColor = 4;
						break;
					case 5:
						heartSelectColor = 5;
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
		
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].blocks.length; j++) {
				if (grid[i].blocks[j] == null) {
					continue;
				}
				if (grid[i].blocks[j].colorID == heartSelectColor && grid[i].blocks[j].type == Block.BlockType.BLOCK) {
					grid[i].blocks[j].clearMark = true;
					count++;
				} else {
					continue;
				}
			}
		}
		removeFromQueue(heartSelectColor);
		if (count <= 0) {
			clearColor = false;
			return 0;
		} else {
			return count;
		}
	}
	
	protected void setGridCounts() {
		
		for (int i = 0; i < blockCounts.length; i++) {
			blockCounts[i] = 0;
		}
		blocksRemaining = grid.length * grid[0].blocks.length;

		for (int x = 0; x < grid.length; x++) {
			for (int y = 0; y < grid[0].blocks.length; y++) {
				if (grid[x].blocks[y] != null) {
					if (grid[x].blocks[y].type == Block.BlockType.BLOCK) {
						blockCounts[grid[x].blocks[y].colorID]++;
					} else if (grid[x].blocks[y].type == Block.BlockType.WEDGE) {
						if (wedgePos[0] >= 0 && !(wedgePos[0] == x && wedgePos[1] == y )) {
							Global.writeToLog("Too many wedge blocks! Additional wedges will be converted to TRASH type.", true);
							grid[x].blocks[y] = new Block(Block.BlockType.TRASH);
						} else {
							wedgePos = new int[] { x, y };
							blocksRemaining--;
						}
					} else if (grid[x].blocks[y].type == Block.BlockType.ROCK) {
						blocksRemaining--;
					}
				}
			}
		}
		
		for (int i = 0; i < blockCounts.length; i++) {
			if (blockCounts[i] > 0) {
				allowedColors = allowedColors | (1 << i);
				totalColors++;
			}
		}
		
		
	}

}
