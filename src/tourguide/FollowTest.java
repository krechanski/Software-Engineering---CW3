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
public class FollowTest {

    private Controller controller;
    private static final double WAYPOINT_RADIUS = 10.0;
    private static final double WAYPOINT_SEPARATION = 25.0;

    // Utility methods to help shorten test text.
    private static Annotation ann(String s) { return new Annotation(s); }
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
        return  LS + LS
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

        checkStatus( controller.startNewTour(
                "T1",
                "Informatics at UoE",
                ann("The Informatics Forum and Appleton Tower\n"))
            );

        checkOutput(1, 0, new Chunk.CreateHeader("Informatics at UoE", 0,  0));

        controller.setLocation(300, -500);

        checkStatus( controller.addLeg(ann("Start at NE corner of George Square\n")) );

        checkOutput(1, 0, new Chunk.CreateHeader("Informatics at UoE", 1,  0));

        checkStatus( controller.addWaypoint(ann("Informatics Forum")) );

        checkOutput(1, 0, new Chunk.CreateHeader("Informatics at UoE", 1,  1));

        checkStatus( controller.endNewTour() );

    }

    private void createFourPointTour() {

        checkStatus( controller.startNewTour(
                "T2",
                "Royal Mile Walk",
                ann("A stroll down the famous Royal Mile\n"))
            );
        checkOutput(1, 0, new Chunk.CreateHeader("Royal Mile Walk", 0,  0));

        controller.setLocation(700, -900);

        checkStatus( controller.addLeg(ann("Start from Edinburgh Castle.\n")) );
        checkOutput(1, 0, new Chunk.CreateHeader("Royal Mile Walk", 1,  0));

        checkStatus( controller.addWaypoint(ann("Edinburgh Castle")) );
        checkOutput(1, 0, new Chunk.CreateHeader("Royal Mile Walk", 1,  1));

        controller.setLocation(400, -600);

        checkStatus( controller.addLeg(ann("Go down to the famous illusion museum.\n")) );
        checkOutput(1, 0, new Chunk.CreateHeader("Royal Mile Walk", 2,  1));

        checkStatus( controller.addWaypoint(ann("Camera Obscura")) );
        checkOutput(1, 0, new Chunk.CreateHeader("Royal Mile Walk", 2,  2));

        controller.setLocation(100, -300);

        checkStatus( controller.addLeg(ann("Check the fancy shops and stop by to taste the finest whiskies.\n")) );
        checkOutput(1, 0, new Chunk.CreateHeader("Royal Mile Walk", 3,  2));

        checkStatus( controller.addWaypoint(ann("Whisky Museum")) );
        checkOutput(1, 0, new Chunk.CreateHeader("Royal Mile Walk", 3,  3));

        controller.setLocation(0, 0);

        checkStatus( controller.addLeg(ann("Stop by to cleanse your sins after drinking that 4 glasses.\n")) );
        checkOutput(1, 0, new Chunk.CreateHeader("Royal Mile Walk", 4,  3));

        checkStatus( controller.addWaypoint(ann("St Giles Cathedral")) );
        checkOutput(1, 0, new Chunk.CreateHeader("Royal Mile Walk", 4,  4));

        checkStatus( controller.endNewTour() );

    }

    private void createThreePointTourWithoutLegs() {

        checkStatus( controller.startNewTour(
                "T3",
                "Quick Royal Mile Walk",
                ann("A stroll down the famous Royal Mile\n"))
            );
        checkOutput(1, 0, new Chunk.CreateHeader("Quick Royal Mile Walk", 0,  0));

        controller.setLocation(700, -900);

        checkStatus( controller.addWaypoint(ann("Edinburgh Castle")) );
        checkOutput(1, 0, new Chunk.CreateHeader("Quick Royal Mile Walk", 1,  1));

        controller.setLocation(400, -600);

        checkStatus( controller.addWaypoint(ann("Camera Obscura")) );
        checkOutput(1, 0, new Chunk.CreateHeader("Quick Royal Mile Walk", 2,  2));

        controller.setLocation(100, -300);

        checkStatus( controller.addWaypoint(ann("Whisky Museum")) );
        checkOutput(1, 0, new Chunk.CreateHeader("Quick Royal Mile Walk", 3,  3));

        checkStatus( controller.endNewTour() );

    }

    private void followFourPointTour() {

        createOnePointTour();
        createFourPointTour();
        createThreePointTourWithoutLegs();

        checkStatus( controller.followTour("T2") );

        controller.setLocation(1000, -1000);

        checkOutput(3,0, new Chunk.FollowHeader("Royal Mile Walk", 0, 4) );
        checkOutput(3,1, new Chunk.FollowLeg(ann("Start from Edinburgh Castle.\n")));
        checkOutput(3,2, new Chunk.FollowBearing(288.0, 316.0));

        controller.setLocation(700, -900);

        checkOutput(4,0, new Chunk.FollowHeader("Royal Mile Walk", 1, 4) );
        checkOutput(4,1, new Chunk.FollowWaypoint(ann("Edinburgh Castle")));
        checkOutput(4,2, new Chunk.FollowLeg(ann("Go down to the famous illusion museum.\n")));
        checkOutput(4,3, new Chunk.FollowBearing(315, 424));

        controller.setLocation(500, -700);

        checkOutput(3,0, new Chunk.FollowHeader("Royal Mile Walk", 1, 4) );
        checkOutput(3,1, new Chunk.FollowLeg(ann("Go down to the famous illusion museum.\n")));
        checkOutput(3,2, new Chunk.FollowBearing(315, 141));

        controller.setLocation(400, -590);

        checkOutput(4,0, new Chunk.FollowHeader("Royal Mile Walk", 2, 4) );
        checkOutput(4,1, new Chunk.FollowWaypoint(ann("Camera Obscura")));
        checkOutput(4,2, new Chunk.FollowLeg(ann("Check the fancy shops and stop by to taste the finest whiskies.\n")));
        checkOutput(4,3, new Chunk.FollowBearing(314, 417));

        controller.setLocation(100, -615);

        checkOutput(3,0, new Chunk.FollowHeader("Royal Mile Walk", 2, 4) );
        checkOutput(3,1, new Chunk.FollowLeg(ann("Check the fancy shops and stop by to taste the finest whiskies.\n")));
        checkOutput(3,2, new Chunk.FollowBearing(0, 315));

        controller.setLocation(105, -305);

        checkOutput(4,0, new Chunk.FollowHeader("Royal Mile Walk", 3, 4) );
        checkOutput(4,1, new Chunk.FollowWaypoint(ann("Whisky Museum")));
        checkOutput(4,2, new Chunk.FollowLeg(ann("Stop by to cleanse your sins after drinking that 4 glasses.\n")));
        checkOutput(4,3, new Chunk.FollowBearing(341, 323));

        controller.setLocation(0, 0);

        checkOutput(2,0, new Chunk.FollowHeader("Royal Mile Walk", 4, 4) );
        checkOutput(2,1, new Chunk.FollowWaypoint(ann("St Giles Cathedral")));

        controller.endSelectedTour();

        Chunk.BrowseOverview overview = new Chunk.BrowseOverview();
        overview.addIdAndTitle("T1", "Informatics at UoE");
        overview.addIdAndTitle("T2", "Royal Mile Walk");
        overview.addIdAndTitle("T3", "Quick Royal Mile Walk");
        checkOutput(1, 0, overview);

    }

    @Test
    public void testFollowFourPointTour() {
        logger.info(makeBanner("testFollowFourPointTour"));
        followFourPointTour();
    }

    @Test
    public void testCorrectMode() {
        logger.info(makeBanner("testCorrectMode"));

        checkStatus( controller.startNewTour(
                "T1",
                "Informatics at UoE",
                ann("The Informatics Forum and Appleton Tower\n"))
            );

        checkStatusNotOK( controller.followTour("T2") );
    }

    @Test
    public void testNonExistent() {
        logger.info(makeBanner("testNonExistent"));

        createOnePointTour();
        createFourPointTour();
        createThreePointTourWithoutLegs();

        checkStatusNotOK( controller.followTour("T4") );
    }



}
