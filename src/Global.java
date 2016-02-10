import java.util.Random;

import javax.print.attribute.standard.DateTimeAtCompleted;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Formatter;
import java.util.HashMap;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.opengl.Texture;

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
	public static Random rand = new Random();
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
	public static long inputReadDelayTimer = 120l;
	private static FileWriter logFile;
	
	
	public enum GameControl {
		RIGHT, LEFT, UP, DOWN,
		SELECT, CANCEL, PAUSE,
		SPECIAL
	}
	
	// a different variable type may be needed to handle key mapping with a many-to-one keys-to-control implementation 
	private static HashMap<Integer, GameControl> keyMap = new HashMap<Integer, GameControl>();
	private static HashMap<Integer, GameControl> gamepadMap = new HashMap<Integer, GameControl>();
	private static int ctrlID = -1;
	private static Controller[] ctrlList;
	private static String[] controllerKeywords = new String[] {
			"Logitech Dual Action"
		};

	public static Controller getController() {
		if (ctrlID == -1) { return null; }
		return ctrlList[ctrlID];
	}
	
	public static void buildControllers() {
		int count = 0;
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
			System.out.printf("Controller detected: %s\n\tButton count: %d; Axis count: %d\n", 
					ctrlList[i].getName(), ctrlList[i].getButtonCount(), ctrlList[i].getAxisCount());
			if (ctrlList[i].getAxisCount() > 0) {
				ctrlID = i;
				break;
			}
		}
		if (ctrlID < 0) {
			System.out.println("No gamepad detected.");
		} else {
			System.out.printf("Controller set: %s; controller ID: %d\n", ctrlList[ctrlID].getName(), ctrlID);
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
		Formatter format = new Formatter();
		String filename = String.format("logs/%tF ", 
				time.getYear(), time.getMonthValue(), time.getDayOfMonth());
/*
		try {
			logFile = new FileWriter(filename);
		} catch (IOException e) {
			System.out.println("Unable to create log file.");
			e.printStackTrace();
		} */
		
	}
	
	public static void closeLog() {
		if (logFile != null) {
			try {
				logFile.close();
			} catch (IOException e) {
				System.out.println("Error closing log file.");
				e.printStackTrace();
			}
		}
	}
}
