package myGameEngine;

//Simple class to collect jump charges so they can be displayed on the HUDs
public class ChargeCollector 
{
    private long timePressed = 0;

    //Getter
    public long getJumpCharge() 
    {
        if (timePressed == 0)
            return 0;
        else if ((System.currentTimeMillis() - timePressed) > 2000)
            return 2000;
        else
            return System.currentTimeMillis() - timePressed; 
    }

    //Set time pressed
    public void setTimePressed(long tp) { this.timePressed = tp; }

    //Reset
    public void reset() { timePressed = 0; }
    
}
