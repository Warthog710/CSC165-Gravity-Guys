package a3;

import java.io.IOException;
import ray.rage.rendersystem.Renderable.Primitive;
import ray.rage.scene.Entity;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rml.Degreef;
import ray.rml.Vector3f;

public class LevelOne {

	private SceneManager sm;
	
	public LevelOne(SceneManager sm) {
		this.sm = sm;
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
        startPlatN.setLocalScale(1.5f, 1, 2);
        
        Entity plat1E = sm.createEntity("platform1", "groundPlatform.obj");
        plat1E.setPrimitive(Primitive.TRIANGLES);
        SceneNode plat1N = levelN.createChildSceneNode(plat1E.getName() + "Node");
        plat1N.attachObject(plat1E);
        plat1N.moveForward(13.93f);
        plat1N.moveLeft(5.98f);
        plat1N.setLocalScale(1, 1, 2);
        
        Entity plat2E = sm.createEntity("platform2", "groundPlatform.obj");
        plat2E.setPrimitive(Primitive.TRIANGLES);
        SceneNode plat2N = levelN.createChildSceneNode(plat2E.getName() + "Node");
        plat2N.attachObject(plat2E);
        plat2N.moveForward(13.93f);
        plat2N.moveRight(5.98f);
        plat2N.setLocalScale(1, 1, 2);
        
        Entity wishbonePlatE = sm.createEntity("wishbonePlatform", "wishbone.obj");
        wishbonePlatE.setPrimitive(Primitive.TRIANGLES);
        SceneNode wishbonePlatN = levelN.createChildSceneNode(wishbonePlatE.getName() + "Node");
        wishbonePlatN.attachObject(wishbonePlatE);
        wishbonePlatN.moveForward(30.8f);
        wishbonePlatN.moveDown(0.7f);
        wishbonePlatN.rotate(Degreef.createFrom(90), Vector3f.createFrom(0.0f, 1.0f, 0.0f));
        wishbonePlatN.setLocalScale(6, 3, 3);
        
        Entity wedgePlatE = sm.createEntity("wedgePlatform", "wedge.obj");
        wedgePlatE.setPrimitive(Primitive.TRIANGLES);
        SceneNode wedgePlatN = levelN.createChildSceneNode(wedgePlatE.getName() + "Node");
        wedgePlatN.attachObject(wedgePlatE);
        wedgePlatN.moveForward(46.5f);
        
        return levelN;
	}
	
}
