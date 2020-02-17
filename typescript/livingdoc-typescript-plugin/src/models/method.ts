import { Statement } from './statement';

export class Method extends Statement {
  constructor(
    name: string,
    public readonly paramters: (Statement | undefined)[],
    public readonly returnTypes: (Statement | undefined)[],
    readonly returnLabel: string | undefined
  ) {
    super(name);
  }

  public toPlantuml() {
    return `${this.name}(${this.paramters.filter(statement => !!statement).map(statement => statement?.toPlantuml())})${
      this.returnLabel ? ': ' + this.returnLabel : ''
    }`;
  }
}
