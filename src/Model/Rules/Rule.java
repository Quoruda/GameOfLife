package Model.Rules;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public abstract class Rule {



    public abstract void next();

    public abstract void genPrimordialSoup(int minX, int minY, int width, int height);

    public abstract int size();

    public abstract HashMap<Point, Color> getColor(int xmin, int ymin, int xmax, int ymax);

    public abstract void setColor(int x, int y, Color color);

    public abstract void reset();

}
