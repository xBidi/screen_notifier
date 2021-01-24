package com.main.image.comparer;

import java.awt.image.BufferedImage;

/** @author diegotobalina created on 5/1/21 */
public class ImageComparerByPixels implements ImageComparer {

  private static final int MAX_DIFFERENCE_PERCENT = 5;

  @Override
  public boolean areEqual(BufferedImage img1, BufferedImage img2) {

    // medidas imagen 1
    int img1Width = img1.getWidth();
    int img1Height = img1.getHeight();
    int img1Size = img1Height * img1Width;

    // medidas imagen 2
    int img2Width = img2.getWidth();
    int img2Height = img2.getHeight();
    int img2Size = img2Height * img2Width;

    // comparar tamaño
    if (img1Size != img2Size) return false;

    // comparar pixeles
    int differencePixelsCount = 0;
    for (int x = 0; x < img1Width; x++) {
      for (int y = 0; y < img1Height; y++) {
        if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
          differencePixelsCount++;
        }
      }
    }

    // calcular % de diferencia
    double differencePercent = reglaDeTres(img1Size, differencePixelsCount, MAX_DIFFERENCE_PERCENT);
    return differencePercent < MAX_DIFFERENCE_PERCENT;
  }

  private double reglaDeTres(
      double img1Size, double differencePixelsCount, double maxDifferencePercent) {
    return ((differencePixelsCount * maxDifferencePercent) / img1Size) * (double) 100;
  }
}
