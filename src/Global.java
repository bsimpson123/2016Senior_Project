import java.util.Random;
import java.util.HashMap;
import org.lwjgl.input.Keyboard;

/* 
 * When adding variables and functions to this class, remember to always mark them
 * as public and static, or they will not be accessible to the rest of the program.
 */


/**
 * Container class for variables and functions that are
 * global in scope.
 * @author John Ojala
 *
 */
public class Global {
	public static Random rand = new Random();
	/** The draw space (width) of the OpenGL environment. */
	public static final int glEnvWidth = 800;
	/** The draw space (height) of the OpenGL environment. */
	public static final int glEnvHeight = 600;
	/** The time in ms since the last frame update. */
	public static long delta = 0;
	/** The pixel height of the game window, defaults to the same size as the OpenGL environment. */
	public static int winHeight = glEnvHeight;
	/** The pixel width of the game window, defaults to the same size as the OpenGL environment. */
	public static int winWidth = glEnvWidth;
	
	
	public enum GameControl {
		RIGHT, LEFT, UP, DOWN,
		SELECT, CANCEL, PAUSE,
		SPECIAL
	}
	
	// a different variable type may be needed to handle key mapping with a many-to-one keys-to-control implementation 
	private static HashMap<Integer, GameControl> keyMap = new HashMap<Integer, GameControl>();
	

	/**
	 * Checks if keys mapped to the specified game control are pressed.
	 * @param control The game controls to check input for
	 * @return true if any key mapped to the specified control is pressed,
	 * false if no keys assigned to the control as pressed. 
	 */
	public static boolean getControlActive(GameControl control) {
		
		for (int kbKey : keyMap.keySet()) {
			if (keyMap.get(kbKey) == control) {
				if (Keyboard.isKeyDown(kbKey)) { return true; }
			}
		}
		return false;
	}
	
	/**
	 * Maps a keyboard key to a specific game control. Multiple keys can be mapped to a single
	 * control, but each key can only be mapped to a single control. 
	 * @param control The game control that the is a key is being assigned to
	 * @param key The key input that will be assigned to the control
	 * @return true if the mapping was successful, false if the key was already assigned to
	 * another game control.
	 */
	public static boolean setKeyMap(GameControl control, int key) {
		if (keyMap.containsKey(key)) {
			return false;
		}
		keyMap.put(key, control);
		return true;
	}
	
	public static void breakKeyMap(int key) {
		if (keyMap.containsKey(key)) {
			keyMap.remove(key);
		}
	}

}
