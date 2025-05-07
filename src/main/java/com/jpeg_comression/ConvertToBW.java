package com.jpeg_comression;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

public class ConvertToBW extends JFrame {
    private JLabel previewLabel;
    private BufferedImage sourceImage;

    public ConvertToBW() {
        super("Convert PNG to B/W with Dithering");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        JButton openBtn = new JButton("Open PNG");
        JButton ditherBtn = new JButton("Convert with Dithering");
        JButton noDitherBtn = new JButton("Convert without Dithering");

        previewLabel = new JLabel();
        previewLabel.setHorizontalAlignment(JLabel.CENTER);

        openBtn.addActionListener(this::onOpen);
        ditherBtn.addActionListener(e -> onConvert(true));
        noDitherBtn.addActionListener(e -> onConvert(false));

        JPanel controlPanel = new JPanel();
        controlPanel.add(openBtn);
        controlPanel.add(ditherBtn);
        controlPanel.add(noDitherBtn);

        add(controlPanel, BorderLayout.NORTH);
        add(new JScrollPane(previewLabel), BorderLayout.CENTER);
    }

    private void onOpen(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File file = chooser.getSelectedFile();
                sourceImage = ImageIO.read(file);
                previewLabel.setIcon(new ImageIcon(sourceImage));
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Failed to load image: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onConvert(boolean dither) {
        if (sourceImage == null) {
            JOptionPane.showMessageDialog(this, "Please open an image first.", "Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }
        BufferedImage bw = dither ? floydSteinbergDither(sourceImage) : thresholdBW(sourceImage);
        previewLabel.setIcon(new ImageIcon(bw));
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save Converted PNG");
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                File out = chooser.getSelectedFile();
                ImageIO.write(bw, "png", out);
                JOptionPane.showMessageDialog(this, "Saved to: " + out.getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Save failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static BufferedImage thresholdBW(BufferedImage src) {
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        WritableRaster raster = result.getRaster();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = src.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int gray = (r + g + b) / 3;
                int bw = gray < 128 ? 0 : 1;
                raster.setSample(x, y, 0, bw);
            }
        }
        return result;
    }

    private static BufferedImage floydSteinbergDither(BufferedImage src) {
        int width = src.getWidth();
        int height = src.getHeight();
        BufferedImage gray = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = src.getRGB(x, y);
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                int grayVal = (int)(0.299 * r + 0.587 * g + 0.114 * b);
                gray.getRaster().setSample(x, y, 0, grayVal);
            }
        }
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        WritableRaster srcRaster = gray.getRaster();
        WritableRaster dstRaster = result.getRaster();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int oldVal = srcRaster.getSample(x, y, 0);
                int newVal = oldVal < 128 ? 0 : 255;
                dstRaster.setSample(x, y, 0, newVal == 255 ? 1 : 0);
                int err = oldVal - newVal;
                distributeError(srcRaster, x + 1, y, err, 7.0/16);
                distributeError(srcRaster, x - 1, y + 1, err, 3.0/16);
                distributeError(srcRaster, x,     y + 1, err, 5.0/16);
                distributeError(srcRaster, x + 1, y + 1, err, 1.0/16);
            }
        }
        return result;
    }

    private static void distributeError(WritableRaster raster, int x, int y, int err, double factor) {
        int width = raster.getWidth();
        int height = raster.getHeight();
        if (x >= 0 && x < width && y >= 0 && y < height) {
            int old = raster.getSample(x, y, 0);
            int updated = (int)Math.round(old + err * factor);
            raster.setSample(x, y, 0, clamp(updated, 0, 255));
        }
    }

    private static int clamp(int val, int min, int max) {
        return val < min ? min : (val > max ? max : val);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ConvertToBW().setVisible(true));
    }
}