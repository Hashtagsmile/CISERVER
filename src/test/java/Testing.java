import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class Testing {

    @Test
    public void posTest(){
        try{
            boolean test = Features.testRepo("./simpleprojects/PosCompPosTest");
            assertTrue(test);
        }
        catch(IOException e){
            System.out.println("IOException in posTest");
        }
    }

    @Test
    public void negTest(){
        try{
            boolean test = Features.testRepo("./simpleprojects/PosCompNegTest");
            assertFalse(test);
        }
        catch(IOException e){
            System.out.println("IOException in negTest");
        }
    }
}
