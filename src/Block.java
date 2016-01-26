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
			new Color(Color.YELLOW),
			new Color(Color.GREEN),
			new Color(Color.RED),
			new Color(Color.PURPLE)
	};

	public static final int  
		BLUE = 0,
		YELLOW = 1,
		GREEN = 2,
		RED = 3,
		PURPLE = 4
	;
	/* Protected variables */
	/* Define the draw space that a block will take up in the grid.
	 * This value is independent from the texture size and will ensure
	 * each block takes up the same space on the grid. */
	protected final int drawHeight = 32;
	protected final int drawWidth = 32;
		
	protected BlockType blockType;
	protected int colorID = -1;
	/** Collection of colors that will be used for standard blocks. */
	protected Texture texture;
	protected int texTop; protected float texTopf;
	protected int texTopStop; protected float texTopStopf;
	protected int texLeft; protected float texLeftf;
	protected int texLeftStop; protected float texLeftStopf;
	
	/** Indicates whether the block has been checked for processing during a game loop.
	 * This value should be reset to false before logic processing each game loop. */
	private boolean checked = false;
	
	/* Constructors */
	/**
	 * Empty constructor used when cloning blocks.
	 */
	private Block() { }
	
	public Block(BlockType type) {
		blockType = type;
		setTextureSubset();
		calculateDrawPoints();
	}
	
	public Block(int colorID) {
		blockType = BlockType.BLOCK;
		// check that provided colorID is within range
		if (colorID >= BlockColors.length) {
			colorID = 0;
		} else {
			this.colorID = colorID;
		}
		setTextureSubset();
		calculateDrawPoints();
	}
	
	/* Class methods */
	
	/** 
	 * Creates a copy of the object block with default block state values.
	 */
	public Block clone() {
		Block b = new Block();
		b.blockType = this.blockType;
		b.texTop = this.texTop;
		b.texTopf = this.texTopf;
		b.texTopStop = this.texTopStop;
		b.texTopStopf = this.texTopStopf;
		b.texLeft = this.texLeft;
		b.texLeftf = this.texLeftf;
		b.texLeftStop = this.texLeftStop;
		b.texLeftStopf = this.texLeftStopf;
		
		b.colorID = this.colorID;
		
		return b;
	}
	
	private void setTextureSubset() {
		// TODO: set variables for draw range for the appropriate color block
		switch (blockType) {
		case BLOCK: // basic color block
			switch (colorID) {
				case BLUE:
					
					break;
				case YELLOW:
					
					break;
				case GREEN:
					
					break;
				case RED:
					
					break;
				case PURPLE:
					
					break;
				default:
					break;
			}
			
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
	
	private void calculateDrawPoints() {
		texTopf = texTop / texture.getHeight();
		texTopStopf = texTopStop / texture.getHeight();
		texLeftf = texLeft / texture.getWidth();
		texLeftStopf = texLeftStop / texture.getWidth();
	}
	
	/**Sets local variables to appropriate values for the specific block type
	 * 
	 */
	private void setBlock() {
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
			
			glTexCoord2f(0, texTop + texTopStop);
			glVertex2f(0, drawHeight);
			
			glTexCoord2f(texLeft + texLeftStop, texTop + texTopStop);
			glVertex2f(drawWidth, drawHeight);
			
			glTexCoord2f(texLeft + texLeftStop, 0);
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
