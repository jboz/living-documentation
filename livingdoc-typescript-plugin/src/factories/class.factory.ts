import { ClassDeclaration, ClassLikeDeclarationBase, HeritageClause, NodeArray, SyntaxKind, TypeChecker } from 'typescript';
import { Class } from '../models/class';
import { Statement } from '../models/statement';
import { Type } from '../models/type';
import { extractComment } from '../utils/comments.utils';
import { GlobalFactory } from './global.factory';

export class ClassFactory {
  public static create(declaration: ClassLikeDeclarationBase, checker: TypeChecker, deep = true): Class | undefined {
    if (declaration.name === undefined) {
      return;
    }
    // This is a top level class, get its symbol
    const classSymbol = checker.getSymbolAtLocation(declaration.name);
    if (classSymbol === undefined) {
      return;
    }

    const classStatement = new Class(classSymbol.getName());
    classStatement.comment = extractComment(declaration, checker);

    if (deep && classSymbol.members !== undefined) {
      classSymbol.members.forEach(member => {
        const declarations = member.getDeclarations();
        if (declarations === undefined) {
          return;
        }
        declarations.forEach(decl => {
          ClassFactory.addMember(GlobalFactory.create(decl, classStatement, checker, false), classStatement);
        });
      });
    }
    classSymbol.declarations
      .map(decla => (decla as ClassDeclaration).heritageClauses as NodeArray<HeritageClause>)
      .filter(clauses => !!clauses)
      .forEach(clauses =>
        clauses
          .filter(clause => clause.token === SyntaxKind.ExtendsKeyword)
          .forEach(clause => classStatement.inheritance.push(new Type(classStatement, clause.types[0].expression.getText())))
      );

    return classStatement;
  }

  private static addMember(member: Statement | undefined, clazz: Class) {
    if (member) {
      clazz.members.push(member);
    }
  }
}
