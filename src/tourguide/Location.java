
package tourguide;

import java.util.logging.Logger;

public class Location {
    private static Logger logger = Logger.getLogger("tourguide");

    public double easting;
    public double northing;


    public Location(double easting, double northing) {
        this.easting = easting;
        this.northing = northing;
    }

//    public Displacement deltaFrom(Location) {
//        return null;
//    }
}
