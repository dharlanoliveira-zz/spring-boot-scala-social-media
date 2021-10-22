Feature: New user registration

  Scenario: Registering new user
    Given There isn't any user registered yet
    When I register a user with username "dharlanoliveira" and email "dharlanoliveira@gmail.com" and password "teste123"
    Then this new user will be created

  Scenario: Registering new user without username
    Given There isn't any user registered yet
    When I register a user without username
    Then the service will warn that username cannot be empty

  Scenario: Registering new user without email
    Given There isn't any user registered yet
    When I register a user without email
    Then the service will warn that email cannot be empty

  Scenario: Registering new user without password
    Given There isn't any user registered yet
    When I register a user without password
    Then the service will warn that password cannot be empty

  Scenario: Registering user with a existent username
    Given There is a user with username "dharlanoliveira"
    When I register a new user with username "dharlanoliveira"
    Then the service will warn that already exists a user registered with this username

  Scenario: Registering user with a existent email
    Given There is a user with email "dharlanoliveira@gmail.com"
    When I register a new user with email "dharlanoliveira@gmail.com"
    Then the service will warn that already exists a user registered with this email