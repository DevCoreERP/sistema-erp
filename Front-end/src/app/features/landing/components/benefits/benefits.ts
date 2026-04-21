import { Component, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

interface AccordionItem {
  id: number;
  title: string;
  description: string;
}

@Component({
  selector: 'app-benefits',
  imports: [RouterLink],
  templateUrl: './benefits.html',
})
export class Benefits {
  openItem = signal<number | null>(1);

  items: AccordionItem[] = [
    {
      id: 1,
      title: 'Centralización de Datos',
      description:
        'Deje de buscar archivos en distintas carpetas. Unifique los perfiles, contratos y documentos de todo su personal en una única plataforma digital, accesible con un solo clic.',
    },
    {
      id: 2,
      title: 'Automatización de Nóminas',
      description:
        'Evite los errores humanos en Excel. Nuestro motor de cálculo procesa sueldos, horas extra, bonos y descuentos impositivos automáticamente, reduciendo el tiempo de cierre de mes a la mitad.',
    },
    {
      id: 3,
      title: 'Autogestión del Empleado',
      description:
        'Empodere a su equipo. Ofrezca un portal donde los colaboradores puedan descargar sus boletas de pago, solicitar vacaciones y reportar ausencias sin saturar al departamento de Recursos Humanos.',
    },
  ];

  toggleItem(id: number) {
    this.openItem.update((current) => (current === id ? null : id));
  }

  isOpen(id: number): boolean {
    return this.openItem() === id;
  }
}
