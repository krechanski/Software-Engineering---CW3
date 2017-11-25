//package tourguide;
//import java.util.logging.*;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//
//
//public class BrowseTourTests {
//
//
//      @Test
//      public void checkBrowseTourMode() {
//          checkStatus(controller.Mode("BROWSE_OVERVIEW");
//      }
//
//     @Test
//     public void browseOneTour() {
//       logger.info(makeBanner("browseOneTour"));
//       checkStatus(controller.showTourDetails("Edinburgh"));
//
//       addOnePointTour();
//
//
//       Chunk.BrowseOverview overview = new Chunk.BrowseOverview();
//       overview.addIdAndTitle("T1", "Burgas Bate");
//     }
//
//     @Test
//     public void noTours() {
//         logger.info(makeBanner("noTours"));
//         checkOutput(1, 0, new Chunk.BrowseOverview() );
//      }
//
//      @Test
//      public void listTours() {
//        logger.info(makeBanner("listTours"));
//      }
//
//
//
//}
