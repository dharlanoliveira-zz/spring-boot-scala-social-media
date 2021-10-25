package dharlanoliveira.mysocialmedia.acceptance.listener

import org.springframework.test.context.{TestContext, TestExecutionListener}

import javax.sql.DataSource

class CleanDatabaseBetweenScenarios extends TestExecutionListener {

  override def beforeTestMethod(testContext: TestContext): Unit = {
    clearDatabase(testContext)
  }

  def clearDatabase(testContext: TestContext): Unit = {
    val applicationContext = testContext.getApplicationContext
    val datasource = applicationContext.getBean(classOf[DataSource])

    val connection = datasource.getConnection

    val statement = connection.createStatement()
    statement.execute("SET REFERENTIAL_INTEGRITY FALSE")

    val resultset = statement.executeQuery("SELECT TABLE_SCHEMA,TABLE_NAME FROM INFORMATION_SCHEMA.TABLES")
    var tables = List[String]()
    while (resultset.next()) {
      val schema = resultset.getString(1)
      val table = resultset.getString(2)
      if (schema == "MYSOCIALMEDIA" && table != "flyway_schema_history")
        tables ::= table
    }

    tables.foreach { it =>
        statement.execute("TRUNCATE TABLE MYSOCIALMEDIA." + it)
    }
    statement.execute("SET REFERENTIAL_INTEGRITY TRUE")
    statement.close()
    connection.close()
  }
}
