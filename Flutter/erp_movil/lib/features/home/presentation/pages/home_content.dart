import 'package:flutter/material.dart';
import '../../../../core/constants/app_colors.dart';
import '../../../../core/constants/app_sizes.dart';
import '../../../notifications/presentation/pages/notifications_page.dart';

class HomeContent extends StatelessWidget {
  const HomeContent({super.key});

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
                            onTap: () {
                              // Navegar a la página de notificaciones (o cambiar pestaña en MainLayout)
                              // Para simplicidad en este widget, podemos abrir la pantalla sobre la actual
                              Navigator.push(context, MaterialPageRoute(builder: (_) => const NotificationsPage()));
                            },
                            child: Container(
                              padding: const EdgeInsets.all(AppSizes.p8),
                              decoration: const BoxDecoration(
                                color: AppColors.pureWhite,
                                shape: BoxShape.circle,
                              ),
                              child: const Icon(Icons.notifications, color: AppColors.primaryBlue),
                            ),
                          ),
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
                      const CircleAvatar(
                        backgroundColor: AppColors.lightBlue,
                        child: Icon(Icons.person, color: AppColors.pureWhite),
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
            child: const Center(
              child: Text(
                'Espacio vacío para listado',
                style: TextStyle(color: AppColors.textLight),
              ),
            ),
          ),
        ),
      ],
    );
  }
}
