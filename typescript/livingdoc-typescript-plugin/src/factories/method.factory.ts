import { FunctionDeclaration, TypeChecker } from 'typescript';
import { Method } from '../models/method';
import { Simple } from '../models/simple';
import { Statement } from '../models/statement';
import { GlobalFactory } from './global.factory';

export class MethodFactory {
  static create(declaration: FunctionDeclaration, checker: TypeChecker, deep: boolean): Statement | undefined {
    if (declaration.type === undefined) {
      return new Simple(declaration.getFullText());
    }
    if (declaration.name === undefined) {
      return;
    }
    const parameters = declaration.parameters.map(parameter => GlobalFactory.create(parameter, checker, false));
    const returnType = GlobalFactory.create(declaration.type, checker, false);
    return new Method(declaration.name.getText(), parameters, [returnType], declaration.type.getText());
  }
}
