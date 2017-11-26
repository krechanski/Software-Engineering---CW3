
package tourguide;

import java.util.ArrayList;
import java.util.List;
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
        logger.finest("newTourAdded");
        return true;
    }

}
