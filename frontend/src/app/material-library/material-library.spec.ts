import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MaterialLibraryComponent } from './material-library';
import { MaterialService } from '../services/material';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MaterialType } from '../entity/material';
import { of, throwError } from 'rxjs';
import { RouterTestingModule } from '@angular/router/testing';
import { FormsModule } from '@angular/forms';

describe('MaterialLibraryComponent', () => {
  let component: MaterialLibraryComponent;
  let fixture: ComponentFixture<MaterialLibraryComponent>;
  let materialServiceSpy: jasmine.SpyObj<MaterialService>;

  beforeEach(async () => {
    const spy = jasmine.createSpyObj('MaterialService', [
      'getAllMaterials', 
      'getSuggestedMaterials', 
      'filterByInterestAreas'
    ]);
    
    await TestBed.configureTestingModule({
      imports: [
        MaterialLibraryComponent,
        HttpClientTestingModule,
        RouterTestingModule,
        FormsModule
      ],
      providers: [
        { provide: MaterialService, useValue: spy }
      ]
    }).compileComponents();

    materialServiceSpy = TestBed.inject(MaterialService) as jasmine.SpyObj<MaterialService>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MaterialLibraryComponent);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should load all materials on init', () => {
    const mockMaterials = [
      { 
        id: 1, 
        title: 'Material 1', 
        materialType: MaterialType.DOCUMENTO, 
        interestArea: ['TECNOLOGIA_DA_INFORMACAO'],
        userUploaderId: 1
      },
      { 
        id: 2, 
        title: 'Material 2', 
        materialType: MaterialType.VIDEO, 
        url: 'http://example.com',
        interestArea: ['ENGENHARIA_CIVIL'],
        userUploaderId: 1 
      }
    ];

    materialServiceSpy.getAllMaterials.and.returnValue(of(mockMaterials));
    materialServiceSpy.getSuggestedMaterials.and.returnValue(of([]));
    
    fixture.detectChanges();

    expect(component.materials.length).toBe(2);
    expect(component.filteredMaterials.length).toBe(2);
    expect(materialServiceSpy.getAllMaterials).toHaveBeenCalled();
  });

  it('should load suggested materials on init', () => {
    const mockSuggestedMaterials = [
      { 
        id: 3, 
        title: 'Material Sugerido', 
        materialType: MaterialType.LINK, 
        url: 'http://example.com',
        interestArea: ['MATEMATICA_E_ESTATISTICA'],
        userUploaderId: 1
      }
    ];

    materialServiceSpy.getAllMaterials.and.returnValue(of([]));
    materialServiceSpy.getSuggestedMaterials.and.returnValue(of(mockSuggestedMaterials));
    
    fixture.detectChanges();

    expect(component.suggestedMaterials.length).toBe(1);
    expect(materialServiceSpy.getSuggestedMaterials).toHaveBeenCalled();
  });

  it('should filter materials by area', () => {
    const mockFilteredMaterials = [
      { 
        id: 1, 
        title: 'Material 1', 
        materialType: MaterialType.DOCUMENTO, 
        interestArea: ['TECNOLOGIA_DA_INFORMACAO'],
        userUploaderId: 1 
      }
    ];

    materialServiceSpy.getAllMaterials.and.returnValue(of([]));
    materialServiceSpy.getSuggestedMaterials.and.returnValue(of([]));
    materialServiceSpy.filterByInterestAreas.and.returnValue(of(mockFilteredMaterials));
    
    fixture.detectChanges();
    
    component.selectedArea = 'TECNOLOGIA_DA_INFORMACAO';
    component.applyFilters();
    
    expect(materialServiceSpy.filterByInterestAreas).toHaveBeenCalledWith(['TECNOLOGIA_DA_INFORMACAO']);
    expect(component.filteredMaterials.length).toBe(1);
  });

  it('should clear filters correctly', () => {
    const mockMaterials = [
      { 
        id: 1, 
        title: 'Material 1', 
        materialType: MaterialType.DOCUMENTO, 
        interestArea: ['TECNOLOGIA_DA_INFORMACAO'],
        userUploaderId: 1
      },
      { 
        id: 2, 
        title: 'Material 2', 
        materialType: MaterialType.VIDEO, 
        interestArea: ['ENGENHARIA_CIVIL'],
        userUploaderId: 1
      }
    ];

    materialServiceSpy.getAllMaterials.and.returnValue(of(mockMaterials));
    
    component.selectedArea = 'TECNOLOGIA_DA_INFORMACAO';
    component.selectedType = 'DOCUMENTO';
    component.searchTerm = 'teste';
    
    component.clearFilters();
    
    expect(component.selectedArea).toBe('');
    expect(component.selectedType).toBe('');
    expect(component.searchTerm).toBe('');
    expect(materialServiceSpy.getAllMaterials).toHaveBeenCalled();
  });

  it('should handle error when loading materials', () => {
    materialServiceSpy.getAllMaterials.and.returnValue(throwError(() => new Error('API Error')));
    materialServiceSpy.getSuggestedMaterials.and.returnValue(of([]));
    
    spyOn(console, 'error');
    spyOn(window, 'alert');
    
    fixture.detectChanges();
    
    expect(console.error).toHaveBeenCalled();
    expect(component.materials.length).toBe(0);
    expect(component.filteredMaterials.length).toBe(0);
  });
});
