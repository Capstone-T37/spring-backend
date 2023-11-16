import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { RequestFormService } from './request-form.service';
import { RequestService } from '../service/request.service';
import { IRequest } from '../request.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IMeet } from 'app/entities/meet/meet.model';
import { MeetService } from 'app/entities/meet/service/meet.service';

import { RequestUpdateComponent } from './request-update.component';

describe('Request Management Update Component', () => {
  let comp: RequestUpdateComponent;
  let fixture: ComponentFixture<RequestUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let requestFormService: RequestFormService;
  let requestService: RequestService;
  let userService: UserService;
  let meetService: MeetService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [RequestUpdateComponent],
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
      .overrideTemplate(RequestUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(RequestUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    requestFormService = TestBed.inject(RequestFormService);
    requestService = TestBed.inject(RequestService);
    userService = TestBed.inject(UserService);
    meetService = TestBed.inject(MeetService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const request: IRequest = { id: 456 };
      const user: IUser = { id: 94884 };
      request.user = user;

      const userCollection: IUser[] = [{ id: 95888 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ request });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Meet query and add missing value', () => {
      const request: IRequest = { id: 456 };
      const meet: IMeet = { id: 26474 };
      request.meet = meet;

      const meetCollection: IMeet[] = [{ id: 68606 }];
      jest.spyOn(meetService, 'query').mockReturnValue(of(new HttpResponse({ body: meetCollection })));
      const additionalMeets = [meet];
      const expectedCollection: IMeet[] = [...additionalMeets, ...meetCollection];
      jest.spyOn(meetService, 'addMeetToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ request });
      comp.ngOnInit();

      expect(meetService.query).toHaveBeenCalled();
      expect(meetService.addMeetToCollectionIfMissing).toHaveBeenCalledWith(
        meetCollection,
        ...additionalMeets.map(expect.objectContaining)
      );
      expect(comp.meetsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const request: IRequest = { id: 456 };
      const user: IUser = { id: 40999 };
      request.user = user;
      const meet: IMeet = { id: 92016 };
      request.meet = meet;

      activatedRoute.data = of({ request });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.meetsSharedCollection).toContain(meet);
      expect(comp.request).toEqual(request);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRequest>>();
      const request = { id: 123 };
      jest.spyOn(requestFormService, 'getRequest').mockReturnValue(request);
      jest.spyOn(requestService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ request });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: request }));
      saveSubject.complete();

      // THEN
      expect(requestFormService.getRequest).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(requestService.update).toHaveBeenCalledWith(expect.objectContaining(request));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRequest>>();
      const request = { id: 123 };
      jest.spyOn(requestFormService, 'getRequest').mockReturnValue({ id: null });
      jest.spyOn(requestService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ request: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: request }));
      saveSubject.complete();

      // THEN
      expect(requestFormService.getRequest).toHaveBeenCalled();
      expect(requestService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IRequest>>();
      const request = { id: 123 };
      jest.spyOn(requestService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ request });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(requestService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareMeet', () => {
      it('Should forward to meetService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(meetService, 'compareMeet');
        comp.compareMeet(entity, entity2);
        expect(meetService.compareMeet).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
