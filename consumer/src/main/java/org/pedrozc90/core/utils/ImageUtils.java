package org.pedrozc90.core.utils;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import org.pedrozc90.adapters.messages.ResizeStrategy;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

@ApplicationScoped
public class ImageUtils {

    private static final Logger logger = Logger.getLogger(ImageUtils.class);

    public byte[] resize(final ResizeStrategy strategy,
                         final byte[] bytes,
                         final int targetWidth,
                         final int targetHeight) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes)) {
            // Read the original image
            final BufferedImage originalImage = ImageIO.read(bis);
            if (originalImage == null) {
                throw new IOException("Failed to read image data");
            }

            // Preserve original image type if possible
            int type = originalImage.getType();
            if (type == BufferedImage.TYPE_CUSTOM) {
                type = BufferedImage.TYPE_INT_ARGB;  // Support transparency
            }

            final int sourceWidth = originalImage.getWidth();
            final int sourceHeight = originalImage.getHeight();

            // Calculate dimensions while maintaining aspect ratio
            final int[] dimensions = strategy.calculate(sourceWidth, sourceHeight, targetWidth, targetHeight);

            // Create new BufferedImage with desired dimensions
            final BufferedImage resizedImage = new BufferedImage(dimensions[0], dimensions[1], type);

            // Get graphics and set rendering hints for better quality
            Graphics2D g2d = resizedImage.createGraphics();
            try {
                // Fill with white background if no alpha
                if (type == BufferedImage.TYPE_INT_RGB) {
                    g2d.setBackground(Color.WHITE);
                    g2d.clearRect(0, 0, dimensions[0], dimensions[1]);
                }

                setRenderingHints(g2d);

                // Draw the image
                g2d.drawImage(originalImage, 0, 0, dimensions[0], dimensions[1], null);
            } finally {
                g2d.dispose(); // Ensure graphics context is always disposed
            }

            // Convert back to byte array
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
                final String format = getImageFormat(bytes);
                ImageIO.write(resizedImage, (format != null) ? format : "jpg", bos);
                return bos.toByteArray();
            }
        }
    }

    private void setRenderingHints(Graphics2D g2d) {
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    }

    private int[] calculateDimensions(int originalWidth, int originalHeight, int targetWidth, int targetHeight) {
        double aspectRatio = (double) originalWidth / originalHeight;
        int newWidth;
        int newHeight;

        if (targetWidth == 0 && targetHeight == 0) {
            return new int[]{ originalWidth, originalHeight };
        }

        if (targetWidth == 0) {
            // Calculate width based on target height
            newHeight = targetHeight;
            newWidth = (int) (targetHeight * aspectRatio);
        } else if (targetHeight == 0) {
            // Calculate height based on target width
            newWidth = targetWidth;
            newHeight = (int) (targetWidth / aspectRatio);
        } else {
            // Both dimensions specified
            double targetAspectRatio = (double) targetWidth / targetHeight;
            if (aspectRatio > targetAspectRatio) {
                newWidth = targetWidth;
                newHeight = (int) (targetWidth / aspectRatio);
            } else {
                newHeight = targetHeight;
                newWidth = (int) (targetHeight * aspectRatio);
            }
        }

        return new int[]{ newWidth, newHeight };
    }

    private String getImageFormat(byte[] imageData) {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageData)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(ImageIO.createImageInputStream(bis));
            if (readers.hasNext()) {
                return readers.next().getFormatName().toLowerCase();
            }
        } catch (IOException e) {
            logger.warn("Could not determine image format", e);
        }
        return null;
    }


//    /**
//     * Get image dimensions without fully reading it into memory
//     */
//    public Dimension getImageDimension(byte[] imageData) throws IOException {
//        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageData)) {
//            BufferedImage bimg = ImageIO.read(bis);
//            return new Dimension(bimg.getWidth(), bimg.getHeight());
//        }
//    }
//
//    /**
//     * Get image format (extension)
//     */
//    public String getImageFormat(byte[] imageData) throws IOException {
//        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageData)) {
//            String format = ImageIO.getImageReaders(ImageIO.createImageInputStream(bis))
//                .next()
//                .getFormatName()
//                .toLowerCase();
//            return format;
//        }
//    }
}