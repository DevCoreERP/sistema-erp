import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../../../core/constants/app_colors.dart';
import '../../../../core/constants/app_sizes.dart';
import '../../../../injection_container.dart' as di;
import '../../../auth/domain/entities/user.dart';
import '../../../auth/data/models/user_model.dart';

class AccountPage extends StatefulWidget {
  const AccountPage({super.key});

  @override
  State<AccountPage> createState() => _AccountPageState();
}

class _AccountPageState extends State<AccountPage> {
  UserEntity? _currentUser;
  
  final _nameController = TextEditingController();
  final _surnameController = TextEditingController();
  final _emailController = TextEditingController();
  final _phoneController = TextEditingController();

  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _loadUserData();
  }

  void _loadUserData() {
    final prefs = di.sl<SharedPreferences>();
    final userJson = prefs.getString('active_user');
    if (userJson != null) {
      final user = UserModel.fromJson(json.decode(userJson));
      setState(() {
        _currentUser = user;
        _nameController.text = user.name;
        _surnameController.text = user.surname;
        _emailController.text = user.email;
        _phoneController.text = user.phone;
      });
    }
  }

  void _requestModification() {
    // Verificar si hay cambios reales
    if (_nameController.text == _currentUser?.name &&
        _surnameController.text == _currentUser?.surname &&
        _emailController.text == _currentUser?.email &&
        _phoneController.text == _currentUser?.phone) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('No has modificado ningún campo.')),
      );
      return;
    }

    // Mostrar modal de confirmación
    showDialog(
      context: context,
      builder: (context) {
        return AlertDialog(
          title: const Text('Confirmar Modificación'),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text('¿Estás seguro que deseas solicitar la modificación de tu información al administrador?', style: TextStyle(fontWeight: FontWeight.bold)),
              const SizedBox(height: AppSizes.p16),
              const Text('Nuevos datos:', style: TextStyle(color: AppColors.primaryBlue)),
              const SizedBox(height: AppSizes.p8),
              Text('Nombre: ${_nameController.text}'),
              Text('Apellido: ${_surnameController.text}'),
              Text('Email: ${_emailController.text}'),
              Text('Teléfono: ${_phoneController.text}'),
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('Cancelar', style: TextStyle(color: Colors.grey)),
            ),
            ElevatedButton(
              onPressed: () {
                Navigator.pop(context);
                _sendRequestToAdmin();
              },
              child: const Text('Aceptar'),
            ),
          ],
        );
      },
    );
  }

  void _sendRequestToAdmin() async {
    setState(() {
      _isLoading = true;
    });

    // Simular tiempo de petición al backend
    await Future.delayed(const Duration(seconds: 3));

    setState(() {
      _isLoading = false;
    });

    if (mounted) {
      showDialog(
        context: context,
        builder: (context) {
          return AlertDialog(
            title: const Row(
              children: [
                Icon(Icons.check_circle, color: AppColors.successGreen),
                SizedBox(width: 8),
                Text('Petición Enviada'),
              ],
            ),
            content: const Text('Tu petición para modificar los datos fue enviada con éxito. El administrador la revisará a la brevedad.'),
            actions: [
              TextButton(
                onPressed: () {
                  Navigator.pop(context);
                  // Opcional: recargar datos originales hasta que se apruebe
                  _loadUserData(); 
                },
                child: const Text('Entendido'),
              ),
            ],
          );
        },
      );
    }
  }

  @override
  void dispose() {
    _nameController.dispose();
    _surnameController.dispose();
    _emailController.dispose();
    _phoneController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (_currentUser == null) {
      return const Scaffold(
        body: Center(child: CircularProgressIndicator()),
      );
    }

    return Scaffold(
      backgroundColor: AppColors.backgroundWhite,
      appBar: AppBar(
        title: const Text('Mi Perfil'),
      ),
      body: _isLoading
          ? const Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  CircularProgressIndicator(),
                  SizedBox(height: 16),
                  Text('Enviando solicitud al servidor...'),
                ],
              ),
            )
          : SingleChildScrollView(
              padding: const EdgeInsets.all(AppSizes.p24),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.stretch,
                children: [
                  // Foto de perfil
                  Center(
                    child: Stack(
                      children: [
                        const CircleAvatar(
                          radius: 50,
                          backgroundColor: AppColors.lightBlue,
                          child: Icon(Icons.person, size: 60, color: AppColors.pureWhite),
                        ),
                        Positioned(
                          bottom: 0,
                          right: 0,
                          child: Container(
                            padding: const EdgeInsets.all(4),
                            decoration: const BoxDecoration(
                              color: AppColors.primaryBlue,
                              shape: BoxShape.circle,
                            ),
                            child: const Icon(Icons.camera_alt, color: AppColors.pureWhite, size: 20),
                          ),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: AppSizes.p32),
                  const Text(
                    'Información Personal',
                    style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: AppColors.primaryBlue),
                  ),
                  const SizedBox(height: AppSizes.p16),
                  _buildTextField('Nombre', _nameController, Icons.person),
                  const SizedBox(height: AppSizes.p16),
                  _buildTextField('Apellido', _surnameController, Icons.person_outline),
                  const SizedBox(height: AppSizes.p16),
                  _buildTextField('Correo Electrónico', _emailController, Icons.email),
                  const SizedBox(height: AppSizes.p16),
                  _buildTextField('Teléfono', _phoneController, Icons.phone),
                  const SizedBox(height: AppSizes.p32),
                  ElevatedButton(
                    onPressed: _requestModification,
                    child: const Text('Solicitar Modificación', style: TextStyle(fontSize: 16)),
                  ),
                ],
              ),
            ),
    );
  }

  Widget _buildTextField(String label, TextEditingController controller, IconData icon) {
    return TextField(
      controller: controller,
      decoration: InputDecoration(
        labelText: label,
        prefixIcon: Icon(icon, color: AppColors.textDark),
        // Visualmente indicar que es editable pero sujeto a aprobación
      ),
    );
  }
}
