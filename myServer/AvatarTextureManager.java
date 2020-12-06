package myServer;

import java.util.ArrayList;
import java.util.Random;

public class AvatarTextureManager 
{
    private ArrayList<AvatarTexture> colors = new ArrayList<>();

    public AvatarTextureManager()
    {
        colors.add(new AvatarTexture("greenPlayer.png", "Green"));
        colors.add(new AvatarTexture("redPlayer.png", "Red"));
        colors.add(new AvatarTexture("purplePlayer.png", "Purple"));
        colors.add(new AvatarTexture("brownPlayer.png", "Brown"));
        colors.add(new AvatarTexture("pinkPlayer.png", "Pink"));
        colors.add(new AvatarTexture("orangePlayer.png", "Orange"));
        colors.add(new AvatarTexture("yellowPlayer.png", "Yellow"));
        colors.add(new AvatarTexture("bluePlayer.png", "Blue"));       
    }

    public AvatarTexture getRandomUnusedTexture()
    {
        Random rand = new Random();
        int index;

        while(true)
        {
            index = rand.nextInt(colors.size());

            if (!colors.get(index).textureUsed)
            {
                colors.get(index).textureUsed = true;
                return colors.get(index);
            }
        }
    }

    public void reclaimTexture(String texName)
    {
        for (AvatarTexture av : colors)
        {
            if (av.textureName == texName)
            {
                av.textureUsed = false;
                break;
            }
        }
    }   
}
