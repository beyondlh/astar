package com.lh;

import java.awt.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class LHMap extends Panel implements Serializable {
    int w = 20;
    int h = 20;
    transient Image buffer;

    GridCell gridCell[][] = new GridCell[w][h];

    public LHMap() {
        super();
        //{{INIT_CONTROLS
        setLayout(new GridLayout(w, h));
        setSize(insets().left + insets().right + 430, insets().top + insets().bottom + 270);
        //}}
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                gridCell[j][i] = new GridCell();
                gridCell[j][i].setPosition(new Point(j, i));
                add(gridCell[j][i]);
            }
        }
    }

    public void paint(Graphics g) {
        if (buffer == null) {
            buffer = createImage(getBounds().width, getBounds().height);
        }
        Graphics bg = buffer.getGraphics();
        super.paint(bg);
        bg.setColor(Color.black);
        g.drawImage(buffer, 0, 0, null);
        //g.drawRect(0,0,getBounds().width-1,getBounds().height-1);
    }

    public void update(Graphics g) {
        paint(g);
    }

    public Point getStartPosition() {
        GridCell start = GridCell.getStartCell();
        return start.getPosition();
    }

    /**
     * 获取临近的四个GridCell
     */
    public GridCell[] getAdjacent(GridCell g) {
        GridCell next[] = new GridCell[4];
        Point p = g.getPosition();
        if (p.y != 0) {
            next[0] = gridCell[p.x][p.y - 1];
        }
        if (p.x != w - 1) {
            next[1] = gridCell[p.x + 1][p.y];
        }
        if (p.y != h - 1) {
            next[2] = gridCell[p.x][p.y + 1];
        }
        if (p.x != 0) {
            next[3] = gridCell[p.x - 1][p.y];
        }
        return next;
    }

    /**
     * 获取临近最优GridCell
     */
    public GridCell getLowestAdjacent(GridCell g) {
        GridCell next[] = getAdjacent(g);
        GridCell small = next[0];
        double dist = Double.MAX_VALUE;
        for (int i = 0; i < 4; i++) {
            if (next[i] != null) {
                double nextDist = next[i].getDistFromStart();
                if (nextDist < dist && nextDist >= 0) {
                    small = next[i];
                    dist = next[i].getDistFromStart();
                }
            }
        }
        return small;
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        GridCell.tidy = false;
        ois.defaultReadObject();
        GridCell.setShowPath(false);
    }
}