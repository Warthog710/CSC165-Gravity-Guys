package a3;
import myGameEngine.PhysicsManager;
import myGameEngine.ScriptManager;
import ray.rage.scene.SceneManager;
import ray.rage.scene.SceneNode;
import ray.rage.scene.Tessellation;
import ray.rml.Degreef;
import ray.rml.Vector3f;

public class UpdateGameVariables 
{
    private SceneManager sm;
    private ScriptManager scriptMan;
    private PhysicsManager physMan;
    private Walls platformWalls;
    private Degreef prevPitch, wishBoneOneYaw, wishBoneTwoYaw, wishBoneThreePitch, wishBoneFourPitch;
    protected boolean runPhysics;

    public UpdateGameVariables(SceneManager sm, ScriptManager scriptMan, PhysicsManager physMan, Walls platformWalls)
    {
        this.sm = sm;
        this.scriptMan = scriptMan;
        this.physMan = physMan;
        this.platformWalls = platformWalls;
        this.prevPitch = (Degreef)this.scriptMan.getValue("wedgePhysicsPlaneRotX");
        this.wishBoneOneYaw = (Degreef)this.scriptMan.getValue("wishBoneOneRotY");
        this.wishBoneTwoYaw = (Degreef)this.scriptMan.getValue("wishBoneTwoRotY");
        this.wishBoneThreePitch = (Degreef)this.scriptMan.getValue("wishBoneThreeRotY");
        this.wishBoneFourPitch = (Degreef)this.scriptMan.getValue("wishBoneThreeRotY");
        this.runPhysics = (boolean)this.scriptMan.getValue("runPhysSim");
    }

    public void update()    
    {
        //If no update is required return
        if (!scriptMan.scriptUpdate("gameVariables.js") && !scriptMan.scriptUpdate("movementInfo.js"))
            return;

        System.out.println("\nUpdating Game Variables...");
        System.out.println("Note: THIS WILL BREAK THE PHYSICS WORLD!!!");

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
        String plat3PhysicsPlane = scriptMan.getValue("plat3PhysicsPlane").toString();
        String plat4PhysicsPlane = scriptMan.getValue("plat4PhysicsPlane").toString();
        String wedgePhysicsPlane = scriptMan.getValue("wedgePhysicsPlane").toString();
        String endPlat1PhysicsPlane = scriptMan.getValue("endPlat1PhysicsPlane").toString();
        String endPlat2PhysicsPlane = scriptMan.getValue("endPlat2PhysicsPlane").toString();
        String endPlat3PhysicsPlane = scriptMan.getValue("endPlat3PhysicsPlane").toString();
        String endPlat4PhysicsPlane = scriptMan.getValue("endPlat4PhysicsPlane").toString();
        String endPlat5PhysicsPlane = scriptMan.getValue("endPlat5PhysicsPlane").toString();
        String wishBoneOne = scriptMan.getValue("wishBoneOne").toString();
        String wishBoneTwo = scriptMan.getValue("wishBoneTwo").toString();
        String wishBoneThree = scriptMan.getValue("wishBoneThree").toString();
        String wishBoneFour = scriptMan.getValue("wishBoneFour").toString();
        String avatarName = scriptMan.getValue("avatarName").toString();

        //Update player position if "updateAvatarPos is true"
        if ((Boolean)scriptMan.getValue("updateAvatarPos"))
        {
                sm.getSceneNode(avatarName + "Node").setLocalPosition((Vector3f)scriptMan.getValue("avatarPos"));
                physMan.updatePhysicsTransforms(sm.getSceneNode(avatarName + "Node"));
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

        sm.getSceneNode(plat3PhysicsPlane + "Node").setLocalPosition((Vector3f)scriptMan.getValue("plat3PhysicsPlanePos"));
        sm.getSceneNode(plat3PhysicsPlane + "Node").setLocalScale((Vector3f)scriptMan.getValue("plat3PhysicsPlaneScale"));
        sm.getSceneNode(plat3PhysicsPlane + "Node").getAttachedObject(plat3PhysicsPlane).setVisible((boolean)scriptMan.getValue("plat3PhysicsPlaneVis"));
        physMan.updatePhysicsTransforms(sm.getSceneNode(plat3PhysicsPlane + "Node"));

        sm.getSceneNode(plat4PhysicsPlane + "Node").setLocalPosition((Vector3f)scriptMan.getValue("plat4PhysicsPlanePos"));
        sm.getSceneNode(plat4PhysicsPlane + "Node").setLocalScale((Vector3f)scriptMan.getValue("plat4PhysicsPlaneScale"));
        sm.getSceneNode(plat4PhysicsPlane + "Node").getAttachedObject(plat4PhysicsPlane).setVisible((boolean)scriptMan.getValue("plat4PhysicsPlaneVis"));
        physMan.updatePhysicsTransforms(sm.getSceneNode(plat4PhysicsPlane + "Node"));

        sm.getSceneNode(endPlat1PhysicsPlane + "Node").setLocalPosition((Vector3f)scriptMan.getValue("endPlat1PhysicsPlanePos"));
        sm.getSceneNode(endPlat1PhysicsPlane + "Node").setLocalScale((Vector3f)scriptMan.getValue("endPlat1PhysicsPlaneScale"));
        sm.getSceneNode(endPlat1PhysicsPlane + "Node").getAttachedObject(endPlat1PhysicsPlane).setVisible((boolean)scriptMan.getValue("endPlat1PhysicsPlaneVis"));
        physMan.updatePhysicsTransforms(sm.getSceneNode(endPlat1PhysicsPlane + "Node"));

        sm.getSceneNode(endPlat2PhysicsPlane + "Node").setLocalPosition((Vector3f)scriptMan.getValue("endPlat2PhysicsPlanePos"));
        sm.getSceneNode(endPlat2PhysicsPlane + "Node").setLocalScale((Vector3f)scriptMan.getValue("endPlat2PhysicsPlaneScale"));
        sm.getSceneNode(endPlat2PhysicsPlane + "Node").getAttachedObject(endPlat2PhysicsPlane).setVisible((boolean)scriptMan.getValue("endPlat2PhysicsPlaneVis"));
        physMan.updatePhysicsTransforms(sm.getSceneNode(endPlat2PhysicsPlane + "Node"));

        sm.getSceneNode(endPlat3PhysicsPlane + "Node").setLocalPosition((Vector3f)scriptMan.getValue("endPlat3PhysicsPlanePos"));
        sm.getSceneNode(endPlat3PhysicsPlane + "Node").setLocalScale((Vector3f)scriptMan.getValue("endPlat3PhysicsPlaneScale"));
        sm.getSceneNode(endPlat3PhysicsPlane + "Node").getAttachedObject(endPlat3PhysicsPlane).setVisible((boolean)scriptMan.getValue("endPlat3PhysicsPlaneVis"));
        physMan.updatePhysicsTransforms(sm.getSceneNode(endPlat3PhysicsPlane + "Node"));

        sm.getSceneNode(endPlat4PhysicsPlane + "Node").setLocalPosition((Vector3f)scriptMan.getValue("endPlat4PhysicsPlanePos"));
        sm.getSceneNode(endPlat4PhysicsPlane + "Node").setLocalScale((Vector3f)scriptMan.getValue("endPlat4PhysicsPlaneScale"));
        sm.getSceneNode(endPlat4PhysicsPlane + "Node").getAttachedObject(endPlat4PhysicsPlane).setVisible((boolean)scriptMan.getValue("endPlat4PhysicsPlaneVis"));
        physMan.updatePhysicsTransforms(sm.getSceneNode(endPlat4PhysicsPlane + "Node"));

        sm.getSceneNode(endPlat5PhysicsPlane + "Node").setLocalPosition((Vector3f)scriptMan.getValue("endPlat5PhysicsPlanePos"));
        sm.getSceneNode(endPlat5PhysicsPlane + "Node").setLocalScale((Vector3f)scriptMan.getValue("endPlat5PhysicsPlaneScale"));
        sm.getSceneNode(endPlat5PhysicsPlane + "Node").getAttachedObject(endPlat5PhysicsPlane).setVisible((boolean)scriptMan.getValue("endPlat5PhysicsPlaneVis"));
        physMan.updatePhysicsTransforms(sm.getSceneNode(endPlat5PhysicsPlane + "Node"));

        sm.getSceneNode(wedgePhysicsPlane + "Node").setLocalPosition((Vector3f)scriptMan.getValue(wedgePhysicsPlane + "Pos"));
        sm.getSceneNode(wedgePhysicsPlane + "Node").setLocalScale((Vector3f)scriptMan.getValue(wedgePhysicsPlane + "Scale"));
        Degreef temp = (Degreef)scriptMan.getValue(wedgePhysicsPlane + "RotX");
        temp = Degreef.createFrom(temp.sub(prevPitch));
        prevPitch = (Degreef)scriptMan.getValue(wedgePhysicsPlane + "RotX");
        sm.getSceneNode(wedgePhysicsPlane + "Node").pitch(temp);
        sm.getSceneNode(wedgePhysicsPlane + "Node").getAttachedObject(wedgePhysicsPlane).setVisible((boolean)scriptMan.getValue(wedgePhysicsPlane + "Vis"));
        physMan.updatePhysicsTransforms(sm.getSceneNode(wedgePhysicsPlane + "Node"));

        sm.getSceneNode(wishBoneOne + "Node").setLocalPosition((Vector3f)scriptMan.getValue(wishBoneOne + "Pos"));
        sm.getSceneNode(wishBoneOne + "Node").setLocalScale((Vector3f)scriptMan.getValue(wishBoneOne + "Scale"));
        temp = (Degreef)scriptMan.getValue(wishBoneOne + "RotY");
        temp = Degreef.createFrom(temp.sub(wishBoneOneYaw));
        wishBoneOneYaw = (Degreef)scriptMan.getValue(wishBoneOne + "RotY");
        sm.getSceneNode(wishBoneOne + "Node").roll(temp);
        sm.getSceneNode(wishBoneOne + "Node").getAttachedObject(wishBoneOne).setVisible((boolean)scriptMan.getValue(wishBoneOne + "Vis"));
        physMan.updatePhysicsTransforms(sm.getSceneNode(wishBoneOne + "Node"));

        sm.getSceneNode(wishBoneTwo + "Node").setLocalPosition((Vector3f)scriptMan.getValue(wishBoneTwo + "Pos"));
        sm.getSceneNode(wishBoneTwo + "Node").setLocalScale((Vector3f)scriptMan.getValue(wishBoneTwo + "Scale"));
        temp = (Degreef)scriptMan.getValue(wishBoneTwo + "RotY");
        temp = Degreef.createFrom(temp.sub(wishBoneTwoYaw));
        wishBoneTwoYaw = (Degreef)scriptMan.getValue(wishBoneTwo + "RotY");
        sm.getSceneNode(wishBoneTwo + "Node").roll(temp);
        sm.getSceneNode(wishBoneTwo + "Node").getAttachedObject(wishBoneTwo).setVisible((boolean)scriptMan.getValue(wishBoneTwo + "Vis"));
        physMan.updatePhysicsTransforms(sm.getSceneNode(wishBoneTwo + "Node"));

        sm.getSceneNode(wishBoneThree + "Node").setLocalPosition((Vector3f)scriptMan.getValue(wishBoneThree + "Pos"));
        sm.getSceneNode(wishBoneThree + "Node").setLocalScale((Vector3f)scriptMan.getValue(wishBoneThree + "Scale"));
        temp = (Degreef)scriptMan.getValue(wishBoneThree + "RotY");
        temp = Degreef.createFrom(temp.sub(wishBoneThreePitch));
        wishBoneThreePitch = (Degreef)scriptMan.getValue(wishBoneThree + "RotY");
        sm.getSceneNode(wishBoneThree + "Node").yaw(temp);
        sm.getSceneNode(wishBoneThree + "Node").getAttachedObject(wishBoneThree).setVisible((boolean)scriptMan.getValue(wishBoneThree + "Vis"));
        physMan.updatePhysicsTransforms(sm.getSceneNode(wishBoneThree + "Node"));

        sm.getSceneNode(wishBoneFour + "Node").setLocalPosition((Vector3f)scriptMan.getValue(wishBoneFour + "Pos"));
        sm.getSceneNode(wishBoneFour + "Node").setLocalScale((Vector3f)scriptMan.getValue(wishBoneFour + "Scale"));
        temp = (Degreef)scriptMan.getValue(wishBoneFour + "RotY");
        temp = Degreef.createFrom(temp.sub(wishBoneFourPitch));
        wishBoneFourPitch = (Degreef)scriptMan.getValue(wishBoneFour + "RotY");
        sm.getSceneNode(wishBoneFour + "Node").yaw(temp);
        sm.getSceneNode(wishBoneFour + "Node").getAttachedObject(wishBoneFour).setVisible((boolean)scriptMan.getValue(wishBoneFour + "Vis"));
        physMan.updatePhysicsTransforms(sm.getSceneNode(wishBoneFour + "Node"));

        updateWalls();
        updateFlails();
        updateNPC();
        
        //Update physics
        runPhysics = (boolean)scriptMan.getValue("runPhysSim");
    }

    //Updates the positions of the walls
    private void updateWalls()
    {
        int offset = 0;
        Vector3f startPos = (Vector3f)scriptMan.getValue("wallStartingPos");
        Vector3f wallScale = (Vector3f)scriptMan.getValue("wallScale");

        for (SceneNode node : platformWalls.getWalls())
        {
            node.setLocalPosition(startPos.x(), startPos.y(), startPos.z() + offset);
            node.setLocalScale(wallScale);
            physMan.updatePhysicsTransforms(node);
            offset += Integer.parseInt(scriptMan.getValue("offset").toString());
            
        }
    }

    private void updateFlails()
    {
        String pillar = scriptMan.getValue("pillarName").toString();
        String flail = scriptMan.getValue("flailName").toString();
        String flailCube = scriptMan.getValue("flailCubeName").toString();

        for (int index = 0; index < 8; index++)
        {
            SceneNode pillarN = sm.getSceneNode(pillar + index + "Node");
            pillarN.setLocalPosition((Vector3f)scriptMan.getValue(pillar + index + "Pos"));
            pillarN.setLocalScale((Vector3f)scriptMan.getValue(pillar + "Scale"));
            physMan.updatePhysicsTransforms(pillarN);

            SceneNode flailN = sm.getSceneNode(flail + index + "Node");
            flailN.setLocalPosition((Vector3f)scriptMan.getValue(flail + index + "Pos"));
            flailN.setLocalScale((Vector3f)scriptMan.getValue(flail + "Scale"));
            physMan.updatePhysicsTransforms(flailN);

            SceneNode cubeN = sm.getSceneNode(flailCube + index + "Node");
            cubeN.setLocalPosition((Vector3f)scriptMan.getValue(flailCube + "Pos"));
            cubeN.setLocalScale((Vector3f)scriptMan.getValue(flailCube + "Scale"));
        }        
    }

    private void updateNPC()
    {
        String npcName = scriptMan.getValue("npcName").toString();
        String platformName = "npcPlatform";

        sm.getSceneNode(platformName + "Node").setLocalPosition((Vector3f)scriptMan.getValue("platformPos"));
        sm.getSceneNode(platformName + "Node").setLocalScale((Vector3f)scriptMan.getValue("platformScale"));
        physMan.updatePhysicsPosition(sm.getSceneNode(platformName + "Node"));
        sm.getSceneNode(npcName + "Node").setLocalPosition((Vector3f)scriptMan.getValue("npcStartLocation"));
    }    
}
