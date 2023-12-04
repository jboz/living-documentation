import { SourceFile } from 'typescript';
import { Statement } from '../models/statement';

export abstract class BaseBuilder extends Statement {
  abstract addSources(sources: readonly SourceFile[]): BaseBuilder;
  abstract build(): string;
}
