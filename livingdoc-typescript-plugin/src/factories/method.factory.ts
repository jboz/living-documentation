import { FunctionDeclaration, TypeChecker } from 'typescript';
import { Method } from '../models/method';
import { Simple } from '../models/simple';
import { Statement } from '../models/statement';
import { GlobalFactory } from './global.factory';

export class MethodFactory {
  public static create(
    parent: Statement | undefined,
    declaration: FunctionDeclaration,
    checker: TypeChecker,
    deep: boolean
  ): Statement | undefined {
    if (declaration.type === undefined) {
      return new Simple(parent, declaration.getFullText());
    }
    if (declaration.name === undefined) {
      return;
    }
    const methodStatement = new Method(parent, declaration.name.getText(), declaration.type.getText());
    methodStatement.paramters = declaration.parameters.map(parameter => GlobalFactory.create(parameter, methodStatement, checker, false));
    methodStatement.returnTypes = [GlobalFactory.create(declaration.type, methodStatement, checker, false)];

    return methodStatement;
  }
}
