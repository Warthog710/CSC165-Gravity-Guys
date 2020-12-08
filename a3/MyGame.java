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
import ray.rage.rendersystem.Renderable.Primitive;
import ray.rage.scene.*;
import ray.rage.scene.Camera.Frustum.*;
import ray.rml.Degreef;
import ray.rml.Vector3;
import ray.rml.Vector3f;
import ray.rage.rendersystem.gl4.GL4RenderSystem;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.input.*;
import ray.input.action.*;
import myGameEngine.*;
import myGameEngine.Actions.*;

public class MyGame extends VariableFrameRateGame 
{
        GL4RenderSystem rs;

        //Game Variables
        private NetworkedClient networkedClient;
        private PhysicsManager physMan;
        private UpdateGameVariables gVars;
        private InputManager im;
        private ScriptManager scriptMan;
        private OrbitCameraController orbitCamera;
        private GhostAvatars ghosts;
        private float lastUpdateTime = 0.0f, elapsTime = 0.0f;
        private Action moveRightAction, moveFwdAction, moveYawAction, jumpAction, resetAction, toggleLightAction;
        private AnimationManager animMan;
        private SoundManager soundMan;

        public WinCondition wc;
        public PlatformController pc;
        public DetectWallCollision collision;
        public BouncyBalls bouncyBalls;
        public Walls platformWalls;
        public NPC npc;
        public Flails flails;

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

        @Override
        public void shutdown()
        {
                try
                {
                        super.shutdown();
                }
                catch (Exception e)
                {
                        this.exit();
                }
        }

        @Override
        public void exit()
        {
                try
                {
                        super.exit();
                }
                catch (Exception e)
                {
                        exit();
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
                physMan = new PhysicsManager(-60f);
                physMan.createStaticGroundPlane(0f, .5f, .9f);
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
                                
                rs.getRenderWindow().setTitle("Plummet Parkour");
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

                //Load player mesh, skeleton, and texture
                //scriptMan.getValue("avatarName").toString()
                SkeletalEntity avatarE = sm.createSkeletalEntity(scriptMan.getValue("avatarName").toString(), "player.rkm", "player.rks");
                Texture tex = sm.getTextureManager().getAssetByPath("greenPlayer.png");
                TextureState tstate = (TextureState) sm.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
                tstate.setTexture(tex);
                avatarE.setRenderState(tstate);
                //load animations
                avatarE.loadAnimation(scriptMan.getValue("walkAnimation").toString(), "newWalk.rka");
                avatarE.loadAnimation(scriptMan.getValue("jumpAnimation").toString(), "jump.rka");

                SceneNode avatarN = sm.getRootSceneNode().createChildSceneNode(avatarE.getName() + "Node");
                avatarN.attachObject(avatarE);
                avatarN.setLocalScale(0.25f, 0.25f, 0.25f);
                avatarN.setLocalPosition((Vector3f)scriptMan.getValue("avatarPos"));
                
                float playerBounciness = 0f;
                float playerFriction = 1f;
                float playerDamping = .9f;
                physMan.createAvatarSphere(avatarN, 1f, playerBounciness, playerFriction, playerDamping);
                
                //? Fixes a movement bug
                avatarN.getPhysicsObject().setSleepThresholds(0f, 0f);                             

                //Set up ambient light
                sm.getAmbientLight().setIntensity((Color)scriptMan.getValue("ambColor"));

                //Set up directional light
                Light dlight = (Light)scriptMan.getValue("dLight");      
                SceneNode dlightNode = sm.getRootSceneNode().createChildSceneNode(scriptMan.getValue("lightName").toString());
                dlightNode.attachObject(dlight);
                dlightNode.setLocalPosition((Vector3f)scriptMan.getValue("dLightPos"));

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
                SceneNode levelN = level.loadLevelObjects();

                //Setup lamp
                Entity lampE = sm.createEntity("lamp", "lamp.obj");
                lampE.setPrimitive(Primitive.TRIANGLES);
                SceneNode lampN = sm.getRootSceneNode().createChildSceneNode(lampE.getName() + "Node");
                lampN.attachObject(lampE);
                lampN.rotate(Degreef.createFrom(-90), Vector3f.createFrom(0.0f, 1.0f, 0.0f));
                lampN.setLocalPosition(levelN.getChild("finishPlatformNode").getWorldPosition().add(2, 3, 2));
                
                //Set up lamp light
                Light lampLight = (Light)scriptMan.getValue("lampLight");
                SceneNode lampLightNode = lampN.createChildSceneNode(scriptMan.getValue("lampLightName").toString() + "Node");
                lampLightNode.attachObject(lampLight);
                lampLight.setVisible(false);
                
                //Setup walls & flails
                platformWalls = new Walls(scriptMan, physMan, eng);
                flails = new Flails(eng, physMan, scriptMan);   
                
                collision = new DetectWallCollision(platformWalls.getWalls());

                //Create input manager
                im = new GenericInputManager();

                //Configure orbit camera controller
                setupOrbitCamera(sm);

                //Setup ghosts
                ghosts = new GhostAvatars(sm);
                
                updateVerticalPosition();

                //Setup networking
                setupNetworking(avatarE);

                //Setup the bouncy balls
                bouncyBalls = new BouncyBalls(physMan, eng, networkedClient);

                //Setup NPC
                npc = new NPC(eng, scriptMan, networkedClient, soundMan, physMan);       
                
                //Setup animation and sound
                soundMan = new SoundManager(sm, scriptMan);
                animMan = new AnimationManager(avatarE, avatarN.getPhysicsObject(), scriptMan, soundMan, networkedClient);   
                soundMan.initAudio();

                //Configure controller(s)
                setupInputs(sm.getCamera(scriptMan.getValue("cameraName").toString()), sm, eng.getRenderSystem().getRenderWindow());

                //Setup gVars
                gVars = new UpdateGameVariables(sm, scriptMan, physMan, platformWalls);

                //Setup moving platforms
                pc = new PlatformController(physMan, scriptMan, sm.getSceneNode(scriptMan.getValue("avatarName").toString() + "Node"));
                pc.addNodeList(level.getEndPlatformPhysicsPlanes());

                //Setup win condition
                wc = new WinCondition(sm.getSceneNode(scriptMan.getValue("avatarName").toString() + "Node"), scriptMan, networkedClient, physMan);

        }

        protected void setupOrbitCamera(SceneManager sm) 
        {
                String avatarName = scriptMan.getValue("avatarName").toString() + "Node";
                String cameraName = scriptMan.getValue("cameraName").toString() + "Node";

                orbitCamera = new OrbitCameraController(sm.getSceneNode(cameraName),
                                sm.getSceneNode(avatarName), im, scriptMan);
        }

        protected void setupNetworking(Entity avatarE)
        {
                try
                {
                        networkedClient = new NetworkedClient(
                                InetAddress.getByName(scriptMan.getValue("serverAddress").toString()),
                                Integer.parseInt(scriptMan.getValue("serverPort").toString()), ghosts, scriptMan, this, avatarE);
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

                //Update game variables
                gVars.update();           

                //Set hud
                rs.setHUD(wc.getHudString());

                //Process inputs
                im.update(elapsTime - lastUpdateTime);

                //Check if player is on wedge and needs upward force
                SceneNode playerN = engine.getSceneManager().getSceneNode(scriptMan.getValue("avatarName").toString() + "Node");
                //Calculate distance from the wedge based on formula for distance between a point and a plane
                double distance = Math.abs(-298.8 * playerN.getLocalPosition().y() + 282.2 * playerN.getLocalPosition().z() - 11686.4)/
                					Math.sqrt(298.8*298.8 + 282.2*282.2);
                if (distance < 0.2 && playerN.getWorldPosition().z() < 70 && playerN.getWorldPosition().z() > 52) {
                	playerN.getPhysicsObject().applyForce(0, 27, 27, 0, 0, 0);
                }

                //Process physiscs world and update objects
                if (gVars.runPhysics)
                {
                        physMan.getPhysicsEngine().update(elapsTime - lastUpdateTime);
                        physMan.updatePhysicsObjects(engine.getSceneManager(), networkedClient, this);
                }

                //Update platform controller
                //!This update must occur after the physics engine update
                pc.update(elapsTime - lastUpdateTime);

                //Update network info
                networkedClient.processPackets(elapsTime - lastUpdateTime);

                //Update orbit camera controllers
                orbitCamera.updateCameraPosition();

                SkeletalEntity playerSE = (SkeletalEntity) engine.getSceneManager().getEntity(scriptMan.getValue("avatarName").toString());
                playerSE.update();
                soundMan.updateSound();
                animMan.checkAnimations();
                bouncyBalls.update(elapsTime - lastUpdateTime);
                npc.update(elapsTime - lastUpdateTime);
                wc.update(elapsTime - lastUpdateTime);

                //Record last update in MS
                lastUpdateTime = elapsTime;
        }

        protected void setupInputs(Camera camera, SceneManager sm, RenderWindow rw) 
        {
                List<Controller> controllerList = im.getControllers();
                String target = scriptMan.getValue("avatarName").toString() + "Node";

                //Setup actions
                moveYawAction = new MoveYawAction(orbitCamera, sm.getSceneNode(target), scriptMan);
                moveRightAction = new MoveRightAction(sm.getSceneNode(target), scriptMan, animMan, this);
                moveFwdAction = new MoveFwdAction(sm.getSceneNode(target), scriptMan, animMan, this);
                jumpAction = new JumpAction(sm.getSceneNode(target), scriptMan, animMan, this, physMan);
                resetAction = new ResetPlayerAction(sm.getSceneNode(target), scriptMan, physMan);
                toggleLightAction = new ToggleLightAction(sm.getSceneNode(target), scriptMan, this);

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
                			im.associateAction(controllerList.get(index), 
                    			        net.java.games.input.Component.Identifier.Button._1, jumpAction, 
                                                InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
                                        im.associateAction(controllerList.get(index), 
                                                net.java.games.input.Component.Identifier.Button._6, resetAction, 
                                                InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
                                        im.associateAction(controllerList.get(index), 
                                                net.java.games.input.Component.Identifier.Button._2, toggleLightAction, 
                                                InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
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
                                        im.associateAction(controllerList.get(index), 
                                        		net.java.games.input.Component.Identifier.Button._0, jumpAction, 
                                                InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);                                                            
                                        im.associateAction(controllerList.get(index), 
                                        		net.java.games.input.Component.Identifier.Button._5, resetAction, 
                                        		InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
                                        im.associateAction(controllerList.get(index), 
                                                net.java.games.input.Component.Identifier.Button._1, toggleLightAction, 
                                                InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
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

        //Update the vertical position based on the tesselation when the avatar is close to the ground
        public void updateVerticalPosition() 
        {
        	SceneNode avatarN = this.getEngine().getSceneManager().getSceneNode(scriptMan.getValue("avatarName").toString() + "Node");
                
                //Only execute if the avatar is close to ground zero
                if (avatarN.getLocalPosition().y() > 2.0f)
                        return;
                
                SceneNode tessN = this.getEngine().getSceneManager().getSceneNode(scriptMan.getValue("terrainName").toString() + "Node");
                Tessellation tessE = (Tessellation)tessN.getAttachedObject(scriptMan.getValue("terrainName").toString());
                
        	//Figure out Avatar's position relative to plane
                Vector3 worldAvatarPosition = avatarN.getWorldPosition();               
                Vector3 localAvatarPosition = avatarN.getLocalPosition();
                
            	//Use avatar World coordinates to get coordinates for height
                Vector3 newAvatarPosition = Vector3f.createFrom(localAvatarPosition.x(),
                        tessE.getWorldHeight(worldAvatarPosition.x(), worldAvatarPosition.z()),
                        localAvatarPosition.z());
                    
            	//Use avatar Local coordinates to set position, including height
            	avatarN.setLocalPosition(newAvatarPosition);
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
