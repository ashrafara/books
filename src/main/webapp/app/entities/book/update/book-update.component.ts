import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { IBook, Book } from '../book.model';
import { BookService } from '../service/book.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IAuthor } from 'app/entities/author/author.model';
import { AuthorService } from 'app/entities/author/service/author.service';

@Component({
  selector: 'jhi-book-update',
  templateUrl: './book-update.component.html',
})
export class BookUpdateComponent implements OnInit {
  isSaving = false;

  authorsSharedCollection: IAuthor[] = [];

  editForm = this.fb.group({
    id: [],
    title: [null, [Validators.required]],
    isbn: [],
    description: [],
    content: [],
    links: [],
    bookdate: [null, [Validators.required]],
    distributor: [null, [Validators.required]],
    bookImage: [],
    bookImageContentType: [],
    bookPdf: [],
    bookPdfContentType: [],
    producer: [null, [Validators.required]],
    authors: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected bookService: BookService,
    protected authorService: AuthorService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ book }) => {
      this.updateForm(book);

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('boksApp.error', { message: err.message })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const book = this.createFromForm();
    if (book.id !== undefined) {
      this.subscribeToSaveResponse(this.bookService.update(book));
    } else {
      this.subscribeToSaveResponse(this.bookService.create(book));
    }
  }

  trackAuthorById(index: number, item: IAuthor): number {
    return item.id!;
  }

  getSelectedAuthor(option: IAuthor, selectedVals?: IAuthor[]): IAuthor {
    if (selectedVals) {
      for (const selectedVal of selectedVals) {
        if (option.id === selectedVal.id) {
          return selectedVal;
        }
      }
    }
    return option;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBook>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(book: IBook): void {
    this.editForm.patchValue({
      id: book.id,
      title: book.title,
      isbn: book.isbn,
      description: book.description,
      content: book.content,
      links: book.links,
      bookdate: book.bookdate,
      distributor: book.distributor,
      bookImage: book.bookImage,
      bookImageContentType: book.bookImageContentType,
      bookPdf: book.bookPdf,
      bookPdfContentType: book.bookPdfContentType,
      producer: book.producer,
      authors: book.authors,
    });

    this.authorsSharedCollection = this.authorService.addAuthorToCollectionIfMissing(this.authorsSharedCollection, ...(book.authors ?? []));
  }

  protected loadRelationshipsOptions(): void {
    this.authorService
      .query()
      .pipe(map((res: HttpResponse<IAuthor[]>) => res.body ?? []))
      .pipe(
        map((authors: IAuthor[]) =>
          this.authorService.addAuthorToCollectionIfMissing(authors, ...(this.editForm.get('authors')!.value ?? []))
        )
      )
      .subscribe((authors: IAuthor[]) => (this.authorsSharedCollection = authors));
  }

  protected createFromForm(): IBook {
    return {
      ...new Book(),
      id: this.editForm.get(['id'])!.value,
      title: this.editForm.get(['title'])!.value,
      isbn: this.editForm.get(['isbn'])!.value,
      description: this.editForm.get(['description'])!.value,
      content: this.editForm.get(['content'])!.value,
      links: this.editForm.get(['links'])!.value,
      bookdate: this.editForm.get(['bookdate'])!.value,
      distributor: this.editForm.get(['distributor'])!.value,
      bookImageContentType: this.editForm.get(['bookImageContentType'])!.value,
      bookImage: this.editForm.get(['bookImage'])!.value,
      bookPdfContentType: this.editForm.get(['bookPdfContentType'])!.value,
      bookPdf: this.editForm.get(['bookPdf'])!.value,
      producer: this.editForm.get(['producer'])!.value,
      authors: this.editForm.get(['authors'])!.value,
    };
  }
}
