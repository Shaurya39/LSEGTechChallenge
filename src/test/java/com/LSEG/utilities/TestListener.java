package com.LSEG.utilities;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import com.aventstack.extentreports.Status;

public class TestListener implements ITestListener {

	private static String getTestMethodName(ITestResult iTestResult) {
		return iTestResult.getMethod().getConstructorOrMethod().getName();
	}

	@Override
	public void onStart(ITestContext iTestContext) {
		System.out.println("I am in onStart method " + iTestContext.getName());
	}

	@Override
	public void onFinish(ITestContext iTestContext) {
		System.out.println("I am in onFinish method " + iTestContext.getName());
		Reporting.getInstance().flush();
	}

	@Override
	public void onTestStart(ITestResult iTestResult) {
		System.out.println(iTestResult.getTestName());
		String description = iTestResult.getMethod().getDescription();
		System.out.println("I am in onTestSuccess method " + getTestMethodName(iTestResult) + " succeed");
		if (iTestResult.getTestName() != null) {
			ExtentReportCreator.startTest(iTestResult.getTestName(),
					iTestResult.getInstance().getClass().getCanonicalName());
		}else if (description != null)
			ExtentReportCreator.startTest(iTestResult.getMethod().getMethodName() + "( " + description + ")",
					iTestResult.getInstance().getClass().getCanonicalName());
		else {
			ExtentReportCreator.startTest(iTestResult.getMethod().getMethodName(),
					iTestResult.getInstance().getClass().getCanonicalName());
		}
		System.out.println("I am in onTestStart method " + getTestMethodName(iTestResult) + " start");
	}

	@Override
	public void onTestSuccess(ITestResult iTestResult) {
		ExtentReportCreator.getTest().log(Status.PASS, "Test passed");
	}

	@Override
	public void onTestFailure(ITestResult iTestResult) {
		System.out.println("I am in onTestFailure method " + getTestMethodName(iTestResult) + " failed");
		ExtentReportCreator.getTest().log(Status.FAIL, iTestResult.getThrowable());
	}

	@Override
	public void onTestSkipped(ITestResult iTestResult) {
		System.out.println("I am in onTestSkipped method " + getTestMethodName(iTestResult) + " skipped");
		ExtentReportCreator.getTest().log(Status.SKIP, "Test Skipped");
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult iTestResult) {
		System.out.println("Test failed but it is in defined success ratio " + getTestMethodName(iTestResult));
	}
}