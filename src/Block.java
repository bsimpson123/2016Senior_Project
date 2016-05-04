import java.util.HashMap;
import org.newdawn.slick.opengl.Texture;

public class Block implements Cloneable {
	/* Public Variables */
	public enum BlockType {
		BLOCK, WEDGE, STAR, TRASH, ROCK, BOMB, HEART
	}

	protected static Sprite blockColor[];
	protected static Sprite bombNumber[];
	protected static Sprite blockStar, blockStarOverlay;
	protected static Sprite blockWedge, blockTrash, blockRock, blockBomb, blockHeart;
	protected static Sprite errorBlock;
	
	public static final int  
		BLUE = 0,
		YELLOW = 1,
		GREEN = 2,
		RED = 3,
		PURPLE = 4,
		GREY = 5
	;
	public static final int blockColorCount = 6;
	
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
	protected int dropDistance = 0;
	protected float dropDistancef = 0f;
	 
	
	/* Constructors */
	public Block(BlockType type, int colorID) {
		this.type = type;
		if ( (colorID >= blockColorCount || colorID < 0) && type == BlockType.BLOCK) {
			colorID = 0; 
		} else if ( (colorID < 2 || colorID > 9) && type == BlockType.BOMB ) {
			colorID = 2; // default,minimum bomb radius, used if set value is out of range
		} else { // colorID may hold state or data information for other block types
			this.colorID = colorID;
		}
		setSprite();
	}
	
	public Block(BlockType type) {
		this.type = type;
		if (type == BlockType.BLOCK) {
			colorID = Global.rand.nextInt(blockColorCount);
		} else if (type == BlockType.BOMB) {
			colorID = 2; // default bomb radius
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
		this.dropDistance = clone.dropDistance;
		this.dropDistancef = clone.dropDistancef;
		
	}
	
	/* Class methods */
	
	public static void initializeBlocks(HashMap<String,Texture> texMap) {
		blockColor = new Sprite[blockColorCount];
		Texture blockTex = texMap.get("blocksheet");
		Texture heartTex = texMap.get("heart");
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
		blockColor[GREY] = new Sprite (
				blockTex,
				new int[] { 212, 431 },
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
				new int[] { 304, 0 },
				new int[] { 32, 32 },
				blockDrawSpace
			);
		blockBomb = new Sprite(
				blockTex,
				new int[] { 272, 0 },
				new int[] { 32, 32 },
				blockDrawSpace
			);
		blockHeart = new Sprite(
				heartTex,
				new int[] { 0, 0 },
				new int[] { 32, 32 },
				blockDrawSpace
			);
		blockRock = new Sprite(
				blockTex,
				new int[] { 272, 32 },
				new int[] { 32, 32 },
				blockDrawSpace
			);
		errorBlock = new Sprite(
				texMap.get("red_ui"),
				new int[] { 381, 36 },
				new int[] { 36, 36 },
				blockDrawSpace
			);
		
		bombNumber = new Sprite[10];
		for (int i = 0; i < 10; i++) {
			if (i < 5) {
				bombNumber[i] = new Sprite(
						texMap.get("bomb_numbers"),
						new int[] { i * 12, 0 },
						new int[] { 10, 16 },
						new int[] { 10, 16 }
					);
			} else {
				bombNumber[i] = new Sprite(
						texMap.get("bomb_numbers"),
						new int[] { (i - 5) * 12, 16 },
						new int[] { 10, 16 },
						new int[] { 10, 16 }
					);
			}
		}
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
				block = blockRock;
				break;
			case BOMB:
				block = blockBomb;
				break;
			case HEART:
				block = blockHeart;
				break;
			default:
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
		} else if (type == BlockType.BOMB) {
			bombNumber[colorID].draw(xc + 9, yc + 7);
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
		} else if (type == BlockType.BOMB) {
			//float factor = size[0] / (float) blockDrawSpace[0];
			bombNumber[colorID].draw(xc + 9, yc + 7);
		}
	}
	
	@Override
	public String toString() {
		String blockID;
		switch (type) {
			case BLOCK:
				blockID = "BLOCK";
				break;
			case WEDGE:
				blockID = "WEDGE";
				break;
			case STAR:
				blockID = "STAR";
				break;
			case ROCK:
				blockID = "ROCK";
				break;
			case TRASH:
				blockID = "TRASH";
				break;
			default:
				blockID = "UNKOWN";
				break;
		}
		return String.format("%s %d c:%s m:%s", blockID, colorID, checked ? "T" : "F", clearMark ? "T" : "F");
	}
}
