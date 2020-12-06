    package myServer;   
    
    //Private class holds texture information
    public class AvatarTexture
    {
        protected String textureName;
        protected String name;
        protected boolean textureUsed;

        public AvatarTexture(String textureName, String name)
        {
            this.textureName = textureName;
            this.name = name;
            textureUsed = false;
        }
    } 