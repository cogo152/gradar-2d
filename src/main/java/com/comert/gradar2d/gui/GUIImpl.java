package com.comert.gradar2d.gui;

import com.comert.gradar2d.plot.Plot;
import com.comert.gradar2d.plot.PlotPool;

import java.util.Optional;
import java.util.stream.Stream;

final class GUIImpl extends Thread implements GUI {

    private static final int REFRESH_TIME_IN_MILLISECOND = 1500;
    private static final int HYPOTENUSE_COEFFICIENT = 5;

    private final GUIFrame guiFrame;
    private final PlotPool plotPool = PlotPool.getInstance();

    private volatile boolean running;
    private volatile boolean threadExited;

    GUIImpl(GUIFrame guiFrame) {
        this.guiFrame = guiFrame;
        threadExited = false;
    }

    @Override
    public void run() {
        while (running) {
            Optional<Stream<Plot>> plotStreamOptional = plotPool.getNotDrawnPlots();
            if (plotStreamOptional.isPresent()) {
                Stream<Plot> plotStream = plotStreamOptional.get();
                plotStream.forEach(
                        plot -> {
                            plot.calculateCoordinates(HYPOTENUSE_COEFFICIENT);
                            guiFrame.addPlotToDraw(plot);

                        });
                guiFrame.drawTracks();
            }
            try {
                Thread.sleep(REFRESH_TIME_IN_MILLISECOND);
            } catch (InterruptedException e) {
                System.out.println(getClass().getSimpleName() + " interrupted");
            }
            guiFrame.clearScreen();
        }

        threadExited = true;
    }

    @Override
    public synchronized void startGUI() {
        if (!running) {
            guiFrame.showFrame();
            setPriority(MIN_PRIORITY);
            running = true;
            start();
        }
    }

    @Override
    public synchronized void stopGUI() {
        if (running) {
            running = false;
            while (!threadExited) {
            }
            guiFrame.closeFrame();
        }
    }
}
