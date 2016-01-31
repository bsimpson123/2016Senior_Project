import java.util.HashMap;
import org.newdawn.slick.opengl.Texture;

public interface GameMode {
	public enum LoadState {
		NOT_LOADED, LOADING_ASSETS, LOADING_DONE, READY, FINALIZED 
	}
	
	/**
	 * Initialize the game mode, load textures, sounds, and other assets
	 * before transferring control to the game mode.
	 * @param textureMap parent texture map containing referencing already loaded textures.
	 */
	public void initialize(HashMap<String,Texture> textureMap);
	/**
	 * Gets the current operating state of the game mode. 
	 * @return NOT_LOADED if the game mode has not begun loading any assets.
	 * LOADING_ASSETS if the game mode is currently loading assets.
	 * LOADING_DONE if the game mode has finished loading assets.
	 * READY if the game mode can take control of the game loop.
	 * UNLOADING if the game mode is currently unloading private assets.
	 * FINALIZED if the game mode has completed unloading all private assets.
	 */
	public LoadState getState();
	public void run();
	public void cleanup();
}
