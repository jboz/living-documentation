import fs from 'fs';
import http from 'http';
import path from 'path';
import { encode } from 'plantuml-encoder';

const AVAILABLE_PLANTUML_EXTENSIONS: string[] = ['svg', 'png'];

export const exportDocument = (document: string, output?: string) => {
  if (output === undefined) {
    console.log(document);
    return;
  }

  const extension: string = path.extname(output).replace(/^\./gm, '');

  if (AVAILABLE_PLANTUML_EXTENSIONS.includes(extension)) {
    requestImageFile(output, document, extension);
    return;
  }

  // tslint:disable-next-line non-literal-fs-path
  fs.writeFileSync(output, document, 'utf-8');
};

const requestImageFile = (output: string, input: string, extension: string) => {
  http.get(
    {
      host: 'www.plantuml.com',
      path: `/plantuml/${extension}/${encode(input)}`
    },
    (res: http.IncomingMessage): void => {
      // tslint:disable-next-line non-literal-fs-path
      const fileStream: fs.WriteStream = fs.createWriteStream(output);
      res.setEncoding('utf-8');
      res.pipe(fileStream);
      res.on('error', (err: Error): void => {
        throw err;
      });
    }
  );
};
