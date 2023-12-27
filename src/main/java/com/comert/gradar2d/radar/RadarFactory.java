package com.comert.gradar2d.radar;

import com.comert.gembedded.api.device.Pin;

public abstract class RadarFactory {

    private RadarFactory() {
    }

    public static Radar createRadar(final Pin transmitterPin, final Pin receiverPin, final Pin pwmPin) {
        final Antenna antenna = new AntennaImpl(transmitterPin, receiverPin, 2);
        final Motor motor = new MotorImpl(pwmPin);
        return new RadarImpl(antenna, motor);
    }

}
