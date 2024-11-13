package com.LSEG.testCases;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import com.LSEG.utilities.ExtentReportCreator;

public class IdentifyOutliersOfTimeSeriesData {

	static List<Outlier> outliers = new ArrayList<>();
	static List<TimeSeriesData> filteredData = new ArrayList<>();

	@Parameters({ "folderName", "noOfFiles" })
	@Test
	public static void processTheFiles(String folderName, int noOfFiles) throws Exception {
		try {
			ExtentReportCreator.logText("info", "folder name is "+folderName+" and number of files to be processed is "+noOfFiles);
			
			// fetching files from the directory for a particular exchange
			String directoryPath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folderName;
			File directory = new File(directoryPath);
			File[] files = directory.listFiles();
			String[] fileNames = new String[files.length];
			int i = 0;
			if (files != null) {
				for (File file : files) {
					fileNames[i] = file.getName();
					i++;
				}
			}
            ExtentReportCreator.logText("info", "fetched files from the folder are "+Arrays.toString(fileNames));
            
			/*
			 * If there are not enough files present for a given exchange process whatever
			 * number of files are present even if it is lower
			 */
			int loopCount = 0;
			if (noOfFiles > fileNames.length) {
				loopCount = fileNames.length;
			} else {
				loopCount = noOfFiles;
			}

			// process each file based on the count given
			for (int j = 0; j < loopCount; j++) {
				ExtentReportCreator.logText("info","******New File getting processed******");
				String filePath = System.getProperty("user.dir") + "\\src\\test\\resources\\" + folderName + "\\"
						+ fileNames[j];

				// returns exactly 30 consecutive data points starting from a random timestamp
				// within the file
				filteredData = getRandom30ConsecutiveTimeSeriesData(filePath);

				// returns the list of outliers
				outliers = findOutliers(filteredData);

				// store outliers in separate csv files
				writeOutliersToCSV(outliers,
						System.getProperty("user.dir") + "\\target\\" + folderName + "_" + fileNames[j]);

			}
		} catch (Exception e) {
			ExtentReportCreator.logText("fail",e.getMessage());
			throw e;
		}
	}

	// 30 consecutive data points starting from a random timestamp within the file
	public static List<TimeSeriesData> getRandom30ConsecutiveTimeSeriesData(String filePath)
			throws IOException, InvalidCSVFormatException {
		List<TimeSeriesData> allData = new ArrayList<>();

		// Read CSV file
		try (FileReader fileReader = new FileReader(filePath);
				CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT)) {
			int expectedColumnCount = -1;

			// add all data to list
			for (CSVRecord record : csvParser) {
				if (expectedColumnCount == -1) {
					expectedColumnCount = record.size(); // Set the column count from the first row
				} else if (record.size() != expectedColumnCount) {
					throw new InvalidCSVFormatException(
							"Inconsistent column count at line " + record.getRecordNumber());
				}
				String stockID = record.get(0);
				String timestamp = record.get(1);
				double price = Double.parseDouble(record.get(2));
				try {
					TimeSeriesData timeSeriesData = new TimeSeriesData(stockID, timestamp, price);
					allData.add(timeSeriesData);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			ExtentReportCreator.logText("fail", e.getMessage());
		} catch (IOException e) {
			ExtentReportCreator.logText("fail", e.getMessage());
		}
		// generate a random number from 1 to size of list excluding last 29 records
		Random r = new Random();
		int startIndex = r.nextInt(allData.size() - 29);
		ExtentReportCreator.logText("info", "Data is taken from row no "+startIndex+" to "+(startIndex+30));
		
		// take 30 consecutive data
		List<TimeSeriesData> thirtyConsecutivedata = allData.subList(startIndex, startIndex + 30);
		return thirtyConsecutivedata;
	}

	// method to find the Outliers
	public static List<Outlier> findOutliers(List<TimeSeriesData> data) {

		// Calculate mean
		double sum = 0.0;
		for (TimeSeriesData row : data) {
			sum = sum + row.price;
		}
		double mean = sum / data.size();
		ExtentReportCreator.logText("info","Mean of 30 records is "+mean);
		
		// standard deviation
		double squaredSum = 0.0;
		for (TimeSeriesData row : data) {
			squaredSum = squaredSum + Math.pow(row.price - mean, 2);
		}
		double standardDeviation = Math.sqrt(squaredSum / data.size());
		ExtentReportCreator.logText("info","Standard Deviation of 30 records is "+standardDeviation);

		// Identify outliers
		double threshold = 2 * standardDeviation;
		for (TimeSeriesData record : data) {
			double deviation = Math.abs(record.price - mean);
			if (deviation > threshold) {
				double percentDeviation = (deviation / mean) * 100;
				outliers.add(
						new Outlier(record.stockID, record.timestamp, record.price, mean, deviation, percentDeviation));
			}
		}
		return outliers;
	}

	// method to store outlier data in csv file
	public static void writeOutliersToCSV(List<Outlier> outliers, String outputFilePath) throws IOException {
		try {
			FileWriter writer = new FileWriter(outputFilePath);
			CSVPrinter csvPrinter = new CSVPrinter(writer, CSVFormat.DEFAULT);

			// adding headers to the file.
			writer.append(
					"Stock-ID, Timestamp, actual stock price at that timestamp, mean of 30 data points, actual stock price - mean, % deviation over and above the threshold \n");
			ExtentReportCreator.logText("info","Output csv file is created and header is added");
			for (Outlier outlier : outliers) {
				csvPrinter.printRecord(outlier.stockID, outlier.timestamp, outlier.actualPrice, outlier.mean,
						outlier.deviation, outlier.percentDeviation);
			}
			ExtentReportCreator.logText("info","Outliers are listed in output csv file");
			csvPrinter.close();
			writer.close();
		} catch (Exception e) {
			ExtentReportCreator.logText("fail",e.getMessage());
		}
	}

	// Custom exception for invalid CSV format
	@SuppressWarnings("serial")
	static class InvalidCSVFormatException extends Exception {
		public InvalidCSVFormatException(String message) {
			super(message);
		}
	}

}
