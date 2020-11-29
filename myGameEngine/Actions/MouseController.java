package myGameEngine.Actions;

import java.awt.*;
import java.awt.event.*;

import ray.rage.rendersystem.*;

public class MouseController implements MouseMotionListener
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
        this.vp = rw.getViewport(1);

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
        //oc.mouseAzimuthAction(-value);
    }

    private void pitch(float value)
    {
        //oc.mouseElevationAction(-value);
    }

    @Override
    public void mouseDragged(MouseEvent e) 
    {
        //Do  nothing...
    }
    
}