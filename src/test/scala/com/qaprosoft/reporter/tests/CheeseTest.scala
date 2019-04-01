package com.qaprosoft.reporter.tests

class CheeseTest extends AbstractTest {

  test("Cheese1") {
    go to host
    LOGGER.info("Cheese1 test")
    for (query <- list) {
      search(query)
      }
  }

  test("Cheese2") {
    go to host
    LOGGER.info("Cheese2 test")
    for (query <- list) {
      search(query)
    }
  }

  test("Cheese3") {
    go to host
    LOGGER.info("Cheese3 test")
    for (query <- list) {
      search(query)
    }
  }

}
