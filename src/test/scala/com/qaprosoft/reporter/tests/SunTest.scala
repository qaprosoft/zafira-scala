package com.qaprosoft.reporter.tests

  class SunTest extends AbstractTest {

    test("Sun1") {
      go to host
      LOGGER.info("Sun1 test")

      for (query <- list) {
        search(query)
      }
      click on "q"
      textField("q").value = "Sun"
      submit()

      assert(pageTitle contains "sdsdsd")
    }

    test("Sun2") {
      go to host
      LOGGER.info("Sun1 test")

      for (query <- list) {
        search(query)
      }

    }

    test("Sun3") {
      go to host
      LOGGER.info("Sun3 test")

      for (query <- list) {
        search(query)
      }

    }

  }
