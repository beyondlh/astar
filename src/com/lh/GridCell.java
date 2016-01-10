package com.lh;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Vector;

public class GridCell extends Component implements Serializable {
    public static final int SET_BLOCKS = 0, SET_START = 1, SET_FINISH = 2;
    public static final double NORMAL = 1, EASY = 0.3, TOUGH = 5, VERY_TOUGH = 10, BLOCK = Double.MAX_VALUE;
    public static boolean tidy = false;
    private static double newBlockStrength = BLOCK;
    private static int editMode = SET_BLOCKS;
    private static GridCell startCell;
    private static GridCell finishCell;
    private static Vector cells = new Vector();
    private static boolean showPath = true;
    private static boolean showDist = true;
    private boolean isStart = false;
    private boolean isFinish = false;
    private double cost = 1.0;
    private transient boolean used = false;
    private transient double distFromStart = -1;
    private transient double distFromFinish = -1;
    // private boolean totalBlock = false;

    private boolean partOfPath = false;

    private Point position;

    public GridCell() {
        cells.addElement(this);
        tidy = true;
        enableEvents(AWTEvent.MOUSE_EVENT_MASK);
    }

    /**
     * Constructer with option for making this cell impasable
     *
     * @param block Boolean, set to true if this cell can not be passed through
     */
    public GridCell(boolean block) {
        this();
        setTotalBlock(block);
    }

    public static void setEditMode(int mode) {
        editMode = mode;
        System.out.println("mode set");
    }

    public static GridCell getStartCell() {
        return startCell;
    }

    public static GridCell getFinishCell() {
        return finishCell;
    }

    public static void reset() {
        for (int i = 0; i < cells.size(); i++) {
            ((GridCell) cells.elementAt(i)).resetCell();
        }
    }

    public static void clearAll() {
        for (int i = 0; i < cells.size(); i++) {
            ((GridCell) cells.elementAt(i)).clearCell();
        }
    }

    public static void setNewBlockStrength(double s) {
        if (s < 0) {
            newBlockStrength = BLOCK;
        } else {
            newBlockStrength = s;
        }
    }

    public static boolean isShowPath() {
        return showPath;
    }

    public static void setShowPath(boolean flag) {
        showPath = flag;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point p) {
        position = p;
    }

    public void processMouseEvent(MouseEvent e) {
        super.processMouseEvent(e);
        if (e.getID() == e.MOUSE_CLICKED) {
            setShowPath(false);
            switch (editMode) {
                case (SET_BLOCKS):
                    if (cost != newBlockStrength) {
                        cost = newBlockStrength;
                    } else {
                        cost = NORMAL;
                    }
                    repaint();
                    break;
                case (SET_START):
                    setStart(true);
                    break;
                case (SET_FINISH):
                    setFinish(true);
                    break;
            }
        }
    }

    public Dimension getPreferredSize() {
        return new Dimension(10, 10);
    }

    public void addToPathFromStart(double distSoFar) {
        used = true;
//
        if (distFromStart == -1) {
            distFromStart = distSoFar + cost;
            return;
        }
        if (distSoFar + cost < distFromStart) {
            distFromStart = distSoFar + cost;
        }
    }

    public void addToPathFromFinish(double distSoFar) {
        used = true;
        if (distFromFinish == -1) {
            distFromFinish = distSoFar + cost;
            return;
        }
        if (distSoFar + cost < distFromFinish) {
            distFromFinish = distSoFar + cost;
        }
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double c) {
        cost = c;
    }

    public boolean isStart() {
        return startCell == this;
    }

    public void setStart(boolean flag) {
        if (flag) {
            GridCell temp = this;
            if (startCell != null) {
                temp = startCell;
                temp.setStart(false);
            }
            startCell = this;
            isStart = true;
            repaint();
            temp.repaint();
        } else {
            isStart = false;
        }
    }

    public boolean isFinish() {
        return finishCell == this;
    }

    public void setFinish(boolean flag) {
        if (flag) {
            GridCell temp = this;
            if (finishCell != null) {
                temp = finishCell;
                temp.setFinish(false);
            }
            finishCell = this;
            isFinish = true;
            repaint();
            temp.repaint();
        } else {
            isFinish = false;
        }
    }

    public boolean isTotalBlock() {
        return cost == BLOCK;
    }

    public void setTotalBlock(boolean flag) {
        if (flag) {
            cost = BLOCK;
        } else {
            cost = NORMAL;
        }
    }

    public boolean isUsed() {
        return used;
    }

    private void resetCell() {
        used = false;
        setPartOfPath(false);
        distFromStart = distFromFinish = -1;
    }

    private void clearCell() {
        setCost(NORMAL);
    }

    public boolean isPartOfPath() {
        return partOfPath;
    }

    public void setPartOfPath(boolean flag) {
        partOfPath = flag;
    }

    /**
     * 到起点的花费
     * */
    public double getDistFromStart() {
        if (GridCell.startCell == this) {
            return 0;
        }
        if (isTotalBlock()) {
            return -1;
        }
        return distFromStart;
    }

    public void paint(Graphics g) {
        Dimension size = getSize();
        g.setColor(Color.white);
        if (cost != NORMAL) {
            if (cost == EASY) {
                g.setColor(Color.orange);
            }
            if (cost == BLOCK) {
                g.setColor(Color.black);
            }
            if (cost == TOUGH) {
                g.setColor(Color.lightGray);
            }
            if (cost == VERY_TOUGH) {
                g.setColor(Color.gray);
            }
        }
        if (showPath && partOfPath) {
            g.setColor(Color.yellow);
        }
        if (startCell == this) {
            g.setColor(Color.green);
        }
        if (finishCell == this) {
            g.setColor(Color.red);
        }
        g.fillRect(0, 0, size.width, size.height);


        g.setColor(Color.black);
        if (showDist && distFromStart > 0) {
            //在这里绘制的数字
            g.drawString("" + distFromStart, 1, (int) (size.height * 0.75));
        }
        g.drawRect(0, 0, size.width - 1, size.height - 1);

    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        if (!tidy) {

            cells = new Vector();
            tidy = true;
        }

        ois.defaultReadObject();
        cells.addElement(this);
        if (isStart) {
            setStart(true);
        }
        if (isFinish) {
            setFinish(true);
        }
    }
}