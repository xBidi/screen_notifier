package com.main.screen;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.swing.*;
import java.awt.*;

/** @author diegotobalina created on 5/1/21 */
@Getter
@Setter
@Slf4j
public class ScreenMarker {
  private int x, y, w, h;

  public ScreenMarker(int x, int y, int w, int h) {
    this.setX(x);
    this.setY(y);
    this.setW(w);
    this.setH(h);
  }

  public void paint() {
    log.info(String.format("painting rectangle in screen with x:%d y:%d w:%d h:%d", x, y, w, h));
    EventQueue.invokeLater(
        () -> {
          try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
          } catch (ClassNotFoundException
              | InstantiationException
              | IllegalAccessException
              | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
          }
          JFrame frame = new JFrame("Screen marker");
          frame.setUndecorated(true);
          frame.setBackground(new Color(0, 0, 0, 0));
          frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
          frame.add(new TestPane(x, y, w, h));
          frame.setAlwaysOnTop(true);
          frame.pack();
          frame.setVisible(true);
        });
  }
}

@Setter
class TestPane extends JPanel {

  private int x, y, w, h;

  public TestPane(int x, int y, int w, int h) {
    this.setX(x);
    this.setY(y);
    this.setW(w);
    this.setH(h);
    setOpaque(false);
    setLayout(new GridBagLayout());
  }

  @Override
  public Dimension getPreferredSize() {
    return new Dimension(x + w + 2, y + h + 2);
  }

  @Override
  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2d = (Graphics2D) g.create();
    g2d.setColor(Color.RED);
    g2d.drawRect(x - 1, y - 1, w + 1, h + 1);
    g2d.dispose();
  }
}
