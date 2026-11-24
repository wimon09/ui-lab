import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class QP01customerTest {
  @Test
  public void customerRequestsAndRatesPizza() {
    WebDriver driver = new ChromeDriver();
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

    try {
      driver.get("https://quickpizza.grafana.com/");
      wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("h1")));
      assertEquals(
          "Looking to break out of your pizza routine?",
          driver.findElement(By.cssSelector("h1")).getText());

      driver.findElement(
          By.xpath("//label[.//span[normalize-space()='Advanced']]")).click();
      WebElement calories =
          wait.until(ExpectedConditions.visibilityOfElementLocated(
              By.xpath("//label[normalize-space()='Max Calories per Slice']/../input")));
      calories.clear();
      calories.sendKeys("600");

      Select excludedTools = new Select(driver.findElement(By.cssSelector("select")));
      wait.until(
          ExpectedConditions.presenceOfElementLocated(
              By.xpath("//select/option[normalize-space()='Knife']")));
      excludedTools.selectByVisibleText("Knife");
      driver.findElement(By.id("default-checkbox")).click();

      String pizzaName =
          "Lab Pizza "
              + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
      driver.findElement(By.cssSelector("input#pizza-name")).sendKeys(pizzaName);
      assertEquals("600", calories.getAttribute("value"));
      assertTrue(driver.findElement(By.id("default-checkbox")).isSelected());
      assertEquals(
          "Knife",
          excludedTools.getFirstSelectedOption().getText());

      driver.findElement(By.name("pizza-please")).click();
      WebElement nameLine =
          wait.until(
              ExpectedConditions.visibilityOfElementLocated(
                  By.xpath("//p[starts-with(., 'Name:')]")));
      assertEquals("Name: " + pizzaName, nameLine.getText());
      String tool = driver.findElement(By.xpath("//p[starts-with(., 'Tool:')]")).getText();
      assertFalse(tool.contains("Knife"));
      String caloriesText =
          driver.findElement(By.xpath("//p[starts-with(., 'Calories per slice:')]")).getText();
      int caloriesPerSlice = Integer.parseInt(caloriesText.split(":")[1].trim());
      assertTrue(caloriesPerSlice <= 600);

      driver.findElement(By.name("rate-5")).click();
      assertEquals(
          "Please log in first.",
          wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("rate-result")))
              .getText());
    } finally {
      driver.quit();
    }
  }
}
