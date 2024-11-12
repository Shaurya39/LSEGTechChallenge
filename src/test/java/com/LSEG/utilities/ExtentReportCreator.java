package com.LSEG.utilities;
import java.util.HashMap;
import java.util.Map;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

public class ExtentReportCreator {

	static Map<Integer, ExtentTest> extentTestMap = new HashMap<Integer, ExtentTest>();
	static ExtentReports extent = Reporting.getInstance();

	public static synchronized ExtentTest getTest() {
		return (ExtentTest) extentTestMap.get((int) (long) (Thread.currentThread().getId()));
	}
	
	public static synchronized ExtentTest startTest(String testName, String className) {
		ExtentTest test = extent.createTest(testName, className);
		extentTestMap.put((int) (long) (Thread.currentThread().getId()), test);
		test.assignAuthor("Shaurya");
		test.assignCategory(className);	
		return test;
	}
	
	public static synchronized void logText(String passOrFail, String message) {
		if(passOrFail.equalsIgnoreCase("pass"))
		{
			getTest().log(Status.PASS, message);
		}
		else if(passOrFail.equalsIgnoreCase("fail"))
		{
			getTest().log(Status.FAIL, message);
		}
		else
		{
			getTest().log(Status.INFO, message);
		}
	}
}
