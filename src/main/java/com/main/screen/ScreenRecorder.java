package com.main.screen;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;

/** @author diegotobalina created on 5/1/21 */
@Setter
@Slf4j
public class ScreenRecorder {
  private int x, y, w, h;

  public ScreenRecorder(int x, int y, int w, int h) {
    this.setX(x);
    this.setY(y);
    this.setW(w);
    this.setH(h);
  }

  public BufferedImage capture() throws AWTException {
    Robot robot = new Robot();
    log.info(String.format("getting a capture from screen with x:%d y:%d w:%d h:%d", x, y, w, h));
    Rectangle captureRect = new Rectangle(x, y, w, h); // taskbar
    return robot.createScreenCapture(captureRect);
  }
}
