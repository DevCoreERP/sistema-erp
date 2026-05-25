import 'package:flutter/material.dart';
import '../../../../core/constants/app_colors.dart';
import '../../../../core/constants/app_sizes.dart';

class NotificationSettingsPage extends StatefulWidget {
  const NotificationSettingsPage({super.key});

  @override
  State<NotificationSettingsPage> createState() => _NotificationSettingsPageState();
}

class _NotificationSettingsPageState extends State<NotificationSettingsPage> {
  bool _soundEnabled = false;
  bool _notificationsEnabled = false;
  bool _hasChanges = false;
  
  // Filter state
  int _selectedFilter = 0; // 0: Más reciente, 1: Más frecuente, 2: Priorizar desactivadas

  void _markAsChanged() {
    setState(() {
      _hasChanges = true;
    });
  }

  void _showFilterDialog() {
    showDialog(
      context: context,
      builder: (BuildContext context) {
        return StatefulBuilder(
          builder: (context, setStateDialog) {
            return AlertDialog(
              backgroundColor: AppColors.pureWhite,
              contentPadding: const EdgeInsets.symmetric(vertical: 20),
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
              content: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  RadioListTile<int>(
                    title: const Text('Más reciente (Predeterminado)'),
                    value: 0,
                    groupValue: _selectedFilter,
                    activeColor: AppColors.primaryBlue,
                    onChanged: (value) {
                      setStateDialog(() => _selectedFilter = value!);
                      setState(() {
                        _selectedFilter = value!;
                        _hasChanges = true;
                      });
                      Navigator.pop(context);
                    },
                  ),
                  RadioListTile<int>(
                    title: const Text('Más frecuente'),
                    value: 1,
                    groupValue: _selectedFilter,
                    activeColor: AppColors.primaryBlue,
                    onChanged: (value) {
                      setStateDialog(() => _selectedFilter = value!);
                      setState(() {
                        _selectedFilter = value!;
                        _hasChanges = true;
                      });
                      Navigator.pop(context);
                    },
                  ),
                  RadioListTile<int>(
                    title: const Text('Priorizar notificaciones desactivadas'),
                    value: 2,
                    groupValue: _selectedFilter,
                    activeColor: AppColors.primaryBlue,
                    onChanged: (value) {
                      setStateDialog(() => _selectedFilter = value!);
                      setState(() {
                        _selectedFilter = value!;
                        _hasChanges = true;
                      });
                      Navigator.pop(context);
                    },
                  ),
                ],
              ),
            );
          }
        );
      },
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.backgroundWhite,
      appBar: AppBar(
        backgroundColor: AppColors.primaryBlue,
        leading: IconButton(
          icon: const Icon(Icons.arrow_back_ios, color: AppColors.pureWhite, size: 20),
          onPressed: () => Navigator.pop(context),
        ),
        title: const Text(
          'CONFIGURACIÓN DE NOTIFICACIONES',
          style: TextStyle(
            color: AppColors.pureWhite,
            fontSize: 16,
            fontWeight: FontWeight.bold,
          ),
        ),
        centerTitle: true,
      ),
      body: Stack(
        children: [
          Column(
            children: [
              Container(
                color: AppColors.pureWhite,
                child: Column(
                  children: [
                    ListTile(
                      leading: const Icon(Icons.sync, color: AppColors.textDark),
                      title: const Text('Restaurar configuración predeterminada'),
                      onTap: () {
                        setState(() {
                          _soundEnabled = false;
                          _notificationsEnabled = false;
                          _selectedFilter = 0;
                          _hasChanges = true;
                        });
                      },
                    ),
                    const Divider(height: 1, thickness: 0.5),
                    ListTile(
                      title: const Text('Sonido de notificaciones'),
                      trailing: Switch(
                        value: _soundEnabled,
                        activeColor: AppColors.primaryBlue,
                        onChanged: (value) {
                          setState(() {
                            _soundEnabled = value;
                            _markAsChanged();
                          });
                        },
                      ),
                    ),
                    const Divider(height: 1, thickness: 0.5),
                    ListTile(
                      title: const Text('Activar/desactivar notificaciones'),
                      trailing: Switch(
                        value: _notificationsEnabled,
                        activeColor: AppColors.primaryBlue,
                        onChanged: (value) {
                          setState(() {
                            _notificationsEnabled = value;
                            _markAsChanged();
                          });
                        },
                      ),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: AppSizes.p16),
              Padding(
                padding: const EdgeInsets.symmetric(horizontal: AppSizes.p16),
                child: Row(
                  children: [
                    Expanded(
                      child: Container(
                        height: 48,
                        padding: const EdgeInsets.symmetric(horizontal: AppSizes.p12),
                        decoration: BoxDecoration(
                          color: Colors.grey.shade200,
                          borderRadius: BorderRadius.circular(24),
                        ),
                        child: Row(
                          children: [
                            const Icon(Icons.search, color: Colors.grey),
                            const SizedBox(width: AppSizes.p8),
                            Expanded(
                              child: TextField(
                                decoration: const InputDecoration(
                                  hintText: 'Buscar la función que desea configurar',
                                  hintStyle: TextStyle(color: Colors.grey, fontSize: 13),
                                  border: InputBorder.none,
                                  enabledBorder: InputBorder.none,
                                  focusedBorder: InputBorder.none,
                                  filled: false,
                                ),
                              ),
                            ),
                          ],
                        ),
                      ),
                    ),
                    const SizedBox(width: AppSizes.p12),
                    InkWell(
                      onTap: _showFilterDialog,
                      child: const Icon(Icons.filter_alt_outlined, color: AppColors.textDark, size: 28),
                    ),
                  ],
                ),
              ),
            ],
          ),
          
          // Botón inferior flotante
          Positioned(
            left: AppSizes.p24,
            right: AppSizes.p24,
            bottom: AppSizes.p32,
            child: ElevatedButton(
              style: ElevatedButton.styleFrom(
                backgroundColor: _hasChanges ? AppColors.primaryBlue : Colors.grey.shade300,
                foregroundColor: _hasChanges ? AppColors.pureWhite : Colors.black87,
                shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(30),
                ),
                padding: const EdgeInsets.symmetric(vertical: AppSizes.p16),
                elevation: _hasChanges ? 4 : 0,
              ),
              onPressed: () {
                if (_hasChanges) {
                  ScaffoldMessenger.of(context).showSnackBar(
                    const SnackBar(content: Text('Configuración guardada')),
                  );
                  setState(() {
                    _hasChanges = false;
                  });
                }
              },
              child: const Text('Guardar configuración', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
            ),
          ),
        ],
      ),
    );
  }
}
