import java.util.Random;
import java.awt.Font;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.*;
import org.newdawn.slick.Color;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureImpl;

/* 
 * When adding variables and functions to this class, remember to always mark them
 * as public and static, or they will not be accessible to the rest of the program.
 */


/**
 * Container class for variables and functions that are
 * global in scope.
 * @author John Ojala
 */
public class Global {
	/** Contains publicly loaded texture objects, mapped to string phrase keys  */
	public static HashMap<String, Texture> textureMap = new HashMap<String, Texture>(15);
	public static final Random rand = new Random();
	/** The draw space (width) of the OpenGL environment. */
	public static final int glEnvWidth = 1024;
	/** The draw space (height) of the OpenGL environment. */
	public static final int glEnvHeight = 768;
	/** The time in ms since the last frame update. */
	public static long delta = 0;
	/** The pixel height of the game window, defaults to the same size as the OpenGL environment. */
	public static int winHeight = glEnvHeight;
	/** The pixel width of the game window, defaults to the same size as the OpenGL environment. */
	public static int winWidth = glEnvWidth;
	/** The minimum time (milliseconds) to wait after receiving input before processing further input.
	 * This is the sensitivity of the input. */
	public static long inputReadDelayTimer = 150l;
	public static UIBox 
		uiBlue, uiRed, uiGreen, uiYellow, uiGrey,
		uiBlueSel, uiRedSel, uiGreenSel, uiYellowSel,
		uiTransWhite;
	
    private static TrueTypeFont font;
	
	private static FileWriter logFile;
	
	public static String[][] audioList = {
			new String[] { "button_click", "media/click3.ogg"},
			new String[] { "explo", "media/Explosion.wav"}
	};

	public static GameSounds sounds = new GameSounds(GameSounds.soundType.SOUND,audioList);

	
	public enum GameControl {
		RIGHT, LEFT, UP, DOWN,
		SELECT, CANCEL, PAUSE,
		SPECIAL1, SPECIAL2
	}
	
	// a different variable type may be needed to handle key mapping with a many-to-one keys-to-control implementation 
	private static HashMap<Integer, GameControl> keyMap = new HashMap<Integer, GameControl>();
	private static HashMap<Integer, GameControl> gamepadMap = new HashMap<Integer, GameControl>();
	private static int ctrlID = -1;
	private static Controller[] ctrlList;
	

	public static Controller getController() {
		if (ctrlID == -1) { return null; }
		return ctrlList[ctrlID];
	}
	
	private static void buildControllers() {
		int count = 0;
		int axis = 0;
		try {
			Controllers.create();
			count = Controllers.getControllerCount();
		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		
		ctrlList = new Controller[count];
		for (int i = 0; i < count; i++) {
			ctrlList[i] = Controllers.getController(i);
			writeToLog(String.format("Controller detected: %s\n\tButton count: %d; Axis count: %d\n", 
					ctrlList[i].getName(), ctrlList[i].getButtonCount(), ctrlList[i].getAxisCount()), false);
			axis = ctrlList[i].getAxisCount();
			// Author Brock
			if ( (axis == 2 || axis == 4 || axis == 6) && ctrlList[i].getButtonCount() >= 8 && ctrlList[i].getButtonCount() <= 16 ) {
			
				ctrlID = i;
				break;
			}
		}
		if (ctrlID < 0) {
			writeToLog("No gamepad detected.");
		} else {
			writeToLog(String.format("Controller set: %s; controller ID: %d\n", ctrlList[ctrlID].getName(), ctrlID));
		}
	}
	
	
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
		if (ctrlID == -1) { return false; }
		if (control == GameControl.LEFT) {
			return (ctrlList[ctrlID].getPovX() == -1f);
		} else if (control == GameControl.RIGHT) {
			return (ctrlList[ctrlID].getPovX() == 1f);
		} else if (control == GameControl.UP) {
			return (ctrlList[ctrlID].getPovY() == -1f);
		} else if (control == GameControl.DOWN) {
			return (ctrlList[ctrlID].getPovY() == 1f);
		} else {
			for (int gpKey : gamepadMap.keySet()) {
				if (gamepadMap.get(gpKey) == control) {
					if (ctrlList[ctrlID].isButtonPressed(gpKey)) { return true; }
				}
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
	
	public static boolean setGamePadMap(GameControl control, int key) {
		if (gamepadMap.containsKey(key)) {
			return false;
		}
		gamepadMap.put(key, control);
		return true;
	}
	
	public static void breakGamePadMap(int key) {
		if (gamepadMap.containsKey(key)) {
			gamepadMap.remove(key);
		}
	}
	
	public static void initLog() {
		LocalDateTime time = LocalDateTime.now();
		String filename = String.format("%1$tF %1$tH%1$tI.log", time);
		try {
			logFile = new FileWriter(filename);
		} catch (IOException e) {
			System.out.println("Unable to create log file.");
			e.printStackTrace();
		} 
		
	}
	public static void writeToLog(String text) { writeToLog(text, false); }
	public static void writeToLog(String text, boolean writeToConsole) {
		if (writeToConsole) {
			System.out.println(text);
		}
		if (logFile == null) { return ; }
		try {
			logFile.write(text);
			logFile.write("\n");
			logFile.flush();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}
	
	private static void closeLog() {
		if (logFile != null) {
			try {
				logFile.close();
			} catch (IOException e) {
				System.out.println("Error closing log file.");
				e.printStackTrace();
			}
		}
	}
	
	public static void globalInit() {
		initLog();
		buildControllers();
		initFonts();
		
	}
	
	private static void initFonts() {
		TextureImpl.bindNone();
        Font awtFont = new Font("SketchFlow Print", Font.BOLD, 20);
		//Font awtFont = new Font("Lucida Handwriting", Font.BOLD, 20);
		//Font awtFont = new Font("Lucida Calligraphy", Font.BOLD, 20);
        font = new TrueTypeFont(awtFont, true);

	}
	
	public static void buildStandardUIBoxes() {
		int[] corner = new int[] { 16, 16 };
		Texture tex = textureMap.get("uibox");
		uiBlue = buildBox(tex, new int[] { 0, 0 }, corner);
		uiRed = buildBox(tex, new int[] { 48, 0 }, corner);
		uiGreen = buildBox(tex, new int[] { 96, 0 }, corner);
		uiYellow = buildBox(tex, new int[] { 144, 0 }, corner);
		uiGrey = buildBox(tex, new int[] { 192, 0 }, corner);
		uiBlueSel = buildBox(tex, new int[] { 0, 96 }, corner);
		uiRedSel = buildBox(tex, new int[] { 48, 96 }, corner);
		uiGreenSel = buildBox(tex, new int[] { 96, 96 }, corner);
		uiYellowSel = buildBox(tex, new int[] { 144, 96 }, corner);
		
		uiTransWhite = buildBox(tex, new int[] { 192, 48 }, corner);
	}
	
	private static UIBox buildBox(Texture tex, int[] offset, int[] corner) {
		Sprite[][] boxParts = new Sprite[3][3];
		int[] point = new int[] { 0, 0 };
		for (int i = 0; i < 3; i++) {
			point[0] = i * corner[0] + offset[0];
			for (int k = 0; k < 3; k++) {
				point[1] = k * corner[1] + offset[1];
				boxParts[i][k] = new Sprite( tex, point, corner, corner);
			}
		}
		return new UIBox(boxParts, corner);
	}
	
	public static void globalFinalize() {
		closeLog();
		for (String ref : Global.textureMap.keySet()) {
			Global.textureMap.get(ref).release();
		}
		Global.textureMap.clear();

	}
	
	public static void drawStringDefaultFont(int xc, int yc, String text, Color color) {
		TextureImpl.bindNone();
		Color.white.bind();
		font.drawString(xc, yc, text, color);
		font.drawString(0, 0, " ", Color.white);
	}
	
	public static int getDrawSize(String text) {
		return font.getWidth(text);
	}
}
