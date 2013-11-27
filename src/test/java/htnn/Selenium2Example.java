package htnn;

import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Selenium2Example {
	public static void main(String[] args) throws IOException {
		// Create a new instance of the Firefox driver
		// Notice that the remainder of the code relies on the interface,
		// not the implementation.
		final WebDriver driver = new FirefoxDriver();
		final FileWriter resultFile = new FileWriter(UUID.randomUUID().toString());
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
		    public void run() { 		
		    	// Close the browser
				driver.quit();

				try {
					resultFile.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}}
		 });

		// And now use this to visit Google
		driver.get("http://testchannel.david-sandbox.appspot.com");
		// Alternatively the same thing can be done like this
		// driver.navigate().to("http://www.google.com");

		// Find the text input element by its name
		// WebElement element = driver.findElement(By.name("container"));

		int i = 0;
		while(true) {
			// Google's search is rendered dynamically with JavaScript.
			// Wait for the page to load, timeout after 10 seconds
			(new WebDriverWait(driver, 3600))
					.until(new WaitForMessageCondiction(driver, i));
			resultFile.append(i + ","
					+ driver.findElement(By.id("message_" + i)).getText() + ","
					+ System.currentTimeMillis() + "\n");
			resultFile.flush();
			i++;
		}


	}

	public static class WaitForMessageCondiction implements
			ExpectedCondition<Boolean> {
		private WebDriver driver;
		private int i;

		public WaitForMessageCondiction(WebDriver driver, int i) {
			this.driver = driver;
			this.i = i;
		}

		@Override
		public Boolean apply(WebDriver arg0) {
			boolean result = false;
			try {
				result = driver.findElement(By.id("message_" + i)) != null;
			} catch (Throwable t) {
				System.out.println("Error when getting element " + i);
			}
			return result;
		}

	}
}