export abstract class Statement {
  constructor(public readonly name: string) {}

  abstract toPlantuml(): string;

  get eol() {
    return `\n`;
  }

  get indent() {
    return '  ';
  }
}
