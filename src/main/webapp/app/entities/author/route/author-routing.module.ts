import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { AuthorComponent } from '../list/author.component';
import { AuthorDetailComponent } from '../detail/author-detail.component';
import { AuthorUpdateComponent } from '../update/author-update.component';
import { AuthorRoutingResolveService } from './author-routing-resolve.service';
import {AuthorAboutComponent} from "../about/author-about.component";

const authorRoute: Routes = [
  {
    path: '',
    component: AuthorComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: AuthorDetailComponent,
    resolve: {
      author: AuthorRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: AuthorUpdateComponent,
    resolve: {
      author: AuthorRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: AuthorUpdateComponent,
    resolve: {
      author: AuthorRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'about',
    component: AuthorAboutComponent,
  },
];

@NgModule({
  imports: [RouterModule.forChild(authorRoute)],
  exports: [RouterModule],
})
export class AuthorRoutingModule {}
