package com.qaprosoft.reporter

import org.scalatest._

trait Fixture extends TestSuite with Util{

  override def withFixture(test: NoArgTest):Outcome = {
    var status:Outcome = null
    println("Tests on start fixture")
    if (ZAFIRA_RERUN_FAILURES) {
      println("Tests need rerun" + singleton.testNamesRerun.toString)
      if (singleton.testNamesRerun.contains(test.name)) {
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
