import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MaterialLibrary } from './material-library';

describe('MaterialLibrary', () => {
  let component: MaterialLibrary;
  let fixture: ComponentFixture<MaterialLibrary>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MaterialLibrary]
    })
    .compileComponents();

    fixture = TestBed.createComponent(MaterialLibrary);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
