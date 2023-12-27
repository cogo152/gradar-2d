package com.comert.gradar2d.app;

import com.comert.gembedded.api.ApplicationContextFactory;
import com.comert.gembedded.api.device.DeviceContext;
import com.comert.gembedded.api.device.Pin;
import com.comert.gradar2d.gui.GUI;
import com.comert.gradar2d.gui.GUIFactory;
import com.comert.gradar2d.plot.PlotPool;
import com.comert.gradar2d.radar.Radar;
import com.comert.gradar2d.radar.RadarFactory;

public class App {

    public static void main(String[] args) throws InterruptedException {

        DeviceContext deviceContext = ApplicationContextFactory.getDeviceContextInstance();

        try {
            deviceContext.setupDevice();

            final var pwmPin = Pin.PIN_19;
            final var transmitterPin = Pin.PIN_20;
            final var receiverPin = Pin.PIN_21;

            final var playTime = 60000;

            final GUI gui = GUIFactory.createGUI();
            final Radar radar = RadarFactory.createRadar(transmitterPin, receiverPin, pwmPin);

            gui.startGUI();
            radar.startRadar();
            Thread.sleep(playTime);
            radar.stopRadar();
            gui.stopGUI();

            PlotPool pool = PlotPool.getInstance();
            pool.printTotalPoolSize();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw throwable;
        } finally {
            deviceContext.shutdownDevice();
        }

    }

}
