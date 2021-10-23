Feature: Creating and Updating Posts

  Scenario: Creating post wihtout user
    When I try to register a post without inform user
    Then the system will inform that user is required

  Scenario: Creating post without text
    Given exists a user with username "dharlanoliveira"
    When This user try to register a post without inform text
    Then the system will inform that text is required

  Scenario: Creating post without image
    Given exists a user with username "dharlanoliveira"
    When This user try to register a post without inform a image
    Then the post will be saved without image

  Scenario: Creating post to a inexistent user
    When I try to create a post using a invalid user
    Then the system will inform that a correct user is required

  Scenario: Creating complete post
    Given exists a user with username "dharlanoliveira"
    When This user try to register a post with text "My first post" and a image
    Then the post will be saved and image will be scalled down

  Scenario: Updating post that doesn't exists
    When This user try to update text "My first post" of a post that doesn't exists
    Then the system will inform that is required a valid post

  Scenario: Updating post with success
    Given exists a user with username "dharlanoliveira"
    And there is a post with text "My first post" from this user
    When this user try to update text "Changing post" of this post
    Then the post text will became "Changing post"

  Scenario: Updating post of other user
    Given exists a user with username "dharlanoliveira"
    And exists a user with username "andrefernandez"
    And there is a post of the user "dharlanoliveira"
    When the user "andrefernandez" try to update this post
    Then the system will inform that just the owner of the post can update the content

  Scenario: Deleting post
    Given exists a user with username "dharlanoliveira"
    And there is a post with text "My first post" from this user
    When the user "dharlanoliveira" delete this post
    Then the post will be deleted