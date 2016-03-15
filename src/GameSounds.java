import static org.lwjgl.opengl.GL11.*;

import java.io.IOException;
import java.util.HashMap;

import org.lwjgl.openal.AL;

import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.util.ResourceLoader;


/*
 * @author Brock
 */
public class GameSounds {
	/** Hash map for referencing audio files that have been loaded. */
	private HashMap<String, Audio> soundMap = new HashMap<String, Audio>();
	protected int soundID;
	soundType stype;
	String sfile;
	String mfile;
	Audio sounds;
	public enum soundType {
		MUSIC, SOUND
	}
	//public static final int  
	//MUSIC = 0,
	//SOUND = 1
    //;
	
	private String[] soundEffectResource = {
			"media/Explosion.wav",
			"media/click3.ogg"
	};
	/** List of all background sounds that will be played */
	private String[] soundBackgroundResource = { 
			"media/Flowing Rocks.ogg"
	};
	
	public GameSounds(soundType type, String input) {
		//soundID = soundNum;
		//stype = type;
		sfile = input;
		if (type == soundType.MUSIC){
			loadMusic(input);
		}
		if (type == soundType.SOUND) {
			loadSound(input);

		}
	}
	
	private boolean loadSound(String file) {
		String source = file, type;
		Audio sound = soundMap.get(file);
		if (sound != null) { return true; } // sound has already been loaded
		
		try {
			source = FileResource.requestResource(file);
			type = source.substring(source.lastIndexOf('.') + 1).toUpperCase(); 
				sfile = file;
				sound = AudioLoader.getAudio(type, ResourceLoader.getResourceAsStream(source));
				soundMap.put(file, sound);
		} catch (IOException e) {
			System.out.printf("Unable to load sound resource: %s\n%s", source, e.getMessage());
			return false;
		}
		return true;
	}

	private boolean loadMusic(String file) {
		String source = file, type;
		Audio sound = soundMap.get(file);
		if (sound != null) { return true; } // sound has already been loaded
		
		try {
			source = FileResource.requestResource(file);
			type = source.substring(source.lastIndexOf('.') + 1).toUpperCase(); 
				mfile = file;
				sounds = AudioLoader.getStreamingAudio(type, ResourceLoader.getResource(source));
				soundMap.put(mfile, sound);
			
		} catch (IOException e) {
			System.out.printf("Unable to load sound resource: %s\n%s", source, e.getMessage());
			return false;
		}
		return true;
	}
	public Audio getSound(String ref) {
		return soundMap.get(ref);
	}
	
	/**
	 * Plays the audio as a sound effect. No effect if the value passed is null.
	 * @param sfx The audio to be played
	 */
	public void playSoundEffect() {
		Audio sfx = getSound(sfile);
		if (sfx == null) { return; } // check that sfx is a defined sound object
		sfx.playAsSoundEffect(1.0f, 1.0f, false);
		return ;
	}
	
	/**
	 * Plays the audio as repeating background music. No effect if the value passed is null.
	 * @param music The audio to be played in the background
	 */
	public void playSoundMusic() {
		Audio music = sounds;//soundMap.get(mfile);
		if (music == null) { return; }
		// TODO: check for and stop previously playing music if any
		music.playAsMusic(1.0f, 1.0f, true);
		return ;
	}
	public void cleanup() {
		AL.destroy();
	}
}
