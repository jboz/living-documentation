import program from 'commander';
import G from 'glob';
import { diagram } from './diagram.mojo';
import { exportDocument } from './export.puml';

program
  .version('0.0.1')
  .option('-i, --input <path>', 'Define the path of the Typescript file')
  .option('-o, --output <path>', "Define the path of the output file. If not defined, it'll output on the STDOUT")
  .option('-d, --deep <boolean>', 'Indicate if program must through dependancies content or not', true);

program.parse(process.argv);

if (!program.input) {
  console.error('Missing input file');
  process.exit(1);
}

G(<string>program.input, (err: Error | null, matches: string[]) => {
  const document = diagram.generateDiagram(matches, program.deep);
  exportDocument(document, program.output);
});