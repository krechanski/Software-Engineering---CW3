package tourguide;

import tourguide.Annotation;

import java.util.logging.Logger;

public class Leg {

    private static Logger logger = Logger.getLogger("tourguide");

    public Annotation note;


    public Leg (Annotation note) {
        this.note = note;
    }



}
