import java.time.LocalDateTime;
import java.util.HashMap;

import org.newdawn.slick.opengl.Texture;

public class PuzzleModeLevel01 extends PuzzleModeLevel{

	//int totalMoves = 6;
	public PuzzleModeLevel01(HashMap<String,Texture> rootTexMap) {
		level = 2;
		
		/**
		 * Change totalMoves for number of moves to clear the grid.
		 */
		totalClears = 3;
		
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
		
		
	}
	
	
	@Override
	protected void buildGrid() {
		
		int r = 0;
		Global.rand.setSeed(LocalDateTime.now().getNano());
		for (int i = 0; i < grid.length; i++) {
			grid[i] = new GridColumn(gridSize[1]);
			for (int k = 0; k < grid[0].blocks.length; k++) {
				// TODO: [CUSTOM] define the randomly generated blocks rate of appearance
				//r = Global.rand.nextInt(2);
				if (i % 2 == 0 && k % 2 == 0) {
					r = 1;
				} else {
					r = 2;
				}
				grid[i].blocks[k] = new Block(Block.BlockType.BLOCK, r);
			}
		}
		// set the block count for the level
		blocksRemaining = grid.length * grid[0].blocks.length;
		// TODO: [CUSTOM] add any custom/special blocks that have limited generation (rocks, trash, wedge, etc.)
		// remember to decrease blocksRemaining for each such block added
		//grid[4].blocks[Global.rand.nextInt(20)] = new Block(Block.BlockType.HEART);
		//grid[16].blocks[Global.rand.nextInt(20)] = new Block(Block.BlockType.HEART);
	}
}
