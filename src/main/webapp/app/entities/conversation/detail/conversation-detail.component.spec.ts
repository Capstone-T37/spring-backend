import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ConversationDetailComponent } from './conversation-detail.component';

describe('Conversation Management Detail Component', () => {
  let comp: ConversationDetailComponent;
  let fixture: ComponentFixture<ConversationDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ConversationDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ conversation: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(ConversationDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(ConversationDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load conversation on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.conversation).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
