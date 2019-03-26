package com.qaprosoft.reporter.tests

import com.qaprosoft.reporter.{ChromeSuite, Util}
import org.scalatest._

class CheeseTest extends FunSuite with ChromeSuite  with Util {


  val host = "http://www.google.com/"

  test("Cheese started") {
    go to (host)
    LOGGER.info("Cheese started test")
    click on "q"
    textField("q").value = "Cheese!"
    submit()
    assert(pageTitle contains ("Cheese!"))
  }

  test("Cheese in progress") {
    go to (host)
    LOGGER.info("Cheese in progress test")
    click on "q"
    textField("q").value = "Cheese!"
    submit()
    assert(pageTitle contains ("Cheese!"))
  }

  test("Cheese finished") {
    go to (host)
    LOGGER.info("Cheese finished test")
    click on "q"
    textField("q").value = "Cheese!"
    submit()
    assert(pageTitle contains ("Cheese!"))
  }

}
