import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.HashMap;

public class Testing {
    String absolutePath = System.getProperty("user.dir");

    @Test
    public void posCompTest(){
        String pathToPos = absolutePath + "/clonedRepo/simpleprojects/PosCompPosTest";
        try{
            HashMap<String, String> info = Features.compileRepo(new HashMap<String, String>(), pathToPos);
            assertTrue(info.get("Status").equals("Success"));
        }
        catch(IOException e){
            System.out.println("IOException in posCompTest");
        }
    }
    @Test
    public void negCompTest(){
        String pathToPos = absolutePath + "/clonedRepo/simpleprojects/NegComp";
        try{
            HashMap<String, String> info = Features.compileRepo(new HashMap<String, String>(), pathToPos);
            assertTrue(info.get("Status").equals("Error"));
        }
        catch(IOException e){
            System.out.println("IOException in negCompTest");
        }
    }

    @Test
    public void posTest(){
        String pathToPos = absolutePath + "/clonedRepo/simpleprojects/PosCompPosTest";
        System.out.println("Running posTest, path: " + pathToPos);
        try{
            boolean test = Features.testRepo(pathToPos);
            assertTrue(test);
        }
        catch(IOException e){
            System.out.println("IOException in posTest");
        }
    }

    @Test
    public void negTest(){
        String pathToNeg = absolutePath + "/clonedRepo/simpleprojects/PosCompNegTest";
        System.out.println("Running negTest, path: " + pathToNeg);
        try{
            boolean test = Features.testRepo(pathToNeg);
            assertFalse(test);
        }
        catch(IOException e){
            System.out.println("IOException in negTest");
        }
    }
}
