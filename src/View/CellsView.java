package View;

import Model.Game;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.HashMap;

public class CellsView extends JPanel {

    private Point2D offset;
    private Game game;
    private int cells_size = 21;
    public int width;
    public int height;
    double scaleFactor = 1;
    double scaleSpeed = 0.1;
    public Color colorSelected = Color.WHITE;


    public CellsView(int width, int height, Game game) {
        setPreferredSize(new Dimension(width, height));
        setBackground(Color.BLACK);
        this.game = game;
        this.width = width;
        this.height = height;
        offset = new Point2D.Double(0, 0);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(-offset.getX(), -offset.getY());
        g2d.scale(scaleFactor, scaleFactor);

        paintCells(g2d);

        if(scaleFactor > 0) paintGrid(g);

    }

    public void paintCells(Graphics g) {
        double scaledCellsSize = cells_size * scaleFactor;
        int xmin = (int) (offset.getX()/scaledCellsSize)-1;
        int ymin = (int) (offset.getY()/scaledCellsSize)-1;
        int xmax = (int) ((offset.getX()+width)/scaledCellsSize)+1;
        int ymax = (int) ((offset.getY()+height)/scaledCellsSize)+1;
        HashMap<Point, Color> colors = game.getRule().getColor(xmin, ymin, xmax, ymax);
        Color c;
        for(Point p : colors.keySet()) {
            c = colors.get(p);
            g.setColor(c);
            g.fillRect(p.x*cells_size, p.y*cells_size, cells_size, cells_size);
        }
    }

    public void paintGrid(Graphics g) {
        double scaledCellsSize = cells_size * scaleFactor;
        int xmin = (int) (offset.getX()/scaledCellsSize)-1;
        int ymin = (int) (offset.getY()/scaledCellsSize)-1;
        int xmax = (int) ((offset.getX()+width)/scaledCellsSize)+1;
        int ymax = (int) ((offset.getY()+height)/scaledCellsSize)+1;

        int d = (int) (Math.max(1, 1/scaleFactor));

        g.setColor(Color.GRAY);
        for(int i = xmin-Math.floorMod(xmin, d); i <= xmax; i+= d) {
            g.drawLine(i*cells_size,ymin*cells_size,i*cells_size,(int) ymax*cells_size);
        }
        for(int i = ymin-Math.floorMod(ymin, d); i <= ymax; i+=d) {
            g.drawLine(xmin*cells_size,i*cells_size,(int) xmax*cells_size,i*cells_size);
        }
    }

    public void translateX(double x){
        offset.setLocation(offset.getX()+x, offset.getY() );
    }

    public void translateY(double y){
        offset.setLocation(offset.getX(), offset.getY()+y);
    }

    public void zoomIn(){
        scaleFactor *= 1+scaleSpeed;

    }

    public void zoomOut(){
        if(scaleFactor > 0.015)  scaleFactor *= 1-scaleSpeed;
    }

    public void clickAt(int x, int y){
        if(!game.isRunning()){
            game.getRule().setColor( (int)((x+offset.getX())/(cells_size*scaleFactor)),  (int)((y+offset.getY())/(cells_size*scaleFactor)), Color.WHITE);
        }
    }

}
