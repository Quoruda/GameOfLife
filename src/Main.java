import Control.InputController;
import Control.MouseController;
import Model.Rules.*;
import View.Window;
import Model.Game;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        int n = 500;
        Rule rule = new Modular(1, n, n){

            @Override
            public boolean isOverPopulation(int n) {
                return n > 3;
            }

            @Override
            public boolean isUnderPopulation(int n) {
                return n < 2;
            }

            @Override
            public boolean isReproduction(int n) {
                return n == 3;
            }
        };

        //rule = new Conway(1000,1000);
        //rule.genPrimordialSoup(0, 0, n-1, n-1);
        Game game = new Game(rule,10);


        game.start();

        SwingUtilities.invokeLater(() -> {
            Window window = new Window(600, 600, game);
            InputController inputController = new InputController(window, game);
            MouseController mouseController = new MouseController(window);
            window.setFPS(30);
            window.start();
        });
    }


}