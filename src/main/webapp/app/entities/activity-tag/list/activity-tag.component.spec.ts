import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ActivityTagService } from '../service/activity-tag.service';

import { ActivityTagComponent } from './activity-tag.component';

describe('ActivityTag Management Component', () => {
  let comp: ActivityTagComponent;
  let fixture: ComponentFixture<ActivityTagComponent>;
  let service: ActivityTagService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [RouterTestingModule.withRoutes([{ path: 'activity-tag', component: ActivityTagComponent }]), HttpClientTestingModule],
      declarations: [ActivityTagComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            data: of({
              defaultSort: 'id,asc',
            }),
            queryParamMap: of(
              jest.requireActual('@angular/router').convertToParamMap({
                page: '1',
                size: '1',
                sort: 'id,desc',
              })
            ),
            snapshot: { queryParams: {} },
          },
        },
      ],
    })
      .overrideTemplate(ActivityTagComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ActivityTagComponent);
    comp = fixture.componentInstance;
    service = TestBed.inject(ActivityTagService);

    const headers = new HttpHeaders();
    jest.spyOn(service, 'query').mockReturnValue(
      of(
        new HttpResponse({
          body: [{ id: 123 }],
          headers,
        })
      )
    );
  });

  it('Should call load all on init', () => {
    // WHEN
    comp.ngOnInit();

    // THEN
    expect(service.query).toHaveBeenCalled();
    expect(comp.activityTags?.[0]).toEqual(expect.objectContaining({ id: 123 }));
  });

  describe('trackId', () => {
    it('Should forward to activityTagService', () => {
      const entity = { id: 123 };
      jest.spyOn(service, 'getActivityTagIdentifier');
      const id = comp.trackId(0, entity);
      expect(service.getActivityTagIdentifier).toHaveBeenCalledWith(entity);
      expect(id).toBe(entity.id);
    });
  });
});
