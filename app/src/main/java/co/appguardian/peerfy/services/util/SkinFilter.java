package co.appguardian.peerfy.services.util;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

/**
 * Created by andresrodriguez on 20/03/18.
 */

public class SkinFilter {

    private Bitmap bitmap;
    public static final String TAG = "ScreenshotService";

    // the pixel threshold is the minimum probability to consider a pixel as skin pixel
    // the total threshold is the minimum percentage of pixels in an image to be considered as to contain enough skin
    private double pixelThreshold = 0.5;
    //public static double totalThreshold = 0.01;
    public static double totalThreshold = 0.15;
    public SkinFilter() {

    }

    public double skinDetector(Bitmap bitmap) {
        this.bitmap = bitmap;

        int orgWidth = bitmap.getWidth();
        int orgHeight = bitmap.getHeight();

        int[] pixels = new int[orgWidth * orgHeight];

        bitmap.getPixels(pixels, 0, orgWidth, 0, 0, orgWidth, orgHeight);

        double[] pw = new double[]{0.40754024030832, 0.59245975969168}; //v2
        //double[] pw = new double[]{0.20754024030832, 0.79245975969168};

        int redValue;
        int blueValue;
        int greenValue;

        boolean result = false;
        int skinPixels = 0;

        Bitmap newBitmap = Bitmap.createBitmap(orgWidth, orgHeight, Bitmap.Config.ARGB_8888);

        for (int i = 0; i < pixels.length; i++) {

            //TODO no se si se divide o no
            double[] pixel = new double[]{(double) Color.blue(pixels[i]) / 255,
                    (double) Color.green(pixels[i]) / 255,
                    (double) Color.red(pixels[i]) / 255};

            double probability = -1.0;

            double pr_xw1 = computeNormal(pixel, true);
            double pr_xw2 = computeNormal(pixel, false);

            probability = (pr_xw1 * pw[0]) / (pr_xw1 * pw[0] + pr_xw2 * pw[1]);

//            Log.d("pixel", "x= " + i/orgWidth + " y = " + i%orgHeight + " _ "+ bitmap.getHeight());

            if (probability > pixelThreshold) {
                skinPixels++;
                newBitmap.setPixel(i%orgWidth, i/orgWidth, pixels[i]);
            }else{
                newBitmap.setPixel(i%orgWidth,  i/orgWidth, 0x00FFFFFF);
            }
        }

        double percentage = (double) skinPixels / pixels.length;

        if (percentage > totalThreshold) {
            result = true;
        }

        Log.d(TAG, "skinPixels=" + skinPixels + " pixels.length=" + pixels.length + " percentage =" + percentage + " result=" +result);

        return percentage;
    }

    private double computeNormal(double[] x, boolean firstClass) {
        double result = -1.0;

        //double[] mu1 = new double[]{0.446530384596127, 0.574893410491521, 0.799954164262249};
        double[] mu1 = new double[]{0.466530384596127, 0.594893410491521, 0.799954164262249};//sirve  v2
        //double[] mu1 = new double[]{0.336530384596127, 0.464893410491521, 0.689954164262249};
        //double[] mu2 = new double[]{0.50194350739586, 0.505140387857697, 0.399961003528625};
      //  double[] mu2 = new double[]{0.51194350739586, 0.515140387857697, 0.399961003528625};//sirve
        double[] mu2 = new double[]{0.69194350739586, 0.665140387857697, 0.509961003528625};// mas oscuro v2
        double[][] s1 = {
                new double[]{338.547318554331, -607.442825669145, 241.621427403746},
                new double[]{-607.442825669144, 1727.06649954285, -1014.85326717578},
                new double[]{241.621427403745, -1014.85326717578, 748.56564204244}};

        double[][] s2 = {
                new double[]{62.7039897620283, -52.8032527287235, -5.14761663176258},
                new double[]{-52.8032527287235, 75.7644966080956, -17.7909489578249},
                new double[]{-5.14761663176258, -17.7909489578249, 31.8593200327147}};

        double[] f = new double[]{199.439565623091, 11.1791198677949};


        if (firstClass) {
            double[] x_m = subtract(x, mu1);
            double[] h = multiply(s1, x_m);
            double mahalanobis = innerProduct(h, x_m) * -0.5;
            double exp = Math.exp(mahalanobis);
            result = f[0] * exp;
        } else {
            double[] x_m = subtract(x, mu2);
            double[] h = multiply(s2, x_m);
            double mahalanobis = innerProduct(h, x_m) * -0.5;
            double exp = Math.exp(mahalanobis);
            result = f[1] * exp;
        }

        return result;
    }

    private double[] subtract(double[] x1, double[] x2) {
        double[] x = new double[x1.length];
        for (int i = 0; i < x1.length; i++) {
            x[i] = x1[i] - x2[i];
        }
        return x;
    }
    private double[] multiply(double[] x1, double[] x2) {
        double[] x = new double[x1.length];
        for (int i = 0; i < x1.length; i++) {
            x[i] = x1[i] * x2[i];
        }
        return x;
    }
    private double[] multiply(double[][] x1, double[] x2) {
        double[] x = new double[x1.length];

        // se comprueba si las matrices se pueden multiplicar
        //TODO comprobar
        if (x1[0].length == x2.length) {


            for (int i = 0; i < x1.length; i++) {
                for (int j = 0; j < x1[0].length; j++) {

                        x[i] += x1[i][j] * x2[j];
                }
            }
        }

        return x;
    }


    public double innerProduct(double[] vector1, double[] vector2)
    {
        double result = 0;

        for (int i = 0; i < vector1.length; i++)
        {
            result += vector1[i] * vector2[i];
        }

        return result;
    }
}
