import 'package:flutter/material.dart';
import '../../../../core/constants/app_colors.dart';
import '../../../../core/constants/app_sizes.dart';
import '../../../notifications/presentation/pages/notifications_page.dart';
import '../../../shifts/presentation/pages/shifts_page.dart';
import '../../../permissions/presentation/pages/permissions_page.dart';
import '../../../vacations/presentation/pages/vacations_page.dart';

class HomeContent extends StatelessWidget {
  final bool hasUnreadNotifications;
  final VoidCallback onNotificationTap;
  final VoidCallback onAvatarTap;

  const HomeContent({
    super.key,
    required this.hasUnreadNotifications,
    required this.onNotificationTap,
    required this.onAvatarTap,
  });

  @override
  Widget build(BuildContext context) {
    final screenHeight = MediaQuery.of(context).size.height;
    
    return Column(
      children: [
        // Zona Superior (1/3 de la pantalla aprox)
        Container(
          height: screenHeight * 0.33,
          width: double.infinity,
          decoration: const BoxDecoration(
            color: AppColors.primaryBlue,
          ),
          child: SafeArea(
            bottom: false,
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: AppSizes.p16, vertical: AppSizes.p16),
              child: Column(
                children: [
                  Row(
                    children: [
                      // Botón Hamburguesa
                      InkWell(
                        onTap: () {
                          Scaffold.of(context).openDrawer();
                        },
                        child: Container(
                          padding: const EdgeInsets.all(AppSizes.p8),
                          decoration: const BoxDecoration(
                            color: AppColors.pureWhite,
                            shape: BoxShape.circle,
                          ),
                          child: const Icon(Icons.menu, color: AppColors.primaryBlue),
                        ),
                      ),
                      const SizedBox(width: AppSizes.p12),
                      
                      // Barra de búsqueda
                      Expanded(
                        child: Container(
                          height: 40,
                          decoration: BoxDecoration(
                            color: AppColors.pureWhite,
                            borderRadius: BorderRadius.circular(20), // Forma de píldora
                          ),
                          alignment: Alignment.center,
                          child: const Text(
                            'Buscar función',
                            style: TextStyle(color: Colors.grey, fontSize: 14),
                          ),
                        ),
                      ),
                      const SizedBox(width: AppSizes.p12),
                      
                      // Botón Campana con Badge
                      Stack(
                        clipBehavior: Clip.none,
                        children: [
                          InkWell(
                            onTap: onNotificationTap,
                            child: Container(
                              padding: const EdgeInsets.all(AppSizes.p8),
                              decoration: const BoxDecoration(
                                color: AppColors.pureWhite,
                                shape: BoxShape.circle,
                              ),
                              child: const Icon(Icons.notifications, color: AppColors.primaryBlue),
                            ),
                          ),
                          if (hasUnreadNotifications)
                            Positioned(
                              right: 0,
                              top: 0,
                              child: Container(
                                padding: const EdgeInsets.all(4),
                                decoration: const BoxDecoration(
                                  color: Colors.red,
                                  shape: BoxShape.circle,
                                ),
                                child: const Text(
                                  '1',
                                  style: TextStyle(color: AppColors.pureWhite, fontSize: 10, fontWeight: FontWeight.bold),
                                ),
                              ),
                            ),
                        ],
                      ),
                      const SizedBox(width: AppSizes.p12),
                      
                      // Avatar
                      InkWell(
                        onTap: onAvatarTap,
                        child: const CircleAvatar(
                          backgroundColor: AppColors.lightBlue,
                          child: Icon(Icons.person, color: AppColors.pureWhite),
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ),
        ),
        
        // Área de contenido
        Expanded(
          child: Container(
            color: const Color(0xFFEEEEEE), // Gris muy claro
            width: double.infinity,
            child: GridView.count(
              padding: const EdgeInsets.all(AppSizes.p16),
              crossAxisCount: 2,
              crossAxisSpacing: AppSizes.p16,
              mainAxisSpacing: AppSizes.p16,
              children: [
                _buildHUCard(context, 'Gestión de Turnos', Icons.calendar_month, const Color(0xFF4CAF50), targetPage: const ShiftsPage()),
                _buildHUCard(context, 'Gestión de Permisos', Icons.assignment_late, const Color(0xFFFF9800), targetPage: const PermissionsPage()),
                _buildHUCard(context, 'Gestión de Vacaciones', Icons.beach_access, const Color(0xFF2196F3), targetPage: const VacationsPage()),
                _buildHUCard(context, 'Reportes y RRHH', Icons.analytics, const Color(0xFF9C27B0)),
              ],
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildHUCard(BuildContext context, String title, IconData icon, Color color, {Widget? targetPage}) {
    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: InkWell(
        onTap: () {
          if (targetPage != null) {
            Navigator.push(context, MaterialPageRoute(builder: (_) => targetPage));
          } else {
            ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Módulo $title en desarrollo...')));
          }
        },
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(AppSizes.p16),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.center,
            children: [
              Container(
                padding: const EdgeInsets.all(AppSizes.p12),
                decoration: BoxDecoration(
                  color: color.withOpacity(0.1),
                  shape: BoxShape.circle,
                ),
                child: Icon(icon, size: 32, color: color),
              ),
              const SizedBox(height: AppSizes.p12),
              Text(
                title,
                textAlign: TextAlign.center,
                style: const TextStyle(
                  fontSize: 14,
                  fontWeight: FontWeight.bold,
                  color: AppColors.textDark,
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
