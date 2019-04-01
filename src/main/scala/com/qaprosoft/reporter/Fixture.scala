package com.qaprosoft.reporter

import java.io.{BufferedWriter, File, FileWriter}

import org.apache.commons.io.FileUtils
import org.openqa.selenium.logging.LogType
import org.openqa.selenium.{By, OutputType, TakesScreenshot}
import org.scalatest._
import org.scalatest.selenium.{Driver, WebBrowser}

import scala.collection.JavaConverters

trait Fixture extends TestSuite with Util with Driver with WebBrowser {

//  override def withFixture(test: NoArgTest):Outcome = {
//    var status:Outcome = null
//    println("Tests on start fixture")
//    if (ZAFIRA_RERUN_FAILURES) {
//      println("Tests need rerun" + singleton.testNamesRerun.toString)
//      if (singleton.testNamesRerun.contains(test.name)) {
//        status = super.withFixture(test)
//      } else {
//        println(test.name + " is already passed before rerun, it will not be executed")
//        status = status.toSucceeded
//      }
//    }
//    else  {
//      status = super.withFixture(test)
//    }
//    return status
//  }

  /**
    * Boolean config, "true" to turn on snapshots. Any other value will be false
    */
  lazy val takeScreenShot = sys.props.getOrElse("screenshot_enabled", "true").toBoolean

  /**
    * Output directory for screenshots. Defaults to tmp directory provided by the OS
    */
  lazy val outputDirectory = {
    sys.props.get("functional.snapshot.output")
      .map(new File(_))
      .getOrElse(FileUtils.getTempDirectory)
  }

  override protected def withFixture(test: NoArgTest): Outcome = {
    LOGGER.info(f"Start -  ${test.name}")
    val outcome = test()
    LOGGER.info(f"End -  ${test.name}")
    if (takeScreenShot && !outcome.isSucceeded) {
      try {
        webDriver match {
          /**
            * Puts screenshots in outputDirectory. Class name, then test name.
            *
            * eg
            * /tmp/com.mosaic.SomeTestSuite/Scenario: blah blah blah.jpg
            */
          case screenShotCapable: TakesScreenshot =>
            val bodyDim: org.openqa.selenium.Dimension = webDriver.findElement(By.tagName("body")).getSize
            val dim2 = new org.openqa.selenium.Dimension(1600, bodyDim.height)
            webDriver.manage().window().setSize(dim2)
            val file = screenShotCapable.getScreenshotAs(OutputType.FILE)
            val directory = this.getClass.getCanonicalName
            val currentOutputDir = s"${outputDirectory.getAbsolutePath}${File.separator}${directory}${File.separator}"
            LOGGER.info(s"Creating directory $currentOutputDir")
            FileUtils.forceMkdir(new File(currentOutputDir))
            //copy screenshot to correct location
            FileUtils.moveFile(file, new File(f"${currentOutputDir}${test.text}.jpg"))

            //write some random info
            val infoFile = new File(f"${currentOutputDir}${test.text}.txt")
            infoFile.createNewFile()
            var writer = new BufferedWriter(new FileWriter(infoFile))
            writer.write(f"BrowserUrl: ${webDriver.getCurrentUrl}")
            writer.flush()
            writer.close()


            val sourceFile = new File(f"${currentOutputDir}${test.text}.html")
            sourceFile.createNewFile()
            writer = new BufferedWriter(new FileWriter(sourceFile))
            writer.write(webDriver.getPageSource)
            writer.flush()
            writer.close()

            val logFile = new File(f"${currentOutputDir}${test.text}.log")
            logFile.createNewFile()
            writer = new BufferedWriter(new FileWriter(logFile))

            val l = JavaConverters.asScalaBuffer(webDriver.manage().logs.get(LogType.BROWSER).getAll).toList.map(_.toString).mkString("\n")
            val x = JavaConverters.asScalaBuffer(webDriver.manage().logs.get(LogType.DRIVER).getAll).toList.map(_.toString).mkString("\n")
            writer.write(l)
            writer.write(x)
            writer.flush()
            writer.close()

          case _ => Unit
        }
      } catch {
        case e: Throwable => LOGGER.error("Unable to create snapshot", e)
      }

    }

    outcome
  }

}
