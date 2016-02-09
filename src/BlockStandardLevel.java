/**
 * 
 * @author John
 */
public abstract class BlockStandardLevel {
	protected static Sprite[] numbers = new Sprite[10]; 
	protected static int score;
	protected static Sprite pauseCursor;
	protected static Sprite cursor;

	protected Sprite levelDisplay;
	protected Sprite background;
	protected Sprite userInterface;

	protected Block[][] grid; // = new Block[20][20]; // [x][y], [c][r]
	protected int[] cursorGridPos = new int[] { 0, 0 };
	protected int blocksRemaining = 0;
	protected boolean gamePaused = false;
	protected long inputDelay = 0;

	public boolean levelFinished = false;
	
	public abstract void run();

	/**
	 * Checks the grid for blocks of the same color sharing edges, and marks those blocks
	 * for removal.
	 * @param grid The 2-dimensional grid of blocks
	 * @param xy 2-element array containing the starting index locations for the search
	 * @return The total number of blocks found
	 */
	protected final int checkGrid(Block[][] grid, int[] xy) {
		if (grid[xy[0]][xy[1]] == null) { return 0; }
		if (grid[xy[0]][xy[1]].type != Block.BlockType.BLOCK) { return 0; }
		return checkGrid(grid, xy[0], xy[1], grid[xy[0]][xy[1]].colorID);
	}

	private final int checkGrid(Block[][] grid, int xc, int yc, final int colorID) {
		int sum = 0;
		if (grid[xc][yc] == null || grid[xc][yc].checked) {
			return 0;
		}
		grid[xc][yc].checked = true;
		if (grid[xc][yc].colorID != colorID) {
			return 0;
		}
		if (grid[xc][yc].type != Block.BlockType.BLOCK) {
			return 0;
		}
		grid[xc][yc].clearMark = true;
		sum = 1;
		if (xc > 0) {
			sum += checkGrid(grid, xc - 1, yc, colorID);
		}
		if (yc > 0) {
			sum += checkGrid(grid, xc, yc - 1, colorID);
		}
		if ( (xc + 1) < grid.length) {
			sum += checkGrid(grid, xc + 1, yc, colorID);
		}
		if ( (yc + 1) < grid[0].length) {
			sum += checkGrid(grid, xc, yc + 1, colorID);
		}
		
		return sum;
		
	}

	protected void drawScore() {
		
	}
	
	protected void drawTopLevelUI() {
		userInterface.draw(0,0);
		
	}
	
	protected abstract void buildGrid(); 

	protected final boolean drawGrid(int[] blockSize, int dropRate) {
		int[] gridBasePos = new int[] { 20, Global.glEnvHeight - blockSize[1] - 50 }; // distance from the left top for the bottom-left of the grid display
		//int dropRate = 20; // millisecond time for a falling block to cover 1 space
		boolean blocksFalling = false;

		// Move falling blocks and render the grid
		for (int i = 0; i < grid.length; i++) {
			for (int k = 0; k < grid[0].length; k++) {
				if (grid[i][k] != null) {
					if (grid[i][k].dropDistance > 0) {
						grid[i][k].dropDistance -= (Global.delta * dropRate) / blockSize[1];
						if (grid[i][k].dropDistance < 0) { 
							grid[i][k].dropDistance = 0; 
						} else {
							blocksFalling = true;
						}
					}
					grid[i][k].draw(
							gridBasePos[0] + blockSize[0] * i,
							gridBasePos[1] - blockSize[1] * k - grid[i][k].dropDistance
							//blockSize
						);
					grid[i][k].checked = false;
					grid[i][k].clearMark = false;
//					grid[i][k].dropDistance -= Global.delta / blockOffSet[1];
//					if (grid[i][k].dropDistance < 0f) { grid[i][k].dropDistance = 0; }
				}
			}
		}
		
		return blocksFalling;
	}
	
	protected abstract void shiftGrid();
	
	protected void checkGridMovement() {
		if (Global.getControlActive(Global.GameControl.UP)) {
			cursorGridPos[1]++;
			if (cursorGridPos[1] >= grid[0].length) {
				cursorGridPos[1] = grid[0].length - 1;
			}
			inputDelay = Global.inputReadDelayTimer;
		}
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
		}
		if (Global.getControlActive(Global.GameControl.RIGHT)) {
			cursorGridPos[0]++;
			if (cursorGridPos[0] >= grid.length) {
				cursorGridPos[0] = grid.length - 1;
			}
			inputDelay = Global.inputReadDelayTimer;
		}
	}
}

