export abstract class Statement {
  constructor(public readonly parent: Statement | undefined, public readonly name: string) {}

  get eol() {
    return `\n`;
  }

  get indent() {
    return '  ';
  }

  public toPlantuml(): string {
    return `${this.name}`;
  }
}
