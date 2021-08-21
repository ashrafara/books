import * as dayjs from 'dayjs';
import { IAuthor } from 'app/entities/author/author.model';

export interface IBook {
  id?: number;
  title?: string;
  isbn?: string | null;
  description?: string | null;
  content?: string | null;
  links?: string | null;
  bookdate?: dayjs.Dayjs;
  distributor?: string;
  bookImageContentType?: string | null;
  bookImage?: string | null;
  bookPdfContentType?: string | null;
  bookPdf?: string | null;
  producer?: string;
  authors?: IAuthor[] | null;
}

export class Book implements IBook {
  constructor(
    public id?: number,
    public title?: string,
    public isbn?: string | null,
    public description?: string | null,
    public content?: string | null,
    public links?: string | null,
    public bookdate?: dayjs.Dayjs,
    public distributor?: string,
    public bookImageContentType?: string | null,
    public bookImage?: string | null,
    public bookPdfContentType?: string | null,
    public bookPdf?: string | null,
    public producer?: string,
    public authors?: IAuthor[] | null
  ) {}
}

export function getBookIdentifier(book: IBook): number | undefined {
  return book.id;
}
