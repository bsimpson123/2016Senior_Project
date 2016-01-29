

import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;
/**
 * Manages texture binding and drawing on the screen.
 * @author John Ojala
 *
 */
public class Sprite {
	protected final float width, height, left, top;
	protected final int[] drawSpace;
	/** The texture containing the image used for the sprite */
	protected final Texture texture;
	
	/**
	 * Defines a sprite for drawing on the screen using a subsection of the provided texture
	 * @param tex The Texture object containing the sub-texture to be drawn 
	 * @param texPos 2-element array indicating the pixel distance from the left and top, respectively, 
	 * where the image to be drawn is located within the texture
	 * @param texSize 2-element array indicating the pixel distance width and height, respectively, 
	 * of the image to be drawn within the texture.
	 * @param drawSize 2-element array defining width and height that the sprite will take up on the screen when drawn.
	 * This value does not have to correlate to texture or sub-texture dimensions.
	 */
	public Sprite(Texture tex, int[] texPos, int[] texSize, int[] drawSize) {
		if (tex == null) { throw new NullPointerException("Undefined texture object passed to Sprite constructor."); }
		if (texPos.length < 2 || texSize.length < 2 || drawSize.length < 2) {
			throw new IllegalArgumentException("Invalid array dimensions provided to Sprite constructor.");
		}
		texture = tex;
		this.width = (float) texSize[0] / texture.getImageWidth();
		this.height = (float) texSize[1] / texture.getImageHeight();
		this.top = (float) texPos[1] / texture.getImageHeight();
		this.left = (float) texPos[0] / texture.getImageWidth();
		this.drawSpace = drawSize.clone();
	}
	
	/**
	 * Defines a sprite for drawing on the screen using all of the provided texture.
	 * @param tex The Texture object containing the texture to draw
	 * @param drawSize 2-element array defining width and height that the sprite will take up on the screen when drawn.
	 * This value does not have to correlate to texture dimensions.
	 */
	public Sprite(Texture tex, int[] drawSize) {
		texture = tex;
		width = 1.0f;
		height = 1.0f;
		top = 0.0f;
		left = 0.0f;
		this.drawSpace = drawSize.clone();
	}
	
	public void draw(int x, int y)  {
		// store the current model matrix
		glPushMatrix();
		glMatrixMode(GL_MODELVIEW);
		// bind the texture for drawing
		texture.bind();
		// translate to the right location and prepare to draw
		glTranslatef(x, y, 0); // texture will be drawn at ( x, y )
		
		// draw a quad textured to match the sprite
		glBegin(GL_QUADS);
		{
			glTexCoord2f(left, top);
			glVertex2i(0, 0);
			
			glTexCoord2f(left, top + height);
			glVertex2i(0, drawSpace[1]);
			
			glTexCoord2f(left + width, top + height);
			glVertex2i(drawSpace[0], drawSpace[1]);
			
			glTexCoord2f(left + width, top);
			glVertex2i(drawSpace[0], 0);
		}
		glEnd();
		
		// restore the model view matrix to prevent contamination
		glPopMatrix();
	}
}
