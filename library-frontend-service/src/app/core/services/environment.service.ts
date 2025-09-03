import { Injectable } from '@angular/core';

export interface Environment {
  readonly production: boolean;
  readonly apiUrl: string;
  readonly version: string;
}

@Injectable({
  providedIn: 'root'
})
export class EnvironmentService {
  readonly environment: Environment = {
    production: false,
    apiUrl: 'http://localhost:8080/api',
    version: '1.0.0'
  };
}