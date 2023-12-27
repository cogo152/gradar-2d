package com.comert.gradar2d.gui;

public abstract class GUIFactory {

    private GUIFactory() {
    }

    public static GUI createGUI() {
        GUIFrame guiFrame = GUIFrameImpl.getInstance();
        return new GUIImpl(guiFrame);
    }

}
