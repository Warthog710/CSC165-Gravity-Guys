package myGameEngine;

import ray.audio.AudioManagerFactory;
import ray.audio.AudioResource;
import ray.audio.AudioResourceType;
import ray.audio.IAudioManager;
import ray.audio.Sound;
import ray.audio.SoundType;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rml.Vector3;
import ray.rml.Vector3f;

public class SoundManager {
	
	private SceneManager sm;
	private ScriptManager scriptMan;
	private IAudioManager audioMgr;
	private Sound walkSound, jumpSound;
	private SceneNode playerN;
	
	public SoundManager(SceneManager sm, ScriptManager scriptMan)
    {
    	this.sm = sm;
    	this.scriptMan = scriptMan;
    	playerN = sm.getSceneNode(scriptMan.getValue("avatarName").toString() + "Node");
    	
    }
	
	
	public void initAudio () {
		AudioResource resource1, resource2;
		audioMgr = AudioManagerFactory.createAudioManager("ray.audio.joal.JOALAudioManager");
		if (!audioMgr.initialize()){ 
			System.out.println("Audio Manager failed to initialize!");
			return;
		}
		
		resource1 = audioMgr.createAudioResource("footsteps.wav", AudioResourceType.AUDIO_SAMPLE);
		resource2 = audioMgr.createAudioResource("jump.wav", AudioResourceType.AUDIO_SAMPLE);
		walkSound = new Sound(resource1, SoundType.SOUND_EFFECT, 100, true);
		jumpSound = new Sound(resource2, SoundType.SOUND_EFFECT, 100, true);
		
		walkSound.initialize(audioMgr);
		jumpSound.initialize(audioMgr);
		walkSound.setMaxDistance(10.0f);
		walkSound.setMinDistance(0.5f);
		walkSound.setRollOff(5.0f);
		jumpSound.setMaxDistance(10.0f);
		jumpSound.setMinDistance(0.5f);
		jumpSound.setRollOff(5.0f);
		walkSound.setLocation(playerN.getWorldPosition());
		jumpSound.setLocation(playerN.getWorldPosition());
		setEarParameters();
	}
	
	public void setEarParameters() {
		SceneNode cameraN = sm.getSceneNode(scriptMan.getValue("cameraName").toString() + "Node");
		Vector3 camDir = cameraN.getWorldForwardAxis();
		
		audioMgr.getEar().setLocation(cameraN.getWorldPosition());
		audioMgr.getEar().setOrientation(camDir, Vector3f.createFrom(0, 1, 0));
	}
	
	public void updateSound() {
		jumpSound.setLocation(playerN.getWorldPosition());
		walkSound.setLocation(playerN.getWorldPosition());
		setEarParameters();
	}
	
	public void playJump() {
		jumpSound.play(100, false);
	}
	
	public void playWalk() {
		walkSound.play();
	}
	
	public void stopWalk() {
		walkSound.stop();
	}
	
}
