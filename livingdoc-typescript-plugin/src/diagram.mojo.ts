import { TypeChecker } from 'typescript';
import { ClassDiagramBuilder } from './builders/class-diagram.builder';
import { BaseMojo } from './mojo/base.mojo';

export class Diagram extends BaseMojo {
  instance(checker: TypeChecker): ClassDiagramBuilder {
    return new ClassDiagramBuilder(checker, this.options);
  }
}
