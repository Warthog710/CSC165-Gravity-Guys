package myGameEngine;

import net.java.games.input.Controller;
import java.awt.*;
import java.awt.event.*;

import ray.rage.Engine;
import ray.rage.rendersystem.*;
import ray.input.InputManager;
import ray.input.action.AbstractInputAction;
import ray.input.action.Action;
import ray.rage.scene.*;
import ray.rml.*;

public class OrbitCameraController {
    private SceneNode cameraN;
    private SceneNode target;
    private float azimuth;
    private float elevation;
    private float radius;
    private float currentPitch;
    private boolean isPitched;
    private ScriptManager scriptMan;
    private MouseController mc;

    private Action orbitAroundAction, orbitElevationAction, orbitRadiusAction, mouseOrbitRadiusAction;

    public OrbitCameraController(Engine eng, SceneNode cameraN, SceneNode target, InputManager im, ScriptManager scriptMan) 
    {
        // Grab the script manager
        this.scriptMan = scriptMan;

        // Set initial values
        this.cameraN = cameraN;
        this.target = target;
        this.azimuth = Float.parseFloat(this.scriptMan.getValue("orbitStartingAzimuth").toString());
        this.elevation = Float.parseFloat(this.scriptMan.getValue("orbitStartingElevation").toString());
        this.radius = Float.parseFloat(this.scriptMan.getValue("orbitStartingRadius").toString());
        this.currentPitch = 0.0f;
        this.isPitched = false;

        //Setup mouse
        this.mc = new MouseController(eng.getRenderSystem().getRenderWindow());
        eng.getRenderSystem().getRenderWindow().addMouseMotionListener(mc);

        //Make the cursor invisible
        Cursor invisCursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(""), new Point(), "invisCursor");
        eng.getRenderSystem().getCanvas().setCursor(invisCursor);

        updateCameraPosition();
    }

    public void updateCameraPosition() 
    {
        double theta = Math.toRadians(azimuth);
        double phi = Math.toRadians(elevation);
        double x = radius * Math.cos(phi) * Math.sin(theta);
        double y = radius * Math.sin(phi);
        double z = radius * Math.cos(phi) * Math.cos(theta);
        cameraN.setLocalPosition(Vector3f.createFrom((float) x, (float) y, (float) z)
                .add(target.getWorldPosition().add(0.0f, 1.0f, 0.0f)));
        cameraN.lookAt(target.getWorldPosition().add(0.0f, 1.0f, 0.0f), Vector3f.createFrom(0.0f, 1.0f, 0.0f));

        //If the camera is pitched, pitch it...
        cameraN.pitch(Degreef.createFrom(-currentPitch));

    }

    public void updateAzimoth(float value) 
    {
        azimuth += value;
        azimuth = azimuth % 360;
        updateCameraPosition();
    }

    public void setupInputs(InputManager im, Controller controller) 
    {
        orbitAroundAction = new OrbitAroundAction();
        orbitElevationAction = new OrbitElevationAction();
        orbitRadiusAction = new OrbitRadiusAction();
        mouseOrbitRadiusAction = new MouseOrbitRadiusAction();

        // If the controller passed is a gamepad
        if (controller.getType() == Controller.Type.GAMEPAD) 
        {
            if (controller.getName().contains("Wireless Controller")) 
            {
                im.associateAction(controller, net.java.games.input.Component.Identifier.Button._4, orbitAroundAction,
                        InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                im.associateAction(controller, net.java.games.input.Component.Identifier.Button._5, orbitAroundAction,
                        InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                im.associateAction(controller, net.java.games.input.Component.Identifier.Axis.RZ, orbitElevationAction,
                        InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                im.associateAction(controller, net.java.games.input.Component.Identifier.Axis.POV, orbitRadiusAction,
                        InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            } 
            else 
            {
                im.associateAction(controller, net.java.games.input.Component.Identifier.Axis.RX, orbitAroundAction,
                        InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                im.associateAction(controller, net.java.games.input.Component.Identifier.Axis.RY, orbitElevationAction,
                        InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
                im.associateAction(controller, net.java.games.input.Component.Identifier.Axis.POV, orbitRadiusAction,
                        InputManager.INPUT_ACTION_TYPE.REPEAT_WHILE_DOWN);
            }
        }

        // If the controller is a mouse, setup the mouse wheel to control the radius
        if (controller.getType() == Controller.Type.MOUSE) {
            im.associateAction(controller, net.java.games.input.Component.Identifier.Axis.Z, mouseOrbitRadiusAction,
                    InputManager.INPUT_ACTION_TYPE.ON_PRESS_ONLY);
        }

    }

    private class OrbitAroundAction extends AbstractInputAction {
        private float azimuthSpeed;

        public OrbitAroundAction() {
            azimuthSpeed = Float.parseFloat(scriptMan.getValue("cameraAzimuthSpeed").toString());
        }

        public void performAction(float time, net.java.games.input.Event evt) {
            float rotateAmount = 0.0f;

            float keyValue = evt.getValue();
            // Deadzone
            if (keyValue > -.2 && keyValue < .2)
                return;
            if (evt.getComponent().getIdentifier() == net.java.games.input.Component.Identifier.Key.J
                    || evt.getComponent().getIdentifier() == net.java.games.input.Component.Identifier.Button._5) {
                keyValue = -keyValue;
            }

            // Updates azimuth speed, if a script update occured
            if (scriptMan.scriptUpdate("movementInfo.js"))
                azimuthSpeed = Float.parseFloat(scriptMan.getValue("cameraAzimuthSpeed").toString());

            rotateAmount = keyValue * azimuthSpeed * time;

            azimuth += rotateAmount;
            azimuth = azimuth % 360;
            updateCameraPosition();
        }
    }

    private class OrbitElevationAction extends AbstractInputAction {
        private float elevationSpeed;

        public OrbitElevationAction() {
            elevationSpeed = Float.parseFloat(scriptMan.getValue("cameraElevationSpeed").toString());
        }

        public void performAction(float time, net.java.games.input.Event evt) {
            float keyValue = evt.getValue();
            // Deadzone
            if (keyValue > -.2 && keyValue < .2)
                return;
            if (evt.getComponent().getIdentifier() == net.java.games.input.Component.Identifier.Key.UP) {
                keyValue = -keyValue;
            }

            // Updates elevation speed, if a script update occured
            if (scriptMan.scriptUpdate("movementInfo.js"))
                elevationSpeed = Float.parseFloat(scriptMan.getValue("cameraElevationSpeed").toString());

            // If camera is not pitched, move the elevation
            if (!isPitched) {
                float elevationAmount = keyValue * elevationSpeed * time;

                // If move would make the dolphin go above 90 degrees, don't make the move
                if ((elevation - elevationAmount) > 90) {
                    return;
                }

                // If the camera would hit the ground plane, set it to the pitch state
                if ((elevation - elevationAmount) < -1) {
                    elevationAmount = 0;
                    isPitched = true;

                    // Set initial pitch of 1.1 degree
                    currentPitch = 1.1f;
                }

                elevation -= elevationAmount;
            }
            // Else the camera must be pitched... adjust that
            else {
                float pitchAmount = -keyValue * elevationSpeed * time;

                // If pitch would be less than 1 degree reset pitch state and pitch
                if ((currentPitch - pitchAmount) < 1) {
                    currentPitch = 0;
                    isPitched = false;
                    return;
                }

                // If the pitch owuld be greater than 90... don't do it
                if ((currentPitch - pitchAmount) > 90) {
                    currentPitch = 90;
                    return;
                }

                // Make the move
                currentPitch -= pitchAmount;
            }
            updateCameraPosition();
        }
    }

    private class OrbitRadiusAction extends AbstractInputAction {
        private float ZOOM_MAX;
        private float ZOOM_MIN;
        private float radiusSpeed;

        public OrbitRadiusAction() {
            // Floats for zoom max and zoom min. The camera will never go beyond these
            ZOOM_MAX = Float.parseFloat(scriptMan.getValue("zoomMax").toString());
            ZOOM_MIN = Float.parseFloat(scriptMan.getValue("zoomMin").toString());
            radiusSpeed = Float.parseFloat(scriptMan.getValue("cameraRadiusSpeed").toString());
        }

        public void performAction(float time, net.java.games.input.Event evt) {
            float radiusAmount = 0.0f;

            // Updates radius speed & min/max if an update occured
            if (scriptMan.scriptUpdate("movementInfo.js")) {
                ZOOM_MAX = Float.parseFloat(scriptMan.getValue("zoomMax").toString());
                ZOOM_MIN = Float.parseFloat(scriptMan.getValue("zoomMin").toString());
                radiusSpeed = Float.parseFloat(scriptMan.getValue("cameraRadiusSpeed").toString());
            }

            // POV hat forward button or keyboard key is pressed
            if (evt.getValue() == .25
                    || evt.getComponent().getIdentifier() == net.java.games.input.Component.Identifier.Key.I) {
                radiusAmount = -radiusSpeed * time;
            }

            // POV hat backward button or keyboard key is pressed
            else if (evt.getValue() == .75
                    || evt.getComponent().getIdentifier() == net.java.games.input.Component.Identifier.Key.K) {
                radiusAmount = radiusSpeed * time;
            }

            // If zoom would be beyond less than zero or greater than max... don't do it
            if ((radius + radiusAmount) < ZOOM_MIN || (radius + radiusAmount) > ZOOM_MAX) {
                return;
            }

            radius += radiusAmount;
            updateCameraPosition();
        }
    }

    private class MouseOrbitRadiusAction extends AbstractInputAction 
    {
        private float ZOOM_MAX, ZOOM_MIN, radiusSpeed;

        public MouseOrbitRadiusAction() 
        {
            // Floats for zoom max and zoom min. The camera will never go beyond these
            ZOOM_MAX = Float.parseFloat(scriptMan.getValue("zoomMax").toString());
            ZOOM_MIN = Float.parseFloat(scriptMan.getValue("zoomMin").toString());
            radiusSpeed = Float.parseFloat(scriptMan.getValue("cameraRadiusSpeed").toString());
        }

        @Override
        public void performAction(float time, net.java.games.input.Event evt) 
        {
            // Updates radius speed & min/max if an update occured
            if (scriptMan.scriptUpdate("movementInfo.js")) 
            {
                ZOOM_MAX = Float.parseFloat(scriptMan.getValue("zoomMax").toString());
                ZOOM_MIN = Float.parseFloat(scriptMan.getValue("zoomMin").toString());
                radiusSpeed = Float.parseFloat(scriptMan.getValue("cameraRadiusSpeed").toString());
            }

            float radiusAmount = -evt.getValue() * (radiusSpeed * 2) * time;

            // If zoom would be beyond less than zero or greater than max... don't do it
            if ((radius + radiusAmount) < ZOOM_MIN || (radius + radiusAmount) > ZOOM_MAX) 
            {
                return;
            }

            radius += radiusAmount;
            updateCameraPosition();           
        }        
    } 
    
    private class MouseController implements MouseMotionListener
    {
        private Robot robot;
        private float prevMouseX, prevMouseY, curMouseX, curMouseY;
        private int centerX, centerY;
        private boolean isRecentering;

        private RenderWindow rw;
        private Viewport vp;

        public MouseController(RenderWindow renWin)
        {
            this.rw = renWin;
            this.vp = rw.getViewport(0);

            int left = rw.getLocationLeft();
            int top = rw.getLocationTop();
            int width = vp.getActualScissorWidth();
            int height = vp.getActualScissorHeight();

            centerX = left + (width/2);
            centerY = top + (height/2);

            isRecentering = false;

            //Note that some platforms may not support the Robot class
            try
            {
                robot = new Robot();  
            } 
            catch (AWTException ex) 
            { 
                throw new RuntimeException("Couldn't create Robot!"); 
            }
            
            recenterMouse();
            prevMouseX = centerX;
            prevMouseY = centerY;
        }

        private void recenterMouse()
        {
            int left = rw.getLocationLeft();
            int top = rw.getLocationTop();
            int width = vp.getActualScissorWidth();
            int height = vp.getActualScissorHeight();

            centerX = left + (width/2);
            centerY = top + (height/2);
            isRecentering = true;
            robot.mouseMove(centerX, centerY);
        }

        @Override
        public void mouseMoved(MouseEvent e) 
        {
            // if robot is recentering and the MouseEvent location is in the center,
            // then this event was generated by the robot    
            if (isRecentering && centerX == e.getXOnScreen() && centerY == e.getYOnScreen())    
            { 
                isRecentering = false; 
            } 
            else    
            {  
                //Event was due to a user mouse-move, and must be processed      
                curMouseX = e.getXOnScreen();      
                curMouseY = e.getYOnScreen();      
                float mouseDeltaX = prevMouseX - curMouseX;      
                float mouseDeltaY = prevMouseY - curMouseY;      
                yaw(mouseDeltaX);      
                pitch(mouseDeltaY);      
                prevMouseX = curMouseX;      
                prevMouseY = curMouseY;

                // Recenter the mouse  
                recenterMouse();      
                prevMouseX = centerX; 
                prevMouseY = centerY;  
            }
        }

        private void yaw(float value)
        {
            azimuth += -value * .072f;
            azimuth = azimuth % 360;
            updateCameraPosition();
        }

        private void pitch(float value)
        {
            //If camera is not pitched, move the elevation
            if (!isPitched)
            {
                float elevationAmount = -value *.072f;

                //If move would make the dolphin go above 90 degrees, don't make the move
                if ((elevation - elevationAmount) > 90)
                {
                    return;
                }

                //If the camera would hit the ground plane, set it to the pitch state
                if ((elevation - elevationAmount) < -5)
                {
                    elevationAmount = 0;
                    isPitched = true;

                    //Set initial pitch of 1.1 degree
                    currentPitch = 1.1f;
                }

                elevation -= elevationAmount;
            }
            //Else the camera must be pitched... adjust that
            else
            {
                float pitchAmount = value * .072f;

                //If pitch would be less than 1 degree reset pitch state and pitch
                if ((currentPitch - pitchAmount) < 1)
                {
                    currentPitch = 0;
                    isPitched = false;
                    return;
                }

                //If the pitch owuld be greater than 90... don't do it
                if ((currentPitch - pitchAmount) > 90)
                {
                    currentPitch = 90;
                    return;
                }

                //Make the move
                currentPitch -= pitchAmount;              
            }
            updateCameraPosition();
        }

        @Override
        public void mouseDragged(MouseEvent e) 
        {
            //Do  nothing...
        }
        
    }
}
