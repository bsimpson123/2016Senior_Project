
public abstract class BlockStandardLevel {
	protected Block[][] grid = new Block[20][20]; // [x][y], [c][r]
	protected static Sprite cursor;
	protected int[] cursorGridPos = new int[] { 0, 0 };
	protected static int score;
	protected int blocksRemaining = 0;

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
	
	/**
	 * Generates a new grid layout based on specified blocks and the weights of each block occurring.
	 * @param elements A list of Block.BlockTypes to generate for the grid
	 * @param weights A list of the weighted chance that each block type will occur within 1000.
	 * This list should be the same size as elements, and the sum of the values should add to 1000.
	 * @param colorRange For BlockType.BLOCK, this is the lower and upper bounds of the colorID to be used.
	 */
	protected void buildGrid(Block.BlockType[] elements, int[] weights, int[] colorRange) {
		int[] localWeights = new int[weights.length];
		localWeights[0]--;
		for (int i = 1; i < weights.length; i++) {
			localWeights[i] += weights[i-1];
		}
		Block b;
		int r;
		for (int i = 0; i < grid.length; i++) {
			for (int k = 0; k < grid[0].length; k++) {
				b = null;
				r = Global.rand.nextInt(1000);
				for (int x = 0; x < localWeights.length; x++) {
					if (r < localWeights[x]) {
						if (elements[x] == Block.BlockType.BLOCK) {
							b = new Block(elements[x], Global.rand.nextInt(colorRange[1]) + colorRange[0]);
						} else {
							b = new Block(elements[x]);
						}
					}
				}
				grid[i][k] = b;
				blocksRemaining++;
			}
		}
	}
}
