import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IActivityTag } from '../activity-tag.model';

@Component({
  selector: 'jhi-activity-tag-detail',
  templateUrl: './activity-tag-detail.component.html',
})
export class ActivityTagDetailComponent implements OnInit {
  activityTag: IActivityTag | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ activityTag }) => {
      this.activityTag = activityTag;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
