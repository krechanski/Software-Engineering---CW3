/**
 *
 */
package tourguide;

/** The class ControllerImp implements the overal
 *  functionality of the app.
 *  @author Hristiyan Yaprakov and Kiril Rechanski
 **/

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

public class ControllerImp implements Controller {
    private static Logger logger = Logger.getLogger("tourguide");
    private static final String LS = System.lineSeparator();

    //--------------------------
    // Methods for pretty Logger output
    //--------------------------

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

    public Mode mode;   
    public double waypointRadius;
    public double waypointSeparation;
    public Location currentLocation;
    public Library library;

    // Private helper instances
    private Tour tour;
    private int stage;

    // List to hold all outputs
    private ArrayList<Chunk> output;

    /*
     * Constructor method which takes the necessary parameters 
     * and initiates the necessary instances for the app to start.
     * It also sets the current mode to be in BROWSE mode and
     * puts the app in the BrowseOverview state.
     */
    public ControllerImp(double waypointRadius, double waypointSeparation) {
        this.waypointRadius = waypointRadius;
        this.waypointSeparation = waypointSeparation;
        this.mode = Mode.BROWSE;
        this.library = new Library();
        this.output = new ArrayList<Chunk>();

        Status browseStatus = showToursOverview();
        if (browseStatus != Status.OK) {
            logger.warning(errorBanner("SOMETHING_WENT_WRONG"));
        }
    }

    //--------------------------
    // Create tour mode
    //--------------------------

    /**
     * The creation of a tour is initiated by this method.
     * It sets to mode to FOLLOW and initiates a tour with the given parameters
     * ready to be populated with waypoints and legs.
     * @return the appropriate status and 
     *         output the initiated new tour.
     */
    @Override
    public Status startNewTour(String id, String title, Annotation annotation) {
        logger.fine(startBanner("startNewTour"));

        // Set the mode
        if (this.mode == Mode.BROWSE) {
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

    /**
     * This method adds a waypoint to a new tour.
     * It checks wether the app is in FOLLOW mode and 
     * assesses whether a leg has been added before it and
     * if the current waypoint is separeted enough from the previous one.
     * @return the status and 
     *         output the updated new tour.
     */
    @Override
    public Status addWaypoint(Annotation annotation) {
        logger.fine(startBanner("addWaypoint"));

        if (this.mode == Mode.CREATE) {
            Waypoint waypoint = new Waypoint(annotation, currentLocation);
            int totalWaypoints = this.tour.waypoints.size();
            this.output.clear();
            logger.finer(finerBanner("Entering"));

            // Add an empty leg there isn't a one following the previous waypoint.
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
                // Add an initial waypoint if there are none added.
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

                // Assess the distance between the new waypoint and the previous one.
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

    /**
     * This method adds a leg after a waypoint.
     * It can be executed only in FOLLOW mode.
     * @return the status and 
     *         output the updated tour.
     */
    @Override
    public Status addLeg(Annotation annotation) {
        logger.fine(startBanner("addLeg"));

        if (this.mode == Mode.CREATE) {
            this.output.clear();

            logger.finer(finerBanner("Entering"));

            // Add a default annotation if such a parameter is missing.
            if (annotation == null) {
                annotation = Annotation.DEFAULT;
            }

            Leg leg = new Leg(annotation);

            // Check if there is a waypoint between this leg and the previous one.
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

    /**
     * A new tour is finished with this method.
     * The method assesses whether the tour has a final waypoint or
     * any at all and adds the tour to the library.
     * @return the status and
     *         set the app to be in BrowseOverview state.
     */
    @Override
    public Status endNewTour() {
        logger.fine(startBanner("endNewTour"));

        if (this.mode == Mode.CREATE) {

            // Check if the tour has any waypoints.
            if (this.tour.waypoints.size() > 0) {

                // Check if the tour finishes in a waypoint.
                if (this.tour.legs.size() == this.tour.waypoints.size()) {
                    
                    // Try to add the tour to the library
                    boolean added = this.library.addTour(this.tour);

                    // Check if a tour with this id has not been created yet.
                    if (!added) {
                        logger.warning(errorBanner("newTourNotAdded"));
                        return new Status.Error("A tour with id: '" + this.tour.id + "' already exists.");
                    }
                    logger.finer(finerBanner("newTourAdded"));

                    this.tour = null;
                    logger.finer(finerBanner("newTourFinished"));

                    // Transition to BROWSE mode.
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

    /**
     * A tour's details are given by this method.
     * @return the status and
     *         output the id, title and annotation of a selected tour.
     */
    @Override
    public Status showTourDetails(String tourID) {
        logger.fine(startBanner("showTourDetails"));
        this.output.clear();

        if (this.mode == Mode.BROWSE) {
            for (Tour tour : this.library.tours) {

                // Find a tour with the given id.
                if (tourID.equalsIgnoreCase(tour.id)) {
                    this.tour = tour;
                    this.output.add(new Chunk.BrowseDetails(tour.id, tour.title, tour.annotation));
                }
            }

            // Return an error if a tour with such id has not been found.
            if (this.tour == null) {
                logger.warning(errorBanner("TOUR_NOT_FOUND"));
                return new Status.Error("A Tour with id: '" + tourID + "' has not been found.");
            }

            return Status.OK;
        } else {
            return new Status.Error("Invalid. The app is not in BROWSE mode");
        }
    }

    /**
     * This method defines the initial state that the app is in.
     * @return the status and 
     *         output all created tours in the library.
     */
    @Override
    public Status showToursOverview() {
        logger.fine(startBanner("browseTourOverview"));
        this.output.clear();

        this.mode = Mode.BROWSE;
        Chunk.BrowseOverview overview = new Chunk.BrowseOverview();

        // Retrieve and output all created tours.
        for (Tour tour: this.library.tours) {
           overview.addIdAndTitle(tour.id, tour.title);
        }
        this.output.add(overview);

        return Status.OK;
    }

    //--------------------------
    // Follow tour mode
    //--------------------------

    /**
     * This is the main method to handle the follow tour sequence.
     * @return the status and the output based on the user's location and the stage
     *         that the execution of the tour is in.
     */
    @Override
    public Status followTour(String id) {
        logger.fine(startBanner("followTour"));
        this.output.clear();

        if (this.mode == Mode.BROWSE) {

            // Initiate a tour
            this.mode = Mode.FOLLOW;
            this.stage = 0;

            // Retrieve the selected tour so the user can start following it
            for (int i=0; i < this.library.tours.size(); i++) {
                if (this.library.tours.get(i).id == id) {
                    this.tour = this.library.tours.get(i);
                }
            }

            // Check if the desired tour exist in the library.
            if (this.tour == null) {
                logger.warning(errorBanner("TOUR_NOT_FOUND"));
                return new Status.Error("A Tour with id: '" + id + "' has not been found.");
            }

            // Output information about the tour, what stage it is in,
            // the annotation of the first leg and
            // the bearing and distance to the first waypoint.
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

            // Check if the user is in the radius of the current waypoint.
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

                    // Check if the user has revisited the previous waypoint.
                    if (userNearCurrentWaypoint.distance() <= this.waypointRadius) {
                        onWaypoint = true;
                        whichWaypoint = this.stage-1;
                        logger.finer(finerBanner("prevWayPointReached"));
                    }
                }
            }

            // Output information about the tour, what stage it is in,
            // the annotation of the appropriate waypoint if one has been reached,
            // the annotation of the appropriate leg and
            // the bearing and distance to the next waypoint.
            this.output.add(new Chunk.FollowHeader(
                this.tour.title,
                this.stage,
                this.tour.waypoints.size()
            ));
            // Check if a waypoint has been reached and output it.
            if (this.stage != 0 && onWaypoint) {
                this.output.add(new Chunk.FollowWaypoint(
                    this.tour.waypoints.get(whichWaypoint).annotation
                ));
            }
            // Check if the current stage isn't the last 
            // in order to output the leg, bearing and distance.
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

    /**
     * This method terminates the execution of a follow tour sequence.
     * @return the status and 
     *         sets the app to the BrowseOverview state.
     */
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

    /**
     * The user's current location is updated by this method.
     */
    @Override
    public void setLocation(double easting, double northing) {
        this.currentLocation = new Location (easting, northing);
        logger.finer(finerBanner("positionUpdated"));

        // Advance the follow tour sequence with the 
        // change in the user's current position.
        if (this.mode == Mode.FOLLOW) {
            Status followStatus = followTour(this.tour.id);
            if (followStatus != Status.OK) {
                logger.warning(errorBanner("SOMETHING_WENT_WRONG"));
            }
        }

    }

    /**
     * This method returns the output of the app and
     * can be called at any time.
     */
    @Override
    public List<Chunk> getOutput() {
        return this.output;
    }


}
