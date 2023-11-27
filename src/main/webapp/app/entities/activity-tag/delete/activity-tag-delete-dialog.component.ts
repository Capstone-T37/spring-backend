import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IActivityTag } from '../activity-tag.model';
import { ActivityTagService } from '../service/activity-tag.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './activity-tag-delete-dialog.component.html',
})
export class ActivityTagDeleteDialogComponent {
  activityTag?: IActivityTag;

  constructor(protected activityTagService: ActivityTagService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.activityTagService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
