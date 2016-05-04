import org.newdawn.slick.opengl.Texture;

public class AnimatedSprite extends Sprite {

	public AnimatedSprite(Texture tex, int[] texPos, int[] texSize, int[] drawSize) {
		super(tex, texPos, texSize, drawSize);
		// TODO Auto-generated constructor stub
	}

	public AnimatedSprite(Texture tex, int[] drawSize) {
		super(tex, drawSize);
		// TODO Auto-generated constructor stub
	}

	public AnimatedSprite(Texture tex, int[] cellSize, int[] cellCount) {
		super(tex, cellSize);
	}
	
	
}
