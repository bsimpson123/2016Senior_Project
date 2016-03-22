import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Random;

import org.newdawn.slick.opengl.Texture;

/**
 * Example level to demonstrate the flow and references need to build a level
 * @author John
 */
public class BlockStandardLevelEx extends BlockStandardLevel {
	/** Defines the width and height of the blocks in the grid. */
	private boolean specialActive = false;
	private Random rand;

	public BlockStandardLevelEx(HashMap<String,Texture> rootTex) {
		rand = Global.rand;
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
	}
	
	@Override
	protected void buildGrid() {
		Block b = null;
		int r = 0;
		int count = 0;
		Global.rand.setSeed(LocalDateTime.now().getNano());
		for (int i = 0; i < grid.length; i++) {
			grid[i] = new GridColumn(gridSize[1]);
			for (int k = 0; k < grid[0].blocks.length; k++) {
				r = Global.rand.nextInt(10000);
				if (r > 20) { 
					b = new Block(Block.BlockType.BLOCK, rand.nextInt(6));
				} else {
					b = new Block(Block.BlockType.BOMB);
				}
				grid[i].blocks[k] = b; //*/
				//grid[i].blocks[k] = new Block(Block.BlockType.BLOCK, (++count) % 2);
			}
			count++;
		}
		// TASK: set the block count for the level
		this.blocksRemaining = grid.length * grid[0].blocks.length;
		r = Global.rand.nextInt(12) + 4;
		grid[r].blocks[0] = new Block(Block.BlockType.ROCK);
		blocksRemaining--; //*/
		grid[r].blocks[1] = new Block(Block.BlockType.BOMB);
		//grid[0].blocks[0] = grid[1].blocks[0].clone();
	}
	
	@Override
	public void run() {
		super.run();
		// draw the grid and handle grid mechanics and input if the game is not paused
		if (!gamePaused && !gameOver && !levelComplete) {
			processQueue();
			energy -= Global.delta;
			if (energy < 0) { energy = 0; }
			if (energy > energyMax) { energy = energyMax; }
			// draw the grid, return value indicates if there are blocks still falling from the last clear
			gridMoving = drawGrid(500);
			//shiftGrid();
		
			cursor.draw(
				// for pointer at center of block
/*				gridBasePos[0] + blockOffSet[0] * cursorGridPos[0] - blockOffSet[0]/2,
				gridBasePos[1] - blockOffSet[1] * cursorGridPos[1] + blockOffSet[1]/2 //*/
				// for selector surrounding block
				gridBasePos[0] + blockSize[0] * cursorGridPos[0],
				gridBasePos[1] - blockSize[1] * cursorGridPos[1],
				blockSize
			);
		
			// process left,right,up,down movement in the grid or special item area
			if (specialActive) {
				// if a special item or event has moved the selector cursor, handle that here
				; 
			} else {
				if (inputDelay <= 0l) {
					checkCommonControls();
				}
			}
			// DEBUG: back out of the game to the main menu. not to be included in finished levels
			if (Global.getControlActive(Global.GameControl.CANCEL)) {
				levelFinished = true;
				gameOver = true;
			}
		}
		
		// draw the top-level UI frame, score and other elements
		drawTopLevelUI();

	}

	@Override
	protected Block getQueueBlock() {
		// TODO Auto-generated method stub
		if (Global.rand.nextInt(100000) == 1) {
			return new Block(Block.BlockType.BOMB);
		}
		Block b = null;
		b = new Block(Block.BlockType.BLOCK, rand.nextInt(6));
		return b;		
	}

	@Override
	protected void processActivate() {
		// TODO: score base value calculation is to be done within each case statement
		switch (grid[cursorGridPos[0]].blocks[cursorGridPos[1]].type) {
			case BLOCK:
				counter = checkGrid(cursorGridPos);
				int adj = (int)Math.pow(counter - 1, 2);
				updateScore(adj);
				addEnergy(adj);
				break;
			case STAR:
				if (cursorGridPos[1] > 0) { break; }
				int lRange = cursorGridPos[0] == 0 ? 0 : cursorGridPos[0] - 1;
				int uRange = cursorGridPos[0] == grid.length - 1 ? grid.length : cursorGridPos[0] + 2;
				for (int i = lRange; i < uRange; i++) {
					for (int k = 0; k < 2; k++) {
						if (grid[i].blocks[k] != null) {
							grid[i].blocks[k].clearMark = true;
							counter++;
						}
					}
				}
				grid[cursorGridPos[0]].blocks[cursorGridPos[1]].clearMark = true;
				updateScore(counter * 5);
				addEnergy(counter * 5);
				break;
			case BOMB:
				counter = activateBombBlock(cursorGridPos);
				updateScore(counter);
				addEnergy(counter);
				break;
			default: // selected block does not activate; do nothing
				break;
		}
	}
}


