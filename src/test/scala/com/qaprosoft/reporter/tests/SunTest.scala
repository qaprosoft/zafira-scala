package com.qaprosoft.reporter.tests

import com.qaprosoft.reporter._
import org.apache.log4j.Logger
import org.scalatest._

  class SunTest extends FunSuite with ChromeSuite with Util {

    val host = "http://www.google.com/"

    test("Sun1") {
      go to host
      click on "q"
      textField("q").value = "Sun"
      submit()
      LOGGER.info("Sun1 test")
      assert(pageTitle contains "sdsdsd")
    }

    test("Sun2") {
      go to host
      click on "q"
      textField("q").value = "Sun"
      submit()
      LOGGER.info("Sun2 test")
      assert(pageTitle contains "Sun")
    }

    test("Sun3") {
      go to host
      click on "q"
      textField("q").value = "Sun"
      submit()
      LOGGER.info("Sun3 test")
      assert(pageTitle contains "Sun")
    }

  }
