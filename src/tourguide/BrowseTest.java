package tourguide;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author pbj
 *
 */
public class BrowseTest {

    private Controller controller;
    private static final double WAYPOINT_RADIUS = 10.0;
    private static final double WAYPOINT_SEPARATION = 25.0;

    // Utility methods to help shorten test text.
    private static Annotation ann(String s) {
        return new Annotation(s);
    }

    private static void checkStatus(Status status) {
        Assert.assertEquals(Status.OK, status);
    }

    private static void checkStatusNotOK(Status status) {
        Assert.assertNotEquals(Status.OK, status);
    }

    private void checkOutput(int numChunksExpected, int chunkNum, Chunk expected) {
        List<Chunk> output = controller.getOutput();
        Assert.assertEquals("Number of chunks", numChunksExpected, output.size());
        Chunk actual = output.get(chunkNum);
        Assert.assertEquals(expected, actual);
    }

    /*
     * Logging functionality
     */
    // Convenience field.  Saves on getLogger() calls when logger object needed.
    private static Logger logger;
    // Update this field to limit logging.
    public static Level loggingLevel = Level.ALL;
    private static final String LS = System.lineSeparator();

    @BeforeClass
    public static void setupLogger() {

        logger = Logger.getLogger("tourguide");
        logger.setLevel(loggingLevel);

        // Ensure the root handler passes on all messages at loggingLevel and above (i.e. more severe)
        Logger rootLogger = Logger.getLogger("");
        Handler handler = rootLogger.getHandlers()[0];
        handler.setLevel(loggingLevel);
    }

    private String makeBanner(String testCaseName) {
        return LS + LS
                + "#############################################################" + LS
                + "TESTCASE: " + testCaseName + LS
                + "#############################################################" + LS;
    }

    @Before
    public void setup() {
        controller = new ControllerImp(WAYPOINT_RADIUS, WAYPOINT_SEPARATION);
    }

    // Locations roughly based on St Giles Cathedral reference.

    private void createOnePointTour() {

        checkStatus(controller.startNewTour(
                "T1",
                "Informatics at UoE",
                ann("The Informatics Forum and Appleton Tower\n"))
        );

        checkOutput(1, 0, new Chunk.CreateHeader("Informatics at UoE", 0, 0));

        controller.setLocation(300, -500);

        checkStatus(controller.addLeg(ann("Start at NE corner of George Square\n")));

        checkOutput(1, 0, new Chunk.CreateHeader("Informatics at UoE", 1, 0));

        checkStatus(controller.addWaypoint(ann("Informatics Forum")));

        checkOutput(1, 0, new Chunk.CreateHeader("Informatics at UoE", 1, 1));

        checkStatus(controller.endNewTour());

    }

    @Test
    public void testCreateOnePointTour() {
        logger.info(makeBanner("testCreateOnePointTour"));
        createOnePointTour();
    }

    private void addTwoPointTour() {
        checkStatus(
                controller.startNewTour("T2", "Old Town", ann("From Edinburgh Castle to Holyrood\n"))
        );

        checkOutput(1, 0, new Chunk.CreateHeader("Old Town", 0, 0));

        controller.setLocation(-500, 0);

        // Leg before this waypoint with default annotation added at same time
        checkStatus(controller.addWaypoint(ann("Edinburgh Castle\n")));

        checkOutput(1, 0, new Chunk.CreateHeader("Old Town", 1, 1));

        checkStatus(controller.addLeg(ann("Royal Mile\n")));

        checkOutput(1, 0, new Chunk.CreateHeader("Old Town", 2, 1));

        checkStatusNotOK(
                controller.endNewTour()
        );

        controller.setLocation(1000, 300);

        checkStatus(controller.addWaypoint(ann("Holyrood Palace\n")));

        checkOutput(1, 0, new Chunk.CreateHeader("Old Town", 2, 2));

        checkStatus(controller.endNewTour());

    }

    private void addOnePointTour() {

        checkStatus(controller.startNewTour(
                "T1",
                "Informatics at UoE",
                ann("The Informatics Forum and Appleton Tower\n"))
        );

        checkOutput(1, 0, new Chunk.CreateHeader("Informatics at UoE", 0, 0));

        controller.setLocation(300, -500);

        checkStatus(controller.addLeg(ann("Start at NE corner of George Square\n")));

        checkOutput(1, 0, new Chunk.CreateHeader("Informatics at UoE", 1, 0));

        checkStatus(controller.addWaypoint(ann("Informatics Forum")));

        checkOutput(1, 0, new Chunk.CreateHeader("Informatics at UoE", 1, 1));

        checkStatus(controller.endNewTour());

    }

    @Test
    public void browseOneTour() {
        logger.info(makeBanner("browseOneTour"));
        addOnePointTour();
        Chunk.BrowseOverview overview = new Chunk.BrowseOverview();
        overview.addIdAndTitle("T1", "Royal Mile");
        checkStatus(controller.showTourDetails("T1"));


    }

    @Test
    public void browsingTwoTours() {
        logger.info(makeBanner("browsingTwoTours"));

        addOnePointTour();
        addTwoPointTour();

        Chunk.BrowseOverview overview = new Chunk.BrowseOverview();
        overview.addIdAndTitle("T1", "Informatics at UoE");
        overview.addIdAndTitle("T2", "Old Town");
        checkOutput(1, 0, overview);

        checkStatusNotOK(controller.showTourDetails("T3"));
        checkStatus(controller.showTourDetails("T1"));

        checkOutput(1, 0, new Chunk.BrowseDetails(
                "T1",
                "Informatics at UoE",
                ann("The Informatics Forum and Appleton Tower\n")
        ));
    }

    @Test
    public void noTours() {
        logger.info(makeBanner("noTours"));
        checkOutput(1, 0, new Chunk.BrowseOverview());
    }

    @Test
    public void testAddTwoPointTour () {
        logger.info(makeBanner("testAddTwoPointTour"));
        addTwoPointTour();

    }

    @Test
    public void testAddOfTwoTours () {
        logger.info(makeBanner("testAddOfTwoTour"));

        addOnePointTour();
        addTwoPointTour();
    }

    @Test
    public void listTours() {
        logger.info(makeBanner("listTours"));

    }
}


