import * as fs from 'fs';
import { createProgram } from 'typescript';
import { ClassDiagramBuilder } from './diagram/class-diagram.builder';

export namespace diagram {
  export const generateDiagram = (roots: ReadonlyArray<string>, onlyDeps = false) => {
    const singleFile = onlyDeps && roots.length === 1 && !fs.lstatSync(roots[0]).isDirectory();
    const program = createProgram(roots, {});
    const diagram = new ClassDiagramBuilder(program.getTypeChecker())
      .addSources(
        program.getSourceFiles().filter(file => {
          return !singleFile || file.fileName.endsWith(roots[0]);
        })
      )
      .build();
    return diagram;
  };
}
