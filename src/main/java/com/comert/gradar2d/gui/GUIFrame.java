package com.comert.gradar2d.gui;

import com.comert.gradar2d.plot.Plot;

interface GUIFrame {

    void showFrame();

    void addPlotToDraw(Plot plot);

    void drawTracks();

    void clearScreen();

    void closeFrame();

}
