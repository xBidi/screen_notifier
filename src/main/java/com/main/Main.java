package com.main;

import com.main.screen.ScreenNotifier;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

  private static String WA_GATEWAY_URL =
      "http://api.whatsmate.net/v3/whatsapp/group/image/message/";

  public static void main(String[] args) {
    if (args.length < 8) {
      log.warn(
          "need 8 arguments to start:"
              + " int x (example: 100),"
              + " int y (example: 200),"
              + " int w (example: 400),"
              + " int h (example: 700),"
              + " String whatsappGroup,"
              + " Boolean onlyTest,"
              + " String instanceId,"
              + " String clientId,"
              + " String clientSecret.");
      return;
    }

    int x = Integer.valueOf(args[0] == null ? "100" : args[0]);
    int y = Integer.valueOf(args[1] == null ? "200" : args[1]);
    int w = Integer.valueOf(args[2] == null ? "400" : args[2]);
    int h = Integer.valueOf(args[3] == null ? "700" : args[3]);

    String whatsappGroup = args[4];
    Boolean onlyTest = Boolean.valueOf(args[5]);
    String instanceId = args[6];
    String clientId = args[7];
    String clientSecret = args[8];

    log.info("starting with configuration");

    log.info("x: " + x);
    log.info("y: " + y);
    log.info("w: " + w);
    log.info("h: " + h);
    log.info("whatsappGroup: " + whatsappGroup);
    log.info("onlyTest: " + onlyTest);
    log.info("instanceId: " + instanceId);
    log.info("clientId: " + clientId);
    log.info("clientSecret: " + clientSecret);

    log.info("configuration loaded");

    new ScreenNotifier(
            x, y, w, h, whatsappGroup, onlyTest, instanceId, clientId, clientSecret, WA_GATEWAY_URL)
        .start();
  }
}
