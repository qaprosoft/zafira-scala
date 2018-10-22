import com.qaprosoft.zafira.client.ZafiraClient
import com.qaprosoft.zafira.models.db.Status
import com.qaprosoft.zafira.models.dto._
import com.qaprosoft.zafira.models.dto.user.UserType

import scala.collection.JavaConverters._

/**
    Simple class that wraps existing ZafiraClient java lib in scala calls
 */
class ScalaZafiraClient(var url: String) {

  val zafiraClient = new ZafiraClient(url)

  /**
    * Login method
    * @param username String user name
    * @param password String password
    */
  def login(username: String, password: String) = {

    this.zafiraClient.login(username, password)
  }

  /**
    * Method to refresh and get temporary session token
    * @param token admin token that you generated in zafira admin panel
    * @return Response[AuthTokenType]
    */
  def refreshToken(token: String) = {
    this.zafiraClient.refreshToken(token)
  }

  /**
    * Sets session token to client
    * @param token String session token
    */
  def setToken(token: String)= {
    this.zafiraClient.setAuthToken("Bearer " + token)
  }

  /**
    * Gets test run user
    * @return UserType that started test
    */
  def getTestRunUser():UserType = {
    this.zafiraClient.getUserProfile.getObject
  }

  /**
    * Register test suite in Zafira
    * @param suiteName String test suite name
    * @param fileName String test suite file name (testNG xml file usually)
    * @param owner UserType owner of test suite
    * @return registered TestSuiteType
    */
  def registerTestSuite(suiteName: String ,fileName: String, owner: UserType): TestSuiteType = {
    this.zafiraClient.registerTestSuite(suiteName, fileName, owner.getId())
  }

  /**
    * Register CI job in Zafira
    * @param jobUrl String ci job url with running build
    * @param owner job owner user id
    * @return registered JobType
    */
  def registerCIJob(jobUrl:String, owner: UserType):JobType ={
    this.zafiraClient.registerJob(jobUrl, owner.getId())
  }

  /**
    * Gets owner of test sute, otherwise gets anonymous
    * @param ownerName String name of test suite owner
    * @return UserType
    */
  def getSuteOwner(ownerName: String):UserType = {
    this.zafiraClient.getUserOrAnonymousIfNotFound(ownerName)
  }

  /**
    * Registers test case in Zafira, it may be a new one
    *
    * @param suite TestSuiteType of running test suite
    * @param testClass String test class name
    * @param testMethod String test method name
    * @return registered test case
    */
  def registerTestCase(suite: TestSuiteType, testClass: String, testMethod: String):TestCaseType = {
    val primaryOwnerId: Long = 1 // predefined fow now
    val secondaryOwnerId: Long = 1 // predefined for now
    this.zafiraClient.registerTestCase(suite.getId, primaryOwnerId, secondaryOwnerId, testClass, testMethod)
  }

  /**
    * Registers test run in Zafira.
    *
    * @param testName String test name
    * @param group String test group
    * @param status Status test status
    * @param testParams String test arguments
    * @param runId Long test run id
    * @param caseId Long test case id
    * @param runCount Int retries count
    * @param dependsonmethods Array[String] list of dependent tests
    * @param configuration String config XML
    * @param threadCiTestId String
    * @param tags Set
    * @return registered test
    */
  def registerTestStart(testName: String, group: String, status: Status, testParams: String, runId: Long, caseId: Long, runCount: Int, configuration: String, dependsonmethods: Array[String], threadCiTestId: String, tags: Set[TagType]):TestType = {
    this.zafiraClient.registerTestStart(testName, group, status, testParams, runId, caseId, runCount, configuration, dependsonmethods, threadCiTestId, tags.asJava)
  }

}
