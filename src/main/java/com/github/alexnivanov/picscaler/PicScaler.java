package com.github.alexnivanov.picscaler;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.function.Function;

public class PicScaler {

    public static void main(String[] args) {
        final JFrame frame = new JFrame("Pic Scaler");

        // Schedule a job for the event-dispatching thread: creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(() -> createAndShowGUI(frame));
    }

    private static void createAndShowGUI(JFrame frame) {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // otherwise the program won't exit
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null); // center on the screen

        JPanel contentPane = new JPanel(new BorderLayout());
        GraphicsPanel graphicsPanel = new GraphicsPanel();
        contentPane.add(graphicsPanel, BorderLayout.CENTER);

        Function<String, Void> showError = (message) -> {
            JOptionPane.showMessageDialog(contentPane, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
            return null;
        };

        JPanel controlsPane = new JPanel();

        controlsPane.add(getLoadImageButton(frame, graphicsPanel, showError));
        controlsPane.add(getControlButton(frame, graphicsPanel, showError));
        controlsPane.add(new JLabel("Масштаб"));
        JTextField scaleTextField = new JTextField(Double.toString(graphicsPanel.maxScale), 3);
        scaleTextField.addActionListener(e -> {
            try {
                graphicsPanel.maxScale = Double.parseDouble(scaleTextField.getText());
            } catch (NumberFormatException nfe) {
                showError.apply("Некорректное число!");
            }
        });
        controlsPane.add(scaleTextField);
        controlsPane.add(new JLabel("Скорость"));
        JTextField speedTextField = new JTextField(Double.toString(graphicsPanel.animationSpeed), 3);
        speedTextField.addActionListener(e -> {
            try {
                graphicsPanel.animationSpeed = Double.parseDouble(speedTextField.getText());
            } catch (NumberFormatException nfe) {
                showError.apply("Некорректное число!");
            }
        });
        controlsPane.add(speedTextField);

        contentPane.add(controlsPane, BorderLayout.PAGE_END);

        frame.setContentPane(contentPane);

        frame.setVisible(true);
    }

    private static JButton getControlButton(JFrame frame, GraphicsPanel graphicsPanel, Function<String, Void> showError) {
        JButton controlButton = new JButton("Старт");

        controlButton.addActionListener(actionEvent -> {
            if (graphicsPanel.running) {
                graphicsPanel.thread = null;
                graphicsPanel.running = false;
                controlButton.setText("Старт");
            } else if (graphicsPanel.image == null) {
                showError.apply("Сначала загрузите изображение!");
            } else {
                graphicsPanel.thread = new Thread(() -> {
                    while (graphicsPanel.running) {
                        try {
                            //noinspection BusyWait
                            Thread.sleep(20);

                            graphicsPanel.tick();

                            SwingUtilities.invokeLater(() -> {
                                frame.repaint();
                                frame.revalidate();
                            });
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                });
                graphicsPanel.thread.start();
                graphicsPanel.running = true;
                controlButton.setText("Стоп");
            }
        });

        return controlButton;
    }

    private static JButton getLoadImageButton(JFrame frame, GraphicsPanel graphicsPanel, Function<String, Void> showError) {
        JButton loadImageButton = new JButton("Загрузить изображение");

        loadImageButton.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(loadImageButton);

            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();

                try {
                    BufferedImage image = ImageIO.read(file);
                    if (image == null) {
                        showError.apply("Не удалось загрузить изображение :(");
                    } else {
                        graphicsPanel.image = image;
                        frame.repaint();
                        frame.revalidate();
                    }
                } catch (IOException ex) {
                    showError.apply("Не удалось загрузить изображение: ");
                }
            }
        });

        return loadImageButton;
    }

}
