package lesson3_6.online;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;

import java.util.ArrayList;
import java.util.stream.Stream;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @ParameterizedTest
    @MethodSource("examination")
    void knifeTest(int[] original, int [] altered) {
        Assertions.assertArrayEquals(altered, Main.knife(original));
    }

     static Stream<Arguments> examination() {
        List<Arguments> list = new ArrayList<>();
        list.add(Arguments.arguments(new int[] {8,0,9,4,2,2,8}, new int[] {2,2,8}));
        list.add(Arguments.arguments(new int[] {6,5,8,4,6,8,5,8,6}, new int[] {6,8,5,8,6}));
        list.add(Arguments.arguments(new int[] {4,4,7,4,8,6,8,6,4}, new int[] {}));
        return list.stream();
    }

    @Test
    void error() {
        Assertions.assertThrows(RuntimeException.class, () -> Main.knife(new int[] {7,8,3,6,2}));
    }

    @Test
    void checkArrTest() {
        Assertions.assertTrue(Main.checkArr(new int[] {4,4,4,1,1}));
        Assertions.assertFalse(Main.checkArr(new int[] {1,1,1,1,1}));
        Assertions.assertFalse(Main.checkArr(new int[] {4,4,4,4,4}));
        Assertions.assertFalse(Main.checkArr(new int[] {1,1,8,4,4,4,4}));
    }
}