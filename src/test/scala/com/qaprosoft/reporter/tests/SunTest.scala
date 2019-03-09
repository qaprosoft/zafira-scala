package com.qaprosoft.reporter.tests

import com.qaprosoft.reporter.ChromeSuite
import org.scalatest._

  class SunTest extends FunSuite with ChromeSuite {

    val host = "http://www.google.com/"

    test("Sun!") {
      go to (host)

      click on "q"
      textField("q").value = "Sun"
      submit()

      assert(pageTitle contains ("fdfdfdfd"))
      webDriver.quit()
    }
  }
