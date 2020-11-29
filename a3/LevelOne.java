package a3;

import java.io.IOException;

import myGameEngine.PhysicsManager;
import myGameEngine.ScriptManager;
import ray.rage.Engine;
import ray.rage.rendersystem.Renderable.Primitive;
import ray.rage.scene.Entity;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rml.Degreef;
import ray.rml.Vector3f;

public class LevelOne 
{

	private SceneManager sm;
    private ScriptManager scriptMan;
    private PhysicsManager physMan;
	
    public LevelOne(Engine eng, ScriptManager scriptMan, PhysicsManager physMan) 
    {
		this.sm = eng.getSceneManager();
        this.scriptMan = scriptMan;
        this.physMan = physMan;
	}
	
	//Loads all the level objects and returns the node group containing them
    public SceneNode loadLevelObjects() throws IOException 
    {
		//Set up level objects
        SceneNode levelN = sm.getRootSceneNode().createChildSceneNode("levelOneNode");  
        levelN.scale((Vector3f)scriptMan.getValue("levelScale"));
        levelN.setLocalPosition((Vector3f)scriptMan.getValue("levelPos"));

        Entity startPlatE = sm.createEntity("startingPlatform", "groundPlatform.obj");
        startPlatE.setPrimitive(Primitive.TRIANGLES);
        SceneNode startPlatN = levelN.createChildSceneNode(startPlatE.getName() + "Node");
        startPlatN.attachObject(startPlatE);
        startPlatN.rotate(Degreef.createFrom(90), Vector3f.createFrom(0.0f, 1.0f, 0.0f));
        startPlatN.scale((Vector3f)scriptMan.getValue("startPlatScale"));
        startPlatN.setLocalPosition((Vector3f)scriptMan.getValue("startPlatPos"));
        createPhysicsPlane("startPhysicsPlane");

        Entity plat1E = sm.createEntity("platform1", "groundPlatform.obj");
        plat1E.setPrimitive(Primitive.TRIANGLES);
        SceneNode plat1N = levelN.createChildSceneNode(plat1E.getName() + "Node");
        plat1N.attachObject(plat1E);
        plat1N.scale((Vector3f)scriptMan.getValue("plat1Scale"));
        plat1N.setLocalPosition((Vector3f)scriptMan.getValue("plat1Pos"));
        createPhysicsPlane("plat1PhysicsPlane");
        
        Entity plat2E = sm.createEntity("platform2", "groundPlatform.obj");
        plat2E.setPrimitive(Primitive.TRIANGLES);
        SceneNode plat2N = levelN.createChildSceneNode(plat2E.getName() + "Node");
        plat2N.attachObject(plat2E);
        plat2N.scale((Vector3f)scriptMan.getValue("plat2Scale"));
        plat2N.setLocalPosition((Vector3f)scriptMan.getValue("plat2Pos"));
        createPhysicsPlane("plat2PhysicsPlane");
        
        Entity wishbonePlatE = sm.createEntity("wishbonePlatform", "wishbone.obj");
        wishbonePlatE.setPrimitive(Primitive.TRIANGLES);
        SceneNode wishbonePlatN = levelN.createChildSceneNode(wishbonePlatE.getName() + "Node");
        wishbonePlatN.attachObject(wishbonePlatE);
        wishbonePlatN.scale((Vector3f)scriptMan.getValue("wishbonePlatScale"));
        wishbonePlatN.rotate(Degreef.createFrom(180), Vector3f.createFrom(0.0f, 1.0f, 0.0f));
        wishbonePlatN.setLocalPosition((Vector3f)scriptMan.getValue("wishbonePlatPos"));
        createPhysicsCylinderPlane("wishBoneOne");
        createPhysicsCylinderPlane("wishBoneTwo");
        
        Entity wedgePlatE = sm.createEntity("wedgePlatform", "wedge.obj");
        wedgePlatE.setPrimitive(Primitive.TRIANGLES);
        SceneNode wedgePlatN = levelN.createChildSceneNode(wedgePlatE.getName() + "Node");
        wedgePlatN.attachObject(wedgePlatE);
        wedgePlatN.scale((Vector3f)scriptMan.getValue("wedgePlatScale"));
        wedgePlatN.setLocalPosition((Vector3f)scriptMan.getValue("wedgePlatPos"));
        createPhysicsPlaneWithRotationAboutX("wedgePhysicsPlane"); 
        createPhysicsPlane("plat3PhysicsPlane");  
        createPhysicsPlane("plat4PhysicsPlane");
        
        Entity endPlat1E = sm.createEntity("endPlatform1", "groundPlatform.obj");
        endPlat1E.setPrimitive(Primitive.TRIANGLES);
        SceneNode endPlat1N = levelN.createChildSceneNode(endPlat1E.getName() + "Node");
        endPlat1N.attachObject(endPlat1E);
        endPlat1N.scale((Vector3f)scriptMan.getValue("endPlat1Scale"));
        endPlat1N.setLocalPosition((Vector3f)scriptMan.getValue("endPlat1Pos"));
        //createPhysicsPlane("endPlat1PhysicsPlane");
        
        Entity endPlat2E = sm.createEntity("endPlatform2", "groundPlatform.obj");
        endPlat2E.setPrimitive(Primitive.TRIANGLES);
        SceneNode endPlat2N = levelN.createChildSceneNode(endPlat2E.getName() + "Node");
        endPlat2N.attachObject(endPlat2E);
        endPlat2N.scale((Vector3f)scriptMan.getValue("endPlat2Scale"));
        endPlat2N.setLocalPosition((Vector3f)scriptMan.getValue("endPlat2Pos"));
        
        Entity endPlat3E = sm.createEntity("endPlatform3", "groundPlatform.obj");
        endPlat3E.setPrimitive(Primitive.TRIANGLES);
        SceneNode endPlat3N = levelN.createChildSceneNode(endPlat3E.getName() + "Node");
        endPlat3N.attachObject(endPlat3E);
        endPlat3N.scale((Vector3f)scriptMan.getValue("endPlat3Scale"));
        endPlat3N.setLocalPosition((Vector3f)scriptMan.getValue("endPlat3Pos"));
        
        Entity endPlat4E = sm.createEntity("endPlatform4", "groundPlatform.obj");
        endPlat4E.setPrimitive(Primitive.TRIANGLES);
        SceneNode endPlat4N = levelN.createChildSceneNode(endPlat4E.getName() + "Node");
        endPlat4N.attachObject(endPlat4E);
        endPlat4N.scale((Vector3f)scriptMan.getValue("endPlat4Scale"));
        endPlat4N.setLocalPosition((Vector3f)scriptMan.getValue("endPlat4Pos"));
        
        Entity endPlat5E = sm.createEntity("endPlatform5", "groundPlatform.obj");
        endPlat5E.setPrimitive(Primitive.TRIANGLES);
        SceneNode endPlat5N = levelN.createChildSceneNode(endPlat5E.getName() + "Node");
        endPlat5N.attachObject(endPlat5E);
        endPlat5N.scale((Vector3f)scriptMan.getValue("endPlat5Scale"));
        endPlat5N.setLocalPosition((Vector3f)scriptMan.getValue("endPlat5Pos"));
        
        return levelN;
    }

    private void createPhysicsPlane(String name) throws IOException
    {
        Entity physicsPlane = sm.createEntity(name, "cube.obj");
        physicsPlane.setPrimitive(Primitive.TRIANGLES);
        SceneNode physicsPlaneNode = sm.getRootSceneNode().createChildSceneNode(name + "Node");
        physicsPlaneNode.attachObject(physicsPlane);
        physicsPlaneNode.setLocalPosition((Vector3f)scriptMan.getValue(name + "Pos"));
        physicsPlaneNode.setLocalScale((Vector3f)scriptMan.getValue(name + "Scale"));
        physicsPlane.setVisible((boolean)scriptMan.getValue(name + "Vis"));
        physMan.createCubePhysicsObject(physicsPlaneNode, 0f, 1f, 1f, .99f);
    }

    private void createPhysicsPlaneWithRotationAboutX(String name) throws IOException
    {
        Entity physicsPlane = sm.createEntity(name, "cube.obj");
        physicsPlane.setPrimitive(Primitive.TRIANGLES);
        SceneNode physicsPlaneNode = sm.getRootSceneNode().createChildSceneNode(name + "Node");
        physicsPlaneNode.attachObject(physicsPlane);
        physicsPlaneNode.setLocalPosition((Vector3f)scriptMan.getValue(name + "Pos"));
        physicsPlaneNode.setLocalScale((Vector3f)scriptMan.getValue(name + "Scale"));
        physicsPlane.setVisible((boolean)scriptMan.getValue(name + "Vis"));
        physMan.createCubePhysicsObjectWithRotationAboutX(physicsPlaneNode, 0f, 1f, 1f, .99f, (Degreef) scriptMan.getValue(name + "RotX"));
    }

    private void createPhysicsCylinderPlane(String name) throws IOException
    {
        Entity physicsPlane = sm.createEntity(name, "cylinder.obj");
        physicsPlane.setPrimitive(Primitive.TRIANGLES);
        SceneNode physicsPlaneNode = sm.getRootSceneNode().createChildSceneNode(name + "Node");
        physicsPlaneNode.attachObject(physicsPlane);
        physicsPlaneNode.setLocalPosition((Vector3f)scriptMan.getValue(name + "Pos"));
        physicsPlaneNode.setLocalScale((Vector3f)scriptMan.getValue(name + "Scale"));
        physicsPlane.setVisible((boolean)scriptMan.getValue(name + "Vis"));
        physMan.createCylinderPhyicsObject(physicsPlaneNode, 0f, 1f, 1f, .99f, (Degreef) scriptMan.getValue(name + "RotY"));

    }
}
