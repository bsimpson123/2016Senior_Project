

import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;
/**
 * Manages texture binding and drawing on the screen.
 * @author John Ojala
 *
 */
public class Sprite {
	protected float width, height;
	/** The texture containing the image used for the sprite */
	protected Texture texture;
	
	/**
	 * 
	 * @param tex The Texture object containing the texture that needs to be drawn to the screen
	 * @param left The leftmost pixel that will be drawn to the screen
	 * @param top The topmost pixel that will be drawn to the screen
	 * @param width The pixel width of the part of the texture that needs to be drawn
	 * @param height The pixel height of the part of the texture that needs to be drawn
	 */
	public Sprite(Texture tex, int left, int top, int width, int height) {
		if (tex == null) { throw new NullPointerException("Undefined texture object passed to Sprite constructor."); }
		texture = tex;
		width = texture.getImageWidth();
		height = texture.getImageHeight();
	}
	
	public Sprite(Texture tex) {
		
	}
	
	public void setDrawDimensions(float[] dim) {
		width = dim[0];
		height = dim[1];
	}
		
	public void draw(int x, int y)  {
		// store the current model matrix
		glPushMatrix();
		// bind the texture for drawing
		texture.bind();
		// translate to the right location and prepare to draw
		glTranslatef(x, y, 0); // texture will be drawn at ( x, y )
		
		// draw a quad textured to match the sprite
		glBegin(GL_QUADS);
		{
			glTexCoord2f(0f, 0f);
			glVertex2f(0f, 0f);
			
			glTexCoord2f(0, texture.getHeight());
			glVertex2f(0, height);
			
			glTexCoord2f(texture.getWidth(), texture.getHeight());
			glVertex2f(width, height);
			
			glTexCoord2f(texture.getWidth(), 0);
			glVertex2f(width, 0);
		}
		glEnd();
		
		// restore the model view matrix to prevent contamination
		glPopMatrix();
	}
}
