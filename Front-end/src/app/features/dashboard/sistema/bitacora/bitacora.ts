import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Sidebar } from '../../components/sidebar/sidebar';
import { Topbar } from '../../components/topbar/topbar';
import { BitacoraService, BitacoraRegistro, PageBitacora } from '../../../../core/services/bitacora.service';

@Component({
  selector: 'app-bitacora',
  standalone: true,
  imports: [CommonModule, Sidebar, Topbar],
  templateUrl: './bitacora.html',
  styleUrls: ['./bitacora.css']
})
export class Bitacora implements OnInit {
  registros: BitacoraRegistro[] = [];
  cargando: boolean = true;
  
  paginaActual: number = 0;
  pageSize: number = 10;
  totalElementos: number = 0;
  totalPaginas: number = 0;

  // Hacemos Math disponible en la plantilla
  Math = Math;

  constructor(
    private bitacoraService: BitacoraService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarRegistros(0);
  }

  cargarRegistros(page: number): void {
    this.cargando = true;
    this.paginaActual = page;
    
    this.bitacoraService.obtenerRegistros(page, this.pageSize).subscribe({
      next: (data: PageBitacora) => {
        this.registros = data.content;
        this.totalElementos = data.totalElements;
        this.totalPaginas = data.totalPages;
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error cargando bitácora', err);
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  cambiarPagina(nuevaPagina: number): void {
    if (nuevaPagina >= 0 && nuevaPagina < this.totalPaginas && nuevaPagina !== this.paginaActual) {
      this.cargarRegistros(nuevaPagina);
    }
  }

  getPaginasArray(): number[] {
    const paginas = [];
    // Mostrar hasta 5 páginas
    let start = Math.max(0, this.paginaActual - 2);
    let end = Math.min(this.totalPaginas - 1, start + 4);
    
    // Ajustar si estamos cerca del final
    if (end - start < 4) {
      start = Math.max(0, end - 4);
    }
    
    for (let i = start; i <= end; i++) {
      paginas.push(i);
    }
    return paginas;
  }

  getStatusClass(status: number): string {
    if (status >= 200 && status < 300) {
      return 'status-success';
    } else if (status >= 400 && status < 500) {
      return 'status-warning';
    } else if (status >= 500) {
      return 'status-danger';
    }
    return 'status-default';
  }

  formatearFecha(isoString: string): string {
    const d = new Date(isoString);
    return d.toLocaleDateString('es-BO', { day: '2-digit', month: 'short', year: 'numeric' });
  }

  formatearHora(isoString: string): string {
    const d = new Date(isoString);
    return d.toLocaleTimeString('es-BO', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
  }
}
