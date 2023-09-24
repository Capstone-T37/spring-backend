import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MeetDetailComponent } from './meet-detail.component';

describe('Meet Management Detail Component', () => {
  let comp: MeetDetailComponent;
  let fixture: ComponentFixture<MeetDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MeetDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ meet: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(MeetDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(MeetDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load meet on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.meet).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
