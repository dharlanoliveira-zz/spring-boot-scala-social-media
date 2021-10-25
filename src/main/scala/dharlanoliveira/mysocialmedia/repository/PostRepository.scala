package dharlanoliveira.mysocialmedia.repository

import dharlanoliveira.mysocialmedia.application.domain.{Comment, Post}
import dharlanoliveira.mysocialmedia.application.dto.Order
import dharlanoliveira.mysocialmedia.application.dto.Order.OrderType
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

  @Autowired
  var userRepository: UserRepository = _

  def save(post: Post): Long = {
    if (post.id == 0) insertPost(post) else updatePost(post)
  }

  def getPostsByUser(uid: String, order: OrderType) : List[Post] = {
    val queryPost = new JdbcTemplate(dataSource)
    val sql = s"SELECT * FROM MYSOCIALMEDIA.POSTS WHERE OWNER_UID=? ORDER BY INSTANT ${order.toString}"
    queryPost.query(sql, postRowMapper, uid).asScala.toList
  }

  def getImageByPostId(id: Long): InputStream = {
    val queryPost = new JdbcTemplate(dataSource)
    val sql = "SELECT * FROM MYSOCIALMEDIA.POSTS WHERE ID=?"
    val images = queryPost.query(sql, new RowMapper[InputStream]() {
      def mapRow(rs: ResultSet, rowNum: Int): InputStream = {
        rs.getBlob("IMAGE").getBinaryStream
      }
    }, id).asScala.toList
    if (images.isEmpty) null else images.head
  }

  def getPostById(postId: Long): Post = {
    val queryPost = new JdbcTemplate(dataSource)
    val sql = "SELECT * FROM MYSOCIALMEDIA.POSTS WHERE ID=?"
    val posts = queryPost.query(sql, postRowMapper, postId).asScala.toList
    if (posts.isEmpty) null else posts.head
  }

  def getAll(order: OrderType = Order.DESC): List[Post] = {
    val queryPost = new JdbcTemplate(dataSource)
    val sql = s"SELECT * FROM MYSOCIALMEDIA.POSTS ORDER BY INSTANT ${order.toString}"
    queryPost.query(sql, postRowMapper).asScala.toList
  }

  def deletePost(id: Long): Unit = {
    val deletePost = new JdbcTemplate(dataSource)
    val sql = "DELETE FROM MYSOCIALMEDIA.POSTS WHERE ID=?"
    val comments = getAllCommentsFromPost(id)
    comments.foreach( c => deleteComment(c.id))
    deletePost.update(sql, id)
  }

  def deleteComment(id: Long): Unit = {
    val deleteComment = new JdbcTemplate(dataSource)
    val sqlMentionatedUsers = "DELETE FROM MYSOCIALMEDIA.COMMENTS_USERS WHERE COMMENT_ID=?"
    val sqlComments = "DELETE FROM MYSOCIALMEDIA.COMMENTS WHERE ID=?"
    deleteComment.update(sqlMentionatedUsers, id)
    deleteComment.update(sqlComments, id)
  }

  def getCommentsByPost(postId: Long): List[Comment] = {
    val query = new JdbcTemplate(dataSource)
    val sql = "SELECT * FROM MYSOCIALMEDIA.COMMENTS WHERE POST_ID=?"
    query.query(sql, commentRowMapper, postId).asScala.toList
  }

  def getUserMentionsByComment(commentId: Long): List[String] = {
    val query = new JdbcTemplate(dataSource)
    val sql = "SELECT * FROM MYSOCIALMEDIA.COMMENTS_USERS WHERE COMMENT_ID=?"
    query.query(sql, userMentionsRowMapper, commentId).asScala.toList
  }

  private def getAllCommentsFromPost(postId: Long): List[Comment] = {
    val queryPost = new JdbcTemplate(dataSource)
    val sql = "SELECT * FROM MYSOCIALMEDIA.COMMENTS WHERE POST_ID=?"
    queryPost.query(sql, commentRowMapper,postId).asScala.toList
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

    val holder = new GeneratedKeyHolder

    insertComment.update("INSERT INTO MYSOCIALMEDIA.COMMENTS(POST_ID,OWNER_UID,TEXT,INSTANT) VALUES(:post_id,:owner_uid,:text,:instant)", parameters, holder)

    comment.id = holder.getKey.longValue()
    insertUsersMentions(comment)
  }


  private def updateComment(comment: Comment): Unit = {
    val updateComment = new NamedParameterJdbcTemplate(dataSource)
    val parameters = updateAndInsertCommentParameters(comment)
    parameters.addValue("id", comment.id)
    parameters.addValue("text", comment.text)
    parameters.addValue("instant", comment.instant)

    updateComment.update("UPDATE MYSOCIALMEDIA.COMMENTS SET TEXT=:text,INSTANT=:instant WHERE ID=:id", parameters)

    deleteAllUserMentionsToComment(comment.id)
    insertUsersMentions(comment)
  }

  private def insertUserMention(commentId: Long, mentionedUser: String): Unit = {
    val insertMention = new NamedParameterJdbcTemplate(dataSource)

    val parameters = new MapSqlParameterSource()

    parameters.addValue("user_uid", userRepository.getUserUidByUsername(mentionedUser))
    parameters.addValue("comment_id", commentId)

    insertMention.update("INSERT INTO MYSOCIALMEDIA.COMMENTS_USERS(USER_UID,COMMENT_ID) VALUES(:user_uid,:comment_id)", parameters)
  }

  def deleteAllUserMentionsToComment(commentId: Long) : Unit = {
    val deleteMentions = new NamedParameterJdbcTemplate(dataSource)

    val parameters = new MapSqlParameterSource()
    parameters.addValue("comment_id", commentId)

    deleteMentions.update("DELETE FROM MYSOCIALMEDIA.COMMENTS_USERS WHERE COMMENT_ID=:comment_id", parameters)
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
          rs.getTimestamp("INSTANT").toLocalDateTime,
          getUserMentionsByComment(rs.getLong("ID"))
        )
      }
    }
  }

  private def userMentionsRowMapper: RowMapper[String] = {
    new RowMapper[String]() {
      def mapRow(rs: ResultSet, rowNum: Int): String = {
        userRepository.getUsernameByUserUid(rs.getString("USER_UID"))
      }
    }
  }

  private def insertUsersMentions(comment: Comment): Unit = {
    comment.usersUidMentions.foreach(mention => {
      if (userRepository.existsUserWithUsername(mention)) {
        insertUserMention(comment.id, mention)
      }
    })
  }

}
