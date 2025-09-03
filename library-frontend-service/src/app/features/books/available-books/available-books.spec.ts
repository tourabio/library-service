import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AvailableBooks } from './available-books';

describe('AvailableBooks', () => {
  let component: AvailableBooks;
  let fixture: ComponentFixture<AvailableBooks>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AvailableBooks]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AvailableBooks);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
