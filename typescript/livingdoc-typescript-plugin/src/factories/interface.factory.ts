import { Symbol, TypeChecker } from 'typescript';
import { Class } from '../models/class';
import { Interface } from '../models/interface';
import { Statement } from '../models/statement';
import { GlobalFactory } from './global.factory';

export class InterfaceFactory {
  public static create(classSymbol: Symbol, checker: TypeChecker, deep = true): Interface {
    const interfaceStatement = new Interface(classSymbol.getName());

    if (deep && classSymbol.members !== undefined) {
      classSymbol.members.forEach(member => {
        const declarations = member.getDeclarations();
        if (declarations === undefined) {
          return;
        }
        declarations.forEach(declaration => {
          InterfaceFactory.addMember(GlobalFactory.create(declaration, checker, false), interfaceStatement);
        });
      });
    }

    return interfaceStatement;
  }

  private static addMember(member: Statement | undefined, clazz: Class) {
    if (member) {
      clazz.members.push(member);
    }
  }
}
