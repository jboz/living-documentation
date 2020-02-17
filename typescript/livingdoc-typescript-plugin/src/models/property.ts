import { Statement } from './statement';

export class Property extends Statement {
  constructor(readonly name: string, public readonly types: (Statement | undefined)[], readonly typeName: string | undefined) {
    super(name);
  }

  toPlantuml() {
    return `${this.name}${this.typeName ? ': ' + this.typeName : ''}`;
  }
}
