import { Statement } from './statement';

export class Property extends Statement {
  constructor(name: string, public readonly type: Statement | undefined) {
    super(name);
  }

  toPlantuml() {
    return `${this.name}${this.type ? ': ' + this.type.name : ''}`;
  }
}
