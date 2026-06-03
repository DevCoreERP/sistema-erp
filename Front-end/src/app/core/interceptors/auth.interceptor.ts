import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../../environments/environment';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.url.startsWith(environment.apiBaseUrl)) {
    let headers = req.headers;

    // Inject tenant subdomain header
    const subdomain = localStorage.getItem('tenant_subdomain');
    if (subdomain && !headers.has('X-Tenant-Subdomain')) {
      headers = headers.set('X-Tenant-Subdomain', subdomain);
    }

    // Inject JWT token as Authorization header (fallback for HttpOnly cookie)
    const token = localStorage.getItem('auth_token');
    if (token && !req.url.includes('/auth/login') && !headers.has('Authorization')) {
      headers = headers.set('Authorization', `Bearer ${token}`);
    }

    const authReq = req.clone({
      headers,
      withCredentials: true,
    });
    return next(authReq);
  }
  return next(req);
};

