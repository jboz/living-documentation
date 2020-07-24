#!/usr/bin/env node

import commander from 'commander';
import { Diagram } from './diagram.mojo';
import { exportDocument } from './export';
import { Glossary } from './glossary.mojo';

export interface Options {
  links: boolean;
}

program
  .version(require('../package.json').version, '-v, --version', 'output the current version')
  .requiredOption('-i, --input <path>', 'define the path of the typescript file')
  .option('-o, --output <path>', 'define the path of the output file. If not defined, it\'ll output on the STDOUT')
  .option('-d, --deep', 'indicate if program must through dependencies content or not', true);

program
  .command('diagram')
  .alias('d')
  .description('Generate classes diagram')
  .option('-l, --links', 'indicate if diagram must contains links', false)
  .action((options: Options) => {
    new Diagram(options).generateFromPath(<string>program.input, program.deep).then(document => exportDocument(document, program.output));
  });

program
  .command('glossary')
  .alias('g')
  .description('Generate classes glossary')
  .action(() => {
    new Glossary().generateFromPath(<string>program.input, program.deep).then(document => exportDocument(document, program.output));
  });

program.parse(process.argv);
