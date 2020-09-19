package comp;

import java.io.File;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class ComparatorTest {

	String chromeDriverPath ;
	
	//@Test
	public void TC01_getWeatherDataFromWeb() {
		
		chromeDriverPath = System.getProperty("user.dir") + File.separator + "src" + File.separator + "main"
				+ File.separator + "resources" + File.separator + "Drivers" + File.separator + "Mac"
				+ File.separator + "chromedriver";
		
		System.setProperty("webdriver.chrome.driver", chromeDriverPath);
		ChromeOptions options = new ChromeOptions();

		options.addArguments("--disable-extensions");
		options.addArguments("test-type");
		options.addArguments("--no-sandbox");
		options.addArguments("--disable-dev-shm-usage");
		WebDriver driver = new ChromeDriver(options);
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		
		
		
		driver.get("https://weather.com");
		
		WebDriverWait wait = new WebDriverWait(driver, 30);
		wait.until(ExpectedConditions.elementToBeClickable(By.id("LocationSearch_input")));
		WebElement searchField = driver.findElement(By.id("LocationSearch_input"));
		searchField.sendKeys("Delhi");
		 Actions builder = new Actions(driver);
	     builder.sendKeys(searchField, Keys.ARROW_DOWN).perform();

	     builder.sendKeys(searchField, Keys.ARROW_DOWN).perform();

	     builder.sendKeys(searchField, Keys.ENTER).perform();
		String temp = driver.findElement(By.xpath("(//h1[contains(.,'Delhi')]/parent::*/following-sibling::*//span[@data-testid='TemperatureValue'])[1]")).getText();
		System.out.println(temp);
//		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(xpathExpression))))
		driver.quit();
	}
	
	@Test
	public void TC02_getWeatherDataFromApi() {
		
		RequestSpecification requestObj = RestAssured.given();
		requestObj.baseUri("https://api.openweathermap.org/");
		requestObj.basePath("data/2.5/weather");
		requestObj.queryParam("q", "Delhi,in");
		requestObj.queryParam("APPID","7fe67bf08c80ded756e598d6f8fedaea");
		requestObj.queryParam("units","metric");
		requestObj.log().all();
		Response response = requestObj.get();
		System.out.println(response.body().asString());
		System.out.println(response.jsonPath().get("main.temp").toString());
		
	}
}
