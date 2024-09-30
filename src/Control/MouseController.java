package Control;

import View.Window;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashMap;

public class MouseController extends MouseAdapter implements MouseWheelListener {

    Window window;
    Boolean rightClick = false;
    HashMap<Object,Point> positions = new HashMap<>();


    public MouseController(Window window) {
        this.window = window;
        window.setMouseController(this);
        window.addMouseWheelListener(this);
        window.getCellsView().addMouseListener(this);
        window.getCellsView().addMouseMotionListener(this);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        positions.put(e.getSource(), e.getPoint());
    }

    public void mouseDragged(MouseEvent e) {
        positions.put(e.getSource(), e.getPoint());
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if(e.getSource() == window.getCellsView()) {
            window.getCellsView().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            rightClick = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if(e.getButton() == MouseEvent.BUTTON1) {
            rightClick = false;
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int notches = e.getWheelRotation();
        if (notches < 0) {
            window.getCellsView().zoomIn();
        } else {
            window.getCellsView().zoomOut();
        }
    }

    public boolean isRightClick() {
        return rightClick;
    }

    public Point getPosition(Object source) {
        return positions.get(source);
    }



}
