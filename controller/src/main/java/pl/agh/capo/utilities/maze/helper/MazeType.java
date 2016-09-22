package pl.agh.capo.utilities.maze.helper;

import pl.agh.capo.utilities.maze.MazeMap;

public enum MazeType {
    NARROW_PASS(MazeHelper.createNarrowPassMaze()),
    CROSS_ROAD(MazeHelper.createCrossRoadMaze()),
    FREE_SPACE(MazeHelper.createFreeSpaceMaze()),
    HUGE_FREE_SPACE(MazeHelper.createHugeFreeSpaceMaze());

    private MazeMap mazeMap;

    MazeType(MazeMap mazeMap){
        this.mazeMap = mazeMap;
    }

    public MazeMap getMazeMap(){
        return mazeMap;
    }
}

