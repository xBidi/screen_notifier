package com.main.screen;

import com.main.image.ImageSender;
import com.main.image.ImageWriter;
import com.main.image.comparer.ImageComparer;
import com.main.image.comparer.ImageComparerByPixels;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/** @author diegotobalina created on 5/1/21 */
@Getter
@Setter
@Slf4j
public class ScreenNotifier {

  private int x, y, w, h;
  private String whatsappGroup, instanceId, clientId, clientSecret, gatewayUrl;
  private boolean onlyTest;

  public ScreenNotifier(
      int x,
      int y,
      int w,
      int h,
      String whatsappGroup,
      boolean onlyTest,
      String instanceId,
      String clientId,
      String clientSecret,
      String gatewayUrl) {
    this.setX(x);
    this.setY(y);
    this.setW(w);
    this.setH(h);
    this.setWhatsappGroup(whatsappGroup);
    this.setOnlyTest(onlyTest);
    this.setInstanceId(instanceId);
    this.setClientId(clientId);
    this.setClientSecret(clientSecret);
    this.setGatewayUrl(gatewayUrl);
  }

  public void start() {
    log.info("painting the screen");
    new ScreenMarker(x, y, w, h).paint();

    log.info("generating timer");
    Timer timer = new Timer();

    ScreenRecorder screenRecorder = new ScreenRecorder(x, y, w, h);
    ImageComparer imageComparer = new ImageComparerByPixels();
    ImageSender imageSender =
        new ImageSender(gatewayUrl, clientId, clientSecret, whatsappGroup, instanceId);

    log.info("starting the timer");
    timer.schedule(new CustomTimer(screenRecorder, imageComparer, imageSender, onlyTest), 0, 5000);
  }
}

@Getter
@Setter
@Slf4j
class CustomTimer extends TimerTask {

  private static final String IMAGE_FORMAT = "jpg";
  private static final String IMAGE_EXTENSION = ".jpg";

  private ScreenRecorder screenRecorder;
  private ImageComparer imageComparer;

  private BufferedImage oldImage;
  private BufferedImage newImage;
  private ImageSender imageSender;
  private Boolean onlyTest;

  CustomTimer(
      ScreenRecorder screenRecorder,
      ImageComparer imageComparer,
      ImageSender imageSender,
      Boolean onlyTest) {
    this.screenRecorder = screenRecorder;
    this.imageComparer = imageComparer;
    this.imageSender = imageSender;
    this.onlyTest = onlyTest;
  }

  private int sameImageCounter = 0;

  /** send the image after a image change and if that image still the same in 3 loops */
  @Override
  @SneakyThrows
  public void run() {
    log.info("creating image capture");
    BufferedImage capture = captureScreen();
    replaceNewImage(capture);
    replaceOldImageIfFirstTimeWithNewOne(); // fix for the null value in the first execution
    boolean areImagesEqual = areImagesEqual();
    replaceOldImageWithNewOne();

    // checks if should send the image using a value as a loop counter
    if (!areImagesEqual) {
      log.info("images are not equal");
      resetSameImageCounter();
      return;
    }
    log.info("images are equal");

    addOneToSameImageCounter();
    if (!shouldSendTheImage()) return;
    // ----- end of the loop condition

    log.info("generating file name and path");
    String newImageName = generateImageName();
    String newImagePath = generateImagePath(newImageName);
    if (isInTestingMode()) {
      log.info("app is in testing mode, stopping");
      return;
    }
    log.info("writing file in disk");
    writeFileInDisk(capture, newImagePath);
    log.info("sending file from disk");
    sendImageFromDiskToApi(newImagePath);
    log.info("deleting file from disk");
    removeFileFromDisk(newImagePath);
  }

  private void resetSameImageCounter() {
    setSameImageCounter(0);
  }

  private int addOneToSameImageCounter() {
    return sameImageCounter++;
  }

  /** check if should send the image using the sameImageCounter */
  private boolean shouldSendTheImage() {
    boolean sending = sameImageCounter == 3;
    log.info(
        String.format(
            "checking same image counter, current value: %d, sending?: %s",
            sameImageCounter, sending));
    return sending;
  }

  private void replaceOldImageWithNewOne() {
    setOldImage(getNewImage());
  }

  private BufferedImage captureScreen() throws AWTException {
    return screenRecorder.capture();
  }

  private boolean isInTestingMode() {
    return onlyTest;
  }

  private String generateImagePath(String newImageName) {
    return newImageName + IMAGE_EXTENSION;
  }

  private String generateImageName() {
    return new Date().toString();
  }

  private void replaceNewImage(BufferedImage capture) {
    setNewImage(capture);
  }

  private void replaceOldImageIfFirstTimeWithNewOne() {
    if (getOldImage() == null) replaceOldImageWithNewOne();
  }

  private boolean areImagesEqual() {

    boolean equal = getImageComparer().areEqual(getOldImage(), getNewImage());
    if (!equal) sameImageCounter = 0;
    return equal;
  }

  private void sendImageFromDiskToApi(String newImagePath) throws IOException {
    getImageSender().send(newImagePath);
  }

  private void removeFileFromDisk(String newImagePath) {
    new File(newImagePath).delete();
  }

  private void writeFileInDisk(BufferedImage capture, String newImagePath) throws IOException {
    new ImageWriter().write(newImagePath, IMAGE_FORMAT, capture);
  }
}
