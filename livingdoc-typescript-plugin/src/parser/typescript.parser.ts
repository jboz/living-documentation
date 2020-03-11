import sortBy from 'lodash/sortBy';
import { forEachChild, SourceFile, TypeChecker } from 'typescript';
import { GlobalFactory } from '../factories/global.factory';
import { Statement } from '../models/statement';

export class TypescriptParser {
  public static parse(sources: readonly SourceFile[], checker: TypeChecker): Statement[] {
    let statements: Statement[] = [];
    sources.forEach(source => {
      if (!source.isDeclarationFile) {
        forEachChild(source, child => {
          // TODO mark deep as false if input filename pattern doesn't match the source path
          const statement = GlobalFactory.create(child, undefined, checker);
          if (statement) {
            statements.push(statement);
          }
        });
      }
    });
    return sortBy(statements, 'name');
  }
}
