package ch.ifocusit.livingdoc.example.domain.aggregate

import java.time.YearMonth;

/**
 * *Monthly* invoice.
 * [NOTE]
 * Generate by the system at contract birth date.
 *
 * @property month Facturation month.
 * @property contract Contract concerned by the invoice.
 *
 * @since 2020-07-24
 */
data class Invoice(val month: YearMonth, val contract: Contract)
