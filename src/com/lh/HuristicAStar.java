package com.lh;

import java.awt.*;
import java.util.Vector;

public class HuristicAStar extends OneTailAStar implements PathFinder {
    double minCost;

    public HuristicAStar() {
        super();
        stepSpeed = 20;
    }

    /**
     * calculates the waighted manhattan distance from a to b
     */
    public double cbDist(Point a, Point b, double low) {
        return low * (Math.abs(a.x - b.x) + Math.abs(a.y - b.y) - 1);
    }

    public GridCell[] findPath(LHMap LHMap) {
        minCost = Double.MAX_VALUE;
        for (int i = 0; i < LHMap.w; i++) {
            for (int j = 0; j < LHMap.h; j++) {
                minCost = Math.min(LHMap.gridCell[j][i].getCost(), minCost);
            }
        }
        //minCost=0.9;
        System.out.println("Cheepest Tile = " + minCost);
        return super.findPath(LHMap);
    }

    public int step() {
        int tests = 0;
        boolean found = false;
        boolean growth = true;
        GridCell finish = GridCell.getFinishCell();
        Point end = finish.getPosition();
        Vector temp = (Vector) edge.clone();
        //find the most promesing edge cell
        double min = Double.MAX_VALUE;
        double score;
        //int best = -1;
        GridCell best = (GridCell) temp.elementAt(temp.size() - 1);

        GridCell now;
        for (int i = 0; i < temp.size(); i++) {
            now = (GridCell) temp.elementAt(i);
            if (!done.contains(now)) {
                score = now.getDistFromStart();
                score += cbDist(now.getPosition(), end, minCost);
                if (score < min) {
                    min = score;
                    best = now;
                }
            }
        }
//      从edge中得到下一个最优点
        now = best;
        //System.out.println(now.getPosition()+" Selected for expansion");
        edge.removeElement(now);
        done.addElement(now);

        //从最优点中找到下一步查找的筛选点
        GridCell next[] = map.getAdjacent(now);
        for (int i = 0; i < 4; i++) {
            if (next[i] != null) {
                if (next[i] == finish) {
                    found = true;
                }
                if (!next[i].isTotalBlock()) {
                    next[i].addToPathFromStart(now.getDistFromStart());
                    tests++;
                    if (!edge.contains(next[i]) && !done.contains(next[i])) {
                        edge.addElement(next[i]);
                        growth = true;
                    }
                }
            }
            if (found) {
                return FOUND;
            }
        }
        map.repaint();
        if (edge.size() == 0) {
            return NO_PATH;
        }

        //now process best.
        return NOT_FOUND;
    }
}