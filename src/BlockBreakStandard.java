import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class BlockBreakStandard implements GameMode {
	protected LoadState currentState = LoadState.NOT_LOADED;
	protected HashMap<String, Texture> localTexMap = new HashMap<String, Texture>(10);
	protected Stack<Integer> cursorPos = new Stack<Integer>();
	protected long inputDelay = Global.inputReadDelayTimer;
	private BlockStandardLevel playLevel;

	// Level variables. These may be moved/removed if level play is moved to separated class object.
	protected Block[][] grid = new Block[20][20]; // [x][y], [c][r]
	protected Sprite cursor;
	protected int[] cursorGridPos = new int[] { 0, 0 };
	protected int level = 1;
	protected int counter = 0;
	protected final int maxLevel = 5;
	protected int[] blockOffSet = new int[] { 32, 32 };
	
	
	protected String[][] texLoadList = new String[][] {
		new String[] { "ui_base", "media/UIpackSheet_transparent.png" },
		new String[] { "ui_stdmode", "media/StandardMode_UI.png" },
		new String[] { "bg_stdmode_wood1", "media/StandardMode_bg_wood1.png" },
		new String[] { "number_white", "media/numbers_sheet_white.png" }
	};
	
	
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
					 System.out.printf("Attempting to load multiple textures to key [%s]", ref[0]);
					 System.out.printf("Texture resource [%s] not loaded.", ref[1]);
				 }
				 localTexMap.put(source, tex);
			 } catch (IOException e) {
				 System.out.printf("Unable to load texture resource %s\n", source);
				 e.printStackTrace();
				 System.exit(-1);
			 }
		}

		for (int i = 0; i < grid.length; i++) {
			for (int k = 0; k < grid[0].length; k++) {
				grid[i][k] = new Block( Block.BlockType.BLOCK, Global.rand.nextInt(3) );
			}
		}
		
		
		cursorGridPos[0] = grid.length / 2;
		cursorGridPos[1] = grid[0].length / 2;
/*		cursor = new Sprite(
				localTexMap.get("ui_base"),
				new int[] { 485, 341 },
				new int[] { 14, 18 },
				new int[] { 28, 36 }
			); //*/
		// update to BlockBreakStandard.cursor after code moves to separate level class
		cursor = new Sprite(
				Global.textureMap.get("blocksheet"),
				new int[] { 240, 0 },
				new int[] { 32, 32 },
				blockOffSet
			);
		// TODO: load static sprite objects for level play.
		/* By pre-loading level assets, there will be no additional load time
		 * when starting or switching levels.
		 */
		BlockStandardLevel.cursor = cursor;
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
		
		playLevel = new BlockStandardLevelEx(localTexMap);
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
		// TODO: draw user interface for practice, play, high score display, and return to main menu
		
		if (playLevel != null) {
			playLevel.run();
			if (playLevel.levelFinished) {
				cleanup();
			}
		}
		
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
}
