import { SourceFile, TypeChecker } from 'typescript';
import { GlobalParameters } from '../global-parameters';
import { Statement } from '../models/statement';
import { TypescriptParser } from '../parser/typescript.parser';
import { BaseBuilder } from './base.builder';

export class GlossaryBuilder extends BaseBuilder {
  private sources!: readonly SourceFile[];
  private statements: Statement[] = [];

  constructor(private readonly checker: TypeChecker) {
    super(undefined, 'ClassDiagramBuilder');
  }

  public addSources(sources: readonly SourceFile[]): GlossaryBuilder {
    this.sources = sources;

    return this;
  }

  public build(): string {
    this.readSources();
    return this.toTable();
  }

  public toTable() {
    const output = ['', '| ObjectName | Attribute name | Type | Description |', '| ---------- | -------------- | ---- | ----------- |'];
    this.statements.forEach(model => {
      output.push(model.toTable());
    });
    output.push('');

    return output.join(GlobalParameters.eol);
  }

  private readSources() {
    this.statements = TypescriptParser.parse(this.sources, this.checker);
  }
}
