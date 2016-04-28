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
	private long keyDelay = 0;
	private final long fileDelayTimer = 5000l;
	private long fileDelay = 0;
	
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
		grid = new GridColumn[gridSize[0]];
		queue = new Block[gridSize[0]];
		buildGrid();
		gridBasePos = new int[] { 20, Global.glEnvHeight - blockSize[1] - 50 };
		// set the cursor starting position in the center of the grid
		cursorGridPos[0] = grid.length / 2;
		cursorGridPos[1] = grid[0].blocks.length / 2;
		// disable the queue. it will not be processed or displayed. no blocks will be added
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
		keyDelay -= Global.delta;
		int key;
		if (moveDelay < 0) {  
			if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
				cursorGridPos[1]++;
				if (cursorGridPos[1] >= grid[0].blocks.length) {
					cursorGridPos[1] = grid[0].blocks.length - 1;
				}
				moveDelay = Global.inputReadDelayTimer;
			} else
			if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
				if (cursorGridPos[1] > 0) {
					cursorGridPos[1]--;
				}
				moveDelay = Global.inputReadDelayTimer;
			} 
			if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
				if (cursorGridPos[0] > 0) {
					cursorGridPos[0]--;
				}
				moveDelay = Global.inputReadDelayTimer;
			} else
			if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
				cursorGridPos[0]++;
				if (cursorGridPos[0] >= grid.length) {
					cursorGridPos[0] = grid.length - 1;
				}
				moveDelay = Global.inputReadDelayTimer;
			}
		}
		int x = this.cursorGridPos[0], y = this.cursorGridPos[1]; // done to improve code readability
		while (Keyboard.next()) {
			key = Keyboard.getEventKey();
			switch (key) {
				case Keyboard.KEY_1:
					if (keyDelay > 0) { break; } 
					updateGrid(list[0]);
					keyDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_2:
					if (keyDelay > 0) { break; } 
					updateGrid(list[1]);
					keyDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_3:
					if (keyDelay > 0) { break; } 
					updateGrid(list[2]);
					keyDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_4:
					if (keyDelay > 0) { break; } 
					updateGrid(list[3]);
					keyDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_5:
					if (keyDelay > 0) { break; } 
					updateGrid(list[4]);
					keyDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_6:
					if (keyDelay > 0) { break; } 
					updateGrid(list[5]);
					keyDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_R: // Rock
					if (grid[x].blocks[y].type != Block.BlockType.ROCK) {
						if (keyDelay > 0) { break; } 
						undo.push(grid.clone());
						updateGrid( list[11].clone() );
						keyDelay = Global.inputReadDelayTimer;
					}
					break;
				case Keyboard.KEY_W: // Wedge
					if (grid[x].blocks[y].type != Block.BlockType.WEDGE) {
						if (keyDelay > 0) { break; } 
						undo.push(grid.clone());
						grid[x].blocks[y] = list[6].clone();
						keyDelay = Global.inputReadDelayTimer;
					}
					break;
				case Keyboard.KEY_N: // Star
					if (keyDelay > 0) { break; } 
					updateGrid(list[7]);
					keyDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_T: // Trash
					if (keyDelay > 0) { break; } 
					updateGrid(list[8]);
					keyDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_H: // Heart
					if (keyDelay > 0) { break; } 
					updateGrid(list[9].clone());
					keyDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_B:
					if (grid[x].blocks[y].type != Block.BlockType.BOMB) {
						if (keyDelay > 0) { break; } 
						undo.push(grid.clone());
						//grid[x].blocks[y] = list[10].clone();
						updateGrid(list[10].clone());
						keyDelay = Global.inputReadDelayTimer;
					}
					break;
				case Keyboard.KEY_EQUALS: // + Bomb range
					if (keyDelay > 0) { break; } 
					if (grid[x].blocks[y].type != Block.BlockType.BOMB) { break; }
					if (grid[x].blocks[y].colorID == 9) { break; } // upper limit for Bomb size
					undo.push(grid.clone());
					grid[x].blocks[y].colorID++;
					keyDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_MINUS: // - Bomb range
					if (keyDelay > 0) { break; } 
					if (grid[x].blocks[y].type != Block.BlockType.BOMB) { break; }
					if (grid[x].blocks[y].colorID  == 2) { break; } // upper limit for Bomb size
					undo.push(grid.clone());
					grid[x].blocks[y].colorID--;
					keyDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_F:
					if (keyDelay > 0) { break; } 
					fillToggle = !fillToggle;
					keyDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_S:
					if (fileDelay > 0) { break; } 
					GridColumn.writeToFile(grid);
					fileDelay = fileDelayTimer;
					break;
				case Keyboard.KEY_L:
					if (fileDelay > 0) { break; } 
					grid = GridColumn.loadFromFile("");
					fileDelay = fileDelayTimer;
					break;
				case Keyboard.KEY_Z:
				case Keyboard.KEY_U:
					if (fileDelay > 0) { break; } 
					if (undo.empty()) { break; } 
					grid = undo.pop();
					keyDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_P:
					if (keyDelay > 0) { break; } 
					if (fillPoint1 == null) {
						fillPoint1 = cursorGridPos.clone();
					} else if (fillPoint2 == null){
						if (cursorGridPos.equals(fillPoint1)) { break; }
						fillPoint2 = cursorGridPos.clone();
						fillToggle = true;
					} else {
						fillPoint1 = fillPoint2;
						fillPoint2 = cursorGridPos.clone();
					}
					keyDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_C: // clear sets
					if (keyDelay > 0) { break; } 
					fillPoint1 = null;
					fillPoint2 = null;
					fillToggle = false;
					keyDelay = Global.inputReadDelayTimer;
					break;
				case Keyboard.KEY_ESCAPE:
					levelFinished = true;
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
		//Global.uiBlue.draw(700, 72, 300, 96);
		userInterface.draw(0,0);
		Global.drawFont48(710, 25, levelTitle, Color.white);

/*		list[0].draw(700, 80);
		Global.drawNumbers24(716, 86, "1", Color.white, true);
		
		list[1].draw(740, 80);
		Global.drawNumbers24(756, 86, "2", Color.white, true); //*/
		
		for (int i = 0; i < 6; i++) {
			list[i].draw(700 + i * 40, 80);
			Global.drawNumbers24(716 + i * 40, 86, String.format("%d", i + 1), Color.black, true);
		}
		list[6].draw(700, 120);
		Global.drawFont24(716, 156, "W", Color.white, true);
		
		list[10].draw(740, 120, blockSize);
		Global.drawFont24(756, 156, "B", Color.white, true);
		
		
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
			int xMin = fillPoint1[0] < fillPoint2[0] ? fillPoint1[0] : fillPoint2[0];
			int yMin = fillPoint1[1] < fillPoint2[1] ? fillPoint1[1] : fillPoint2[1];
			int xMax = fillPoint1[0] > fillPoint2[0] ? fillPoint1[0] : fillPoint2[0];
			int yMax = fillPoint1[1] > fillPoint2[1] ? fillPoint1[1] : fillPoint2[1];
			
			for (int x = xMin; x <= xMax; x++) {
				for (int y = yMin; y <= yMax; y++) {
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
				grid[i].blocks[k] = new Block(Block.BlockType.BLOCK, Block.BLUE); 
			}
		}
		this.blocksRemaining = grid.length * grid[0].blocks.length;
	}
	
	@Override
	protected Block getQueueBlock() {
		return null;		
	}
	
}


