import { RootAggregate } from '../../../../../src/decorators/root-aggregate.decorators';
import { Access } from './access.model';
import { Contract } from './contract.model';
import { PaymentState } from './payment-state.model';

/**
 * Monthly bill.
 */
@RootAggregate
export class Bill {
  constructor(
    /**
     * Which month of the bill.
     */
    month: string,

    /**
     * Contract concerned by the bill.
     */
    contract: Contract,

    /**
     * Bill contents.
     */
    accesses: Access[],

    /**
     * Bill payment state
     */
    paymentState: PaymentState
  ) {}
}
