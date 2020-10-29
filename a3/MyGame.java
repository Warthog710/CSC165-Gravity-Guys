package a3;

import java.awt.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import net.java.games.input.Controller;
import ray.rage.*;
import ray.rage.game.*;
import ray.rage.rendersystem.*;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.scene.*;
import ray.rage.scene.Camera.Frustum.*;
import ray.rage.rendersystem.gl4.GL4RenderSystem;
import ray.input.*;
import ray.input.action.*;

import myGameEngine.*;

//TODO: Have the game server save its IP in a file... If no IP parameter is passed read the IP from their

public class MyGame extends VariableFrameRateGame 
{

        OrbitCameraController playerOneOrbitCameraController;
        String elapsTimeStr, counterStr, dispStr;
        float lastUpdateTime = 0.0f;
        float elapsTime = 0.0f;
        GhostAvatars ghosts;
        GL4RenderSystem rs;
        int elapsTimeSec;   

        private NetworkedClient networkedClient;
        private String serverAddress;
        private InputManager im;
        private int serverPort;

        private Action moveRightAction, moveFwdAction, moveYawAction;

        public MyGame(String serverAddress, int serverPort) 
        {
                super();

                //Setup server info
                this.serverAddress = serverAddress;
                this.serverPort = serverPort;
        }

        //TODO: If no parameters are passed... errors occur... Fail gracefully? Or assume no server exists and continue on

        public static void main(String[] args) 
        {
                //Read command line arguments & save them
                Game game = new MyGame(args[0], Integer.parseInt(args[1]));

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
                //TODO: This is only called if "ESC" is used to exit the game... Need this to work even if the window is closed manually...
                networkedClient.sendBYE();
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
                rs.createRenderWindow(new DisplayMode(1400, 900, 24, 60), false);
                rs.getRenderWindow().setTitle("Final Project (NAME TBD)");

        }

        @Override
        protected void setupCameras(SceneManager sm, RenderWindow rw) 
        {
                // Create camera for player one
                Camera cameraP1 = sm.createCamera("playerOneCamera", Projection.PERSPECTIVE);
                rw.getViewport(0).setCamera(cameraP1);
                cameraP1.setMode('n');

                // Attach player 1 camera to root scene node
                SceneNode cNodeP1 = sm.getRootSceneNode().createChildSceneNode("playerOneCameraNode");
                cNodeP1.attachObject(cameraP1);
        }

        @Override
        protected void setupScene(Engine eng, SceneManager sm) throws IOException 
        {
                //Place player avatar
                Entity dolphin1E = sm.createEntity("playerOneDolphin", "dolphinHighPoly.obj");
                dolphin1E.setPrimitive(Primitive.TRIANGLES);
                SceneNode dolphin1N = sm.getRootSceneNode().createChildSceneNode(dolphin1E.getName() + "Node");
                dolphin1N.attachObject(dolphin1E);
                dolphin1N.moveLeft(1.5f);
                dolphin1N.moveUp(.31f);

                //Place groundplane
                ManualObject groundPlane = new GroundPlaneObject(sm, eng).makeObject("groundPlane");
                groundPlane.setPrimitive(Primitive.TRIANGLES);
                SceneNode gpNode = sm.getRootSceneNode().createChildSceneNode("groundPlaneNode");
                gpNode.attachObject(groundPlane);

                //Set up ambient light
                sm.getAmbientLight().setIntensity(new Color(.3f, .3f, .3f));

                //Set up Skybox
                SkyBox sk = sm.createSkyBox("skybox");
                sk.setTexture(eng.getTextureManager().getAssetByPath("../skyboxes/blueSky/back.jpg"),
                                SkyBox.Face.BACK);
                sk.setTexture(eng.getTextureManager().getAssetByPath("../skyboxes/blueSky/front.jpg"),
                                SkyBox.Face.FRONT);
                sk.setTexture(eng.getTextureManager().getAssetByPath("../skyboxes/blueSky/left.jpg"),
                                SkyBox.Face.LEFT);
                sk.setTexture(eng.getTextureManager().getAssetByPath("../skyboxes/blueSky/right.jpg"),
                                SkyBox.Face.RIGHT);
                sk.setTexture(eng.getTextureManager().getAssetByPath("../skyboxes/blueSky/top.jpg"),
                                SkyBox.Face.TOP);
                sk.setTexture(eng.getTextureManager().getAssetByPath("../skyboxes/blueSky/bottom.jpg"),
                                SkyBox.Face.BOTTOM);
                sm.setActiveSkyBox(sk);

                //Create input manager
                im = new GenericInputManager();

                //Configure orbit camera controller
                setupOrbitCamera(sm);

                //Setup ghosts
                ghosts = new GhostAvatars(sm);

                //Setup networking
                setupNetworking();

                //Configure controller(s)
                setupInputs(sm.getCamera("playerOneCamera"), sm, eng.getRenderSystem().getRenderWindow());
        }

        protected void setupOrbitCamera(SceneManager sm) 
        {
                playerOneOrbitCameraController = new OrbitCameraController(sm.getSceneNode("playerOneCameraNode"),
                                sm.getSceneNode("playerOneDolphinNode"), im);
        }

        protected void setupNetworking()
        {
                try
                {
                        networkedClient = new NetworkedClient(InetAddress.getByName(serverAddress), serverPort, ghosts, this);
                }
                catch (UnknownHostException e)
                {
                        e.printStackTrace();
                }
                catch (IOException e)
                {
                        e.printStackTrace();
                }

                //Verify client was setup correctly
                if (networkedClient == null)
                {
                        System.out.println("Missing network host...");
                }
                //Else, send a join msg to the server
                else
                {
                        //Send name of the node that is joining
                        networkedClient.sendJOIN("playerOneDolphinNode");
                }
        }

        @Override
        protected void update(Engine engine) 
        {
                // Get window, and calculate times
                rs = (GL4RenderSystem) engine.getRenderSystem();
                elapsTime += engine.getElapsedTimeMillis();
                elapsTimeSec = Math.round(elapsTime / 1000.0f);

                // Get dolphin positions
                String playerOnePos = "(" + Integer.toString(Math
                                .round(engine.getSceneManager().getSceneNode("playerOneDolphinNode").getLocalPosition().x()))
                                + ", "
                                + Integer.toString(Math.round(engine.getSceneManager().getSceneNode("playerOneDolphinNode")
                                                .getLocalPosition().y()))
                                + ", " + Integer.toString(Math.round(engine.getSceneManager()
                                                .getSceneNode("playerOneDolphinNode").getLocalPosition().z()))
                                + ")";                


                // Set hud
                rs.setHUD("Dolphin Position: " + playerOnePos, 15, 15);

                // Process inputs
                im.update(elapsTime - lastUpdateTime);

                // Update orbit camera controllers
                playerOneOrbitCameraController.updateCameraPosition();

                //Update network info
                networkedClient.processPackets();

                //If I'm connected to a server
                if (networkedClient.isConnected)
                {
                        //Ask for details from the server
                        networkedClient.sendWANTDETAILSFOR();

                        //Send an update to the server (only will send if an update has actually occured)
                        networkedClient.sendUPDATEFOR("playerOneDolphinNode");
                }
                //Else, try to connect to a server (allows the game to connect to a server even if it starts after...)
                else
                {
                        networkedClient.sendJOIN("playerOneDolphinNode");
                }

                // Record last update in MS
                lastUpdateTime = elapsTime;
        }

        protected void setupInputs(Camera camera, SceneManager sm, RenderWindow rw) 
        {
                List<Controller> controllerList = im.getControllers();

                //Setup actions
                moveYawAction = new MoveYawAction(playerOneOrbitCameraController, sm.getSceneNode("playerOneDolphinNode"));
                moveRightAction = new MoveRightAction(sm.getSceneNode("playerOneDolphinNode"), networkedClient);
                moveFwdAction = new MoveFwdAction(sm.getSceneNode("playerOneDolphinNode"), networkedClient);

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
                        	if (controllerList.get(index).getName().contains("Wireless Controller")) {
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
                        	else {
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
                                playerOneOrbitCameraController.setupInputs(im, controllerList.get(index));
                        	
                        }

                        //If mouse, attach inputs...
                        else if (controllerList.get(index).getType() == Controller.Type.MOUSE)
                        {
                                //Attach mouse inputs here

                        }
                }
        }
}
