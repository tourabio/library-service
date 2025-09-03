import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookDetailsModal } from './book-details-modal';

describe('BookDetailsModal', () => {
  let component: BookDetailsModal;
  let fixture: ComponentFixture<BookDetailsModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookDetailsModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BookDetailsModal);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
