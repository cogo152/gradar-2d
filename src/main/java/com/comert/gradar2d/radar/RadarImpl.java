package com.comert.gradar2d.radar;

import com.comert.gradar2d.plot.Plot;
import com.comert.gradar2d.plot.PlotPool;

final class RadarImpl extends Thread implements Radar {

    private static final int MINIMUM_AZIMUTH = 0;
    private static final int MAXIMUM_AZIMUTH = 180;
    private static final int AZIMUTH_COVERAGE = 70;
    private static final int LAST_DETECTED_AZIMUTH = MAXIMUM_AZIMUTH + AZIMUTH_COVERAGE;
    private static final int POSITIVE_AZIMUTH_CORRECTION = 17;
    private static final int NEGATIVE_AZIMUTH_CORRECTION = -17;
    private static final int AZIMUTH_SPEED = 3;

    private volatile boolean running;
    private volatile boolean threadExited;

    private final Antenna antenna;
    private final Motor motor;
    private final PlotPool plotPool = PlotPool.getInstance();

    RadarImpl(final Antenna antenna, final Motor motor) {
        this.antenna = antenna;
        this.motor = motor;
        this.threadExited = false;
    }

    @Override
    public void run() {

        int lastDetectedAzimuth;

        while (running) {

            lastDetectedAzimuth = LAST_DETECTED_AZIMUTH;

            for (int azimuth = MINIMUM_AZIMUTH; azimuth <= MAXIMUM_AZIMUTH; azimuth += AZIMUTH_SPEED) {
                motor.turnAzimuth(azimuth);
                final int range = antenna.getRange();
                if (range > -1) {
                    final boolean detected = !isAlreadyDetected(lastDetectedAzimuth, azimuth);
                    if (detected) {
                        final int correctedAzimuth = correctAzimuth(azimuth, POSITIVE_AZIMUTH_CORRECTION);
                        sendPlot(correctedAzimuth, range);
                        lastDetectedAzimuth = azimuth;
                    }
                }
            }

            lastDetectedAzimuth = LAST_DETECTED_AZIMUTH;

            for (int azimuth = MAXIMUM_AZIMUTH; azimuth >= MINIMUM_AZIMUTH; azimuth -= AZIMUTH_SPEED) {
                motor.turnAzimuth(azimuth);
                final int range = antenna.getRange();
                if (range > -1) {
                    final boolean detected = !isAlreadyDetected(lastDetectedAzimuth, azimuth);
                    if (detected) {
                        final int correctedAzimuth = correctAzimuth(azimuth, NEGATIVE_AZIMUTH_CORRECTION);
                        sendPlot(correctedAzimuth, range);
                        lastDetectedAzimuth = azimuth;
                    }
                }
            }
        }

        threadExited = true;
    }

    private boolean isAlreadyDetected(final int lastDetectedAzimuth, final int azimuth) {
        return Math.abs(lastDetectedAzimuth - azimuth) < AZIMUTH_COVERAGE;
    }

    private int correctAzimuth(final int azimuth, final int rangeCoefficient) {
        return azimuth + rangeCoefficient;
    }

    private void sendPlot(final int azimuth, final int range) {
        Plot plot = plotPool.getDrawnPlot();
        plot.setPlotAzimuth(azimuth);
        plot.setPlotRange(range);
        plot.setDrawn(false);
    }

    @Override
    public synchronized void startRadar() {
        motor.startMotor();
        antenna.startAntenna();
        running = true;
        setPriority(MAX_PRIORITY);
        start();
    }

    @Override
    public synchronized void stopRadar() {
        running = false;
        while (!threadExited) {
        }
        motor.stopMotor();
        antenna.stopAntenna();
    }
}
