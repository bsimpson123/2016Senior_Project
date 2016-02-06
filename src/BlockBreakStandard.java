import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class BlockBreakStandard implements GameMode {
	protected LoadState currentState = LoadState.NOT_LOADED;
	protected HashMap<String, Texture> localTexMap = new HashMap<String, Texture>(10);
	protected Stack<Integer> cursorPos = new Stack<Integer>();
	protected long inputDelay = Global.inputReadDelayTimer;

	// Level variables. These may be moved/removed if level play is moved to separated class object.
	protected Block[][] grid = new Block[20][20]; // [x][y], [c][r]
	protected Sprite cursor;
	protected int[] cursorGridPos = new int[] { 0, 0 };
	protected int level = 1;
	protected int counter = 0;
	protected final int maxLevel = 5;
	protected int[] blockOffSet = new int[] { 32, 32 };
	
	
	protected String[][] texLoadList = new String[][] {
		new String[] { "ui_base", "media/UIpackSheet_transparent.png" }
	};
	
	
	public BlockBreakStandard() {
		// TODO: set or load any custom environment variables
		// do not load assets at this point
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
					 System.out.printf("Attempting to load multiple textures to key [%s]", ref[0]);
					 System.out.printf("Texture resource [%s] not loaded.", ref[1]);
				 }
				 localTexMap.put(source, tex);
			 } catch (IOException e) {
				 System.out.printf("Unable to load texture resource %s\n", source);
				 e.printStackTrace();
				 System.exit(-1);
			 }
		}

		for (int i = 0; i < grid.length; i++) {
			for (int k = 0; k < grid[0].length; k++) {
				grid[i][k] = new Block( Block.BlockType.BLOCK, Global.rand.nextInt(3) );
			}
		}
		
		
		cursorGridPos[0] = grid.length / 2;
		cursorGridPos[1] = grid[0].length / 2;
/*		cursor = new Sprite(
				localTexMap.get("ui_base"),
				new int[] { 485, 341 },
				new int[] { 14, 18 },
				new int[] { 28, 36 }
			); //*/
		cursor = new Sprite(
				Global.textureMap.get("blocksheet"),
				new int[] { 240, 0 },
				new int[] { 32, 32 },
				blockOffSet
			);
		
		// Update mode state when asset loading is completed
		currentState = LoadState.LOADING_DONE;
		return;
	}

	@Override
	public LoadState getState() {
		return currentState;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		currentState = LoadState.READY;
		int[] gridBasePos = new int[] { 20, Global.glEnvHeight - blockOffSet[1] - 50 }; // distance from the left top for the bottom-left of the grid display
		int dropRate = 20; // millisecond time for a falling block to cover 1 space
		boolean blocksFalling = false;

		
		// Move falling blocks and render the grid
		for (int i = 0; i < grid.length; i++) {
			for (int k = 0; k < grid[0].length; k++) {
				if (grid[i][k] != null) {
					if (grid[i][k].dropDistance > 0) {
						grid[i][k].dropDistance -= (Global.delta * dropRate) / blockOffSet[1];
						if (grid[i][k].dropDistance < 0) { 
							grid[i][k].dropDistance = 0; 
						} else {
							blocksFalling = true;
						}
					}
					grid[i][k].draw(
							gridBasePos[0] + blockOffSet[0] * i,
							gridBasePos[1] - blockOffSet[1] * k - grid[i][k].dropDistance
						);
					grid[i][k].checked = false;
					grid[i][k].clearMark = false;
//					grid[i][k].dropDistance -= Global.delta / blockOffSet[1];
//					if (grid[i][k].dropDistance < 0f) { grid[i][k].dropDistance = 0; }
				}
			}
		}
		
		cursor.draw(
				// for pointer at center of block
/*				gridBasePos[0] + blockOffSet[0] * cursorGridPos[0] - blockOffSet[0]/2,
				gridBasePos[1] - blockOffSet[1] * cursorGridPos[1] + blockOffSet[1]/2 //*/
				// for selector surrounding block
				gridBasePos[0] + blockOffSet[0] * cursorGridPos[0],
				gridBasePos[1] - blockOffSet[1] * cursorGridPos[1]
			);
		
		
		if (inputDelay <= 0) {
			if (Global.getControlActive(Global.GameControl.CANCEL)) {
				// TODO: display exiting information in some manner
				cleanup();
				return;
			}
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
					cursorGridPos[0] --;
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
			if (!blocksFalling && Global.getControlActive(Global.GameControl.SELECT)) {
				counter = checkGrid(grid, cursorGridPos);
				if (counter > 1) {
					// TODO: score calculation
					
					// remove blocks marked to be cleared
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
								dropDist += blockOffSet[1];
								slotDist++;
							} else if (dropDist > 0) {
								grid[i][k].dropDistance = dropDist;
								grid[i][k-slotDist] = grid[i][k];
								grid[i][k] = null;
							}
						}
					}
					Block[] emptyset = new Block[grid[0].length];
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
				
				inputDelay = Global.inputReadDelayTimer;
			}
		} else {
			inputDelay -= Global.delta;
		}
		
		
	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		for (Texture ref : localTexMap.values()) {
			ref.release();
		}
		localTexMap.clear();
		
		/* Indicate that the game mode had complete unloading and is ready to
		 * return control to previous control loop.
		 */
		currentState = LoadState.FINALIZED;
	}
	
	
	
	/**
	 * Checks the grid for blocks of the same color sharing edges, and marks those blocks
	 * for removal.
	 * @param grid The 2-dimensional grid of blocks
	 * @param xy 2-element array containing the starting index locations for the search
	 * @return The total number of blocks found
	 */
	public int checkGrid(Block[][] grid, int[] xy) {
		if (grid[xy[0]][xy[1]] == null) { return 0; }
		if (grid[xy[0]][xy[1]].type != Block.BlockType.BLOCK) { return 0; }
		return checkGrid(grid, xy[0], xy[1], grid[xy[0]][xy[1]].colorID);
	}
	
	
	private int checkGrid(Block[][] grid, int xc, int yc, final int colorID) {
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
