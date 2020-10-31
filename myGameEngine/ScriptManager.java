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
        engine = factory.getScriptEngine(new String [] {"--no-deprecation-warning"});

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

    //Returns a generic object. You will need to cast to the variable type you expect
    public Object getValue(String fileName, String variable)
    {
        //Check for update and return requested value
        checkForUpdate(fileName);
        return engine.get(variable);
    }

    private void checkForUpdate(String fileName)
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
            }
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }    
}
