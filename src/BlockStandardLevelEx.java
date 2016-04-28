import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Stack;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.Color;

/**
 * Example level to demonstrate the flow and references need to build a level
 * @author John
 */
public class BlockStandardLevelEx extends BlockStandardLevel {
	private Block[] list = new Block[12];
	private Stack<GridColumn[]> undo = new Stack<GridColumn[]>();
	private boolean fillToggle = false;
	private int[] fillPoint1 = null;
	private int[] fillPoint2 = null;
	private long moveDelay = 0;
	
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
		list[11] = new Block(Block.BlockType.ROCK);
	}
	
	
	@Override
	protected void checkCommonControls() {
		moveDelay -= Global.delta;
		char c;
		int key;
		int x = this.cursorGridPos[0], y = this.cursorGridPos[1]; // done to improve code readability
		while (Keyboard.next()) {
			c = Keyboard.getEventCharacter();
			key = Keyboard.getEventKey();
			if (moveDelay > 0) { continue; } 
			switch (key) {
				case Keyboard.KEY_1:
					updateGrid(list[0]);
					moveDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_2:
					updateGrid(list[1]);
					moveDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_3:
					updateGrid(list[2]);
					moveDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_4:
					updateGrid(list[3]);
					moveDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_5:
					updateGrid(list[4]);
					moveDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_6:
					updateGrid(list[5]);
					moveDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_R: // Rock
					if (grid[x].blocks[y].type != Block.BlockType.ROCK) {
						undo.push(grid.clone());
						updateGrid( list[11].clone() );
					}
					moveDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_W: // Wedge
					if (grid[x].blocks[y].type != Block.BlockType.WEDGE) {
						undo.push(grid.clone());
						grid[x].blocks[y] = list[6].clone();
					}
					moveDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_N: // Star
					updateGrid(list[y]);
					moveDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_T: // Trash
					updateGrid(list[8]);
					moveDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_H: // Heart
					updateGrid(list[9].clone());
					moveDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_B:
					if (grid[x].blocks[y].type != Block.BlockType.BOMB) {
						undo.push(grid.clone());
						//grid[x].blocks[y] = list[10].clone();
						updateGrid(list[10].clone());
					}
					moveDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_EQUALS: // + Bomb range
					
					break;
				case Keyboard.KEY_MINUS: // - Bomb range
					
					break;
				case Keyboard.KEY_F:
					fillToggle = !fillToggle;
					moveDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_S:
					// TODO: write grid to file
					GridColumn.writeToFile(grid);
					break;
				case Keyboard.KEY_L:
					// TODO: load grid from file
					grid = GridColumn.loadFromFile("");
					moveDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_Z:
					grid = undo.pop();
					moveDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_P:
					if (fillPoint1 == null) {
						fillPoint1 = cursorGridPos.clone();
					} else if (fillPoint2 == null){
						fillPoint2 = cursorGridPos.clone();
						fillToggle = true;
					} else {
						fillPoint1 = fillPoint2;
						fillPoint2 = cursorGridPos.clone();
					}
					moveDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_C: // clear sets
					fillPoint1 = null;
					fillPoint2 = null;
					fillToggle = false;
					moveDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_UP:
				case Keyboard.KEY_DOWN:
				case Keyboard.KEY_LEFT:
				case Keyboard.KEY_RIGHT:
					if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
						cursorGridPos[1]++;
						if (cursorGridPos[1] >= grid[0].blocks.length) {
							cursorGridPos[1] = grid[0].blocks.length - 1;
						}
					} else
					if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
						if (cursorGridPos[1] > 0) {
							cursorGridPos[1]--;
						}
					} 
					if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
						if (cursorGridPos[0] > 0) {
							cursorGridPos[0]--;
						}
					} else
					if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
						cursorGridPos[0]++;
						if (cursorGridPos[0] >= grid.length) {
							cursorGridPos[0] = grid.length - 1;
						}
					}
					moveDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_ESCAPE:
					if (gameOver) {
						levelFinished = true;
					}
					this.gameOver = true;
					break;
				default:
					break;
			}
			
		}
		if (undo.size() > 15) { // limit undo history to 15 entries
			undo.remove(0);
		}
		
	}
	
	@Override
	protected void drawTopLevelUI() {
		Global.uiRed.draw(700, 16, 300, 56);
		Global.uiBlue.draw(700, 72, 300, 96);
		userInterface.draw(0,0);
		Global.drawFont48(710, 25, levelTitle, Color.white);
		int offsetX = 860;
		int yPos = 16;
		int[] numResize = new int[] { 30, 40 };
		Global.uiGreen.draw(680, 500, 100, 100);
	}
	
	@Override
	protected void drawCursor() {
		energy = energyMax; // prevent energy from decreasing
		
		cursor.draw(
				gridBasePos[0] + blockSize[0] * cursorGridPos[0],
				gridBasePos[1] - blockSize[1] * cursorGridPos[1],
				blockSize
			);
		
		if (fillPoint1 != null) {
			// TODO: draw points tinted with color
			Color.cyan.bind();
			cursor.draw(
					gridBasePos[0] + blockSize[0] * fillPoint1[0], 
					gridBasePos[1] - blockSize[1] * fillPoint1[1],
					blockSize
				);
			Color.white.bind();
		}
		if (fillPoint2 != null) {
			Color.magenta.bind();
			cursor.draw(
					gridBasePos[0] + blockSize[0] * fillPoint2[0], 
					gridBasePos[1] - blockSize[1] * fillPoint2[1],
					blockSize
				);
			Color.white.bind();
		}
	}

	
	private void updateGrid(Block copyBlock) {
		undo.push(grid.clone());
		if (!fillToggle) {
			grid[cursorGridPos[0]].blocks[cursorGridPos[1]] = copyBlock.clone();
			return;
		}
		
		if (fillPoint1 != null && fillPoint2 != null) {
			// use fill point defined area
			int t;
			if (fillPoint1[0] < fillPoint2[0]) {
				t = fillPoint1[0];
				fillPoint1[0] = fillPoint2[0];
				fillPoint2[0] = t;
			}
			if (fillPoint1[1] < fillPoint2[1]) {
				t = fillPoint1[1];
				fillPoint1[1] = fillPoint2[1];
				fillPoint2[1] = t;
			}
			for (int x = fillPoint1[0]; x <= fillPoint2[0]; x++) {
				for (int y = fillPoint1[1]; y <= fillPoint2[1]; y++) {
					grid[x].blocks[y] = copyBlock.clone();
				}
			}
		} else {
			// use matching block defined areas
			checkGrid(cursorGridPos);
			for (int j = 0; j < grid.length; j++) {
				for (int k = 0; k < grid[0].blocks.length; k++) {
					if (grid[j].blocks[k].clearMark) {
						grid[j].blocks[k] = copyBlock.clone();
					}
				}
			}
		}
		fillToggle = false;
		fillPoint1 = null;
		fillPoint2 = null;
	}
	
	@Override	protected void buildGrid() {
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


