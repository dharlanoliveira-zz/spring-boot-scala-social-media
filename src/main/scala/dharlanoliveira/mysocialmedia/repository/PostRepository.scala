package dharlanoliveira.mysocialmedia.repository

import dharlanoliveira.mysocialmedia.application.domain.Post
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.{MapSqlParameterSource, NamedParameterJdbcTemplate}
import org.springframework.jdbc.core.support.SqlLobValue
import org.springframework.jdbc.core.{JdbcTemplate, RowMapper}
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.jdbc.support.lob.DefaultLobHandler
import org.springframework.stereotype.Component

import java.io.InputStream
import java.sql.{ResultSet, Types}
import javax.sql.DataSource
import scala.jdk.CollectionConverters.CollectionHasAsScala

@Component
class PostRepository {


  @Autowired
  var dataSource: DataSource = _

  def save(post: Post): Long = {
    val insertPost = new NamedParameterJdbcTemplate(dataSource)

    val holder = new GeneratedKeyHolder

    val parameters = new MapSqlParameterSource
    parameters.addValue("owner_uid", post.userUid)
    parameters.addValue("text", post.text)
    parameters.addValue("instant", post.instant, Types.TIMESTAMP)

    if(post.image != null) {
      parameters.addValue("image", new SqlLobValue(post.image, post.image.available(), new DefaultLobHandler), Types.BLOB)
    } else {
      parameters.addValue("image", null, Types.BLOB)
    }

    insertPost.update("INSERT INTO MYSOCIALMEDIA.POSTS(OWNER_UID,TEXT,INSTANT,IMAGE) VALUES(:owner_uid,:text,:instant,:image)", parameters, holder)

    post.id = holder.getKey.longValue()
    post.id
  }

  def getImageByPostId(id: Long): InputStream = {
    val queryUser = new JdbcTemplate(dataSource)
    val sql = "SELECT * FROM MYSOCIALMEDIA.POSTS WHERE ID=?"
    queryUser.queryForObject(sql, new RowMapper[InputStream]() {
      def mapRow(rs: ResultSet, rowNum: Int): InputStream = {
        rs.getBlob("IMAGE").getBinaryStream
      }
    }, id)
  }

  def getAll: List[Post] = {
    val queryUser = new JdbcTemplate(dataSource)
    val sql = "SELECT * FROM MYSOCIALMEDIA.POSTS"
    queryUser.query(sql, new RowMapper[Post]() {
      def mapRow(rs: ResultSet, rowNum: Int): Post = {
        new Post(rs.getLong("ID"),
          rs.getString("OWNER_UID"),
          rs.getString("TEXT"),
          rs.getBinaryStream("IMAGE"),
          rs.getTimestamp("instant").toLocalDateTime
        )
      }
    }).asScala.toList
  }
}
