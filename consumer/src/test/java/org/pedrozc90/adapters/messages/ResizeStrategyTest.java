package org.pedrozc90.adapters.messages;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
public class ResizeStrategyTest {

    @Test
    public void testResizeStrategies() {
        int srcWidth = 1920;
        int srcHeight = 1080;
        int targetWidth = 800;
        int targetHeight = 600;

        // Test FIXED_DIMENSIONS
        int[] fixed = ResizeStrategy.FIXED_DIMENSIONS.calculate(srcWidth, srcHeight, targetWidth, targetHeight);
        assertEquals(targetWidth, fixed[0]);
        assertEquals(targetHeight, fixed[1]);

        // Test MAINTAIN_ASPECT_RATIO
        int[] maintained = ResizeStrategy.MAINTAIN_ASPECT_RATIO.calculate(srcWidth, srcHeight, targetWidth, targetHeight);
        assertEquals(800, maintained[0]);
        assertEquals(450, maintained[1]);

        // Test FIT_TO_BOX
        int[] fitted = ResizeStrategy.FIT_TO_BOX.calculate(srcWidth, srcHeight, targetWidth, targetHeight);
        assertEquals(800, fitted[0]);
        assertEquals(450, fitted[1]);
    }

}
