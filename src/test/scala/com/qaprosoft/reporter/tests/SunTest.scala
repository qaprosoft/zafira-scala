package com.qaprosoft.reporter.tests

import com.qaprosoft.reporter.{ChromeSuite, Fixture}
import org.scalatest._

  class SunTest extends FunSuite with ChromeSuite with Fixture {

    val host = "http://www.google.com/"

    test("Sun started") {
      go to (host)
      click on "q"
      textField("q").value = "Sun"
      submit()
      println("Sun started is executed")
      assert(pageTitle contains ("sdsdsd"))
    }

    test("Sun in progress") {
      go to (host)

      click on "q"
      textField("q").value = "Sun"
      submit()
      println("Sun in progress is executed")
      assert(pageTitle contains ("Sun"))
    }

    test("Sun finished") {
      go to (host)

      click on "q"
      textField("q").value = "Sun"
      submit()
      println("Sun finished is executed")
      assert(pageTitle contains ("Sun"))
    }

  }
