import { Symbol, TypeChecker } from 'typescript';
import { Class } from '../models/class';
import { Statement } from '../models/statement';
import { GlobalFactory } from './global.factory';

export class ClassFactory {
  public static create(classSymbol: Symbol, checker: TypeChecker, deep = true): Class {
    const classStatement = new Class(classSymbol.getName());

    if (deep && classSymbol.members !== undefined) {
      classSymbol.members.forEach(member => {
        const declarations = member.getDeclarations();
        if (declarations === undefined) {
          return;
        }
        declarations.forEach(declaration => {
          ClassFactory.addMember(GlobalFactory.create(declaration, checker, false), classStatement);
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
