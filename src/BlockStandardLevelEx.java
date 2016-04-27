import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Stack;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.opengl.Texture;

/**
 * Example level to demonstrate the flow and references need to build a level
 * @author John
 */
public class BlockStandardLevelEx extends BlockStandardLevel {
	private Block[] list = new Block[11];
	private Stack<GridColumn[]> undo = new Stack<GridColumn[]>();
	private boolean fillToggle = false;
	
	public BlockStandardLevelEx(HashMap<String,Texture> rootTex) {
		// set the score multiplier for the level when 
		levelMultiplier = 1.5f;
		// Set environment textures and variables
		background = new Sprite(
				rootTex.get("bg_space_1"),
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
		blockSize = new int[] { 32, 32 };
		gridSize = new int[] { 20, 20 };
		//grid = new Block[20][20];
		grid = new GridColumn[gridSize[0]];
		queue = new Block[gridSize[0]];
		buildGrid();
		gridBasePos = new int[] { 20, Global.glEnvHeight - blockSize[1] - 50 };
		// set the cursor starting position in the center of the grid
		cursorGridPos[0] = grid.length / 2;
		cursorGridPos[1] = grid[0].blocks.length / 2;
		// TODO: [CUSTOM] set energy and energyMax if different than default (100000)
		// set energy max if not default
		energy = energyMax = 200000;
		queueDisabled = true;
		list[0] = new Block(Block.BlockType.BLOCK, Block.BLUE);
		list[1] = new Block(Block.BlockType.BLOCK, Block.YELLOW);
		list[2] = new Block(Block.BlockType.BLOCK, Block.GREEN);
		list[3] = new Block(Block.BlockType.BLOCK, Block.RED);
		list[4] = new Block(Block.BlockType.BLOCK, Block.PURPLE);
		list[5] = new Block(Block.BlockType.BLOCK, Block.GREY);
		list[6] = new Block(Block.BlockType.WEDGE);
		list[7] = new Block(Block.BlockType.STAR);
		list[8] = new Block(Block.BlockType.TRASH);
		list[9] = new Block(Block.BlockType.HEART);
		list[10] = new Block(Block.BlockType.BOMB);
	}
	
	
	@Override
	protected void checkCommonControls() {
		char c;
		int x = this.cursorGridPos[0], y = this.cursorGridPos[1]; // done to improve code readability
		while (Keyboard.next()) {
			c = Character.toUpperCase( Keyboard.getEventCharacter() );
			switch (c) {
				case '1':
				case '2':
				case '3':
				case '4':
				case '5':
					if (grid[x].blocks[y].type != Block.BlockType.BLOCK || grid[x].blocks[y].type != Block.BlockType.BOMB) {
						break;
					}
					int v = Character.getNumericValue(c) - 1; // get the matching colorID value for the provided number
					undo.push(grid.clone());
					if (grid[x].blocks[y].type == Block.BlockType.BLOCK && fillToggle) {
						// only BLOCK types are affected by the fill toggle
						int t = checkGrid(cursorGridPos);
						for (int j = 0; j < grid.length; j++) {
							for (int k = 0; k < grid[0].blocks.length; k++) {
								if (grid[j].blocks[k].clearMark) {
									grid[j].blocks[k].colorID = v;
								}
							}
						}
						fillToggle = false;
					} else {
						if (grid[x].blocks[y].type == Block.BlockType.BOMB && v < 2) {
							v = 2;
						}
						grid[x].blocks[y].colorID = v;
						fillToggle = false;
					}
					break;
				case 'Q': // set as block type at default color (blue)
					if (grid[x].blocks[y].type != Block.BlockType.BLOCK) {
						undo.push(grid.clone());
						grid[x].blocks[y] = list[0].clone();
					}
					break;
				case 'W':
					if (grid[x].blocks[y].type != Block.BlockType.WEDGE) {
						undo.push(grid.clone());
						grid[x].blocks[y] = list[6].clone();
					}
					break;
				case 'E':
					if (grid[x].blocks[y].type != Block.BlockType.STAR) {
						undo.push(grid.clone());
						grid[x].blocks[y] = list[7].clone();
					}
					break;
				case 'T':
					if (grid[x].blocks[y].type != Block.BlockType.TRASH) {
						undo.push(grid.clone());
						grid[x].blocks[y] = list[8].clone();
					}
					break;
				case 'R': 
					if (grid[x].blocks[y].type != Block.BlockType.HEART) {
						undo.push(grid.clone());
						grid[x].blocks[y] = list[9].clone();
					}
					break;
				case 'Y':
					if (grid[x].blocks[y].type != Block.BlockType.BOMB) {
						undo.push(grid.clone());
						grid[x].blocks[y] = list[10].clone();
					}
					break;
				case 'F':
					fillToggle = !fillToggle;
					break;
				case 'S':
					// TODO: write grid to file
					break;
				case 'L':
					// TODO: load grid from file
					break;
				case 'Z':
					grid = undo.pop();
					break;
					
			}
		}
	}
	
	
	@Override
	protected void buildGrid() {
		for (int i = 0; i < grid.length; i++) {
			grid[i] = new GridColumn(gridSize[1]);
			for (int k = 0; k < grid[0].blocks.length; k++) {
				grid[i].blocks[k] = new Block(Block.BlockType.BLOCK, Block.BLUE); //*/
			}
		}
		this.blocksRemaining = grid.length * grid[0].blocks.length;
	}
	
	@Override
	protected Block getQueueBlock() {
		// TODO Auto-generated method stub
		return null;		
	}
}


