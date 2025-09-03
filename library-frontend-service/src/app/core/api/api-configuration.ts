import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

/**
 * Global configuration for API client
 */
@Injectable({
  providedIn: 'root',
})
export class ApiConfiguration {
  rootUrl: string = environment.apiUrl;
}