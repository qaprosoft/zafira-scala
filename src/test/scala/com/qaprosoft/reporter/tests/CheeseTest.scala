package com.qaprosoft.reporter.tests

import com.qaprosoft.reporter.ChromeSuite
import org.scalatest._

class CheeseTest extends FunSuite with ChromeSuite {

  val host = "http://www.google.com/"

  test("Cheese!") {
    go to (host)

    click on "q"
    textField("q").value = "Cheese!"
    submit()

    assert(pageTitle contains ("Cheese!"))
    webDriver.quit()
  }

}
