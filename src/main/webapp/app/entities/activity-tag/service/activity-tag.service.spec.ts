import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IActivityTag } from '../activity-tag.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../activity-tag.test-samples';

import { ActivityTagService } from './activity-tag.service';

const requireRestSample: IActivityTag = {
  ...sampleWithRequiredData,
};

describe('ActivityTag Service', () => {
  let service: ActivityTagService;
  let httpMock: HttpTestingController;
  let expectedResult: IActivityTag | IActivityTag[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(ActivityTagService);
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

    it('should create a ActivityTag', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const activityTag = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(activityTag).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a ActivityTag', () => {
      const activityTag = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(activityTag).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a ActivityTag', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of ActivityTag', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a ActivityTag', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addActivityTagToCollectionIfMissing', () => {
      it('should add a ActivityTag to an empty array', () => {
        const activityTag: IActivityTag = sampleWithRequiredData;
        expectedResult = service.addActivityTagToCollectionIfMissing([], activityTag);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(activityTag);
      });

      it('should not add a ActivityTag to an array that contains it', () => {
        const activityTag: IActivityTag = sampleWithRequiredData;
        const activityTagCollection: IActivityTag[] = [
          {
            ...activityTag,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addActivityTagToCollectionIfMissing(activityTagCollection, activityTag);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a ActivityTag to an array that doesn't contain it", () => {
        const activityTag: IActivityTag = sampleWithRequiredData;
        const activityTagCollection: IActivityTag[] = [sampleWithPartialData];
        expectedResult = service.addActivityTagToCollectionIfMissing(activityTagCollection, activityTag);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(activityTag);
      });

      it('should add only unique ActivityTag to an array', () => {
        const activityTagArray: IActivityTag[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const activityTagCollection: IActivityTag[] = [sampleWithRequiredData];
        expectedResult = service.addActivityTagToCollectionIfMissing(activityTagCollection, ...activityTagArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const activityTag: IActivityTag = sampleWithRequiredData;
        const activityTag2: IActivityTag = sampleWithPartialData;
        expectedResult = service.addActivityTagToCollectionIfMissing([], activityTag, activityTag2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(activityTag);
        expect(expectedResult).toContain(activityTag2);
      });

      it('should accept null and undefined values', () => {
        const activityTag: IActivityTag = sampleWithRequiredData;
        expectedResult = service.addActivityTagToCollectionIfMissing([], null, activityTag, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(activityTag);
      });

      it('should return initial array if no ActivityTag is added', () => {
        const activityTagCollection: IActivityTag[] = [sampleWithRequiredData];
        expectedResult = service.addActivityTagToCollectionIfMissing(activityTagCollection, undefined, null);
        expect(expectedResult).toEqual(activityTagCollection);
      });
    });

    describe('compareActivityTag', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareActivityTag(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareActivityTag(entity1, entity2);
        const compareResult2 = service.compareActivityTag(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareActivityTag(entity1, entity2);
        const compareResult2 = service.compareActivityTag(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareActivityTag(entity1, entity2);
        const compareResult2 = service.compareActivityTag(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
