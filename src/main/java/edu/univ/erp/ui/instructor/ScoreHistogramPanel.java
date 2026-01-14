package edu.univ.erp.ui.instructor;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Collections;

public class ScoreHistogramPanel extends JPanel {

    private final List<Double> scores;

    public ScoreHistogramPanel(List<Double> scores) {
        this.scores = scores != null ? scores : Collections.emptyList();
        setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (scores.isEmpty()) {
            g.drawString("No scores available", 20, 20);
            return;
        }

        Graphics2D g2 = (Graphics2D) g;
        int width = getWidth();
        int height = getHeight();

        int bins = 10;
        int[] counts = new int[bins];

        for (double s : scores) {
            int b = (int) Math.min(bins - 1, (s / 100.0) * bins);
            counts[b]++;
        }

        int maxCount = 1;
        for (int c : counts) maxCount = Math.max(maxCount, c);

        int barWidth = width / bins;

        for (int i = 0; i < bins; i++) {

            int barHeight = (int) ((counts[i] / (double) maxCount) * (height - 40));

            int x = i * barWidth + 5;
            int y = height - barHeight - 20;

            g2.setColor(new Color(100, 180, 255));
            g2.fillRect(x, y, barWidth - 10, barHeight);

            g2.setColor(Color.BLACK);
            g2.drawRect(x, y, barWidth - 10, barHeight);

            g2.drawString((i * 10) + "-" + (i * 10 + 9), x, height - 5);
        }
    }
}
