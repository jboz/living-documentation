package ch.ifocusit.livingdoc.example.domain.aggregate

import java.time.LocalDate;
import java.time.MonthDay;

/**
 * Telecom contract
 *
 * @property effectDate Contract effect date.
 */
 data class Contract(val id: String, val effectDate: LocalDate) {

    /**
     * Extract birth day from effect date.
     * @return the contract birth date
     */
     fun getBirthDay() = MonthDay.from(effectDate)
 }