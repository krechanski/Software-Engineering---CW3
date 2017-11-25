
package tourguide;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Library {

    public List<Tour> tours;
    private static Logger logger = Logger.getLogger("tourguide");
    

    public Library() {
        this.tours = new ArrayList<Tour>();
    }

    public void addTour(Tour tour) {
        this.tours.add(tour);
        logger.finest("newTourAdded");
    }

}
