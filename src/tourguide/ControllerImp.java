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
        return LS
                + "-------------------------------------------------------------" + LS
                + "MESSAGE: " + messageName + LS
                + "-------------------------------------------------------------";
    }

    private String errorBanner(String messageName) {
        return LS
                + "ERROR: " + " !!!!! " + messageName + " !!!!! ";
    }

    private String finerBanner(String messageName) {
        return LS
                + "STATUS: " + " ------- " + messageName + " ------- ";
    }

    private String finestBanner(String messageName) {
        return LS
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

    private int stage;

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
                Waypoint prevWaypoint = this.tour.waypoints.get(totalWaypoints - 1);
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
                    boolean added = this.library.addTour(this.tour);
                    if (!added) {
                        logger.warning(errorBanner("newTourNotAdded"));
                        return new Status.Error("A tour with id: '" + this.tour.id + "' already exists.");
                    }
                    logger.finer(finerBanner("newTourAdded"));

                    this.tour = null;
                    logger.finer(finerBanner("newTourFinished"));

                    Status browseStatus = showToursOverview();
                    if (browseStatus != Status.OK) {
                        logger.warning(errorBanner("SOMETHING_WENT_WRONG"));
                    }

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
        logger.fine("showTourDetails");
        if (this.mode == Mode.BROWSE_OVERVIEW) {
            this.mode = Mode.BROWSE_DETAILS;

            for (Tour tour : this.library.tours) {
                if (tourID.equalsIgnoreCase(tour.id)) {
                    this.tour = tour;
                    this.output.add(new Chunk.BrowseDetails(tour.id,tour.title, tour.annotation));
                }
            }

            if (this.tour == null) {
                logger.warning(errorBanner("TOUR_NOT_FOUND"));
                return new Status.Error("A Tour with id: '" + tourID + "' has not been found.");
            }

            return Status.OK;
        } else {
            return new Status.Error("Invalid. The app is not in BROWSE_OVERVIEW mode");
        }
    }

    @Override
    public Status showToursOverview() {
        logger.fine(startBanner("browseTourOverview"));
        this.output.clear();

        this.mode = Mode.BROWSE_OVERVIEW;
        Chunk.BrowseOverview overview = new Chunk.BrowseOverview();

        for (Tour tour: this.library.tours) {
           overview.addIdAndTitle(tour.id, tour.title);
        }

        this.output.add(overview);

        return Status.OK;
    }

    //--------------------------
    // Follow tour mode
    //--------------------------

    @Override
    public Status followTour(String id) {
        logger.fine(startBanner("followTour"));
        this.output.clear();

        if (this.mode == Mode.BROWSE_DETAILS || this.mode == Mode.BROWSE_OVERVIEW) {
            this.mode = Mode.FOLLOW;
            this.stage = 0;

            for (int i=0; i < this.library.tours.size(); i++) {
                if (this.library.tours.get(i).id == id) {
                    this.tour = this.library.tours.get(i);
                }
            }

            if (this.tour == null) {
                logger.warning(errorBanner("TOUR_NOT_FOUND"));
                return new Status.Error("A Tour with id: '" + id + "' has not been found.");
            }

            this.output.add(new Chunk.FollowHeader(
                this.tour.title,
                this.stage,
                this.tour.waypoints.size()
            ));
            this.output.add(new Chunk.FollowLeg(
                this.tour.legs.get(0).annotation
            ));

            Displacement disp = new Displacement(
                (this.tour.waypoints.get(0).location.easting - currentLocation.easting),
                (this.tour.waypoints.get(0).location.northing - currentLocation.northing)
            );
            this.output.add(new Chunk.FollowBearing(
                disp.bearing(),
                disp.distance()
            ));

            logger.finer(finerBanner("followTourInitiated"));
        } else if (this.mode == Mode.FOLLOW) {
            logger.finer(finerBanner("Entering"));

            boolean onWaypoint = false;
            int whichWaypoint = 0;

            Displacement userNearCurrentWaypoint = new Displacement(
                (this.tour.waypoints.get(this.stage).location.easting - currentLocation.easting),
                (this.tour.waypoints.get(this.stage).location.northing - currentLocation.northing)
            );
            if (userNearCurrentWaypoint.distance() <= this.waypointRadius) {
                onWaypoint = true;
                whichWaypoint = this.stage;
                this.stage++;
                logger.finer(finerBanner("nextWayPointReached"));
            } else {
                if (this.stage != 0) {
                    Displacement userNearPrevWaypoint = new Displacement(
                    (this.tour.waypoints.get(this.stage-1).location.easting - currentLocation.easting),
                    (this.tour.waypoints.get(this.stage-1).location.northing - currentLocation.northing)
                    );
                    if (userNearCurrentWaypoint.distance() <= this.waypointRadius) {
                        onWaypoint = true;
                        whichWaypoint = this.stage-1;
                        logger.finer(finerBanner("prevWayPointReached"));
                    }
                }
            }

            this.output.add(new Chunk.FollowHeader(
                this.tour.title,
                this.stage,
                this.tour.waypoints.size()
            ));
            if (this.stage != 0 && onWaypoint) {
                this.output.add(new Chunk.FollowWaypoint(
                    this.tour.waypoints.get(whichWaypoint).annotation
                ));
            }
            if (this.stage != this.tour.waypoints.size()) {
                this.output.add(new Chunk.FollowLeg(
                    this.tour.legs.get(this.stage).annotation
                ));
                Displacement userToNextWaypoint = new Displacement(
                    (this.tour.waypoints.get(this.stage).location.easting - currentLocation.easting),
                    (this.tour.waypoints.get(this.stage).location.northing - currentLocation.northing)
                );
                this.output.add(new Chunk.FollowBearing(
                    userToNextWaypoint.bearing(),
                    userToNextWaypoint.distance()
                ));
            }
        } else {
            logger.warning(errorBanner("NOT_IN_BROWSE_MODE"));
            return new Status.Error("Invalid operation. The app is not in BROWSE Mode.");
        }

        logger.finer(finerBanner("followingTour"));
        return Status.OK;

    }

    @Override
    public Status endSelectedTour() {
        if (this.mode == Mode.FOLLOW) {
            this.output.clear();
            this.tour = null;

            logger.finer(finerBanner("endFollowTour"));

            Status browseStatus = showToursOverview();
            if (browseStatus != Status.OK) {
                logger.warning(errorBanner("SOMETHING_WENT_WRONG"));
            }
        } else {
            logger.warning(errorBanner("NOT_IN_FOLLOW_MODE"));
            return new Status.Error("Invalid operation. The app is not in FOLLOW Mode.");
        }

        return Status.OK;
    }

    //--------------------------
    // Multi-mode methods
    //--------------------------
    @Override
    public void setLocation(double easting, double northing) {
        this.currentLocation = new Location (easting, northing);
        logger.finer(finerBanner("positionUpdated"));

        if (this.mode == Mode.FOLLOW) {
            Status followStatus = followTour(this.tour.id);
            if (followStatus != Status.OK) {
                logger.warning(errorBanner("SOMETHING_WENT_WRONG"));
            }
        }

    }

    @Override
    public List<Chunk> getOutput() {
        return this.output;
    }


}
