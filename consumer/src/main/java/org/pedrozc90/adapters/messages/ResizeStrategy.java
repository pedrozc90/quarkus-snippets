package org.pedrozc90.adapters.messages;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum ResizeStrategy {

    FIXED_DIMENSIONS {
        @Override
        public int[] calculate(int srcWidth, int srcHeight, int targetWidth, int targetHeight) {
            return new int[]{ targetWidth, targetHeight };
        }
    },
    MAINTAIN_ASPECT_RATIO {
        @Override
        public int[] calculate(int srcWidth, int srcHeight, int targetWidth, int targetHeight) {
            double ratio = Math.min(
                (double) targetWidth / srcWidth,
                (double) targetHeight / srcHeight
            );

            return new int[]{
                (int) Math.round(srcWidth * ratio),
                (int) Math.round(srcHeight * ratio)
            };
        }
    },
    FIT_TO_BOX {
        @Override
        public int[] calculate(int srcWidth, int srcHeight, int targetWidth, int targetHeight) {
            double srcRatio = (double) srcWidth / srcHeight;
            double targetRatio = (double) targetWidth / targetHeight;

            if (srcRatio > targetRatio) {
                // Source is wider - fit to width
                return new int[]{
                    targetWidth,
                    (int) Math.round(targetWidth / srcRatio)
                };
            }

            // Source is taller - fit to height
            return new int[]{
                (int) Math.round(targetHeight * srcRatio),
                targetHeight
            };
        }
    };

    public abstract int[] calculate(int srcWidth, int srcHeight, int targetWidth, int targetHeight);

}
