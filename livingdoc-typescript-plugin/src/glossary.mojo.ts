import { TypeChecker } from 'typescript';
import { GlossaryBuilder } from './builders/glossary.builder';
import { BaseMojo } from './mojo/base.mojo';

export class Glossary extends BaseMojo {
  instance(checker: TypeChecker): GlossaryBuilder {
    return new GlossaryBuilder(checker, this.options);
  }
}
