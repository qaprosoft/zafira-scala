package com.qaprosoft.reporter.tests

import com.qaprosoft.reporter.{ChromeSuite, Util}
import org.scalatest.FunSuite

abstract class AbstractTest extends FunSuite with ChromeSuite  with Util{

  val host = "http://www.google.com/"

  val list = List("Cheese", "Potato", "Carrot", "Green", "Black", "Bag", "Apple", "Bananas", "Cherries", "List")

  def search(query:String) = {
    click on "q"
    textField("q").value = query
    submit()
    assert(pageTitle contains query)
  }

}
