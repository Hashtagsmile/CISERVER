import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class Testing {

    @Test
    public void posTest(){
        boolean test = testRepo("./simpleprojects/PosCompPosTest");
        assertTrue(test);
    }

    @Test
    public void negTest(){
        boolean test = testRepo("./simpleprojects/PosCompNegTest");
        assertFalse(test);
    }
}
