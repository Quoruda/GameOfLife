package Model;

import Model.Rules.Rule;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

public class Game {

    private Rule rule ;

    private int tick;

    private int tps;

    private Timer timer;



    public static final Color BACKGROUND_COLOR = Color.BLACK;

    public Game(Rule rule, int tps) {
        this.rule = rule;
        this.tps = tps;
        tick = 0;
        timer = new Timer(getDelay(), new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextTick();
            }
        } );
        Timer timerInfo = new Timer(1000, new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("tick: " + tick + " nbAlive:" + rule.size());
            }
        });
        //timerInfo.start();
    }

    public void setTps(int tps) {
        this.tps = tps;
    }

    public int getDelay(){
        return 1000/tps;
    }

    public void nextTick(){
        rule.next();
        tick++;
    }

    public void start(){
        timer.setDelay(getDelay());
        timer.restart();
    }

    public void pause(){
        timer.stop();
    }

    public Rule getRule(){
        return rule;
    }


    public boolean isRunning(){
        return timer.isRunning();
    }


    public void readLifFile(String filename) {
        rule.reset();
        File file = new File(filename);
        int x1 = 0;
        int  y1 = 0;
        int x2 = 0;
        int y2 = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String ligne;
            while ((ligne = br.readLine()) != null) {
                if(ligne.startsWith("#")){
                    if(ligne.startsWith("#P")){
                        String[] parts = ligne.split(" ");
                        x1 = Integer.parseInt(parts[1]);
                        y1 = Integer.parseInt(parts[2]);
                        x2 = 0;
                        y2 = 0;
                    }
                }else{
                    for(x2 = 0; x2 < ligne.length(); x2++){
                        Character c = ligne.charAt(x2);
                        if(c.equals('*')){
                            int x = x1+x2;
                            int y = y1+y2;
                            rule.setColor(x, y, Color.WHITE);
                            //System.out.println(x+" "+y);
                        }
                    }
                    y2++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
