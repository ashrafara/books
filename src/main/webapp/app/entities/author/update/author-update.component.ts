import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IAuthor, Author } from '../author.model';
import { AuthorService } from '../service/author.service';

@Component({
  selector: 'jhi-author-update',
  templateUrl: './author-update.component.html',
})
export class AuthorUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    fullname: [],
    phone: [],
    birthdate: [],
    email: [],
  });

  constructor(protected authorService: AuthorService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ author }) => {
      this.updateForm(author);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const author = this.createFromForm();
    if (author.id !== undefined) {
      this.subscribeToSaveResponse(this.authorService.update(author));
    } else {
      this.subscribeToSaveResponse(this.authorService.create(author));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAuthor>>): void {
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

  protected updateForm(author: IAuthor): void {
    this.editForm.patchValue({
      id: author.id,
      fullname: author.fullname,
      phone: author.phone,
      birthdate: author.birthdate,
      email: author.email,
    });
  }

  protected createFromForm(): IAuthor {
    return {
      ...new Author(),
      id: this.editForm.get(['id'])!.value,
      fullname: this.editForm.get(['fullname'])!.value,
      phone: this.editForm.get(['phone'])!.value,
      birthdate: this.editForm.get(['birthdate'])!.value,
      email: this.editForm.get(['email'])!.value,
    };
  }
}
