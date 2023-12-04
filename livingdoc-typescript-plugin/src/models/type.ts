import { Statement } from './statement';

export class Type extends Statement {
  constructor(public readonly parent: Statement | undefined, name: string) {
    super(parent, name);
  }
}
