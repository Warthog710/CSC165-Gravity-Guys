package myGameEngine;

import java.util.HashMap;
import java.util.Vector;

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
	private Sound windSound;
	private HashMap<SceneNode, Sound> walkSound, jumpSound;
	private Vector<SceneNode> players;
	private SceneNode playerN, npcN;
	
	public SoundManager(SceneManager sm, ScriptManager scriptMan)
    {
    	this.sm = sm;
    	this.scriptMan = scriptMan;
		playerN = sm.getSceneNode(scriptMan.getValue("avatarName").toString() + "Node");
		walkSound = new HashMap<SceneNode, Sound>();
		jumpSound = new HashMap<SceneNode, Sound>();
		players = new Vector<SceneNode>();
		players.add(playerN);
		
		//Create a shutdown hook
		Runtime.getRuntime().addShutdownHook(new SoundShutdownHook());
    }
	
	
	public void initAudio () {
		AudioResource resource1, resource2, resource3;
		audioMgr = AudioManagerFactory.createAudioManager("ray.audio.joal.JOALAudioManager");
		if (!audioMgr.initialize()){ 
			System.out.println("Audio Manager failed to initialize!");
			return;
		}
		
		resource1 = audioMgr.createAudioResource("./assets/sounds/walk.wav", AudioResourceType.AUDIO_SAMPLE);
		resource2 = audioMgr.createAudioResource("./assets/sounds/jump.wav", AudioResourceType.AUDIO_SAMPLE);
		resource3 = audioMgr.createAudioResource("./assets/sounds/wind.wav", AudioResourceType.AUDIO_SAMPLE);
		walkSound.put(playerN, new Sound(resource1, SoundType.SOUND_EFFECT, 100, true));
		jumpSound.put(playerN, new Sound(resource2, SoundType.SOUND_EFFECT, 100, false));
		windSound = new Sound(resource3, SoundType.SOUND_EFFECT, 100, false);
		walkSound.get(playerN).initialize(audioMgr);
		jumpSound.get(playerN).initialize(audioMgr);
		windSound.initialize(audioMgr);
		walkSound.get(playerN).setMaxDistance(30.0f);
		walkSound.get(playerN).setMinDistance(10.0f);
		walkSound.get(playerN).setRollOff(1.0f);
		jumpSound.get(playerN).setMaxDistance(30.0f);
		jumpSound.get(playerN).setMinDistance(10.0f);
		jumpSound.get(playerN).setRollOff(1.0f);
		windSound.setMaxDistance(20.0f);
		windSound.setMinDistance(10.0f);
		windSound.setRollOff(1.0f);
		walkSound.get(playerN).setLocation(playerN.getWorldPosition());
		jumpSound.get(playerN).setLocation(playerN.getWorldPosition());
		npcN = sm.getSceneNode(scriptMan.getValue("npcName").toString() + "Node");
		windSound.setLocation(npcN.getWorldPosition());
		setEarParameters();
	}
	
	public void setEarParameters() {
		SceneNode cameraN = sm.getSceneNode(scriptMan.getValue("cameraName").toString() + "Node");
		Vector3 camDir = cameraN.getLocalForwardAxis();
		
		audioMgr.getEar().setLocation(cameraN.getWorldPosition());
		audioMgr.getEar().setOrientation(camDir, Vector3f.createFrom(0, 1, 0));
	}
	
	public void addGhost(SceneNode ghostN) {
		AudioResource resource1, resource2;
		resource1 = audioMgr.createAudioResource("./assets/sounds/walk.wav", AudioResourceType.AUDIO_SAMPLE);
		resource2 = audioMgr.createAudioResource("./assets/sounds/jump.wav", AudioResourceType.AUDIO_SAMPLE);
		players.add(ghostN);
		Sound walkingSound = new Sound(resource1, SoundType.SOUND_EFFECT, 100, true);
		Sound jumpingSound = new Sound(resource2, SoundType.SOUND_EFFECT, 100, false);
		walkingSound.initialize(audioMgr);
		jumpingSound.initialize(audioMgr);
		walkingSound.setMaxDistance(30.0f);
		walkingSound.setMinDistance(10.0f);
		walkingSound.setRollOff(1.0f);
		jumpingSound.setMaxDistance(30.0f);
		jumpingSound.setMinDistance(10.0f);
		jumpingSound.setRollOff(1.0f);
		walkingSound.setLocation(ghostN.getWorldPosition());
		jumpingSound.setLocation(ghostN.getWorldPosition());
		walkSound.put(ghostN, walkingSound);
		jumpSound.put(ghostN, jumpingSound);
		setEarParameters();
	}
	
	public boolean removeGhost(SceneNode ghostN) {
		if (players.contains(ghostN)) {
			players.remove(ghostN);
			walkSound.get(ghostN).release(audioMgr);
			walkSound.remove(ghostN);
			jumpSound.get(ghostN).release(audioMgr);
			jumpSound.remove(ghostN);
			return true;
		}
		else
			return false;
	}
	
	public void updateSound() {
		for (SceneNode node : players) {
				jumpSound.get(node).setLocation(node.getWorldPosition());
				walkSound.get(node).setLocation(node.getWorldPosition());
		}
		windSound.setLocation(npcN.getWorldPosition());
		setEarParameters();
	}
	
	public void playJump(SceneNode node) {
		jumpSound.get(node).play(100, false);
	}
	
	public void playWalk(SceneNode node) {
		walkSound.get(node).play();
	}
	
	public void stopWalk(SceneNode node) {
		walkSound.get(node).stop();
	}
	
	public void playWind() {
		if (!windSound.getIsPlaying())
			windSound.play();
	}

	public IAudioManager getSoundManager()
	{
		return audioMgr;
	}

	//Runs when the game shutdowns to properly close out audio sources
	private class SoundShutdownHook extends Thread
	{
		public void run()
		{
			audioMgr.shutdown();
		}
	}
}
