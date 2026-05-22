import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import '../../../../core/errors/failures.dart';
import '../../domain/entities/user.dart';
import '../../domain/repositories/auth_repository.dart';
import '../models/user_model.dart';

class AuthRepositoryImpl implements AuthRepository {
  final SharedPreferences sharedPreferences;

  AuthRepositoryImpl(this.sharedPreferences);

  @override
  Future<UserEntity> login(String email, String password) async {
    // retardo de red
    await Future.delayed(const Duration(seconds: 2));

    final userJson = sharedPreferences.getString('user_$email');
    if (userJson != null) {
      final user = UserModel.fromJson(json.decode(userJson));
      // Guardar sesión activa 
      await sharedPreferences.setString('active_user', userJson);
      return user;
    } else {
      throw const AuthFailure('Credenciales incorrectas o usuario no encontrado.');
    }
  }

  @override
  Future<UserEntity> register(String name, String surname, String email, String phone, String password) async {
    // retardo de red
    await Future.delayed(const Duration(seconds: 2));

    final newUser = UserModel(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      name: name,
      surname: surname,
      email: email,
      phone: phone,
    );

    // Guardar usuario para base de datos
    await sharedPreferences.setString('user_$email', json.encode(newUser.toJson()));
    // Guardar como sesión activa
    await sharedPreferences.setString('active_user', json.encode(newUser.toJson()));

    return newUser;
  }
}
