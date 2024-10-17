package View;

import Control.InputController;
import Control.MouseController;
import Model.Game;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.util.HashSet;

public class Window extends JFrame {

    private Game game;

    private final static int BUTTON_HEIGHT = 50;

    private int fps;

    private Timer timer;

    CellsView cellsView;

    private double deltaTime;
    private long lastCurrentTimeMillis;

    private InputController inputController;
    private MouseController mouseController;

    public Window(int width, int height, Game game) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(width, height+BUTTON_HEIGHT);
        setResizable(false);
        setVisible(true);
        setFocusable(true);
        requestFocusInWindow();
        setLayout(new BorderLayout());

        this.game = game;
        fps = 120;
        cellsView = new CellsView(width, height, game);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JButton button1 = new JButton("<");
        JButton button2 = new JButton("||");
        JButton button3 = new JButton(">");
        JButton openFileButton = new JButton("Open file");
        buttonPanel.add(openFileButton);
        //buttonPanel.add(button1);
        //buttonPanel.add(button2);
        //buttonPanel.add(button3);
        buttonPanel.setSize(width, BUTTON_HEIGHT );

        add(buttonPanel, BorderLayout.NORTH);
        add(cellsView, BorderLayout.CENTER);

        openFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }
        });

        timer = new Timer(getDelay(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moveCamera();
                draw();
                repaint();
            }
        });
    }

    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Life Files (*.lif)", "lif"));
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            game.readLifFile(selectedFile.getAbsolutePath());
        }
        requestFocusInWindow();

    }

    public void start(){
        timer.start();
    }

    public void moveCamera(){
        long currentTimeMillis = System.currentTimeMillis();
        long duration =currentTimeMillis - lastCurrentTimeMillis;
        lastCurrentTimeMillis = currentTimeMillis;
        deltaTime = 1.0 / (double) duration;

        HashSet<Integer> keys = inputController.getKeys();
        double speed = 150;

        double x = 0;
        double y = 0;

        if(keys.contains(KeyEvent.VK_UP)){
            y = -1;
        }
        if(keys.contains(KeyEvent.VK_DOWN)){
            y = 1;
        }
        if(keys.contains(KeyEvent.VK_RIGHT)){
            x = 1;
        }
        if(keys.contains(KeyEvent.VK_LEFT)){
            x = -1;
        }

        double l = Math.sqrt(x*x + y*y);
        if(l != 0){
            x = x/l;
            y = y/l;
        }

        cellsView.translateY(y*deltaTime*speed);
        cellsView.translateX(x*deltaTime*speed);
    }

    public void draw(){
        if(mouseController.isRightClick()){
            Point p = mouseController.getPosition(getCellsView());
            if(p != null){
                cellsView.clickAt(p.x, p.y);
            }
        }
    }

    public int getDelay(){
        return 1000/fps;
    }

    public void setFPS(int fps) {
        this.fps = fps;
        timer.setDelay(getDelay());
    }

    public void setInputController(InputController inputController) {
        addKeyListener(inputController);
        this.inputController = inputController;
    }

    public void setMouseController(MouseController mouseController) {
        addMouseListener(mouseController);
        this.mouseController = mouseController;
    }

    public CellsView getCellsView(){
        return cellsView;
    }

}
