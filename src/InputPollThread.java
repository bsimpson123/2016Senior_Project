/**
 * Thread class to poll inputs at a regular interval independent from
 * game play logic and render processing. 
 * @author John
 *
 */
public final class InputPollThread implements Runnable {
	private final GameControls gameControls;
	
	public InputPollThread(GameControls controls) {
		this.gameControls = controls;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		while (true) {
			try {
				Thread.sleep(Global.inputReadDelayTimer);
			} catch (InterruptedException e) {
				Global.writeToLog("Thread sleep interruption event.");
				Global.writeToLog(e.getStackTrace().toString());
				System.exit(0);
			}
			synchronized(gameControls) {
				// TODO: check against inputs and set control variables
				gameControls.reset();
				
				if (!gameControls.GameRunning) { break; }
			}
		}
		
	}

}
