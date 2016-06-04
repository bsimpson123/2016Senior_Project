
public class BlockEndlessLevel extends BlockBreakLevel {
	int Difficulty;
	int yMax = grid[0].blocks.length-1;
	int rx, ry;
	//private final int bColors = 3;
	
	@Override protected void buildGrid(int levelSelect){
		int bColors = 4; //may adjust 
		minColors = 6;
		grid = new GridColumn[20]; //how many columns of blocks
		for (int i = 0; i < grid.length; i++) {
			grid[i] = new GridColumn(21); //rows of blocks
			for (int k = 0; k < 10; k++) { //initial height of 10 rows of blocks 
				grid[i].blocks[k] = new Block(Block.BlockType.BLOCK, Global.rand.nextInt(bColors)); //3 colors at first
			}
		}
		
		gridBasePos = new int[] { 20, Global.glEnvHeight - blockSize[1] - 50 };
		cursorGridPos[0] = grid.length / 2;
		cursorGridPos[1] = grid[0].blocks.length / 2;
		queue = new Block[grid.length];
		setGridCounts();
	}
	@Override public void run() {
		// decrement input delay variables
		actionDelay -= Global.delta;
		inputDelay -= Global.delta;
		
		background.draw(0, 0);
		
		if (blocksRemaining == 0) {
			/*levelComplete = true;
			if (!endLevelDelayed) {
				endLevelDelayed = true;
				pauseCursorPos = 0;
				score += energy >> 6;
				energy = 0;
				inputDelay = Global.inputReadDelayTimer;
			}*/
		}
		
		if (!gamePaused) {
			// process active gameplay
			queueManualShiftDelay -= Global.delta;
			gridShiftActionDelay -= Global.delta;
			if (!disableEnergy) { 
				energy -= Global.delta; 
				if (energy < 0) { energy = 0; }
				else if (energy > energyMax) { energy = energyMax; }
			}
			processQueue();
			processGridBlocks(grid);
			drawGrid(grid);
			drawCursor();
			
			for (int x = 0; x < grid.length; x++) {
				if (grid[x].blocks[yMax] != null && blocksMoving == false) { //if block enters the 21st row
				
					//game over
					gameOver = true;
				}
			}
			
			// check if heart special control is active and handle accordingly
			if (heartSpecialActive) {
				/** @author Brock */
				DrawHeartSelector(); 
				heartMenuControls();
				if (clearColor) {
					int counter = activateHeartBlock(cursorGridPos);
					updateScore(counter);
					addEnergy(counter);
					removeMarkedBlocks();
					heartSpecialActive = false;
					clearColor = false; 
				}
			} else { // no special circumstance, handle input normally
				if (inputDelay <= 0l) {
					if(gameOver == false)
						checkCommonControls();
				}
			}
		}
		drawTopLevelUI();
	}
	
	//allowedColors = allowedColors | (1 << 5) | (1 << 0) | (1 << 2); //work in progress
	
	public BlockEndlessLevel(int levelSelect) {
		super(levelSelect);
		disableEnergy = true;
		queueStepDelayTimer = 400;
		Difficulty = levelSelect;
		buildGrid(Difficulty);
		levelTitle = String.format("Difficulty %02d", Difficulty);
	}
}
