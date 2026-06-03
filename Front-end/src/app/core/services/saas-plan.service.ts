import { Injectable } from '@angular/core';

export type SaasPlanId = 'sin-plan' | 'esencial' | 'profesional' | 'premium';

export type SaasFeatureKey =
  | 'inicio'
  | 'empleados'
  | 'gestion-organizacional'
  | 'asistencia'
  | 'turnos'
  | 'nomina'
  | 'permisos'
  | 'vacaciones'
  | 'reportes-basicos'
  | 'reportes-avanzados'
  | 'autoservicio-empleado'
  | 'usuarios-privilegios'
  | 'roles-permisos'
  | 'reclutamiento'
  | 'onboarding'
  | 'gestion-desempeno'
  | 'capacitacion'
  | 'desarrollo-talento'
  | 'analytics-ia'
  | 'sucesion'
  | 'compensacion-variable'
  | 'integraciones-avanzadas'
  | 'api-ilimitada'
  | 'soporte-prioritario'
  | 'saas';

export interface SaasSubscriptionStorage {
  plan: string;
  estado: string;
  fechaInicio?: string;
  vencimiento?: string;
  usuariosActivos?: number;
  precio?: number;
  moneda?: string;
  usuariosMinimos?: number;
}

export interface SaasFeatureRule {
  key: SaasFeatureKey;
  label: string;
  requiredPlan: SaasPlanId;
  description: string;
}

@Injectable({
  providedIn: 'root',
})
export class SaasPlanService {
  private readonly subscriptionKey = 'saas_active_subscription';

  private readonly planLevel: Record<SaasPlanId, number> = {
    'sin-plan': 0,
    esencial: 1,
    profesional: 2,
    premium: 3,
  };

  private readonly planLabels: Record<SaasPlanId, string> = {
    'sin-plan': 'Sin plan activo',
    esencial: 'Esencial',
    profesional: 'Profesional',
    premium: 'Premium',
  };

  private readonly featureRules: Record<SaasFeatureKey, SaasFeatureRule> = {
    inicio: {
      key: 'inicio',
      label: 'Inicio',
      requiredPlan: 'esencial',
      description: 'Panel principal del sistema.',
    },
    empleados: {
      key: 'empleados',
      label: 'Empleados / Core HR',
      requiredPlan: 'esencial',
      description: 'Gestión básica de personal y expedientes.',
    },
    'gestion-organizacional': {
      key: 'gestion-organizacional',
      label: 'Gestión organizacional',
      requiredPlan: 'esencial',
      description: 'Áreas, departamentos, cargos y estructura interna.',
    },
    asistencia: {
      key: 'asistencia',
      label: 'Asistencia',
      requiredPlan: 'esencial',
      description: 'Control básico de asistencia y ausencias.',
    },
    turnos: {
      key: 'turnos',
      label: 'Turnos laborales',
      requiredPlan: 'esencial',
      description: 'Gestión básica de turnos y asignaciones.',
    },
    nomina: {
      key: 'nomina',
      label: 'Nómina',
      requiredPlan: 'esencial',
      description: 'Gestión de nómina incluida desde el plan Esencial.',
    },
    permisos: {
      key: 'permisos',
      label: 'Permisos',
      requiredPlan: 'esencial',
      description: 'Gestión de permisos laborales y ausencias.',
    },
    vacaciones: {
      key: 'vacaciones',
      label: 'Vacaciones',
      requiredPlan: 'esencial',
      description: 'Gestión de vacaciones y saldo disponible.',
    },
    'reportes-basicos': {
      key: 'reportes-basicos',
      label: 'Reportes básicos',
      requiredPlan: 'esencial',
      description: 'Reportes generales incluidos en el plan Esencial.',
    },
    'reportes-avanzados': {
      key: 'reportes-avanzados',
      label: 'Reportes avanzados',
      requiredPlan: 'profesional',
      description: 'Reportes ampliados para empresas en crecimiento.',
    },
    'autoservicio-empleado': {
      key: 'autoservicio-empleado',
      label: 'Autoservicio del empleado',
      requiredPlan: 'esencial',
      description: 'Portal de autoservicio incluido en el plan Esencial.',
    },
    'usuarios-privilegios': {
      key: 'usuarios-privilegios',
      label: 'Gestión de usuarios con privilegios',
      requiredPlan: 'profesional',
      description: 'Administración avanzada de usuarios según privilegios.',
    },
    'roles-permisos': {
      key: 'roles-permisos',
      label: 'Roles y permisos avanzados',
      requiredPlan: 'profesional',
      description: 'Control avanzado de acceso y permisos por rol.',
    },
    reclutamiento: {
      key: 'reclutamiento',
      label: 'Reclutamiento',
      requiredPlan: 'profesional',
      description: 'Gestión de reclutamiento incluida desde el plan Profesional.',
    },
    onboarding: {
      key: 'onboarding',
      label: 'Onboarding',
      requiredPlan: 'profesional',
      description: 'Proceso de incorporación de personal.',
    },
    'gestion-desempeno': {
      key: 'gestion-desempeno',
      label: 'Gestión del desempeño',
      requiredPlan: 'profesional',
      description: 'Evaluación y seguimiento del desempeño laboral.',
    },
    capacitacion: {
      key: 'capacitacion',
      label: 'Capacitación',
      requiredPlan: 'profesional',
      description: 'Gestión de capacitación y formación interna.',
    },
    'desarrollo-talento': {
      key: 'desarrollo-talento',
      label: 'Desarrollo del talento',
      requiredPlan: 'profesional',
      description: 'Seguimiento del crecimiento profesional del personal.',
    },
    'analytics-ia': {
      key: 'analytics-ia',
      label: 'Analytics con IA',
      requiredPlan: 'premium',
      description: 'Analítica avanzada e inteligencia artificial.',
    },
    sucesion: {
      key: 'sucesion',
      label: 'Sucesión',
      requiredPlan: 'premium',
      description: 'Planificación de sucesión y continuidad de liderazgo.',
    },
    'compensacion-variable': {
      key: 'compensacion-variable',
      label: 'Compensación variable',
      requiredPlan: 'premium',
      description: 'Gestión estratégica de compensaciones variables.',
    },
    'integraciones-avanzadas': {
      key: 'integraciones-avanzadas',
      label: 'Integraciones avanzadas',
      requiredPlan: 'premium',
      description: 'Conexiones avanzadas con otros sistemas.',
    },
    'api-ilimitada': {
      key: 'api-ilimitada',
      label: 'API ilimitada',
      requiredPlan: 'premium',
      description: 'Acceso completo a API ilimitada.',
    },
    'soporte-prioritario': {
      key: 'soporte-prioritario',
      label: 'Soporte prioritario',
      requiredPlan: 'premium',
      description: 'Soporte prioritario incluido solo en Premium.',
    },
    saas: {
      key: 'saas',
      label: 'Mi suscripción',
      requiredPlan: 'sin-plan',
      description: 'Administración de planes, pagos y suscripción.',
    },
  };

  getActiveSubscription(): SaasSubscriptionStorage | null {
    try {
      const raw = localStorage.getItem(this.subscriptionKey);

      if (!raw) {
        return null;
      }

      const subscription = JSON.parse(raw) as SaasSubscriptionStorage;

      if (!subscription || !subscription.plan) {
        return null;
      }

      return subscription;
    } catch {
      return null;
    }
  }

  getActivePlan(): SaasPlanId {
    const subscription = this.getActiveSubscription();

    if (!subscription) {
      return 'sin-plan';
    }

    const estado = this.normalize(subscription.estado);

    if (
      estado.includes('cancelada') ||
      estado.includes('vencida') ||
      estado.includes('suspendida')
    ) {
      return 'sin-plan';
    }

    const plan = this.normalize(subscription.plan);

    if (plan.includes('premium')) {
      return 'premium';
    }

    if (plan.includes('profesional')) {
      return 'profesional';
    }

    if (plan.includes('esencial')) {
      return 'esencial';
    }

    return 'sin-plan';
  }

  hasActiveSubscription(): boolean {
    return this.getActivePlan() !== 'sin-plan';
  }

  canAccess(feature: SaasFeatureKey): boolean {
    const activePlan = this.getActivePlan();
    const requiredPlan = this.getRequiredPlan(feature);

    return this.planLevel[activePlan] >= this.planLevel[requiredPlan];
  }

  isLocked(feature: SaasFeatureKey): boolean {
    return !this.canAccess(feature);
  }

  getRequiredPlan(feature: SaasFeatureKey): SaasPlanId {
    return this.featureRules[feature]?.requiredPlan ?? 'premium';
  }

  getFeatureLabel(feature: SaasFeatureKey): string {
    return this.featureRules[feature]?.label ?? 'Función del sistema';
  }

  getFeatureDescription(feature: SaasFeatureKey): string {
    return this.featureRules[feature]?.description ?? 'Funcionalidad restringida por plan.';
  }

  getPlanLabel(plan: SaasPlanId): string {
    return this.planLabels[plan] ?? 'Plan requerido';
  }

  getRequiredPlanLabel(feature: SaasFeatureKey): string {
    return this.getPlanLabel(this.getRequiredPlan(feature));
  }

  getLockMessage(feature: SaasFeatureKey): string {
    const featureLabel = this.getFeatureLabel(feature);
    const requiredPlanLabel = this.getRequiredPlanLabel(feature);

    return `${featureLabel} está disponible desde el plan ${requiredPlanLabel}.`;
  }

  getAllRules(): SaasFeatureRule[] {
    return Object.values(this.featureRules);
  }

  private normalize(value: string | undefined | null): string {
    if (!value) {
      return '';
    }

    return value
      .toString()
      .trim()
      .toLowerCase()
      .normalize('NFD')
      .replace(/[\u0300-\u036f]/g, '');
  }
}