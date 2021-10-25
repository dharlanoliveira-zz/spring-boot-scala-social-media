package dharlanoliveira.mysocialmedia.repository

import dharlanoliveira.mysocialmedia.application.domain.User
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.{MapSqlParameterSource, NamedParameterJdbcTemplate}
import org.springframework.jdbc.core.{JdbcTemplate, RowMapper}
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Component

import java.sql.{ResultSet, Types}
import javax.sql.DataSource
import scala.jdk.CollectionConverters.CollectionHasAsScala
import scala.jdk.javaapi.CollectionConverters.asJava

@Component
class UserRepository {

  @Autowired
  var dataSource: DataSource = _

  def save(user: User): String = {
    val insertUser = new NamedParameterJdbcTemplate(dataSource)

    val parameters = new MapSqlParameterSource
    parameters.addValue("uid", user.uid)
    parameters.addValue("username", user.username)
    parameters.addValue("mail", user.email)

    insertUser.update("INSERT INTO MYSOCIALMEDIA.USERS(UID,USERNAME,MAIL) VALUES(:uid,:username,:mail)", parameters)

    user.uid
  }

  def existsUserWithUsername(username: String): Boolean = {
    val queryUser = new JdbcTemplate(dataSource)

    val sql = "SELECT COUNT(*) FROM MYSOCIALMEDIA.USERS WHERE USERNAME=?"
    val count = queryUser.queryForObject(sql,classOf[Integer], username)

    count > 0
  }

  def existsUserWithId(uid: String): Boolean = {
    val queryUser = new JdbcTemplate(dataSource)

    val sql = "SELECT COUNT(*) FROM MYSOCIALMEDIA.USERS WHERE UID=?"
    val count = queryUser.queryForObject(sql,classOf[Integer], uid)

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

  def getUserUidByUsername(username: String): String = {
    val queryUser = new JdbcTemplate(dataSource)
    val sql = "SELECT UID FROM MYSOCIALMEDIA.USERS WHERE USERNAME=?"

    val users = queryUser.query(sql, new RowMapper[String]() {
      def mapRow(rs: ResultSet, rowNum: Int): String = {
        rs.getString("UID")
      }
    }, username).asScala.toList

    if(users.isEmpty) null else users.head
  }

  def getUsernameByUserUid(uid: String): String = {
    val queryUser = new JdbcTemplate(dataSource)
    val sql = "SELECT USERNAME FROM MYSOCIALMEDIA.USERS WHERE UID=?"

    val users = queryUser.query(sql, new RowMapper[String]() {
      def mapRow(rs: ResultSet, rowNum: Int): String = {
        rs.getString("USERNAME")
      }
    }, uid).asScala.toList

    if(users.isEmpty) null else users.head
  }

}
