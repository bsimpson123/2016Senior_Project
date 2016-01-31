import java.util.HashMap;
import org.newdawn.slick.opengl.Texture;

public class BlockBreakStandard implements GameMode {
	protected LoadState currentState = LoadState.NOT_LOADED;
	protected HashMap<String, Texture> rootTexMap = null;
	protected HashMap<String, Texture> localTexMap = new HashMap<String, Texture>(10);
	protected Block[][] grid = new Block[20][17]; // [x][y], [c][r]
	
	
	public BlockBreakStandard() {
		// TODO: set or load any custom environment variables
		// do not load assets at this point
	}
	
	@Override
	public void initialize(HashMap<String, Texture> textureMap) {
		currentState = LoadState.LOADING_ASSETS;
		rootTexMap = textureMap;
		// TODO Auto-generated method stub
		for (int i = 0; i < grid.length; i++) {
			for (int k = 0; k < grid[0].length; k++) {
				grid[i][k] = new Block( Block.BlockType.BLOCK, Global.rand.nextInt(3) );
			}
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
		// TODO Auto-generated method stub
		currentState = LoadState.READY;
		int[] blockOffSet = new int[] { 32, 32 };
		int[] gridBasePos = new int[] { 75, 540 }; // distance from the left top for the bottom-left of the grid display
		
		if (Global.getControlActive(Global.GameControl.CANCEL)) {
			// TODO: display exiting information in some manner
			cleanup();
			return;
		}

		for (int i = 0; i < grid.length; i++) {
			for (int k = 0; k < grid[0].length; k++) {
				grid[i][k].draw(
						gridBasePos[0] + blockOffSet[0] * i,
						gridBasePos[1] - blockOffSet[1] * k
					);
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
