import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IMeet } from '../meet.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../meet.test-samples';

import { MeetService } from './meet.service';

const requireRestSample: IMeet = {
  ...sampleWithRequiredData,
};

describe('Meet Service', () => {
  let service: MeetService;
  let httpMock: HttpTestingController;
  let expectedResult: IMeet | IMeet[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MeetService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Meet', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const meet = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(meet).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Meet', () => {
      const meet = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(meet).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Meet', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Meet', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Meet', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addMeetToCollectionIfMissing', () => {
      it('should add a Meet to an empty array', () => {
        const meet: IMeet = sampleWithRequiredData;
        expectedResult = service.addMeetToCollectionIfMissing([], meet);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(meet);
      });

      it('should not add a Meet to an array that contains it', () => {
        const meet: IMeet = sampleWithRequiredData;
        const meetCollection: IMeet[] = [
          {
            ...meet,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addMeetToCollectionIfMissing(meetCollection, meet);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Meet to an array that doesn't contain it", () => {
        const meet: IMeet = sampleWithRequiredData;
        const meetCollection: IMeet[] = [sampleWithPartialData];
        expectedResult = service.addMeetToCollectionIfMissing(meetCollection, meet);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(meet);
      });

      it('should add only unique Meet to an array', () => {
        const meetArray: IMeet[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const meetCollection: IMeet[] = [sampleWithRequiredData];
        expectedResult = service.addMeetToCollectionIfMissing(meetCollection, ...meetArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const meet: IMeet = sampleWithRequiredData;
        const meet2: IMeet = sampleWithPartialData;
        expectedResult = service.addMeetToCollectionIfMissing([], meet, meet2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(meet);
        expect(expectedResult).toContain(meet2);
      });

      it('should accept null and undefined values', () => {
        const meet: IMeet = sampleWithRequiredData;
        expectedResult = service.addMeetToCollectionIfMissing([], null, meet, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(meet);
      });

      it('should return initial array if no Meet is added', () => {
        const meetCollection: IMeet[] = [sampleWithRequiredData];
        expectedResult = service.addMeetToCollectionIfMissing(meetCollection, undefined, null);
        expect(expectedResult).toEqual(meetCollection);
      });
    });

    describe('compareMeet', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareMeet(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareMeet(entity1, entity2);
        const compareResult2 = service.compareMeet(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareMeet(entity1, entity2);
        const compareResult2 = service.compareMeet(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareMeet(entity1, entity2);
        const compareResult2 = service.compareMeet(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
