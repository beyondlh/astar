package com.lh;

import java.util.Vector;

public class OneTailAStar extends Object implements PathFinder, Runnable {
    public final int NO_PATH = -1, NOT_FOUND = 0, FOUND = 1;

    protected Vector edge;
    protected Vector done;
    protected LHMap map;
    int stepSpeed = 100;//higher is slower
    private int maxSteps = 1000;

    Thread loop;
    double distFromStart = 0;
    private boolean findFirst = false;

    public GridCell[] findPath(LHMap map) {
        this.map = map;
        GridCell.reset();
        edge = new Vector();
        done = new Vector();
        System.out.println("开始寻找路径");
        if (GridCell.getStartCell() == null) {
            System.out.println("起点没有设置");
            return null;
        }
        if (GridCell.getFinishCell() == null) {
            System.out.println("终点没有设置");
            return null;
        }
        System.out.println("起点坐标 " + map.getStartPosition());
        loop = new Thread(this);
        loop.start();
        return null;
    }

    public void run() {
        edge.addElement(GridCell.getStartCell());
        int pass = 0;
        boolean found = false;
        double start, diff;
        int state = NOT_FOUND;
        while (state == NOT_FOUND && pass < maxSteps) {
            pass++;
            start = System.currentTimeMillis();
            state = step();
            diff = System.currentTimeMillis() - start;
            try {
                loop.sleep(Math.max((long) (stepSpeed - diff), 0));
            } catch (InterruptedException e) {
            }
        }
        if (state == FOUND) {
            setPath(map);
        } else {
            System.out.println("没有找到合适路径");
        }
    }


    public int step() {
        int tests = 0;
        boolean found = false;
        boolean growth = false;
        GridCell finish = GridCell.getFinishCell();
//        最开始的时候edge中只有起点
        Vector temp = (Vector) edge.clone();
        for (int i = 0; i < temp.size(); i++) {
            GridCell now = (GridCell) temp.elementAt(i);
            GridCell next[] = map.getAdjacent(now);
            for (int j = 0; j < 4; j++) {
                if (next[j] != null) {
                    if (next[j] == finish) {
                        found = true;
                    }
                    next[j].addToPathFromStart(now.getDistFromStart());
                    tests++;
                    //如果不是障碍点，且edge中不包含next[j],将next[j]放入到edge中
                    if (!next[j].isTotalBlock() && !edge.contains(next[j])) {
                        edge.addElement(next[j]);
                        growth = true;
                    }
                }
            }
            if (found) {
                return FOUND;
            }
            done.addElement(now);

            //edge.removeElement(now);
        }
        map.repaint();
        if (!growth) {
            return NO_PATH;
        }
        // System.out.println("Tests:"+tests+" Edge:"+edge.size()+" Done:"+done.size());
        return NOT_FOUND;
    }

    public void setPath(LHMap map) {
        System.out.println("Path Found");
        GridCell.setShowPath(true);
        boolean finished = false;
        GridCell next;
        GridCell now = GridCell.getFinishCell();
        GridCell stop = GridCell.getStartCell();
        while (!finished) {
            next = map.getLowestAdjacent(now);
            now = next;
            now.setPartOfPath(true);
            now.repaint();
            if (now == stop) {
                finished = true;
            }
            try {
                loop.sleep(stepSpeed);
            } catch (InterruptedException e) {
            }
        }
        System.out.println("完成");
    }

}