package com.comert.gradar2d.radar;

import com.comert.gembedded.api.ApplicationContextFactory;
import com.comert.gembedded.api.device.DeviceContext;
import com.comert.gembedded.api.device.Pin;
import com.comert.gembedded.api.device.pwm.PWMFactory;
import com.comert.gembedded.api.device.pwm.PWMMode;
import com.comert.gembedded.api.device.pwm.PWMPin;
import com.comert.gembedded.api.device.pwm.PWMPinConfigurator;
import com.comert.gembedded.api.device.pwm.PWMPolarity;
import com.comert.gembedded.api.device.pwm.PWMSilence;

final class MotorImpl implements Motor {

    private static final int RANGE = 20000;
    private final static int DEGREE0 = 2000;
    private final static int DEGREE180 = 10000;
    private final static int RATE = (int) ((DEGREE180 - DEGREE0) / 180.0);

    private static final int CENTER_POSITION = DEGREE0;

    private final PWMPin servo;

    MotorImpl(final Pin pwmPin) {
        DeviceContext deviceContext = ApplicationContextFactory.getDeviceContextInstance();

        PWMFactory pwdFactory = deviceContext.getPWMFactoryInstance();

        servo = pwdFactory.createPWMPin(
                PWMPinConfigurator
                        .getBuilder()
                        .pin(pwmPin)
                        .mode(PWMMode.MARK_SPACE)
                        .polarity(PWMPolarity.LOW_HIGH)
                        .silence(PWMSilence.LOW)
                        .range(RANGE)
                        .build());
    }

    @Override
    public synchronized void startMotor() {
        servo.enable();
        turnAzimuth(CENTER_POSITION);
    }

    @Override
    public void turnAzimuth(int azimuth) {
        servo.writeData(DEGREE0 + (RATE * azimuth));
    }

    @Override
    public synchronized void stopMotor() {
        turnAzimuth(CENTER_POSITION);
        servo.disable();
    }
}
