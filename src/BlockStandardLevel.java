
public abstract class BlockStandardLevel {
	protected Block[][] grid = new Block[20][20]; // [x][y], [c][r]
	protected Sprite cursor;
	protected int[] cursorGridPos = new int[] { 0, 0 };

	public boolean levelFinished = false;
	
	public abstract void run();

	/**
	 * Checks the grid for blocks of the same color sharing edges, and marks those blocks
	 * for removal.
	 * @param grid The 2-dimensional grid of blocks
	 * @param xy 2-element array containing the starting index locations for the search
	 * @return The total number of blocks found
	 */
	public final int checkGrid(Block[][] grid, int[] xy) {
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

	
}
