import 'dart:convert';
import 'package:shared_preferences/shared_preferences.dart';
import '../../../../core/errors/failures.dart';
import '../../domain/entities/user.dart';
import '../../domain/repositories/auth_repository.dart';
import '../models/user_model.dart';
import '../../../../core/network/api_client.dart';

class AuthRepositoryImpl implements AuthRepository {
  final ApiClient apiClient;
  final SharedPreferences sharedPreferences;

  AuthRepositoryImpl({required this.apiClient, required this.sharedPreferences});

  @override
  Future<UserEntity> login(String email, String password) async {
    try {
      final response = await apiClient.post('/auth/login', body: {
        'email': email,
        'password': password,
      });

      // Se espera que la respuesta tenga 'token'
      if (response != null && response['token'] != null) {
        final token = response['token'] as String;
        await sharedPreferences.setString('auth_token', token);
        
        UserModel user;
        try {
          // Obtener los datos reales del perfil desde el backend
          final meResponse = await apiClient.get('/auth/me');
          if (meResponse != null) {
            user = UserModel(
              id: meResponse['id']?.toString() ?? '0',
              name: meResponse['firstName']?.toString() ?? 'Usuario',
              surname: meResponse['surnames']?.toString() ?? '',
              email: meResponse['email']?.toString() ?? email,
              phone: meResponse['phoneNumber']?.toString() ?? '',
            );
          } else {
            throw Exception('Respuesta vacía');
          }
        } catch (e) {
          // Fallback si no tiene permisos o falla
          user = UserModel(
            id: '0', 
            name: 'Usuario', 
            surname: '(Sin datos)', 
            email: email, 
            phone: '',
          );
        }
        
        await sharedPreferences.setString('active_user', json.encode(user.toJson()));
        return user;
      } else {
        throw const AuthFailure('Formato de respuesta incorrecto');
      }
    } on Failure {
      rethrow;
    } catch (e) {
      throw AuthFailure('Error de red o servidor: $e');
    }
  }
}
