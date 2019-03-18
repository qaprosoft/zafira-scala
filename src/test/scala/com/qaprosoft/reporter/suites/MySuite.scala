package com.qaprosoft.reporter.suites

import com.qaprosoft.reporter.tests.{CheeseTest, SunTest}
import org.scalatest._

class MySuite extends Suites (
  new SunTest,
  new CheeseTest)
