import { GlobalParameters } from '../global-parameters';
import { Statement } from './statement';

export class Property extends Statement {
  public types: (Statement | undefined)[] = [];
  public many: boolean = false;

  constructor(public readonly parent: Statement | undefined, readonly name: string, readonly typeName: string | undefined) {
    super(parent, name);
  }

  public toPlantuml() {
    return `${this.name}${this.typeName ? ': ' + this.typeName : ''}`;
  }

  public toTable() {
    return `||${this.name}|${this.typeName ? this.typeName : ''}|${this.comments?.join(GlobalParameters.br)}|`;
  }
}
