import G from 'glob';
import { createProgram, TypeChecker } from 'typescript';
import { Options } from '..';
import { BaseBuilder } from '../builders/base.builder';

export abstract class BaseMojo {
  constructor(readonly options?: Options) {}

  generateFromPath = (path: string, deep = true): Promise<string> => {
    return new Promise<string[]>((resolve, reject) => {
      G(path, (err: Error | null, matches: string[]) => {
        if (err) {
          return reject(err);
        }
        return resolve(matches);
      });
    }).then(matches => this.generate(matches, deep));
  };

  abstract instance(checker: TypeChecker): BaseBuilder;

  generate = (roots: ReadonlyArray<string>, deep = true): string => {
    // if true, only reference of dependencies will be showned, without content
    const singleFile = !deep && roots.length === 1; // && !fs.lstatSync(roots[0]).isDirectory();
    const program = createProgram(roots, {});
    const glossary = this.instance(program.getTypeChecker())
      .addSources(
        program.getSourceFiles().filter(file => {
          return !singleFile || file.fileName.endsWith(roots[0]);
        })
      )
      .build();

    return glossary;
  };
}
