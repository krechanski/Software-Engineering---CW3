
package tourguide;

/** The class Location is used to hold the location of each waypoint
 *  and the user's current location.
 *  @author Hristiyan Yaprakov and Kiril Rechanski
 **/

import java.util.logging.Logger;

public class Location {
    private static Logger logger = Logger.getLogger("tourguide");

    public double easting;
    public double northing;


    public Location(double easting, double northing) {
        this.easting = easting;
        this.northing = northing;
    }

}
