package myGameEngine;

import java.io.File;
import java.io.FileReader;
import java.util.HashMap;

import javax.script.ScriptEngine;
import jdk.nashorn.api.scripting.NashornScriptEngineFactory;

//Suppresses removal warning for Nashorn
@SuppressWarnings("removal")
public class ScriptManager 
{
    private ScriptEngine engine;
    private HashMap<String, Long> scriptMod;

    public ScriptManager()
    {
        //Creating a Nashorn script engine... Hiding the deprecation warning
        NashornScriptEngineFactory factory = new NashornScriptEngineFactory();
        engine = factory.getScriptEngine();

        //Setup the Hashmap
        scriptMod = new HashMap<>();
    }
    
    public void loadScript(String fileName)
    {
        try
        {
            FileReader myScript = new FileReader("./scripts/" + fileName);
            engine.eval(myScript);
            myScript.close();

            //Record the time the file was modified last
            File fp = new File("./scripts/" + fileName);
            scriptMod.put(fileName, fp.lastModified());
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }

    //Puts a variable in the engine.
    public void putObjectInEngine(String name, Object obj)
    {
        this.engine.put(name, obj);
    }

    //Returns a generic object. You will need to cast to what you expect
    public Object getValue(String variable)
    {
        //Return requested value
        return engine.get(variable);
    }

    //Updates a script and returns true if an update occured, else false is returned.
    public boolean scriptUpdate(String fileName)
    {
        try
        {
            File fp = new File("./scripts/" + fileName);

            //If its been modified re-read the file
            if (scriptMod.get(fileName) < fp.lastModified())
            {
                FileReader myScript = new FileReader("./scripts/" + fileName);
                engine.eval(myScript);
                myScript.close();

                //Record the new modification time
                scriptMod.replace(fileName, fp.lastModified());
                return true;
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }

        //No update... return false
        return false;
    }    
}
