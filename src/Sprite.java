

import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;
/**
 * Manages texture binding and drawing on the screen.
 * @author John Ojala
 *
 */
public class Sprite {
	protected int width, height;
	/** Height/Width offset used to center the sprite when drawing */
	protected int centerWidthOffset, centerHeightOffset;
	/** The texture holding the image used for the sprite */
	protected Texture texture;
	
	public Sprite(Texture tex) {
		if (tex == null) { throw new NullPointerException("Undefined texture object passed to Sprite constructor."); }
		texture = tex;
		width = texture.getImageWidth();
		height = texture.getImageHeight();
		centerWidthOffset = width / 2;
		centerHeightOffset = height / 2;
	}
	
	
	public int getWidth() { return texture.getImageWidth(); }
	public int getHeight() { return texture.getImageHeight(); }
	
	public void draw(int x, int y)  {
		// store the current model matrix
		glPushMatrix();
		// bind to the appropriate texture for this sprite
		texture.bind();
		// translate to the right location and prepare to draw
		glTranslatef(x - centerWidthOffset, y - centerHeightOffset, 0); // texture will be drawn centered at ( x, y )
		
		// draw a quad textured to match the sprite
		glBegin(GL_QUADS);
		{
			glTexCoord2f(0, 0);
			glVertex2f(0, 0);
			
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
