import { HeritageClause, InterfaceDeclaration, NodeArray, SyntaxKind, TypeChecker } from 'typescript';
import { Interface } from '../models/interface';
import { Statement } from '../models/statement';
import { Type } from '../models/type';
import { extractComments } from '../utils/comments.utils';
import { GlobalFactory } from './global.factory';

export class InterfaceFactory {
  public static create(declaration: InterfaceDeclaration, checker: TypeChecker, deep = true): Interface | undefined {
    // This is a top level class, get its symbol
    const interfaceSymbol = checker.getSymbolAtLocation(declaration.name);
    if (interfaceSymbol === undefined) {
      return;
    }
    const interfaceStatement = new Interface(interfaceSymbol.getName());
    interfaceStatement.comments = extractComments(declaration, checker);

    if (deep) {
      interfaceSymbol.members?.forEach(member => {
        const declarations = member.getDeclarations();
        if (declarations === undefined) {
          return;
        }
        declarations.forEach(decl => {
          InterfaceFactory.addMember(GlobalFactory.create(decl, interfaceStatement, checker, false), interfaceStatement);
        });
      });
    }
    interfaceSymbol.declarations
      .map(decla => (decla as InterfaceDeclaration).heritageClauses as NodeArray<HeritageClause>)
      .filter(clauses => !!clauses)
      .forEach(clauses =>
        clauses
          .filter(clause => clause.token === SyntaxKind.ExtendsKeyword)
          .forEach(clause => interfaceStatement.inheritance.push(new Type(interfaceStatement, clause.types[0].expression.getText())))
      );

    return interfaceStatement;
  }

  private static addMember(member: Statement | undefined, clazz: Interface) {
    if (member) {
      clazz.members.push(member);
    }
  }
}
