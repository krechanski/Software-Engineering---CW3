
package tourguide;

/** The class Waypoint hold the information about a Point of interest
 *  and its location.
 *  @author Hristiyan Yaprakov and Kiril Rechanski
 **/

public class Waypoint {

    public Annotation annotation;
    public Location location;

    public Waypoint(Annotation annotation, Location location) {
        this.annotation = annotation;
        this.location = location;
    }


}
