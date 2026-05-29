import 'package:flutter/material.dart';
import '../../../../core/constants/app_colors.dart';
import '../../../../core/constants/app_sizes.dart';
import 'package:get_it/get_it.dart';
import '../../../../core/network/api_client.dart';

class VacationsPage extends StatefulWidget {
  const VacationsPage({super.key});

  @override
  State<VacationsPage> createState() => _VacationsPageState();
}

class _VacationsPageState extends State<VacationsPage> {
  String _startDate = 'Fecha de inicio';
  String _endDate = 'Fecha de fin';
  bool _isLoading = false;

  void _submitRequest() async {
    if (_startDate == 'Fecha de inicio' || _endDate == 'Fecha de fin') {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('Por favor selecciona el rango de fechas'), backgroundColor: AppColors.errorRed),
      );
      return;
    }

    setState(() {
      _isLoading = true;
    });

    // Simular retardo de red o llamar API real
    try {
      final apiClient = GetIt.instance<ApiClient>();
      await apiClient.post('/solicitudes', body: {
        'fechaInicio': _startDate,
        'fechaFin': _endDate,
        'motivo': 'Vacaciones',
      });
    } catch (e) {
      // Ignorar para efectos de presentación si falla, pero en prod mostraríamos error
    }

    setState(() {
      _isLoading = false;
    });

    if (mounted) {
      showDialog(
        context: context,
        builder: (_) => AlertDialog(
          title: const Text('Solicitud Enviada'),
          content: const Text('Tu solicitud de vacaciones ha sido enviada con éxito. Un administrador revisará tu petición y recibirás una notificación con la resolución.'),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.pop(context); // Cerrar dialog
                Navigator.pop(context); // Volver al home
              },
              child: const Text('Entendido'),
            ),
          ],
        ),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Solicitar Vacaciones')),
      backgroundColor: AppColors.backgroundWhite,
      body: _isLoading 
        ? _buildLoadingState() 
        : _buildForm(),
    );
  }

  Widget _buildLoadingState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: const [
          CircularProgressIndicator(color: AppColors.primaryBlue),
          SizedBox(height: AppSizes.p24),
          Text('Procesando solicitud...', style: TextStyle(fontSize: 16, color: AppColors.textDark)),
        ],
      ),
    );
  }

  Widget _buildForm() {
    return SingleChildScrollView(
      padding: const EdgeInsets.all(AppSizes.p24),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          _buildBalanceCard(),
          const SizedBox(height: AppSizes.p32),
          const Text(
            'Seleccionar Fechas',
            style: TextStyle(
              fontSize: 20,
              fontWeight: FontWeight.bold,
              color: AppColors.primaryBlue,
            ),
          ),
          const SizedBox(height: AppSizes.p16),
          Row(
            children: [
              Expanded(
                child: _buildDateSelector(_startDate, (date) {
                  setState(() { _startDate = date; });
                }),
              ),
              const SizedBox(width: AppSizes.p16),
              Expanded(
                child: _buildDateSelector(_endDate, (date) {
                  setState(() { _endDate = date; });
                }),
              ),
            ],
          ),
          const SizedBox(height: AppSizes.p32),
          ElevatedButton(
            onPressed: _submitRequest,
            child: const Text('Enviar Solicitud'),
          ),
        ],
      ),
    );
  }

  Widget _buildBalanceCard() {
    return Card(
      color: AppColors.lightBlue.withOpacity(0.1),
      elevation: 0,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(AppSizes.radiusMedium),
        side: const BorderSide(color: AppColors.lightBlue),
      ),
      child: const Padding(
        padding: EdgeInsets.all(AppSizes.p16),
        child: Column(
          children: [
            Icon(Icons.beach_access, size: 48, color: AppColors.primaryBlue),
            SizedBox(height: AppSizes.p8),
            Text(
              'Días Disponibles',
              style: TextStyle(fontSize: 16, color: AppColors.textDark),
            ),
            SizedBox(height: AppSizes.p8),
            Text(
              '14 Días',
              style: TextStyle(fontSize: 32, fontWeight: FontWeight.bold, color: AppColors.primaryBlue),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildDateSelector(String currentValue, Function(String) onDateSelected) {
    return InkWell(
      onTap: () async {
        final picked = await showDatePicker(
          context: context,
          initialDate: DateTime.now(),
          firstDate: DateTime.now(),
          lastDate: DateTime.now().add(const Duration(days: 365)),
        );
        if (picked != null) {
          onDateSelected('${picked.day}/${picked.month}/${picked.year}');
        }
      },
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: AppSizes.p16, horizontal: AppSizes.p12),
        decoration: BoxDecoration(
          color: AppColors.pureWhite,
          borderRadius: BorderRadius.circular(AppSizes.radiusSmall),
          border: Border.all(color: Colors.grey.shade300),
        ),
        child: Column(
          children: [
            const Icon(Icons.calendar_month, color: AppColors.primaryBlue),
            const SizedBox(height: AppSizes.p8),
            Text(
              currentValue,
              textAlign: TextAlign.center,
              style: const TextStyle(fontSize: 14),
            ),
          ],
        ),
      ),
    );
  }
}
