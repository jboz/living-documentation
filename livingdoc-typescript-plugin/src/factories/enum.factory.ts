import { EnumDeclaration, TypeChecker } from 'typescript';
import { Class } from '../models/class';
import { Enum } from '../models/enum';
import { Statement } from '../models/statement';
import { GlobalFactory } from './global.factory';

export class EnumFactory {
  public static create(declaration: EnumDeclaration, checker: TypeChecker, deep = true): Enum | undefined {
    if (declaration.name === undefined) {
      return;
    }
    const enumSymbol = checker.getSymbolAtLocation(declaration.name);
    if (enumSymbol === undefined) {
      return;
    }

    const enumStatement = new Enum(enumSymbol.getName());

    if (deep && declaration.members !== undefined) {
      declaration.members.forEach(member => {
        this.addMember(GlobalFactory.create(member, enumStatement, checker, false), enumStatement);
      });
    }

    return enumStatement;
  }

  private static addMember(member: Statement | undefined, clazz: Class) {
    if (member) {
      clazz.members.push(member);
    }
  }
}
