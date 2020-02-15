import { Statement } from './statement';

export class Type extends Statement {
  constructor(name: string, public readonly complement: string = '') {
    super(name);
  }

  toPlantuml() {
    return `${this.name}${this.complement}`;
  }
}
