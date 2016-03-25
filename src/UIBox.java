/**
 * 
 * @author John
 */
public class UIBox {
	private final Sprite[][] box;
	private final int[] edge;
	private final int[][][] shift = new int[3][3][];
	
	public UIBox(Sprite[][] boxParts, int[] cornerDim) {
		box = boxParts;
		edge = cornerDim.clone();
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				shift[x][y] = new int[] { 0, 0 };
			}
		}
	
	}
	
	public void draw(int xc, int yc, int width, int height) {
		int[] drawSpace = edge.clone();
		int[] xy = new int[2];
		for (int i = 0; i < 3; i++) {
			for (int k = 0; k < 3; k++) {
				if (i == 0) { // left edge
					xy[0] = xc; 
					drawSpace[0] = edge[0];
				} else if (i == 1) { // center horizontal
					xy[0] = xc + edge[0];
					drawSpace[0] = width - (2 * (edge[0])); 
				} else { // right edge
					xy[0] = xc + width - edge[0];
					drawSpace[0] = edge[0]; 
				}
				if (k == 0) { // top edge
					xy[1] = yc;
					drawSpace[1] = edge[1];
				} else if (k == 1) { // center vertical
					xy[1] = yc + edge[1];
					drawSpace[1] = height - (2 * (edge[1])); 
				} else { // bottom edge
					xy[1] = yc + height - edge[1];
					drawSpace[1] = edge[1];
				}
				box[i][k].draw(xy, drawSpace);
			}
		}
		
	}
}
