import { ClassLikeDeclarationBase, TypeChecker } from 'typescript';
import { Class } from '../models/class';
import { Statement } from '../models/statement';
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

    return classStatement;
  }

  private static addMember(member: Statement | undefined, clazz: Class) {
    if (member) {
      clazz.members.push(member);
    }
  }
}
