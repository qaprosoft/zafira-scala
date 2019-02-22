package com.qaprosoft.reporter.tests

import org.openqa.selenium._
import org.openqa.selenium.chrome.ChromeDriver
import org.scalatest._
import org.scalatest.selenium.WebBrowser

  class SunTest extends FunSuite with WebBrowser {

    implicit val webDriver: WebDriver = new ChromeDriver
    val host = "http://www.google.com/"

    test("Sun!") {
      go to (host)

      click on "q"
      textField("q").value = "Sun"
      submit()

      assert(pageTitle contains ("Sun"))
      webDriver.quit()
    }
  }
