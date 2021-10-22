Feature: Creating and Updating Comments

  Scenario: Adding comment to a invalid post

    Given exists a user with username "dharlanoliveira"
    When this user try to add a comment to a invalid post
    Then the system will inform that a comment only can be added to a valid post

  Scenario: Adding comment without user

    Given exists a user with username "dharlanoliveira"
    And there is a post with text "My post" from this user
    When you try to add a comment to this post without inform a user
    Then the system will inform that user is required in a comment

  Scenario: Adding comment to a post

    Given exists a user with username "dharlanoliveira"
    And exists a user with username "andrefernandez"
    And there is a post with text "My post" from the user "dharlanoliveira"
    When the user "andrefernandez" add a comment with text "first comment" to this post
    Then comment will be added with text "first comment"

  Scenario: Adding second comment to a post

    Given exists a user with username "dharlanoliveira"
    And exists a user with username "andrefernandez"
    And there is a post with text "My post" from the user "dharlanoliveira"
    When the user "andrefernandez" add a comment with text "first comment" to this post
    When the user "andrefernandez" add a comment with text "second comment" to this post
    Then this post will have two comments

  Scenario: Editing comment

    Given exists a user with username "dharlanoliveira"
    And exists a user with username "andrefernandez"
    And there is a post with text "My post" from the user "dharlanoliveira"
    And there is a comment "first comment" on this post from the user "andrefernandez"
    When the user "andrefernandez" change the text of this comment to "changed comment"
    Then this comment will have the text "changed comment"


  Scenario: Editing comment from other user

    Given exists a user with username "dharlanoliveira"
    And exists a user with username "andrefernandez"
    And there is a post with text "My post" from the user "dharlanoliveira"
    And there is a comment "first comment" on this post from the user "andrefernandez"
    When the user "dharlanoliveira" change the text of this comment to "changed comment"
    Then the system will inform that only the owner can edit the comment