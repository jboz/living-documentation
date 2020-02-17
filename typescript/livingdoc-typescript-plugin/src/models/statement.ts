export abstract class Statement {
  constructor(public readonly name: string) {}

  public toPlantuml(): string {
    return `${this.name}`;
  }

  get eol() {
    return `\n`;
  }

  get indent() {
    return '  ';
  }
}
