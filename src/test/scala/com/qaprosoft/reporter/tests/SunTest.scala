package com.qaprosoft.reporter.tests

import com.qaprosoft.reporter._
import org.apache.log4j.Logger
import org.scalatest._

  class SunTest extends FunSuite with ChromeSuite {

    val LOG = Logger.getLogger(this.getClass)

    val host = "http://www.google.com/"

    test("Sun started") {
      go to (host)
      click on "q"
      textField("q").value = "Sun"
      submit()
      LOG.info("Sun started test")
      assert(pageTitle contains ("sdsdsd"))
    }

    test("Sun in progress") {
      go to (host)

      click on "q"
      textField("q").value = "Sun"
      submit()
      LOG.info("Sun in progress test")
      assert(pageTitle contains ("Sun"))
    }

    test("Sun finished") {
      go to (host)

      click on "q"
      textField("q").value = "Sun"
      submit()
      LOG.info("Sun finished test")
      assert(pageTitle contains ("Sun"))
    }

  }
