package Model.Rules;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;

public abstract class Modular extends Rule {

    int width, height;
    boolean[][] states;
    boolean[][] tempStates;
    private int range;

    public Modular(int range, int width, int height) {
        states = new boolean[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                states[j][i] = false;
            }
        }
        tempStates = new boolean[width][height];
        this.width = width;
        this.height = height;
        this.range = range;
    }

    public static void cloneStates(boolean[][] source, boolean[][] target) {
        for (int i = 0; i < source.length; i++) {
            for (int j = 0; j < source[i].length; j++) {
                target[i][j] = source[i][j];
            }
        }
    }

    public boolean isALive(boolean[][] states,int x, int y) {
        return states[Math.floorMod(y,height)][Math.floorMod(x,width)];
    }

    public int getNbNeighbours(boolean[][] states,int x, int y) {
        int count = 0;
        for(int x2 = x -range; x2 <= x + range; x2++) {
            for(int y2 = y -range; y2 <= y + range; y2++) {
                if(x2 != x || y2 != y) {
                    if(states[Math.floorMod(y2, height)][Math.floorMod(x2, width)]) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    @Override
    public void next() {
        cloneStates(states, tempStates);

        int n, x, y;
        for(y = 0; y < height; y++){
            for(x = 0; x < width; x++){
                n = getNbNeighbours(tempStates, x, y);
                if(isALive(states,x,y)){
                    if(isUnderPopulation(n) || isOverPopulation(n)){
                        states[y][x] = false;
                    }
                }else{
                    if(isReproduction(n)){
                        states[y][x] = true;
                    }
                }
            }
        }
    }

    @Override
    public void genPrimordialSoup(int minX, int minY, int width, int height) {
        Random random = new Random();
        for(int x = minX; x < minX+width; x++){
            for(int y = minY; y < minY+height; y++){
                if(random.nextBoolean()){
                    states[y][x] = true;
                }
            }
        }
    }

    @Override
    public int size() {
        int n = 0;
        int x, y;
        for(x = 0; x < width; x++){
            for(y = 0; y < height; y++){
                if(states[y][x]) n++;
            }
        }
        return n;
    }

    @Override
    public HashMap<Point, Color> getColor(int xmin, int ymin, int xmax, int ymax) {
        boolean[][] tempStates = new boolean[height][width];
        cloneStates(states, tempStates);
        HashMap<Point, Color> colors = new HashMap<>();
        int x, y;
        for(x = xmin; x <= xmax; x++){
            for(y = ymin; y <= ymax; y++){
                if(isALive(tempStates,x,y)){
                    colors.put(new Point(x, y), Color.WHITE);
                }
            }
        }
        return colors;
    }

    @Override
    public void setColor(int x, int y, Color color) {
        if(color == Color.WHITE){
            states[Math.floorMod(y, height)][Math.floorMod(x, width)] = true;
        }else{
            states[Math.floorMod(y, height)][Math.floorMod(x, width)] = false;
        }
    }

    public abstract boolean isOverPopulation(int n);

    public abstract boolean isUnderPopulation(int n);

    public abstract boolean isReproduction(int n);

    public void reset(){
        states = new boolean[height][width];
        tempStates = new boolean[height][width];
    }

}
