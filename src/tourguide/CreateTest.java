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
public class CreateTest {

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

    private void createOnePointTourWithoutLeg() {

        checkStatus( controller.startNewTour(
                "T1",
                "Informatics at UoE",
                ann("The Informatics Forum and Appleton Tower\n"))
            );

        checkOutput(1, 0, new Chunk.CreateHeader("Informatics at UoE", 0,  0));

        controller.setLocation(300, -500);

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

    private void createTwoTours() {

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

        checkStatusNotOK( controller.startNewTour(
                "T4",
                "Another Quick Royal Mile Walk",
                ann("A stroll down the famous Royal Mile\n"))
            );

        checkStatus( controller.endNewTour() );

    }

    private void createMultipleInvalidWaypoints() {

        checkStatusNotOK( controller.addWaypoint(ann("Greyfriars Bobby")) );

        checkStatus( controller.startNewTour(
                "T0",
                "Spooky Greyfriars",
                ann("A haunted exploration of the most famous graveyard.\n"))
            );
        checkOutput(1, 0, new Chunk.CreateHeader("Spooky Greyfriars", 0,  0));

        controller.setLocation(500, 500);

        checkStatus( controller.addWaypoint(ann("Greyfriars Bobby")) );
        checkOutput(1, 0, new Chunk.CreateHeader("Spooky Greyfriars", 1,  1));

        controller.setLocation(500, 500);
        checkStatusNotOK( controller.addWaypoint(ann("Greyfriars Bobby Pub")) );

        controller.setLocation(500, 525);
        checkStatus( controller.addWaypoint(ann("Greyfriars Bobby Pub")) );

        controller.setLocation(510, 530);
        checkStatusNotOK( controller.addWaypoint(ann("Greyfriars Bobby Graveyard Gate")) );

        controller.setLocation(500, 500);
        checkStatus( controller.addWaypoint(ann("Greyfriars Bobby Graveyard Gate")) );

        checkStatus( controller.endNewTour() );

        controller.setLocation(600, 500);
        checkStatusNotOK( controller.addWaypoint(ann("Greyfriars Bobby Church")) );

    }

    private void createMultipleInvalidLegs() {

        checkStatusNotOK( controller.addLeg(ann("Start from Greyfriars Bobby")) );

        checkStatus( controller.startNewTour(
                "T0",
                "Spooky Greyfriars",
                ann("A haunted exploration of the most famous graveyard.\n"))
            );
        checkOutput(1, 0, new Chunk.CreateHeader("Spooky Greyfriars", 0,  0));

        controller.setLocation(500, 500);

        checkStatus( controller.addLeg(ann("Start from Greyfriars Bobby")) );
        checkOutput(1, 0, new Chunk.CreateHeader("Spooky Greyfriars", 1,  0));

        checkStatus( controller.addWaypoint(ann("Greyfriars Bobby")) );
        checkOutput(1, 0, new Chunk.CreateHeader("Spooky Greyfriars", 1,  1));

        controller.setLocation(300, 200);
        checkStatus( controller.addLeg(ann("Proceed through the gates.")) );

        controller.setLocation(500, 300);
        checkStatusNotOK( controller.addLeg(ann("Proceed even further.")) );

        checkStatus( controller.addWaypoint(ann("Greyfriars Bobby")) );
        checkOutput(1, 0, new Chunk.CreateHeader("Spooky Greyfriars", 2,  2));

        checkStatus( controller.endNewTour() );

        controller.setLocation(-400, 1200);
        checkStatusNotOK( controller.addLeg(ann("End outside Greyfriars Bobby")) );

    }

    private void invalidFinishes() {

        checkStatusNotOK( controller.endNewTour() );

        checkStatus( controller.startNewTour(
                "T0",
                "Spooky Greyfriars",
                ann("A haunted exploration of the most famous graveyard.\n"))
            );
        checkOutput(1, 0, new Chunk.CreateHeader("Spooky Greyfriars", 0,  0));

        checkStatusNotOK( controller.endNewTour() );

        controller.setLocation(500, 500);
        checkStatus( controller.addWaypoint(ann("Greyfriars Bobby")) );

        controller.setLocation(700, 700);
        checkStatus( controller.addLeg(ann("Go through Greyfriars Bobby")) );

        checkStatusNotOK( controller.endNewTour() );

        checkStatus( controller.addWaypoint(ann("Greyfriars Bobby")) );

        checkStatus( controller.endNewTour() );

        checkStatusNotOK( controller.endNewTour() );

    }

    @Test
    public void testCreateOnePointTour() {
        logger.info(makeBanner("testCreateOnePointTour"));
        createOnePointTour();
    }

    @Test
    public void testCreateOnePointTourWithoutLeg() {
        logger.info(makeBanner("testCreateOnePointTourWithoutLeg"));
        createOnePointTourWithoutLeg();
    }

    @Test
    public void testCreateThreePointTourWithoutLegs() {
        logger.info(makeBanner("testCreateThreePointTourWithoutLegs"));
        createThreePointTourWithoutLegs();
    }

    @Test
    public void testCreateFourPointTour() {
        logger.info(makeBanner("testCreateFourPointTour"));
        createFourPointTour();
    }

    @Test
    public void testCreateTwoTours() {
        logger.info(makeBanner("testCreateTwoTours"));
        createTwoTours();
    }
    
    @Test
    public void testCreateMultipleInvalidWaypoints() {
        logger.info(makeBanner("testCreateMultipleInvalidWaypoints"));
        createMultipleInvalidWaypoints();
    }

    @Test
    public void testCreateMultipleInvalidLegs() {
        logger.info(makeBanner("testCreateMultipleInvalidLegs"));
        createMultipleInvalidLegs();
    }

    @Test
    public void testInvalidFinishes() {
        logger.info(makeBanner("testInvalidFinishes"));
        invalidFinishes();
    }


}
