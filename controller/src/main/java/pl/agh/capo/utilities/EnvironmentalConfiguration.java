package pl.agh.capo.utilities;

import pl.agh.capo.controller.collision.velocity.CollisionFreeVelocityType;

public class EnvironmentalConfiguration {

    public static final boolean SIMULATION = true;

    // RabbitMQ connection leads
    public static final String ADDRESS = SIMULATION ? "127.0.0.1" : "192.168.2.101";
    public static final String USERNAME = SIMULATION ? "guest" : "panda";
    public static final String PASSWORD = SIMULATION ?"guest" : "panda";
    public static final String CHANNEL_NAME = "capoRobotState";

    public static final int SIMULATION_TIME_STEP_IN_MS = 200; 
    public static final int SIMULATION_SLEEP_BETWEEN_TIME_STEP_IN_MS = 5;  //SIMULATION_TIME_STEP_IN_MS;
        
    // Empirically selected values to control robots
    // Units consisted with International System of Units
    public static final double ROBOT_DIAMETER = 0.3;
    public static final double ROBOT_WHEELS_HALF_DISTANCE = 0.14;
    public static final double ROBOT_MAX_SPEED = 0.5;
    public static final double PREF_ROBOT_SPEED = ROBOT_MAX_SPEED / 2.0;

    // Multiplier of angle to desired velocity (used to perform turning)
    public static final double ANGULAR_VELOCITY_FACTOR = 3.0;

    // Max speed multiplier determining collision free zone in Free right side solution
    public static final double RECIPROCITY_FACTOR_OPPOSITE = 5.0;
    public static final double RECIPROCITY_FACTOR_PRIMARY = 1.0;
    public static final double RECIPROCITY_FACTOR_SUBORDINATED = 10.0;
    public static final double RECIPROCITY_FACTOR_BEHIND = 3.0;

    // Multiplier of half robot diameter
    public static final double WALL_COLLISION_MARGIN_FACTOR = 1.6;

    // Radius used to find VO
    public static final double VO_ROBOT_RADIUS = 1.6 * ROBOT_DIAMETER; //1.6 * ROBOT_DIAMETER;
    
    // Minimum distance which allow robots to turn near each other,
    // Additional area of VO - not quite consistent with definition
    public static final double ACCEPTABLE_RADIUS =  1 * VO_ROBOT_RADIUS; 

    // Multiplier of max speed during selecting velocity in pure VO solution
    public static final double MIN_SPEED_FACTOR = 0.2;


    public static CollisionFreeVelocityType COLLISIONFREEVELOCITYMETHOD = CollisionFreeVelocityType.RIGHT_HAND;
    
    
    public static boolean FEAR = (1 == 0); 
    
    public static boolean ACTIVEFEARFACTORGATE = false;
    
    // Tries of selecting velocity in pure VO solution
    // public static final int TRIES_COUNT = 500;
}
