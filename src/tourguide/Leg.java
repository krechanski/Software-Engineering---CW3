package tourguide;

/** The class Waypoint hold the information about a Point of interest
 *  and its location.
 *  @author Hristiyan Yaprakov and Kiril Rechanski
 **/

import java.util.logging.Logger;

public class Leg {

    private static Logger logger = Logger.getLogger("tourguide");

    public Annotation annotation;

    public Leg (Annotation annotation) {
        this.annotation = annotation;
    }
}
