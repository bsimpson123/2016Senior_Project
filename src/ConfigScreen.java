import java.util.HashMap;

import org.newdawn.slick.opengl.Texture;

public class ConfigScreen implements GameMode {
	protected LoadState currentState = LoadState.NOT_LOADED;
	protected HashMap<String, Texture> localTexMap = new HashMap<String, Texture>(10);
	
	
	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public LoadState getState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

	}

	@Override
	public void cleanup() {
		// TODO Auto-generated method stub

	}

}
