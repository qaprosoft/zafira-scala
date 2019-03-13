//package com.qaprosoft.reporter
//
//import com.qaprosoft.zafira.config.IConfigurator
//import org.apache.commons.lang3.ArrayUtils
//import java.util
//
//import com.qaprosoft.zafira.models.dto.TestType
//import org.apache.log4j.spi.LoggerFactory
//import org.scalatest.events.{Event, RunStarting, TestFailed}
//import org.slf4j.LoggerFactory
//import org.slf4j.LoggerFactory
//
//import scala.collection.JavaConversions._
//
//class ExcludeTestsForRerun {
//
//   val DO_NOT_RUN_TEST_NAMES = "doNotRunTestNames"
//   val ENABLED = "enabled"
//
//  private val LOGGER = LoggerFactory.getLogger(classOf[ZafiraReporter])
//
//  def excludeTestsForRerun(event: TestFailed, testRunResults: util.List[TestType], configurator: IConfigurator): Unit = {
//    val testNamesNoRerun = new util.ArrayList[String]
//    val classesToRerun = new util.HashSet[String]
//
//    for (test <- testRunResults) {
//      if (!test.isNeedRerun) testNamesNoRerun.add(test.getName)
//      else classesToRerun.add(test.getTestClass)
//    }
//    val testNamesNoRerunArr = testNamesNoRerun.toArray(new Array[String](testNamesNoRerun.size))
//
//    var isAnythingMarked = true
//    while ( {
//      isAnythingMarked
//    }) {
//      isAnythingMarked = false
//      for (testNGMethod <- suite.getAllMethods) {
//        val annotations = testNGMethod.getConstructorOrMethod.getMethod.getAnnotations
//        var isTest = false
//        var shouldUpdateDataProvider = false
//        for (a <- annotations) {
//          if (a.isInstanceOf[Nothing]) {
//            isTest = true
//            if (!(a.asInstanceOf[Nothing]).dataProvider.isEmpty) if (!classesToRerun.contains(testNGMethod.getRealClass.getName) && a.asInstanceOf[Nothing].enabled) {
//              modifyAnnotationValue(a, testNGMethod, ENABLED, false)
//              isAnythingMarked = true
//              for (m <- testNGMethod.getMethodsDependedUpon) {
//                allDependentMethods = ArrayUtils.removeElement(allDependentMethods, m)
//              }
//            }
//            else shouldUpdateDataProvider = true
//            else if (!ArrayUtils.contains(allDependentMethods, testNGMethod.getRealClass.getName + "." + testNGMethod.getConstructorOrMethod.getMethod.getName)) {
//              @SuppressWarnings(Array("deprecation")) val suiteRunner = new Nothing(new Nothing, new XmlSuite, "")
//              val testRunner = new TestRunner(new Nothing, suiteRunner, testNGMethod.getXmlTest, false, null, new util.ArrayList[IClassListener])
//              val testResult = new TestResult(testNGMethod.getTestClass, testNGMethod.getInstance, testNGMethod, null, 0, 0, testRunner)
//              if (testNamesNoRerun.contains(configurator.getTestName(testResult)) && a.asInstanceOf[Nothing].enabled) {
//                modifyAnnotationValue(a, testNGMethod, ENABLED, false)
//                isAnythingMarked = true
//                for (m <- testNGMethod.getMethodsDependedUpon) {
//                  allDependentMethods = ArrayUtils.removeElement(allDependentMethods, m)
//                }
//              }
//            }
//            break //todo: break is not supported
//          }
//        }
//        if (isTest && shouldUpdateDataProvider) for (a <- annotations) {
//          modifyAnnotationValue(a, testNGMethod, DO_NOT_RUN_TEST_NAMES, testNamesNoRerunArr)
//        }
//      }
//    }
//  }
//
//  @SuppressWarnings(Array("unchecked")) private def modifyAnnotationValue(a: Nothing, testNGMethod: ITestNGMethod, fieldName: String, newValue: Any): Unit = {
//    val c = a.getClass
//    val aMethods = c.getDeclaredMethods
//    for (m <- aMethods) {
//      if (fieldName == m.getName) {
//        LOGGER.info(String.format("'%s' annotation was found for method '%s'", m.getName, testNGMethod.getConstructorOrMethod.getMethod.getName))
//        val handler = Proxy.getInvocationHandler(a)
//        var f = null
//        try
//          f = handler.getClass.getDeclaredField("memberValues")
//        catch {
//          case e@(_: NoSuchFieldException | _: SecurityException) =>
//            throw new IllegalStateException(e)
//        }
//        f.setAccessible(true)
//        var memberValues = null
//        try
//          memberValues = f.get(handler).asInstanceOf[Nothing]
//        catch {
//          case e@(_: IllegalArgumentException | _: IllegalAccessException) =>
//            throw new IllegalStateException(e)
//        }
//        memberValues.put(fieldName, newValue)
//        return
//      }
//    }
//  }
//
//}
