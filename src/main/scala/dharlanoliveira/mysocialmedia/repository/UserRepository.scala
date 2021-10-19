package dharlanoliveira.mysocialmedia.repository

import dharlanoliveira.mysocialmedia.application.domain.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.{JdbcTemplate, RowMapper, RowMapperResultSetExtractor}
import org.springframework.jdbc.core.simple.SimpleJdbcInsert
import org.springframework.stereotype.Component

import java.sql.ResultSet
import javax.sql.DataSource
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.jdk.javaapi.CollectionConverters.asJava

@Component
class UserRepository {



  @Autowired
  var dataSource: DataSource = _

  def save(user: User): String = {
    val parameters = user.toMap
    val insertUser = new SimpleJdbcInsert(dataSource)
              .withSchemaName("MYSOCIALMEDIA")
              .withTableName("USERS")

    insertUser.execute(asJava(parameters))
    user.uid
  }

  def existsUserWithUsername(username: String): Boolean = {
    val queryUser = new JdbcTemplate(dataSource)

    val sql = "SELECT COUNT(*) FROM MYSOCIALMEDIA.USERS WHERE USERNAME=?"
    val count = queryUser.queryForObject(sql,classOf[Integer], username)

    count > 0
  }

  def existsUserWithMail(email: String): Boolean = {
    val queryUser = new JdbcTemplate(dataSource)

    val sql = "SELECT COUNT(*) FROM MYSOCIALMEDIA.USERS WHERE MAIL=?"
    val count = queryUser.queryForObject(sql,classOf[Integer], email)

    count > 0
  }

  def getAll: List[User] = {
    val queryUser = new JdbcTemplate(dataSource)
    val sql = "SELECT * FROM MYSOCIALMEDIA.USERS"
    queryUser.query(sql, new RowMapper[User]() {
      def mapRow(rs: ResultSet, rowNum: Int): User = {
        new User(rs.getString("UID").toCharArray,rs.getString("USERNAME"),rs.getString("MAIL"))
      }
    }).asScala.toList
  }

}
