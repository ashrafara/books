import * as dayjs from 'dayjs';
import { IAuthor } from 'app/entities/author/author.model';

export interface IBook {
  id?: number;
  title?: string;
  isbn?: string | null;
  bookdate?: dayjs.Dayjs;
  distributor?: string;
  producer?: string;
  authors?: IAuthor[] | null;
}

export class Book implements IBook {
  constructor(
    public id?: number,
    public title?: string,
    public isbn?: string | null,
    public bookdate?: dayjs.Dayjs,
    public distributor?: string,
    public producer?: string,
    public authors?: IAuthor[] | null
  ) {}
}

export function getBookIdentifier(book: IBook): number | undefined {
  return book.id;
}
