package a3;

import java.io.IOException;
import java.util.Vector;

import myGameEngine.PhysicsManager;
import myGameEngine.ScriptManager;
import ray.rage.Engine;
import ray.rage.asset.texture.Texture;
import ray.rage.rendersystem.Renderable.Primitive;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
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
    private Vector<SceneNode> endPlatformPhysicsPlanes;
    private Texture physicsPlaneTex;
	
    public LevelOne(Engine eng, ScriptManager scriptMan, PhysicsManager physMan) 
    {
		this.sm = eng.getSceneManager();
        this.scriptMan = scriptMan;
        this.physMan = physMan;
        this.endPlatformPhysicsPlanes = new Vector<>();

        try
        {
            this.physicsPlaneTex = this.sm.getTextureManager().getAssetByPath("physicsPlatformTexture.png");            
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
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
        createPhysicsPlaneWithRotationAboutY("wishBoneThree");
        createPhysicsPlaneWithRotationAboutY("wishBoneFour");
        
        Entity wedgePlatE = sm.createEntity("wedgePlatform", "wedge.obj");
        wedgePlatE.setPrimitive(Primitive.TRIANGLES);
        SceneNode wedgePlatN = levelN.createChildSceneNode(wedgePlatE.getName() + "Node");
        wedgePlatN.attachObject(wedgePlatE);
        wedgePlatN.scale((Vector3f)scriptMan.getValue("wedgePlatScale"));
        wedgePlatN.setLocalPosition((Vector3f)scriptMan.getValue("wedgePlatPos"));
        createPhysicsPlaneWithRotationAboutX("wedgePhysicsPlane"); 
        createPhysicsPlane("plat3PhysicsPlane");  
        createPhysicsPlane("plat4PhysicsPlane");
        
        createPhysicsPlane("endPlat1PhysicsPlane");
        endPlatformPhysicsPlanes.add(sm.getSceneNode("endPlat1PhysicsPlaneNode"));
        Vector3f scale = (Vector3f)scriptMan.getValue("endPlat1PhysicsPlaneScale");
        sm.getSceneNode("endPlat1PhysicsPlaneNode").setLocalScale(scale.x(), scale.y() + .1f, scale.z());
        
        createPhysicsPlane("endPlat2PhysicsPlane");
        endPlatformPhysicsPlanes.add(sm.getSceneNode("endPlat2PhysicsPlaneNode"));
        scale = (Vector3f)scriptMan.getValue("endPlat2PhysicsPlaneScale");
        sm.getSceneNode("endPlat2PhysicsPlaneNode").setLocalScale(scale.x(), scale.y() + .1f, scale.z());
        
        createPhysicsPlane("endPlat3PhysicsPlane");
        endPlatformPhysicsPlanes.add(sm.getSceneNode("endPlat3PhysicsPlaneNode"));
        scale = (Vector3f)scriptMan.getValue("endPlat3PhysicsPlaneScale");
        sm.getSceneNode("endPlat3PhysicsPlaneNode").setLocalScale(scale.x(), scale.y() + .1f, scale.z());
        
        createPhysicsPlane("endPlat4PhysicsPlane");
        endPlatformPhysicsPlanes.add(sm.getSceneNode("endPlat4PhysicsPlaneNode"));
        scale = (Vector3f)scriptMan.getValue("endPlat4PhysicsPlaneScale");
        sm.getSceneNode("endPlat4PhysicsPlaneNode").setLocalScale(scale.x(), scale.y() + .1f, scale.z());
        
        createPhysicsPlane("endPlat5PhysicsPlane");
        endPlatformPhysicsPlanes.add(sm.getSceneNode("endPlat5PhysicsPlaneNode"));
        scale = (Vector3f)scriptMan.getValue("endPlat5PhysicsPlaneScale");
        sm.getSceneNode("endPlat5PhysicsPlaneNode").setLocalScale(scale.x(), scale.y() + .1f, scale.z());
        
        Entity finishPlatE = sm.createEntity("finishPlatform", "finishPlatform.obj");
        finishPlatE.setPrimitive(Primitive.TRIANGLES);
        SceneNode finishPlatN = levelN.createChildSceneNode(finishPlatE.getName() + "Node");
        finishPlatN.attachObject(finishPlatE);
        finishPlatN.scale((Vector3f)scriptMan.getValue("finishPlatScale"));
        finishPlatN.setLocalPosition((Vector3f)scriptMan.getValue("finishPlatPos"));
        createPhysicsPlane("finishPlatPhysicsPlane");
        
        return levelN;
    }

    private void createPhysicsPlane(String name) throws IOException
    {
        //Create the entity from the customCube obj
        Entity physicsPlane = sm.createEntity(name, "customCube.obj");
        physicsPlane.setPrimitive(Primitive.TRIANGLES);
        SceneNode physicsPlaneNode = sm.getRootSceneNode().createChildSceneNode(name + "Node");

        //Texture it...
        TextureState tState = (TextureState)sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
        tState.setTexture(physicsPlaneTex);
        physicsPlane.setRenderState(tState);

        //Size and physics
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

    private void createPhysicsPlaneWithRotationAboutY(String name) throws IOException
    {
        Entity physicsPlane = sm.createEntity(name, "cube.obj");
        physicsPlane.setPrimitive(Primitive.TRIANGLES);
        SceneNode physicsPlaneNode = sm.getRootSceneNode().createChildSceneNode(name + "Node");
        physicsPlaneNode.attachObject(physicsPlane);
        physicsPlaneNode.setLocalPosition((Vector3f)scriptMan.getValue(name + "Pos"));
        physicsPlaneNode.setLocalScale((Vector3f)scriptMan.getValue(name + "Scale"));
        physicsPlane.setVisible((boolean)scriptMan.getValue(name + "Vis"));
        physMan.createCubePhysicsObjectWithRotationAboutY(physicsPlaneNode, 0f, 1f, 1f, .99f, (Degreef) scriptMan.getValue(name + "RotY"));

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

    public Vector<SceneNode> getEndPlatformPhysicsPlanes()
    {
        return endPlatformPhysicsPlanes;
    }
}
