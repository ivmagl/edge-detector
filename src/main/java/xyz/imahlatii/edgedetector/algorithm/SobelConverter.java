package xyz.imahlatii.edgedetector.algorithm;

public class SobelConverter {
    private static final int[][] HORIZONTAL_MASK = { {-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1} };
    private static final int[][] VERTICAL_MASK = { {-1, -2, -1}, {0, 0, 0}, {1, 2, 1} };

    public static int[][] getHorizontalMask(int[][] raw) {
        int[][] out = null;
        int height = raw.length;
        int width = raw[0].length;
        
        if (height > 2 && width > 2) {
            out = new int[height - 2][width - 2];
        
            performSobelConvolution(height, width, raw, HORIZONTAL_MASK, out);
        }
        
        return out;
    }

    public static int[][] getVerticalMask(int[][] raw) {
        int[][] out = null;
        int height = raw.length;
        int width = raw[0].length;
        
        if (height > 2 || width > 2) {
            out = new int[height - 2][width - 2];
        
            performSobelConvolution(height, width, raw, VERTICAL_MASK, out);
        }
        
        return out;
    }

    private static void performSobelConvolution(int height, int width, int[][] raw, int[][] mask, int[][] out) {
        for (int r = 1; r < height - 1; r++) {
            for (int c = 1; c < width - 1; c++) {
                int sum = 0;

                for (int kr = -1; kr < 2; kr++) {
                    for (int kc = -1; kc < 2; kc++) {
                        sum += (mask[kr + 1][kc + 1] * raw[r + kr][c + kc]);
                    }
                }

                out[r - 1][c - 1] = sum;
            }
        }
    }
}
