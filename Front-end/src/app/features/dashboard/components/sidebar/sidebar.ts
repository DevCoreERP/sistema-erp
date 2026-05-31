import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { Router, RouterLink, RouterLinkActive, NavigationEnd } from '@angular/router';
import { filter, Subscription } from 'rxjs';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrls: ['./sidebar.css'],
})
export class Sidebar implements OnInit, OnDestroy {
  private router = inject(Router);
  private routerSubscription?: Subscription;

  orgExpanded: boolean = false;
  turnosExpanded: boolean = false;

  ngOnInit(): void {
    this.actualizarGruposSegunRuta(this.router.url);

    this.routerSubscription = this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event) => {
        const navigation = event as NavigationEnd;
        this.actualizarGruposSegunRuta(navigation.urlAfterRedirects);
      });
  }

  ngOnDestroy(): void {
    this.routerSubscription?.unsubscribe();
  }

  toggleOrg(): void {
    this.orgExpanded = !this.orgExpanded;
  }

  toggleTurnos(): void {
    this.turnosExpanded = !this.turnosExpanded;
  }

  private actualizarGruposSegunRuta(url: string): void {
    if (this.esRutaGestionOrganizacional(url)) {
      this.orgExpanded = true;
    }

    if (this.esRutaTurnos(url)) {
      this.turnosExpanded = true;
    }
  }

  private esRutaGestionOrganizacional(url: string): boolean {
    return (
      url.includes('/areas') ||
      url.includes('/departamentos') ||
      url.includes('/cargos')
    );
  }

  private esRutaTurnos(url: string): boolean {
    return (
      url.includes('/gestion-turnos') ||
      url.includes('/asignacion-turnos')
    );
  }
}