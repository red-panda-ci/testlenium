@develop

Feature: Open google page
  As a user I open the google page

  Scenario Outline: The user opens the google page
    Given the <PAGE> page
    When the google page is full loaded
    Then I can fill the search element with <TEXT>

    Examples:
      | PAGE   | TEXT   |
      | google | google |
