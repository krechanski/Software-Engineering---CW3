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

    private String errorBanner(String messageName) {
        return  LS
                + "ERROR: " + " !!!!! " + messageName + " !!!!! ";
    }

    private String finerBanner(String messageName) {
        return  LS
                + "STATUS: " + " ------- " + messageName + " ------- ";
    }

    private String finestBanner(String messageName) {
        return  LS
                + "STATUS: " + " ******* " + messageName + " ******* ";
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

    private ArrayList<Chunk> output;

    public ControllerImp(double waypointRadius, double waypointSeparation) {
        this.waypointRadius = waypointRadius;
        this.waypointSeparation = waypointSeparation;
        this.mode = Mode.BROWSE_OVERVIEW;
        this.library = new Library();
        this.output = new ArrayList<Chunk>();
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
        if (this.mode == Mode.BROWSE_OVERVIEW) {
            this.mode = Mode.CREATE;

            // Initialize a tour object
            this.tour = new Tour(id, title, annotation);
            this.output.clear();
            this.output.add(new Chunk.CreateHeader(
                this.tour.title,
                this.tour.legs.size(),
                this.tour.waypoints.size()
            ));

            logger.finer(finerBanner("newTourStarted"));

            return Status.OK;
        } else {
            logger.warning(errorBanner("NOT_IN_BROWSE_MODE"));
            return new Status.Error("The app must be in BROWSE Mode in order to start creating a tour.");
        }
    }

    @Override
    public Status addWaypoint(Annotation annotation) {
        logger.fine(startBanner("addWaypoint"));

        if (this.mode == Mode.CREATE) {
            Waypoint waypoint = new Waypoint(annotation, currentLocation);
            int totalWaypoints = this.tour.waypoints.size();
            this.output.clear();
            logger.finer(finerBanner("Entering"));

            if (this.tour.legs.size() == totalWaypoints) {
                Status addLegStatus = addLeg(null);
                if (addLegStatus != Status.OK) {
                    logger.warning(errorBanner("EMPTY_LEG_NOT_ADDED"));
                    return addLegStatus;
                }
                this.output.clear();
                logger.finest(finestBanner("emptyLegAdded"));
            }

            if (totalWaypoints == 0) {
                this.tour.waypoints.add(waypoint);
                this.output.add(new Chunk.CreateHeader(
                    this.tour.title,
                    this.tour.legs.size(),
                    this.tour.waypoints.size()
                ));
                logger.finer(finerBanner("initialWaypointAdded"));
                return Status.OK;
            } else {
                Waypoint prevWaypoint = this.tour.waypoints.get(totalWaypoints-1);
                Displacement waypointDisplacement = new Displacement(
                    (currentLocation.easting - prevWaypoint.location.easting),
                    (currentLocation.northing - prevWaypoint.location.northing)
                );
                if (waypointDisplacement.distance() < this.waypointSeparation) {
                    logger.warning(errorBanner("WAYPOINT_TOO_CLOSE_TO_PREV"));
                    return new Status.Error("The distance between two adjacent waypoints should be: " + this.waypointSeparation);
                } else {
                    this.tour.waypoints.add(waypoint);
                }
            }

            this.output.add(new Chunk.CreateHeader(
                this.tour.title,
                this.tour.legs.size(),
                this.tour.waypoints.size()
            ));

            logger.finer(finerBanner("waypointAdded"));
            return Status.OK;
        } else {
            logger.warning(errorBanner("NOT_IN_CREATE_MODE"));
            return new Status.Error("Invalid operation. The app is not in CREATE Mode.");
        }
    }

    @Override
    public Status addLeg(Annotation annotation) {
        logger.fine(startBanner("addLeg"));

        if (this.mode == Mode.CREATE) {
            this.output.clear();

            logger.finer(finerBanner("Entering"));

            if (annotation == null) {
                annotation = Annotation.DEFAULT;
            }
            Leg leg = new Leg(annotation);

            if (this.tour.legs.size() == this.tour.waypoints.size()) {
                this.tour.legs.add(leg);
            } else {
                logger.warning(errorBanner("WAYPOINT_MISSING"));
                return new Status.Error("Cannot add a leg right after another leg.");
            }

            this.output.add(new Chunk.CreateHeader(
                this.tour.title,
                this.tour.legs.size(),
                this.tour.waypoints.size()
            ));

            logger.finer(finerBanner("legAdded"));
            return Status.OK;
        } else {
            logger.warning(errorBanner("NOT_IN_CREATE_MODE"));
            return new Status.Error("Invalid operation. The app is not in CREATE Mode.");
        }
    }

    @Override
    public Status endNewTour() {
        logger.fine(startBanner("endNewTour"));

        if (this.mode == Mode.CREATE) {
            if (this.tour.waypoints.size() > 0) {
                if (this.tour.legs.size() == this.tour.waypoints.size()) {
                    this.tour = null;
                    this.mode = Mode.BROWSE_OVERVIEW;
                    logger.finer(finerBanner("tourFinished"));
                    return Status.OK;
                } else {
                    logger.warning(errorBanner("NO_FINAL_WAYPOINT"));
                    return new Status.Error("Cannot finish creating a tour without a final waypoint.");
                }
            } else {
                logger.warning(errorBanner("NO_WAYPOINTS"));
                return new Status.Error("A tour should have at least one waypoint.");
            }
        } else {
            logger.warning(errorBanner("NOT_IN_CREATE_MODE"));
            return new Status.Error("Invalid operation. The app is not in CREATE Mode.");
        }
    }

    //--------------------------
    // Browse tours mode
    //--------------------------

    @Override
    public Status showTourDetails(String tourID) {
        logger.fine("tourDetails");
        if (this.mode == Mode.BROWSE_DETAILS) {
            Tour tourDetails = new Tour(tourID);
            return Status.OK;
        }

        else {
            return new Status.Error("Invalid. The app is not in BROWSE_DETAILS mode");
        }



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
        return this.output;
    }


}
