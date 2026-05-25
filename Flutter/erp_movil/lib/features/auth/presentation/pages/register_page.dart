import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../../../../core/constants/app_colors.dart';
import '../../../../core/constants/app_sizes.dart';
import '../bloc/auth_bloc.dart';
import '../bloc/auth_event.dart';
import '../bloc/auth_state.dart';
import '../../../home/presentation/pages/main_layout.dart';

class RegisterPage extends StatefulWidget {
  const RegisterPage({super.key});

  @override
  State<RegisterPage> createState() => _RegisterPageState();
}

class _RegisterPageState extends State<RegisterPage> {
  final _nameController = TextEditingController();
  final _surnameController = TextEditingController();
  final _emailController = TextEditingController();
  final _phoneController = TextEditingController();
  final _passwordController = TextEditingController();

  @override
  void dispose() {
    _nameController.dispose();
    _surnameController.dispose();
    _emailController.dispose();
    _phoneController.dispose();
    _passwordController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: AppColors.backgroundWhite,
      appBar: AppBar(
        title: const Text('Registro de Empleado'),
      ),
      body: BlocConsumer<AuthBloc, AuthState>(
        listener: (context, state) {
          if (state is AuthSuccess) {
            Navigator.pushAndRemoveUntil(
              context,
              MaterialPageRoute(builder: (_) => const MainLayout()),
              (route) => false,
            );
          } else if (state is AuthError) {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(content: Text(state.message), backgroundColor: AppColors.errorRed),
            );
          }
        },
        builder: (context, state) {
          return SingleChildScrollView(
            padding: const EdgeInsets.all(AppSizes.p24),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                const Text(
                  'Completar Datos',
                  style: TextStyle(
                    fontSize: 24,
                    fontWeight: FontWeight.bold,
                    color: AppColors.primaryBlue,
                  ),
                ),
                const SizedBox(height: AppSizes.p24),
                TextField(
                  controller: _nameController,
                  decoration: const InputDecoration(labelText: 'Nombre', prefixIcon: Icon(Icons.person)),
                ),
                const SizedBox(height: AppSizes.p16),
                TextField(
                  controller: _surnameController,
                  decoration: const InputDecoration(labelText: 'Apellido', prefixIcon: Icon(Icons.person_outline)),
                ),
                const SizedBox(height: AppSizes.p16),
                TextField(
                  controller: _emailController,
                  decoration: const InputDecoration(labelText: 'Correo Electrónico', prefixIcon: Icon(Icons.email)),
                  keyboardType: TextInputType.emailAddress,
                ),
                const SizedBox(height: AppSizes.p16),
                TextField(
                  controller: _phoneController,
                  decoration: const InputDecoration(labelText: 'Teléfono', prefixIcon: Icon(Icons.phone)),
                  keyboardType: TextInputType.phone,
                ),
                const SizedBox(height: AppSizes.p16),
                TextField(
                  controller: _passwordController,
                  decoration: const InputDecoration(labelText: 'Contraseña', prefixIcon: Icon(Icons.lock)),
                  obscureText: true,
                ),
                const SizedBox(height: AppSizes.p32),
                if (state is AuthLoading)
                  const Center(child: CircularProgressIndicator())
                else
                  ElevatedButton(
                    onPressed: () {
                      context.read<AuthBloc>().add(
                        RegisterEvent(
                          _nameController.text,
                          _surnameController.text,
                          _emailController.text,
                          _phoneController.text,
                          _passwordController.text,
                        ),
                      );
                    },
                    child: const Text('Registrarse', style: TextStyle(fontSize: 16)),
                  ),
              ],
            ),
          );
        },
      ),
    );
  }
}
