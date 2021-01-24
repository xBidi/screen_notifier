package com.main.image;

import com.google.gson.Gson;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

/** @author diegotobalina created on 5/1/21 */
@Setter
@Slf4j
public class ImageSender {

  private String gatewayUrl, clientId, clientSecret, group, instanceId;

  public ImageSender(
      String gatewayUrl, String clientId, String clientSecret, String group, String instanceId) {
    this.gatewayUrl = gatewayUrl;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.instanceId = instanceId;
    this.group = group;
  }

  public void send(String imagePath) throws IOException {
    Path path = Paths.get(imagePath);
    byte[] imageBytes = Files.readAllBytes(path);
    String caption = new Date(System.currentTimeMillis()).toString();
    new Thread(
            () -> {
              try {
                sendPhotoGroupMessage(group, imageBytes, caption, instanceId);
              } catch (Exception e) {
                e.printStackTrace();
              }
            })
        .start();
  }

  public void sendPhotoGroupMessage(
      String group, byte[] imageBytes, String caption, String instanceId) throws Exception {
    byte[] encodedBytes = Base64.encodeBase64(imageBytes);
    String base64Image = new String(encodedBytes);

    ImageGroupMessage imageMsgObj = new ImageGroupMessage();
    imageMsgObj.group_name = group;
    imageMsgObj.image = base64Image;
    imageMsgObj.caption = caption;

    Gson gson = new Gson();
    String jsonPayload = gson.toJson(imageMsgObj);

    URL url = new URL(gatewayUrl + instanceId);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setDoOutput(true);
    conn.setRequestMethod("POST");
    conn.setRequestProperty("X-WM-CLIENT-ID", clientId);
    conn.setRequestProperty("X-WM-CLIENT-SECRET", clientSecret);
    conn.setRequestProperty("Content-Type", "application/json");

    OutputStream os = conn.getOutputStream();
    os.write(jsonPayload.getBytes());
    os.flush();
    os.close();

    int statusCode = conn.getResponseCode();
    log.info("response from WhatsApp Gateway: \n");
    log.info("response status Code: " + statusCode);
    log.info("response body start");
    BufferedReader br =
        new BufferedReader(
            new InputStreamReader(
                (statusCode == 200) ? conn.getInputStream() : conn.getErrorStream()));
    String output;
    while ((output = br.readLine()) != null) {
      log.info(output);
    }
    conn.disconnect();
    log.info("response body end");
  }
}

class ImageGroupMessage {
  String group_name = null;
  String caption = null;
  String image = null;
}
