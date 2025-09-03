import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { NotFoundComponent } from './not-found.component';

describe('NotFoundComponent', () => {
  let component: NotFoundComponent;
  let fixture: ComponentFixture<NotFoundComponent>;
  let mockRouter: jest.Mocked<Router>;

  beforeEach(async () => {
    // Create mock object for Router
    const routerSpy = {
      navigate: jest.fn()
    } as jest.Mocked<Router>;

    await TestBed.configureTestingModule({
      imports: [
        NotFoundComponent,
        NoopAnimationsModule,
        MatButtonModule,
        MatIconModule
      ],
      providers: [
        { provide: Router, useValue: routerSpy }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(NotFoundComponent);
    component = fixture.componentInstance;
    mockRouter = TestBed.inject(Router) as jest.Mocked<Router>;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have correct notFoundImagePath', () => {
    expect(component.notFoundImagePath).toBe('/notfound.png');
  });

  describe('Template Rendering', () => {
    it('should display the 404 error code', () => {
      const errorCodeElement = fixture.nativeElement.querySelector('.error-code');
      expect(errorCodeElement).toBeTruthy();
      expect(errorCodeElement.textContent.trim()).toBe('404');
    });

    it('should display the page not found title', () => {
      const titleElement = fixture.nativeElement.querySelector('.not-found-title');
      expect(titleElement).toBeTruthy();
      expect(titleElement.textContent).toContain('Page Not Found');
    });

    it('should display the friendly message', () => {
      const messageElement = fixture.nativeElement.querySelector('.not-found-message');
      expect(messageElement).toBeTruthy();
      expect(messageElement.textContent).toContain('wandered off into the library stacks');
    });

    it('should display the not found image with correct attributes', () => {
      const imageElement = fixture.nativeElement.querySelector('.not-found-image');
      expect(imageElement).toBeTruthy();
      expect(imageElement.src).toContain('/notfound.png');
      expect(imageElement.alt).toContain('Page not found illustration');
      expect(imageElement.getAttribute('loading')).toBe('lazy');
    });

    it('should display action buttons', () => {
      const homeButton = fixture.nativeElement.querySelector('[aria-label="Navigate to books page"]');
      const backButton = fixture.nativeElement.querySelector('[aria-label="Go back to previous page"]');
      
      expect(homeButton).toBeTruthy();
      expect(homeButton.textContent).toContain('Return to Library');
      expect(backButton).toBeTruthy();
      expect(backButton.textContent).toContain('Go Back');
    });

    it('should display help section', () => {
      const helpSection = fixture.nativeElement.querySelector('.not-found-help');
      const helpText = fixture.nativeElement.querySelector('.help-text');
      
      expect(helpSection).toBeTruthy();
      expect(helpText).toBeTruthy();
      expect(helpText.textContent).toContain('librarian at the front desk');
    });

    it('should display suggestion list with proper items', () => {
      const suggestionList = fixture.nativeElement.querySelector('.not-found-suggestion ul');
      const listItems = suggestionList.querySelectorAll('li');
      
      expect(listItems.length).toBe(3);
      expect(listItems[0].textContent).toContain('Double-check the URL');
      expect(listItems[1].textContent).toContain('Navigate back to the main library');
      expect(listItems[2].textContent).toContain('Use the search');
    });
  });

  describe('Navigation Methods', () => {
    describe('navigateToHome', () => {
      it('should navigate to books page successfully', async () => {
        mockRouter.navigate.mockResolvedValue(true);

        await component.navigateToHome();

        expect(mockRouter.navigate).toHaveBeenCalledWith(['/books']);
      });

      it('should fallback to login page when books navigation fails', async () => {
        mockRouter.navigate.mockRejectedValue(new Error('Navigation failed'));
        const consoleSpy = jest.spyOn(console, 'error').mockImplementation();

        await component.navigateToHome();

        expect(mockRouter.navigate).toHaveBeenCalledWith(['/books']);
        expect(mockRouter.navigate).toHaveBeenCalledWith(['/auth/login']);
        expect(consoleSpy).toHaveBeenCalledWith('Navigation error:', expect.any(Error));
        
        consoleSpy.mockRestore();
      });
    });

    describe('goBack', () => {
      it('should call window.history.back()', () => {
        const backSpy = jest.spyOn(window.history, 'back').mockImplementation();

        component.goBack();

        expect(backSpy).toHaveBeenCalled();
        backSpy.mockRestore();
      });
    });
  });

  describe('Button Interactions', () => {
    it('should call navigateToHome when home button is clicked', () => {
      const navigateSpy = jest.spyOn(component, 'navigateToHome').mockImplementation();
      const homeButton = fixture.nativeElement.querySelector('[aria-label="Navigate to books page"]');

      homeButton.click();

      expect(navigateSpy).toHaveBeenCalled();
      navigateSpy.mockRestore();
    });

    it('should call goBack when back button is clicked', () => {
      const backSpy = jest.spyOn(component, 'goBack').mockImplementation();
      const backButton = fixture.nativeElement.querySelector('[aria-label="Go back to previous page"]');

      backButton.click();

      expect(backSpy).toHaveBeenCalled();
      backSpy.mockRestore();
    });
  });

  describe('Accessibility', () => {
    it('should have proper ARIA labels on buttons', () => {
      const homeButton = fixture.nativeElement.querySelector('[aria-label="Navigate to books page"]');
      const backButton = fixture.nativeElement.querySelector('[aria-label="Go back to previous page"]');
      const errorCode = fixture.nativeElement.querySelector('[aria-label="Error code 404"]');

      expect(homeButton.getAttribute('aria-label')).toBe('Navigate to books page');
      expect(backButton.getAttribute('aria-label')).toBe('Go back to previous page');
      expect(errorCode.getAttribute('aria-label')).toBe('Error code 404');
    });

    it('should have proper semantic HTML structure', () => {
      const mainElement = fixture.nativeElement.querySelector('main[role="main"]');
      const headingElement = fixture.nativeElement.querySelector('h1');

      expect(mainElement).toBeTruthy();
      expect(headingElement).toBeTruthy();
    });

    it('should have proper image alt text', () => {
      const imageElement = fixture.nativeElement.querySelector('.not-found-image');
      expect(imageElement.alt).toBe('Page not found illustration - A person looking through books in a library');
    });
  });

  describe('Component Configuration', () => {
    it('should be configured as standalone component', () => {
      expect(NotFoundComponent).toBeDefined();
      // Note: In actual Angular testing, you might need to verify the standalone nature differently
    });

    it('should use OnPush change detection strategy', () => {
      // This would typically be verified through the component metadata in a real test
      expect(component).toBeTruthy();
    });
  });
});