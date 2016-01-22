

import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;
/**
 * Breaks a texture object into virtual cells and cycles through those cells during draw calls.
 * Drawn cells will advance automatically based on time delta since last draw. 
 * @author John Ojala
 */
public class AnimatedSprite extends Sprite {
	// specify the starting and stopping limits within a sprite sheet
	private final int cellStartLeft;
	private final int cellStopLeft;
	private final int cellStartTop;
	private final int cellStopTop;

	private final int cellWidth;
	private final int cellHeight;
	private int xIndex;
	private final int rowElements;
	private int yIndex;
	private final int rowCount;
	private final int frameDelay;
	private int frameDelayRemaining;
	private int emptyEndCells;
	
	/**
	 * Create a new AnimatedSprite object that will draw animated cells of a texture to the screen
	 * @param tex The texture holding all the cells to be drawn
	 * @param cellWidth The width of individual cells
	 * @param cellHeight The height of individual cells
	 * @param frameDelay The number of milliseconds between each frame draw
	 * @param emptyTerminatingCells The number of empty cells in the last row
	 */
	public AnimatedSprite(Texture tex, int cellWidth, int cellHeight, int frameDelay, int emptyTerminatingCells) { 
		super(tex);
		if (cellWidth < 1) { throw new IllegalArgumentException("cellWidth cannot be less than 1"); }
		if (cellHeight < 1) { throw new IllegalArgumentException("cellHeight cannot be less than 1"); }
		if (frameDelay < 1) { throw new IllegalArgumentException("frameDelay cannot be less that 1"); }
		this.cellWidth = cellWidth;
		this.cellHeight = cellHeight;
		xIndex = 0;
		yIndex = 0;
		rowElements = width / cellWidth;
		rowCount = height / cellHeight;
		if (emptyTerminatingCells < 0 || emptyTerminatingCells > rowElements) { 
			throw new IllegalArgumentException("emptyTerminatingCells cannot be negative and must be less than the number of cells per row");
		}
		this.frameDelay = frameDelay;
		frameDelayRemaining = frameDelay;
		cellStartLeft = 0;
		cellStopLeft = (int) tex.getWidth();
		cellStartTop = 0;
		cellStopTop = (int) tex.getHeight();
	}
	
	public AnimatedSprite(Texture tex, int[] cellDimensions, int frameDelay, int[] texBounds) {
		super(tex);
		if (cellDimensions[0] < 1 || cellDimensions[2] < 1) {
			throw new IllegalArgumentException("Cell dimensions cannot be less than 1.");
		}
		if (frameDelay < 1) {
			throw new IllegalArgumentException("Cell frame delay cannot be less than 1");
		}
		if (texBounds[0] < 1 || texBounds[1] < 1) {
			throw new IllegalArgumentException("Cell texture bounds cannot be less than 1.");
		}
		this.cellWidth = cellDimensions[0];
		this.cellHeight = cellDimensions[1];
		this.frameDelay = frameDelay;
		this.cellStartLeft = texBounds[0];
		this.cellStartTop = texBounds[1];
		this.cellStopLeft = texBounds[2];
		this.cellStopTop = texBounds[3];
		this.rowElements = width / this.cellWidth;
		this.rowCount = height / this.cellHeight;
	}
	
	/**
	 * Advances the cell index based on the number of milliseconds passed
	 * @param delta The number of milliseconds passed
	 */
	public void advanceFrameTime(int delta) {
		frameDelayRemaining -= delta;
		while (frameDelayRemaining <= 0) {
			frameDelayRemaining += frameDelay;
			xIndex++;
			if (xIndex == rowElements) {
				xIndex = 0;
				yIndex++;
			} else if (yIndex + 1 == rowCount && xIndex == rowElements - emptyEndCells) {
				xIndex = 0;
				yIndex++;
			}
			if (yIndex == rowCount) {
				yIndex = 0;
			}
		}
	}
	
	@Override
	public int getWidth() { return cellWidth; }
	
	@Override
	public int getHeight() { return cellHeight; }

	@Override
	/**
	 * Draws the current sprite cell to the screen without advancing the cell index.
	 * Sprite.advanceFrameTime(int) should be called in the game loop before draw.
	 * @param x The center x coordinate of the draw
	 * @param y The center y coordinate of the draw
	 */
	public void draw(int x, int y) { 
		float texCellStartY = yIndex * cellHeight;
		float texCellStartX = xIndex * cellWidth;
		
		// store the current model matrix
		glPushMatrix();
		// bind to the appropriate texture for this sprite
		texture.bind();
		// translate to the right location and prepare to draw
		glTranslatef(x - centerHeightOffset, y - centerHeightOffset, 0); // texture will be drawn centered at ( x, y )
		
		// draw a quad textured to match the sprite
		glBegin(GL_QUADS);
		{
			glTexCoord2f(texCellStartX, texCellStartY);
			glVertex2f(0, 0);
			
			glTexCoord2f(texCellStartX, texCellStartY + cellHeight);
			glVertex2f(0, cellHeight);
			
			glTexCoord2f(texCellStartX + cellWidth, texCellStartY + cellHeight);
			glVertex2f(cellWidth, cellHeight);
			
			glTexCoord2f(texCellStartX + cellWidth, texCellStartY);
			glVertex2f(cellWidth, 0);
		}
		glEnd();
		
		// restore the model view matrix to prevent contamination
		glPopMatrix();
	}

}
