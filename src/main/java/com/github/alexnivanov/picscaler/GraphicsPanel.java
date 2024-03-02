package com.github.alexnivanov.picscaler;

import javax.swing.*;
import java.awt.*;

import static java.lang.Math.cos;

public class GraphicsPanel extends JPanel {

    Image image;
    Thread thread;
    volatile boolean running;

    double maxScale = 3.0;
    double animationScale = 1.0;
    double animationSpeed = 0.05;
    long animationFrame = 0;

    @Override
    public void paint(Graphics g) {
        if (image != null) {
            int imageWidth = image.getWidth(null);
            int imageHeight = image.getHeight(null);

            int screenWidth = getWidth();
            int screenHeight = getHeight();

            double imageRatio = getRatio(imageWidth, imageHeight);
            double screenRatio = getRatio(screenWidth, screenHeight);

            int width;
            int height;

            if (imageRatio > screenRatio) {
                double scale = getRatio(screenHeight, imageHeight);

                width = (int) (imageWidth * scale * animationScale);
                height = (int) (screenHeight * animationScale);
            } else {
                double scale = getRatio(screenWidth, imageWidth);

                width = (int) (screenWidth * animationScale);
                height = (int) (imageHeight * scale * animationScale);
            }

            int x = (screenWidth - width) / 2;
            int y = (screenHeight - height) / 2;

            g.drawImage(image, x, y, width, height, Color.WHITE, null);
        }
    }

    public void tick() {
        animationFrame++;

        animationScale = 1.0 + (maxScale - 1.0) * (1 - cos(animationSpeed * animationFrame));
    }

    private static double getRatio(int numerator, int denominator) {
        return ((double) numerator) / ((double) denominator);
    }

}
