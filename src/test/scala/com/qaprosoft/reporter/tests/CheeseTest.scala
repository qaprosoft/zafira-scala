package com.qaprosoft.reporter.tests

import com.qaprosoft.reporter.ChromeSuite
import org.scalatest._

class CheeseTest extends FunSuite with ChromeSuite {

  val host = "http://www.google.com/"

  test("Cheese started") {
    go to (host)
    suiteLogger.info("Google is opened")
    click on "q"
    textField("q").value = "Cheese!"
    suiteLogger.info("Cheese query is typed")
    submit()
    suiteLogger.info("Submit button is clicked")
    assert(pageTitle contains ("Cheese!"))
  }

  test("Cheese finished") {
    go to (host)
    suiteLogger.info("Google is opened")
    click on "q"
    textField("q").value = "Cheese!"
    suiteLogger.info("Cheese query is typed")
    submit()
    suiteLogger.info("Submit button is clicked")
    assert(pageTitle contains ("Cheese!"))
  }

}
