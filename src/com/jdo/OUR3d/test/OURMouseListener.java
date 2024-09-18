package com.jdo.OUR3d.test;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public interface OURMouseListener extends MouseListener{
    /**
     * Invoked when a mouse button has been pressed on a component.
     */
    default public void mousePressed(MouseEvent e){}

    /**
     * Invoked when a mouse button has been released on a component.
     */
    default public void mouseReleased(MouseEvent e){}

    /**
     * Invoked when the mouse enters a component.
     */
    default public void mouseEntered(MouseEvent e){}

    /**
     * Invoked when the mouse exits a component.
     */
    default public void mouseExited(MouseEvent e){}

}
