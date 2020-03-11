Feature: Billing content

    In order to be *informed*
    As a _telecom customer_
    I want to show mensual reporting

    Scenario: show service access details
        Given a bill of 2017-02
        When I get billing of 2017-02 month
        Then the bill of month 2017-02 is returned

    Scenario: show service access details
        Given a bill of 2017-02
        And a call to 079 155 15 15 for 15 minutes the 2017-02-25T09:45:15
        And a call to 079 111 11 11 for 47 minutes the 2017-02-15T20:11:05
        And 14 SMS to the 079 111 11 11 phone number during the 2017-02 month on different moments
        And a SMS to 079 333 33 33 the 2017-02-11T14:15:16
        When I get billing of 2017-02 month
        Then I can show a call to the 079 155 15 15 during 15 minutes the 2017-02-25T09:45:15
        Then I can show a call to the 079 111 11 11 during 47 minutes the 2017-02-15T20:11:05
        And I show the SMS to the 079 333 33 33 the 2017-02-11T14:15:16
        And I show the 14 SMS the 079 111 11 11 in then 2017-02 month

    Scenario: show service access resume calls duration
        Given a bill of 2017-02
        Given a call to 079 111 11 11 for 47 minutes the 2017-02-15T20:11:05
        Given a call to 079 111 11 11 for 11 minutes the 2017-02-20T09:45:15
        Given a call to 079 333 33 33 for 4 minutes the 2017-02-20T11:15:15
        Given a call to 079 111 11 11 for 78 minutes the 2017-02-25T20:11:05
        When I get billing of 2017-02 month
        Then I show a total call of 140 minutes
