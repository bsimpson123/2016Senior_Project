
public class GameControls {
	public enum GameControl {
		RIGHT, LEFT, UP, DOWN,
		SELECT, CANCEL, PAUSE,
		SPECIAL1, SPECIAL2
	}
	
	protected boolean 
		RIGHT = false,
		LEFT = false,
		UP = false,
		DOWN = false;
	
	protected boolean
		SELECT = false,
		CANCEL = false,
		PAUSE = false,
		SPECIAL1 = false,
		SPECIAL2 = false;
	
	protected boolean GameRunning = true;
	
	protected void reset() {
		RIGHT = false;
		LEFT = false;
		UP = false;
		DOWN = false;
		
		SELECT = false;
		CANCEL = false;
		PAUSE = false;
		SPECIAL1 = false;
		SPECIAL2 = false;
	}
	
}
