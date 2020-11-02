package a3;

import java.io.IOException;

import myGameEngine.ScriptManager;
import ray.rage.rendersystem.Renderable.Primitive;
import ray.rage.scene.Entity;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rml.Degreef;
import ray.rml.Vector3f;

public class LevelOne {

	private SceneManager sm;
	private ScriptManager scriptMan;
	
	public LevelOne(SceneManager sm, ScriptManager scriptMan) {
		this.sm = sm;
		this.scriptMan = scriptMan;
	}
	
	//Loads all the level objects and returns the node group containing them
	public SceneNode loadLevelObjects() throws IOException {
		//Set up level objects
        SceneNode levelN = sm.getRootSceneNode().createChildSceneNode("levelOneNode");
        
        Entity startPlatE = sm.createEntity("startingPlatform", "groundPlatform.obj");
        startPlatE.setPrimitive(Primitive.TRIANGLES);
        SceneNode startPlatN = levelN.createChildSceneNode(startPlatE.getName() + "Node");
        startPlatN.attachObject(startPlatE);
        startPlatN.rotate(Degreef.createFrom(90), Vector3f.createFrom(0.0f, 1.0f, 0.0f));
        startPlatN.scale((Vector3f)scriptMan.getValue("startPlatScale"));
        startPlatN.setLocalPosition((Vector3f)scriptMan.getValue("startPlatPos"));
        
        Entity plat1E = sm.createEntity("platform1", "groundPlatform.obj");
        plat1E.setPrimitive(Primitive.TRIANGLES);
        SceneNode plat1N = levelN.createChildSceneNode(plat1E.getName() + "Node");
        plat1N.attachObject(plat1E);
        plat1N.scale((Vector3f)scriptMan.getValue("plat1Scale"));
        plat1N.setLocalPosition((Vector3f)scriptMan.getValue("plat1Pos"));
        
        Entity plat2E = sm.createEntity("platform2", "groundPlatform.obj");
        plat2E.setPrimitive(Primitive.TRIANGLES);
        SceneNode plat2N = levelN.createChildSceneNode(plat2E.getName() + "Node");
        plat2N.attachObject(plat2E);
        plat2N.scale((Vector3f)scriptMan.getValue("plat2Scale"));
        plat2N.setLocalPosition((Vector3f)scriptMan.getValue("plat2Pos"));
        
        Entity wishbonePlatE = sm.createEntity("wishbonePlatform", "wishbone.obj");
        wishbonePlatE.setPrimitive(Primitive.TRIANGLES);
        SceneNode wishbonePlatN = levelN.createChildSceneNode(wishbonePlatE.getName() + "Node");
        wishbonePlatN.attachObject(wishbonePlatE);
        wishbonePlatN.scale((Vector3f)scriptMan.getValue("wishbonePlatScale"));
        wishbonePlatN.rotate(Degreef.createFrom(90), Vector3f.createFrom(0.0f, 1.0f, 0.0f));
        wishbonePlatN.setLocalPosition((Vector3f)scriptMan.getValue("wishbonePlatPos"));
        
        Entity wedgePlatE = sm.createEntity("wedgePlatform", "wedge.obj");
        wedgePlatE.setPrimitive(Primitive.TRIANGLES);
        SceneNode wedgePlatN = levelN.createChildSceneNode(wedgePlatE.getName() + "Node");
        wedgePlatN.attachObject(wedgePlatE);
        wedgePlatN.scale((Vector3f)scriptMan.getValue("wedgePlatScale"));
        wedgePlatN.setLocalPosition((Vector3f)scriptMan.getValue("wedgePlatPos"));
        
        return levelN;
	}
	
}
