import { Component, signal } from '@angular/core';

interface Tab {
  id: string;
  label: string;
  image: string;
  title: string;
  description: string;
  benefits: string[];
}

@Component({
  selector: 'app-features',
  imports: [],
  templateUrl: './features.html',
})
export class Features {
  activeTab = signal('talento');

  tabs: Tab[] = [
    {
      id: 'talento',
      label: 'Gestión de Talento',
      image: 'https://images.unsplash.com/photo-1552664739-d9c6ad95b44a?w=800&q=80',
      title: 'Gestión centralizada de empleados',
      description: 'Unifique toda la información de su personal en una única base de datos segura. Simplifique la incorporación y mantenga los perfiles actualizados en tiempo real.',
      benefits: [
        'Almacene contratos, documentos de identidad y certificados en expedientes digitales.',
        'Automatice las notificaciones de cumpleaños, aniversarios y vencimientos de contratos.',
      ],
    },
    {
      id: 'asistencia',
      label: 'Control de Asistencia',
      image: 'https://images.unsplash.com/photo-1554467386-2194e3f39a86?w=800&q=80',
      title: 'Seguimiento preciso del tiempo laboral',
      description: 'Registre entradas, salidas y ausencias de forma automática. Elimine el papel y reduzca errores manuales en el control de asistencia.',
      benefits: [
        'Configure turnos y horarios flexibles con validación geolocalizada.',
        'Genere reportes de puntualidad y ausentismo en un clic.',
      ],
    },
    {
      id: 'nominas',
      label: 'Cálculo de Nóminas',
      image: 'https://images.unsplash.com/photo-1554224155-6723b9b11710?w=800&q=80',
      title: 'Nóminas calculadas sin errores',
      description: 'Automatice el cálculo de salarios, deducciones y prestaciones. Garantice el cumplimiento normativo y evite penalizaciones.',
      benefits: [
        'Integre directamente con control de asistencia y vacaciones.',
        'Genere recibos de pago digitales y despácheselos por correo automáticamente.',
      ],
    },
    {
      id: 'reportes',
      label: 'Analítica y Reportes',
      image: 'https://images.unsplash.com/photo-1460925895917-afdab827c52f?w=800&q=80',
      title: 'Decisiones basadas en datos reales',
      description: 'Visualice indicadores clave de su capital humano en dashboards interactivos. Identifique tendencias y tome decisiones estratégicas.',
      benefits: [
        'Acceda a métricas de rotación, satisfacción y rendimiento en tiempo real.',
        'Exporte reportes personalizados en PDF y Excel para presentaciones directivas.',
      ],
    },
  ];

  selectTab(tabId: string) {
    this.activeTab.set(tabId);
  }

  get currentTab(): Tab {
    return this.tabs.find((t) => t.id === this.activeTab())!;
  }
}
