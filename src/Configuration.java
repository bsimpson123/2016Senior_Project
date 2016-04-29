import java.io.BufferedReader; // file I/O will be used to load and save preferences
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

public class Configuration implements GameMode {
	private LoadState currentState = LoadState.NOT_LOADED;
	
	private final String title = "Configuration";
	private final int titleOffset;
	private final Color shader = Global.menuButtonShader;
	private final Color reset = Color.white;
	private final Sprite background;
	
	private final String[] menus = new String[] {
		"Input Sensitivity",
		"Block Cascading",
		"Wait For Block Fall",
		"Fullscreen",
		"Input",
		""
		};
	
	private final String[][] options = new String[][] {
		new String[] { "", "" }, // sensitivity
		new String[] { "ON", "OFF" }, // cascading
		new String[] { "Wait", "No Wait" }, // grid wait
		new String[] { "Fullscreen", "Window" }, // fullscreen
		new String[] { "Keyboard", "Gamepad" }, // input configuration
		new String[] { "Accept", "Cancel" } // save or discard changes
	};
	
	private final int[][] spacing = new int[][] {
		new int[] { 24, 478, 751 }, // left edge 
		new int[] { 430, 249, 249 }, // width
		new int[] { 239, 603, 876 } // center
	};
	
	private int select = 0;
	private int selectMax = menus.length;
	private long inputDelay = Global.inputReadDelayTimer;
	
	
	// copy variables for settings. these will be used instead the values in Global
	// these values will be copied over if the setting are selected to be saved,
	// else they will be discarded if the user selects cancel
	private int[] settings;
	private long inputReadDelayTimer = Global.inputReadDelayTimer;
	private int fullscreen = 0; // TODO: move fullscreen setting to Global and make public/protected
	private int blockCascade = Global.useBlockCascading ? 1 : 0;
	private int waitForGrid = Global.waitForGridMovement ? 1 : 0;
	
	
	public Configuration() {
		titleOffset = Global.getFont48DrawSize(title) / 2;
		settings = new int[] {
			(int)Global.inputReadDelayTimer,
			0, // fullscreen
			Global.useBlockCascading ? 1 : 0,
			Global.waitForGridMovement ? 1 : 0,
			0, // input config
			0 // accept/cancel
		};
		background = new Sprite(
				Global.textureMap.get("main_menu_background"),
				new int[] {0,0},
				new int[] {1024,768},
				new int[] {1024,768}
			);
		
	}
		
	@Override
	public void initialize() {
		// This should always be the first line
		currentState = LoadState.LOADING_ASSETS;
		// TODO Auto-generated method stub

		
		
		currentState = LoadState.LOADING_DONE;
		return;
		
	}

	@Override
	public LoadState getState() {
		// TODO Auto-generated method stub
		return currentState;
	}

	@Override
	public void run() {
		currentState = LoadState.READY;
		// TODO Auto-generated method stub
		background.draw(0, 0);
		Global.drawFont48(512 - titleOffset, 80, title, Color.gray);
		
		for (int i = 0, xs = 20, ys = 200; i < menus.length; i++, ys += 80) {
			shader.bind();
			if (i < 5) { // do not draw for last option
				Global.uiTransWhite.draw(xs, ys, 420, 54);
			}
			if (i == 0) {
				// TODO: slider for sensitivity adjustment
			} else {
				Global.uiTransWhite.draw(xs + 500, ys, 200, 54);
				Global.uiTransWhite.draw(xs + 750, ys, 200, 54);
			}
			reset.bind();
			if (i == select) {
				Global.drawFont48(xs + 210, ys + 10, menus[i], Color.white, true);
			} else {
				Global.drawFont48(xs + 210, ys + 10, menus[i], Color.black, true);
			}
			if (i > 0 && settings[i] == 0) {
				Global.drawFont48(xs + 500, ys + 10, options[i][0], Color.cyan);
				Global.drawFont48(xs + 750, ys + 10, options[i][1], Color.gray);
			} else {
				Global.drawFont48(xs + 500, ys + 10, options[i][0], Color.gray);
				Global.drawFont48(xs + 750, ys + 10, options[i][1], Color.cyan);
			}
		}
		
		processInput();
		
		
	}

	
	private void processInput() {
		inputDelay -= Global.delta;
		if (inputDelay > 0) { return; }
		if (Global.getControlActive(Global.GameControl.CANCEL)) {
			cleanup();
			return;
		}
		
		if (Global.getControlActive(Global.GameControl.UP)) {
			select --;
			if (select < 0) { select = 0; }
			inputDelay = inputReadDelayTimer;
		} else 
		if (Global.getControlActive(Global.GameControl.DOWN)) {
			select++;
			if (select > selectMax) {
				select = selectMax;
			}
			inputDelay = inputReadDelayTimer;
		}
		
		int action = 0;
		if (Global.getControlActive(Global.GameControl.LEFT)) {
			action = -1;
			inputDelay = inputReadDelayTimer;
		} else
		if (Global.getControlActive(Global.GameControl.RIGHT)) {
			action = 1;
			inputDelay = inputReadDelayTimer;
		}
		
	}
	
	@Override
	public void cleanup() {
		// TODO clean up resources and save changes

		saveSettings();
		currentState = LoadState.FINALIZED;
	}
	
	
	public static void loadSettings() {
		
	}
	
	public static void saveSettings() {
		
	}

	public static void setupDefaultValues() {
		Global.setKeyMap(Global.GameControl.UP, Keyboard.KEY_UP);
		Global.setKeyMap(Global.GameControl.DOWN, Keyboard.KEY_DOWN);
		Global.setKeyMap(Global.GameControl.LEFT, Keyboard.KEY_LEFT);
		Global.setKeyMap(Global.GameControl.RIGHT, Keyboard.KEY_RIGHT);
		Global.setKeyMap(Global.GameControl.SELECT, Keyboard.KEY_X);
		Global.setKeyMap(Global.GameControl.SELECT, Keyboard.KEY_RETURN);
		Global.setKeyMap(Global.GameControl.SELECT, Keyboard.KEY_SPACE);
		Global.setKeyMap(Global.GameControl.CANCEL, Keyboard.KEY_W);
		Global.setKeyMap(Global.GameControl.CANCEL, Keyboard.KEY_ESCAPE);
		Global.setKeyMap(Global.GameControl.PAUSE, Keyboard.KEY_F);
		Global.setKeyMap(Global.GameControl.SPECIAL1, Keyboard.KEY_A);
		Global.setKeyMap(Global.GameControl.SPECIAL2, Keyboard.KEY_D);
		
		// gamepad controls
		Controller gamepad = Global.getController();
		if (gamepad != null) {
			// add gamepad controls here;
			Global.setGamePadMap(Global.GameControl.SELECT, 1);
			Global.setGamePadMap(Global.GameControl.CANCEL, 3);
			Global.setGamePadMap(Global.GameControl.SPECIAL1, 2);
			Global.setGamePadMap(Global.GameControl.SPECIAL2, 0);
			Global.setGamePadMap(Global.GameControl.PAUSE, 9);
			
		}

	}
	
}
