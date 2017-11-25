/**
 *
 */
package tourguide;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author pbj
 */
public class ControllerImp implements Controller {
    private static Logger logger = Logger.getLogger("tourguide");
    private static final String LS = System.lineSeparator();

    private String startBanner(String messageName) {
        return  LS
                + "-------------------------------------------------------------" + LS
                + "MESSAGE: " + messageName + LS
                + "-------------------------------------------------------------";
    }

    //--------------------------
    // Global Controller Variables
    //--------------------------

    public Mode mode;   // A Mode instance that will keep track of the state the app is in
    public double waypointRadius;
    public double waypointSeparation;
    public Location currentLocation;
    public Library library;

    private Tour tour;

    public ControllerImp(double waypointRadius, double waypointSeparation) {
        this.waypointRadius = waypointRadius;
        this.waypointSeparation = waypointSeparation;
        this.mode = Mode.BROWSE_DETAILS;
        this.library = new Library();
    }

    //--------------------------
    // Create tour mode
    //--------------------------

    // Some examples are shown below of use of logger calls.  The rest of the methods below that correspond
    // to input messages could do with similar calls.

    @Override
    public Status startNewTour(String id, String title, Annotation annotation) {
        logger.fine(startBanner("startNewTour"));

        // Set the mode
        if (this.mode == Mode.BROWSE) {
            this.mode = Mode.CREATE;

            // Initialize a tour object
            this.tour = new Tour(id, title, annotation);
            logger.finer("addTheNewTour");

            return Status.OK;
        } else {
            return new Status.Error("The app must be in BROWSE Mode in order to start creating a tour.");
        }
    }

    @Override
    public Status addWaypoint(Annotation annotation) {
        logger.fine(startBanner("addWaypoint"));

        if (this.mode != Mode.CREATE) {
            Waypoint waypoint = new Waypoint(annotation, currentLocation);
            int totalWaypoints = this.tour.waypoints.size();

            if (this.tour.legs.size() == totalWaypoints) {
                Status addLegStatus = addLeg(null);
                if (addLegStatus != Status.OK) {
                    return addLegStatus;
                }
            }

            if (totalWaypoints == 0) {
                this.tour.waypoints.add(waypoint);

                return Status.OK;
            } else {
                Waypoint prevWaypoint = this.tour.waypoints.get(totalWaypoints-1);
                Displacement waypointDisplacement = new Displacement(
                    (currentLocation.easting - prevWaypoint.location.easting),
                    (currentLocation.northing - prevWaypoint.location.northing)
                );
                if (waypointDisplacement.distance() < this.waypointSeparation) {
                    return new Status.Error("The distance between two waypoints should be: " + this.waypointSeparation);
                } else {
                    this.tour.waypoints.add(waypoint);
                }
            }

            return Status.OK;
        } else {
            return new Status.Error("Invalid operation. The app is not in CREATE Mode.");
        }
    }

    @Override
    public Status addLeg(Annotation annotation) {
        logger.fine(startBanner("addLeg"));

        if (this.mode != Mode.CREATE) {
            if (annotation == null) {
                annotation = Annotation.DEFAULT;
            }
            Leg leg = new Leg(annotation);

            if (this.tour.legs.size() == this.tour.waypoints.size()) {
                this.tour.legs.add(leg);
            } else {
                return new Status.Error("Cannot add a leg right after another leg.");
            }

            return Status.OK;
        } else {
            return new Status.Error("Invalid operation. The app is not in CREATE Mode.");
        }
    }

    @Override
    public Status endNewTour() {
        logger.fine(startBanner("endNewTour"));
        return new Status.Error("unimplemented");
    }

    //--------------------------
    // Browse tours mode
    //--------------------------

    @Override
    public Status showTourDetails(String tourID) {
        this.mode = Mode.BROWSE_DETAILS;
        logger.fine("tourDetails");
        Tour tourDetails = new Tour(tourID);
        return Status.OK;
    }

    @Override
    public Status showToursOverview() {

//       if (this.mode != Mode.BROWSE_DETAILS) {
//           logger.info("showToursOverview");
//
//
//       }

        return null;
    }

    //--------------------------
    // Follow tour mode
    //--------------------------

    @Override
    public Status followTour(String id) {
        return new Status.Error("unimplemented");
    }

    @Override
    public Status endSelectedTour() {
        return new Status.Error("unimplemented");
    }

    //--------------------------
    // Multi-mode methods
    //--------------------------
    @Override
    public void setLocation(double easting, double northing) {
        this.currentLocation = new Location (easting, northing);
    }

    @Override
    public List<Chunk> getOutput() {
        return new ArrayList<Chunk>();
    }


}
