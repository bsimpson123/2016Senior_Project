import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;

public class UIBox {
	private Texture texture;
	private float[][][] texCoords = new float[3][3][4];
	private final int[] minDraw;
	/** The width and height of the edges. Top and bottom edges need to be the same height, left and right the same width. */
	private final int[] buffers;
	
	/**
	 * 
	 * @param boxTex The texture containing the user interface box parts to draw 
	 * @param boxPartCoords a 3-dimensional grid containing the start and end pixel coordinates for each part.
	 * [0][0] is top-left corner, [2][2] is bottom-right corner. 3rd dimension is { x-start, y-start, e-end, y-end }
	 * within the texture.
	 * @throws Exception if 
	 */
	public UIBox(Texture boxTex, int[][][] boxPartCoords) throws Exception {
		if (boxTex == null) { throw new NullPointerException("null value passed to UIBox constructor for Texture boxTex."); }
		if (boxPartCoords == null) { throw new NullPointerException("null value passed tp UIBox constructor for int[][][] boxPartCoords"); }
		texture = boxTex;
		int minX = 0, minY = 0;
		try {
			for (int i = 0; i < 3; i++) {
				for (int k = 0; k < 3; k++) {
					texCoords[i][k][0] = (float) boxPartCoords[i][k][0] / boxTex.getImageWidth();
					texCoords[i][k][1] = (float) boxPartCoords[i][k][1] / boxTex.getImageHeight();
					texCoords[i][k][2] = (float) boxPartCoords[i][k][2] / boxTex.getImageWidth();
					texCoords[i][k][3] = (float) boxPartCoords[i][k][3] / boxTex.getImageHeight();
					minX += boxPartCoords[i][k][2] - boxPartCoords[i][k][0];
					minY += boxPartCoords[i][k][3] - boxPartCoords[i][k][1];
				}
			}
		} catch (IndexOutOfBoundsException ioobe) {
			throw new Exception ("Incomplete box texture coordinates in UIBox constructor.");
		}
		minDraw = new int[] { minX, minY };
		buffers = new int[] { boxPartCoords[0][0][2], boxPartCoords[0][0][3] };
	}
	
	
	public void draw(int[] topLeft, int[] dimensions) {
		if (dimensions[0] < minDraw[0] || dimensions[1] < minDraw[1]) {
			throw new IllegalArgumentException("Specified draw dimensions in UIBox.draw were too small.");
		}
		dimensions[0]--;
		dimensions[1]--;
		int midWidth = dimensions[0] - (2 * buffers[0]);
		int midHeight = dimensions[1] - (2 * buffers[1]);
		glPushMatrix();
		glMatrixMode(GL_MODELVIEW);
		texture.bind();
		glTranslatef(topLeft[0], topLeft[1], 0);
		
		glBegin(GL_QUADS);
		{
			// top-left corner
			glTexCoord2f(texCoords[0][0][0], texCoords[0][0][1]);
			glVertex2i(0, 0);
			glTexCoord2f(texCoords[0][0][0], texCoords[0][0][3]);
			glVertex2i(0, buffers[1]);
			glTexCoord2f(texCoords[0][0][2], texCoords[0][0][3]);
			glVertex2i(buffers[0], buffers[1]);
			glTexCoord2f(texCoords[0][0][2], texCoords[0][0][1]);
			glVertex2i(buffers[0], 0);
			
			// top-middle edge
			//glTexCoord2f(texCoords[0][1][0], texCoords[0][1][1]);
			
			
		}; glEnd();
		
		
		glPopMatrix();
	}
	
}
