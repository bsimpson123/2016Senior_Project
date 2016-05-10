import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;

public class AnimatedSprite extends Sprite {
	/** The dimension of each cell within the texture. */
	private final int[] cellSize;
	/** The cell grid layout within the texture. */
	private final int[] texCellLayout;
	/*  */
	private final int[] cellTexStartCoord;
	/** The amount of negative (empty) space between each cell frame. */
	private int[] cellFrameBuffer;
	/** Identifies which cell in <code>texCellLayout</code> is the current frame. */
	private int[] activeCell;
	/** The amount of time to wait between each frame. */
	private final long frameDelayTimer;
	/** The time remaining for displaying the current frame. */
	private long frameDelay;
	/** The number of empty cells present in the last row of <code>texCellLayout</code>. */
	private int emptyCells;
	/*  */
	private float[] cellDrawOffset;
	/** The number of times to repeat the animation, -1 to repeat endlessly. */
	private int repeatCount = -1;
	/** Indicates if the animation has completed the required (limited) number of animation loops. */
	private boolean finished = false;
	/** OpenGL texture float offsets for drawing frames. */
	private final int[] glDrawSpace;

	/**
	 * 
	 * @param tex <code>Texture</code> object containing the animation cells
	 * @param cellSize The width and height of an individual cell as array
	 * @param cellArangement The grid arrangement of the animation cells within the texture as [row count][column count]
	 * @param frameDelayTime The amount of time (milliseconds) to wait between each frame
	 * @param playTimes The number of times to play the animation sequence. Zero or negative values will cause
	 * the animation to repeat forever.
	 */
	public AnimatedSprite(Texture tex, int[] cellSize, int[] cellArangement, int frameDelayTime, int playTimes) {
		super(tex);		
		glDrawSpace = new int[] { 
			tex.getImageWidth(),
			tex.getImageHeight()
		};
		cellTexStartCoord = new int[] { 0, 0 };
		cellFrameBuffer = new int[] { 0, 0 };
		frameDelayTimer = frameDelayTime;
		frameDelay = frameDelayTimer;
		repeatCount = playTimes;
		texCellLayout = cellArangement.clone();
		this.cellSize = cellSize.clone();
		activeCell = new int[] { 0, 0 };
	}
	
	public AnimatedSprite(Texture tex, int[] cellSize, int frameDelayTime, int playTimes) {
		super(tex);		
		glDrawSpace = new int[] { 
			tex.getImageWidth(),
			tex.getImageHeight()
		};
		cellTexStartCoord = new int[] { 0, 0 };
		cellFrameBuffer = new int[] { 0, 0 };
		frameDelayTimer = frameDelayTime;
		frameDelay = frameDelayTimer;
		repeatCount = playTimes;
		texCellLayout = new int[] {
			( (int) tex.getHeight() ) / cellSize[0],
			( (int) tex.getWidth() ) / cellSize[1]
		};
		this.cellSize = cellSize.clone();
		activeCell = new int[] { 0, 0 };
	}
	
	public boolean isFinished() { return finished; }
	
	public void update(long delta) {
		frameDelay -= delta;
		if (frameDelay > 0) {
			return ;
		}
		frameDelay += frameDelayTimer;
		activeCell[0]++;
		if (activeCell[0] == texCellLayout[0]) {
			activeCell[0] = 0;
			activeCell[1]++;
		}
		if (activeCell[1] == texCellLayout[1]) {
			activeCell[1] = 0;
		} else if (activeCell[1] == (texCellLayout[1] - 1) && (activeCell[0] + emptyCells) == texCellLayout[0]) {
			repeatCount -= 1;
			if (repeatCount == 0) { // do not check for less than 1, negative values indicate repeat forever
				finished = true;
			}
			activeCell[0] = 0;
			activeCell[1] = 0;
		}
	}
	
	@Override
	public void draw(int x, int y) {
		if (finished) { return; }
		glPushMatrix();
		glMatrixMode(GL_MODELVIEW);
		texture.bind();
		glTranslatef(x, y, 0); // texture will be drawn at ( x, y )
		
		float[] cellStart = new float[2];
		cellStart[0] = ( (float) activeCell[0] * (cellSize[0] + cellFrameBuffer[0]) ) / glDrawSpace[0];
		cellStart[1] = ( (float) activeCell[1] * (cellSize[1] + cellFrameBuffer[1]) ) / glDrawSpace[1];
		float[] cellStop = new float[2];
		cellStop[0] = cellStart[0] + ( (float) cellSize[0] ) / glDrawSpace[0];
		cellStop[1] = cellStart[1] + ( (float) cellSize[1] ) / glDrawSpace[1];
		
		// draw a quad textured to match the sprite
		glBegin(GL_QUADS);
		{
			glTexCoord2f(cellStart[0], cellStart[1]);
			glVertex2i(0, 0);
			
			glTexCoord2f(cellStart[0], cellStop[1]);
			glVertex2i(0, drawSpace[1]);
			
			glTexCoord2f(cellStop[0], cellStop[1]);
			glVertex2i(drawSpace[0], drawSpace[1]);
			
			glTexCoord2f(cellStop[0], cellStart[1]);
			glVertex2i(drawSpace[0], 0);
		}
		glEnd();
		glPopMatrix();

	}
	
	
}
