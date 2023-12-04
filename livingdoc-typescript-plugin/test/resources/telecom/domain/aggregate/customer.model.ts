import { Contract } from './contract.model';

/**
 * Customer of the telecom service
 */
export interface Customer {
  /**
   * Email of the customer.
   */
  email: string;

  /**
   * Customer's contracts.
   */
  contracts?: Contract[];
}
