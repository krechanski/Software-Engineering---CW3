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


    public ControllerImp(double waypointRadius, double waypointSeparation) {
        this.waypointRadius = waypointRadius;
        this.waypointSeparation = waypointSeparation;
        this.mode = Mode.BROWSE;
    }

    //--------------------------
    // Create tour mode
    //--------------------------

    // Some examples are shown below of use of logger calls.  The rest of the methods below that correspond
    // to input messages could do with similar calls.

    @Override
    public Status startNewTour(String id, String title, Annotation annotation) {
        logger.fine(startBanner("startNewTour"));
        return new Status.Error("unimplemented");
    }

    @Override
    public Status addWaypoint(Annotation annotation) {
        logger.fine(startBanner("addWaypoint"));
        return new Status.Error("unimplemented");
    }

    @Override
    public Status addLeg(Annotation annotation) {
        logger.fine(startBanner("addLeg"));
        return new Status.Error("unimplemented");
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
        return new Status.Error("unimplemented");
    }

    @Override
    public Status showToursOverview() {
        return new Status.Error("unimplemented");
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
    }

    @Override
    public List<Chunk> getOutput() {
        return new ArrayList<Chunk>();
    }


}
