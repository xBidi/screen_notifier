package com.main.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/** @author diegotobalina created on 5/1/21 */
public class ImageWriter {

  public ImageWriter() {}

  public void write(String imagePath, String format, BufferedImage image) throws IOException {
    ImageIO.write(image, format, new File(imagePath));
  }
}
