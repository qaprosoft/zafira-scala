package com.qaprosoft.reporter

import java.util

import org.scalatest._

trait Fixture extends TestSuite with Util{

  var sharable = new util.ArrayList[String]

  override def withFixture(test: NoArgTest):Outcome = {
    var status:Outcome = null
    println("Tests needs rerun 2 " + sharable.toString)
    if (ZAFIRA_RERUN_FAILURES) {
      println("ZAFIRA_RERUN_FAILURES " + ZAFIRA_RERUN_FAILURES.toString)
      if (sharable.contains(test.name)) {
        status = super.withFixture(test)
      } else {
        println(test.name + " is already passed before rerun")
        status = status.toSucceeded
      }
    }
    else  status = super.withFixture(test)
    return status
  }
}
