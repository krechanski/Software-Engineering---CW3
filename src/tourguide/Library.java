
package tourguide;

/** The class Library stores all tours created.
 *  @author Hristiyan Yaprakov and Kiril Rechanski
 **/

import java.util.*;
import java.util.logging.Logger;

public class Library {

    public List<Tour> tours;
    private static Logger logger = Logger.getLogger("tourguide");
    private static final String LS = System.lineSeparator();

    private String errorBanner(String messageName) {
        return LS
                + "ERROR: " + " !!!!! " + messageName + " !!!!! ";
    }

    public Library() {

        this.tours = new ArrayList<Tour>();
    }

    /** This method adds a created tour to the list of tours
     *  and sorts that list
     *
     * @return confirmation whether a tour has been 
     *         successfuly added or not.
     */

    public boolean addTour(Tour tour) {
        boolean contains = false;
        for (Tour t : this.tours) {
            if (t.id == tour.id) {
                contains = true;
            }
        }
        if (contains) {
            logger.warning(errorBanner("tourWitIdExists"));
            return false;
        }
        
        this.tours.add(tour);
        Collections.sort(tours, new TourComparator());
        logger.finest("newTourAdded");
        return true;
    }

}
