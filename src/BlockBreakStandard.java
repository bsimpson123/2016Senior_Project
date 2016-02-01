import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class BlockBreakStandard implements GameMode {
	protected LoadState currentState = LoadState.NOT_LOADED;
	protected HashMap<String, Texture> rootTexMap = null;
	protected HashMap<String, Texture> localTexMap = new HashMap<String, Texture>(10);
	protected Block[][] grid = new Block[20][17]; // [x][y], [c][r]
	protected Stack<Integer> cursorPos = new Stack<Integer>();
	protected Sprite cursor;
	protected int[] cursorGridPos = new int[] { 0, 0 };
	protected int level = 1;
	protected final int maxLevel = 5;
	protected long inputDelay = 0;
	protected final long inputDelayTimer = 100l;
	
	protected String[][] texLoadList = new String[][] {
		new String[] { "ui_base", "media/UIpackSheet_transparent.png" }
	};
	
	
	public BlockBreakStandard() {
		// TODO: set or load any custom environment variables
		// do not load assets at this point
	}
	
	@Override
	public void initialize(HashMap<String, Texture> textureMap) {
		currentState = LoadState.LOADING_ASSETS;
		rootTexMap = textureMap;
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
		
/*		cursor = new Sprite(
				localTexMap.get("ui_base"),
				new int[] { 485, 341 },
				new int[] { 14, 18 },
				new int[] { 28, 36 }
			); //*/
		cursor = new Sprite(
				rootTexMap.get("blocksheet"),
				new int[] { 240, 0 },
				new int[] { 32, 32 },
				new int[] { 32, 32 }
			);
		
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
		// TODO Auto-generated method stub
		currentState = LoadState.READY;
		int[] blockOffSet = new int[] { 32, 32 };
		int[] gridBasePos = new int[] { 75, 540 }; // distance from the left top for the bottom-left of the grid display
		if (inputDelay <= 0) {
			if (Global.getControlActive(Global.GameControl.CANCEL)) {
				// TODO: display exiting information in some manner
				cleanup();
				return;
			}
			if (Global.getControlActive(Global.GameControl.UP)) {
				cursorGridPos[1]++;
				if (cursorGridPos[1] >= grid[0].length) {
					cursorGridPos[1] = grid[0].length - 1;
				}
				inputDelay = inputDelayTimer;
			}
			if (Global.getControlActive(Global.GameControl.DOWN)) {
				if (cursorGridPos[1] > 0) {
					cursorGridPos[1]--;
				}
				inputDelay = inputDelayTimer;
			}
			if (Global.getControlActive(Global.GameControl.LEFT)) {
				if (cursorGridPos[0] > 0) {
					cursorGridPos[0] --;
				}
				inputDelay = inputDelayTimer;
			}
			if (Global.getControlActive(Global.GameControl.RIGHT)) {
				cursorGridPos[0]++;
				if (cursorGridPos[0] >= grid.length) {
					cursorGridPos[0] = grid.length - 1;
				}
				inputDelay = inputDelayTimer;
			}
		} else {
			inputDelay -= Global.delta;
		}
		for (int i = 0; i < grid.length; i++) {
			for (int k = 0; k < grid[0].length; k++) {
				grid[i][k].draw(
						gridBasePos[0] + blockOffSet[0] * i,
						gridBasePos[1] - blockOffSet[1] * k
					);
			}
		}
		
		cursor.draw(
				// for pointer at center of block
/*				gridBasePos[0] + blockOffSet[0] * cursorGridPos[0] - blockOffSet[0]/2,
				gridBasePos[1] - blockOffSet[1] * cursorGridPos[1] + blockOffSet[1]/2 //*/
				// for selector surrounding block
				gridBasePos[0] + blockOffSet[0] * cursorGridPos[0],
				gridBasePos[1] - blockOffSet[1] * cursorGridPos[1]
			);
		
		
		
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
