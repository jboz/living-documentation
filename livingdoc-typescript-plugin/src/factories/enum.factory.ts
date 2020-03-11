import { EnumDeclaration, TypeChecker } from 'typescript';
import { Enum } from '../models/enum';
import { Statement } from '../models/statement';
import { extractComments } from '../utils/comments.utils';
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
    enumStatement.comments = extractComments(declaration, checker);

    if (deep) {
      declaration.members?.forEach(member => {
        this.addMember(GlobalFactory.create(member, enumStatement, checker, false), enumStatement);
      });
    }

    return enumStatement;
  }

  private static addMember(member: Statement | undefined, clazz: Enum) {
    if (member) {
      clazz.members.push(member);
    }
  }
}
