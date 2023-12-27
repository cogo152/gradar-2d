package com.comert.gradar2d.radar;

interface Motor {

    void startMotor();

    void turnAzimuth(int azimuthDegree);

    void stopMotor();
}
