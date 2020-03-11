#!/usr/bin/env node

import program from 'commander';
import { Diagram } from './diagram.mojo';
import { exportDocument } from './export';
import { Glossary } from './glossary.mojo';

program
  .version(require('../package.json').version, '-v, --version', 'output the current version')
  .option('-i, --input <path>', 'define the path of the typescript file')
  .option('-o, --output <path>', "define the path of the output file. If not defined, it'll output on the STDOUT")
  .option('-d, --deep <boolean>', 'indicate if program must through dependancies content or not', true);

program
  .command('diagram')
  .alias('d')
  .description('Generate classes diagram')
  .action(() => {
    new Diagram().generateFromPath(<string>program.input, program.deep).then(document => exportDocument(document, program.output));
  });

program
  .command('glossary')
  .alias('g')
  .description('Generate classes glossary')
  .action(() => {
    new Glossary().generateFromPath(<string>program.input, program.deep).then(document => exportDocument(document, program.output));
  });

program.parse(process.argv);

if (!program.input) {
  console.error('Missing input file');
  process.exit(1);
}
