# WeatherComparator
A java project that compares weather results from two endpoints using Selenium and Rest-Assured in Java

Pre-requisites:
1. Maven must be configured on test machine.
2. JDK8 must be installed on test machine.

To run the test from cli , the command is:

	mvn test -Dtest=ComparatorTest
	

##### Salient features


1.  The web test is executed using Selenium webdriver on chrome browser. 
2.  The Api test is driven using Rest-Assured
3.  The comparator logic is based on unitary difference. By default , the tolerance limit is set to 3. It can overridden by adding `-Dtolerance=2` in above command 
3.  TestNG framework is used to create test methods and test reports


 

 
