import { Statement } from './statement';

export class Simple extends Statement {
  constructor(public readonly parent: Statement | undefined, name: string) {
    super(parent, name);
  }

  public toPlantuml() {
    return this.name;
  }
}
