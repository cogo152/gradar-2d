package com.comert.gradar2d.plot;

public final class Plot implements Cloneable {

    private int plotAzimuth;
    private int plotRange;
    private int plotX;
    private int plotY;
    private boolean drawn;

    Plot() {
    }

    public void setPlotAzimuth(int plotAzimuth) {
        this.plotAzimuth = plotAzimuth;
    }

    public void setPlotRange(int plotRange) {
        this.plotRange = plotRange;
    }

    public int getPlotX() {
        return plotX;
    }

    public int getPlotY() {
        return plotY;
    }

    public boolean isDrawn() {
        return drawn;
    }

    public void setDrawn(boolean drawn) {
        this.drawn = drawn;
    }

    public void calculateCoordinates(final int hypotenuseCoefficient) {
        final int hypotenuse = plotRange * hypotenuseCoefficient;
        plotX = (int) (Math.cos(Math.toRadians(plotAzimuth)) * hypotenuse);
        plotY = (int) (Math.sin(Math.toRadians(plotAzimuth)) * hypotenuse);
    }

    @Override
    protected Plot clone() {
        Plot plot;
        try {
            plot = (Plot) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            plot = new Plot();
        }
        return plot;
    }

}
