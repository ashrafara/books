import { IBook } from 'app/entities/book/book.model';

export interface IAuthor {
  id?: number;
  fullname?: string | null;
  phone?: string | null;
  birthdate?: string | null;
  email?: string | null;
  books?: IBook[] | null;
}

export class Author implements IAuthor {
  constructor(
    public id?: number,
    public fullname?: string | null,
    public phone?: string | null,
    public birthdate?: string | null,
    public email?: string | null,
    public books?: IBook[] | null
  ) {}
}

export function getAuthorIdentifier(author: IAuthor): number | undefined {
  return author.id;
}
