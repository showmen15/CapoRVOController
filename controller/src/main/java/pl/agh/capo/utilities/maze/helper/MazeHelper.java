package pl.agh.capo.utilities.maze.helper;


import pl.agh.capo.utilities.maze.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MazeHelper {

    public static List<Room> buildRooms(MazeMap maze) {
        List<Room> rooms = new ArrayList<Room>();
        for (Space space : maze.getSpaces()) {
            Room room = new Room(getRoomWalls(space.getId(), maze), getRoomGates(space.getId(), maze), space.getId());
            rooms.add(room);
        }
        for (Room room : rooms){
            Map<String, Room> gateRooms = new HashMap<String, Room>();
            for (Gate gate : room.getGates()){
                Room nextRoom = findRoom(gate.getId(), room.getSpaceId(), rooms, maze);
                gateRooms.put(gate.getId(), nextRoom);
            }
            room.setGateRooms(gateRooms);
        }
        return rooms;
    }

    private static Room findRoom(String gateId, String roomId, List<Room> rooms, MazeMap maze){
        for (SpaceGate spaceGate : maze.getSpaceGates()){
            if (spaceGate.getGateId().equals(gateId) && !spaceGate.getSpaceId().equals(roomId)){
                return findRoom(spaceGate.getSpaceId(), rooms);
            }
        }
        return null;
    }

    private static Room findRoom(String id, List<Room> rooms){
        for (Room room : rooms){
            if (room.getSpaceId().equals(id)){
                return room;
            }
        }
        return null;
    }

    private static List<Gate> getRoomGates(String spaceId, MazeMap maze) {
        List<Gate> gates = new ArrayList<Gate>();
        for (SpaceGate spaceGate : maze.getSpaceGates()){
            if (!spaceGate.getSpaceId().equals(spaceId)){
                continue;
            }
            Gate gate = getGate(spaceGate.getGateId(), maze.getGates());
            if (gate == null){
                continue;
            }
            gates.add(gate);
        }
        return gates;
    }

    private static Gate getGate(String gateId, List<Gate> gates) {
        for (Gate gate : gates){
            if (gate.getId().equals(gateId)){
                return gate;
            }
        }
        return null;
    }

    private static List<Wall> getRoomWalls(String spaceId, MazeMap maze) {
        List<Wall> walls = new ArrayList<Wall>();
        for (SpaceWall spaceWall : maze.getSpaceWalls()){
            if (!spaceWall.getSpaceId().equals(spaceId)){
                continue;
            }
            Wall wall = getWall(spaceWall.getWallId(), maze.getWalls());
            if (wall == null){
                continue;
            }
            walls.add(wall);
        }
        return walls;
    }

    private static Wall getWall(String wallId, List<Wall> walls) {
        for (Wall wall : walls){
            if (wall.getId().equals(wallId)){
                return wall;
            }
        }
        return null;
    }

    public static double getMinY(List<Wall> walls){
        double min = Double.MAX_VALUE;
        for (Wall wall : walls){
            if (wall.getFrom().getY() < min){
                min = wall.getFrom().getY();
            }
            if (wall.getTo().getY() < min){
                min = wall.getTo().getY();
            }
        }
        return min;
    }

    public static double getMaxY(List<Wall> walls){
        double max = Double.MIN_VALUE;
        for (Wall wall : walls){
            if (wall.getFrom().getY() > max){
                max = wall.getFrom().getY();
            }
            if (wall.getTo().getY() > max){
                max = wall.getTo().getY();
            }
        }
        return max;
    }

    public static double getMinX(List<Wall> walls){
        double min = Double.MAX_VALUE;
        for (Wall wall : walls){
            if (wall.getFrom().getX() < min){
                min = wall.getFrom().getX();
            }
            if (wall.getTo().getX() < min){
                min = wall.getTo().getX();
            }
        }
        return min;
    }

    public static double getMaxX(List<Wall> walls){
        double max = Double.MIN_VALUE;
        for (Wall wall : walls){
            if (wall.getFrom().getX() > max){
                max = wall.getFrom().getX();
            }
            if (wall.getTo().getX() > max){
                max = wall.getTo().getX();
            }
        }
        return max;
    }


    public static MazeMap createCrossRoadMaze(){
        List<Wall> walls = new ArrayList<>();
        walls.add(createWall(0.0, 0.0, 0.0, 3.0));
        walls.add(createWall(0.0, 0.0, 3.0, 0.0));
        walls.add(createWall(1.0, 1.0, 1.0, 2.0));
        walls.add(createWall(1.0, 1.0, 2.0, 1.0));
        walls.add(createWall(1.0, 2.0, 2.0, 2.0));
        walls.add(createWall(2.0, 1.0, 2.0, 2.0));
        walls.add(createWall(0.0, 3.0, 2.0, 3.0));
        walls.add(createWall(3.0, 0.0, 3.0, 2.0));
        walls.add(createWall(3.0, 2.0, 5.0, 2.0));
        walls.add(createWall(5.0, 2.0, 5.0, 5.0));
        walls.add(createWall(2.0, 5.0, 5.0, 5.0));
        walls.add(createWall(2.0, 3.0, 2.0, 5.0));
        walls.add(createWall(3.0, 3.0, 3.0, 4.0));
        walls.add(createWall(3.0, 3.0, 4.0, 3.0));
        walls.add(createWall(4.0, 3.0, 4.0, 4.0));
        walls.add(createWall(3.0, 4.0, 4.0, 4.0));
        MazeMap mazeMap = new MazeMap();
        mazeMap.setGates(new ArrayList<>());
        mazeMap.setWalls(walls);
        return mazeMap;
    }

    public static MazeMap createNarrowPassMaze() {
        List<Wall> walls = new ArrayList<>();
        walls.add(createWall(0.0, 0.0, 0.0, 5.0));
        walls.add(createWall(0.0, 0.0, 3.0, 0.0));
        walls.add(createWall(3.0, 0.0, 3.0, 5.0));
        walls.add(createWall(0.0, 5.0, 3.0, 5.0));
        walls.add(createWall(0.0, 2.5, 1.05, 2.5));
        walls.add(createWall(1.95, 2.5, 3.0, 2.5));

        MazeMap mazeMap = new MazeMap();
        mazeMap.setGates(new ArrayList<>());
        mazeMap.setWalls(walls);
        return mazeMap;
    }

    public static MazeMap createHugeFreeSpaceMaze() {
        return createFreeSpaceMaze(20.0);
    }

    public static MazeMap createFreeSpaceMaze() {
        return createFreeSpaceMaze(5.0);
    }

    public static MazeMap createFreeSpaceMaze(double size) {
        List<Wall> walls = new ArrayList<>();
        walls.add(createWall(0.0, 0.0, 0.0, size));
        walls.add(createWall(0.0, 0.0, size, 0.0));
        walls.add(createWall(size, 0.0, size, size));
        walls.add(createWall(0.0, size, size, size));
        MazeMap mazeMap = new MazeMap();
        mazeMap.setGates(new ArrayList<>());
        mazeMap.setWalls(walls);
        return mazeMap;
    }

    private static Wall createWall(double x1, double y1, double x2, double y2){
        Coordinates c1 = new Coordinates();
        c1.setX(x1);
        c1.setY(y1);
        Coordinates c2 = new Coordinates();
        c2.setX(x2);
        c2.setY(y2);
        Wall wall = new Wall();
        wall.setFrom(c1);
        wall.setTo(c2);
        return wall;
    }
}
