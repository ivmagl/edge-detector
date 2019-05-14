package xyz.imahlatii.edgedetector.utils;

import java.awt.image.BufferedImage;

public class ImageUtils {

    public static int[][] getGrayscaleArray(BufferedImage img) {
        int[][] grayscale = null;
        int height = img.getHeight();
        int width = img.getWidth();
        
        if (height > 0 && width > 0) {
            grayscale = new int[height][width];

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int bits = img.getRGB(j, i);
                    long avg = Math.round((((bits >> 16) & 0xff) + ((bits >> 8) & 0xff) + (bits & 0xff)) / 3.0);
                    grayscale[i][j] = (int) avg;
                }
            }
        }
        
        return grayscale;
    }

    public static BufferedImage getGrayscaleImage(int[][] raw) {
        BufferedImage image = null;
        int height = raw.length;
        int width = raw[0].length;
        
        if (height > 0 && width > 0) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    image.setRGB(j, i, (raw[i][j] << 16) | (raw[i][j] << 8) | (raw[i][j]));
                }
            }
        }
        
        return image;
    }
}
