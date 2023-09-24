import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { MeetFormService } from './meet-form.service';
import { MeetService } from '../service/meet.service';
import { IMeet } from '../meet.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';

import { MeetUpdateComponent } from './meet-update.component';

describe('Meet Management Update Component', () => {
  let comp: MeetUpdateComponent;
  let fixture: ComponentFixture<MeetUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let meetFormService: MeetFormService;
  let meetService: MeetService;
  let userService: UserService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [MeetUpdateComponent],
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
      .overrideTemplate(MeetUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MeetUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    meetFormService = TestBed.inject(MeetFormService);
    meetService = TestBed.inject(MeetService);
    userService = TestBed.inject(UserService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const meet: IMeet = { id: 456 };
      const user: IUser = { id: 31497 };
      meet.user = user;

      const userCollection: IUser[] = [{ id: 5442 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ meet });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const meet: IMeet = { id: 456 };
      const user: IUser = { id: 61213 };
      meet.user = user;

      activatedRoute.data = of({ meet });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.meet).toEqual(meet);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMeet>>();
      const meet = { id: 123 };
      jest.spyOn(meetFormService, 'getMeet').mockReturnValue(meet);
      jest.spyOn(meetService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ meet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: meet }));
      saveSubject.complete();

      // THEN
      expect(meetFormService.getMeet).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(meetService.update).toHaveBeenCalledWith(expect.objectContaining(meet));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMeet>>();
      const meet = { id: 123 };
      jest.spyOn(meetFormService, 'getMeet').mockReturnValue({ id: null });
      jest.spyOn(meetService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ meet: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: meet }));
      saveSubject.complete();

      // THEN
      expect(meetFormService.getMeet).toHaveBeenCalled();
      expect(meetService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IMeet>>();
      const meet = { id: 123 };
      jest.spyOn(meetService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ meet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(meetService.update).toHaveBeenCalled();
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
  });
});
