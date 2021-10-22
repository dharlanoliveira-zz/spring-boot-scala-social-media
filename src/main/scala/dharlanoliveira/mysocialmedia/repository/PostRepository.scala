package dharlanoliveira.mysocialmedia.repository

import dharlanoliveira.mysocialmedia.application.domain.{Comment, Post}
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
    if (post.id == 0) insertPost(post) else updatePost(post)
  }

  def getImageByPostId(id: Long): InputStream = {
    val queryUser = new JdbcTemplate(dataSource)
    val sql = "SELECT * FROM MYSOCIALMEDIA.POSTS WHERE ID=?"
    val images = queryUser.query(sql, new RowMapper[InputStream]() {
      def mapRow(rs: ResultSet, rowNum: Int): InputStream = {
        rs.getBlob("IMAGE").getBinaryStream
      }
    }, id).asScala.toList
    if (images.isEmpty) null else images.head
  }

  def getPostById(postId: Long): Post = {
    val queryUser = new JdbcTemplate(dataSource)
    val sql = "SELECT * FROM MYSOCIALMEDIA.POSTS WHERE ID=?"
    val posts = queryUser.query(sql, postRowMapper, postId).asScala.toList
    if (posts.isEmpty) null else posts.head
  }

  def getAll: List[Post] = {
    val queryUser = new JdbcTemplate(dataSource)
    val sql = "SELECT * FROM MYSOCIALMEDIA.POSTS"
    queryUser.query(sql, postRowMapper).asScala.toList
  }


  def getCommentsByPost(postId: Long): List[Comment] = {
    val query = new JdbcTemplate(dataSource)
    val sql = "SELECT * FROM MYSOCIALMEDIA.COMMENTS WHERE POST_ID=?"
    query.query(sql, commentRowMapper, postId).asScala.toList
  }

  def getCommentById(id: Long) : Comment = {
    val query = new JdbcTemplate(dataSource)
    val sql = "SELECT * FROM MYSOCIALMEDIA.COMMENTS WHERE ID=?"
    val comments = query.query(sql, commentRowMapper, id).asScala.toList
    if (comments.isEmpty) null else comments.head
  }

  private def insertPost(post: Post): Long = {
    val insertPost = new NamedParameterJdbcTemplate(dataSource)

    val holder = new GeneratedKeyHolder

    val parameters = updateAndInsertPostParameters(post)

    insertPost.update("INSERT INTO MYSOCIALMEDIA.POSTS(OWNER_UID,TEXT,INSTANT,IMAGE) VALUES(:owner_uid,:text,:instant,:image)", parameters, holder)

    post.id = holder.getKey.longValue()
    post.id
  }

  private def updatePost(post: Post): Long = {
    val updatePost = new NamedParameterJdbcTemplate(dataSource)

    val parameters = updateAndInsertPostParameters(post)
    parameters.addValue("id", post.id)

    updatePost.update("UPDATE MYSOCIALMEDIA.POSTS SET OWNER_UID=:owner_uid,TEXT=:text,INSTANT=:instant,IMAGE=:image WHERE :id", parameters)

    post.comments.foreach(comment => {
      if (comment.id == 0) insertComment(post, comment) else updateComment(comment)
    })

    post.id
  }

  private def insertComment(post: Post, comment: Comment): Unit = {
    val insertComment = new NamedParameterJdbcTemplate(dataSource)
    val parameters = updateAndInsertCommentParameters(comment, post)

    insertComment.update("INSERT INTO MYSOCIALMEDIA.COMMENTS(POST_ID,OWNER_UID,TEXT,INSTANT) VALUES(:post_id,:owner_uid,:text,:instant)", parameters)
  }

  private def updateComment(comment: Comment): Unit = {
    val updateComment = new NamedParameterJdbcTemplate(dataSource)
    val parameters = updateAndInsertCommentParameters(comment)
    parameters.addValue("id", comment.id)

    updateComment.update("UPDATE MYSOCIALMEDIA.COMMENTS SET TEXT=:text,INSTANT=:instant WHERE ID=:id", parameters)
  }

  private def updateAndInsertPostParameters(post: Post): MapSqlParameterSource = {
    val parameters = new MapSqlParameterSource()

    parameters.addValue("owner_uid", post.ownerUid)
    parameters.addValue("text", post.text)
    parameters.addValue("instant", post.instant, Types.TIMESTAMP)

    if (post.image != null) {
      parameters.addValue("image", new SqlLobValue(post.image, post.image.available(), new DefaultLobHandler), Types.BLOB)
    } else {
      parameters.addValue("image", null, Types.BLOB)
    }

    parameters
  }

  private def updateAndInsertCommentParameters(comment: Comment, post: Post = null): MapSqlParameterSource = {
    val parameters = new MapSqlParameterSource
    parameters.addValue("owner_uid", comment.ownerUid)

    if (post != null)
      parameters.addValue("post_id", post.id)

    parameters.addValue("text", comment.text)
    parameters.addValue("instant", comment.instant, Types.TIMESTAMP)
    parameters
  }

  private def postRowMapper: RowMapper[Post] = {
    new RowMapper[Post]() {
      def mapRow(rs: ResultSet, rowNum: Int): Post = {
        new Post(rs.getLong("ID"),
          rs.getString("OWNER_UID"),
          rs.getString("TEXT"),
          rs.getBinaryStream("IMAGE"),
          rs.getTimestamp("INSTANT").toLocalDateTime,
          getCommentsByPost(rs.getLong("ID"))
        )
      }
    }
  }

  private def commentRowMapper: RowMapper[Comment] = {
    new RowMapper[Comment]() {
      def mapRow(rs: ResultSet, rowNum: Int): Comment = {
        new Comment(rs.getLong("ID"),
          rs.getString("TEXT"),
          rs.getString("OWNER_UID"),
          rs.getTimestamp("INSTANT").toLocalDateTime
        )
      }
    }
  }
}
