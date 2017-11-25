
package tourguide;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Tour {

    public String id;
    public String title;
    public Annotation annotation;
    public ArrayList<Waypoint> waypoints;
    public ArrayList<Leg> legs;

    public Tour (String id, String title, Annotation ann) {
        this.id = id;
        this.title = title;
        this.annotation = ann;
        this.waypoints = new ArrayList<Waypoint>();
        this.legs = new ArrayList<Leg>();
    }

}
