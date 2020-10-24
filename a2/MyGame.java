package a2;

import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.HashMap;

import net.java.games.input.Controller;
import ray.rage.*;
import ray.rage.game.*;
import ray.rage.rendersystem.*;
import ray.rage.rendersystem.Renderable.*;
import ray.rage.scene.*;
import ray.rage.scene.Camera.Frustum.*;
import ray.rage.scene.controllers.*;
import ray.rml.*;
import ray.rage.rendersystem.gl4.GL4RenderSystem;
import ray.rage.rendersystem.states.RenderState;
import ray.rage.rendersystem.states.TextureState;
import ray.rage.asset.texture.*;
import ray.input.*;
import ray.input.action.*;

import myGameEngine.*;

//TO DO:

//Done
//Make a groud plane composed of 2 triangles
//Implement a charge jump, to reach the planets
//Jumping, start with a speed, that slows down every so often (time). Eventually reversing and going back to the ground. This simulates gravity
//Jumping detects collisions with planets, and reverse direction
//When the yaw of the dolphin changes, move the orbit camera's azimuth equal to the yaw so it tracks at the same position
//Write your own node controller to do something... (jump?)
//Move input assignments for orbit camera to setup inputs
//Make orbit camera use time values in the movement
//Split screen play (2 viewports horizontally split) keyboard/mouse controls one, gamepad the other
//idea: For hierachical nodes, simply create 2 subgroups of the planetgroup, each controller by a unique controller to identify which player touched each planet
//Implement a hierarchical relationship between 2 or more game objects
//Make the dolphins distinguisable from each other
//When the dolphin jumps pitch it up a bit
//Idea when the orbital camera hits the ground plane pitch it up
//Based on who is winning the center is colored that teams color?
//Goal is to grab a planet and take it to the origin?
//Idea: when the dolphin jumps up, the planet shrinks and begins following... still apply the node controller
//Can not move camera (or dolphin) below ground plane
//If the other dolphin shoots the planet that is following... it returns to its previous location and resets
//Add a line going down from a planet to the surface so the player know where to jump
//Dolphin cannot move beyond the ground plane
//Add an action to recenter the orbit camera
//Remove temp ground plane texture
//Show jump charge on HuD
//Include Full Screen Exclusive Mode Dialog...



//Hierarchy notes:
//      1. All of the planets when first spawned hang off the "planetGroup" scenenode
//      2. When a planet is grabbed by a player it is detached from the "planetGroup" and attached to the player's dolphin as a child
//      3. The planet attached to the dolphin is given a custom controller
//      4. Each planet has 2 children. A light positioned above the planet and a vertical line





public class MyGame extends VariableFrameRateGame 
{

        OrbitCameraController playerOneOrbitCameraController, playerTwoOrbitCameraController;
        Texture greenTexture, redTexture, orangeTexture;
        int elapsTimeSec, score = 0, nameOffset = 0;
        String elapsTimeStr, counterStr, dispStr;
        ChargeCollector playerOneCC, playerTwoCC;
        FollowManager player1FM, player2FM;
        float lastUpdateTime = 0.0f;
        float elapsTime = 0.0f;
        RotationController rc;
        StretchController sc;
        HeightController hc;
        GL4RenderSystem rs;
        MouseController mc;
        LaserManager lm;
        JumpManager jm;

        private int playerOneScore = 0, playerTwoScore = 0;
        private enum towerColor { ORANGE, GREEN, RED };
        towerColor tc = towerColor.ORANGE;
        private InputManager im;

        private Action moveRightAction, moveFwdAction, moveYawAction, playerOneJumpAction, playerTwoJumpAction,
                        mouseRadiusAction, moveFwdKeyboardAction, moveRightKeyboardAction, moveYawKeyboardAction,
                        playerOneLaserAction, playerTwoLaserAction, moveLeftKeyboardAction, moveBwdKeyboardAction;

        public MyGame() 
        {
                super();
                System.out.println("Dolphin Contest!");
                System.out.println("\nGamepad Controls\n================");
                System.out.println("Move dolphin forward/backward: Y Axis (left stick)");
                System.out.println("Move dolphin left/right: X axis (left stick)");
                System.out.println("Yaw dolphin left/right: Z axis (left & right triggers)");
                System.out.println("Move camera azimuth: RX axis (right stick)");
                System.out.println("Move camera elevation: RY axis (right stick)");
                System.out.println("Move camera radius: POV hat (forward/backward)");
                System.out.println("Jump: Button 1 (A) (hold down to charge your jump)");
                System.out.println("Fire laser: Button 3 (X) (shoot planets following the other player)");
                System.out.println("\nKeyboard/Mouse Controls\n=======================");
                System.out.println("Move dolphin forward: W");
                System.out.println("Move dolphin backward: S");
                System.out.println("Move dolphin left: A");
                System.out.println("Move dolphin right: D");
                System.out.println("Yaw dolphin left: Q");
                System.out.println("Yaw dolphin right: E");
                System.out.println("Move camera azimuth: Mouse X axis");
                System.out.println("Move camera elevation: Mouse Y axis");
                System.out.println("Move camera radius: Scroll wheel");
                System.out.println("Jump: Spacebar (hold down to charge your jump)");
                System.out.println("Fire laser: Left mouse button (shoot planets following the other player)");
                System.out.println("\nPRESS ESC AT ANY TIME TO EXIT THE GAME\n");
                System.out.println("Collect planets by jumping and hitting them. Once a planet is collected take it back to the center of the tower to score a point. Fire lasers at your oponenet and hit the planet they are carrying to make the planet go back to where it spawned. The tower in the middle will be the color of team that is winning (red or green) or orange if the teams are tied.");

        }

        public static void main(String[] args) 
        {
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
        protected void setupWindow(RenderSystem rs, GraphicsEnvironment ge) 
        {
                DisplaySettingsDialog dsd = new
                DisplaySettingsDialog(ge.getDefaultScreenDevice());
                dsd.showIt();
                rs.createRenderWindow(dsd.getSelectedDisplayMode(),
                dsd.isFullScreenModeSelected());
                //rs.createRenderWindow(new DisplayMode(1920, 1080, 24, 60), false);

                rs.getRenderWindow().setTitle("Dolphin Contest");

        }

        @Override
        protected void setupWindowViewports(RenderWindow rw) 
        {
                rw.addKeyListener(this);

                // Top viewport definition
                Viewport topView = rw.getViewport(0);
                topView.setDimensions(.5f, 0f, 1f, .5f);
                topView.setClearColor(Color.BLACK);

                // Bottom viewport definition
                Viewport bottomView = rw.createViewport(0f, 0f, 1f, .5f);
                bottomView.setClearColor(Color.BLACK);
        }

        @Override
        protected void setupCameras(SceneManager sm, RenderWindow rw) 
        {
                // Create camera for player one
                Camera cameraP1 = sm.createCamera("playerOneCamera", Projection.PERSPECTIVE);
                rw.getViewport(0).setCamera(cameraP1);
                cameraP1.setMode('n');

                // Create camera for player two
                Camera cameraP2 = sm.createCamera("playerTwoCamera", Projection.PERSPECTIVE);
                rw.getViewport(1).setCamera(cameraP2);
                cameraP2.setMode('n');

                // Attach player 1 camera to root scene node
                SceneNode cNodeP1 = sm.getRootSceneNode().createChildSceneNode("playerOneCameraNode");
                cNodeP1.attachObject(cameraP1);

                // Attach player 2 camera to root scene node
                SceneNode cNodeP2 = sm.getRootSceneNode().createChildSceneNode("playerTwoCameraNode");
                cNodeP2.attachObject(cameraP2);
        }

        @Override
        protected void setupScene(Engine eng, SceneManager sm) throws IOException 
        {
                //Place the eiffel tower
                Entity towerEntity = sm.createEntity("tower", "EiffelTower.obj");
                towerEntity.setPrimitive(Primitive.TRIANGLES);
                SceneNode towerNode = sm.getRootSceneNode().createChildSceneNode("towerNode");
                towerNode.attachObject(towerEntity);   
                towerNode.setLocalScale(0.3f, 0.3f, 0.3f);

                //Create a point light at the base of the tower
                Light pLight = sm.createLight("towerLight", Light.Type.POINT);
                pLight.setAmbient(new Color(0.01f, 0.01f, 0.01f));
                pLight.setDiffuse(new Color(0.1f, 0.1f, 0.1f));
                pLight.setSpecular(new Color(1f, 1f, 1f));

                SceneNode towerLightNode = towerNode.createChildSceneNode("towerLightNode");
                towerLightNode.attachObject(pLight);
                towerLightNode.moveUp(1.0f);

                //Pre-create 2 textures for recoloring the tower
                greenTexture = eng.getTextureManager().getAssetByPath("flatGreen.jpg");
                redTexture = eng.getTextureManager().getAssetByPath("flatRed.jpg");
                orangeTexture = eng.getTextureManager().getAssetByPath("flatOrange.jpg");

                // Place player 1 dolphin
                Entity dolphin1E = sm.createEntity("playerOneDolphin", "dolphinHighPoly.obj");
                dolphin1E.setPrimitive(Primitive.TRIANGLES);
                SceneNode dolphin1N = sm.getRootSceneNode().createChildSceneNode(dolphin1E.getName() + "Node");
                dolphin1N.attachObject(dolphin1E);
                dolphin1N.moveLeft(1.5f);
                dolphin1N.moveUp(.31f);
                
                //Make dolphin green
                Texture dolphin1T = eng.getTextureManager().getAssetByPath("greenDolphin.png");
                TextureState dolphin1S = (TextureState) eng.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
                dolphin1S.setTexture(dolphin1T);
                dolphin1E.setRenderState(dolphin1S);

                // Place player 2 dolphin
                Entity dolphin2E = sm.createEntity("playerTwoDolphin", "dolphinHighPoly.obj");
                dolphin2E.setPrimitive(Primitive.TRIANGLES);
                SceneNode dolphin2N = sm.getRootSceneNode().createChildSceneNode(dolphin2E.getName() + "Node");
                dolphin2N.attachObject(dolphin2E);
                dolphin2N.moveRight(1.5f);
                dolphin2N.moveUp(.31f);

                //Make dolphin red
                Texture dolphin2T = eng.getTextureManager().getAssetByPath("redDolphin.png");
                TextureState dolphin2S = (TextureState) eng.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
                dolphin2S.setTexture(dolphin2T);
                dolphin2E.setRenderState(dolphin2S);

                // Place groundplane
                ManualObject groundPlane = new GroundPlaneObject(sm, eng).makeObject("groundPlane");
                groundPlane.setPrimitive(Primitive.TRIANGLES);
                SceneNode gpNode = sm.getRootSceneNode().createChildSceneNode("groundPlaneNode");
                gpNode.attachObject(groundPlane);

                // Setup object controller(s)
                rc = new RotationController(Vector3f.createUnitVectorY(), .01f);
                sm.addController(rc);

                hc = new HeightController();
                sm.addController(hc);

                sc = new StretchController();
                sm.addController(sc);

                // Spawn planets
                SpawnPlanets sp = new SpawnPlanets();
                sp.spawnPlanets(eng, sm, rc);

                // Set up ambient light
                sm.getAmbientLight().setIntensity(new Color(.3f, .3f, .3f));

                // Set up Skybox
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

                // Create input manager
                im = new GenericInputManager();

                // Configure orbit camera controller
                setupOrbitCamera(sm);

                // Setup jump manager
                jm = new JumpManager(sm, .31f);

                //Setup follow managers
                player1FM = new FollowManager(sm, sm.getSceneNode("playerOneDolphinNode"), rc, sc);
                player2FM = new FollowManager(sm, sm.getSceneNode("playerTwoDolphinNode"), rc, hc);

                //Setup laser manager
                lm = new LaserManager(eng, player1FM, player2FM);

                //Setup charge collector
                playerOneCC = new ChargeCollector();
                playerTwoCC = new ChargeCollector();

                // Configure controller(s)
                setupInputs(sm.getCamera("playerOneCamera"), sm, eng.getRenderSystem().getRenderWindow());

                // Setup mouse controller
                mc = new MouseController(eng.getRenderSystem().getRenderWindow(), playerTwoOrbitCameraController);
                eng.getRenderSystem().getRenderWindow().addMouseMotionListener(mc);

                //Change the cursor....
                Cursor invisCursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(""), new Point(), "invisCursor");
                eng.getRenderSystem().getCanvas().setCursor(invisCursor);
        }

        protected void setupOrbitCamera(SceneManager sm) 
        {
                playerOneOrbitCameraController = new OrbitCameraController(sm.getSceneNode("playerOneCameraNode"),
                                sm.getSceneNode("playerOneDolphinNode"), im);
                playerTwoOrbitCameraController = new OrbitCameraController(sm.getSceneNode("playerTwoCameraNode"),
                                sm.getSceneNode("playerTwoDolphinNode"), im);
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
                                
                String playerTwoPos = "(" + Integer.toString(Math
                                .round(engine.getSceneManager().getSceneNode("playerTwoDolphinNode").getLocalPosition().x()))
                                + ", "
                                + Integer.toString(Math.round(engine.getSceneManager().getSceneNode("playerTwoDolphinNode")
                                .getLocalPosition().y()))
                                + ", " + Integer.toString(Math.round(engine.getSceneManager()
                                .getSceneNode("playerTwoDolphinNode").getLocalPosition().z()))
                                + ")";

                // Set huds
                rs.setHUD("Score: " + playerOneScore + "     Planet Following: " + player1FM.checkFollow() + "     Jump Charge: " + playerOneCC.getJumpCharge() + "/2000     Dolphin Position: " + playerOnePos, 15, (rs.getCanvas().getHeight() / 2) + 15);
                rs.setHUD2("Score: " + playerTwoScore + "     Planet Following: " + player2FM.checkFollow() + "     Jump Charge: " + playerTwoCC.getJumpCharge() + "/2000     Dolphin Position: " + playerTwoPos, 15, 15);

                // Update jump manager
                HashMap<SceneNode, Node> temp = jm.update(elapsTime - lastUpdateTime);

                //Check for collisions
                if (!temp.isEmpty())
                {
                        //Check whether dolphin1 collided
                        if (temp.containsKey(engine.getSceneManager().getSceneNode("playerOneDolphinNode")))
                        {
                                //Verify a node is not currently following...
                                if (!player1FM.checkFollow())
                                {
                                        Node planet = temp.get(engine.getSceneManager().getSceneNode("playerOneDolphinNode"));
        
                                        rc.removeNode(planet);
                                        player1FM.addFollow(engine.getSceneManager().getSceneNode(planet.getName()));                                       
                                }
                        }

                        //Check whether dolphin2 collided
                        if (temp.containsKey(engine.getSceneManager().getSceneNode("playerTwoDolphinNode")))
                        {
                                if (!player2FM.checkFollow())
                                {
                                        Node planet = temp.get(engine.getSceneManager().getSceneNode("playerTwoDolphinNode"));

                                        rc.removeNode(planet);
                                        player2FM.addFollow(engine.getSceneManager().getSceneNode(planet.getName()));     

                                }
                        }
                }
                
                //Check to see if the dolphins have returned to the origin with a planet in toe... if so give them a point
                Vector3f worldOrigin = (Vector3f)Vector3f.createFrom(0.0f, 0.0f, 0.0f);

                if (ObjectDistance.distanceBetweenVectors((Vector3f)engine.getSceneManager().getSceneNode("playerOneDolphinNode").getLocalPosition(), worldOrigin) <= 1.0f)
                {
                        //If their is a planet in toe
                        if (player1FM.checkFollow())
                        {
                                //Increment the score
                                playerOneScore++;

                                //Remove the node...
                                player1FM.removeNode();

                                //Change the color of the tower to the team that most recently returned a node...
                                if (tc != towerColor.GREEN && playerOneScore > playerTwoScore)
                                {
                                        TextureState greenState = (TextureState) engine.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
                                        greenState.setTexture(greenTexture);
                                        engine.getSceneManager().getEntity("tower").setRenderState(greenState);
                                        tc = towerColor.GREEN;
                                }
                        }
                }

                if (ObjectDistance.distanceBetweenVectors((Vector3f)engine.getSceneManager().getSceneNode("playerTwoDolphinNode").getLocalPosition(), worldOrigin) <= 1.0f)
                {
                        //If their is a planet in toe
                        if (player2FM.checkFollow())
                        {
                                //Increment the score
                                playerTwoScore++;

                                //Remove the node...
                                player2FM.removeNode();

                                //Change the color of the tower to the team that most recently returned a node...
                                if (tc != towerColor.RED && playerTwoScore > playerOneScore)
                                {
                                        TextureState redState = (TextureState) engine.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
                                        redState.setTexture(redTexture);
                                        engine.getSceneManager().getEntity("tower").setRenderState(redState);
                                        tc = towerColor.RED;
                                }
                        }
                }

                //If their is a tie... set the tower's color to orange
                if (tc != towerColor.ORANGE && playerOneScore == playerTwoScore)
                {       
                        TextureState orangeState = (TextureState) engine.getRenderSystem().createRenderState(RenderState.Type.TEXTURE);
                        orangeState.setTexture(orangeTexture);
                        engine.getSceneManager().getEntity("tower").setRenderState(orangeState);
                        tc = towerColor.ORANGE;
                }

                //Update the laser manager
                lm.update(elapsTime - lastUpdateTime);

                // Process inputs
                im.update(elapsTime - lastUpdateTime);

                // Update orbit camera controllers
                playerOneOrbitCameraController.updateCameraPosition();
                playerTwoOrbitCameraController.updateCameraPosition();

                // Record last update in MS
                lastUpdateTime = elapsTime;
        }

        protected void setupInputs(Camera camera, SceneManager sm, RenderWindow rw) 
        {
                List<Controller> controllerList = im.getControllers();

                //Setup actions
                moveYawAction = new MoveYawAction(playerOneOrbitCameraController, sm.getSceneNode("playerOneDolphinNode"));
                moveRightAction = new MoveRightAction(sm.getSceneNode("playerOneDolphinNode"));
                moveFwdAction = new MoveFwdAction(sm.getSceneNode("playerOneDolphinNode"));
                playerOneJumpAction = new JumpAction(sm.getSceneNode("playerOneDolphinNode"), jm, playerOneCC);
                playerTwoJumpAction = new JumpAction(sm.getSceneNode("playerTwoDolphinNode"), jm, playerTwoCC);
                playerOneLaserAction = new LaserAction(sm.getSceneNode("playerOneDolphinNode"), lm);
                playerTwoLaserAction = new LaserAction(sm.getSceneNode("playerTwoDolphinNode"), lm);
                moveFwdKeyboardAction = new MoveFwdKeyboardAction(sm.getSceneNode("playerTwoDolphinNode"));
                moveRightKeyboardAction = new MoveRightKeyboardAction(sm.getSceneNode("playerTwoDolphinNode"));
                moveLeftKeyboardAction = new MoveLeftKeyboardAction(sm.getSceneNode("playerTwoDolphinNode"));
                moveBwdKeyboardAction = new MoveBwdKeyboardAction(sm.getSceneNode("playerTwoDolphinNode"));
                moveYawKeyboardAction = new MoveYawKeyboardAction(playerTwoOrbitCameraController, sm.getSceneNode("playerTwoDolphinNode"));
                mouseRadiusAction = new MoveMouseRadiusAction(playerTwoOrbitCameraController);

                // Iterate over all input devices
                for (int index = 0; index < controllerList.size(); index++) 
                {
                        // NOTE: This code also deals with no gamepads as it would not attempt to attach
                        // any gamepad controls unless it see's an item of Type.GAMEPAD

                        // If keyboard, attach inputs...
                        if (controllerList.get(index).getType() == Controller.Type.KEYBOARD) 
                        {
                                im.associateAction(controllerList.get(index),
                                        net.java.games.input.Component.Identifier.Key.W, moveFwdKeyboardAction,
                                        InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                                im.associateAction(controllerList.get(index),
                                        net.java.games.input.Component.Identifier.Key.S, moveBwdKeyboardAction,
                                        InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                                im.associateAction(controllerList.get(index),
                                        net.java.games.input.Component.Identifier.Key.A, moveLeftKeyboardAction,
                                        InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                                im.associateAction(controllerList.get(index),
                                        net.java.games.input.Component.Identifier.Key.D, moveRightKeyboardAction,
                                        InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                                im.associateAction(controllerList.get(index),
                                        net.java.games.input.Component.Identifier.Key.Q, moveYawKeyboardAction,
                                        InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                                im.associateAction(controllerList.get(index),
                                        net.java.games.input.Component.Identifier.Key.E, moveYawKeyboardAction,
                                        InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                                im.associateAction(controllerList.get(index),
                                        net.java.games.input.Component.Identifier.Key.SPACE, playerTwoJumpAction,
                                        InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
                        }

                        // If gamepad, attach inputs...
                        else if (controllerList.get(index).getType() == Controller.Type.GAMEPAD) 
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
                                                net.java.games.input.Component.Identifier.Button._0, playerOneJumpAction,
                                                InputManager.INPUT_ACTION_TYPE.ON_PRESS_AND_RELEASE);
                                im.associateAction(controllerList.get(index),
                                                net.java.games.input.Component.Identifier.Button._2, playerOneLaserAction,
                                                InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
                                

                                //Setup orbit camera controller inputs
                                playerOneOrbitCameraController.setupInputs(im, controllerList.get(index));

                        }

                        //If mouse, attach inputs...
                        else if (controllerList.get(index).getType() == Controller.Type.MOUSE)
                        {
                                im.associateAction(controllerList.get(index),
                                                net.java.games.input.Component.Identifier.Axis.Z, mouseRadiusAction,
                                                InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
                                im.associateAction(controllerList.get(index),
                                                net.java.games.input.Component.Identifier.Button.LEFT, playerTwoLaserAction,
                                                InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
                        }
                }
        }
}
