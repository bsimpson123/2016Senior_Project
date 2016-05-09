import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;

public class AnimatedSprite extends Sprite {
	private final int[] cellSize;
	private final int[] texCellLayout;
	private final int[] cellTexStartCoord;
	private int[] cellFrameBuffer;
	private int[] activeCell;
	private final long frameDelayTimer;
	private long frameDelay;
	private int emptyCells;
	private float[] cellDrawOffset;
	private final boolean repeat;
	private boolean finished = false;
	private final int[] glDrawSpace;

	public AnimatedSprite(Texture tex, int[] cellSize, int[] cellSetup, int frameDelayTime, boolean repeats) {
		super(tex);		
		glDrawSpace = new int[] { 
			tex.getImageWidth(),
			tex.getImageHeight()
		};
		cellTexStartCoord = new int[] { 0, 0 };
		cellFrameBuffer = new int[] { 0, 0 };
		frameDelayTimer = frameDelayTime;
		frameDelay = frameDelayTimer;
		repeat = repeats;
		texCellLayout = cellSetup.clone();
		this.cellSize = cellSize.clone();
		activeCell = new int[] { 0, 0 };
	}
	
	public AnimatedSprite(Texture tex, int[] cellSize, int frameDelayTime, boolean repeats) {
		super(tex);		
		glDrawSpace = new int[] { 
			tex.getImageWidth(),
			tex.getImageHeight()
		};
		cellTexStartCoord = new int[] { 0, 0 };
		cellFrameBuffer = new int[] { 0, 0 };
		frameDelayTimer = frameDelayTime;
		frameDelay = frameDelayTimer;
		repeat = repeats;
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
			activeCell[0] = 0;
			activeCell[1] = 0;
		}
	}
	
	@Override
	public void draw(int x, int y) {
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
