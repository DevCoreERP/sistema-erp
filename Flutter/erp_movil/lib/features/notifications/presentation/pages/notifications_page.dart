import 'package:flutter/material.dart';
import '../../../../core/constants/app_colors.dart';
import '../../../../core/constants/app_sizes.dart';
import 'notification_settings_page.dart';

class NotificationsPage extends StatelessWidget {
  const NotificationsPage({super.key});

  @override
  Widget build(BuildContext context) {
    // Historial simulado de solicitudes
    final List<Map<String, dynamic>> history = [
      {
        'title': 'Permiso Médico',
        'reason': 'Cita médica general',
        'dateRange': '25/05/2026',
        'status': 'Aprobado',
        'timestamp': 'Hace 2 días',
      },
      {
        'title': 'Solicitud de Vacaciones',
        'reason': 'Descanso anual',
        'dateRange': '10/06/2026 al 15/06/2026',
        'status': 'Pendiente',
        'timestamp': 'Hace 5 horas',
      },
      {
        'title': 'Permiso Personal',
        'reason': 'Trámites bancarios',
        'dateRange': '12/04/2026',
        'status': 'Rechazado',
        'timestamp': 'El mes pasado',
      },
    ];

    return Scaffold(
      backgroundColor: AppColors.backgroundWhite,
      appBar: AppBar(
        title: const Text('Notificaciones e Historial'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () {
            Navigator.maybePop(context);
          },
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (_) => const NotificationSettingsPage()),
              );
            },
          ),
        ],
      ),
      body: ListView.builder(
        padding: const EdgeInsets.all(AppSizes.p16),
        itemCount: history.length,
        itemBuilder: (context, index) {
          final item = history[index];
          
          Color statusColor = Colors.grey;
          IconData statusIcon = Icons.help;

          if (item['status'] == 'Aprobado') {
            statusColor = AppColors.successGreen;
            statusIcon = Icons.check_circle;
          } else if (item['status'] == 'Pendiente') {
            statusColor = AppColors.warningOrange;
            statusIcon = Icons.access_time;
          } else if (item['status'] == 'Rechazado') {
            statusColor = AppColors.errorRed;
            statusIcon = Icons.cancel;
          }

          return Card(
            elevation: 2,
            margin: const EdgeInsets.only(bottom: AppSizes.p12),
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
            child: Padding(
              padding: const EdgeInsets.all(AppSizes.p16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    mainAxisAlignment: MainAxisAlignment.spaceBetween,
                    children: [
                      Text(
                        item['title'],
                        style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16),
                      ),
                      Text(
                        item['timestamp'],
                        style: const TextStyle(color: Colors.grey, fontSize: 12),
                      ),
                    ],
                  ),
                  const Divider(height: 24),
                  Row(
                    children: [
                      const Icon(Icons.calendar_today, size: 16, color: AppColors.textDark),
                      const SizedBox(width: AppSizes.p8),
                      Text('Fecha(s): ${item['dateRange']}'),
                    ],
                  ),
                  const SizedBox(height: AppSizes.p8),
                  Row(
                    children: [
                      const Icon(Icons.info_outline, size: 16, color: AppColors.textDark),
                      const SizedBox(width: AppSizes.p8),
                      Expanded(child: Text('Motivo: ${item['reason']}')),
                    ],
                  ),
                  const SizedBox(height: AppSizes.p16),
                  Row(
                    mainAxisAlignment: MainAxisAlignment.end,
                    children: [
                      Chip(
                        avatar: Icon(statusIcon, color: AppColors.pureWhite, size: 16),
                        label: Text(
                          item['status'],
                          style: const TextStyle(color: AppColors.pureWhite, fontWeight: FontWeight.bold),
                        ),
                        backgroundColor: statusColor,
                      ),
                    ],
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }
}
