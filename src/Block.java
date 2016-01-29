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
	protected final int drawHeight = 32;
	protected final int drawWidth = 32;
	
	protected BlockType blockType;
	protected Sprite block;
	protected int colorID = 0;
	/** Collection of colors that will be used for standard blocks. */
	
	/** Indicates whether the block has been checked for processing during a game loop.
	 * This value should be reset to false before logic processing each game loop. */
	protected boolean checked = false;
	protected boolean clearMark = false;
	
	/* Constructors */
	/**
	 * Empty constructor used when cloning blocks.
	 */
	private Block() { }
	
	/**
	 * 
	 * @param type
	 */
	public Block(BlockType type) {
		blockType = type;
	}
	
	public Block(int colorID) {
		blockType = BlockType.BLOCK;
		// check that provided colorID is within range
		if (colorID >= blockColorCount) {
			colorID = 0;
		} else {
			this.colorID = colorID;
		}
	}
	
	/* Class methods */
	
	public static void initializeBlocks(HashMap<String,Texture> texMap) {
		blockColor = new Sprite[5];
		Texture blockTex = texMap.get("blocksheet");
		blockColor[BLUE] = new Sprite(
				blockTex,
				new int[] { 212, 266 },
				new int[] { 32, 32 },
				new int[] { 32, 32 }
			);
		blockColor[YELLOW] = new Sprite(
				blockTex,
				new int[] { 212, 332 },
				new int[] { 32, 32 },
				new int[] { 32, 32 }
			);
		blockColor[GREEN] = new Sprite(
				blockTex,
				new int[] { 212, 299 },
				new int[] { 32, 32 },
				new int[] { 32, 32 }
			);
		blockColor[RED] = new Sprite(
				blockTex,
				new int[] { 212, 431 },
				new int[] { 32, 32 },
				new int[] { 32, 32 }
			);
		blockColor[PURPLE] = new Sprite(
				blockTex,
				new int[] { 240, 33 },
				new int[] { 32, 32 },
				new int[] { 32, 32 }
			);
		blockTex = texMap.get("yellowtiles");
		blockStar = new Sprite(
				blockTex,
				new int[] {  },
				new int[] {  },
				new int[] { 32, 32 }
			);
	}
	
	/** 
	 * Creates a copy of the object block with default block state values.
	 */
	public Block clone() {
		Block b = new Block();
		b.blockType = this.blockType;
		b.colorID = this.colorID;
		b.block = this.block;
		
		return b;
	}
	
	public void draw(int xc, int yc) {
		switch (blockType) {
			case BLOCK:
				blockColor[colorID].draw(xc, yc);
			case ROCK:
				
				break;
			case STAR:
				break;
			case TRASH:
				break;
			case WEDGE:
				break;
			default:
				break;
		}
	}
	
	/**
	 * 
	 */
	public int activate(Block[][] grid, int xPos, int yPos) {
		int score = 0;
		int stopWidth = grid[0].length - 1;
		int stopHeight = grid.length - 1;
		
		switch (this.blockType) {
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
