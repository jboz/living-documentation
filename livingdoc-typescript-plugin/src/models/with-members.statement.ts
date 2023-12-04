import { Statement } from './statement';

export abstract class WithMembersStatement extends Statement {
  public members: Statement[] = [];
  public inheritance: Statement[] = [];

  constructor(name: string) {
    super(undefined, name);
  }

  abstract get type(): string;
}
