import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ActivityTagFormService } from './activity-tag-form.service';
import { ActivityTagService } from '../service/activity-tag.service';
import { IActivityTag } from '../activity-tag.model';
import { ITag } from 'app/entities/tag/tag.model';
import { TagService } from 'app/entities/tag/service/tag.service';
import { IActivity } from 'app/entities/activity/activity.model';
import { ActivityService } from 'app/entities/activity/service/activity.service';

import { ActivityTagUpdateComponent } from './activity-tag-update.component';

describe('ActivityTag Management Update Component', () => {
  let comp: ActivityTagUpdateComponent;
  let fixture: ComponentFixture<ActivityTagUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let activityTagFormService: ActivityTagFormService;
  let activityTagService: ActivityTagService;
  let tagService: TagService;
  let activityService: ActivityService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [ActivityTagUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(ActivityTagUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(ActivityTagUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    activityTagFormService = TestBed.inject(ActivityTagFormService);
    activityTagService = TestBed.inject(ActivityTagService);
    tagService = TestBed.inject(TagService);
    activityService = TestBed.inject(ActivityService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Tag query and add missing value', () => {
      const activityTag: IActivityTag = { id: 456 };
      const tag: ITag = { id: 25323 };
      activityTag.tag = tag;

      const tagCollection: ITag[] = [{ id: 85572 }];
      jest.spyOn(tagService, 'query').mockReturnValue(of(new HttpResponse({ body: tagCollection })));
      const additionalTags = [tag];
      const expectedCollection: ITag[] = [...additionalTags, ...tagCollection];
      jest.spyOn(tagService, 'addTagToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ activityTag });
      comp.ngOnInit();

      expect(tagService.query).toHaveBeenCalled();
      expect(tagService.addTagToCollectionIfMissing).toHaveBeenCalledWith(tagCollection, ...additionalTags.map(expect.objectContaining));
      expect(comp.tagsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Activity query and add missing value', () => {
      const activityTag: IActivityTag = { id: 456 };
      const activity: IActivity = { id: 32572 };
      activityTag.activity = activity;

      const activityCollection: IActivity[] = [{ id: 46169 }];
      jest.spyOn(activityService, 'query').mockReturnValue(of(new HttpResponse({ body: activityCollection })));
      const additionalActivities = [activity];
      const expectedCollection: IActivity[] = [...additionalActivities, ...activityCollection];
      jest.spyOn(activityService, 'addActivityToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ activityTag });
      comp.ngOnInit();

      expect(activityService.query).toHaveBeenCalled();
      expect(activityService.addActivityToCollectionIfMissing).toHaveBeenCalledWith(
        activityCollection,
        ...additionalActivities.map(expect.objectContaining)
      );
      expect(comp.activitiesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const activityTag: IActivityTag = { id: 456 };
      const tag: ITag = { id: 84411 };
      activityTag.tag = tag;
      const activity: IActivity = { id: 43201 };
      activityTag.activity = activity;

      activatedRoute.data = of({ activityTag });
      comp.ngOnInit();

      expect(comp.tagsSharedCollection).toContain(tag);
      expect(comp.activitiesSharedCollection).toContain(activity);
      expect(comp.activityTag).toEqual(activityTag);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IActivityTag>>();
      const activityTag = { id: 123 };
      jest.spyOn(activityTagFormService, 'getActivityTag').mockReturnValue(activityTag);
      jest.spyOn(activityTagService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ activityTag });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: activityTag }));
      saveSubject.complete();

      // THEN
      expect(activityTagFormService.getActivityTag).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(activityTagService.update).toHaveBeenCalledWith(expect.objectContaining(activityTag));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IActivityTag>>();
      const activityTag = { id: 123 };
      jest.spyOn(activityTagFormService, 'getActivityTag').mockReturnValue({ id: null });
      jest.spyOn(activityTagService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ activityTag: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: activityTag }));
      saveSubject.complete();

      // THEN
      expect(activityTagFormService.getActivityTag).toHaveBeenCalled();
      expect(activityTagService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IActivityTag>>();
      const activityTag = { id: 123 };
      jest.spyOn(activityTagService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ activityTag });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(activityTagService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareTag', () => {
      it('Should forward to tagService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(tagService, 'compareTag');
        comp.compareTag(entity, entity2);
        expect(tagService.compareTag).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareActivity', () => {
      it('Should forward to activityService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(activityService, 'compareActivity');
        comp.compareActivity(entity, entity2);
        expect(activityService.compareActivity).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
