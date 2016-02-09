import java.util.HashMap;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;

public class Block {
	/* Public Variables */
	public enum BlockType {
		BLOCK, WEDGE, STAR, TRASH, ROCK
	}

	protected static Sprite blockColor[];
	protected static Sprite blockStar, blockStarOverlay;
	protected static Sprite blockWedge, blockTrash, blockRock;
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
	public boolean checked = false;
	public boolean clearMark = false;
	public int dropDistance = 0;
	 
	
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
				new int[] { 212, 233 },
				new int[] { 32, 32 },
				blockDrawSpace
			);
		blockColor[PURPLE] = new Sprite(
				blockTex,
				new int[] { 240, 33 },
				new int[] { 32, 32 },
				blockDrawSpace
			);
		blockStar = new Sprite(
				blockTex,
				new int[] { 65, 361 },
				new int[] { 48, 48 },
				blockDrawSpace
			);
		blockStarOverlay = new Sprite(
				texMap.get("yellow_ui"),
				new int[] { 417, 0 },
				new int[] { 32, 32 },
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
		if (type == BlockType.STAR) {
			blockStarOverlay.draw(xc, yc);
		}
	}

	public void draw(int xc, int yc, int[] size) {
		if (block == null) {
			errorBlock.draw(xc, yc, size);
			return ;
		}
		block.draw(xc, yc, size);
		if (type == BlockType.STAR) {
			blockStarOverlay.draw(xc, yc, size);
		}
	}
}
