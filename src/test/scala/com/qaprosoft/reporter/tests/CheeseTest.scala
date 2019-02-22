package com.qaprosoft.reporter.tests

import org.openqa.selenium._
import org.openqa.selenium.chrome.ChromeDriver
import org.scalatest._
import org.scalatest.selenium.WebBrowser

class CheeseTest extends FunSuite with WebBrowser {

  implicit val webDriver: WebDriver = new ChromeDriver
  val host = "http://www.google.com/"

  //Create tests with custom hook to have report
  test("Cheese!") {
    go to (host)

    click on "q"
    textField("q").value = "Cheese!"
    submit()

    assert(pageTitle contains ("Cheese!"))
    webDriver.quit()
  }

}
