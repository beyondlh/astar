package com.lh;

public interface PathFinder
{
    /**
     * Finds the shortest route through the LHMap and returns an array
     * of all of the cells.
     */
    public GridCell[] findPath(LHMap LHMap);
}