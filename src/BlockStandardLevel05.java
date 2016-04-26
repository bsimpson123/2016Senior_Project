import java.util.HashMap;
import java.io.*;

import org.newdawn.slick.opengl.Texture;
/**
 * Template level for simplify level-building.
 * Copy the code here into a new BlockStandardLevel extended class and edit
 * where necessary to set level difficulty. 
 * @author John
 */
public class BlockStandardLevel05 extends BlockStandardLevel {
	private  BufferedReader infile;
	
	public BlockStandardLevel05(HashMap<String,Texture> rootTexMap) {
		
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
		
		
	}
	
	@Override
	protected void buildGrid() {
		try {
			String parseline;
			String[] parseCSV;
			infile = new BufferedReader(new FileReader("media/sp2.csv"));
			parseline = infile.readLine();
			int x=0;
			while (parseline!= null) {
				parseCSV = parseline.split(",");
				grid[x] = new GridColumn(gridSize[1]);
				for (int i = 0; i < parseCSV.length; i++) {
					
					grid[x].blocks[i] = new Block(Block.BlockType.BLOCK, Integer.parseInt(parseCSV[i]));
				}
				x++;
				parseline = infile.readLine();
			}
			
			infile.close();
			//outfile.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// set the block count for the level
		blocksRemaining = grid.length * grid[0].blocks.length;
		// TODO: [CUSTOM] add any custom/special blocks that have limited generation (rocks, trash, wedge, etc.)
		// remember to decrease blocksRemaining for each such block added 
		
	}
	
	@Override
	protected Block getQueueBlock() {
		Block b = null;
		// TODO: [CUSTOM] define the type and rate of blocks that are added to the grid via the queue
		b = new Block(Block.BlockType.BLOCK, Global.rand.nextInt(3));

		return b;		
	}

	@Override
	protected void processActivate() {
		// TODO: score base value calculation is to be done within each case statement
		// [CUSTOM] add case statements for each type of block that can be activated in the level
		switch (grid[cursorGridPos[0]].blocks[cursorGridPos[1]].type) {
			case BLOCK:
				counter = checkGrid(cursorGridPos);
				int adj = (int)Math.pow(counter - 1, 2);
				updateScore(adj);
				addEnergy(adj);
				break;
			default: // block does not activate, do nothing
				break;
		}
		
	}


}
