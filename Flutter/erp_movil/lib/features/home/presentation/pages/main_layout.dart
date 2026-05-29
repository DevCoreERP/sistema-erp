import 'package:flutter/material.dart';
import '../../../../core/constants/app_colors.dart';
import '../../../../core/constants/app_sizes.dart';
import '../../../permissions/presentation/pages/permissions_page.dart';
import '../../../shifts/presentation/pages/shifts_page.dart';
import '../../../vacations/presentation/pages/vacations_page.dart';
import '../../../notifications/presentation/pages/notifications_page.dart';
import 'home_content.dart';
import 'work_page.dart';
import 'account_page.dart';

class MainLayout extends StatefulWidget {
  final int initialIndex;
  const MainLayout({super.key, this.initialIndex = 0});

  @override
  State<MainLayout> createState() => _MainLayoutState();
}

class _MainLayoutState extends State<MainLayout> {
  late int _currentIndex;
  bool _hasUnreadNotifications = true;

  @override
  void initState() {
    super.initState();
    _currentIndex = widget.initialIndex;
    if (_currentIndex == 2) {
      _hasUnreadNotifications = false;
    }
  }

  void _onItemTapped(int index) {
    setState(() {
      _currentIndex = index;
      if (index == 2) {
        _hasUnreadNotifications = false;
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    final List<Widget> pages = [
      HomeContent(
        hasUnreadNotifications: _hasUnreadNotifications,
        onNotificationTap: () => _onItemTapped(2),
        onAvatarTap: () => _onItemTapped(3),
      ),
      const WorkPage(),
      const NotificationsPage(),
      const AccountPage(),
    ];

    return Scaffold(
      backgroundColor: AppColors.backgroundWhite,
      drawer: _buildDrawer(context),
      body: pages[_currentIndex],
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _currentIndex,
        onTap: _onItemTapped,
        type: BottomNavigationBarType.fixed,
        backgroundColor: AppColors.pureWhite,
        selectedItemColor: AppColors.primaryBlue,
        unselectedItemColor: AppColors.textDark,
        showUnselectedLabels: true,
        items: const [
          BottomNavigationBarItem(
            icon: Icon(Icons.home_outlined),
            activeIcon: Icon(Icons.home),
            label: 'Inicio',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.work_outline),
            activeIcon: Icon(Icons.work),
            label: 'Trabajo',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.notifications_none),
            activeIcon: Icon(Icons.notifications),
            label: 'Notificaciones',
          ),
          BottomNavigationBarItem(
            icon: Icon(Icons.person_outline),
            activeIcon: Icon(Icons.person),
            label: 'Cuenta',
          ),
        ],
      ),
    );
  }

  Widget _buildDrawer(BuildContext context) {
    return Drawer(
      child: Column(
        children: [
          DrawerHeader(
            decoration: const BoxDecoration(color: AppColors.primaryBlue),
            child: SizedBox(
              width: double.infinity,
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  Image.asset('assets/prismaLogo_transparent.png', height: 48),
                  const SizedBox(height: AppSizes.p12),
                  const Text(
                    'Portal Prisma',
                    style: TextStyle(
                      color: AppColors.pureWhite,
                      fontSize: 20,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ],
              ),
            ),
          ),
          ListTile(
            leading: const Icon(
              Icons.calendar_month,
              color: AppColors.primaryBlue,
            ),
            title: const Text('Turnos'),
            onTap: () {
              Navigator.pop(context); // Close drawer
              Navigator.push(
                context,
                MaterialPageRoute(builder: (_) => const ShiftsPage()),
              );
            },
          ),
          ListTile(
            leading: const Icon(
              Icons.assignment_late,
              color: AppColors.primaryBlue,
            ),
            title: const Text('Permisos'),
            onTap: () {
              Navigator.pop(context);
              Navigator.push(
                context,
                MaterialPageRoute(builder: (_) => const PermissionsPage()),
              );
            },
          ),
          ListTile(
            leading: const Icon(
              Icons.beach_access,
              color: AppColors.primaryBlue,
            ),
            title: const Text('Vacaciones'),
            onTap: () {
              Navigator.pop(context);
              Navigator.push(
                context,
                MaterialPageRoute(builder: (_) => const VacationsPage()),
              );
            },
          ),
        ],
      ),
    );
  }
}
