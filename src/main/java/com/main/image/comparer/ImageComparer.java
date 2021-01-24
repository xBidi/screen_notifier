package com.main.image.comparer;

import java.awt.image.BufferedImage;

/** @author diegotobalina created on 5/1/21 */
public interface ImageComparer {
  boolean areEqual(BufferedImage img1, BufferedImage img2);
}
