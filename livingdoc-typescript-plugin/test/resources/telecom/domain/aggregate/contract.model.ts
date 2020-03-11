import { Customer } from './customer.model';

/**
 * Telecom contract
 */
export interface Contract {
  /**
   * Contract identifier.
   * Generate by the system and communicate to client.
   */
  id: number;

  /**
   * Contract customer.
   */
  customer: Customer;
}
