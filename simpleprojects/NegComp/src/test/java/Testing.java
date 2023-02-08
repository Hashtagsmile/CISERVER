import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

public class Testing {
    @Test
    public void PosTest(){
        int[] arr = new int[]{2, 4, 4, 3, 1, 7, 4, 2};
        int[] arrRes = new int[]{2, 3, -1, 1, 1, 1, -1, -1};
        assertArrayEquals(arrRes, NegComp.numbFreq(arr));
    }

    @Test
    public void NegTest(){
        int[] arr = new int[]{2, 2, 8, 3, 1, 8, 4, 2};
        int[] arrRes = new int[]{2, 3, -1, 1, 1, 1, -1, -1};
        assertFalse(Arrays.equals(NegComp.numbFreq(arr), arrRes));
    }
}