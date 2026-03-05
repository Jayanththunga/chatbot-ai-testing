package utility;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

public class ExtentManager {
    private static final ExtentReports extent;

    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<>();

    static {
        ExtentSparkReporter spark = new ExtentSparkReporter("./testOutput/Report.html");
        extent = new ExtentReports();
        extent.attachReporter(spark);
    }

    public static ExtentReports getExtentReports() {
        return extent;
    }

    public static ExtentTest createTest(String testName) {
        ExtentTest extentTest = extent.createTest(testName);
        test.set(extentTest);
        return extentTest;
    }

    public static ExtentTest getTest() {
        return test.get();
    }

    public static void removeTest() {
        test.remove();
    }

    public static void flushReports() {
        if (extent != null) {
            extent.flush();
        }
    }
}
