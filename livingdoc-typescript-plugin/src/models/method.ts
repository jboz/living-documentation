import { Statement } from './statement';

export class Method extends Statement {
  public paramters: (Statement | undefined)[] = [];
  public returnTypes: (Statement | undefined)[] = [];

  constructor(public readonly parent: Statement | undefined, name: string, readonly returnLabel: string | undefined) {
    super(parent, name);
  }

  public toPlantuml() {
    return `${this.name}(${this.paramters.filter(statement => !!statement).map(statement => statement?.toPlantuml())})${
      this.returnLabel ? ': ' + this.returnLabel : ''
    }`;
  }
}
