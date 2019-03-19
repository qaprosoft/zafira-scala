package com.qaprosoft.reporter

import java.util

import org.scalatest._

trait Fixture extends TestSuite with Util{

  var testNamesRerun = new util.ArrayList[String]

  override def withFixture(test: NoArgTest):Outcome = {
    var status:Outcome = null

    if (ZAFIRA_RERUN_FAILURES) {
      println("Tests need rerun" + testNamesRerun.toString)
      if (testNamesRerun.contains(test.name)) {
        status = super.withFixture(test)
      } else {
        println(test.name + " is already passed before rerun, it will not be executed")
        status = status.toSucceeded
      }
    }
    else  {
      status = super.withFixture(test)
    }
    return status
  }
}
