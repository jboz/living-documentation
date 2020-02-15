import { Statement } from './statement';

export class Method extends Statement {
  constructor(name: string, public readonly paramters: Array<Statement | undefined>, public readonly returnType: Statement | undefined) {
    super(name);
  }

  toPlantuml() {
    return `${this.name}(${this.paramters.filter(statement => !!statement).map(statement => statement?.toPlantuml())})${
      this.returnType ? ': ' + this.returnType.toPlantuml() : ''
    }`;
  }
}
