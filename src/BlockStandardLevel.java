import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;
/**
 * This class serves as the base class for all Block Breaker Standard mode levels,
 * and defines and abstracts many of the functions that many level design simpler.
 * @author John Ojala
 */
public abstract class BlockStandardLevel {
	protected static Sprite[] numbers = new Sprite[10]; 
	protected static int score;
	protected static Sprite pauseCursor;
	protected static Sprite cursor;
	protected static Sprite[] shiftLR = new Sprite[2];
	protected static Sprite nLevel;
	protected static Sprite overlay;
	
	private static int scoreDisplay = 0;
	private static int change = 0;
	private static long scoreUpdateDelayTimer = 50l;
	private static long scoreUpdateDelay = scoreUpdateDelayTimer;

	protected Sprite levelDisplay;
	protected Sprite background;
	protected Sprite userInterface;
	protected static Sprite emptyEnergy; //empty energy bar
	protected static Texture energyBar; //energy bar
	protected int energyMax = 100000;
	protected int energy = energyMax;
	protected float energyGainMultiplier = 1.0f;

	// grid variables
	protected GridColumn[] grid;
	protected int[] gridSize;
	protected int[] gridBasePos;
	// grid shifting variables
	protected boolean gridShiftActive = false;
	protected boolean blockDropActive = false;
	protected boolean gridMoving = false;
	protected int gridShiftDir = 1;
	private long shiftActionDelayTimer = 1000l;
	private long gridShiftActionDelay = shiftActionDelayTimer;
	// grid queue variables
	protected Block[] queue;
	private long queueStepDelayTimer = 500l;
	private long queueStepDelay = queueStepDelayTimer;
	private int queueStepReq = 4;
	private int queueStepCount = 0;
	private int queueCount = 0;
	private int queueLimit = 5;
	private long queueManualShiftDelayTimer = 250l;
	private long queueManualShiftDelay = queueManualShiftDelayTimer;
	private boolean queueHold = false;
	
	protected int[] cursorGridPos = new int[] { 0, 0 };
	protected int[] blockSize;
	protected int blocksRemaining = 0;
	protected boolean gamePaused = false;
	protected long inputDelay = 0;
	protected long actionDelay = Global.inputReadDelayTimer * 2;
	protected int level = 1;
	/** Sets sets the multiplier to apply to all score additions/subtractions. */
	protected float levelMultiplier = 1.0f;
	/** Used to contain removed block counts for grid clears. */
	protected int counter = 0;

	protected boolean levelFinished = false;
	protected boolean gameOver = false;
	protected boolean levelComplete = false;
	protected boolean practice = false;
	
	private Sprite optionFrameMid = new Sprite(
			Global.textureMap.get("blue_ui"),
			new int[] { 0, 59 },
			new int[] { 190, 20 },
			new int[] { 250, 250 }
		);
	private Sprite hoverBox = new Sprite(
			Global.textureMap.get("green_ui"),
			new int[] { 0, 144 },
			new int[] { 190, 48 },
			new int[] { 190, 48 }
		);
	
	private long movementInputDelay = Global.inputReadDelayTimer;
	protected int pauseCursorPos = 0;
	
	Sprite pauseBox = new Sprite(
			Global.textureMap.get("green_ui"),
			new int[] {0,0},
			new int[] {190,48},
			new int[] {190,48}
		);
	
	public void run() {
		// decrement delay variables
		queueManualShiftDelay -= Global.delta;
		gridShiftActionDelay -= Global.delta;
		actionDelay -= Global.delta;
		inputDelay -= Global.delta;
		/* Draw all background elements. These should always be the first items drawn to screen. */
		background.draw(0, 0);
		counter = 0;
		
		if (blocksRemaining == 0) {
			levelComplete = true;
			
		} else if (energy == 0) {
			// game over
			
		}
		
	}

	/**
	 * Checks the grid for blocks of the same color sharing edges, and marks those blocks
	 * for removal.
	 * @param grid The 2-dimensional grid of blocks
	 * @param xy 2-element array containing the starting index locations for the search
	 * @return The total number of blocks found
	 * @author John
	 */
	protected final int checkGrid(int[] xy) {
		if (grid[xy[0]].blocks[xy[1]] == null) { return 0; }
		if (grid[xy[0]].blocks[xy[1]].type != Block.BlockType.BLOCK) { return 0; }
		return checkGrid(xy[0], xy[1], grid[xy[0]].blocks[xy[1]].colorID);
	}

	/**
	 * @author John 
	 */
	private final int checkGrid(int xc, int yc, final int colorID) {
		int sum = 0;
		if (grid[xc].blocks[yc] == null || grid[xc].blocks[yc].checked) {
			return 0;
		}
		grid[xc].blocks[yc].checked = true;
		if (grid[xc].blocks[yc].colorID != colorID) {
			return 0;
		}
		if (grid[xc].blocks[yc].type != Block.BlockType.BLOCK) {
			return 0;
		}
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

	/**
	 * @author John
	 */
	protected void drawScore() {
		if (score > 0) {
			scoreUpdateDelay -= Global.delta;
			if (scoreUpdateDelay <= 0 && score != scoreDisplay) {
				if (scoreDisplay < score) { // most common case, score is increasing
					change = (score - scoreDisplay) >> 2;
					if (change == 0) { change = 4; }
					scoreDisplay += change;
					if (scoreDisplay > score) { scoreDisplay = score; }
				} else { // score decreasing
					change = (scoreDisplay - score) >> 2;
					if (change == 0) { change = 4; }
					scoreDisplay -= change;
					if (scoreDisplay < score) { scoreDisplay = score; }
				}
				scoreUpdateDelay = scoreUpdateDelayTimer;
			}
		} else { 
			score = 0;
			scoreDisplay = score; 
		}
		char[] strScore = Integer.toString(scoreDisplay).toCharArray();
		int offsetX = 948;
		int yPos = 125;
		for (int i = strScore.length - 1; i >= 0; i--) {
			getNumber(strScore[i]).draw(offsetX, yPos);
			offsetX -= 24;
		}
		for (int i = strScore.length; i < 11; i++) {
			numbers[0].draw(offsetX, yPos);
			offsetX -= 24;
		}
	}
	
	/**
	 * @author John 
	 */
	private Sprite getNumber(char c) {
		switch (c) {
			case '0':
				return numbers[0];
			case '1':
				return numbers[1];
			case '2':
				return numbers[2];
			case '3':
				return numbers[3];
			case '4':
				return numbers[4];
			case '5':
				return numbers[5];
			case '6':
				return numbers[6];
			case '7':
				return numbers[7];
			case '8':
				return numbers[8];
			case '9':
			default:
				return numbers[9];
		}
	}
	
	/**
	 * @author John
	 */
	protected void drawTopLevelUI() {
		Global.uiRed.draw(700, 16, 300, 56);
		Global.uiBlue.draw(700, 72, 300, 96);
		userInterface.draw(0,0);
		char[] lvl = Integer.toString(level).toCharArray();
		int offsetX = 860;
		int yPos = 16;
		int[] numResize = new int[] { 30, 40 };
		for (int i = 0; i < lvl.length; i++) {
			getNumber(lvl[i]).draw(offsetX, yPos, numResize);
			offsetX += 24;
		}
		drawScore();
		Global.uiGreen.draw(680, 500, 100, 100);
		if (gridShiftDir == 1) {
			shiftLR[1].draw(680, 500);
		} else {
			shiftLR[0].draw(680, 500);
		}
		drawEnergy();

		if (levelComplete) {
			drawGrid();
			// TODO: level complete code
			if (actionDelay < 0 && Global.getControlActive(Global.GameControl.SELECT)) {
				levelFinished = true;
			}
			
			overlay.draw(0, 0);
			nLevel.draw(200, 200);
			// placeholder for level advancement
		} else if (gamePaused) {
// Author: Brock
			pauseControls();

			overlay.draw(0, 0);
			optionFrameMid.draw(180, 250);
			
			if (pauseCursorPos == 0) {
				pauseBox.draw(210, 370);
				hoverBox.draw(210, 310);
			}
			if (pauseCursorPos == 1) {
				hoverBox.draw(210, 370);
				pauseBox.draw(210, 310);
			}
			

		} else if (gameOver) {
			drawGrid();
		}
	}
	
	protected abstract void buildGrid(); 

	/**
	 * 
	 * @param shiftRate The drop rate in pixels/second for falling blocks
	 * @return true if blocks are currently falling within the grid, false
	 * if no blocks are currently falling 
	 * @author John
	 */
	protected final boolean drawGrid(int shiftRate) {
		gridShiftActionDelay -= Global.delta;
		queueManualShiftDelay -= Global.delta;
		int[] gridBasePos = new int[] { 20, Global.glEnvHeight - blockSize[1] - 50 }; // distance from the left top for the bottom-left of the grid display
		//int dropRate = 20; // millisecond time for a falling block to cover 1 space
		blockDropActive = false;
		gridShiftActive = false;
		int blockMoveRate = (int)(Global.delta * shiftRate) / 1000;
		int columnMoveRate = (int) (Global.delta * shiftRate) / 500; // columns move 2x as fast as blocks
		// adjust falling block offsets
		for (int i = 0; i < grid.length; i++) {
			for (int k = 0; k < grid[0].blocks.length; k++) {
				if (grid[i].blocks[k] != null) {
					if (grid[i].blocks[k].dropDistance > 0) {
						grid[i].blocks[k].dropDistance -= blockMoveRate;
						if (grid[i].blocks[k].dropDistance < 0) { 
							grid[i].blocks[k].dropDistance = 0; 
						} else {
							blockDropActive = true;
						}
					}
				}
			}
		}
		// adjust grid column offsets if no blocks are falling
		if (!blockDropActive) {
			for (int i = 0; i < grid.length; i++) {
				if (grid[i].columnOffset != 0) {
					if (gridShiftDir == 1) { // right-shift
						grid[i].columnOffset += columnMoveRate;
						if (grid[i].columnOffset >= 0) { 
							grid[i].columnOffset = 0; 
						} else {
							gridShiftActive = true;
						}
					} else { // left-shift
						grid[i].columnOffset -= columnMoveRate;
						if (grid[i].columnOffset <= 0) {
							grid[i].columnOffset = 0;
						} else {
							gridShiftActive = true;
						}
					}
				}
			}
		}
		// draw the grid
		for (int i = 0; i < grid.length; i++) {
			for (int k = 0; k < grid[0].blocks.length; k++) {
				if (grid[i].blocks[k] != null) {
					grid[i].blocks[k].draw(
							gridBasePos[0] + blockSize[0] * i + grid[i].columnOffset,
							gridBasePos[1] - blockSize[1] * k - grid[i].blocks[k].dropDistance,
							blockSize
						);
					grid[i].blocks[k].checked = false;
					grid[i].blocks[k].clearMark = false;
				}
			}
		}
		drawQueue();
		return (blockDropActive || gridShiftActive);
	}
	
	/**
	 * Draws the block grid without processing any block movement.
	 * @author John
	 */
	protected final void drawGrid() {
		for (int i = 0; i < grid.length; i++) {
			for (int k = 0; k < grid[0].blocks.length; k++) {
				if (grid[i].blocks[k] != null) {
					grid[i].blocks[k].draw(
							gridBasePos[0] + blockSize[0] * i + grid[i].columnOffset,
							gridBasePos[1] - blockSize[1] * k - grid[i].blocks[k].dropDistance,
							blockSize
						);
					grid[i].blocks[k].checked = false;
					grid[i].blocks[k].clearMark = false;
				}
			}
		}
		drawQueue();
		
	}
	protected void pauseControls() {
// Author: Brock
		if (inputDelay <= 0) {
			
			if (Global.getControlActive(Global.GameControl.UP)) {
					pauseCursorPos--;
		
				if (pauseCursorPos < 0) {
						pauseCursorPos = 1;
				}
				inputDelay = Global.inputReadDelayTimer * 2;
			}
			if (Global.getControlActive(Global.GameControl.DOWN)) {
					pauseCursorPos++;
		
				if (pauseCursorPos > 1) {
					pauseCursorPos = 0;
				}
				inputDelay = Global.inputReadDelayTimer * 2;
			}
			if (Global.getControlActive(Global.GameControl.CANCEL)) { // Cancel key moves the cursor to the program exit button

			}
			if (Global.getControlActive(Global.GameControl.PAUSE)) { // Cancel key moves the cursor to the program exit button
					gamePaused = false;
					inputDelay = Global.inputReadDelayTimer * 2;		
			}
		
			if (Global.getControlActive(Global.GameControl.SELECT)) {

				switch (pauseCursorPos) {
					case 0:
						gamePaused = false;
						inputDelay = Global.inputReadDelayTimer;	
						break;

					case 1:
						levelComplete = true;
						gameOver = true;
						inputDelay = Global.inputReadDelayTimer;	
						break;

				}
			}
		} else if (inputDelay > 0) {
			inputDelay -= Global.delta;
		}
	}
	
	/**
	 * Checks against movement inputs within the grid, and adjusts the cursor
	 * position accordingly.
	 * @author John
	 */
	protected void checkCommonControls() {
		if (Global.getControlActive(Global.GameControl.PAUSE)) {
				gamePaused = true;
				inputDelay = 1000l;		
		}
		else 
		if (Global.getControlActive(Global.GameControl.SPECIAL2)) {
			// queue control
			queueHold = true;
			if ( queueManualShiftDelay <= 0) {
				if (Global.getControlActive(Global.GameControl.LEFT)) {
					// shift queue left
					shiftQueue(-1);
					queueManualShiftDelay = queueManualShiftDelayTimer;
				} else if (Global.getControlActive(Global.GameControl.RIGHT)) {
					// shift queue right
					shiftQueue(1);
					queueManualShiftDelay = queueManualShiftDelayTimer;
				} else if (Global.getControlActive(Global.GameControl.DOWN)) {
					// drop (add to grid) queue
					int overflow = addToGrid();
					updateScore( overflow * -10 );
					queueManualShiftDelay = queueManualShiftDelayTimer;
				}
			}
		} else {
			queueHold = false;
			// cursor control
			if (Global.getControlActive(Global.GameControl.SPECIAL1) && gridShiftActionDelay <= 0) {
				gridShiftActionDelay = shiftActionDelayTimer;
				gridShiftActive = true;
				gridShiftDir *= -1;
				shiftGridColumns();
			}
			
			if (Global.getControlActive(Global.GameControl.UP)) {
				cursorGridPos[1]++;
				if (cursorGridPos[1] >= grid[0].blocks.length) {
					cursorGridPos[1] = grid[0].blocks.length - 1;
				}
				inputDelay = Global.inputReadDelayTimer;
			} else
			if (Global.getControlActive(Global.GameControl.DOWN)) {
				if (cursorGridPos[1] > 0) {
					cursorGridPos[1]--;
				}
				inputDelay = Global.inputReadDelayTimer;
			} 
			if (Global.getControlActive(Global.GameControl.LEFT)) {
				if (cursorGridPos[0] > 0) {
					cursorGridPos[0]--;
				}
				inputDelay = Global.inputReadDelayTimer;
			} else
			if (Global.getControlActive(Global.GameControl.RIGHT)) {
				cursorGridPos[0]++;
				if (cursorGridPos[0] >= grid.length) {
					cursorGridPos[0] = grid.length - 1;
				}
				inputDelay = Global.inputReadDelayTimer;
			}
			if (actionDelay <= 0) {
				if (!gridMoving && Global.getControlActive(Global.GameControl.SELECT) &&
						grid[cursorGridPos[0]].blocks[cursorGridPos[1]] != null) {
					counter = 0;
					processActivate();
					if (counter > 1 || grid[cursorGridPos[0]].blocks[cursorGridPos[1]].type == Block.BlockType.BOMB) {
						// decrease the blocksRemaining counter after blocks are cleared
//						blocksRemaining -= counter; // now adjusted in removeMarkedBlocks() function
						removeMarkedBlocks();
						dropBlocks();
						shiftGridColumns();
						// action delay is only increased if an action was performed and the grid was changed
						actionDelay = Global.inputReadDelayTimer;
					}
				}
			}
		}
	}

	/**
	 * @author John
	 */
	protected final void removeMarkedBlocks() {
		for (int xc = 0; xc < grid.length; xc++) {
			for (int yc = 0; yc < grid[0].blocks.length; yc++) {
				if (grid[xc].blocks[yc] != null && grid[xc].blocks[yc].clearMark) {
					grid[xc].blocks[yc] = null;
					blocksRemaining--;
				}
			}
		}
	}
	
	/**
	 * Calculates and sets the drop distance for remaining blocks after blocks
	 * have been cleared from the grid. This method should be overridden if special
	 * blocks in play would prevent normal block falling behavior.
	 * @param blockDimensions the height of blocks used in the level. 
	 * Used to calculate the distance blocks will be offset. 
	 * @author John
	 */
	protected void dropBlocks() {
		int dropDist = 0;
		int slotDist = 0;
		for (int i = 0; i < grid.length; i++) {
			slotDist = 0;
			dropDist = 0;
			for (int k = 0; k < grid[0].blocks.length; k++) {
				if (grid[i].blocks[k] == null) {
					dropDist += blockSize[1];
					slotDist++;
				} else if (dropDist > 0) {
					grid[i].blocks[k].dropDistance = dropDist;
					grid[i].blocks[k-slotDist] = grid[i].blocks[k];
					grid[i].blocks[k] = null;
				}
			}
		}
		return ;
	}
	
	/**
	 * @author John
	 */
	protected void shiftGridColumns() {
		GridColumn emptyset = new GridColumn(grid[0].blocks.length);
		int colDist = 0, shiftDist = 0;
		if (gridShiftDir == 1) {
			for (int xc = grid.length - 1; xc >= 0; xc--) { // xCurrent, xPrevious
				if (grid[xc].blocks[0] == null) {
					colDist++;
					shiftDist += blockSize[0];
				} else if (grid[xc].blocks[0].type == Block.BlockType.ROCK) {
					// columns with ROCK blocks do not shift and stop other columns from moving past
					colDist = 0;
					shiftDist = 0;
					continue;
				} else if (shiftDist > 0) {
					grid[xc].columnOffset -= shiftDist; 
					grid[xc + colDist] = grid[xc];
					grid[xc] = emptyset.clone();
				}
			}
		} else if (gridShiftDir == -1) {
			for (int xc = 0; xc < grid.length; xc++) {
				if (grid[xc].blocks[0] == null) {
					colDist++;
					shiftDist += blockSize[0];
				} else if (grid[xc].blocks[0].type == Block.BlockType.ROCK) {
					// columns with ROCK blocks do not shift and stop other columns from moving past
					colDist = 0;
					shiftDist = 0;
					continue;
				} else if (shiftDist > 0) {
					grid[xc].columnOffset += shiftDist;
					grid[xc - colDist] = grid[xc];
					grid[xc] = emptyset.clone();
				}
			}
		}
		return ;
	}
	
	/**
	 * Adds blocks in queue to the grid at the top level. Returns the number of blocks 
	 * that could not be added (such as when the column is already full). 
	 * @param blockQueue <code>Block</code> array containing blocks to be added for each
	 * grid column.
	 * @return The number of blocks that could not be added to the grid.
	 * @author John
	 */
	protected int addToGrid() {
		int overflow = 0;
		int yMax = grid[0].blocks.length - 1;
		for (int x = 0; x < grid.length; x++) {
			if (queue[x] != null) {
				if (grid[x].blocks[yMax] == null) {
					grid[x].blocks[yMax] = queue[x];
					blocksRemaining++;
				} else {
					overflow++;
				}
				queue[x] = null;
			}
		}
		if (overflow < queueCount) {
			gridMoving = true;
		}
		dropBlocks();
		shiftGridColumns();
		queueCount = 0;
		return overflow;
	}
	
	protected abstract Block getQueueBlock();
	
	protected abstract void processActivate(); 
	
	/**
	 * 
	 * @param direction
	 * @author John
	 */
	private void shiftQueue(int direction) {
		if (levelComplete) { return ; }
		int xMax = queue.length;
		int current, next;
		if (direction == 1) { // shift right
			for (int x = xMax - 1; x >=0; x--) {
				if (queue[x] == null) {
					current = x;
					next = (xMax + (x - 1)) % xMax;
					for (int i = 0; i < xMax; i++) {
						current = (xMax + (x - i)) % xMax;
						next = (xMax + (current - 1)) % xMax;
						queue[current] = queue[next];
						queue[next] = null;
					}
					break;
				}
			}
		} else {
			for (int x = 0; x < xMax; x++) {
				if (queue[x] == null) { // find first null space
					current = x;
					next = (x + 1) % xMax;
					for (int i = 0 ; i < xMax; i++) { // shift the queue
						current = (x + i) % xMax;
						next = (current + 1) % xMax;
						queue[current] = queue[next];
						queue[next] = null;
					}
					break;
				}
			}
			
		}
	}
	/**
	 * @author John
	 */
	protected void processQueue() {
		if (levelComplete) { return; }
		queueStepDelay -= Global.delta;
		int xMax = queue.length;
		if (queueStepDelay > 0) { return; }
		queueStepDelay = queueStepDelayTimer; // reset step timer
		queueStepCount++;
		// shift the queue to the left by one block
		if (queueCount >= queueLimit && queueStepCount == 2) {
			int overflow = addToGrid();
			updateScore( overflow * -10 );
			queueCount = 0;
		} else if (!queueHold) {
			shiftQueue(-1);
		}
		if (queueStepCount < queueStepReq) { 
			return; 
		}
		queueStepCount = 0; // reset steps-remaining-until-block-add timer
		Block b = getQueueBlock();
		int firstNull = queue.length - 1;
		if (queue[firstNull] != null) {
			for (int x = xMax - 1; x > 0; x--) { // find closest null from right
				if (queue[x] == null) {
					firstNull = x;
					break;
				}
			}
			// shift right-most blocks to make room for new block
			for (int x = firstNull; x < queue.length - 1; x++) { 
				queue[x] = queue[x + 1];
			}
		}
		queue[queue.length - 1] = b;
		queueCount++;

	}
	/**
	 * @author John
	 */
	protected void drawQueue() {
		int[] anchorPos = new int[] { 20, 40 };
		int offset = 0;
		for (int i = 0; i < queue.length; i++) {
			if (queue[i] != null) {
				queue[i].draw(anchorPos[0] + offset, anchorPos[1], blockSize);
			}
			offset += blockSize[0];
		}
		return ;
	}
	
	/**
	 * @author Mario
	 */
	protected void drawEnergy() {
		emptyEnergy.draw(20, 740);
		energyBar.bind();
		float percent = (float) energy/(float) energyMax;
		glPushMatrix();
		glTranslatef(20,740,0); // x y z
		glBegin(GL_QUADS);
		{
			glTexCoord2f(0,0);
			glVertex2i(0,0);
			
			glTexCoord2f(percent,0);
			glVertex2i((int) (percent*640),0);
			
			glTexCoord2f(percent,1);
			glVertex2i((int)(percent*640),32);
			
			glTexCoord2f(0,1);
			glVertex2i(0,32);
		}
		glEnd();
		glPopMatrix();
	}
	
	/**
	 * @param baseAdjustment The base value to adjust score by, before applying the <code>levelMultiplier</code>
	 * @author John
	 */
	protected void updateScore(int baseAdjustment) {
		score += (int)Math.floor(baseAdjustment * levelMultiplier);
	}
	
	protected void addEnergy(int baseAdjustment) {
		energy += (int)Math.floor(baseAdjustment * energyGainMultiplier);
	}

	/**
	 * Marks for removal blocks within <code>radius</code> block distance (including diagonal).
	 * Recursively activates any other bomb blocks within the radius.
	 * @param radius the blocks radius from the bomb to clear
	 * @param pos starting position for the bomb block
	 * @return The number of blocks removed
	 * @author John
	 */
	protected int activateBombBlock(int[] pos, int radius) {
		if (radius < 1) { throw new IllegalArgumentException("Invalid bomb radius."); }
		int xMin = pos[0] - radius, 
			xMax = pos[0] + radius,
			yMin = pos[1] - radius, 
			yMax = pos[1] + radius;
		int[] xEdge = new int[] { xMin, xMax }; // edge values before range checks
		int[] yEdge = new int[] { yMin, yMax };
		int cornerRadius = radius / 3;
		if (cornerRadius <= 0) { cornerRadius = 1; }
		int count = 0;
		if (xMin < 0) { xMin = 0; }
		if (xMax >= gridSize[0]) { xMax = gridSize[0] - 1; }
		if (yMin < 0) { yMin = 0; }
		if (yMax >= gridSize[1]) { yMax = gridSize[1] - 1; }
		// mark center bomb as cleared to prevent recursive calls to already activated bomb blocks
		grid[pos[0]].blocks[pos[1]].clearMark = true;
		count++;
		for (int i = xMin; i <= xMax; i++) {
			for (int k = yMin; k <= yMax; k++) {
				if (i < (xEdge[0] + cornerRadius) || i > (xEdge[1] - cornerRadius)) {
					if (k < (yEdge[0] + cornerRadius) || k > (yEdge[1] - cornerRadius)) {
						continue; // skip corner checks
					}
				}
				if (grid[i].blocks[k] != null && !grid[i].blocks[k].clearMark) {
					if (grid[i].blocks[k].type == Block.BlockType.BOMB) {
						count += activateBombBlock(new int[] { i, k }, radius);
					} else if (grid[i].blocks[k].type == Block.BlockType.ROCK) { // ignore rock blocks
						continue;
					} else if (grid[i].blocks[k].type == Block.BlockType.WEDGE) { // ignore wedge blocks
						continue;
					} else {
						grid[i].blocks[k].clearMark = true;
						count++;
					}
				}
			}
		}
		return count;
	}
}

