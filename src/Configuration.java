import java.io.BufferedReader; // file I/O will be used to load and save preferences
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

public class Configuration implements GameMode {
	private LoadState currentState = LoadState.NOT_LOADED;
	
	private final String title = "Configuration";
	private final int titleOffset;
	private final Color shader = Global.menuButtonShader;
	private final Color reset = Color.white;
	private final Sprite background;
	private final Sprite[] sensBar = new Sprite[3];
	
	private final String[] menus = new String[] {
		"Input Sensitivity",
		"Block Cascading",
		"Wait For Block Fall",
		"Fullscreen",
		"Configure Input",
		""
		};
	
	private final String[][] options = new String[][] {
		new String[] { "", "" }, //new String[] { "Fast", "Slow" }, // sensitivity
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
	private long inputReadDelayTimer = Global.inputReadDelayTimer;
	private long inputDelay = Global.inputReadDelayTimer;
	
	
	private final int delayMin = 8;
	private final int delayMax = 50;
	private final int delayStep = 10;
	
	/** Holds the original settings at screen load. This values will be set back if the user cancels from the screen.
	 * Does not affect keyboard/gamepad configuration. */
	private final int originalSettings[];
	
	// copy variables for settings. these will be used instead the values in Global
	// these values will be copied over if the setting are selected to be saved,
	// else they will be discarded if the user selects cancel
	private int[] settings;
	/*private long inputReadDelayTimer = Global.inputReadDelayTimer;
	private int fullscreen = 0; // TODO: move fullscreen setting to Global and make public/protected
	private int blockCascade = Global.useBlockCascading ? 1 : 0;
	private int waitForGrid = Global.waitForGridMovement ? 1 : 0; //*/
	
	
	public Configuration() {
		titleOffset = Global.getFont48DrawSize(title) / 2;
		settings = new int[] {
			(int)Global.inputReadDelayTimer / 10,
			Global.fullscreen ? 1 : 0, // fullscreen
			Global.useBlockCascading ? 1 : 0,
			Global.waitForGridMovement ? 1 : 0,
			0, // input config
			0 // accept/cancel
		};
		
		originalSettings = new int[] {
				(int)Global.inputReadDelayTimer / 10,
				Global.fullscreen ? 1 : 0, // fullscreen
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

		sensBar[0] = new Sprite(
				Global.textureMap.get("blocksheet"),
				new int[] { 0, 100 },
				new int[] { 24, 24 },
				new int[] { 24, 24 }
			);
		sensBar[1] = new Sprite(
				Global.textureMap.get("blocksheet"),
				new int[] { 25, 100 },
				new int[] { 24, 24 },
				new int[] { 24, 24 }
			);
		sensBar[2] = new Sprite(
				Global.textureMap.get("blocksheet"),
				new int[] { 79, 100 },
				new int[] { 24, 24 },
				new int[] { 24, 24 }
			);
		
		
		
		
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
		
		int barW = ( (settings[0] * 100) / delayMax ) * 5;
		
		for (int i = 0, ys = 200; i < menus.length; i++, ys += 80) {
			shader.bind();
			if (i < 5) { // do not draw for last option
				Global.uiTransWhite.draw(spacing[0][0], ys, spacing[1][0], 54);
			}
			if (i == 0) {
				// TODO: slider for sensitivity adjustment
				reset.bind();
				sensBar[0].draw(spacing[0][1], ys + 12);
				sensBar[1].draw(spacing[0][1] + 24, ys + 12, new int[] { barW, 24 });
				sensBar[2].draw(spacing[0][1] + barW + 24, ys + 12);
				shader.bind();
			} else {
				Global.uiTransWhite.draw(spacing[0][1], ys, spacing[1][1], 54);
				Global.uiTransWhite.draw(spacing[0][2], ys, spacing[1][2], 54);
			}
			reset.bind();
			if (i == select) {
				Global.drawFont48(spacing[2][0], ys + 10, menus[i], Color.white, true);
			} else {
				Global.drawFont48(spacing[2][0], ys + 10, menus[i], Color.black, true);
			}
			if (i > 0 && (settings[i] % 2) == 1) {
				Global.drawFont48(spacing[2][1], ys + 10, options[i][0], Color.cyan, true);
				Global.drawFont48(spacing[2][2], ys + 10, options[i][1], Color.gray, true);
			} else {
				Global.drawFont48(spacing[2][1], ys + 10, options[i][0], Color.gray, true);
				Global.drawFont48(spacing[2][2], ys + 10, options[i][1], Color.cyan, true);
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

		if (Global.getControlActive(Global.GameControl.SELECT)) {
			switch (select) {
				case 3: // window/fullscreen switch 
					
					break;
				case 4: // goto input config for keyboard/gamepad
					
					break;
				case 5: // accept/cancel and leave screen
					if (settings[select] % 2 == 0) {
						commitSettings();
					} else {
						resetSettings();
					}
					cleanup();
					return;
			}
			
			
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
		if (action != 0) {
			if (select > 0) {
				settings[select] += action;
			} else {
				settings[0] += action;
				if (settings[0] < delayMin) {
					settings[0] = delayMin;
				} else if (settings[0] > delayMax) {
					settings[0] = delayMax;
				}
			}
			
		}
		
	}
	
	private void commitSettings() {
		Global.inputReadDelayTimer = (long)settings[0] * delayStep;
		Global.useBlockCascading = settings[1] % 2 == 1 ? true : false;
		Global.waitForGridMovement = settings[2] % 2 == 1 ? true : false;
		boolean fc = settings[3] % 2 == 1 ? true : false;
		if (Global.fullscreen != fc) {
			Global.fullscreen = fc;
			try {
				Display.setFullscreen(Global.fullscreen);
			} catch (LWJGLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void resetSettings() {
		
	}
	
	@Override
	public void cleanup() {
		// TODO clean up resources and save changes
		

		saveSettings();
		currentState = LoadState.FINALIZED;
	}
	
	
	public static void loadSettings() {
		String filename = "game.conf";
		try {
			BufferedReader inf = new BufferedReader( new FileReader(filename));
			
			inf.close();
		} catch (IOException err) {
			
		}
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
