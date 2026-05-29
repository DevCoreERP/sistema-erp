import 'package:flutter/material.dart';
import 'package:table_calendar/table_calendar.dart';
import 'package:intl/intl.dart';
import '../../../../core/constants/app_colors.dart';
import '../../../../core/constants/app_sizes.dart';
import 'package:get_it/get_it.dart';
import '../../../../core/network/api_client.dart';

class ShiftsPage extends StatefulWidget {
  const ShiftsPage({super.key});

  @override
  State<ShiftsPage> createState() => _ShiftsPageState();
}

class _ShiftsPageState extends State<ShiftsPage> {
  late DateTime _focusedDay;
  DateTime? _selectedDay;
  late final DateTime _firstDay;
  late final DateTime _lastDay;

  // Mock data for shifts
  final Map<DateTime, Map<String, String>> _shiftsData = {};

  @override
  void initState() {
    super.initState();
    final now = DateTime.now();
    _focusedDay = now;
    _selectedDay = now;
    
    _firstDay = DateTime(now.year, now.month, 1);
    _lastDay = DateTime(now.year, now.month + 2, 0);

    _generateMockData(now);
  }

  void _generateMockData(DateTime now) {
    setState(() {
      _shiftsData.clear();
      // Generar turnos estáticos para cualquier usuario
      for (int i = -10; i < 20; i++) {
        final date = DateTime(now.year, now.month, now.day + i);
        // Omitir domingos
        if (date.weekday == DateTime.sunday) continue;

        String asistencia = 'Pendiente';
        if (date.isBefore(DateTime(now.year, now.month, now.day))) {
          if (i == -2) {
            asistencia = 'Ausente';
          } else {
            asistencia = 'Asistió';
          }
        }

        _shiftsData[_normalizeDate(date)] = {
          'tipo': 'Turno Regular',
          'entrada': '08:00 AM',
          'comida': '13:00 - 14:00 PM',
          'salida': '17:00 PM',
          'asistencia': asistencia,
        };
      }
    });
  }



  // Normalizar fechas para comparar solo día, mes y año
  DateTime _normalizeDate(DateTime date) {
    return DateTime(date.year, date.month, date.day);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Horario de Turnos')),
      backgroundColor: AppColors.backgroundWhite,
      body: Column(
        children: [
          Container(
            color: AppColors.pureWhite,
            child: TableCalendar(
              locale: 'es_ES',
              firstDay: _firstDay,
              lastDay: _lastDay,
              focusedDay: _focusedDay,
              selectedDayPredicate: (day) => isSameDay(_selectedDay, day),
              onDaySelected: (selectedDay, focusedDay) {
                setState(() {
                  _selectedDay = selectedDay;
                  _focusedDay = focusedDay;
                });
              },
              calendarFormat: CalendarFormat.month,
              availableCalendarFormats: const {
                CalendarFormat.month: 'Mes',
              },
              calendarStyle: const CalendarStyle(
                selectedDecoration: BoxDecoration(
                  color: AppColors.primaryBlue,
                  shape: BoxShape.circle,
                ),
                todayDecoration: BoxDecoration(
                  color: AppColors.lightBlue,
                  shape: BoxShape.circle,
                ),
                markerDecoration: BoxDecoration(
                  color: AppColors.warningOrange,
                  shape: BoxShape.circle,
                ),
              ),
              eventLoader: (day) {
                final normalized = _normalizeDate(day);
                if (_shiftsData.containsKey(normalized)) {
                  return ['Evento'];
                }
                return [];
              },
            ),
          ),
          const SizedBox(height: AppSizes.p16),
          Expanded(
            child: _buildShiftDetails(),
          ),
        ],
      ),
    );
  }

  Widget _buildShiftDetails() {
    if (_selectedDay == null) return const SizedBox();

    final normalizedDate = _normalizeDate(_selectedDay!);
    final shift = _shiftsData[normalizedDate];

    final dateStr = DateFormat('EEEE, d MMMM yyyy', 'es_ES').format(_selectedDay!);

    if (shift == null) {
      return Padding(
        padding: const EdgeInsets.all(AppSizes.p16),
        child: Column(
          children: [
            Text(dateStr, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
            const SizedBox(height: AppSizes.p24),
            const Icon(Icons.free_breakfast, size: 64, color: Colors.grey),
            const SizedBox(height: AppSizes.p16),
            const Text('Día de descanso. No tienes turno asignado.', style: TextStyle(color: AppColors.textDark)),
          ],
        ),
      );
    }

    if (shift['tipo'] == 'Vacaciones') {
      return Padding(
        padding: const EdgeInsets.all(AppSizes.p16),
        child: Card(
          color: AppColors.lightBlue.withOpacity(0.2),
          elevation: 0,
          shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
          child: Padding(
            padding: const EdgeInsets.all(AppSizes.p24),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Text(dateStr, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
                const SizedBox(height: AppSizes.p16),
                const Icon(Icons.beach_access, size: 48, color: AppColors.primaryBlue),
                const SizedBox(height: AppSizes.p16),
                const Text('¡Día de Vacaciones!', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: AppColors.primaryBlue)),
                const SizedBox(height: AppSizes.p8),
                Text(shift['mensaje'] ?? ''),
              ],
            ),
          ),
        ),
      );
    }

    // Configurar color y badge de asistencia
    Color statusColor = Colors.grey;
    IconData statusIcon = Icons.help_outline;
    
    if (shift['asistencia'] == 'Asistió') {
      statusColor = AppColors.successGreen;
      statusIcon = Icons.check_circle;
    } else if (shift['asistencia'] == 'Ausente') {
      statusColor = AppColors.errorRed;
      statusIcon = Icons.cancel;
    } else {
      statusColor = AppColors.warningOrange;
      statusIcon = Icons.schedule;
    }

    return Padding(
      padding: const EdgeInsets.all(AppSizes.p16),
      child: Card(
        elevation: 3,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        child: Padding(
          padding: const EdgeInsets.all(AppSizes.p16),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  Text(
                    dateStr,
                    style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold, color: AppColors.primaryBlue),
                  ),
                  Chip(
                    avatar: Icon(statusIcon, size: 16, color: AppColors.pureWhite),
                    label: Text(
                      shift['asistencia']!,
                      style: const TextStyle(color: AppColors.pureWhite, fontWeight: FontWeight.bold, fontSize: 12),
                    ),
                    backgroundColor: statusColor,
                    visualDensity: VisualDensity.compact,
                  ),
                ],
              ),
              const Divider(height: 24),
              _buildDetailRow(Icons.login, 'Hora de Entrada', shift['entrada']!, AppColors.primaryBlue),
              const SizedBox(height: AppSizes.p16),
              _buildDetailRow(Icons.restaurant, 'Descanso / Comida', shift['comida']!, AppColors.primaryBlue),
              const SizedBox(height: AppSizes.p16),
              _buildDetailRow(Icons.logout, 'Hora de Salida', shift['salida']!, AppColors.primaryBlue),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildDetailRow(IconData icon, String label, String time, Color color) {
    return Row(
      children: [
        Container(
          padding: const EdgeInsets.all(8),
          decoration: BoxDecoration(
            color: color.withOpacity(0.1),
            shape: BoxShape.circle,
          ),
          child: Icon(icon, color: color),
        ),
        const SizedBox(width: AppSizes.p16),
        Expanded(
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(label, style: const TextStyle(color: AppColors.textDark, fontSize: 12)),
              Text(time, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 16)),
            ],
          ),
        ),
      ],
    );
  }
}
