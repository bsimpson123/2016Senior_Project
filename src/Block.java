import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.util.Color;
import org.newdawn.slick.opengl.Texture;

public class Block {
	/* Public Variables */
	public enum BlockType {
		BLOCK, WEDGE, STAR, TRASH, ROCK
	}
	public static Color[] BlockColors = new Color[] {
			new Color(Color.BLUE),
			new Color(Color.GREEN),
			new Color(Color.YELLOW),
			new Color(Color.RED),
			new Color(Color.PURPLE)
	};

	/* Private variables */
	/* Define the draw space that a block will take up in the grid.
	 * This value is independent from the texture size. */
	protected final int drawHeight = 32;
	protected final int drawWidth = 32;
	
	
	protected BlockType blockType;
	private int colorID = -1;
	/** Collection of colors that will be used for standard blocks. */
	private Texture texture;
	private int texTop;
	private int texHeight;
	private int texLeft;
	private int texWidth;
	
	/** Indicates whether the block has been checked for processing during a game loop */
	private boolean checked = false;
	
	/* Constructors */
	/**
	 * Empty constructor used when cloning blocks.
	 */
	private Block() { }
	
	public Block(BlockType type) {
		blockType = type;
		setBlock();
	}
	
	public Block(BlockType type, int colorID) {
		blockType = type;
		this.colorID = colorID;
		// check that provided colorID is within range
		if (colorID < 0 || colorID >= BlockColors.length) {
			colorID = Global.rand.nextInt(BlockColors.length);
		}
	}
	
	/* Class methods */
	
	/** 
	 * Creates a copy of the object block with default block state values.
	 */
	public Block clone() {
		Block b = new Block();
		b.blockType = this.blockType;
		b.texTop = this.texTop;
		b.texHeight = this.texHeight;
		b.texLeft = this.texLeft;
		b.texWidth = this.texWidth;
		b.colorID = this.colorID;
		
		return b;
	}
	
	
	/**Sets local variables to appropriate values for the specific block type
	 * 
	 */
	private void setBlock() {
		switch (blockType) {
		case BLOCK: // basic color block

			break;
		case WEDGE:
			
			break;
		case STAR:
			
			break;
		case TRASH:
			
			break;
		case ROCK:
			
			break;
		default: // should not happen
			
			break;	
		}
	}
	
	public void draw(float xc, float yc) {
		// store the current model matrix
		glPushMatrix();
		// bind to the appropriate texture for this sprite
		texture.bind();
		// translate to the right location and prepare to draw
		glTranslatef(xc, yc, 0); // texture will be drawn from corner set at ( x, y )
		
		// draw a quad textured to match the sprite
		glBegin(GL_QUADS);
		{
			glTexCoord2f(texLeft, texTop);
			glVertex2f(0, 0);
			
			glTexCoord2f(0, texTop + texHeight);
			glVertex2f(0, drawHeight);
			
			glTexCoord2f(texLeft + texWidth, texTop + texHeight);
			glVertex2f(drawWidth, drawHeight);
			
			glTexCoord2f(texLeft + texWidth, 0);
			glVertex2f(drawWidth, 0);
		}
		glEnd();
		
		// restore the model view matrix to prevent contamination
		glPopMatrix();
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
