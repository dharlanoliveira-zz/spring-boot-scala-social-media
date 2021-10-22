package dharlanoliveira.mysocialmedia.application.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CommentTest {

  @Test
  def shouldReturnEmptyListWhenThereIsntAnyUserMention(): Unit ={

    val comment = new Comment()
    val usersMentions = comment.extractMentions("My name is André")

    assertThat(usersMentions.length).isEqualTo(0)

  }

  @Test
  def shouldReturnOneMentionWhenThereIsOneMention(): Unit ={

    val comment = new Comment()
    val usersMentions = comment.extractMentions("My name is @andre")

    assertThat(usersMentions.length).isEqualTo(1)
    assertThat(usersMentions.head).isEqualTo("andre")

  }

  @Test
  def shouldUnderlineMakePartOfTheUsername(): Unit ={

    val comment = new Comment()
    val usersMentions = comment.extractMentions("My name is @andre_fernandez")

    assertThat(usersMentions.length).isEqualTo(1)
    assertThat(usersMentions.head).isEqualTo("andre_fernandez")

  }

  @Test
  def shouldUppercaseMakePartOfTheUsername(): Unit ={

    val comment = new Comment()
    val usersMentions = comment.extractMentions("My name is @AndreFernandez")

    assertThat(usersMentions.length).isEqualTo(1)
    assertThat(usersMentions.head).isEqualTo("AndreFernandez")

  }

  @Test
  def shouldReturnMultipleMentionsWhenThereIsMoreThanOneUser(): Unit = {
    val comment = new Comment()
    val usersMentions = comment.extractMentions("My name is @dharlanoliveira and i'm married with @pamella and I have a son called @pedro ")

    assertThat(usersMentions.length).isEqualTo(3)
    assertThat(usersMentions(0)).isEqualTo("dharlanoliveira")
    assertThat(usersMentions(1)).isEqualTo("pamella")
    assertThat(usersMentions(2)).isEqualTo("pedro")

  }

}
