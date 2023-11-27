import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ActivityTagDetailComponent } from './activity-tag-detail.component';

describe('ActivityTag Management Detail Component', () => {
  let comp: ActivityTagDetailComponent;
  let fixture: ComponentFixture<ActivityTagDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ActivityTagDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ activityTag: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ActivityTagDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ActivityTagDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load activityTag on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.activityTag).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
