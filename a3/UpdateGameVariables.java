package a3;

import myGameEngine.PhysicsManager;
import myGameEngine.ScriptManager;
import ray.rage.scene.SceneManager;
import ray.rage.scene.Tessellation;
import ray.rml.Degreef;
import ray.rml.Vector3f;

public class UpdateGameVariables 
{
    private SceneManager sm;
    private ScriptManager scriptMan;
    private PhysicsManager physMan;
    private Degreef prevPitch;
    protected boolean runPhysics;

    public UpdateGameVariables(SceneManager sm, ScriptManager scriptMan, PhysicsManager physMan)
    {
        this.sm = sm;
        this.scriptMan = scriptMan;
        this.physMan = physMan;
        this.prevPitch = (Degreef)this.scriptMan.getValue("wedgePhysicsPlaneRotX");
        this.runPhysics = (boolean)this.scriptMan.getValue("runPhysSim");
    }

    public void update()    
    {
        //If no update is required return
        if (!scriptMan.scriptUpdate("gameVariables.js"))
            return;

        System.out.println("\nUpdating Game Variables...");

        //Get names
        String terrainName = scriptMan.getValue("terrainName").toString();
        String levelName = scriptMan.getValue("levelName").toString();        
        String startPlatName = scriptMan.getValue("startPlatName").toString();
        String plat1Name = scriptMan.getValue("plat1Name").toString();
        String plat2Name = scriptMan.getValue("plat2Name").toString();
        String wishbonePlatName = scriptMan.getValue("wishbonePlatName").toString();
        String wedgePlatName = scriptMan.getValue("wedgePlatName").toString();
        String startPhysicsPlane = scriptMan.getValue("startPhysicsPlane").toString();
        String plat1PhysicsPlane = scriptMan.getValue("plat1PhysicsPlane").toString();
        String plat2PhysicsPlane = scriptMan.getValue("plat2PhysicsPlane").toString();
        String wedgePhysicsPlane = scriptMan.getValue("wedgePhysicsPlane").toString();

        //Update player position if "updateAvatarPos is true"
        if ((Boolean)scriptMan.getValue("updateAvatarPos"))
        {
                sm.getSceneNode(scriptMan.getValue("avatarName").toString() + "Node").setLocalPosition((Vector3f)scriptMan.getValue("avatarPos"));
                physMan.updatePhysicsTransforms(sm.getSceneNode(scriptMan.getValue("avatarName").toString() + "Node"));
        }

        //Update Tesselation
        sm.getSceneNode(terrainName + "Node").setLocalScale((Vector3f)scriptMan.getValue("terrainTessScale"));
        Tessellation tessE = (Tessellation)sm.getSceneNode(terrainName + "Node").getAttachedObject(terrainName);
        int tessQuality = Integer.parseInt(scriptMan.getValue("tessQuality").toString());
        float tessSubdivisions = Float.parseFloat(scriptMan.getValue("tessSubdivisions").toString());
        tessE.setQuality(tessQuality);
        tessE.setSubdivisions(tessSubdivisions);
        tessE.setHeightMapTiling(Integer.parseInt(scriptMan.getValue("heightTiling").toString()));
        tessE.setNormalMapTiling(Integer.parseInt(scriptMan.getValue("normalTiling").toString()));
        tessE.setTextureTiling(Integer.parseInt(scriptMan.getValue("textureTiling").toString()));
        
        //Update level one
        sm.getSceneNode(levelName + "Node").setLocalScale((Vector3f)scriptMan.getValue("levelScale"));
        sm.getSceneNode(levelName + "Node").setLocalPosition((Vector3f)scriptMan.getValue("levelPos"));
        sm.getSceneNode(startPlatName + "Node").setLocalScale((Vector3f)scriptMan.getValue("startPlatScale"));
        sm.getSceneNode(startPlatName + "Node").setLocalPosition((Vector3f)scriptMan.getValue("startPlatPos"));
        sm.getSceneNode(plat1Name + "Node").setLocalScale((Vector3f)scriptMan.getValue("plat1Scale"));
        sm.getSceneNode(plat1Name + "Node").setLocalPosition((Vector3f)scriptMan.getValue("plat1Pos"));
        sm.getSceneNode(plat2Name + "Node").setLocalScale((Vector3f)scriptMan.getValue("plat2Scale"));
        sm.getSceneNode(plat2Name + "Node").setLocalPosition((Vector3f)scriptMan.getValue("plat2Pos"));
        sm.getSceneNode(wishbonePlatName + "Node").setLocalScale((Vector3f)scriptMan.getValue("wishbonePlatScale"));
        sm.getSceneNode(wishbonePlatName + "Node").setLocalPosition((Vector3f)scriptMan.getValue("wishbonePlatPos"));
        sm.getSceneNode(wedgePlatName + "Node").setLocalScale((Vector3f)scriptMan.getValue("wedgePlatScale"));
        sm.getSceneNode(wedgePlatName + "Node").setLocalPosition((Vector3f)scriptMan.getValue("wedgePlatPos"));

        //Update level one physics ground planes
        sm.getSceneNode(startPhysicsPlane + "Node").setLocalPosition((Vector3f)scriptMan.getValue("startPhysicsPlanePos"));
        sm.getSceneNode(startPhysicsPlane + "Node").setLocalScale((Vector3f)scriptMan.getValue("startPhysicsPlaneScale"));
        sm.getSceneNode(startPhysicsPlane + "Node").getAttachedObject(startPhysicsPlane).setVisible((boolean)scriptMan.getValue("startPhysicsPlaneVis"));
        physMan.updatePhysicsTransforms(sm.getSceneNode(startPhysicsPlane + "Node"));

        sm.getSceneNode(plat1PhysicsPlane + "Node").setLocalPosition((Vector3f)scriptMan.getValue("plat1PhysicsPlanePos"));
        sm.getSceneNode(plat1PhysicsPlane + "Node").setLocalScale((Vector3f)scriptMan.getValue("plat1PhysicsPlaneScale"));
        sm.getSceneNode(plat1PhysicsPlane + "Node").getAttachedObject(plat1PhysicsPlane).setVisible((boolean)scriptMan.getValue("plat1PhysicsPlaneVis"));
        physMan.updatePhysicsTransforms(sm.getSceneNode(plat1PhysicsPlane + "Node"));

        sm.getSceneNode(plat2PhysicsPlane + "Node").setLocalPosition((Vector3f)scriptMan.getValue("plat2PhysicsPlanePos"));
        sm.getSceneNode(plat2PhysicsPlane + "Node").setLocalScale((Vector3f)scriptMan.getValue("plat2PhysicsPlaneScale"));
        sm.getSceneNode(plat2PhysicsPlane + "Node").getAttachedObject(plat2PhysicsPlane).setVisible((boolean)scriptMan.getValue("plat2PhysicsPlaneVis"));
        physMan.updatePhysicsTransforms(sm.getSceneNode(plat2PhysicsPlane + "Node"));

        sm.getSceneNode(wedgePhysicsPlane + "Node").setLocalPosition((Vector3f)scriptMan.getValue(wedgePhysicsPlane + "Pos"));
        sm.getSceneNode(wedgePhysicsPlane + "Node").setLocalScale((Vector3f)scriptMan.getValue(wedgePhysicsPlane + "Scale"));
        Degreef temp = (Degreef)scriptMan.getValue(wedgePhysicsPlane + "RotX");
        temp = Degreef.createFrom(temp.sub(prevPitch));
        prevPitch = (Degreef)scriptMan.getValue(wedgePhysicsPlane + "RotX");
        sm.getSceneNode(wedgePhysicsPlane + "Node").pitch(temp);
        sm.getSceneNode(wedgePhysicsPlane + "Node").getAttachedObject(wedgePhysicsPlane).setVisible((boolean)scriptMan.getValue(wedgePhysicsPlane + "Vis"));
        physMan.updatePhysicsTransforms(sm.getSceneNode(wedgePhysicsPlane + "Node"));

        //Update physics
        runPhysics = (boolean)scriptMan.getValue("runPhysSim");
    }
    
}
