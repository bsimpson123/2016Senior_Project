/**
 * This class serves as the base class for all Block Breaker Standard mode levels,
 * and defines and abstracts many of the functions that many level design simpler.
 * @author John Ojala
 */
public abstract class BlockStandardLevel {
	protected static Sprite[] numbers = new Sprite[10]; 
	protected static int score;
	protected static Sprite pauseCursor;
	protected static Sprite cursor;

	private static int scoreDisplay = 0;
	private static int change = 0;
	private static long scoreUpdateDelayTimer = 50l;
	private static long scoreUpdateDelay = scoreUpdateDelayTimer;

	protected Sprite levelDisplay;
	protected Sprite background;
	protected Sprite userInterface;

	//protected Block[][] grid; // = new Block[20][20]; // [x][y], [c][r]
	protected GridColumn[] grid;
	protected int[] gridSize;
	// grid shifting variables
	protected boolean gridShiftActive = false;
	protected boolean blockDropActive = false;
	protected boolean gridMoving = false;
	protected int gridShiftDir = 1;

	
	protected int[] cursorGridPos = new int[] { 0, 0 };
	protected int blocksRemaining = 0;
	protected boolean gamePaused = false;
	protected long inputDelay = 0;
	protected int level = 1;
	protected float levelMultiplier = 1.0f;

	public boolean levelFinished = false;
	public boolean gameOver = false;
	
	public abstract void run();

	/**
	 * Checks the grid for blocks of the same color sharing edges, and marks those blocks
	 * for removal.
	 * @param grid The 2-dimensional grid of blocks
	 * @param xy 2-element array containing the starting index locations for the search
	 * @return The total number of blocks found
	 */
	protected final int checkGrid(int[] xy) {
		if (grid[xy[0]].blocks[xy[1]] == null) { return 0; }
		if (grid[xy[0]].blocks[xy[1]].type != Block.BlockType.BLOCK) { return 0; }
		return checkGrid(xy[0], xy[1], grid[xy[0]].blocks[xy[1]].colorID);
	}

	private final int checkGrid(int xc, int yc, final int colorID) {
		int sum = 0;
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

	protected void drawScore() {
		if (score > 0) {
			scoreUpdateDelay -= Global.delta;
			if (scoreUpdateDelay <= 0 && score != scoreDisplay) {
				if (scoreDisplay < score) { // most common case, score is increasing
					change = (score - scoreDisplay) >> 2;
					if (change == 0) { change = 4; }
					scoreDisplay += change;
					if (scoreDisplay > score) { scoreDisplay = score; }
				}
				scoreUpdateDelay = scoreUpdateDelayTimer;
			}
		} else { 
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
	
	protected void drawTopLevelUI() {
		Global.uiRed.draw(700, 16, 300, 56);
		Global.uiBlue.draw(700, 72, 300, 96);
		userInterface.draw(0,0);
		char[] lvl = Integer.toString(level).toCharArray();
		int offsetX = 860;
		int yPos = 16;
		int[] numResize = new int[] { 30, 40 };
		for (int i = 0; i < lvl.length; i++) {
			getNumber(lvl[i]).draw(offsetX, yPos, numResize);
			offsetX += 24;
		}
		drawScore();
		
	}
	
	protected abstract void buildGrid(); 

	/**
	 * 
	 * @param blockSize The size of the blocks being drawn
	 * @param shiftRate The drop rate in pixels/second for falling blocks
	 * @return true if blocks are currently falling within the grid, false
	 * if no blocks are currently falling 
	 */
	protected final boolean drawGrid(int[] blockSize, int shiftRate) {
		int[] gridBasePos = new int[] { 20, Global.glEnvHeight - blockSize[1] - 50 }; // distance from the left top for the bottom-left of the grid display
		//int dropRate = 20; // millisecond time for a falling block to cover 1 space
		blockDropActive = false;
		gridShiftActive = false;
		int moveRate = (int)(Global.delta * shiftRate) / 1000;
		// Move falling blocks and render the grid
		for (int i = 0; i < grid.length; i++) {
			for (int k = 0; k < grid[0].blocks.length; k++) {
				if (grid[i].blocks[k] != null) {
					if (grid[i].blocks[k].dropDistance > 0) {
						grid[i].blocks[k].dropDistance -= moveRate;
						if (grid[i].blocks[k].dropDistance < 0) { 
							grid[i].blocks[k].dropDistance = 0; 
						} else {
							blockDropActive = true;
						}
					}
				}
			}
		}
		if (!blockDropActive) {
			for (int i = 0; i < grid.length; i++) {
				//if (grid[i].columnOffset != 0) {
					if (gridShiftDir == 1) { // right-shift
						grid[i].columnOffset += moveRate;
						if (grid[i].columnOffset >= 0) { 
							grid[i].columnOffset = 0; 
						} else {
							gridShiftActive = true;
						}
					} else { // left-shift
						grid[i].columnOffset -= moveRate;
						if (grid[i].columnOffset <= 0) {
							grid[i].columnOffset = 0;
						} else {
							gridShiftActive = true;
						}
					}
				//}
			}
		}
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
		
		return (blockDropActive || gridShiftActive);
	}
	
	/**
	 * Checks against movement inputs within the grid, and adjusts the cursor
	 * position accordingly.
	 */
	protected void checkCommonControls() {
		if (Global.getControlActive(Global.GameControl.UP)) {
			cursorGridPos[1]++;
			if (cursorGridPos[1] >= grid[0].blocks.length) {
				cursorGridPos[1] = grid[0].blocks.length - 1;
			}
			inputDelay = Global.inputReadDelayTimer;
			return;
		}
		if (Global.getControlActive(Global.GameControl.DOWN)) {
			if (cursorGridPos[1] > 0) {
				cursorGridPos[1]--;
			}
			inputDelay = Global.inputReadDelayTimer;
			return;
		}
		if (Global.getControlActive(Global.GameControl.LEFT)) {
			if (cursorGridPos[0] > 0) {
				cursorGridPos[0]--;
			}
			inputDelay = Global.inputReadDelayTimer;
			return;
		}
		if (Global.getControlActive(Global.GameControl.RIGHT)) {
			cursorGridPos[0]++;
			if (cursorGridPos[0] >= grid.length) {
				cursorGridPos[0] = grid.length - 1;
			}
			inputDelay = Global.inputReadDelayTimer;
			return;
		}
		/*// future implementation
		if (Global.getControlActive(Global.GameControl.SPECIAL)) {
			shiftDirection ^= 1;
			inputDelay = Global.inputReadDelayTimer;
			return;
		} //*/
	}

	protected final void removeMarkedBlocks() {
		for (int xc = 0; xc < grid.length; xc++) {
			for (int yc = 0; yc < grid[0].blocks.length; yc++) {
				if (grid[xc].blocks[yc] != null && grid[xc].blocks[yc].clearMark) {
					grid[xc].blocks[yc] = null;
				}
			}
		}
	}
	
	/**
	 * Calculates and sets the drop distance for remaining blocks after blocks
	 * have been cleared from the grid. This method should be overridden if special
	 * blocks in play would prevent normal block falling behavior.
	 * @param blockDimensions the height of blocks used in the level. 
	 * Used to calculate the distance blocks will be offset. 
	 */
	protected void dropBlocks(int blockDimensions) {
		int dropDist = 0;
		int slotDist = 0;
		int yMax = grid[0].blocks.length - 1;
		for (int i = 0; i < grid.length; i++) {
			slotDist = 0;
			dropDist = 0;
			for (int k = 0; k < grid[0].blocks.length; k++) {
				if (grid[i].blocks[k] == null) {
					dropDist += blockDimensions;
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
	
	
	protected void shiftGridColumns(int blockDimensions) {
		GridColumn emptyset = new GridColumn(grid[0].blocks.length);
		int colDist = 0, shiftDist = 0;
		if (gridShiftDir == 1) {
			for (int xc = grid.length - 1; xc >= 0; xc--) { // xCurrent, xPrevious
				if (grid[xc].blocks[0] == null) {
					colDist++;
					shiftDist += blockDimensions;
				} else if (shiftDist > 0) {
					grid[xc].columnOffset -= shiftDist; 
					grid[xc + colDist] = grid[xc];
					grid[xc] = emptyset.clone();
				}
			}
		} else if (gridShiftDir == -1) {
			
		}
		return ;
	}
	
	protected void switchGridShift() {
		gridShiftDir ^= 1;
		
	}
}

