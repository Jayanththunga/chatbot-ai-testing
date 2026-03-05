package utility;

import com.aventstack.extentreports.Status;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListeners implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        ExtentManager.createTest(
              result.getTestClass().getName() + " :: " + result.getMethod().getMethodName()
        );
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        if (ExtentManager.getTest() != null) {
            ExtentManager.getTest().log(Status.PASS, "Test passed");
            ExtentManager.removeTest();
        }
    }

    @Override
    public void onTestFailure(ITestResult result) {
        if (ExtentManager.getTest() != null) {
            ExtentManager.getTest().log(Status.FAIL, result.getThrowable());
            ExtentManager.removeTest();
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (ExtentManager.getTest() != null) {
            ExtentManager.getTest().log(Status.SKIP, "Test skipped");
            ExtentManager.removeTest();
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        ExtentManager.flushReports();
    }
}
