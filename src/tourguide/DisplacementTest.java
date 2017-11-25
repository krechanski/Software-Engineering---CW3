/**
 * 
 */
package tourguide;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author pbj
 */
public class DisplacementTest {
    /**
     * EPS = Epsilon, the difference to allow in floating point numbers when 
     * comparing them for equality.
     */
    private static final double EPS = 0.01; 
    
    @Test
    public void testNorthBearing() {
        double bearing = new Displacement(0.0, 1.0).bearing();
        assertEquals(0.0, bearing, EPS);
    }

    @Test
    public void testEastBearing() {
        double bearing = new Displacement(1.0, 0.0).bearing();
        assertEquals(90, bearing, EPS);
    }


    @Test
    public void testDistance () {
        double distance = new Displacement(2.0, 2.0).distance();
        assertEquals(Math.sqrt(8), distance, EPS);
    }

    @Test
    public void testDistanceNegativeX() {
        double bearing = new Displacement(-50.0, 54.465).bearing();
        assertEquals(317.44783935, bearing, EPS);
    }

    @Test
    public void testDistanceNegativeY() {
        double bearing = new Displacement(23.21, -35.75).bearing();
        assertEquals(147.0071265878387, bearing, EPS);
    }

    @Test
    public void testOrigin() {
        double bearing = new Displacement(0,0).bearing();
        assertEquals(0, bearing, EPS);
    }


 
}
