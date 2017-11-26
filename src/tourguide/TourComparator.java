package tourguide;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

public class TourComparator implements Comparator<Tour> {

    @Override
    public int compare(Tour o1, Tour o2) {
        return o1.id.compareTo(o2.id);
    }
}