import flatten from 'lodash/flatten';
import { NamedDeclaration, TypeChecker } from 'typescript';

export const extractComments = (declaration: NamedDeclaration, checker: TypeChecker): string[] | undefined => {
  if (declaration.name && checker.getSymbolAtLocation(declaration.name)) {
    const symbol = checker.getSymbolAtLocation(declaration.name);
    if (symbol && symbol.getDocumentationComment(checker)) {
      return flatten(
        symbol
          .getDocumentationComment(checker)
          .map(comment => comment.text)
          .map(comment => comment.split(/\r?\n/))
      );
    }
  }
  return undefined;
};
