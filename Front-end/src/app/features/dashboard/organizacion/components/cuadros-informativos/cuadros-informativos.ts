import { Component, input, Input } from '@angular/core';

@Component({
  selector: 'app-cuadros-informativos',
  standalone: true,
  templateUrl: './cuadros-informativos.component.html'
})
export class CuadrosInformativosComponent {
  @Input() nombreSeccion: string = '';
  @Input() totalAreas: number = 0;
  @Input() activas: number = 0;
  @Input() inactivas: number = 0;
  @Input() filtradas: number = 0;

  get items(){
    return [
      { title: `Total de ${this.nombreSeccion}`, value: this.totalAreas, description: 'Estructura general registrada' },
      { title: 'Activas', value: this.activas, description: 'Disponibles para operación' },
      { title: 'Inactivas', value: this.inactivas, description: 'Fuera de operación temporal' },
      { title: 'Resultados visibles', value: this.filtradas, description: 'Según el filtro aplicado' }
    ];
  }
}
