import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IMeet } from '../meet.model';

@Component({
  selector: 'jhi-meet-detail',
  templateUrl: './meet-detail.component.html',
})
export class MeetDetailComponent implements OnInit {
  meet: IMeet | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ meet }) => {
      this.meet = meet;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
