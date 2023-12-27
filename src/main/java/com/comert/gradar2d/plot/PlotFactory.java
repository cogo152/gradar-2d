package com.comert.gradar2d.plot;

abstract class PlotFactory {

    private static final Plot PLOT_PROTOTYPE;

    static {
        PLOT_PROTOTYPE = new Plot();
        PLOT_PROTOTYPE.setDrawn(true);
    }

    private PlotFactory() {
    }

    public static Plot createPlot() {
        return PLOT_PROTOTYPE.clone();
    }

}
