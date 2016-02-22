import java.io.IOException;
import java.util.HashMap;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class BlockBreakStandard implements GameMode {
	protected LoadState currentState = LoadState.NOT_LOADED;
	protected HashMap<String, Texture> localTexMap = new HashMap<String, Texture>(10);
	protected int cursorPos = 0;
	//protected long inputDelay = Global.inputReadDelayTimer;
	private BlockStandardLevel playLevel;

	// Level variables. These may be moved/removed if level play is moved to separated class object.
	protected Sprite cursor;
	protected int[] blockOffSet = new int[] { 32, 32 };
	private long movementInputDelay = Global.inputReadDelayTimer;
	
	private boolean pageBack = false;
	/** The current game mode within the main logic loop. */

	protected String[][] texLoadList = new String[][] {
		new String[] { "ui_base", "media/UIpackSheet_transparent.png" },
		new String[] { "ui_stdmode", "media/StandardMode_UI.png" },
		new String[] { "bg_stdmode_wood1", "media/StandardMode_bg_wood1.png" },
		new String[] { "number_white", "media/numbers_sheet_white.png" }, 
		new String[] { "energy_empty", "media/energy_bar_empty.png" },
		new String[] { "energybar", "media/energy_bar.png"+- }
	};
	
	private final int GameModeSelection = 0,
		BlockMatchStandard = 1,
		PracticeMode = 2,
		HighScore = 3
		;
	int activeGameMode = GameModeSelection;

	private int optionBoxOffset = 0;
	private Sprite[] selector = new Sprite[2];
	private Sprite optionBox;

	
	public BlockBreakStandard() {
		// TODO: set or load any custom environment variables
		// do not load assets at this point
	}
	
	@Override
	public void initialize() {
		// This should always be the first line
		currentState = LoadState.LOADING_ASSETS;
		// TODO Auto-generated method stub
		Texture tex;
		String type; // holds file type extension
		String source; // absolute file path to resource
		for (String ref[] : texLoadList) {
			// Load local textures
			type = ref[1].substring(ref[1].lastIndexOf('.')).toUpperCase();
			tex = null;
			source = ref[1];
			 try {
				 source = FileResource.requestResource(ref[1]);
				 tex = TextureLoader.getTexture(type, ResourceLoader.getResourceAsStream(ref[1]));
				 if ( localTexMap.putIfAbsent(ref[0], tex) != null) {
					 // report error, attempting to add duplicate key entry
					 Global.writeToLog(String.format("Attempting to load multiple textures to key [%s]", ref[0]));
					 Global.writeToLog(String.format("Texture resource [%s] not loaded.", ref[1]) );
				 }
				 localTexMap.put(source, tex);
			 } catch (IOException e) {
				 Global.writeToLog(String.format("Unable to load texture resource %s\n", source) );
				 e.printStackTrace();
				 System.exit(-1);
			 }
		}
// author Brock
		optionBox = new Sprite(
				Global.textureMap.get("green_ui"),
				new int[] { 0, 0 },
				new int[] { 190, 48 },
				new int[] { 190, 48 }
			);
		
		selector[0] = new Sprite( // left-side arrow
				Global.textureMap.get("grey_ui"),
				new int[] { 39, 478 },
				new int[] { 38, 30 },
				new int[] { 38, 30 }
			);
		selector[1] = new Sprite( // right-side arrow
				Global.textureMap.get("grey_ui"),
				new int[] { 0, 478 },
				new int[] { 38, 30 },
				new int[] { 38, 30 }
			);
		
		// author: John
		// update to BlockBreakStandard.cursor after code moves to separate level class
		cursor = new Sprite(
				Global.textureMap.get("blocksheet"),
				new int[] { 240, 0 },
				new int[] { 32, 32 },
				blockOffSet
			);
		BlockStandardLevel.cursor = cursor;
		BlockStandardLevel.emptyEnergy = new Sprite(
				localTexMap.get("energy_empty"),
				new int[] { 0, 0 },
				new int[] { 512, 20 },
				new int[] { 640, 20 }
			);
		BlockStandardLevel.energyBar = localTexMap.get("energybar");
		// BlockStandardLevel.pauseCursor
		// BlockStandardLevel.numbers // numbers used for score display
		int offset = 0;
		for (int i = 0; i < BlockStandardLevel.numbers.length; i++) {
			offset = i * 24 - 1;
			BlockStandardLevel.numbers[i] = new Sprite(
					localTexMap.get("number_white"),
					new int[] { offset, 0 },
					new int[] { 24, 30 },
					new int[] { 24, 30 }
				);
		}
		
		// Update mode state when asset loading is completed
		currentState = LoadState.LOADING_DONE;
		return;
	}

	@Override
	public LoadState getState() {
		return currentState;
	}

	@Override
	public void run() {
		currentState = LoadState.READY;
		//movementInputDelay = Global.inputReadDelayTimer;

		
		if (playLevel != null) {
			playLevel.run();
			if (playLevel.levelFinished) {
				if (playLevel.gameOver) {
					playLevel = null;
					movementInputDelay = Global.inputReadDelayTimer;
				} else {
					// load next level
					switch(playLevel.level) {
						case 1:
							
							break;
						default:
							break;
					}
				}
				
			}
		} else {
// @author Brock
			moveCursor();
			optionBoxOffset = 100;
			if (cursorPos == 0) {
				optionBox.draw(180 + optionBoxOffset, 180);
				optionBox.draw(180, 250);
				optionBox.draw(180, 320);
				optionBox.draw(180, 390);
				
				selector[0].draw(160 + optionBoxOffset, 187 + cursorPos * 70);
				selector[1].draw(351 + optionBoxOffset, 187 + cursorPos * 70);
			} 
			if (cursorPos == 1) {
				optionBox.draw(180, 180);
				optionBox.draw(180 + optionBoxOffset, 250);
				optionBox.draw(180, 320);
				optionBox.draw(180, 390);
				
				selector[0].draw(160 + optionBoxOffset, 187 + cursorPos * 70);
				selector[1].draw(351 + optionBoxOffset, 187 + cursorPos * 70);
			}
			if (cursorPos == 2) {
				optionBox.draw(180, 180);
				optionBox.draw(180, 250);
				optionBox.draw(180 + optionBoxOffset, 320);
				optionBox.draw(180, 390);
				
				selector[0].draw(160 + optionBoxOffset, 187 + cursorPos * 70);
				selector[1].draw(351 + optionBoxOffset, 187 + cursorPos * 70);
			}
			if (cursorPos == 3) {
				optionBox.draw(180, 180);
				optionBox.draw(180, 250);
				optionBox.draw(180, 320);
				optionBox.draw(180 + optionBoxOffset, 390);
				
				selector[0].draw(160 + optionBoxOffset, 187 + cursorPos * 70);
				selector[1].draw(351 + optionBoxOffset, 187 + cursorPos * 70);
			}
			
		}
		if (pageBack) { cleanup(); }
	}
	
	@Override
	public void cleanup() {
		// TODO Auto-generated method stub
		for (Texture ref : localTexMap.values()) {
			ref.release();
		}
		localTexMap.clear();
		
		/* Indicate that the game mode had complete unloading and is ready to
		 * return control to previous control loop.
		 */
		currentState = LoadState.FINALIZED;
	}
	
	/**
	 * @author Brock
	 */
	public void moveCursor() {
		if (movementInputDelay <= 0) {
		if (Global.getControlActive(Global.GameControl.UP)) {
			cursorPos--;
			if (cursorPos < 0) {
				
				cursorPos = 3;
			}
			movementInputDelay = Global.inputReadDelayTimer;
		}
		if (Global.getControlActive(Global.GameControl.DOWN)) {
			cursorPos++;
			if (cursorPos > 3) {
				cursorPos = 0;
			}
			movementInputDelay = Global.inputReadDelayTimer;
		}
		if (Global.getControlActive(Global.GameControl.CANCEL)) { // Cancel key moves the cursor to the program exit button
			cursorPos = 3;
		}
		
		if (Global.getControlActive(Global.GameControl.SELECT)) {
			switch (cursorPos) {
				case 0:
					playLevel = new BlockStandardLevelEx(localTexMap);
					BlockStandardLevel.score = 0;
					//activeGameMode = BlockMatchStandard;
					break;
				case 1:
					playLevel = new BlockStandardLevelEx(localTexMap);
					BlockStandardLevel.score = 0;
					//activeGameMode = BlockMatchStandard;
					break;
					
				case 2:
					//game = 
					//pageBack = true;
					//activeGameMode = MainMenu;
					//gameRunning = false;
					break;
				case 3:
					pageBack = true;
					break;
			}
			
		}
	} else if (movementInputDelay > 0) {
		movementInputDelay -= Global.delta;
	}

		}
		
	

}

