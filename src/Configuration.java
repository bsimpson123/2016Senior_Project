import java.io.BufferedReader; // file I/O will be used to load and save preferences
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.lwjgl.input.Controller;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.opengl.Texture;

public class Configuration implements GameMode {
	private LoadState currentState = LoadState.NOT_LOADED;
	
	private final String title = "Configuration";
	private final int titleOffset;
	
	private final String[] options = new String[] {
		"Input Sensitivity",
		"Block Cascading",
		"Wait For Block Fall",
		"Fullscreen",
		""
		};
	
	
	public Configuration() {
		titleOffset = Global.getFont48DrawSize(title) / 2;
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
		Global.drawFont48(512 - titleOffset, 80, title, Color.gray);
		
		
		
	}

	
	private void processInput() {
		
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
