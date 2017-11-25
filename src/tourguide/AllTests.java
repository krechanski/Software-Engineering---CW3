/**
 *
 */
package tourguide;

import java.util.logging.Level;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * @author pbj
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ DisplacementTest.class, ControllerTest.class, TestTest.class})
public class AllTests {

    public static void main(String[] args) {
        String test = "all";
        if (args.length > 2){
            System.err.println("Unrecognised arguments");
            return;
        } else if (args.length == 1 || args.length == 2) {
            String loggingLevel = args[0];
            if (loggingLevel.equals("off")) {
                ControllerTest.loggingLevel = Level.OFF;
           } else if (loggingLevel.equals("info")) {
                ControllerTest.loggingLevel = Level.INFO;
            } else if (loggingLevel.equals("fine")) {
                ControllerTest.loggingLevel = Level.FINE;
            } else if (loggingLevel.equals("finer")) {
                ControllerTest.loggingLevel = Level.FINER;
            } else {
                System.err.println("Unrecognised logging level argument: " + loggingLevel);
                return;
            }
        }
        if (args.length == 2) {
            test = args[1];
        }
        runJUnitTests(test);
    }

    public static void runJUnitTests(String test) {
        Result result;
        switch (test) {
            case "all":
                result = JUnitCore.runClasses(AllTests.class);
                break;
            case "test":
                result = JUnitCore.runClasses(TestTest.class);
                break;
            case "create":
                result = JUnitCore.runClasses(CreateTest.class);
                break;
            default:
                result = JUnitCore.runClasses(AllTests.class);
        }

        System.out.println("TEST RESULTS");
        System.out.println("Number of tests run: " + result.getRunCount());
        if (result.wasSuccessful()) {
            System.out.println("ALL TESTS PASSED");
        } else {
            System.out.println("SOME TESTS FAILED");
            System.out.println("Number of failed tests: " + result.getFailureCount());
            for (Failure failure : result.getFailures()) {
                System.out.println(failure.toString());
            }
        }
    }


}
