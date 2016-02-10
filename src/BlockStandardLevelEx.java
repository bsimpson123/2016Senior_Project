import java.util.HashMap;
import java.util.Random;

import org.newdawn.slick.opengl.Texture;

/**
 * Example level to demonstrate the flow and references need to build a level
 * @author John
 */
public class BlockStandardLevelEx extends BlockStandardLevel {
	/** Defines the width and height of the blocks in the grid. */
	private final int[] blockDimL1; // block dimensions Level 1
	private int gridShift = 1;
	private boolean specialActive = false;
	private final int[] gridBasePos;
	private Random rand; // used to shorten the code for calling the randomizer
	private final int queueStepsUntilAdd = 4; // time in ms between each block being added to the queue
	private int queueStepsRemaining = queueStepsUntilAdd;
	private final long queueStepTimer = 750l; // time delay in ms between each step shift in the queue
	private long queueStepDelay = queueStepTimer * 2; // initial delay is 2x longer than regular delay
	private int blocksInQueue = 0;
	private int queueBlockLimit = 5; // number of blocks that can be in queue until forced into grid

	public BlockStandardLevelEx(HashMap<String,Texture> rootTex) {
		rand = Global.rand;
		// set the score multiplier for the level when 
		levelMultiplier = 1.5f;
		// Set environment textures and variables
		background = new Sprite(
				rootTex.get("bg_stdmode_wood1"),
				new int[] { 0, 0 },
				new int[] { 1024, 768 },
				new int[] { Global.glEnvWidth, Global.glEnvHeight }
			);
		userInterface = new Sprite(
				rootTex.get("ui_stdmode"),
				new int[] { 0, 0 },
				new int[] { 1024, 768 },
				new int[] { Global.glEnvWidth, Global.glEnvHeight }
			);
		// this value can be { 16, 16 } or { 8, 8 } to reduce the size of the blocks
		// but the grid size should be increased proportionately
		blockDimL1 = new int[] { 32, 32 };
		grid = new Block[20][20];
		// gridQueue should be the same size as the first grid dimension
		gridQueue = new Block[20];
		buildGrid();
		gridBasePos = new int[] { 20, Global.glEnvHeight - blockDimL1[1] - 50 };
		// set the cursor starting position in the center of the grid
		cursorGridPos[0] = grid.length / 2;
		cursorGridPos[1] = grid[0].length / 2;
	}
	
	@Override
	protected void buildGrid() {
		Block b = null;
		int r = 0;
		for (int i = 0; i < grid.length; i++) {
			for (int k = 0; k < grid[0].length; k++) {
				r = Global.rand.nextInt(10000);
				if (r > 20) { 
					b = new Block(Block.BlockType.BLOCK, rand.nextInt(3));
				} else {
					b = new Block(Block.BlockType.STAR);
				}
				grid[i][k] = b;
			}
		}
	}
	
	@Override
	public void run() {
		/* Draw all background elements. These should always be the first items drawn to screen. */
		background.draw(0, 0);
		int counter = 0;
		
		// draw the grid and handle grid mechanics and input if the game is not paused
		if (!gamePaused) {
			// draw the grid, return value indicates if there are blocks still falling from the last clear
			boolean blocksFalling = drawGrid(blockDimL1, 20);
			drawQueue(blockDimL1);
			cursor.draw(
				// for pointer at center of block
/*				gridBasePos[0] + blockOffSet[0] * cursorGridPos[0] - blockOffSet[0]/2,
				gridBasePos[1] - blockOffSet[1] * cursorGridPos[1] + blockOffSet[1]/2 //*/
				// for selector surrounding block
				gridBasePos[0] + blockDimL1[0] * cursorGridPos[0],
				gridBasePos[1] - blockDimL1[1] * cursorGridPos[1],
				blockDimL1
			);
		

			// process left,right,up,down movement in the grid or special item area
			if (specialActive) {
				// if a special item or event has moved the selector cursor, handle that here
				; 
			} else {
				processQueue();
				if (inputDelay <= 0l) {
					checkGridMovement();
				} else {
					inputDelay -= Global.delta;
				}
			}
			if (Global.getControlActive(Global.GameControl.CANCEL)) {
				levelFinished = true;
			}
			if (inputDelay <= 0) {
				if (!blocksFalling && Global.getControlActive(Global.GameControl.SELECT) &&
						grid[cursorGridPos[0]][cursorGridPos[1]] != null) {
					counter = 0;
					// TODO: score calculation is to be done within each case statement in line
					// with the value of each 
					switch (grid[cursorGridPos[0]][cursorGridPos[1]].type) {
						case BLOCK:
							counter = checkGrid(grid, cursorGridPos);
							score += (int)Math.floor(Math.pow(counter - 1, 2) * levelMultiplier);
							break;
						case STAR:
							if (cursorGridPos[1] > 0) { break; }
							int lRange = cursorGridPos[0] == 0 ? 0 : cursorGridPos[0] - 1;
							int uRange = cursorGridPos[0] == grid.length - 1 ? grid.length : cursorGridPos[0] + 2;
							for (int i = lRange; i < uRange; i++) {
								for (int k = 0; k < 2; k++) {
									if (grid[i][k] != null) {
										grid[i][k].clearMark = true;
										counter++;
									}
								}
							}
							score += counter * 5;
							break;
						default:
							break;
					}
					if (counter > 1) {
						blocksRemaining -= counter;
						if (blocksRemaining == 0) {
							// end game code or setup
						} else {
							// remove blocks marked to be cleared
							shiftGrid();
						}
						// input delay is only increased if an action was performed and the grid was changed
						inputDelay = Global.inputReadDelayTimer;
						
					}
				}
			} else {
				inputDelay -= Global.delta;
			}
		}
		
		// draw the top-level UI frame, score and other elements
		drawTopLevelUI();
		
		if (gamePaused) {
			// draw the pause menu and handle input appropriately
		}

	}
	
	private void processQueue() {
		queueStepDelay -= Global.delta;
		if (queueStepDelay > 0) { return ; }
		queueStepDelay += queueStepTimer;
		if (gridQueue[0] == null && blocksInQueue > 0) {
			for (int i = 0; i < gridQueue.length - 1; i++) {
				gridQueue[i] = gridQueue[i + 1]; // shift blocks left by one space
				gridQueue[i + 1] = null;
			}
		}
		if (blocksInQueue == queueBlockLimit) {
			blocksInQueue = addToGrid(gridQueue);
			if (blocksInQueue > 0) {
				// shuffle the  queue
			}
		}
		queueStepsRemaining--;
		if (queueStepsRemaining > 0) { return ; }
		Block b;
		int r = rand.nextInt(10000);
		if (r > 20) {
			b = new Block(Block.BlockType.BLOCK, rand.nextInt(3));
		} else {
			b = new Block(Block.BlockType.STAR);
		}
		blocksInQueue++;
		gridQueue[gridQueue.length - 1] = b;
		queueStepsRemaining = queueStepsUntilAdd;
		return;
	}
	
	@Override
	protected void shiftGrid() {
		for (int i = 0; i < grid.length; i++) {
			int slotDist = 0;
			int dropDist = 0;
			for (int k = 0; k < grid[0].length; k++) {
				if (grid[i][k] != null && grid[i][k].clearMark) {
					grid[i][k] = null;
				} 
			}
			for (int k = 0; k < grid[0].length; k++) {
				if (grid[i][k] == null) {
					dropDist += blockDimL1[1];
					slotDist++;
				} else if (dropDist > 0) {
					grid[i][k].dropDistance = dropDist;
					grid[i][k-slotDist] = grid[i][k];
					grid[i][k] = null;
				}
			}
		}
		Block[] emptyset = new Block[grid[0].length];
		// shift the grid to the right
		if (gridShift == 1) {
			for (int i = grid.length - 1; i > 0; i--) {
				if (grid[i][0] == null) {
					for (int k = i - 1; k >= 0; k--) {
						if (grid[k][0] != null) {
							grid[i] = grid[k];
							grid[k] = emptyset;
							break;
						}
					}
				}
			}
		}

	}

}
