package com.qaprosoft.reporter.tests

import com.qaprosoft.reporter.{ChromeSuite, Util}
import org.scalatest._

class CheeseTest extends FunSuite with ChromeSuite  with Util {


  val host = "http://www.google.com/"

  test("Cheese1") {
    go to host
    LOGGER.info("Cheese1 test")
    click on "q"
    textField("q").value = "Cheese!"
    submit()
    assert(pageTitle contains "Cheese!")
  }

  test("Cheese2") {
    go to host
    LOGGER.info("Cheese2 test")
    click on "q"
    textField("q").value = "Cheese!"
    submit()
    assert(pageTitle contains "Cheese!")
  }

  test("Cheese3") {
    go to host
    LOGGER.info("Cheese3 test")
    click on "q"
    textField("q").value = "Cheese!"
    submit()
    assert(pageTitle contains "Cheese!")
  }

}
