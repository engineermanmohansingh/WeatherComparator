package comp;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.Reporter;
import org.testng.TestNGException;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ComparatorTest {

	String chromeDriverPath, tempFromApi, tempFromWeb;
	String os;
	WebDriver driver;
	WebDriverWait wait;
	int compTolerance;
	@BeforeClass
	public void setup() {
		compTolerance= Integer.valueOf(System.getProperty("tolerance","3"));
		os = System.getProperty("os.name");
		if(os.toLowerCase().contains("mac")) {
		chromeDriverPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main"
				+ File.separator + "resources" + File.separator + "Drivers" + File.separator + "Mac" + File.separator
				+ "chromedriver";
		}else if(os.toLowerCase().contains("windows")) {
			chromeDriverPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main"
					+ File.separator + "resources" + File.separator + "Drivers" + File.separator + "Windows" + File.separator
					+ "chromedriver.exe";
			
		}else if(os.toLowerCase().contains("linux")) {
			chromeDriverPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main"
					+ File.separator + "resources" + File.separator + "Drivers" + File.separator + "Linux" + File.separator
					+ "chromedriver";
			
		}
		System.setProperty("webdriver.chrome.driver", chromeDriverPath);
		ChromeOptions options = new ChromeOptions();

		options.addArguments("--disable-extensions");
		options.addArguments("test-type");
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-dev-shm-usage");
		driver = new ChromeDriver(options);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		wait = new WebDriverWait(driver, 30);
	}

	@Test
	public void TC01_getWeatherDataFromWeb() {

		driver.get("https://weather.com");
		Reporter.log("Navigated to weather.com website", true);

		wait.until(ExpectedConditions.elementToBeClickable(By.id("LocationSearch_input")));
		WebElement searchField = driver.findElement(By.id("LocationSearch_input"));
		searchField.sendKeys("Delhi");
		Reporter.log("Entered city Delhi in Location Search Field", true);

		wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Delhi']")));
		driver.findElement(By.xpath("//button[text()='Delhi']")).click();

		tempFromWeb = driver.findElement(By.xpath(
				"(//h1[contains(.,'Delhi')]/parent::*/following-sibling::*//span[@data-testid='TemperatureValue'])[1]"))
				.getText().trim();
		Assert.assertTrue(driver.findElement(By.tagName("h1")).getText().contains("Delhi"),
				"The result page is not showing expected City name");

		System.out.println(tempFromWeb);

	}

	@Test
	public void TC02_getWeatherDataFromApi() {

		RequestSpecification requestObj = RestAssured.given();
		requestObj.baseUri("https://api.openweathermap.org/");
		requestObj.basePath("data/2.5/weather");
		requestObj.queryParam("q", "Delhi,in");
		requestObj.queryParam("APPID", "7fe67bf08c80ded756e598d6f8fedaea");
		requestObj.queryParam("units", "metric");
		Reporter.log("Request details :- ",true);
		Reporter.log(requestObj.log().all().toString(),true);
		requestObj.log().all();
		Response response = requestObj.get();
		System.out.println(response.body().asString());
		System.out.println(response.jsonPath().get("main.temp").toString());
		Reporter.log("Response retreived :-");
		Reporter.log(response.body().prettyPrint(),true);
		tempFromApi = response.jsonPath().get("main.temp").toString();

	}

	@Test
	public void TC03_compareDataFromTwoSources() {

		Reporter.log("Comparing two temperature values");
		System.out.println("Temperature extracted from API ="+tempFromApi +" ~ ? ~ Temperature extracted from Web ="+tempFromWeb);
		float tempA = Float.valueOf(tempFromApi);
		float tempW = Float.valueOf(tempFromWeb.substring(0,tempFromWeb.length()-1));
		if((tempA-tempW)==0) {
			Reporter.log("Two temperature values match exactly");
		}else {
			float res = Math.abs(tempA-tempW) ;
			if(res<compTolerance) {
				Reporter.log("Two values match within the tolerance range, unit difference observed -"+res+" and tolerance specified -"+compTolerance,true);
			}else {
				throw new TestNGException("Two values do not match within the tolerance range, unit difference observed -"+res+" and tolerance specified -"+compTolerance);
			}
		}
		
		
		
	}

	@AfterClass
	public void tearDown() {
		driver.quit();
	}
}
