package com.qaprosoft.reporter.tests

import com.qaprosoft.reporter.{ChromeSuite, Fixture, Util}
import org.scalatest._

  class SunTest extends FunSuite with ChromeSuite with Util {

    val host = "http://www.google.com/"

    test("Sun started") {
      go to (host)
      click on "q"
      textField("q").value = "Sun"
      submit()
      LOGGER.info("Sun started test")
      assert(pageTitle contains ("sdsdsd"))
    }

    test("Sun in progress") {
      go to (host)

      click on "q"
      textField("q").value = "Sun"
      submit()
      LOGGER.info("Sun in progress test")
      assert(pageTitle contains ("Sun"))
    }

    test("Sun finished") {
      go to (host)

      click on "q"
      textField("q").value = "Sun"
      submit()
      LOGGER.info("Sun finished test")
      assert(pageTitle contains ("Sun"))
    }

  }
