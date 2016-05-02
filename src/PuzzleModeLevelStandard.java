import java.time.LocalDateTime;
import java.util.HashMap;

import org.newdawn.slick.opengl.Texture;

public class PuzzleModeLevelStandard extends PuzzleModeLevel {
	public PuzzleModeLevelStandard() {
		blockSize = new int[] { 32, 32 }; // default block size is { 32, 32 }
		gridSize = new int[] { 20, 20 }; // default grid size is { 20, 20 }
		grid = new GridColumn[gridSize[0]];
		buildGrid();
		gridBasePos = new int[] { 375 + 82, 700 };
	}
	
	@Override
	protected void buildGrid() {
		
		int r = 0;
		Global.rand.setSeed(LocalDateTime.now().getNano());
		for (int i = 0; i < grid.length; i++) {
			grid[i] = new GridColumn(gridSize[1]);
			for (int k = 0; k < grid[0].blocks.length; k++) {
				// TODO: [CUSTOM] define the randomly generated blocks rate of appearance
				r = 1;//Global.rand.nextInt(2);
				
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
