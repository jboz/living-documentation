import { NamedDeclaration, TypeChecker } from 'typescript';

export const extractComment = (declaration: NamedDeclaration, checker: TypeChecker): string | undefined => {
  if (declaration.name && checker.getSymbolAtLocation(declaration.name)) {
    const symbol = checker.getSymbolAtLocation(declaration.name);
    if (symbol && symbol.getDocumentationComment(checker)) {
      return symbol
        .getDocumentationComment(checker)
        .map(comment => comment.text)
        .map(comment => comment.split(/\r?\n/).join(' '))
        .join(' ');
    }
  }
  return undefined;
};
