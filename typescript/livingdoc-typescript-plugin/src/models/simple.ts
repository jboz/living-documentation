import { Statement } from './statement';

export class Simple extends Statement {
  constructor(name: string) {
    super(name);
  }

  toPlantuml() {
    return this.name;
  }
}
