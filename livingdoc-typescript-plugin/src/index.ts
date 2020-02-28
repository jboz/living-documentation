#!/usr/bin/env node

import program from 'commander';
import G from 'glob';
import { diagram } from './diagram.mojo';
import { exportDocument } from './export';

program
  .version(require('../package.json').version, '-v, --version', 'output the current version')
  .option('-i, --input <path>', 'define the path of the typescript file')
  .option('-o, --output <path>', "define the path of the output file. If not defined, it'll output on the STDOUT")
  .option('-d, --deep <boolean>', 'indicate if program must through dependancies content or not', true);

program.parse(process.argv);

if (!program.input) {
  console.error('Missing input file');
  process.exit(1);
}

G(<string>program.input, (err: Error | null, matches: string[]) => {
  const document = diagram.generateDiagram(matches, program.deep);
  exportDocument(document, program.output);
});
