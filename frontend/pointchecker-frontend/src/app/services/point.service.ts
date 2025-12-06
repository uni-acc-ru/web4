import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Point, PointRequest } from '../models/point.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class PointService {
  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {}

  private getHeaders(): HttpHeaders {
    const token = this.authService.getToken();
    return new HttpHeaders({
      'Authorization': token ? `Bearer ${token}` : ''
    });
  }

  checkPoint(request: PointRequest): Observable<Point> {
    return this.http.post<Point>(
      `${environment.apiUrl}/points`,
      request,
      { headers: this.getHeaders() }
    );
  }

  getPoints(): Observable<Point[]> {
    return this.http.get<Point[]>(
      `${environment.apiUrl}/points`,
      { headers: this.getHeaders() }
    );
  }

  clearPoints(): Observable<void> {
    return this.http.delete<void>(
      `${environment.apiUrl}/points`,
      { headers: this.getHeaders() }
    );
  }
}
