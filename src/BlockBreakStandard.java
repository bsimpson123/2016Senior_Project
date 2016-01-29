import java.util.HashMap;
import org.newdawn.slick.opengl.Texture;

public class BlockBreakStandard implements GameMode {
	protected LoadState currentState = LoadState.NOT_LOADED;
	protected HashMap<String, Texture> rootTexMap = null;
	protected HashMap<String, Texture> localTexMap = new HashMap<String, Texture>(10);
	protected Block[][] grid = new Block[20][30];
	@Override
	public void initialize(HashMap<String, Texture> textureMap) {
		rootTexMap = textureMap;
		// TODO Auto-generated method stub

	}

	@Override
	public LoadState getState() {
		return currentState;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		for (String ref : localTexMap.keySet()) {
			localTexMap.get(ref).release();
		}
		localTexMap.clear();
	}

}
