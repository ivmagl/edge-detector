package xyz.imahlatii.edgedetector.algorithm;

import xyz.imahlatii.edgedetector.utils.ImageUtils;

import java.awt.image.BufferedImage;

public class CannyAlgorithm {
    private static final int GAUSSIAN_RADIUS = 7;
    private static final double GAUSSIAN_INTENSITY = 1.5;
    
    private static int standardDeviation;
    private static int meanOfMagnitude;
    private static int numberOfDeviation;
    private static double hysteresisHighThreshold;
    private static double hysteresisLowThreshold;
    private static double thresholdFraction;
    private static int[][] gradientDirectionMask;
    private static int[][] horizontalMask;
    private static int[][] verticalMask;
    private static double[][] directionMask;

    public static BufferedImage cannyEdges(BufferedImage image, int numberOfDeviations, double fraction) {
        int[][] raw;
        int[][] blurred;
        BufferedImage edges = null;
        numberOfDeviation = numberOfDeviations;
        thresholdFraction = fraction;

        if (image != null && numberOfDeviations > 0 && fraction > 0) {
            raw = ImageUtils.getGrayscaleArray(image);
            blurred = GaussianFilter.blurGrayscaleArray(raw, GAUSSIAN_RADIUS, GAUSSIAN_INTENSITY);
            horizontalMask = SobelConverter.getHorizontalMask(blurred);
            verticalMask = SobelConverter.getVerticalMask(blurred);

            calculateMagnitude();
            calculateDirection();
            calculateSuppression();

            edges = ImageUtils.getGrayscaleImage(calculateHysteresis());
        }
        
        return edges;
    }

    private static void calculateMagnitude() {
        double sum = 0;
        double var = 0;
        int height = horizontalMask.length;
        int width = horizontalMask[0].length;
        double pixelTotal = height * width;
        directionMask = new double[height][width];
        
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                directionMask[r][c] = Math.sqrt(horizontalMask[r][c] * horizontalMask[r][c] + verticalMask[r][c] *
                        verticalMask[r][c]);
                
                sum += directionMask[r][c];
            }
        }
        
        meanOfMagnitude = (int) Math.round(sum / pixelTotal);

        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                double diff = directionMask[r][c] - meanOfMagnitude;
                
                var += (diff * diff);
            }
        }
        
        standardDeviation = (int) Math.sqrt(var / pixelTotal);
    }

    private static void calculateDirection() {
        int height = horizontalMask.length;
        int width = horizontalMask[0].length;
        double piRadius = 180 / Math.PI;
        gradientDirectionMask = new int[height][width];
        
        for (int r = 0; r < height; r++) {
            for (int c = 0; c < width; c++) {
                double angle = Math.atan2(verticalMask[r][c], horizontalMask[r][c]) * piRadius;

                if (angle < 0) {
                    angle += 360.;
                }

                if (angle <= 22.5 || (angle >= 157.5 && angle <= 202.5) || angle >= 337.5) {
                    gradientDirectionMask[r][c] = 0;
                } else if ((angle >= 22.5 && angle <= 67.5) || (angle >= 202.5 && angle <= 247.5)) {
                    gradientDirectionMask[r][c] = 45;
                } else if ((angle >= 67.5 && angle <= 112.5) || (angle >= 247.5 && angle <= 292.5)) {
                    gradientDirectionMask[r][c] = 90;
                } else {
                    gradientDirectionMask[r][c] = 135;
                }
            }
        }
    }

    private static void calculateSuppression() {
        int height = directionMask.length - 1;
        int width = directionMask[0].length - 1;
        
        for (int r = 1; r < height; r++) {
            for (int c = 1; c < width; c++) {
                double magnitude = directionMask[r][c];
                
                switch (gradientDirectionMask[r][c]) {
                    case 0 :
                        if (magnitude < directionMask[r][c - 1] && magnitude < directionMask[r][c + 1]) {
                            directionMask[r - 1][c - 1] = 0;
                        }
                        break;
                    case 45 :
                        if (magnitude < directionMask[r - 1][c + 1] && magnitude < directionMask[r + 1][c - 1]) {
                            directionMask[r - 1][c - 1] = 0;
                        }
                        break;
                    case 90 :
                        if (magnitude < directionMask[r - 1][c] && magnitude < directionMask[r + 1][c]) {
                            directionMask[r - 1][c - 1] = 0;
                        }
                        break;
                    case 135 :
                        if (magnitude < directionMask[r - 1][c - 1] && magnitude < directionMask[r + 1][c + 1]) {
                            directionMask[r - 1][c - 1] = 0;
                        }
                        break;
                }
            }
        }
    }

    private static int[][] calculateHysteresis() {
        int height = directionMask.length - 1;
        int width = directionMask[0].length - 1;
        int[][] bin = new int[height - 1][width - 1];
        
        hysteresisHighThreshold = meanOfMagnitude + (numberOfDeviation * standardDeviation);
        hysteresisLowThreshold = hysteresisHighThreshold * thresholdFraction;
        
        for (int r = 1; r < height; r++) {
            for (int c = 1; c < width; c++) {
                double magnitude = directionMask[r][c];
                
                if (magnitude >= hysteresisHighThreshold) {
                    bin[r - 1][c - 1] = 255;
                } else if (magnitude < hysteresisLowThreshold) {
                    bin[r - 1][c - 1] = 0;
                } else {
                    boolean connected = false;
                    
                    for (int nr = -1; nr < 2; nr++) {
                        for (int nc = -1; nc < 2; nc++) {
                            if (directionMask[r + nr][c + nc] >= hysteresisHighThreshold) {
                                connected = true;
                            }
                        }
                    }
                    
                    bin[r - 1][c - 1] = (connected) ? 255 : 0;
                }
            }
        }
        
        return bin;
    }
}
