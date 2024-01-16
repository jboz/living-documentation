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
      |       0 |      5 |
      |       1 |      6 |
      |       5 |     10 |

  Rule: There can be only One

    Example: Only One -- More than one alive
      Given there are 3 ninjas
      And there are more than one ninja alive
      When 2 ninjas meet, they will fight
      Then one ninja dies (but not me)
      And there is one ninja less alive

    Example: Only One -- One alive
      Given there is only 1 ninja alive
      Then he (or she) will live forever ;-)

      Examples: 
        | operand | result |
        |       0 |      5 |
        |       1 |      6 |
        |       5 |     10 |

  Rule: There can be Two (in some cases)

    Example: Two -- Dead and Reborn as Phoenix
