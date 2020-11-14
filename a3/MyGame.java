package a3;

import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.util.List;
import java.awt.geom.*;

import net.java.games.input.Controller;
import ray.rage.*;
import ray.rage.asset.texture.Texture;
import ray.rage.game.*;
import ray.rage.rendersystem.*;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.scene.*;
import ray.rage.scene.Camera.Frustum.*;
import ray.rml.Vector3;
import ray.rml.Vector3f;
import ray.rage.rendersystem.gl4.GL4RenderSystem;
import ray.rage.rendersystem.states.TextureState;
import ray.input.*;
import ray.input.action.*;

import myGameEngine.*;

public class MyGame extends VariableFrameRateGame 
{
        GL4RenderSystem rs;

        //Game Variables
        private NetworkedClient networkedClient;
        private PhysicsManager physMan;
        private InputManager im;
        private ScriptManager scriptMan;
        private OrbitCameraController orbitCamera;
        private GhostAvatars ghosts;
        private float lastUpdateTime = 0.0f, elapsTime = 0.0f;
        private Action moveRightAction, moveFwdAction, moveYawAction;
        private boolean runPhysics;

        public static void main(String[] args) 
        {
                //? Server parameters are now saved in gameVariables.js
                Game game = new MyGame();

                try 
                {
                        game.startup();
                        game.run();
                } 
                catch (Exception e) 
                {
                        e.printStackTrace(System.err);
                } 
                finally 
                {
                        game.shutdown();
                        game.exit();
                }
        }

        public MyGame()
        {
                //Call parent constructor
                super();

                //Setup script manager and load initial script files    
                scriptMan = new ScriptManager();  
                scriptMan.loadScript("gameVariables.js");
                scriptMan.loadScript("movementInfo.js");

                //Setup physics manager
                physMan = new PhysicsManager(-8f, scriptMan);
                runPhysics = (boolean)scriptMan.getValue("runPhysSim");
        }

        @Override
        protected void setupWindow(RenderSystem rs, GraphicsEnvironment ge) 
        {
                //DisplaySettingsDialog dsd = new
                //DisplaySettingsDialog(ge.getDefaultScreenDevice());
                //dsd.showIt();
                //rs.createRenderWindow(dsd.getSelectedDisplayMode(),
                //dsd.isFullScreenModeSelected());

                //Creates a fixed window... this is quicker for testing
                rs.createRenderWindow(new DisplayMode(Integer.parseInt(scriptMan.getValue("windowWidth").toString()),
                                Integer.parseInt(scriptMan.getValue("windowHeight").toString()), 24, 60), false);
                                
                rs.getRenderWindow().setTitle("Final Project (NAME TBD)");
        }

        @Override
        protected void setupCameras(SceneManager sm, RenderWindow rw) 
        {
                //Create camera for player
                Camera camera = sm.createCamera(scriptMan.getValue("cameraName").toString(), Projection.PERSPECTIVE);
                rw.getViewport(0).setCamera(camera);
                camera.setMode('n');

                //Attach camera to root scene node
                SceneNode cNodeP1 = sm.getRootSceneNode().createChildSceneNode(camera.getName() + "Node");
                cNodeP1.attachObject(camera);
        }

        @Override
        protected void setupScene(Engine eng, SceneManager sm) throws IOException 
        {
                //Load the light script file
                scriptMan.putObjectInEngine("sm", sm);
                scriptMan.loadScript("lights.js");

                //Place player avatar
                Entity avatarE = sm.createEntity(scriptMan.getValue("avatarName").toString(), "dolphinHighPoly.obj");
                avatarE.setPrimitive(Primitive.TRIANGLES);

                SceneNode avatarN = sm.getRootSceneNode().createChildSceneNode(avatarE.getName() + "Node");
                avatarN.attachObject(avatarE);
                avatarN.setLocalPosition((Vector3f)scriptMan.getValue("avatarPos"));   
                physMan.createSpherePhysicsObject(avatarN, 1f, 1f, 1f, .9f);     
                
                //! Temp physics cube
                Entity cubeE = sm.createEntity("cube", "cube.obj");
                cubeE.setPrimitive(Primitive.TRIANGLES);

                SceneNode cubeN = sm.getRootSceneNode().createChildSceneNode(cubeE.getName() + "Node");
                cubeN.attachObject(cubeE);
                cubeN.setLocalScale(1f, 1f, 1f);
                physMan.createCubePhysicsObject(cubeN, 0f, 1f, 1f, .9f);

          
                //Set up ambient light
                sm.getAmbientLight().setIntensity((Color)scriptMan.getValue("ambColor"));

                //Set up point light
                Light plight = (Light)scriptMan.getValue("pLight");        		
                SceneNode plightNode = sm.getRootSceneNode().createChildSceneNode(scriptMan.getValue("lightName").toString());
                plightNode.attachObject(plight);
                plightNode.setLocalPosition((Vector3f)scriptMan.getValue("pLightPos"));

                //Setup skybox
                setupSkybox(eng);

                //Grab values for tesselation from script
                int tessQuality = Integer.parseInt(scriptMan.getValue("tessQuality").toString());
                float tessSubdivisions = Float.parseFloat(scriptMan.getValue("tessSubdivisions").toString());
                
                //Set up terrain
                Tessellation tessE = sm.createTessellation(scriptMan.getValue("terrainName").toString(), tessQuality);
                tessE.setSubdivisions(tessSubdivisions);
                tessE.setHeightMap(eng, "tileableHeightMap.png");
                tessE.setNormalMap(eng, "tileableNormal.png");
                tessE.setTexture(eng, "rock.jpg");
                tessE.getTextureState().setWrapMode(TextureState.WrapMode.REPEAT);
                tessE.setHeightMapTiling(Integer.parseInt(scriptMan.getValue("heightTiling").toString()));
                tessE.setNormalMapTiling(Integer.parseInt(scriptMan.getValue("normalTiling").toString()));
                tessE.setTextureTiling(Integer.parseInt(scriptMan.getValue("textureTiling").toString()));

                SceneNode tessN = sm.getRootSceneNode().createChildSceneNode(tessE.getName() + "Node");
                tessN.attachObject(tessE);
                tessN.scale((Vector3f)scriptMan.getValue("terrainTessScale"));
                
                //Load level one
                LevelOne level = new LevelOne(eng, scriptMan, physMan);
                level.loadLevelObjects();

                //Create input manager
                im = new GenericInputManager();

                //Configure orbit camera controller
                setupOrbitCamera(sm);

                //Setup ghosts
                ghosts = new GhostAvatars(sm);
                
                updateVerticalPosition();

                //Setup networking
                setupNetworking();

                //Configure controller(s)
                setupInputs(sm.getCamera(scriptMan.getValue("cameraName").toString()), sm, eng.getRenderSystem().getRenderWindow());
        }

        protected void setupOrbitCamera(SceneManager sm) 
        {
                String avatarName = scriptMan.getValue("avatarName").toString() + "Node";
                String cameraName = scriptMan.getValue("cameraName").toString() + "Node";

                orbitCamera = new OrbitCameraController(sm.getSceneNode(cameraName),
                                sm.getSceneNode(avatarName), im, scriptMan);
        }

        protected void setupNetworking()
        {
                try
                {
                        networkedClient = new NetworkedClient(
                                InetAddress.getByName(scriptMan.getValue("serverAddress").toString()),
                                Integer.parseInt(scriptMan.getValue("serverPort").toString()), ghosts, scriptMan, this);
                }
                catch (Exception e)
                {
                        e.printStackTrace();
                }
        }

        @Override
        protected void update(Engine engine) 
        {
                //Get window, and calculate times
                rs = (GL4RenderSystem) engine.getRenderSystem();
                elapsTime += engine.getElapsedTimeMillis();

                //If game variables have been updated... update it
                if (scriptMan.scriptUpdate("gameVariables.js"))
                        updateGameVariables(engine.getSceneManager());                

                //Get avatar positions
                String playerOnePos = "("
                        + Integer.toString(Math.round(engine.getSceneManager()
                        .getSceneNode(scriptMan.getValue("avatarName").toString() + "Node")
                        .getLocalPosition().x()))
                        + ", "
                        + Integer.toString(Math.round(engine.getSceneManager()
                        .getSceneNode(scriptMan.getValue("avatarName").toString() + "Node")
                        .getLocalPosition().y()))
                        + ", "
                        + Integer.toString(Math.round(engine.getSceneManager()
                        .getSceneNode(scriptMan.getValue("avatarName").toString() + "Node")
                        .getLocalPosition().z()))
                        + ")";         

                //Set hud
                rs.setHUD("Avatar Position: " + playerOnePos, (Integer) scriptMan.getValue("hudX"),
                                (Integer) scriptMan.getValue("hudY"));

                //Process inputs
                im.update(elapsTime - lastUpdateTime);

                //Process physiscs world and update objects
                if (runPhysics)
                {
                        physMan.getPhysicsEngine().update(elapsTime - lastUpdateTime);
                        physMan.updatePhysicsObjects(engine.getSceneManager());
                }

                //Update network info
                networkedClient.processPackets(elapsTime - lastUpdateTime);

                //Update orbit camera controllers
                orbitCamera.updateCameraPosition();

                //Record last update in MS
                lastUpdateTime = elapsTime;
        }

        protected void setupInputs(Camera camera, SceneManager sm, RenderWindow rw) 
        {
                List<Controller> controllerList = im.getControllers();
                String target = scriptMan.getValue("avatarName").toString() + "Node";

                //Setup actions
                moveYawAction = new MoveYawAction(orbitCamera, sm.getSceneNode(target), scriptMan, networkedClient);
                moveRightAction = new MoveRightAction(sm.getSceneNode(target), networkedClient, scriptMan, physMan, this);
                moveFwdAction = new MoveFwdAction(sm.getSceneNode(target), networkedClient, scriptMan, physMan, this);

                // Iterate over all input devices
                for (int index = 0; index < controllerList.size(); index++) 
                {
                        // NOTE: This code also deals with no gamepads as it would not attempt to attach
                        // any gamepad controls unless it see's an item of Type.GAMEPAD

                        // If keyboard, attach inputs...
                        if (controllerList.get(index).getType() == Controller.Type.KEYBOARD) 
                        {
                                //Setup keyboard input here

                        }

                        // If gamepad, attach inputs...
                        else if (controllerList.get(index).getType() == Controller.Type.GAMEPAD) 
                        {
                                if (controllerList.get(index).getName().contains("Wireless Controller")) 
                                {
                			im.associateAction(controllerList.get(index), 
                				net.java.games.input.Component.Identifier.Axis.Y, moveFwdAction, 
                				InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                			im.associateAction(controllerList.get(index), 
                				net.java.games.input.Component.Identifier.Axis.X, moveRightAction, 
        					InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                			im.associateAction(controllerList.get(index), 
                				net.java.games.input.Component.Identifier.Axis.Z, moveYawAction, 
        					InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                		}
                                else 
                                {
                                        im.associateAction(controllerList.get(index),
                                                net.java.games.input.Component.Identifier.Axis.Z, moveYawAction,
                                                InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                                        im.associateAction(controllerList.get(index),
                                                net.java.games.input.Component.Identifier.Axis.Y, moveFwdAction,
                                                InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                                        im.associateAction(controllerList.get(index),
                                                net.java.games.input.Component.Identifier.Axis.X, moveRightAction,
                                                InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);                                
                                }
                                
                                //Setup orbit camera controller inputs
                                orbitCamera.setupInputs(im, controllerList.get(index));                        	
                        }

                        //If mouse, attach inputs...
                        else if (controllerList.get(index).getType() == Controller.Type.MOUSE)
                        {
                                //Attach mouse inputs here

                        }
                }
        }

        //? Added an offset of .4f to the dolphin to move it up a bit
        public void updateVerticalPosition() 
        {
        	SceneNode avatarN = this.getEngine().getSceneManager().getSceneNode(scriptMan.getValue("avatarName").toString() + "Node");
        	SceneNode tessN = this.getEngine().getSceneManager().getSceneNode(scriptMan.getValue("terrainName").toString() + "Node");
                Tessellation tessE = (Tessellation)tessN.getAttachedObject(scriptMan.getValue("terrainName").toString());

                //Only execute if the avatar is close to ground zero
                if (avatarN.getLocalPosition().y() > 2.0f)
                        return;
                
        	//Figure out Avatar's position relative to plane
                Vector3 worldAvatarPosition = avatarN.getWorldPosition();               
                Vector3 localAvatarPosition = avatarN.getLocalPosition();
                
            	//Use avatar World coordinates to get coordinates for height
                Vector3 newAvatarPosition = Vector3f.createFrom(localAvatarPosition.x(),
                                tessE.getWorldHeight(worldAvatarPosition.x(), worldAvatarPosition.z()) + 0.4f,
                                localAvatarPosition.z());
                    
            	//Use avatar Local coordinates to set position, including height
            	avatarN.setLocalPosition(newAvatarPosition);
        }

        private void updateGameVariables(SceneManager sm)
        {
                System.out.println("Updating variables...");
                String terrainName = scriptMan.getValue("terrainName").toString();
                String levelName = scriptMan.getValue("levelName").toString();
                
                String startPlatName = scriptMan.getValue("startPlatName").toString();
                String plat1Name = scriptMan.getValue("plat1Name").toString();
                String plat2Name = scriptMan.getValue("plat2Name").toString();
                String wishbonePlatName = scriptMan.getValue("wishbonePlatName").toString();
                String wedgePlatName = scriptMan.getValue("wedgePlatName").toString();
                String startPhysicsPlane = scriptMan.getValue("startPhysicsPlane").toString();
                //String plat1PhysicsPlane = scriptMan.getValue("plat1PhysicsPlane").toString();
                //String plat2PhysicsPlane = scriptMan.getValue("plat2PhysicsPlane").toString();

                //Update player position if "updateAvatarPos is true"
                if ((Boolean)scriptMan.getValue("updateAvatarPos"))
                        sm.getSceneNode(scriptMan.getValue("avatarName").toString() + "Node").setLocalPosition((Vector3f)scriptMan.getValue("avatarPos"));

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
                //sm.getSceneNode(startPhysicsPlane + "Node").setLocalPosition((Vector3f)scriptMan.getValue("startPhysicsPlanePos"));
                //sm.getSceneNode(startPhysicsPlane + "Node").setLocalScale((Vector3f)scriptMan.getValue("startPhysicsPlaneScale"));
                //sm.getSceneNode(startPhysicsPlane + "Node").getAttachedObject(startPhysicsPlane).setVisible((boolean)scriptMan.getValue("startPhysicsPlaneVis"));
                //physMan.updatePhysicsTransforms(sm.getSceneNode(startPhysicsPlane + "Node"));

                //sm.getSceneNode(plat1PhysicsPlane + "Node").setLocalPosition((Vector3f)scriptMan.getValue("plat1PhysicsPlanePos"));
                //sm.getSceneNode(plat1PhysicsPlane + "Node").setLocalScale((Vector3f)scriptMan.getValue("plat1PhysicsPlaneScale"));
                //sm.getSceneNode(plat1PhysicsPlane + "Node").getAttachedObject(plat1PhysicsPlane).setVisible((boolean)scriptMan.getValue("plat1PhysicsPlaneVis"));
                //physMan.updatePhysicsTransforms(sm.getSceneNode(plat1PhysicsPlane + "Node"));


                //sm.getSceneNode(plat2PhysicsPlane + "Node").setLocalPosition((Vector3f)scriptMan.getValue("plat2PhysicsPlanePos"));
                //sm.getSceneNode(plat2PhysicsPlane + "Node").setLocalScale((Vector3f)scriptMan.getValue("plat2PhysicsPlaneScale"));
                //sm.getSceneNode(plat2PhysicsPlane + "Node").getAttachedObject(plat2PhysicsPlane).setVisible((boolean)scriptMan.getValue("plat2PhysicsPlaneVis"));
                //physMan.updatePhysicsTransforms(sm.getSceneNode(plat2PhysicsPlane + "Node"));


                //Update physics
                runPhysics = (boolean)scriptMan.getValue("runPhysSim");
        }

        private void setupSkybox(Engine eng) throws IOException
        {
                // Set up Skybox
                Texture front = eng.getTextureManager().getAssetByPath("../skyboxes/calm_sea/front.jpeg");
                Texture back = eng.getTextureManager().getAssetByPath("../skyboxes/calm_sea/back.jpeg");
                Texture left = eng.getTextureManager().getAssetByPath("../skyboxes/calm_sea/left.jpeg");
                Texture right = eng.getTextureManager().getAssetByPath("../skyboxes/calm_sea/right.jpeg");
                Texture top = eng.getTextureManager().getAssetByPath("../skyboxes/calm_sea/top.jpeg");
                Texture bottom = eng.getTextureManager().getAssetByPath("../skyboxes/calm_sea/bottom.jpeg");

                // Flip textures
                AffineTransform xform = new AffineTransform();
                xform.translate(0, front.getImage().getHeight());
                xform.scale(1d, -1d);

                front.transform(xform);
                back.transform(xform);
                left.transform(xform);
                right.transform(xform);
                top.transform(xform);
                bottom.transform(xform);

                //Load and set active
                SkyBox sk = eng.getSceneManager().createSkyBox("skybox");
                sk.setTexture(front, SkyBox.Face.FRONT);
                sk.setTexture(back, SkyBox.Face.BACK);
                sk.setTexture(left, SkyBox.Face.LEFT);
                sk.setTexture(right, SkyBox.Face.RIGHT);
                sk.setTexture(top, SkyBox.Face.TOP);
                sk.setTexture(bottom, SkyBox.Face.BOTTOM);

                eng.getSceneManager().setActiveSkyBox(sk);
        }
}
