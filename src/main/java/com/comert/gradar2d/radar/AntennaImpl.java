package com.comert.gradar2d.radar;

import com.comert.gembedded.api.ApplicationContextFactory;
import com.comert.gembedded.api.device.DeviceContext;
import com.comert.gembedded.api.device.Pin;
import com.comert.gembedded.api.device.gpio.Event;
import com.comert.gembedded.api.device.gpio.GPIOFactory;
import com.comert.gembedded.api.device.gpio.ListenerCallBack;
import com.comert.gembedded.api.device.gpio.ListenerPin;
import com.comert.gembedded.api.device.gpio.ListenerPinConfigurator;
import com.comert.gembedded.api.device.gpio.OutputPin;
import com.comert.gembedded.api.device.gpio.OutputPinConfigurator;

final class AntennaImpl implements Antenna, ListenerCallBack {

    private static final int MIN_ANTENNA_RANGE_IN_CENTIMETER = 2;
    private static final int MAX_ANTENNA_RANGE_IN_CENTIMETER = 200;
    private static final int TRANSMISSION_TIMEOUT_IN_NANOSECOND = 10000;
    private static final int RECEIVE_TIMEOUT_IN_MILLISECOND = 10;
    private static final int EVENT_TIMEOUT_IN_MILLISECOND = 100;

    private final OutputPin transmitter;
    private final ListenerPin receiver;

    private final int attemptTime;
    private volatile boolean produced;
    private volatile long signalTransmittedTime, signalReceivedTime;
    private volatile float rawRange;
    private volatile boolean rawRangeCorrect;

    AntennaImpl(final Pin transmitterPin, final Pin receiverPin, final int attemptTime) {

        DeviceContext deviceContext = ApplicationContextFactory.getDeviceContextInstance();

        GPIOFactory gpioFactory = deviceContext.getGPIOFactoryInstance();

        transmitter = gpioFactory.createOutputPin(
                OutputPinConfigurator
                        .getBuilder()
                        .pin(transmitterPin)
                        .build());

        receiver = gpioFactory.createListenerPin(
                ListenerPinConfigurator
                        .getBuilder()
                        .pin(receiverPin)
                        .eventStatus(Event.SYNCHRONOUS_BOTH)
                        .timeoutInMilSec(EVENT_TIMEOUT_IN_MILLISECOND)
                        .callBack(this)
                        .build());

        this.attemptTime = attemptTime;
    }

    @Override
    public void startAntenna() {
        receiver.start();
    }

    @Override
    public int getRange() {
        float range = 0.0f;
        int detectionTime = 0;

        for (int i = 0; i < attemptTime; i++) {
            transmit();
            receive();
            if (rawRangeCorrect) {
                range += rawRange;
                detectionTime++;
            }
        }

        return (detectionTime == 0) ? -1 : (int) (range / detectionTime);
    }

    private void transmit() {
        transmitter.setHigh();
        try {
            Thread.sleep(0, TRANSMISSION_TIMEOUT_IN_NANOSECOND);
        } catch (InterruptedException e) {
            System.out.println(getClass().getSimpleName() + " interrupted");
        }
        transmitter.setLow();
    }

    private void receive() {
        try {
            Thread.sleep(RECEIVE_TIMEOUT_IN_MILLISECOND);
        } catch (InterruptedException e) {
            System.out.println(getClass().getSimpleName() + " interrupted");
        }
    }

    @Override
    public void stopAntenna() {
        receiver.terminate();
    }

    @Override
    public void onRising(long timeStamp) {
        if (!produced) {
            signalTransmittedTime = System.nanoTime();
            produced = true;
        }
    }

    @Override
    public void onFalling(long timeStamp) {
        if (produced) {
            signalReceivedTime = System.nanoTime();
            final float rawRange = (signalReceivedTime - signalTransmittedTime) * 0.000017f;
            if (MAX_ANTENNA_RANGE_IN_CENTIMETER > rawRange & rawRange > MIN_ANTENNA_RANGE_IN_CENTIMETER) {
                this.rawRange = rawRange;
                rawRangeCorrect = true;
            } else {
                rawRangeCorrect = false;
            }
            produced = false;
        }
    }

    @Override
    public void onTimeout() {
        // System.out.println("Timeout");
    }

    @Override
    public void onError() {
        // System.out.println("error");
    }

}
