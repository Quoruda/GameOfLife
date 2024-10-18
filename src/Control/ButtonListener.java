package Control;

import Model.Game;
import View.CellsView;
import View.Window;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonListener implements ActionListener {

    Window window;
    Game game;

    public ButtonListener(Window window, Game game) {
        this.window = window;
        this.game = game;
        window.setActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == window.openFileButton) {
            window.openFile();
        } else if(e.getSource() == window.primordialSoupButton) {
            CellsView cellsView = window.getCellsView();
            game.getRule().genPrimordialSoup(cellsView.getXmin(), cellsView.getYmin(), cellsView.getXmax(), cellsView.getYmax());
        } else if(e.getSource() == window.clearButton) {
            game.getRule().reset();
        }
        window.requestFocusInWindow();
    }
}
