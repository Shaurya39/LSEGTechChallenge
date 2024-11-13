Project to spot possible errors or “outliers” in the price data provided for a number of different global “Exchanges”. For each stock exchange, specified number of stock files can be selected and for each file provided, list of outliers present in that specific file is filtered and stored in the output file.

Framework - TestNG, Build Management tool - Maven

Methods to set up and run the project
1. Using maven command line
   
   a) install maven
   
   b) open command prompt and go to project location by command ->  cd project location.
   
   c) mvn clean test - use this command to run the project.
   
3. Using Eclipse IDE


   a) Download and import the project in eclipse.
   
   b) 3 Folders of different exchanges are present containing different files in each.
   
   c) In TestNG.xml we can change the parameter "folderName" out of any of the 3 folders available in project and "noOfFiles" as per our wish.
   
   d) Run the TestNG.xml file as TestNG Suite.
   
   e) Output files and Report will get generated in folder named "target".


Input -> Folder Name and Number of files to be processed are passed as parameters.
   
Output -> Outliers details are stored in separate csv files in folder named "target" and extent report is also generated under same folder.

Note - If there are no outliers found in the randomly selected 30 data points then output csv file will have no records stored, it will just have the header row.
