package xyz.imahlatii.edgedetector.algorithm;

public class GaussianFilter {

    private static final double SQRT2PI = Math.sqrt(2 * Math.PI);

    public static int[][] blurGrayscaleArray(int[][] raw, int rad, double intensity) {
        int height = raw.length;
        int width = raw[0].length;
        double norm = 0.;
        double intensitySquared = 2 * intensity * intensity;
        double inverseIntensityPI = 1 / (SQRT2PI * intensity);
        double[] mask = new double[2 * rad + 1];
        int[][] outGrayscale = new int[height - 2 * rad][width - 2 * rad];

        for (int x = -rad; x < rad + 1; x++) {
            double exp = Math.exp(-((x * x) / intensitySquared));
            
            mask[x + rad] = inverseIntensityPI * exp;
            norm += mask[x + rad];
        }

        for (int r = rad; r < height - rad; r++) {
            for (int c = rad; c < width - rad; c++) {
                double sum = 0.;
                
                for (int mr = -rad; mr < rad + 1; mr++) {
                    sum += (mask[mr + rad] * raw[r][c + mr]);
                }

                sum /= norm;
                outGrayscale[r - rad][c - rad] = (int) Math.round(sum);
            }
        }

        for (int r = rad; r < height - rad; r++) {
            for (int c = rad; c < width - rad; c++) {
                double sum = 0.;
                
                for(int mr = -rad; mr < rad + 1; mr++) {
                    sum += (mask[mr + rad] * raw[r + mr][c]);
                }

                sum /= norm;
                outGrayscale[r - rad][c - rad] = (int) Math.round(sum);
            }
        }
        
        return outGrayscale;
    }
}
