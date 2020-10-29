package myServer;

import java.io.IOException;

//Creates a new network server based on passed parameters...
public class NetworkingServer 
{
    public NetworkingServer(int serverPort)
    {
        try
        {
            new GameServer(serverPort);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        System.out.println("Game Server Running...");
        if (args.length > 0)
        {
            System.out.println(args[0]);
            new NetworkingServer(Integer.parseInt(args[0]));
        }
    }    
}
