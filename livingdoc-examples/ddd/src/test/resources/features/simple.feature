# language: en
Feature: A simple feature
  This is the feature description.

  Background: Background
    Given a simple background step.

  Scenario: Apple
    Given two apples
    When I eat one apple
    Then there is one apple left.

  Scenario Outline: Second scenario title
    Will add 5 to any number
    Given a number <operand>
    When I add it to 5
    Then the result is <result>
   Examples:
     | operand | result |
     | 0       | 5      |
     | 1       | 6      |
     | 5       | 10     |