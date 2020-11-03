package myGameEngine;

//This class executes when MyGame is asked to shutdown. This insures the client says bye to the server
public class NetworkShutdownHook extends Thread
{
    NetworkedClient networkedClient;

    public NetworkShutdownHook(NetworkedClient networkedClient)
    {
        this.networkedClient = networkedClient;
    }

    //Executes a bye to the server
    public void run()
    {
        networkedClient.sendBYE();
    }    
}
