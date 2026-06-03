import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../errors/failures.dart';

/// Cliente HTTP personalizado para manejar las peticiones a la API.
/// Se encarga de inyectar el token de autorización si está disponible.
class ApiClient {
  // Configurar para Producción (Backend Desplegado en AWS/VPS)
  // URL base para presentar la Beta 1 con conexión a internet real
  static const String baseUrl = 'http://18.224.29.65:8081/erp-rrhh/v1';
  final SharedPreferences sharedPreferences;

  ApiClient({required this.sharedPreferences});

  Future<Map<String, String>> _getHeaders() async {
    final headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    };
    
    // Obtener el token guardado
    final token = sharedPreferences.getString('auth_token');
    if (token != null) {
      headers['Authorization'] = 'Bearer $token';
    }
    
    return headers;
  }

  /// Realiza una petición GET al endpoint especificado.
  Future<dynamic> get(String endpoint) async {
    final url = Uri.parse('$baseUrl$endpoint');
    final headers = await _getHeaders();

    try {
      final response = await http.get(url, headers: headers).timeout(const Duration(seconds: 5));
      return _processResponse(response);
    } catch (e) {
      throw ServerFailure('Error de conexión con el servidor: $e');
    }
  }

  /// Realiza una petición POST al endpoint especificado.
  Future<dynamic> post(String endpoint, {Map<String, dynamic>? body}) async {
    final url = Uri.parse('$baseUrl$endpoint');
    final headers = await _getHeaders();

    try {
      final response = await http.post(
        url,
        headers: headers,
        body: body != null ? json.encode(body) : null,
      ).timeout(const Duration(seconds: 5));
      return _processResponse(response);
    } catch (e) {
      throw ServerFailure('Error de conexión con el servidor: $e');
    }
  }

  /// Procesa la respuesta HTTP y maneja errores comunes.
  dynamic _processResponse(http.Response response) {
    if (response.statusCode >= 200 && response.statusCode < 300) {
      if (response.body.isNotEmpty) {
        return json.decode(response.body);
      }
      return null;
    } else if (response.statusCode == 401 || response.statusCode == 403) {
      throw const AuthFailure('Credenciales incorrectas o sesión expirada.');
    } else {
      try {
        final errorBody = json.decode(response.body);
        throw ServerFailure(errorBody['error'] ?? 'Error desconocido en el servidor');
      } catch (_) {
        throw ServerFailure('Error del servidor: código ${response.statusCode}');
      }
    }
  }
}
