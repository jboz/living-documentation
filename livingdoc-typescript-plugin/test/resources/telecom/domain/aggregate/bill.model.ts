import { Access } from './access.model';
import { Contract } from './contract.model';
import { PaymentState } from './payment-state.model';

/**
 * Monthly bill.
 */
export interface Bill {
  /**
   * Which month of the bill.
   */
  month: string;

  /**
   * Contract concerned by the bill.
   */
  contract: Contract;

  /**
   * Bill contents.
   */
  accesses: Access[];

  /**
   * Bill payment state
   */
  paymentState: PaymentState;
}
