import { InterfaceDeclaration, TypeChecker } from 'typescript';
import { Class } from '../models/class';
import { Interface } from '../models/interface';
import { Statement } from '../models/statement';
import { GlobalFactory } from './global.factory';

export class InterfaceFactory {
  public static create(declaration: InterfaceDeclaration, checker: TypeChecker, deep = true): Interface | undefined {
    // This is a top level class, get its symbol
    const interfaceSymbol = checker.getSymbolAtLocation(declaration.name);
    if (interfaceSymbol === undefined) {
      return;
    }
    const interfaceStatement = new Interface(interfaceSymbol.getName());

    if (deep && interfaceSymbol.members !== undefined) {
      interfaceSymbol.members.forEach(member => {
        const declarations = member.getDeclarations();
        if (declarations === undefined) {
          return;
        }
        declarations.forEach(decl => {
          InterfaceFactory.addMember(GlobalFactory.create(decl, interfaceStatement, checker, false), interfaceStatement);
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
