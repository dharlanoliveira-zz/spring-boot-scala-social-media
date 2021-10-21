Feature: Creating new post

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