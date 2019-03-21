package com.qaprosoft.reporter.tests


import com.qaprosoft.reporter.{ChromeSuite, Util}
import org.scalatest._

class CheeseTest extends FunSuite with ChromeSuite  with Util {


  val host = "http://www.google.com/"

  test("Cheese started") {
    go to (host)
    LOGGER.info("Google is opened")
    click on "q"
    textField("q").value = "Cheese!"
    LOGGER.info("Cheese query is typed")
    submit()
    println("Cheese started is executed")
    LOGGER.info("Submit button is clicked")
    assert(pageTitle contains ("Cheese!"))
  }

  test("Cheese in progress") {
    go to (host)
    LOGGER.info("Google is opened")
    click on "q"
    textField("q").value = "Cheese!"
    LOGGER.info("Cheese query is typed")
    submit()
    println("Cheese in progress is executed")
    LOGGER.info("Submit button is clicked")
    assert(pageTitle contains ("Cheese!"))
  }

  test("Cheese finished") {
    go to (host)
    LOGGER.info("Google is opened")
    click on "q"
    textField("q").value = "Cheese!"
    LOGGER.info("Cheese query is typed")
    submit()
    println("Cheese finished is executed")
    LOGGER.info("Submit button is clicked")
    assert(pageTitle contains ("Cheese!"))
  }

}
