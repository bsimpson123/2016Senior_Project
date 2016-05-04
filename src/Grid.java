
public class Grid {
	// grid property variables
	private GridColumn[] grid;
	private int[] gridSize;
	private int[] blockSize;
	private int blocksRemaining = 0;
	
	// queue variables
	private Block[] queue;
	private boolean queueDisabled = false;
	private long queueStepDelayTimer = 500l;
	private long queueStepDelay = queueStepDelayTimer;
	private int queueStepReq = 4;
	private int queueStepCount = 0;
	private int queueCount = 0;
	private int queueLimit = 5;
	
	
	public Grid(int[] dimensions) {
		
	}
	
	public Grid(String sourceFile) {
		
	}
	
	public void saveGridTo(String filename) {
		
	}
	
	private void setGridCounts() {
		
	}
	
	private void removeMarkedBlocks() {
		
	}
	
	private int addToGrid(Block[] line) {
		int overflow = 0;
		
		
		return overflow;
	}
	
	private void draw(int[] gridBasePos) {
		
	}
	
	public int checkGrid(int[] xy) {
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
		
		if (grid[xc].blocks[yc].dropDistance != 0) { return 0; }
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
}
