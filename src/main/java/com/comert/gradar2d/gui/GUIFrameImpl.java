package com.comert.gradar2d.gui;

import com.comert.gradar2d.plot.Plot;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import javax.swing.SwingUtilities;

final class GUIFrameImpl extends Frame implements GUIFrame {

    private static final GUIFrameImpl INSTANCE;

    static {
        INSTANCE = new GUIFrameImpl();
    }

    static GUIFrameImpl getInstance() {
        return INSTANCE;
    }

    private final int screenX = 0;
    private final int screenY = 0;
    private final int screenWidth = 1920;
    private final int screenHeight = 1100;

    private final int radarBaseX = 960;
    private final int radarBaseY = 25;

    private final Set<Plot> toPaintPlots = new HashSet<>(20);

    private GUIFrameImpl() throws HeadlessException {
        setBounds(screenX, screenY, screenWidth, screenHeight);
        setResizable(false);
    }

    private void paintBackground(final Graphics graphics) {
        graphics.fillRect(radarBaseX, radarBaseY, 50, 50);
    }

    private void paintTracks(final Graphics graphics) {
        toPaintPlots.forEach(
                plot -> {
                    graphics.fillOval(radarBaseX - plot.getPlotX(), radarBaseY + plot.getPlotY(), 25, 25);
                    plot.setDrawn(true);
                });
    }

    @Override
    public void paint(Graphics graphics) {
        paintBackground(graphics);
        paintTracks(graphics);
    }

    @Override
    public void showFrame() {
        setVisible(true);
    }

    @Override
    public void addPlotToDraw(Plot plot) {
        toPaintPlots.add(plot);

    }

    @Override
    public void drawTracks() {
        repaint();

    }

    @Override
    public void clearScreen() {
        toPaintPlots.clear();
        repaint();
    }

    @Override
    public void closeFrame() {
        setVisible(false);

        Runnable exitAfterMain = () -> this.dispose();
        SwingUtilities.invokeLater(exitAfterMain);
    }

}
