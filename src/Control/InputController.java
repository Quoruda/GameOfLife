package Control;

import Model.Game;
import View.Window;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashSet;

public class InputController implements KeyListener {

    private Window window;
    private Game game;
    HashSet<Integer> keys = new HashSet<>();

    public InputController(Window window, Game game) {
        this.window = window;
        window.setInputController(this);
        this.game = game;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        keys.add(e.getKeyCode());
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keys.remove(e.getKeyCode());
        if(e.getKeyCode() == KeyEvent.VK_SPACE) {
            if(game.isRunning()){
                game.pause();
            }else{
                game.start();
            }
        }else if(e.getKeyCode() == KeyEvent.VK_TAB) {
            if(window.getCellsView().colorSelected == Color.WHITE){
                window.getCellsView().colorSelected = Color.BLACK;
            }else{
                window.getCellsView().colorSelected = Color.WHITE;
            }
        }else if(e.getKeyCode() == KeyEvent.VK_G){
            window.getCellsView().showGrid = !window.getCellsView().showGrid;
        }else{
        }

    }

    public HashSet<Integer> getKeys() {
        return keys;
    }
}
