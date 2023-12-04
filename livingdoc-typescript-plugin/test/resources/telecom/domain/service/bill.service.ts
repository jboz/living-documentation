import { Bill } from '../aggregate/bill.model';

export interface BillsService {
  getBills(): Bill[];
  getBill(month: string): Bill;
}
