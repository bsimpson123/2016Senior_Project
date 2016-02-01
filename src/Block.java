import java.util.HashMap;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;

public class Block {
	/* Public Variables */
	public enum BlockType {
		BLOCK, WEDGE, STAR, TRASH, ROCK
	}

	protected static Sprite blockColor[];
	protected static Sprite blockWedge, blockStar, blockTrash, blockRock;
	protected static Sprite errorBlock;
	
	public static final int  
		BLUE = 0,
		YELLOW = 1,
		GREEN = 2,
		RED = 3,
		PURPLE = 4
	;
	public static final int blockColorCount = 5;
	
	/* Protected variables */
	/* Define the draw space that a block will take up in the grid.
	 * This value is independent from the texture size and will ensure
	 * each block takes up the same space on the grid. */
	protected static final int[] blockDrawSpace = new int[] { 32, 32 };
	
	public final BlockType type;
	protected Sprite block;
	protected int colorID = 0;
	/** Collection of colors that will be used for standard blocks. */
	
	/** Indicates whether the block has been checked for processing during a game loop.
	 * This value should be reset to false before logic processing each game loop. */
	protected boolean checked = false;
	protected boolean clearMark = false;
	
	/* Constructors */
	public Block(BlockType type, int colorID) {
		this.type = type;
		if (colorID >= blockColorCount) {
			colorID = 0;
		} else {
			this.colorID = colorID;
		}
		setSprite();
	}
	
	public Block(BlockType type) {
		this.type = type;
		if (type == BlockType.BLOCK) {
			colorID = Global.rand.nextInt(blockColorCount);
		} else {
			colorID = 0;
		}
		setSprite();
	}
	
	/** 
	 * Creates a new instance of the provided Block object, reseting only block states checked and clear to default values.
	 */
	public Block(Block clone) {
		this.type = clone.type;
		this.block = clone.block;
		this.colorID = clone.colorID;
	}
	
	/* Class methods */
	
	public static void initializeBlocks(HashMap<String,Texture> texMap) {
		blockColor = new Sprite[5];
		Texture blockTex = texMap.get("blocksheet");
		blockColor[BLUE] = new Sprite(
				blockTex,
				new int[] { 212, 266 },
				new int[] { 32, 32 },
				blockDrawSpace
			);
		blockColor[YELLOW] = new Sprite(
				blockTex,
				new int[] { 212, 332 },
				new int[] { 32, 32 },
				blockDrawSpace
			);
		blockColor[GREEN] = new Sprite(
				blockTex,
				new int[] { 212, 299 },
				new int[] { 32, 32 },
				blockDrawSpace
			);
		blockColor[RED] = new Sprite(
				blockTex,
				new int[] { 212, 431 },
				new int[] { 32, 32 },
				blockDrawSpace
			);
		blockColor[PURPLE] = new Sprite(
				blockTex,
				new int[] { 240, 33 },
				new int[] { 32, 32 },
				blockDrawSpace
			);
		blockTex = texMap.get("yellowtiles");
		blockStar = new Sprite(
				blockTex,
				new int[] { 1036, 260 },
				new int[] { 128, 128 },
				blockDrawSpace
			);
		blockWedge = new Sprite(
				blockTex,
				new int[] { 1036, 874 },
				new int[] { 128, 128 },
				blockDrawSpace
			);
		errorBlock = new Sprite(
				texMap.get("red_ui"),
				new int[] { 381, 36 },
				new int[] { 36, 36 },
				blockDrawSpace
			);
	}
	
	private void setSprite() {
		switch (type) {
			case BLOCK:
				block = blockColor[colorID];
				break;
			case WEDGE:
				block = blockWedge;
				break;
			case STAR:
				block = blockStar;
				break;
			case TRASH:
				// TODO: assign trash sprite to block variable
				break;
			case ROCK:
				// TODO: assign rock sprite to block variable
				break;
		}
	}
	
	
	
	/** 
	 * Creates a copy of the object block with default block state values.
	 */
	public Block clone() {
		return new Block(this);
	}
	
	public void draw(int xc, int yc) {
		if (block == null) { 
			errorBlock.draw(xc, yc);
			// TODO: add draw for 'error' block to indicate a problem with block texture assignment
			return ;
		}
		block.draw(xc, yc);
	}
	
	/**
	 * 
	 */
	public int activate(Block[][] grid, int xPos, int yPos) {
		int score = 0;
		int stopWidth = grid[0].length - 1;
		int stopHeight = grid.length - 1;
		
		switch (this.type) {
		case BLOCK:
			// Normal blocks clear all same color blocks sharing an edge
			// Score returned equals (n-1)^2 blocks cleared.
			// Blocks that do not share an edge with a similar color block do nothing.
			
			// TODO: algorithm to search for similar connected blocks, mark those blocks for removal,
			// and count blocks that are removed.
			
			break;
		case WEDGE: 
		case ROCK:
		case TRASH:
			// Wedge, trash, and rock blocks cannot be activated, do nothing.
			break;
		case STAR:
			// Star blocks clear all blocks in a one block radius, including diagonals,
			// but can only be activated if at the bottom row.
			if (yPos != 0) { // check for bottom row
				break;
			}
			
			break;
		default:
			break;
		}
		
		
		return score;
	}
}
