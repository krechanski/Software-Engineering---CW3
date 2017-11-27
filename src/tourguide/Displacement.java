package tourguide;

import java.util.logging.Logger;

/** The class Displacement calculates the position of a given location
 *  relative to another.
 *  @author Paul Jackson
 **/

public class Displacement {
    private static Logger logger = Logger.getLogger("tourguide");
    private static final String LS = System.lineSeparator();

    private String finerBanner(String messageName) {
        return LS
                + "STATUS: " + " ------- " + messageName + " ------- ";
    }

    public double east;
    public double north;

    /** This method initializes the variables east and north which are declared at the
     * beginning of the class.
     *
     * @param e Stands for 'east'
     * @param n Stands for 'north
     * */

    public Displacement(double e, double n) {
        logger.finer(finerBanner("East: " + e + "  North: "  + n));
        
        east = e;
        north = n;
    }

    /** This method returns the the distance between two points using the
     *  distance formula.
     *
     * @return calculated distance using the Math.sqrt() function.
     */

    public double distance() {
        logger.finer(finerBanner("Entering"));
        
        return Math.sqrt(east * east + north * north);
    }


    /** This method returns the bearings measured clockwise from north direction.
     * Also atan2(y,x) computes angle from x-axis towards y-axis, returning a negative result
     * when y is negative.
     *
     * @return The angle converted to degrees from radians.
     */

    public double bearing() {
        logger.finer(finerBanner("Entering"));
        
        double inRadians = Math.atan2(east, north);
        
        if (inRadians < 0) {
            inRadians = inRadians + 2 * Math.PI;
        }
        
        return Math.toDegrees(inRadians);
    }
        
    
    
}
