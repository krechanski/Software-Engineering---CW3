package tourguide;

import java.util.logging.Logger;

public class Leg {

    private static Logger logger = Logger.getLogger("tourguide");

    public Annotation annotation;


    public Leg (Annotation annotation) {
        this.annotation = annotation;
    }
}
